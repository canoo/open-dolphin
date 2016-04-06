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

import org.opendolphin.core.client.ClientAttributeWrapper
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import java.beans.PropertyChangeListener

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.demo.DemoStyle.style
import static groovyx.javafx.GroovyFX.start
import static javafx.scene.paint.Color.*

/**
 * This demo shows how to deal with more complex isDirty requirements.
 * Use case: portfolio with many positions. Each position is a financial instrument with a weight.
 * The portfolio is considered dirty when any of its core attributes changes or any position changes.
 * The Save button should only be enabled when the portfolio is dirty. Hitting the button does nothing.
 * Approach: put an additional "saveable" attribute on the portfolio and keep it up to date
 * with any isDirty changes of relevant sources.
 * This approach can even work in a "cascading" style.
 * With complex isDirty logic, the question arises whether this logic should reside
 * on the server - and called via commands from the client.
 */

class ComposedDirtyView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def portfolio = dolphin.presentationModel 'portfolio1', name:'Blue chips', date:'14.04.2009', saveable:false
            def selectedPosition = dolphin.presentationModel "selected", weight: null

            ObservableList<ClientPresentationModel> positions = FXCollections.observableArrayList()

            stage {
                scene {
                    gridPane {

                        label id: 'header', row: 0, column: 1, 'Portfolio 1'

                        label id: 'nameLabel', 'Name: ', row: 1, column: 0
                        textField id: 'nameInput', row: 1, column: 1

                        label id: 'dateLabel', 'Effective date: ', row: 2, column: 0
                        textField id: 'dateInput', row: 2, column: 1


                        label id: 'instLabel', 'Instruments: ', row: 3, column: 0
                        tableView(id: 'table', row: 3, column: 1) {
                            instCol     = tableColumn(text:'Instrument')
                            weightCol   = tableColumn(text:'Weight')
                        }

                        hbox row: 4, column: 1, spacing:5, {
                            button id: 'saveButton', 'Save'
                            button '+ 10', id:'plus',  onAction: { selectedPosition.weight.value += 10 }
                            button '- 10', id:'minus', onAction: { selectedPosition.weight.value -= 10 }
                        }
                    }
                }
            }
            table.items = positions

            style delegate

            // auto-update the cell values
            instCol.cellValueFactory   = { return new ClientAttributeWrapper(it.value.instrument) } as Callback
            weightCol.cellValueFactory = { return new ClientAttributeWrapper(it.value.weight) } as Callback

            [IBM:20, APPLE:70, HP:10].each { instrument, weight ->
                def pm = dolphin.presentationModel("portfolio1-$instrument","Position",
                     instrument:instrument, weight:weight, portfolio:1)
                pm.weight.qualifier = "portfolio1-$instrument-weight"
                positions.add pm
            }

            plus.disabled = true
            minus.disabled = true
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selection ->
                dolphin.apply selection to selectedPosition
                plus.disabled = false
                minus.disabled = false
            } as ChangeListener )

            // bi-directional binding of the core portfolio attributes
            bind 'name' of portfolio to 'text' of nameInput
            bind 'date' of portfolio to 'text' of dateInput
            bind 'text' of nameInput to 'name' of portfolio
            bind 'text' of dateInput to 'date' of portfolio

            // display of dirty core attributes
            bindInfo DIRTY_PROPERTY of portfolio.name using { it ? RED : WHITE } to 'textFill' of nameLabel
            bindInfo DIRTY_PROPERTY of portfolio.date using { it ? RED : WHITE } to 'textFill' of dateLabel

            // display of composed dirtyness
            bind 'saveable' of portfolio to 'title'    of primaryStage , { it ? '** DIRTY **': '' }
            bind 'saveable' of portfolio to 'disabled' of saveButton,    { !it }

            // when any relevant state changes, update the 'saveable' attribute
            def relevant = portfolio.attributes - portfolio.saveable + positions
            relevant*.addPropertyChangeListener('dirty', { evt ->
                portfolio.saveable.value = relevant.any { it.isDirty() }
            } as PropertyChangeListener)

            primaryStage.show()
        }
    }

}
