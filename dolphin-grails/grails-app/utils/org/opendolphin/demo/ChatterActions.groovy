package org.opendolphin.demo

import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.SwitchPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.demo.crud.PositionConstants

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

public class ChatterActions extends DolphinServerAction {

    public static final String CMD_INIT     = "chatter.init"
    public static final String CMD_POST     = "chatter.post"
    public static final String CMD_ON_PUSH  = "chatter.on.push"
    public static final String CMD_RELEASE  = "chatter.release"
    public static final String PM_ID_INPUT  = 'chatter.input'
    public static final String TYPE_POST    = 'chatter.type.post'
    public static final String ATTR_NAME    = "name"
    public static final String ATTR_MESSAGE = "message"
    public static final String ATTR_DATE    = "date"

    static final Agent history = new Agent<List<DTO>>([])

    static final AtomicInteger count = new AtomicInteger(0)
    final int userId;
    int postCount = 0

    private final EventBus chatterBus
    private final DataflowQueue chatQueue

    ChatterActions(EventBus sharedChatterBus) {
        chatterBus = sharedChatterBus
        userId = count.getAndIncrement()
        chatQueue = new DataflowQueue()
        chatterBus.subscribe(chatQueue)
    }

    protected void newPost(String name) {
        def postId = postCount++
        String pmId = "$userId-$postId".toString()          // every PM for a post gets a unique Id of the form userId-postId
        String now = new Date().format('dd.MM.yy HH:mm')
        def currentPost = new DTO(
            new Slot(ATTR_NAME,   name, "$pmId-$ATTR_NAME"),    // all post qualifiers are unique of the form userId-postId-attrName
            new Slot(ATTR_MESSAGE,  "", "$pmId-$ATTR_MESSAGE"),
            new Slot(ATTR_DATE,    now, "$pmId-$ATTR_DATE")
        )
        history.sendAndWait { List posts ->
            posts << currentPost
            if (posts.size() > 10) posts.remove(0)
        }
        chatterBus.publish(chatQueue, [type: "new", dto: currentPost])
        ServerPresentationModel newPostPM = serverDolphin.presentationModel(pmId, TYPE_POST, currentPost)
        ServerPresentationModel inputPM = serverDolphin[PM_ID_INPUT]
        if (newPostPM && inputPM) {
            inputPM.syncWith(newPostPM)
        } else {
            println "ERROR: cannot sync input pm. newPostPm: $newPostPM, inputPM: $inputPM"
        }
    }

    protected void updateHistory(ServerAttribute attribute) {
        history.sendAndWait { List<DTO> posts -> // todo dk: could be send and wait
            def slot   = posts*.slots.flatten().find { it.qualifier == attribute.qualifier }
            if (!slot) return
            slot.value = attribute.value
            slot.tag   = attribute.tag
        }
    }

    protected validateAndPromoteValueChange(ServerAttribute attr) {
        String toCheck = attr.value
        String replaced = toCheck.replaceAll(/<(\/?\w)/, /&lt;\1/) // html-escape opening angle brackets
        if (toCheck == replaced) {
            updateHistory(attr)
            chatterBus.publish(chatQueue, [type: "change", qualifier: attr.qualifier, value: attr.value])
        } else {
            attr.value = replaced // this will trigger this method again...
        }
    }

    protected void createPostPM(DTO post) {
        // we can pick any qualifier to strip off the pmId
        String pmId = post.slots.first().qualifier.split('-')[0..-2].join('-')
        getServerDolphin().presentationModel( pmId, TYPE_POST, post)
    }

    public void registerIn(ActionRegistry actionRegistry) {
        ChatterActions me = this
        actionRegistry.register(CMD_INIT, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                // display the old conversation that happened before we joined
                history.sendAndWait { List<DTO> posts ->
                    for(post in posts) {
                        createPostPM(post)
                    }
                }

                // at this point, the input pm must be known and we can attach PCLs
                for(attribute in getServerDolphin().getAt(PM_ID_INPUT).attributes) {
                    attribute.addPropertyChangeListener("value", new PropertyChangeListener() {
                        @Override void propertyChange(PropertyChangeEvent evt) {
                            validateAndPromoteValueChange((ServerAttribute) evt.source)
                        }
                    })
                }

                // make sure the collection does not grow overly long
                getServerDolphin().addModelStoreListener TYPE_POST, { ModelStoreEvent event ->
                    if (event.type != ModelStoreEvent.Type.ADDED) return;
                    def posts = getServerDolphin().findAllPresentationModelsByType(TYPE_POST)
                    if (posts.size() > 10) {
                       getServerDolphin().remove(posts.first())
                    }
                }

                // we start with an initial open post
                newPost("User-${this.userId} (please change)")
            }
        })

        actionRegistry.register(CMD_POST, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                String name = getServerDolphin().getAt(PM_ID_INPUT).getAt(ATTR_NAME).value
                newPost(name)
            }
        })

        actionRegistry.register(CMD_ON_PUSH, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                Map post = chatQueue.getVal(60, TimeUnit.SECONDS)    // return all values
                while (null != post) {
                    if (post.type == "new") {
                        createPostPM(post.dto)
                    }
                    if (post.type == "change") {
                        def attributes = getServerDolphin().findAllAttributesByQualifier(post.qualifier)
                        if (attributes) {
                            attributes.first().value = post.value
                        }
                    }
                    post = chatQueue.getVal(20, TimeUnit.MILLISECONDS)
                }
            }
        })

    }

}

