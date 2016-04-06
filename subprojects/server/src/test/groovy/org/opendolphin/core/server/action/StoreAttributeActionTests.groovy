/*
 * Copyright 2012-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.core.server.action

import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerDolphinFactory
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.comm.ActionRegistry

class StoreAttributeActionTests extends GroovyTestCase {
    DefaultServerDolphin dolphin
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
        dolphin.add(new ServerPresentationModel('model', [], dolphin.serverModelStore))
        registry.getAt('AttributeCreated').first().handleCommand(new AttributeCreatedNotification(pmId: 'model', propertyName: 'newAttribute', newValue: 'value'), [])
        assert dolphin.getAt('model').getAt('newAttribute')
        assert 'value' == dolphin.getAt('model').getAt('newAttribute').value
    }

    void testStoreAttribute_AlreadyExistingAttribute() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = new ServerAttribute('newAttribute', '')
        dolphin.add(new ServerPresentationModel('model', [attribute], dolphin.serverModelStore))
        registry.getAt('AttributeCreated').first().handleCommand(new AttributeCreatedNotification(pmId: 'model', attributeId: attribute.id, propertyName: 'newAttribute', newValue: 'value'), [])
        assert '' == dolphin.getAt('model').getAt('newAttribute').value
    }

    void testChangeAttributeMetadata_AttributeNotFound() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = new ServerAttribute('newAttribute', '')
        registry.getAt('ChangeAttributeMetadata').first().handleCommand(new ChangeAttributeMetadataCommand(attributeId: attribute.id, metadataName: 'dirty', value: true), [])
        assert !attribute.dirty
    }

    void testChangeAttributeMetadata() {
        new StoreAttributeAction(serverDolphin: dolphin).registerIn registry
        ServerAttribute attribute = new ServerAttribute('newAttribute', '')
        dolphin.add(new ServerPresentationModel('model', [attribute], dolphin.serverModelStore))
        registry.getAt('ChangeAttributeMetadata').first().handleCommand(new ChangeAttributeMetadataCommand(attributeId: attribute.id, metadataName: 'value', value: 'newValue'), [])
        assert 'newValue' == attribute.value
    }
}
