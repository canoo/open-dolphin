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
package org.opendolphin.core.client.comm

import org.opendolphin.core.Attribute
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand
import org.opendolphin.core.comm.ValueChangedCommand

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class AttributeChangeListener implements PropertyChangeListener {

    ClientModelStore clientModelStore
    ClientConnector clientConnector

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.propertyName == Attribute.DIRTY_PROPERTY) {
            // ignore
        } else if (evt.propertyName == Attribute.VALUE) {
            if (evt.oldValue == evt.newValue) return
            if (isSendable(evt)) {
                clientConnector.send constructValueChangedCommand(evt)
            }
            List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
            attributes.each { it.value = evt.newValue }
        } else if (evt.propertyName == Attribute.BASE_VALUE) {
            if (evt.oldValue == evt.newValue) return
            if (isSendable(evt)) {
                clientConnector.send constructChangeAttributeMetadataCommand(evt)
            }
            List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
            attributes.each { it.baseValue = evt.newValue }
        } else {
            // we assume the change is on a metadata property such as qualifier
            if (isSendable(evt)) {
                clientConnector.send constructChangeAttributeMetadataCommand(evt)
            }
        }
    }

    private boolean isSendable(PropertyChangeEvent evt) {
        def pmOfAttribute = ((Attribute)evt.source).getPresentationModel()
        if (pmOfAttribute == null)              return true
        if (pmOfAttribute.isClientSideOnly())   return false
        return true
    }

    private ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        new ValueChangedCommand(
                attributeId: evt.source.id,
                oldValue: evt.oldValue,
                newValue: evt.newValue
        )
    }

    private ChangeAttributeMetadataCommand constructChangeAttributeMetadataCommand(PropertyChangeEvent evt) {
        new ChangeAttributeMetadataCommand(
                attributeId: evt.source.id,
                metadataName: evt.propertyName,
                value: evt.newValue
        )
    }

}
