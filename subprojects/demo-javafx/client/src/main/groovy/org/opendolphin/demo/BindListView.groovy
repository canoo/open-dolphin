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
import org.opendolphin.core.client.GClientPresentationModel
import org.opendolphin.core.client.GClientDolphin
import groovyx.javafx.SceneGraphBuilder

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback

import static org.opendolphin.demo.DemoStyle.blueStyle
import static VehicleConstants.*

import static groovyx.javafx.GroovyFX.start

import org.opendolphin.core.client.ClientAttributeWrapper
import org.opendolphin.core.ModelStoreEvent

/**
 * This demos shows two list views on the same list of PresentationModels where one list view shows
 * all models of a given type and the second view shows only a subset (the "magenta" ones).
 * It also shows how to bind against a (changing) list of PresentationModels of a certain type and how
 * to use an additional custom filter.
 * How to use: initially, the right view should be empty (no magenta ones).
 * Clicking the add button adds magenta objects to the store and they should appear in both list views.
 * Clicking the clear button should empty both lists.
 */

class BindListView {

    static show(ClientDolphin dolphin) {

        ObservableList<GClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        ObservableList<GClientPresentationModel> observableListOfMagentaPms = FXCollections.observableArrayList()

        dolphin.addModelStoreListener TYPE_VEHICLE, { evt ->
            syncList(observableListOfPms, evt)
        }

        dolphin.addModelStoreListener TYPE_VEHICLE, { evt ->
            if (! evt.presentationModel.id.startsWith('magenta')) return
            syncList(observableListOfMagentaPms, evt)
        }

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        left margin: 10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                x1Col = tableColumn(text: 'X', prefWidth: 40)
                            }
                        }
                        center margin: 10, {
                            tableView(id: 'smallTable', opacity: 0.2d) {
                                tableColumn(property: 'id', text: "Color", prefWidth: 50)
                                x2Col = tableColumn(text: 'X', prefWidth: 40)
                            }
                        }

                        right margin: 10, {
                            vbox {
                                button id:'add', text:'Add'
                                button id:'clear', text:'Clear'
                            }
                        }
                    }
                }
            }

            blueStyle sgb

            table.items = observableListOfPms
            smallTable.items = observableListOfMagentaPms

			add.onAction {
                dolphin.presentationModel "magenta_${System.currentTimeMillis()}",
                   TYPE_VEHICLE,
                   (ATT_X) : 0
			}

            clear.onAction {
                dolphin.send CMD_CLEAR
			}

            // auto-update the cell values
            x1Col.cellValueFactory = { new ClientAttributeWrapper(it.value[ATT_X]) } as Callback
            x2Col.cellValueFactory = { new ClientAttributeWrapper(it.value[ATT_X]) } as Callback

            dolphin.send CMD_PULL, { pms ->
                fadeTransition(1.s, node: table,      to: 1).playFromStart()
                fadeTransition(1.s, node: smallTable, to: 1).playFromStart()
            }

            // startup and main loop
            primaryStage.show()
        }
    }

    def static void syncList(ObservableList<GClientPresentationModel> list, ModelStoreEvent evt) {
        switch (evt.type) {
            case ModelStoreEvent.Type.ADDED:
                list << evt.presentationModel
                break
            case ModelStoreEvent.Type.REMOVED:
                list.remove(evt.presentationModel)
        }
    }
}
