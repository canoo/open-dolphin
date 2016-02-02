package org.opendolphin.demo.projector.components

import org.opendolphin.demo.projector.UiControlType

@Singleton(strict = false)
class JavaFxComponentRegistry implements IJavaFxComponentsRegistry {

    private final Map<UiControlType, IJavaFxComponentFactory> componentRegistry = new HashMap<>();

    private JavaFxComponentRegistry() {
        init();
    }

    private void init() {
        register(UiControlType.INPUTFIELD, new JavaFxInputFieldFactory());
        register(UiControlType.TEXTAREA, new JavaFxTextAreaFactory());
    }

    void register(UiControlType controlType, IJavaFxComponentFactory factory) {
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