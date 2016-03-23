package demo

import grails.plugin.jms.Subscriber
import groovy.json.JsonSlurper
import org.opendolphin.core.comm.JsonCodec
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot

class OnNotificationService {
    static transactional = false
    static exposes = ['jms']
    def grailsApplication   // inject for use of config values
    def chatterBus

    private JsonCodec codec = new JsonCodec()

    @Subscriber
    def newMessages(message) { // method name must be same as NotificationController.TOPIC_NAME
//        println "message: chatter bus is $chatterBus"
//        println "message is $message"
        Map got = new JsonSlurper().parseText((String) message)

        if (got.uuid == grailsApplication.config.uuid) {
            println "ignoring my own message"
            return
        }
        println "relaying from JMS to event queue, type ${got.content?.type}"

        def chatEvent = [type:got.content?.type]
        if (null != got.content?.qualifier) chatEvent.qualifier = got.content?.qualifier
        if (null != got.content?.value) chatEvent.value = got.content?.value
        if (null != got.content?.dto) {
            def slots = got.content?.dto?.collect {
                new Slot(it.propertyName, it.value, it.qualifier)
            }
            chatEvent.dto = new DTO(slots)
        }
        def noSender = null
        chatterBus.publish(noSender, chatEvent)
    }
}
