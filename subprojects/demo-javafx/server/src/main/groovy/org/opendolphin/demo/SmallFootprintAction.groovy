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

import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import java.util.concurrent.TimeUnit

@Log
class SmallFootprintAction extends DolphinServerAction {

    private EventBus smallFootprintBus
    private final DataflowQueue rectQueue = new DataflowQueue()

    SmallFootprintAction subscribedTo(EventBus smallFootprintBus) {
        this.smallFootprintBus = smallFootprintBus
        smallFootprintBus.subscribe(rectQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            def attribute = modelStore.findAttributeById(command.attributeId)
            if (attribute) {
                if (attribute.qualifier == "sfTrigger") {
                    def value = command.newValue
                    smallFootprintBus.publish(rectQueue, value)
                }
            }
        }

        registry.register("poll.sfTrigger") { NamedCommand command, response ->
            def max = 20

            def value = rectQueue.getVal(1, TimeUnit.MINUTES)    // typical long-poll wait
            while (null != value) { // for efficiency read all there is until quiet
                response << new DataCommand(value: value)
                if (! max--) break
                value = rectQueue.getVal(20, TimeUnit.MILLISECONDS) // quiet time
            }
            return response
        }
    }
}
