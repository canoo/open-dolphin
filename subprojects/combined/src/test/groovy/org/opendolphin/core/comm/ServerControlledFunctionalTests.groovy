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

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.comm.RunLaterUiThreadHandler
import org.opendolphin.core.client.comm.SynchronousUiThreadHandler
import org.opendolphin.core.server.*

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.concurrent.TimeUnit

/**
 * Functional tests for scenarios that customers observed when controlling
 * the application purely from server side.
 */

class ServerControlledFunctionalTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    volatile DefaultServerDolphin serverDolphin
    volatile ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
//        clientDolphin.clientConnector.strictMode = true // true is default
//        clientDolphin.clientConnector.sleepMillis = 1
//        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void nonStrict() {
        clientDolphin.clientConnector.strictMode = false
    }

    void testPMsWereDeletedAndRecreatedNonStrict() {
        nonStrict()
        testPMsWereDeletedAndRecreated()
    }
    void testPMsWereDeletedAndRecreated() {
        // a pm created on the client side
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 0 ))

        // register a server-side action that sees the second PM
        serverDolphin.action("checkPmIsThere") { cmd, list ->
            assert serverDolphin.getAt("pm1").a.value == 1
            assert clientDolphin.getAt("pm1").a.value == 1 // well, maybe not a good idea...
            context.assertionsDone()
        }

        assert clientDolphin.getAt("pm1").a.value == 0
        clientDolphin.delete(clientDolphin.getAt("pm1"))
        clientDolphin.presentationModel("pm1", new ClientAttribute("a", 1 ))
        clientDolphin.send("checkPmIsThere")
    }


    void testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServerNonStrict() {
        nonStrict()
        testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServer()
    }
    void testPMsWereCreatedOnServerSideDeletedByTypeRecreatedOnServer() { // the "Baerbel" problem
        serverDolphin.action("createPM") { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
        }
        serverDolphin.action("deleteAndRecreate") { cmd, list ->
            serverDolphin.removeAllPresentationModelsOfType("myType") // delete
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0))) // recreate
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',1))) // recreate

            assert serverDolphin.findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.findAllPresentationModelsByType("myType")[0].a.value == 0
            assert serverDolphin.findAllPresentationModelsByType("myType")[1].a.value == 1
        }

        serverDolphin.action("assertRetainedServerState") { cmd, list ->
            assert serverDolphin.findAllPresentationModelsByType("myType").size() == 2
            assert serverDolphin.findAllPresentationModelsByType("myType")[0].a.value == 0
            assert serverDolphin.findAllPresentationModelsByType("myType")[1].a.value == 1
        }

        clientDolphin.send("createPM"){
            assert clientDolphin.findAllPresentationModelsByType("myType").size() == 1
            assert clientDolphin.findAllPresentationModelsByType("myType").first().a.value == 0
        }
        clientDolphin.send("deleteAndRecreate") {
            assert clientDolphin.findAllPresentationModelsByType("myType").size() == 2
            assert clientDolphin.findAllPresentationModelsByType("myType")[0].a.value == 0
            assert clientDolphin.findAllPresentationModelsByType("myType")[1].a.value == 1
        }
        clientDolphin.send("assertRetainedServerState") {
            context.assertionsDone()
        }
    }

    void testValueChangesInOnFinishedHandlerAreSentToTheServerNonStrict() {
        nonStrict()
        testValueChangesInOnFinishedHandlerAreSentToTheServer()
    }
    void testValueChangesInOnFinishedHandlerAreSentToTheServer() {
        def oldValue = 0
        def newValue = 1
        assert oldValue != newValue // sanity check

        // a pm created on the client side
        clientDolphin.presentationModel("pm", new ClientAttribute("a", oldValue ))

        serverDolphin.action("checkPM") { cmd, list ->
            assert serverDolphin.getAt("pm").a.value == newValue
        }
        clientDolphin.sync { // using sync's EmptyCommand as some arbitrary command
            // a value change in response to a server command
            clientDolphin.getAt("pm").a.value = newValue

            clientDolphin.send("checkPM") {
                context.assertionsDone()
            }
        }
    }

    void testChangeValueMultipleTimesAndBackToBaseNonStrict() {
        nonStrict()
        testChangeValueMultipleTimesAndBackToBase()
    }
    void testChangeValueMultipleTimesAndBackToBase() { // Alex issue
        // register a server-side action that creates a PM
        serverDolphin.action("createPM") { cmd, list ->
            serverDolphin.presentationModel("myPm", null, new DTO(new Slot('a',0)))
        }
        serverDolphin.action("changeValueMultipleTimesAndBackToBase") { cmd, list ->
            def myPm = serverDolphin.getAt("myPm")
            myPm.a.value = 1
            myPm.a.value = 2
            assert myPm.a.dirty
            myPm.a.value = 0
            assert ! myPm.a.dirty
        }

        clientDolphin.send("createPM")
        clientDolphin.send("changeValueMultipleTimesAndBackToBase") {
            def myPm = clientDolphin.getAt("myPm")
            assert myPm.a.value == 0
            assert ! myPm.a.dirty
            context.assertionsDone()
        }
    }

    void testServerSideRemoveNonStrict() {
        nonStrict()
        testServerSideRemove()
    }
    void testServerSideRemove() {
        serverDolphin.action("createPM") { cmd, list ->
            serverDolphin.presentationModel("myPm", null, new DTO(new Slot('a',0)))
        }
        serverDolphin.action("remove") { cmd, list ->
            def myPm = serverDolphin.getAt("myPm")
            serverDolphin.remove(myPm)
            assert null == serverDolphin.getAt("myPm")
        }

        clientDolphin.send("createPM"){
            assert clientDolphin.getAt("myPm")
        }
        clientDolphin.send("remove") {
            assert null == clientDolphin.getAt("myPm")
            context.assertionsDone()
        }
    }

    void testServerSideSetAndUnsetQualifierNonStrict() {
        nonStrict()
        testServerSideSetAndUnsetQualifier()
    }
    void testServerSideSetAndUnsetQualifier() {
        serverDolphin.action("createPM") { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
        }
        serverDolphin.action("setAndUnsetQualifier") { cmd, list ->
            def myPm = serverDolphin.findAllPresentationModelsByType("myType").first()
            myPm.a.qualifier = "myQualifier"
            myPm.a.qualifier = "othervalue"
        }

        clientDolphin.send("createPM"){
            def pm = clientDolphin.findAllPresentationModelsByType("myType").first()
            pm.getAt('a').addPropertyChangeListener("qualifier", new PropertyChangeListener() {
                @Override
                void propertyChange(PropertyChangeEvent evt) { // assume a client side listener
                    pm.getAt('a').qualifier="myQualifier"
                }
            })
        }
        clientDolphin.send("setAndUnsetQualifier") {
            assert "myQualifier" == clientDolphin.findAllPresentationModelsByType("myType").first().a.qualifier
            context.assertionsDone()
        }
    }

    void testServerSideSetQualifierPlusServerSideApplyNonStrict() {
        nonStrict()
        testServerSideSetQualifierPlusServerSideApply()
    }
    void testServerSideSetQualifierPlusServerSideApply() {
        serverDolphin.action("createPM1") { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
            serverDolphin.presentationModel("target1", null, new DTO(new Slot('a',1)))
        }
        serverDolphin.action("setQualifier") { cmd, list ->
            def myPm = serverDolphin.findAllPresentationModelsByType("myType").first()
            myPm.a.qualifier = "myQualifier"
        }
        serverDolphin.action("apply") { cmd, list ->
            def source = serverDolphin.findAllPresentationModelsByType("myType").first()
            def target = serverDolphin.getAt("target1")
            target.syncWith(source)
            assert target.a.value == 0
            assert target.a.qualifier == "myQualifier"
        }

        clientDolphin.send("createPM1"){
            assert clientDolphin.findAllPresentationModelsByType("myType").first()
        }
        clientDolphin.send("setQualifier") {
            assert clientDolphin.findAllPresentationModelsByType("myType").first().a.qualifier == "myQualifier"
        }
        clientDolphin.send("apply") {
            assert clientDolphin.getAt("target1").a.value == 0
            assert clientDolphin.getAt("target1").a.qualifier == "myQualifier"
            context.assertionsDone()
        }
    }


    void testServerSideRebaseNonStrict() {
        nonStrict()
        testServerSideRebase()
    }
    void testServerSideRebase() {
        serverDolphin.action("createPM") { cmd, list ->
            serverDolphin.presentationModel(null, "myType", new DTO(new Slot('a',0)))
        }
        serverDolphin.action("rebase") { cmd, list ->
            def myPm = serverDolphin.findAllPresentationModelsByType("myType").first()
            myPm.a.value = 1
            myPm.rebase()
        }

        clientDolphin.send("createPM")
        clientDolphin.send("rebase") {
            def pm = clientDolphin.findAllPresentationModelsByType("myType").first()
            assert ! pm.dirty
            assert pm.a.value == 1
            assert pm.a.baseValue == 1
            context.assertionsDone()
        }
    }

}