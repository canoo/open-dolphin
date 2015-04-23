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
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.util.Callback

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoSearchProperties.*
import static org.opendolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

class DemoSearchView {

    static show(ClientDolphin clientDolphin) {

        def searchCriteria = clientDolphin.presentationModel(SEARCH_CRITERIA, [FIRST, SECOND, NAME])

        ObservableList<GClientPresentationModel> observableListOfKoMaps = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 500, height: 500, {
                    borderPane {
                        top margin:10, {
                            gridPane styleClass:"form", {
                                label       'One first choice', row:0, column:0
                                choiceBox   id:'gvf',           row:0, column:1, items: FXCollections.observableArrayList(), opacity: 0.2
                                label       'Another one',      row:1, column:0
                                choiceBox   id:'dst',           row:1, column:1, items: FXCollections.observableArrayList(), opacity: 0.2
                                label       'Name',             row:2, column:0
                                textField   id:'bez',           row:2, column:1
                                button      'Search', id:'search', row:3, column:1
                            }

                        }
                        center margin:10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                idCol     = tableColumn(property:'id', text:"Contact", prefWidth: 100 )
                                koNameCol = tableColumn(text:'Name', prefWidth: 100)
                                dateCol   = tableColumn(text:'Date', prefWidth: 250)
                            }
                        }
            }   }   }

            table.items = observableListOfKoMaps

            idCol.cellValueFactory     = { new SimpleStringProperty( it.value["id"])         } as Callback
            koNameCol.cellValueFactory = { new SimpleStringProperty( it.value[CONTACT_NAME]) } as Callback
            dateCol.cellValueFactory   = { new SimpleStringProperty( it.value[CONTACT_DATE]) } as Callback

            clientDolphin.data FIRST_FILL_CMD,  { maps ->
                for (data in maps) {
                    gvf.items << data[TEXT]
                }
                gvf.selectionModel.selectedIndex = 0
                fadeTransition(1.s, node: gvf, to: 1).playFromStart()
            }

            clientDolphin.data SECOND_FILL_CMD, { maps ->
                for (data in maps) {
                    dst.items << data[TEXT]
                }
                dst.selectionModel.selectedIndex = 0
                fadeTransition(1.s, node: dst, to: 1).playFromStart()
            }

            // listeners
            search.onAction = {
                search.disabled = true
                searchCriteria[NAME].value = bez.text
                table.opacity = 0.2
                observableListOfKoMaps.clear()
                clientDolphin.data SEARCH_CMD, { maps ->
                    for (data in maps) {
                        observableListOfKoMaps << data
                    }
                    search.disabled = false
                    fadeTransition(0.5.s, node: table, to: 1).playFromStart()
                }
            } as EventHandler

            blueStyle sgb

            // all the bindings ...

            bind FIRST of searchCriteria to 'value' of gvf
            gvf.selectionModel.selectedItemProperty().addListener( { o, oldVal, newVal ->
                searchCriteria[FIRST].value = newVal
            } as ChangeListener)

            bind SECOND of searchCriteria to 'value' of dst
            dst.selectionModel.selectedItemProperty().addListener( { o, oldVal, newVal ->
                searchCriteria[SECOND].value = newVal
            } as ChangeListener)

            bind NAME of searchCriteria to 'text' of bez

            primaryStage.show()
        }
    }
}
