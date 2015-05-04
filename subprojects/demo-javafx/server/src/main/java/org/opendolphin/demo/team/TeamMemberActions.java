package org.opendolphin.demo.team;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import groovy.lang.Closure;
import groovyx.gpars.agent.Agent;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.*;
import org.opendolphin.core.server.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import static org.opendolphin.demo.team.TeamMemberConstants.*;

public class TeamMemberActions extends DolphinServerAction {

    /** thread safe shared state to keep around for new user sessions */
    private final Agent currentMembers ;

    /** thread safe unique member count across all sessions */
    static final AtomicInteger memberCount = new AtomicInteger(0);

    private EventBus teamBus;
    private final DataflowQueue<TeamEvent> memberQueue = new DataflowQueue<TeamEvent>();

    public TeamMemberActions(EventBus teamBus, Agent<List<DTO>> history) {
        this.teamBus = teamBus;
        this.teamBus.subscribe(memberQueue);
        currentMembers = history;
    }

    @Override
    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_INIT, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                // display the shared state
                try {
                    currentMembers.sendAndWait(new Closure(this) {
                        void doCall(List<DTO> members) {
                            for (DTO member : members) {
                                getServerDolphin().presentationModel(null, TYPE_TEAM_MEMBER, member);
                            }
                        }
                    });
                } catch (InterruptedException e) { /* do nothing */ }

                final ServerPresentationModel mold = getServerDolphin().getAt(PM_ID_MOLD);
                if (null == mold) System.out.println("SEVERE: Mold PM must be known before calling CMD_INIT.");
                registerOnValueChange(mold);

                // we do not necessarily select at start
            }
        });

        actionRegistry.register(CMD_ADD, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                // prepare a DTO to add
                int memberId = memberCount.getAndIncrement();
                final DTO dto = new DTO(
                    new Slot(ATT_FIRSTNAME, "",     qualifier(memberId, ATT_FIRSTNAME)),
                    new Slot(ATT_LASTNAME, "",      qualifier(memberId, ATT_LASTNAME)),
                    new Slot(ATT_FUNCTION, "",      qualifier(memberId, ATT_FUNCTION)),
                    new Slot(ATT_AVAILABLE,  false, qualifier(memberId, ATT_AVAILABLE)),
                    new Slot(ATT_CONTRACTOR, false, qualifier(memberId, ATT_CONTRACTOR)),
                    new Slot(ATT_WORKLOAD,   0,     qualifier(memberId, ATT_WORKLOAD))
                );
                // add the dto to shared state
                try {
                    currentMembers.sendAndWait(new Closure(this) {
                        void doCall(List<DTO> members) {
                            members.add(dto);
                        }
                    });
                } catch (InterruptedException e) { /* do nothing */ }
                // notify all others about the new team member
                teamBus.publish(memberQueue, new TeamEvent(TeamEvent.Type.NEW, dto));
                // create the pm
                String addedId = uniqueId(memberId);
                getServerDolphin().presentationModel(addedId, TYPE_TEAM_MEMBER, dto); // create on server

                // it is a server-side decision that after creating a new team member,
                // it should be immediately selected
                changeValue(findSelectedPmAttribute(), addedId); // let client do this for consistency
            }
        });

        // whenever we delete a pm, we must publish it to others and update the shared state
        actionRegistry.register(CMD_REMOVE, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                String selPmId = (String) findSelectedPmAttribute().getValue();
                ServerPresentationModel pm = getServerDolphin().getAt(selPmId);
                if (null == pm) {
                    System.out.println("cannot find pm to delete with id "+selPmId);
                    return;
                }
                if (!TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) return; // sanity check
                getServerDolphin().remove(pm);
                // take the qualifier of the first attribute as the indication which pm to remove
                Attribute indicator = pm.getAttributes().get(0);
                removeFromHistory(indicator);
                teamBus.publish(memberQueue, new TeamEvent(TeamEvent.Type.REMOVE, indicator.getQualifier(), null));
            }
        });

        actionRegistry.register(CMD_SAVE, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                String pmIdToSave = (String) findSelectedPmAttribute().getValue();
                ServerPresentationModel pm = getServerDolphin().getAt(pmIdToSave);
                if (null == pm) {
                    System.out.println("Cannot save unknown presentation model with id "+pmIdToSave);
                    return;
                }
                // saving the model to the database here. We assume all was ok:
                pm.rebase();
                for (Attribute attribute : pm.getAttributes()) {
                    rebaseInHistory(attribute);
                    teamBus.publish(memberQueue, new TeamEvent(TeamEvent.Type.REBASE, attribute.getQualifier(), null));
                }
            }
        });

        actionRegistry.register(ACTION_ON_PUSH, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                try {
                    processEventsFromQueue(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) { /* do nothing */ }
            }
        });

        // the release action is in the TeamBusRelease

    }

    private boolean silent = false;
    final PropertyChangeListener proliferator = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (silent) return;
            GServerAttribute attribute = (GServerAttribute) evt.getSource();
            boolean updated = updateHistory(attribute);
            if (updated)
                teamBus.publish(memberQueue, new TeamEvent(TeamEvent.Type.CHANGE, attribute.getQualifier(), attribute.getValue()));
        }
    };

    private void registerOnValueChange(ServerPresentationModel member) {
        for (final Attribute attribute : member.getAttributes()) {
            attribute.addPropertyChangeListener(Attribute.VALUE, proliferator);
        }
    }

    // inside this method do all manipulations directly on the server side since it has nothing to do
    // with user interactions and is thus safe to process in a more efficient manner.
    private void processEventsFromQueue(int timeoutValue, TimeUnit timeoutUnit) throws InterruptedException {
        TeamEvent event = memberQueue.getVal(timeoutValue, timeoutUnit);
        while (null != event) {
            if (TeamEvent.Type.NEW == event.type) {
                getServerDolphin().presentationModel(null, TYPE_TEAM_MEMBER, event.dto); // create on server side
            }
            if (TeamEvent.Type.CHANGE == event.type) {
                silent = true; // do not issue additional posts on the bus from value changes that come from the bus
                List<ServerAttribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                for (ServerAttribute attribute : attributes) {
                    PresentationModel pm = attribute.getPresentationModel();
                    if (TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) {
                        attribute.setValue(event.value);
                    }
                }
                silent = false;
            }
            if (TeamEvent.Type.REBASE == event.type) {
                List<ServerAttribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                for (ServerAttribute attribute : attributes) {
                    attribute.rebase();
                }
            }
            if (TeamEvent.Type.REMOVE == event.type) {
                List<ServerAttribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                Set<ServerPresentationModel> toDelete = new HashSet<ServerPresentationModel>();
                for (ServerAttribute attribute : attributes) {
                    ServerPresentationModel pm = attribute.getPresentationModel();
                    if (TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) {
                        toDelete.add(pm);
                    }
                }
                for (ServerPresentationModel pm : toDelete) {
                    getServerDolphin().remove(pm);
                }
            }
            event = memberQueue.getVal(20, TimeUnit.MILLISECONDS);
        }
    }

    private Slot findSlotByQualifier(List<DTO> dtos, String qualifier) {
        if (qualifier == null) return null;
        Slot result = null;
        for (DTO dto : dtos) {
            for (Slot slot : dto.getSlots()) {
                if (qualifier.equals(slot.getQualifier())) {
                    return slot;
                }
            }
        }
        return result;
    }

    private boolean updateHistory(final Attribute attr) {
        final AtomicBoolean updated = new AtomicBoolean(false);
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    Slot slot = findSlotByQualifier(members, attr.getQualifier());
                    if (null == slot) return;
                    if (!slot.getValue().equals(attr.getValue())) {
                        slot.setValue(attr.getValue());
                        updated.set(true);
                    }
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
        return updated.get();
    }

    private boolean rebaseInHistory(final Attribute attr) {
        final AtomicBoolean updated = new AtomicBoolean(false);
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    Slot slot = findSlotByQualifier(members, attr.getQualifier());
                    if (null == slot) return;
                    slot.setBaseValue(slot.getValue());
                    updated.set(true);
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
        return updated.get();
    }

    private void removeFromHistory(final Attribute attr) {
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    DTO toRemove = null;
                    out:
                    for (DTO member : members) {
                        for (Slot slot : member.getSlots()) {
                            if (slot.getQualifier().equals(attr.getQualifier())) {
                                toRemove = member;
                                break out;
                            }
                        }
                    }
                    if (null != toRemove) members.remove(toRemove);
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
    }

    private ServerAttribute findSelectedPmAttribute() {
        return getServerDolphin().getAt(PM_ID_SELECTED).getAt(ATT_SEL_PM_ID);
    }

}


