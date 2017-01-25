/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Callback
import org.opendolphin.core.comm.GetPresentationModelCommand

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

import jfxtras.labs.scene.control.gauge.*
import static jfxtras.labs.scene.control.gauge.Gauge.FrameDesign.CHROME

import static org.opendolphin.demo.LazyLoadingConstants.ATT.*

/**
 * A demo that shows how to easily do lazy loading with the standard Dolphin on-board means.
 * Simply start and see how the table on the left-hand-side fills - depending on the sleepMillis
 * more or less quickly.
 * The details show how big the "imaginary" table is and how many items are loaded lazily from the server.
 * Clicking on any row will display the details in the textField - loaded as a presentation model.
 * Clicking in a not-yet-lazily loaded row does nothing.
 */

class LazyLoadingView {

    static show(ClientDolphin dolphin) {

        ClientPresentationModel dataMold = dolphin.presentationModel('dataMold', [ID, FIRST_LAST, LAST_FIRST, CITY, PHONE])

        ObservableList<Integer> observableList = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate as SceneGraphBuilder

            def gauge = new Radial (
                styleModel: new StyleModel(frameDesign: CHROME),
                title: "Real Load %",
                lcdDecimals : 3,
                prefWidth:  250,
                prefHeight: 250,
                effect: dropShadow(radius: 20, color:rgba(0,0,0,0.4))
            )

            stage title:"Dolphin lazy loading demo", {
                scene width: 750, height: 500, {
                    borderPane {
                        center margin:10, {
                            gridPane styleClass:"form", {
                                columnConstraints  halignment: "right"
                                columnConstraints  halignment: "left"
                                label     row: 0, column: 0, 'Name'
                                textField row: 0, column: 1, id: 'nameField', prefColumnCount:10
                                label     row: 1, column: 0, 'City'
                                textField row: 1, column: 1, id: 'cityField', prefColumnCount:10
                                label     row: 2, column: 0, 'Phone'
                                textField row: 2, column: 1, id: 'phoneField', prefColumnCount:10
                                label     row: 3, column: 0, 'table size:'
                                label     row: 3, column: 1, id:'tableSizeField'
                                label     row: 4, column: 0, 'lazily loaded:'
                                label     row: 4, column: 1, id:"lazilyLoadedField"
                                label     row: 5, column: 0, 'index:'
                                label     row: 5, column: 1, id:"selectedIndexField"
                                node      row: 6, column: 1, gauge
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table') {
                                nameCol = tableColumn(property:'name', text:"Name", prefWidth: 150 )
                                cityCol = tableColumn(property:'city', text:"City", prefWidth: 150 )
                            }
                        }
            }   }   }
            blueStyle sgb

            table.items = observableList

            // all the bindings ...
            bind ID         of dataMold to FX.TEXT of selectedIndexField
            bind FIRST_LAST of dataMold to FX.TEXT of nameField
            bind CITY       of dataMold to FX.TEXT of cityField
            bind PHONE      of dataMold to FX.TEXT of phoneField

            Map<String, SimpleStringProperty> nameProps = [:].withDefault { new SimpleStringProperty("...") }
            Map<String, SimpleStringProperty> cityProps = [:].withDefault { new SimpleStringProperty("...") }

            dolphin.addModelStoreListener(LazyLoadingConstants.TYPE.LAZY, new ModelStoreListener() {
                @Override
                void modelStoreChanged(ModelStoreEvent event) {
                    assert event.type == ModelStoreEvent.Type.ADDED // sanity check. we only have adding events anyway
                    nameProps[event.presentationModel.id].setValue(event.presentationModel[LAST_FIRST]?.value)
                    cityProps[event.presentationModel.id].setValue(event.presentationModel[CITY]?.value)
                }
            })

            // cell values are lazily requested from JavaFX and must return an observable value
            nameCol.cellValueFactory = {
                String lazyId = it.value['id']
                def placeholder = nameProps[lazyId]
                if (placeholder.value == "...") {
                    dolphin.clientConnector.send(new GetPresentationModelCommand(pmId: lazyId))
                }
                return placeholder
            } as Callback
            cityCol.cellValueFactory = {
                String lazyId = it.value['id']
                def placeholder = cityProps[lazyId]
                if (placeholder.value == "...") {
                    dolphin.clientConnector.send(new GetPresentationModelCommand(pmId: lazyId))
                }
                return placeholder
            } as Callback

            // when a table row is selected, we fill the mold and the detail view gets updated
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                dolphin.clientModelStore.withPresentationModel(selectedPm.id.toString(), new WithPresentationModelHandler() {
                    void onFinished(ClientPresentationModel presentationModel) {
                        dolphin.apply presentationModel to dataMold
                    }
                } )
            } as ChangeListener )

            // count the number of lazily loaded pms by listing to the model store to update the counter field
            int count = 0
            dolphin.addModelStoreListener(LazyLoadingConstants.TYPE.LAZY) { ModelStoreEvent evt ->
                if (evt.type == ModelStoreEvent.Type.ADDED) {
                    lazilyLoadedField.text = ++count
                }
            }
            // count the number of lazily loaded pms by listing to the model store to update the gauge LCD
            dolphin.addModelStoreListener(LazyLoadingConstants.TYPE.LAZY) { ModelStoreEvent evt ->
                if (evt.type == ModelStoreEvent.Type.ADDED) {
                    gauge.value = 100d * count / observableList.size()
                }
            }

            // when starting, first fill the table with pm ids
            dolphin.data LazyLoadingConstants.CMD.PULL, { data ->
                for (map in data) {
                    observableList << map
                }
                tableSizeField.text = observableList.size()
            }

            primaryStage.show()
        }
    }
}
