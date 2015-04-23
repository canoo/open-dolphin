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

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientDolphin

import static javafx.scene.paint.Color.*
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.*
import static groovyx.javafx.GroovyFX.start

import static org.opendolphin.core.Attribute.DIRTY_PROPERTY

/**
 * This demo shows how to bind against the dirty state of attributes and their full presentation model.
 * How to use: changing any value in any text field should make its label red, enable the save button,
 * and change the frame title. Undoing the changes must revert the effect.
 * Clicking the button has no effect.
 */


class DirtyAttributeFlagView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def model = dolphin.presentationModel 'person', (NAME):'', (LASTNAME):'Smith'

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1,
                                'Person Form'

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'lastnameLabel', 'Lastname: ', row: 2, column: 0
                        textField id: 'lastnameInput', row: 2, column: 1

                        button id: 'saveButton', 'Save', row: 3, column: 1
                    }
                }
            }

            style delegate

            bind NAME     of model          to FX.TEXT      of nameInput
            bind LASTNAME of model          to FX.TEXT      of lastnameInput
            bind FX.TEXT  of nameInput      to NAME         of model
            bind FX.TEXT  of lastnameInput  to LASTNAME     of model

            bindInfo DIRTY_PROPERTY of model[NAME]         to FX.TEXT_FILL  of nameLabel,     { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model[LASTNAME]     to FX.TEXT_FILL  of lastnameLabel, { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of model               to FX.TITLE      of primaryStage , { it ? '** DIRTY **': '' }
            bindInfo DIRTY_PROPERTY of model               to FX.DISABLE   of saveButton,    { !it }

            primaryStage.show()
        }
    }

}
