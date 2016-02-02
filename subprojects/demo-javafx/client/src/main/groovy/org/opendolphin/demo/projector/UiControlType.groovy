package org.opendolphin.demo.projector

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

import static org.opendolphin.core.Tag.WIDGET_HINT

enum UiControlType {
    INPUTFIELD, TEXTAREA

    static def getValueOf(ClientPresentationModel model, String propName) {
        assert model
        assert propName
        assert model.getAt(propName)

        // by default if not widget_hint assume text fields
        UiControlType widgetHint = UiControlType.INPUTFIELD
        ClientAttribute widgetHintAttr = model.getAt(propName, WIDGET_HINT)
        if (widgetHintAttr != null) {
            String widgetHintValue = widgetHintAttr.value
            widgetHint = widgetHintValue.toUpperCase() as UiControlType
        }
        widgetHint
    }

}
