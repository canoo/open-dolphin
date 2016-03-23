/*
 * Copyright 2012-2016 Canoo Engineering AG.
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

import com.sun.javafx.binding.StringFormatter
import groovyx.javafx.beans.FXBindable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import org.opendolphin.binding.Binder
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.ClientDolphin

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.*
import static groovyx.javafx.GroovyFX.start
import static javafx.geometry.HPos.RIGHT

class MultipleAttributesView {

    static show(ClientDolphin clientDolphin) {

        start { app ->
            // construct the PM
            def titleAttr = new ClientAttribute(TITLE)
            titleAttr.value = "Update label on keystroke"
            def purposeAttr = new ClientAttribute(PURPOSE)
            purposeAttr.value = "Update label on action"
            def pm = new ClientPresentationModel('demo',[titleAttr, purposeAttr])
            clientDolphin.clientModelStore.add pm
            pm.rebase()

            def updateTitle   = { pm.title.value = titleInput.text }
            def updatePurpose = { pm.purpose.value = purposeInput.text }

            stage {
                scene {
                    gridPane  {

                        label id: 'header', row: 0, column: 1,
                              'A composite presentation model'

                        label       'Title: ',        row: 1, column: 0
                        label       id: 'titleLabel', row: 1, column: 1
                        textField   id: 'titleInput', row: 2, column: 1,
                              onAction: updateTitle, onKeyReleased: updateTitle

                        label       'Purpose: ',        row: 3, column: 0
                        label       id: 'purposeLabel', row: 3, column: 1
                        textField   id: 'purposeInput', row: 4, column: 1,
                              onAction: updatePurpose

                        label       id: 'bothAreDirtyLabel',  row: 5, column: 1
                        label       id: 'eitherIsDirtyLabel', row: 6, column: 1

                        button "Update labels", row: 7, column: 1,
                              halignment: RIGHT,
                              onAction: {
                                  updateTitle()
                                  updatePurpose()
                              }
            }   }   }

            style delegate

            bind TITLE of pm to FX.TEXT of titleLabel
            Binder.bind TITLE of pm to FX.TEXT of titleInput

            bind PURPOSE of pm to FX.TEXT of purposeLabel
            Binder.bind PURPOSE of pm to FX.TEXT of purposeInput

            // binding some observable boolean information to the JavaFX property of a combinator
            def combo = new PropertyCombo()
            bindInfo DIRTY_PROPERTY of pm.title   to "a" of combo
            bindInfo DIRTY_PROPERTY of pm.purpose to "b" of combo

            // using the and/or combination as the single source of whatever binding
            bothAreDirtyLabel.textProperty().bind(StringFormatter.concat("Are both dirty: ",   combo.both().asString()))
            eitherIsDirtyLabel.textProperty().bind(StringFormatter.concat("Is either dirty: ", combo.either().asString()))

            primaryStage.show()
        }
    }

    static class PropertyCombo {
        @FXBindable boolean a
        @FXBindable boolean b
        BooleanProperty both() {
            def result = new SimpleBooleanProperty()
            result.bind(a().and(b()))
            return result
        }
        BooleanProperty either() {
            def result = new SimpleBooleanProperty()
            result.bind(a().or(b()))
            return result
        }

    }
}
