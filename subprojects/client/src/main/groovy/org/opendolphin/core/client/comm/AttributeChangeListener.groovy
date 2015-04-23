package org.opendolphin.core.client.comm

import org.opendolphin.core.Attribute
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.comm.BaseValueChangedCommand
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand
import org.opendolphin.core.comm.ValueChangedCommand

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class AttributeChangeListener implements PropertyChangeListener {

    ClientModelStore clientModelStore
    ClientConnector clientConnector

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.propertyName == BaseAttribute.DIRTY_PROPERTY) {
            // ignore
        } else if (evt.propertyName == BaseAttribute.VALUE) {
            if (evt.oldValue == evt.newValue) return
            if (isSendable(evt)) {
                clientConnector.send constructValueChangedCommand(evt)
            }
            List<Attribute> attributes = clientModelStore.findAllAttributesByQualifier(evt.source.qualifier)
            attributes.each { it.value = evt.newValue }
        } else if (evt.propertyName == BaseAttribute.BASE_VALUE) {
            if (evt.oldValue == evt.newValue) return
            if (isSendable(evt)) {
                clientConnector.send constructBaseValueChangedCommand(evt)
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

    private BaseValueChangedCommand constructBaseValueChangedCommand(PropertyChangeEvent evt) {
        new BaseValueChangedCommand(
                attributeId: evt.source.id
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
