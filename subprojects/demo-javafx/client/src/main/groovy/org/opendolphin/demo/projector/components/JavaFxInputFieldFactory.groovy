package org.opendolphin.demo.projector.components;

class JavaFxInputFieldFactory implements IJavaFxComponentFactory {

    @Override
    def createComponent(properties) {
        new JavaFxInputFieldBuilder().withProperties(properties).build()
    }
}
