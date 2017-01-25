/*
 * Copyright 2012-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.demo.projector

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.demo.FX

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.core.Tag.*
import static org.opendolphin.demo.DemoStyle.style

/**
 * An example where not only the values and dirty properties are bound but also the
 * text of labels is determined by tag attributes.
 * In addition the "first name" input field gets a tooltip via dolphin tags and a validator
 * that uses the REGEX tag. When the first name contains an 'a' is considered valid and displayed in black,
 * otherwise in red.
 * Hitting the "German" button sets the label tags to their German translation.
 * The dirty state of the presentation model must be independent of the labels now being "dirty".
 * Hitting "Undo" must revert all changes including the label changes and dirty states.
 */

class SimpleFormView {

    static show(ClientDolphin dolphin) {
        start { // we have UI-toolkit specific starting sequence

            IProjector projector = new JavaFxProjector(dolphin: dolphin, stage: primaryStage)

            IPresentation form  = projector.createSimpleForm("person")    // using the specific projector
            IPresentation frame = projector.createFrame(form, 400, 200)   // composing presentations

            style delegate // styling could also be part of the projection
            frame.visible = true
        }
    }
}


/** Abstract Factory pattern */
interface IProjector {
    IPresentation createFrame(IPresentation root, double width, double height)
    IPresentation createSimpleForm(String pmId)
}

interface IPresentation {
    void setVisible(boolean visible)
    Object getWidget()
}

class JavaFxPresentation implements IPresentation {
    javafx.scene.Node node
    void setVisible(boolean visible) {
        node.setVisible(visible)
    }
    javafx.scene.Node getWidget() {
        return node
    }
}

class JavaFxStage implements IPresentation {
    Stage stage
    void setVisible(boolean visible) {
        if (visible) stage.show()
        else stage.close()
    }
    Stage getWidget() {
        return stage
    }
}

class JavaFxProjector implements IProjector {
    ClientDolphin   dolphin
    Stage           stage

    JavaFxStage createFrame(IPresentation root, double width, double height) {
        Parent sceneRoot = (Parent) root.widget
        Scene scene = new Scene(sceneRoot, width, height)
        stage.scene = scene
        return new JavaFxStage(stage: stage)
    }

    JavaFxPresentation createSimpleForm(String pmId) {
        def grid = new GridPane()

        dolphin.send 'init', { pms ->        // only do binding after server has initialized the tags
            ClientPresentationModel model = dolphin.getAt(pmId)
            int row = 0

            // make a new row in the grid for each attribute in the form's presentation model
            for (ClientAttribute valAtt in model.attributes.findAll{it.tag == VALUE}) {
                String propName = valAtt.propertyName
                def labelAtt    = model.getAt(propName, LABEL)
                def label       = new Label(propName) // if there is no label tag, use property name as fallback
                grid.add(label, 0, row)
                if (labelAtt) {
                    bind propName, LABEL of model to FX.TEXT of label  // label may change at runtime
                }
                def input = new TextField()           // we currently assume text fields only. More is to come here
                grid.add(input, 1, row)
                bind propName of model to FX.TEXT of input

                if (model.getAt(propName, REGEX)) {   // bind regex validator if applicable
                    Closure regexer = { newVal ->
                        boolean matches = newVal ==~ model.getAt(propName, REGEX).value
                        putStyle(input, !matches, 'invalid')
                        return newVal
                    }
                    bind FX.TEXT of input using regexer to propName of model
                } else {
                    bind FX.TEXT of input to propName of model
                }

                if (model.getAt(propName, TOOLTIP)) {
                    bind propName, TOOLTIP of model to FX.TOOLTIP of input, { new Tooltip(it) }
                }

                row++
            }

            // make a button for each supplied action that is only enabled when the form is dirty

            Closure inverter = { ! it }

            def buttons = new HBox(5)
            grid.add(buttons, 1, row)

            ClientPresentationModel actions = dolphin.getAt(pmId+".actions")
            for (ClientAttribute valAtt in actions.attributes.findAll{it.tag == VALUE}) {
                String actionName = valAtt.value
                String propName   = valAtt.propertyName
                def buttonModel   = valAtt.presentationModel
                def button        = new Button()
                bind propName, LABEL   of buttonModel to FX.TEXT    of button
                bind propName, TOOLTIP of buttonModel to FX.TOOLTIP of button, { new Tooltip(it) }
                button.onAction = { dolphin.send(actionName) }
                bindInfo DIRTY_PROPERTY of model using inverter to FX.DISABLE of button
                buttons.children.add(button)
            }
        }

        return new JavaFxPresentation(node: grid)
    }

    static void putStyle(node, boolean addOrRemove, String styleClassName) {
        if (addOrRemove) {
            node.styleClass.add(styleClassName)
        } else {
            node.styleClass.remove(styleClassName)
        }
    }
}