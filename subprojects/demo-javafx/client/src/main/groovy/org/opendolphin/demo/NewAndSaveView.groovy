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
import javafx.event.EventHandler

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.*
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.paint.Color.*

/**
 * This demos shows how to handle the case that a temporary presentation model needs to synchronize
 * with a persistent one, which is not initially available.
 * This approach is only needed when the pm-id is to be made from information that is only available
 * after save (like the db record id). A reasonable alternative is to use a "domainId" attribute.
 */

class NewAndSaveView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def person = dolphin.presentationModel 'selectedPerson', (NAME):'', (LASTNAME):''
            def form   = dolphin.presentationModel 'personForm', mode:'Create'

            stage {
                scene {
                    gridPane {
                        label       id: 'header',        row: 0, column: 1

                        label       id: 'nameLabel',     row: 1, column: 0, 'Name: '
                        textField   id: 'nameInput',     row: 1, column: 1

                        label       id: 'lastnameLabel', row: 2, column: 0, 'Lastname: '
                        textField   id: 'lastnameInput', row: 2, column: 1

                        button      id: 'saveButton',    row: 3, column: 1
                    }
                }
            }

            style delegate

            bind NAME     of person         to FX.TEXT  of nameInput
            bind LASTNAME of person         to FX.TEXT  of lastnameInput
            bind FX.TEXT  of nameInput      to NAME     of person
            bind FX.TEXT  of lastnameInput  to LASTNAME of person

            bind 'mode' of form to FX.TEXT of saveButton
            bind 'mode' of form to FX.TEXT of header, { it == 'Create' ? "New Person" : "Edit Person" }

            bindInfo DIRTY_PROPERTY of person[NAME]         to FX.TEXT_FILL  of nameLabel,     { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of person[LASTNAME]     to FX.TEXT_FILL  of lastnameLabel, { it ? RED : WHITE }
            bindInfo DIRTY_PROPERTY of person               to FX.TITLE      of primaryStage , { it ? '** Unsaved **': '' }
            bindInfo DIRTY_PROPERTY of person               to FX.DISABLE   of saveButton,    { !it }

            saveButton.onAction = {
                if (form.mode.value == "Update") {
                    println "do the update handling"
                    return
                }
                dolphin.send "saveNewSelectedPerson", { pms ->
                    if (pms.size() != 1) return // not getting back a new pm means that save was not successful
                    dolphin.apply pms.first() to person
                    form.mode.value = "Update"
                }
            } as EventHandler

            primaryStage.show()
        }
    }
}
