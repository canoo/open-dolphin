package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.List

public class TutorialAction extends DolphinServerAction {

    public static final String CMD_ECHO     = "org.opendolphin.demo.Tutorial.echo"
    public static final String PM_ID_MODEL  = 'org.opendolphin.demo.Tutorial.modelId'
    public static final String ATTR_ID      = "attrId"
    public static final String CMD_ADD      = "org.opendolphin.demo.Tutorial.add"

    private static int count = 0

    public void registerIn(ActionRegistry actionRegistry) {
        actionRegistry.register(CMD_ECHO, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {

                final ServerPresentationModel presentationModel = getServerDolphin().getAt(PM_ID_MODEL)
                final ServerAttribute attribute = presentationModel[ATTR_ID]

//                changeValue attribute, "Server: ${attribute.value}" // old: strict-mode style
                attribute.value = "Server: ${attribute.value}"
            }
        })

        actionRegistry.register(CMD_ADD, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                count++
                presentationModel("weather.$count", "weather", new DTO(
                        new Slot("temperature", String.valueOf((int) (Math.random() * 100)), "weather.${count}.temperature"),
                        new Slot("humidity",    String.valueOf((int) (Math.random() * 100)), "weather.${count}.humidity")
                ))
            }
        })
    }

}

