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
import org.opendolphin.core.client.GClientDolphin
import groovyx.javafx.SceneGraphBuilder
import javafx.event.EventHandler

import static org.opendolphin.binding.JFXBinder.bind
import static groovyx.javafx.GroovyFX.start

/**
 * This demo show how to mark objects (applying a color) depending on a choice of categories (size or weight)
 * without server roundtrip, i.e. the client knows about the possible categories and the value dependencies.
 * The trick is to call the mapping logic in the conversion logic of the binding.
 * How to use: select "size" as category, click on left rectangle to increase its size and observe the color change.
 */

class CategoryChangeView {

    static show(ClientDolphin dolphin) {

        def categories = [
                weight : [
                        (0..5)  : 'yellow',
                        (6..10) : 'red'
                ],
                size : [
                        20      : 'blue',
                        40      : 'green',
                      {it > 40} : 'black'
                ]
        ]

        def select   = dolphin.presentationModel 'select',   category: null
        def firstPm  = dolphin.presentationModel 'firstPm',  weight: 3, size:20
        def secondPm = dolphin.presentationModel 'secondPm', weight:10, size:40


        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 400, height: 300, {
                    borderPane {
                        top margin:10, {
                            choiceBox id:'choice', items:['weight','size'], {
                                onSelect { control, item -> select.category.value = item }
                            }
                        }
                        stackPane {
                            group effect: dropShadow(offsetY: 2, offsetX: 2, radius: 3, input: lighting{distant(azimuth: -135.0)}), {
                                rectangle(id: 'first',  x: 0,  y: 0, width: 40, height: 40, fill: white)
                                rectangle(id: 'second', x: 60, y: 0, width: 40, height: 40, fill: white)
            }   }   }   }   }


            def mapper = { pm, cat ->
                if (null == cat) return sgb.white
                def cases = categories[cat]
                if (null == cases) return sgb.white
                def entry = cases.find{ k,v -> pm[cat].value in k }
                if (null == entry) return sgb.white
                return sgb[entry.value]
            }
            bind 'category' of select to 'fill' of first , mapper.curry(firstPm)            // alternative 1
            bind 'category' of select to 'fill' of second, { cat -> mapper(secondPm, cat) } // alternative 2

            bind 'size' of firstPm to 'width' of first, { size ->
                if (select.category?.value == 'size') first.fill = mapper(firstPm,'size')
                return size
            }

            first.onMouseClicked = { firstPm.size.value += 10 } as EventHandler

            primaryStage.show()
        }
    }
}
