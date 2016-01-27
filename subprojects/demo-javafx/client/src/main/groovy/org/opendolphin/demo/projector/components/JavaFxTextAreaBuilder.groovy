package org.opendolphin.demo.projector.components

import javafx.scene.control.TextArea

class JavaFxTextAreaBuilder extends AJavaFxTextFieldBuilder {

    def build() {
        return build(new TextArea())
    }

}
