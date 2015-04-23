package org.opendolphin.binding

import org.opendolphin.core.client.GClientAttribute

class UnbindClientOtherOfAble {
    final GClientAttribute attribute
    final String targetPropertyName

    UnbindClientOtherOfAble(GClientAttribute attribute, String targetPropertyName) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName)
        attribute.removePropertyChangeListener('value', listener)
    }
}
