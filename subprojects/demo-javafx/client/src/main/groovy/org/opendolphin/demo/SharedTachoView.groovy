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

import jfxtras.labs.scene.control.gauge.ColorDef
import jfxtras.labs.scene.control.gauge.Gauge
import org.opendolphin.core.client.ClientDolphin
import jfxtras.labs.scene.control.gauge.Radial
import jfxtras.labs.scene.control.gauge.StyleModel

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start
import static jfxtras.labs.scene.control.gauge.Gauge.FrameDesign.CHROME

/**
 * This demo shows how to use the publish-subscribe pattern with the help of the Dolphin
 * event bus. Start multiple instances of it.
 * You can change the speed with the slider in any started instance.
 * Observe how changing the speed updates all views quasi-instantly.
 * The slider is an input control and a view of the current speed at the same time.
 * This may lead to a conflict when the speed is concurrently set in two instance to different values.
 * The latest change will win and the read-only gauge will always show the winning value.
 */

class SharedTachoView {

    static show(ClientDolphin readDolphin, ClientDolphin writeDolphin) {

        start { app ->
            def gauge = new Radial(
                styleModel: new StyleModel(frameDesign: CHROME, pointerType: Gauge.PointerType.TYPE4),
                title: "km/h",
                prefWidth: 250, prefHeight: 250,
                valueAnimationEnabled: false,
                effect: dropShadow(radius: 20, color: rgba(0, 0, 0, 0.4))
            )

            stage title: "Dolphin shared tacho demo", {
                scene width: 400, height: 400, {
                    borderPane {
                        center margin: 10, { node gauge }
                        bottom margin: 40, { slider id: 'slider' }
                    }
                }
            }
            blueStyle delegate

            def readCar = readDolphin.presentationModel 'Train', speed: 0
            readCar.speed.qualifier = "train.speed"

            def writeCar = writeDolphin.presentationModel 'Train', speed: 0
            writeCar.speed.qualifier = "train.speed.input"

            bind 'speed' of readCar  to FX.VALUE of gauge
            bind 'speed' of readCar  to FX.VALUE of slider, { slider.pressed ? slider.value : readCar.speed.value }

            bind FX.VALUE of slider to 'speed' of writeCar, { it.toInteger() }

            Closure longPoll
            longPoll = { readDolphin.send "poll.train.speed", longPoll }
            longPoll()

            primaryStage.show()
        }
    }
}
