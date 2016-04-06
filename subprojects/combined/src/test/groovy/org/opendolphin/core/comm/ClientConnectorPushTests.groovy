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
package org.opendolphin.core.comm

import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.DefaultServerDolphin
import spock.lang.Specification

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ClientConnectorPushTests extends Specification {

    volatile TestInMemoryConfig app
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    protected void setup() {
        app = new TestInMemoryConfig()
        serverDolphin = app.serverDolphin
        clientDolphin = app.clientDolphin
        LogConfig.noLogs()
    }

    // make sure the tests only count as ok if context.assertionsDone() has been reached
    protected void cleanup() {
        clientDolphin.sync { app.assertionsDone() }
        assert app.done.await(4, TimeUnit.SECONDS) // max waiting time for async operations to have finished
        clientDolphin.stopPushListening()
    }


    void "listening without push action does not work"() {
        when:
        clientDolphin.startPushListening(null, "ReleaseAction")
        then:
        clientDolphin.isPushListening() == false
    }
    void "listening without release action does not work"() {
        when:
        clientDolphin.startPushListening("PushAction", null)
        then:
        clientDolphin.isPushListening() == false
    }
    void "listening can be started and stopped"() {
        when:
        clientDolphin.startPushListening("PushAction", "ReleaseAction")
        then:
        clientDolphin.isPushListening()
        when:
        clientDolphin.stopPushListening()
        then:
        clientDolphin.isPushListening() == false
    }

    void "core push: server-side commands are immediately processed when listening"() {
        given:
        CountDownLatch pushWasCalled = new CountDownLatch(1)
        serverDolphin.action("PushAction") { cmd, resp ->
            pushWasCalled.countDown()
        }
        when:
        clientDolphin.startPushListening("PushAction", "ReleaseAction")
        then:
        pushWasCalled.await(1, TimeUnit.SECONDS)
    }

    void "autorelease: sending any client-side command releases the read lock"() {
        given:
        CountDownLatch spoofCounter  = new CountDownLatch(3)
        DataflowQueue  readQueue     = new DataflowQueue()
        EventBus bus = new EventBus()
        bus.subscribe(readQueue)
        serverDolphin.action("PushAction") { cmd, resp ->
            def value = readQueue.getVal(3, TimeUnit.SECONDS)
            if (value) spoofCounter.countDown()
        }
        serverDolphin.action("ReleaseAction") { cmd, resp ->
            bus.publish(null, true)
            spoofCounter.countDown()
        }
        when:
        clientDolphin.startPushListening("PushAction", "ReleaseAction")
        clientDolphin.send "JustSomeCommandToTriggerTheRelease", {
            spoofCounter.countDown()
        }
        then:
        spoofCounter.await(1, TimeUnit.SECONDS)
    }

}