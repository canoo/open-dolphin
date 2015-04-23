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

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientDolphin
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.demo.DemoStyle.blueStyle
/**

 */

class SmallFootprintView {



    static show(ClientDolphin providerDolphin, ClientDolphin updateDolphin) {

        start { app ->
            def sgb = delegate

            def sfPm = providerDolphin.presentationModel 'sf', sfTrigger:''
            sfPm.sfTrigger.qualifier = "sfTrigger"

            boolean paint = false

            def colors = "black blue red green transparent white orange".tokenize()
            Color color = Color.BLACK
            String colorStr = "black"

            def circles = [:]


            stage title: "Dolphin with many rectangles but small footprint", {
                scene width: 394, height: 420, {
                    borderPane {
                        top {
                            hbox alignment: 'center', padding:5, {
                                choiceBox(value: colorStr, items: colors) {
                                    onSelect { control, item ->
                                        colorStr = item
                                        color = Color.valueOf(item)
                                    }
                                }
                            }
                        }
                        pane id:'rects', {
                            (1..128).each { int row ->
                                (1..128).each { int col ->
                                    String id = ""+row+" "+col
                                    circles[id] = circle id:id, centerX: col * 3, centerY: row * 3, radius: 3, fill: Color.rgb(0,0,0,0.1)
                                }
                            }
                        }
                    }
                }
            }
            blueStyle sgb

            rects.onMouseDragged = {
                int row = it.y.toInteger().intdiv(3)
                int col = it.x.toInteger().intdiv(3)
                Circle target = circles[""+row+" "+col]
                if (!target) return
                def oldfill = target.fill
                target.fill = color
                Platform.runLater {
                    if (oldfill == color) return
                    sfPm.sfTrigger.value = target.id + " " + colorStr
                }
            } as EventHandler


            Closure longPoll
            longPoll = { updateDolphin.send "poll.sfTrigger", new OnFinishedHandlerAdapter(){
                @Override
                void onFinishedData(List<Map> data) {
                    for (map in data) {
                        def (row,col,fill) = map.value.tokenize(' ')
                        circles[""+row+" "+col].fill = Color.valueOf(fill)
                    }
                    longPoll()
                }
            } }
            longPoll()

            primaryStage.show()
        }
    }
}
