package org.opendolphin.core.server.action

import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.comm.ActionRegistry

class DolphinServerActionTests extends GroovyTestCase {

    DolphinServerAction action;

    @Override
    protected void setUp() throws Exception {
        action = new DolphinServerAction() {
            @Override
            void registerIn(ActionRegistry registry) {

            }
        }
        action.dolphinResponse = []
    }

    void testCreatePresentationModel() {
        action.presentationModel('p1', 'person', new DTO())
        assert 1 == action.dolphinResponse.size()
        assert CreatePresentationModelCommand == action.dolphinResponse.first().class
        assert 'p1' == action.dolphinResponse.first().pmId
        assert 'person' == action.dolphinResponse.first().pmType
    }

    void testChangeValue() {
        action.changeValue(new GServerAttribute('attr', 'initial'), 'newValue')
        assert 1 == action.dolphinResponse.size()
        assert ValueChangedCommand == action.dolphinResponse.first().class
    }

    void testInitializeAt() {
        action.initAt('p1', 'attr', 'qualifier')
        assert 1 == action.dolphinResponse.size()
        assert InitializeAttributeCommand == action.dolphinResponse.first().class

    }

}
