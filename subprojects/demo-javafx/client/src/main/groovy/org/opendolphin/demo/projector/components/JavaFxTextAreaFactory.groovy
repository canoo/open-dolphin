package org.opendolphin.demo.projector.components

class JavaFxTextAreaFactory implements IJavaFxComponentFactory {

    @Override
    def createComponent(properties) {
        new JavaFxTextAreaBuilder().withProperties(properties).build()
    }
}
