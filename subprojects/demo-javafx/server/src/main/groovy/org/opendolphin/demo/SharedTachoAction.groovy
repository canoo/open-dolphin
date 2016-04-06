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

import org.opendolphin.core.Tag
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowQueue

import java.util.concurrent.TimeUnit

@Log
class SharedTachoAction extends DolphinServerAction {

    private EventBus tachoBus
    private final DataflowQueue speedQueue = new DataflowQueue()

    SharedTachoAction subscribedTo(EventBus tachoBus) {
        this.tachoBus = tachoBus
        tachoBus.subscribe(speedQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAttributeById(command.attributeId)
            if (attribute) {
                if (attribute.qualifier == "train.speed.input") {
                    def value = command.newValue
                    tachoBus.publish(speedQueue, value)
                    log.info "published train speed $value"
                }
            }
        }

        registry.register("poll.train.speed") { NamedCommand command, response ->

            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAllAttributesByQualifier("train.speed").find{ it.tag == Tag.VALUE }
            if (! attribute) return

            def speed = speedQueue.getVal(1, TimeUnit.MINUTES)    // typical long-poll wait

            def lastSpeed = speed
            while (null != speed) { // for efficiency read all there is until quiet
                lastSpeed = speed
                speed = speedQueue.getVal(10, TimeUnit.MILLISECONDS) // quiet time
            }
            if (null != lastSpeed) {
                log.info "got speed notification: $lastSpeed"
                serverDolphin.changeValueCommand(response, attribute, lastSpeed)
            }

            return response
        }
    }
}
