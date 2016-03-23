import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.LogConfig
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.demo.ChatterActions
import org.opendolphin.demo.ChatterRelease
import org.opendolphin.demo.CustomAction
import org.opendolphin.demo.DemoTitlePurposeAction
import org.opendolphin.demo.ManyEventsAction
import org.opendolphin.demo.PerformanceAction
import org.opendolphin.demo.SharedTachoAction
import org.opendolphin.demo.TesselAction
import org.opendolphin.demo.TutorialAction
import org.opendolphin.demo.VehiclePushActions
import org.opendolphin.demo.SmallFootprintAction
import org.opendolphin.demo.crud.CrudActions
import org.opendolphin.demo.crud.CrudService
import groovy.util.logging.Log
import org.opendolphin.demo.team.TeamBusRelease
import org.opendolphin.demo.team.TeamMemberActions

import java.util.logging.Level
import java.util.logging.Logger

@Log
class DolphinSpringBean {

    private static final teamHistory = new Agent<List<DTO>>(new LinkedList<DTO>());

    DolphinSpringBean(
        ServerDolphin dolphin,
        CrudService crudService,
        EventBus tachoBus,
        EventBus manyEventsBus,
        EventBus smallFootprintBus,
        EventBus chatterBus,
        EventBus teamBus
    ) {

        Logger.getLogger("").level = Level.INFO
        LogConfig.logCommunication()
//        LogConfig.noLogs()

        log.info "creating new dolphin session"

        dolphin.registerDefaultActions()

        dolphin.register(new VehiclePushActions())
        dolphin.register(new CrudActions(crudService: crudService))
        dolphin.register(new DemoTitlePurposeAction())
        dolphin.register(new CustomAction())
        dolphin.register(new PerformanceAction())
        dolphin.register(new SharedTachoAction().subscribedTo(tachoBus))
        dolphin.register(new ManyEventsAction().subscribedTo(manyEventsBus))
        dolphin.register(new SmallFootprintAction().subscribedTo(smallFootprintBus))

        // for the dolphin.js demos
        dolphin.register(new TutorialAction())

        dolphin.register(new TeamMemberActions(teamBus, teamHistory));
        dolphin.getServerConnector().register(new TeamBusRelease(teamBus));

        dolphin.register(new ChatterActions().subscribedTo(chatterBus))
        dolphin.getServerConnector().register(new ChatterRelease(chatterBus));

        dolphin.register(new TesselAction())



        println "DolphinSpringBean chatter bus $chatterBus"

        println "init done."

    }
}
