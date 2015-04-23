package org.opendolphin.core.client.comm

import org.opendolphin.core.client.ClientDolphinFactory
import org.opendolphin.core.client.GClientDolphin
import org.opendolphin.core.comm.Command

class InMemoryClientConnectorTests extends GroovyTestCase {

    void testCallConnector_NoServerConnectorWired() {
        InMemoryClientConnector connector = new InMemoryClientConnector(ClientDolphinFactory.create())
        assert [] == connector.transmit([new Command()])
    }

    void testCallConnector_ServerWired() {
        InMemoryClientConnector connector = new InMemoryClientConnector(ClientDolphinFactory.create())
        def command = new Command()
        boolean serverCalled = false
        connector.serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }]
        connector.transmit([command])
        assert serverCalled
    }

    void testCallConnector_ServerWiredWithSleep() {
        InMemoryClientConnector connector = new InMemoryClientConnector(ClientDolphinFactory.create())
        connector.sleepMillis = 10
        def command = new Command()
        boolean serverCalled = false
        connector.serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }]
        connector.transmit([command])
        assert serverCalled
    }

}
