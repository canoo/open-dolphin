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

import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientDolphin
import org.opendolphin.core.client.comm.InMemoryClientConnector
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener

import static org.opendolphin.binding.Binder.bind
import static org.opendolphin.core.ModelStoreEvent.Type.ADDED
import static org.opendolphin.core.ModelStoreEvent.Type.REMOVED
import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.demo.DemoStyle.style

/**
 * Measuring the response time when requesting so-many presentation models
 * from the server.
 */

class PerformanceView {

    static show(ClientDolphin dolphin) {

        def input = dolphin.presentationModel "input", count:0, attCount:0, time:0

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage title: 'Measure Dolphin Response Times', {
                scene width: 400, height: 250, {
                    gridPane {
                        label "Number of PMs", row:0, column:0
                        textField id:'number', row:0, column:1, text:'1'

                        label "Number of Attributes", row:1, column:0
                        textField id:'attCount', row:1, column:1, text:'1'

                        hbox row:2, column:1, spacing:10, {
                            button 'Request',id:'request'
                            button 'Clear',  id:'clear'
                        }

                        label "Last request (ms)", row:3, column:0
                        textField id:'time',       row:3, column:1

                        checkBox 'Show logs', id:'doLog', selected:true, row:4, column:1

                        label "Connector sleep (ms)", row:5, column:0
                        textField id:'conSleep',      row:5, column:1, text:getSleepMillis(dolphin)

                        label "PMs in store", row:6, column:0
                        label id:'store',     row:6, column:1, text:0

                        label "Memory (MB)", row:7, column:0
                        label id:'mem',      row:7, column:1, text: memString

                    }
                }
            }

            bind 'time' of input to 'text' of time

            style sgb

            dolphin.addModelStoreListener { event ->
                if (event.type == ADDED)   store.text = store.text.toInteger() + 1
                if (event.type == REMOVED) store.text = store.text.toInteger() - 1
                mem.text = memString
            }

            doLog.onAction {
                if (doLog.selected) {
                    LogConfig.logCommunication()
                } else {
                    LogConfig.noLogs()
                }
            }
            conSleep.text().addListener ({ obj, old, newVal ->
                if (dolphin.clientConnector instanceof InMemoryClientConnector) dolphin.clientConnector.sleepMillis = conSleep.text.toInteger()
            } as ChangeListener)

			request.onAction {
                request.disable = true
                input.count.value = number.text
                input.attCount.value = attCount.text
                long start = System.nanoTime()
                dolphin.send "stressTest", { pms ->
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    request.disable = false
                }

			}
			clear.onAction {
                clear.disable = true
                long start = System.nanoTime()
                def all = dolphin.findAllPresentationModelsByType('all')
                def temp = new LinkedList(all)
                for (pm in temp) { dolphin.delete(pm) }
                dolphin.sync {
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    clear.disable = false
                }
			}
            primaryStage.show()
        }
    }
    static String getSleepMillis(ClientDolphin dolphin) {
        if ( ! (dolphin.clientConnector instanceof InMemoryClientConnector)) return
        dolphin.clientConnector.sleepMillis
    }

    static String getMemString() {
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).intdiv(1000000).toString()
    }

}
