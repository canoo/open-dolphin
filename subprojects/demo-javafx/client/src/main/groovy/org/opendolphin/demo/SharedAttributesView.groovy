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

import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.ClientDolphin
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList

import java.beans.PropertyChangeListener

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.DemoStyle.blueStyle
import static VehicleConstants.*
import static org.opendolphin.demo.VehicleTaskConstants.ATT_DESCRIPTION
import static groovyx.javafx.GroovyFX.start

class SharedAttributesView {

    static show(ClientDolphin clientDolphin) {

        def selectedVehicle = clientDolphin.presentationModel null, vehiclePmId : null

        ObservableList<ClientPresentationModel> observableListOfPms   = FXCollections.observableArrayList()
        ObservableList<ClientPresentationModel> observableListOfTasks = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate as SceneGraphBuilder
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        left margin: 10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                value ATT_X,        tableColumn(text: 'X', prefWidth: 40)
                                value ATT_Y,        tableColumn(text: 'Y', prefWidth: 40)
                                value ATT_ROTATE,   tableColumn(text: 'Angle')
                            }
                        }
                        center margin: [10, 0, 10, 0], {
                            tabPane id: 'vehicles'
                        }
                        right margin: 10, {
                            tableView(id: 'taskTable', opacity: 0.2d) {
                                value ATT_DESCRIPTION,  tableColumn(text: "Task", prefWidth: 100)
                                value ATT_COLOR,        tableColumn(text: 'Vehicle Color', prefWidth: 50)
                                value ATT_X,            tableColumn(text: 'Vehicle X',     prefWidth: 50)
                            }
                        }
                    }
                }
            }
            table.items = observableListOfPms
            taskTable.items = observableListOfTasks

            // startup and main loop

            clientDolphin.send VehicleTaskConstants.CMD_PULL, { pms ->
                for (pm in pms) {
                    observableListOfTasks << pm
                }
                fadeTransition(1.s, node: taskTable, to: 1).playFromStart()

                clientDolphin.send VehicleConstants.CMD_PULL, { vehiclePMs ->
                    for (pm in vehiclePMs) {
                        observableListOfPms << pm
                    }
                    fadeTransition(1.s, node: table, to: 1).playFromStart()
                }
            }

            blueStyle sgb

            // all the bindings ...

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                selectedVehicle.vehiclePmId.value = selectedPm.id
            } as ChangeListener)

            taskTable.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                selectedVehicle.vehiclePmId.value = selectedPm.fill.value
            } as ChangeListener)

            selectedVehicle.vehiclePmId.addPropertyChangeListener('value', { evt ->
                def selectedPmId = evt.newValue
                def tab = sgb.vehicles.tabs.find { it.id == selectedPmId }
                if (!tab) {
                    def grid
                    tab = sgb.tab id: selectedPmId, {
                        grid = gridPane hgap: 5, vgap: 5, padding: 10, alignment: "top_left", opacity: 0.3d, translateY: -200, {
                            columnConstraints(halignment: "right")
                            text ' X:', row: 0, column: 0
                            textField id: 'x', prefColumnCount: 3, row: 0, column: 1
                            text ' Y:', row: 1, column: 0
                            textField id: 'y', prefColumnCount: 3, row: 1, column: 1
                            text ' Angle:', row: 2, column: 0
                            textField id: 'angle', prefColumnCount: 3, row: 2, column: 1
                            text ' Width:', row: 3, column: 0
                            textField id: 'width', prefColumnCount: 3, row: 3, column: 1
                            translateTransition(0.5.s, to: 0).playFromStart()
                        }
                    }

                    def detailPm = clientDolphin.findPresentationModelById(selectedPmId)
                    assert detailPm

                    bind ATT_COLOR  of detailPm  to FX.TEXT   of tab

                    bind ATT_X      of detailPm  to FX.TEXT   of sgb.x
                    bind FX.TEXT    of sgb.x     to ATT_X     of detailPm

                    bind ATT_Y      of detailPm  to FX.TEXT   of sgb.y
                    bind ATT_ROTATE of detailPm  to FX.TEXT   of sgb.angle

                    bind ATT_WIDTH  of detailPm  to FX.TEXT   of sgb.width
                    bind FX.TEXT    of sgb.width to ATT_WIDTH of detailPm

                    fadeTransition(1.s, node: grid, to: 1).playFromStart()

                    sgb.vehicles.tabs << tab
                }
                sgb.vehicles.selectionModel.select(tab)
            } as PropertyChangeListener)

            primaryStage.show()
        }
    }


}
