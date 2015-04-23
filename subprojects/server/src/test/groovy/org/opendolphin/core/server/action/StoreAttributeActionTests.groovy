package org.opendolphin.core.server.action

import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.GServerPresentationModel
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.core.server.ServerDolphinFactory
import org.opendolphin.core.server.comm.ActionRegistry

class StoreAttributeActionTests extends GroovyTestCase {
    ServerDolphin dolphin
    ActionRegistry registry

    @Override
    protected void setUp() throws Exception {
        dolphin = ServerDolphinFactory.create()
        dolphin.serverModelStore.currentResponse = []
        registry = new ActionRegistry()
    }

    void testStoreAttribute() {
        StoreAttributeAction action = new StoreAttributeAction(serverDolphin: dolphin)
        action.registerIn(registry)
        registry.getAt('AttributeCreated').first().handleCommand(new AttributeCreatedNotification(pmId: 'model', propertyName: 'newAttribute', newValue: 'value'), [])
        assert dolphin.getAt('model').getAt('newAttribute')
        assert 'value' == dolphin.getAt('model').getAt('newAttribute').value
    }

    void testStoreAttribute_ModelExists() {
        StoreAttributeAction action = new StoreAttributeAction(serverDolphin: dolphin)
        action.registerIn(registry)
        dolphin.add(dolphin.createPresentationModel('model', []))
        registry.getAt('AttributeCreated').first().handleCommand(new AttributeCreatedNotification(pmId: 'model', propertyName: 'newAttribute', newValue: 'value'), [])
        assert dolphin.getAt('model').getAt('newAttribute')
        assert 'value' == dolphin.getAt('model').getAt('newAttribute').value
    }

    void testStoreAttribute_AlreadyExistingAttribute() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = dolphin.createAttribute('newAttribute', '')
        dolphin.add(dolphin.createPresentationModel('model', [attribute]))
        registry.getAt('AttributeCreated').first().handleCommand(new AttributeCreatedNotification(pmId: 'model', attributeId: attribute.id, propertyName: 'newAttribute', newValue: 'value'), [])
        assert '' == dolphin.getAt('model').getAt('newAttribute').value
    }

    void testChangeAttributeMetadata_AttributeNotFound() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = dolphin.createAttribute('newAttribute', '')
        registry.getAt('ChangeAttributeMetadata').first().handleCommand(new ChangeAttributeMetadataCommand(attributeId: attribute.id, metadataName: 'dirty', value: true), [])
        assert !attribute.dirty
    }

    void testChangeAttributeMetadata() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = dolphin.createAttribute('newAttribute', '')
        dolphin.add(dolphin.createPresentationModel('model', [attribute]))
        registry.getAt('ChangeAttributeMetadata').first().handleCommand(new ChangeAttributeMetadataCommand(attributeId: attribute.id, metadataName: 'value', value: 'newValue'), [])
        assert 'newValue' == attribute.value
    }
}
