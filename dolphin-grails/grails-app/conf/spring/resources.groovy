import org.opendolphin.core.server.ServerModelStore
import org.opendolphin.core.comm.JsonCodec
import org.opendolphin.core.comm.ZippedJsonCodec
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerConnector
import org.springframework.jms.connection.SingleConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory

beans = {

    jmsConnectionFactory(SingleConnectionFactory) { bean ->
        bean.scope = 'singleton'
        targetConnectionFactory = { ActiveMQConnectionFactory cf ->
            brokerURL = 'tcp://localhost:61616' // requires starting the activemq server externally
        }
    }

    teamBus(EventBus) { bean ->
        bean.scope = 'singleton'
    }

    tachoBus(EventBus) { bean ->
        bean.scope = 'singleton'
    }

    manyEventsBus(EventBus) { bean ->
        bean.scope = 'singleton'
    }

    smallFootprintBus(EventBus) { bean ->
        bean.scope = 'singleton'
    }

    chatterBus(EventBus) { bean ->
        bean.scope = 'singleton'
    }

    modelStore(ServerModelStore) { bean ->
        bean.scope = 'session' // every session must have its own model store
    }

    serverConnector(ServerConnector) { bean ->
        bean.scope = 'session'  // could be shared among sessions but since the registry is set, this is safer...
        codec = new JsonCodec()
        serverModelStore = ref('modelStore')
    }

    serverDolphin(DefaultServerDolphin, ref('modelStore'), ref('serverConnector')) { bean ->
        bean.scope = 'session'
    }

    eventRelaySpringBean(
        EventRelaySpringBean,
        ref('chatterBus'),
        ref('jmsService'),
        ref('grailsApplication')
    ) { bean ->
        bean.scope = 'singleton'
    }

    dolphinBean(
        DolphinSpringBean,
        ref('serverDolphin'),
        ref('grailsCrudService'),                    // more services would come here...
        ref('tachoBus'),
        ref('manyEventsBus'),
        ref('smallFootprintBus'),
        ref('chatterBus'),
        ref('teamBus')
    ) { bean ->
        bean.scope = 'session'
    }

}
