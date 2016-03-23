import demo.NotificationController
import groovy.json.JsonBuilder
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.server.EventBus
import grails.plugin.jms.JmsService

class EventRelaySpringBean {

    private DataflowQueue chatQueue = new DataflowQueue() // needs to be a field or it will be gc'ed

    EventRelaySpringBean(
        EventBus chatterBus,
        JmsService jmsService,
        grailsApplication
    ) {

        if (System.properties.'server.port'=='9090') {
            println "read-only servers do not post to JMS"
            return
        }

        println "setting up event relay"
        println "EventRelaySpringBean chatter bus $chatterBus"
        println "JMS service is $jmsService"
        String uuid = grailsApplication.config.uuid

        println "queue $chatQueue"
        chatterBus.subscribe(chatQueue)

        chatQueue.wheneverBound { chatEvent ->
            if (chatEvent.type == "release") {
                println "release not relayed"
                return
            }
            def transfer = [type: chatEvent.type]
            if (null != chatEvent.dto) {
                transfer.dto = chatEvent.dto.encodable()
            }
            if (null != chatEvent.qualifier) {
                transfer.qualifier = chatEvent.qualifier
            }
            if (null != chatEvent.value) {
                transfer.value = chatEvent.value
            }

            println "publishing to JMS"

            String message = new JsonBuilder([uuid: uuid, content:transfer]).toString()

            println message
            jmsService.send(topic: NotificationController.TOPIC_NAME, message)
        }

    }
}
