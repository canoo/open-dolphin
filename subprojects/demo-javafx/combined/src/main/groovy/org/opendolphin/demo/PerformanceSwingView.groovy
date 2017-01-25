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

import org.opendolphin.LogConfig
import org.opendolphin.core.client.comm.InMemoryClientConnector
import org.opendolphin.core.client.ClientDolphin
import groovy.swing.SwingBuilder

import javax.swing.BorderFactory
import javax.swing.WindowConstants

import static org.opendolphin.binding.Binder.bind
import static org.opendolphin.core.ModelStoreEvent.Type.ADDED
import static org.opendolphin.core.ModelStoreEvent.Type.REMOVED

/**
 * Measuring the response time when requesting so-many presentation models
 * from the server.
 */

class PerformanceSwingView {

    static show(ClientDolphin dolphin) {

        def input = dolphin.presentationModel "input", count:0, attCount:0, time:0

        SwingBuilder builder = new SwingBuilder()
        builder.build {
            frame title: 'Measure Dolphin Response Times', pack:true, visible:true, size:[400,250], location:[100,100], defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE, {
                panel border:BorderFactory.createEmptyBorder(10,10,10,10),  {
                    gridLayout(rows:8, cols:2, vgap: 8, hgap: 5)

                    label "Number of PMs"
                    textField id:'number', text:'1'

                    label "Number of Attributes"
                    textField id:'attCount',      text:'1'

                    label ""
                    hbox {
                        button 'Request',id:'request'
                        button 'Clear',  id:'clear'
                    }

                    label "Last request (ms)"
                    textField id:'time'

                    label ""
                    checkBox 'Show logs', id:'doLog', selected:true

                    label "Connector sleep (ms)"
                    textField id:'conSleep',      text:getSleepMillis(dolphin)

                    label "PMs in store"
                    label id:'store',     text:0

                    label "Memory (MB)"
                    label id:'mem',    text: memString

                }
            }

            bind 'time' of input to 'text' of time

            dolphin.addModelStoreListener { event ->
                if (event.type == ADDED)   store.text = store.text.toInteger() + 1
                if (event.type == REMOVED) store.text = store.text.toInteger() - 1
                mem.text = memString
            }

            doLog.actionPerformed = {
                if (doLog.selected) {
                    LogConfig.logCommunication()
                } else {
                    LogConfig.noLogs()
                }
            }
            conSleep.actionPerformed = {
                if (dolphin.clientConnector instanceof InMemoryClientConnector) dolphin.clientConnector.sleepMillis = conSleep.text.toInteger()
            }

			request.actionPerformed = {
                request.enabled = false
                input.count.value = number.text
                input.attCount.value = attCount.text
                long start = System.nanoTime()
                dolphin.send "stressTest", { pms ->
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    request.enabled = true
                }

			}
			clear.actionPerformed = {
                clear.enabled = false
                long start = System.nanoTime()
                def all = dolphin.findAllPresentationModelsByType('all')
                def temp = new LinkedList(all)
                for (pm in temp) { dolphin.delete(pm) }
                dolphin.sync {
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    clear.enabled = true
                }
			}
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
