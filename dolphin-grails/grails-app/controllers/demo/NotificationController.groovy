package demo

class NotificationController {

    static final TOPIC_NAME = 'newMessages'

    def jmsService

	def index = {
		def message = "Hi, it is ${grailsApplication.config.uuid} " + new Date()
        jmsService.send(topic: TOPIC_NAME, message)
		render message
	}
}

