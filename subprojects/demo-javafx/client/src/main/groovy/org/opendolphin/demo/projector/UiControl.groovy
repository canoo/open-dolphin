package org.opendolphin.demo.projector

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

import static org.opendolphin.core.Tag.WIDGET_HINT

final class UiControl {

    final String name;

    protected UiControl(String name) { this.name = name }

    final String toString() { name }

    static final Map<String, UiControl> uiControlFor = [:].withDefault { String key -> new UiControl(key) }

    static final UiControl INPUTFIELD = uiControlFor["INPUTFIELD"]

    static final UiControl TEXTAREA = uiControlFor["TEXTAREA"]

    static UiControl uiControlFor(ClientPresentationModel model, String propName) {
        assert model
        assert propName
        assert model.getAt(propName)

        // by default if not widget_hint assume text fields
        UiControl widgetHint = INPUTFIELD
        ClientAttribute widgetHintAttr = model.getAt(propName, WIDGET_HINT)
        if (widgetHintAttr != null) {
            String widgetHintValue = widgetHintAttr.value
            widgetHint = uiControlFor[widgetHintValue.toUpperCase()]
        }
        widgetHint
    }

}
