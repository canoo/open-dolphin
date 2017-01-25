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

import groovyx.javafx.SceneGraphBuilder
import javafx.scene.Scene
import javafx.scene.layout.GridPane
import javafx.stage.Stage

import static javafx.geometry.HPos.LEFT
import static javafx.geometry.HPos.RIGHT
import static javafx.scene.layout.Priority.ALWAYS

class DemoStyle {

    static blueStyle(SceneGraphBuilder sgb){
        sgb.with {
            primaryStage.scene.fill = radialGradient(stops: [groovyblue.brighter(), groovyblue.darker()]).build()
            primaryStage.scene.stylesheets << 'demo.css'
        }
    }

    static style(SceneGraphBuilder sgb) {
        blueStyle(sgb)
        Stage frame = sgb.primaryStage
        Scene scene = frame.scene

        GridPane grid = scene.root
        grid.styleClass << 'form'
        grid.columnConstraints << sgb.columnConstraints(halignment: RIGHT, hgrow: ALWAYS)
        grid.columnConstraints << sgb.columnConstraints(halignment: LEFT,  hgrow: ALWAYS)

        sgb.translateTransition(1.s, node: grid, fromY: -100, toY: 0).play()
    }
}
