package org.opendolphin.binding

import org.opendolphin.core.client.GClientAttribute

class UnbindClientFromAble {
    final GClientAttribute attribute

    UnbindClientFromAble(GClientAttribute attribute) {
        this.attribute = attribute
    }

    UnbindClientOtherOfAble from(String targetPropertyName) {
        new UnbindClientOtherOfAble(attribute, targetPropertyName)
    }
}
