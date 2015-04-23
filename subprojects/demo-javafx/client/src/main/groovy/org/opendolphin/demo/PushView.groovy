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
import org.opendolphin.logo.DolphinLogoBuilder
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.shape.Rectangle

import java.beans.PropertyChangeListener

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.DemoStyle.blueStyle
import static VehicleConstants.*
import static groovyx.javafx.GroovyFX.start

class PushView {

    static show(ClientDolphin dolphin) {

        def longPoll = null
        longPoll = { dolphin.send CMD_UPDATE, longPoll }

        GClientPresentationModel selectedVehicle = dolphin.presentationModel(ID_SELECTED, ALL_ATTRIBUTES)

        ObservableList<GClientPresentationModel> observableListOfPms = FXCollections.observableArrayList()
        Map<String, Rectangle> pmIdsToRect = [:] // pmId to rectangle

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        top margin:10, {
                            hbox alignment:'center', prefWidth: 700, spacing:5, id:'header', {
                                label 'Selected'
                                rectangle(id:'selRect', arcWidth:10, arcHeight:10, width:74, height:20, stroke: cyan, strokeWidth: 2, strokeType:'outside') {
                                    effect dropShadow(offsetY:2, offsetX:2, radius:3, input: lighting{distant(azimuth: -135.0)})
                                }
                                label ' X:';     textField id: 'selX', prefColumnCount:3
                                label ' Y:';     textField id: 'selY', prefColumnCount:3
                                label ' Angle:'; rectangle id: 'selAngle', width:26, height:5, fill: linearGradient(stops: [[0.6, white], [1, red]])
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table', opacity: 0.2d) {
                                tableColumn(property:'id',    text:"Color", prefWidth: 50 )
                                value ATT_X,      tableColumn(text:'X',     prefWidth: 40)
                                value ATT_Y,      tableColumn(text:'Y',     prefWidth: 40)
                                value ATT_ROTATE, tableColumn(text:'Angle')
                            }
                        }
                        stackPane {
                            group {
                                def logo = new DolphinLogoBuilder().width(401).height(257).build()
                                def strokes = new ArrayList<Node>(logo.getChildren())
                                for (stroke in strokes) {
                                    path(stroke)
                                    stroke.opacity = 0.1d
                                }
                            }
                            group id: 'parent', effect: dropShadow(offsetY: 2, offsetX: 2, radius: 3, input: lighting{distant(azimuth: -135.0)}), {
                                rectangle(x: 0, y: 0, width: 400, height: 400, fill: transparent, stroke: groovyblue, strokeWidth: 0) // rigidArea
            }   }   }   }   }

            table.items = observableListOfPms

            // used as both, event handler and change listener
            def changeSelectionHandler = { pm ->
                return {
                    dolphin.apply pm to selectedVehicle
                }
            }

            // when a new pm is added to the list, create the rectangles along with their animations
            observableListOfPms.addListener({ ListChangeListener.Change listChange ->
                while(listChange.next()) { /*sigh*/
                    for (GClientPresentationModel pm in listChange.addedSubList) {
                        pmIdsToRect[pm.id] = sgb.rectangle(fill: sgb[pm.id], arcWidth:10, arcHeight:10, stroke: cyan, strokeWidth: 0, strokeType:'outside') {
                            //effect lighting()
                        }
                        Rectangle rectangle = pmIdsToRect[pm.id]
                        rectangle.onMouseClicked = changeSelectionHandler(pm) as EventHandler
                        pm.attributes*.propertyName.each { prop ->
                            if(prop == 'fill') return // only for the moment - until we convert types
                            rectangle[prop] = pm[prop].value
                            pm[prop].addPropertyChangeListener 'value', { evt ->
                                sgb.timeline {
                                    at(0.5.s) { change(rectangle, prop) to evt.newValue tween "ease_both" }
                                }.play()
                            } as PropertyChangeListener
                        }
                        sgb.parent.children << rectangle
                    }
                }
            } as ListChangeListener)

            // startup and main loop

            dolphin.send CMD_PULL, { pms ->
                for (pm in pms) {
                    observableListOfPms << pm
                }
                fadeTransition(1.s, node:table, to:1).playFromStart()
                longPoll()
            }

            blueStyle sgb

            // all the bindings ...

            bind ATT_X      of selectedVehicle to FX.TEXT   of selX // simple binding + action
            selX.onAction = { selectedVehicle[ATT_X].value = (it.source.text ?: 0).toInteger() } as EventHandler

            bind ATT_Y      of selectedVehicle to FX.TEXT   of selY // example of a "bidirectional" binding
            bind FX.TEXT    of selY            to ATT_Y     of selectedVehicle, { it ? it.toInteger() : 0 }

            bind ATT_ROTATE of selectedVehicle to FX.ROTATE of selAngle, { (it ?: 0 ).toDouble() }
            bind ATT_COLOR  of selectedVehicle to FX.FILL   of selRect,  { it ? sgb[it] : sgb.transparent }

            // bind 'selectedItem' of table.selectionModel to { ... }
            table.selectionModel.selectedItemProperty().addListener( { o, oldVal, selectedPm ->
                changeSelectionHandler(selectedPm).call()
            } as ChangeListener )

            // bind COLOR of selectedVehicle to { ... }
            selectedVehicle[ATT_COLOR].addPropertyChangeListener('value', { evt ->
                def from = evt.oldValue
                def to   = evt.newValue
                if (from ) pmIdsToRect[from].strokeWidth = 0
                pmIdsToRect[to].strokeWidth = 3
            } as PropertyChangeListener)

            selectedVehicle[ATT_COLOR].addPropertyChangeListener('value', { evt ->
                def to   = evt.newValue
                table.selectionModel.select dolphin.findPresentationModelById(to)
            } as PropertyChangeListener)

            primaryStage.show()
        }
    }
}
