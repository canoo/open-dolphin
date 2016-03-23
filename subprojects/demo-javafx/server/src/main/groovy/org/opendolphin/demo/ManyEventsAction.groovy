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

import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log
import groovyx.gpars.dataflow.DataflowQueue

import java.util.concurrent.TimeUnit

@Log
class ManyEventsAction extends DolphinServerAction {

    private EventBus manyEventsBus
    private final DataflowQueue speedQueue = new DataflowQueue()

    private final int EVENT_CONSUMER_WAIT_MS = 5000
    private int waitMillis = EVENT_CONSUMER_WAIT_MS

    ManyEventsAction subscribedTo(EventBus manyEventsBus) {
        this.manyEventsBus = manyEventsBus
        manyEventsBus.subscribe(speedQueue)
        return this
    }

    void registerIn(ActionRegistry registry) {

        registry.register("many.events") { NamedCommand command, response ->

            def atSpeed = serverDolphin.getAt("ManyEvents")?.getAt("speed")
            if (! atSpeed) return
            def atColor = serverDolphin.getAt("ManyEvents")?.getAt("color")
            if (! atColor) return

            def post = speedQueue.getVal(waitMillis, TimeUnit.MILLISECONDS)    // return all values
            while (null != post) {
                def (speed, color) = post
                log.info "got speed notification: $speed"
                atSpeed.value = speed
                atColor.value = color
                post = speedQueue.getVal(20, TimeUnit.MILLISECONDS)
            }
            log.info "returning speed data"
            return response
        }
    }
}
