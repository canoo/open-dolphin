/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

package org.opendolphin.demo

import javafx.scene.control.Tooltip
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientDolphin

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.core.Tag.LABEL
import static org.opendolphin.core.Tag.TOOLTIP
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.getLASTNAME
import static org.opendolphin.demo.MyProps.ATT.getNAME

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

class AttributeTagView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def model = dolphin.presentationModel 'person', (NAME): '', (LASTNAME): 'Smith'

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1, 'Person Form'

                        label id: 'nameLabel', row: 1, column: 0, ' ' * 20
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', row: 2, column: 0, ' ' * 20
                        textField id: 'lastnameInput', row: 2, column: 1

                        hbox row: 3, column: 1, spacing: 5, {
                            button id: 'german', 'German', onAction: { dolphin.send 'german' }
                            button id: 'reset', 'Undo', onAction: { model.reset() }
                        }
                    }
                }
            }
            def sgb = delegate
            style sgb

            // binding the values
            bind NAME of model to FX.TEXT of nameInput
            bind LASTNAME of model to FX.TEXT of lastnameInput

            dolphin.send 'init', { pms ->        // only do binding after server has initialized the tags
                bind FX.TEXT of nameInput to NAME of model, { newVal ->
                    boolean matches = newVal ==~ model.getAt(NAME, Tag.REGEX).value
                    putStyle(sgb.nameInput, !matches, 'invalid')
                    return newVal
                }
                bind FX.TEXT of lastnameInput to LASTNAME of model

                bind NAME, LABEL of model to FX.TEXT of nameLabel
                bind NAME, TOOLTIP of model to FX.TOOLTIP of nameInput, { new Tooltip(it) }

                bind LASTNAME, LABEL of model to FX.TEXT of lastnameLabel
            }

            // binding meta properties
            model[NAME].addPropertyChangeListener DIRTY_PROPERTY, {
                putStyle sgb.nameLabel, it.newValue, DIRTY_PROPERTY
            }
            model[LASTNAME].addPropertyChangeListener DIRTY_PROPERTY, {
                putStyle sgb.lastnameLabel, it.newValue, DIRTY_PROPERTY
            }
            bindInfo DIRTY_PROPERTY of model to FX.TITLE of primaryStage, { it ? '** DIRTY **' : '' }
            bindInfo DIRTY_PROPERTY of model to FX.DISABLE of reset, { !it }

            primaryStage.show()
        }
    }

    static void putStyle(node, boolean addOrRemove, String styleClassName) {
        if (addOrRemove) {
            node.styleClass.add(styleClassName)
        } else {
            node.styleClass.remove(styleClassName)
        }
    }

}
