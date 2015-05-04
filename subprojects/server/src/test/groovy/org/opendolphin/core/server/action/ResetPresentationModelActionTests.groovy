package org.opendolphin.core.server.action

import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.ResetPresentationModelCommand
import org.opendolphin.core.server.ServerModelStore
import org.opendolphin.core.server.GServerPresentationModel
import org.opendolphin.core.server.ServerConnector

class ResetPresentationModelActionTests extends GroovyTestCase {

    void testResetModel() {
        def store = new ServerModelStore()
        ServerConnector connector = new ServerConnector(serverModelStore: store)
        ResetPresentationModelAction action = new ResetPresentationModelAction(store)
        store.add(new GServerPresentationModel('p1',[],store))
        connector.register(action)
        List response = connector.receive(new ResetPresentationModelCommand('p1'))
        assert 1 == response.size()
        assert PresentationModelResetedCommand == response.first().class
        assert 'p1' == response.first().pmId

    }
}
