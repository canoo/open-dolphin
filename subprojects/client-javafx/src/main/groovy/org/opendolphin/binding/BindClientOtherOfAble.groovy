package org.opendolphin.binding

import org.opendolphin.core.client.GClientAttribute

class BindClientOtherOfAble {
    final GClientAttribute attribute
    final String targetPropertyName
    final Converter converter

    BindClientOtherOfAble(GClientAttribute attribute, String targetPropertyName, Converter converter) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

}
