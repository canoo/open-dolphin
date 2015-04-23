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

import groovyx.javafx.SceneGraphBuilder
import javafx.event.EventHandler
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.RIGHT
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.getTITLE

class SingleAttributeMultipleBindingsView {
    void show(ClientDolphin clientDolphin) {
        start { app ->
            SceneGraphBuilder builder = delegate
            layoutFrame builder
            style builder

            def pm = createPresentationModel(clientDolphin)
            bindPmToViews pm, builder
            attachHandlers pm, builder

            primaryStage.show() // must come last or css shrinks textfield height
        }
    }

    def layoutFrame(SceneGraphBuilder sgb) {
        sgb.stage {
            scene {
                gridPane {
                    label id: 'header', row: 0, column: 1
                    label id: 'label', row: 1, column: 0
                    textField id: 'input', row: 1, column: 1
                    button id: 'submit', row: 3, column: 1, halignment: RIGHT,
                            "Update labels and title"
                }
            }
        }
    }

    ClientPresentationModel createPresentationModel(ClientDolphin dolphin) {
        def titleAttr = dolphin.createAttribute(TITLE, "Some Text: <enter> or <submit>")
        return dolphin.presentationModel('demo', titleAttr)
    }

    void bindPmToViews(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        sgb.with {
            bind TITLE of pm to FX.TITLE of primaryStage   // groovy style

            bind(TITLE).of(pm).to(FX.TEXT).of(label)       // java fluent-interface style

            bind TITLE of pm to FX.TEXT of input

            // auto-update the header with every keystroke
            bind FX.TEXT of input to FX.TEXT of header

            // the below is an alternative that updates the pm with every keystroke and thus all bound listeners
            // bind TEXT of input to TITLE of pm
        }
    }

    void attachHandlers(ClientPresentationModel pm, SceneGraphBuilder sgb) {
        def copyFieldToPm = { pm[TITLE].value = sgb.input.text } as EventHandler
        sgb.input.onAction = copyFieldToPm
        sgb.submit.onAction = copyFieldToPm
    }
}