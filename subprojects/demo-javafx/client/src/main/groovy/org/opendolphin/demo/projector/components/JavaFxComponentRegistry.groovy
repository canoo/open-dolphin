package org.opendolphin.demo.projector.components

import org.opendolphin.demo.projector.UiControl

import static org.opendolphin.demo.projector.UiControl.INPUTFIELD
import static org.opendolphin.demo.projector.UiControl.TEXTAREA

@Singleton(strict = false)
class JavaFxComponentRegistry implements IJavaFxComponentsRegistry {

    private final Map<UiControl, IJavaFxComponentFactory> componentRegistry = new HashMap<>();

    private JavaFxComponentRegistry() {
        init();
    }

    private void init() {
        register(INPUTFIELD, new JavaFxInputFieldFactory());
        register(TEXTAREA, new JavaFxTextAreaFactory());
    }

    void register(UiControl controlType, IJavaFxComponentFactory factory) {
        assert controlType
        componentRegistry.put(controlType, factory)
    }

    def create(uiControl, properties) {
        def factory = componentRegistry.get(uiControl)
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for widget hint '${uiControl}'")
        }
        return factory.createComponent(properties)
    }

}