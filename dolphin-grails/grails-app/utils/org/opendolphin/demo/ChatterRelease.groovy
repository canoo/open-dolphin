package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.SignalCommand
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.action.ServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import static org.opendolphin.demo.ChatterActions.CMD_RELEASE

class ChatterRelease implements ServerAction {
    private final EventBus chatterBus;

    public ChatterRelease(EventBus chatterBus) {
        this.chatterBus = chatterBus;
    }

    @Override public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_RELEASE, new CommandHandler<SignalCommand>() {
            @Override public void handleCommand(SignalCommand command, List<Command> response) {
                chatterBus.publish(null, [type: "release"]);
            }
        });

    }
}
