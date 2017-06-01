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

package org.opendolphin.core.comm

import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.LogConfig
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.*
import org.opendolphin.core.server.*
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Level



class StallingPushIssueTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        //LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        clientDolphin.stopPushListening()
        assert context.done.await(20, TimeUnit.SECONDS)
    }

    // did we send enough releases at the right time such that the communication is not stalled?
    void testStallingIssue() {
        clientDolphin.clientConnector.sleepMillis = 100 // this is needed or the stalling does not happen

        def bus = new EventBus()
        def queue = new DataflowQueue()
        bus.subscribe queue

        serverDolphin.action "onPush", { cmd, response ->
            queue.val // in reality, one should never read without a max waiting time!
        }
        serverDolphin.action "onRelease", { cmd, response ->
            bus.publish null, "xxx" // any value will release the waiting onPush
        }

        def pm = clientDolphin.presentationModel("pm", null, attr: -1)
        clientDolphin.startPushListening("onPush", "onRelease")

        // we send a hundred value changes (no batching)
        // each "request" for sending a value change must trigger a release,
        // publishing on the bus, releasing the onPush (which would otherwise stall forever)
        100.times { pm.attr.value = it }

        clientDolphin.sync {
            clientDolphin.stopPushListening()
            context.assertionsDone()
        }
    }


}