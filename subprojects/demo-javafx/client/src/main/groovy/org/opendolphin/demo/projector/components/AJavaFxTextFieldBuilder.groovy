package org.opendolphin.demo.projector.components

import javafx.scene.control.TextInputControl
import javafx.scene.control.Tooltip
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.demo.FX

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.core.Tag.REGEX
import static org.opendolphin.core.Tag.TOOLTIP

abstract class AJavaFxTextFieldBuilder {
    protected String propName
    protected ClientPresentationModel model
    protected TextInputControl input;

    def withProperties(properties) {
        properties.each { name, value ->
            this."${name}" = value
        }
        assert model
        assert propName
        assert model.getAt(propName)
        this
    }

    def build(input) {
        assert input
        this.input = input
        setBehavior()
        this.input
    }

    protected void setBehavior() {
        bind propName of model to FX.TEXT of input
        regExpBind()
        tooltipBind()
    }


    protected void tooltipBind() {
        if (model.getAt(propName, TOOLTIP)) {
            bind propName, TOOLTIP of model to FX.TOOLTIP of input, { new Tooltip(it) }
        }
    }

    protected void regExpBind() {
        if (model.getAt(propName, REGEX)) {   // bind regex validator if applicable
            Closure regexer = { newVal ->
                boolean matches = newVal ==~ model.getAt(propName, REGEX).value
                setStyle(input, !matches, 'invalid')
                return newVal
            }
            bind FX.TEXT of input using regexer to propName of model
        } else {
            bind FX.TEXT of input to propName of model
        }
    }

    protected static void setStyle(node, boolean addStyle, String styleClassName) {
        if (addStyle) {
            node.styleClass.add(styleClassName)
        } else {
            node.styleClass.remove(styleClassName)
        }
    }

}
