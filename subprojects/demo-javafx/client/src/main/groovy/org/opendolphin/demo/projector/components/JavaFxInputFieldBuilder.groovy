package org.opendolphin.demo.projector.components

import javafx.scene.control.TextField

class JavaFxInputFieldBuilder extends AJavaFxTextFieldBuilder {

    def build() {
        return build(new TextField())
    }

}
