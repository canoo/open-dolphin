package org.opendolphin.binding

import org.opendolphin.core.client.GClientAttribute

class BindClientToAble {
    final GClientAttribute attribute
    final Converter converter

    BindClientToAble(GClientAttribute attribute, Converter converter = null) {
        this.attribute = attribute
        this.converter = converter
    }

    BindClientOtherOfAble to(String targetPropertyName) {
        new BindClientOtherOfAble(attribute, targetPropertyName, converter)
    }

    BindClientToAble using(Closure converter) {
        using(new ConverterAdapter(converter))
    }

    BindClientToAble using(Converter converter) {
        return new BindClientToAble(attribute, converter)
    }

}
