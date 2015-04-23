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

import org.opendolphin.binding.Binder
import org.opendolphin.core.client.ClientDolphin

import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.RIGHT
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.getPURPOSE
import static org.opendolphin.demo.MyProps.ATT.getTITLE

class MultipleAttributesView {

    static show(ClientDolphin clientDolphin) {

        start { app ->
            // construct the PM
            def titleAttr = clientDolphin.createAttribute(TITLE)
            titleAttr.value = "A PM with multiple attributes"
            def purposeAttr = clientDolphin.createAttribute(PURPOSE)
            purposeAttr.value = "Show the need for PMs"
            def pm = clientDolphin.createPresentationModel('demo', [titleAttr, purposeAttr])
            clientDolphin.clientModelStore.add pm

            def updateTitle = { pm.title.value = titleInput.text }
            def updatePurpose = { pm.purpose.value = purposeInput.text }

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1,
                                'A composite presentation model'

                        label 'Title: ', row: 1, column: 0
                        label id: 'titleLabel', row: 1, column: 1
                        textField id: 'titleInput', row: 2, column: 1,
                                onAction: updateTitle, onKeyReleased: updateTitle

                        label 'Purpose: ', row: 3, column: 0
                        label id: 'purposeLabel', row: 3, column: 1
                        textField id: 'purposeInput', row: 4, column: 1,
                                onAction: updatePurpose

                        button "Update labels", row: 5, column: 1,
                                halignment: RIGHT,
                                onAction: {
                                    updateTitle()
                                    updatePurpose()
                                }
                    }
                }
            }

            style delegate

            bind TITLE of pm to FX.TEXT of titleLabel
            Binder.bind TITLE of pm to FX.TEXT of titleInput

            bind PURPOSE of pm to FX.TEXT of purposeLabel
            Binder.bind PURPOSE of pm to FX.TEXT of purposeInput

            primaryStage.show()
        }
    }
}
