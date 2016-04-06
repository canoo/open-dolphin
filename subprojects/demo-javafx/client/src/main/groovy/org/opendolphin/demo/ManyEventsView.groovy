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

import org.opendolphin.core.client.ClientDolphin
import javafx.scene.paint.Color
import jfxtras.labs.scene.control.gauge.Radial
import jfxtras.labs.scene.control.gauge.StyleModel

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start
import static jfxtras.labs.scene.control.gauge.Gauge.FrameDesign.CHROME

/**
 * After the server has been started, use multiple instances of this view to visualize
 * the server-side events.
 */

class ManyEventsView {

    static show(ClientDolphin dolphin) {

        start { app ->
            def gauge = new Radial(
                styleModel: new StyleModel(frameDesign: CHROME),
                title: "events",
                histogramCreationEnabled: true,
                histogramVisible: true,
                ledVisible: false,
                lcdVisible: false,
                prefWidth: 200, prefHeight: 200
            )
            gauge.gaugeModel.valueAnimationEnabled = false

            stage title: "Many events demo", {
                scene width: 220, height: 220, {
                    borderPane {
                        center margin: 10, { node gauge }
                    }
                }
            }
            blueStyle delegate

            def event = dolphin.presentationModel 'ManyEvents', speed: 1, color: 0

            bind 'speed' of event to FX.VALUE of gauge
            bind 'color' of event to 'histogramColor' of gauge, { it % 2 == 0 ? Color.ORANGERED : Color.GREENYELLOW }

            // for read-only clients, we don't need release actions
            dolphin.startPushListening("many.events", "there.is.no.release.action.needed")

            primaryStage.show()
        }
    }
}
