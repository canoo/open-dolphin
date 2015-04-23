package org.opendolphin.binding

import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.GClientAttribute
import org.opendolphin.core.client.GClientPresentationModel

class JFXUnbindOfAble {
    private String sourcePropertyName

    JFXUnbindOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    JFXUnbindFromAble of(javafx.scene.Node source) {
        new JFXUnbindFromAble(source, sourcePropertyName)
    }

    UnbindFromAble of(PresentationModel source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }

    UnbindClientFromAble of(GClientPresentationModel source) {
        new UnbindClientFromAble((GClientAttribute) source.findAttributeByPropertyName(sourcePropertyName))
    }

    UnbindPojoFromAble of(Object source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }
}
