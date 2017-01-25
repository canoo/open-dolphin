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
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Functional tests for the server-side state changes.
 */

class ServerPresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    DefaultServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
        LogConfig.noLogs()
    }

    @Override
    protected void tearDown() {
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void testServerModelStoreAcceptsConfig() {
        new ServerModelStore(new ModelStoreConfig())
        context.assertionsDone()
    }

    void testServerPresentationModelDoesNotRejectAutoId() {
        // re-enable the shouldFail once we have proper Separation of commands and notifications
//        shouldFail IllegalArgumentException, {
        assert new ServerPresentationModel("1${ServerPresentationModel.AUTO_ID_SUFFIX}", [], new ServerModelStore())
//        }
        context.assertionsDone()
    }

    void testSecondServerActionCanRelyOnAttributeValueChange() {
        def model = clientDolphin.presentationModel("PM1", ["att1"] )

        serverDolphin.action "setValue", { cmd, response ->
            serverDolphin.getAt("PM1").getAt("att1").value = 1
        }

        serverDolphin.action "assertValue", { cmd, response ->
            assert 1 == serverDolphin.getAt("PM1").getAt("att1").value
        }

        clientDolphin.send "setValue"
        clientDolphin.send "assertValue", {
            assert 1 == model.att1.value
            context.assertionsDone()
        }
    }

    void testSecondServerActionCanRelyOnAttributeReset() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.att1.dirty

        serverDolphin.action "reset", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert at.dirty
            at.reset()
            assert ! at.dirty
        }

        serverDolphin.action "assertPristine", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert ! at.dirty
            assert at.value == "base"
        }

        clientDolphin.send "reset"
        clientDolphin.send "assertPristine"

        clientDolphin.sync {
            assert ! model.att1.dirty
            assert model.att1.value == "base"
            context.assertionsDone()
        }
    }

    void testSecondServerActionCanRelyOnAttributeRebase() {
        def model = clientDolphin.presentationModel("PM1", att1:'base', att2:'base')
        model.att1.qualifier = 'qualifier'
        model.att2.qualifier = 'qualifier'

        model.att1.value = 'changed'
        assert model.att1.dirty

        serverDolphin.action "rebase", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert at.dirty
            at.rebase()
            assert ! at.dirty
            assert ! serverDolphin.getAt("PM1").getAt("att2").dirty
        }

        serverDolphin.action "assertNewPristine", { cmd, response ->
            def at = serverDolphin.getAt("PM1").getAt("att1")
            assert ! at.dirty
            assert at.value == "changed"
            assert ! serverDolphin.getAt("PM1").getAt("att2").dirty
        }

        clientDolphin.send "rebase"
        clientDolphin.send "assertNewPristine"

        clientDolphin.sync {
            assert ! model.att1.dirty
            assert ! model.att2.dirty
            assert model.att1.value == "changed"
            assert model.att2.value == "changed"
            context.assertionsDone()
        }
    }

    void testServerSideValueChangesUseQualifiers() {
        def model = clientDolphin.presentationModel("PM1", att1:'base', att2:'base')
        model.att1.qualifier = 'qualifier'
        model.att2.qualifier = 'qualifier'

        serverDolphin.action "changeValue", { cmd, response ->
            def at1 = serverDolphin.getAt("PM1").getAt("att1")
            assert at1.value == 'base'
            at1.value = 'changed'
            assert serverDolphin.getAt("PM1").getAt("att2").value == 'changed'
        }

        serverDolphin.action "changeBaseValue", { cmd, response ->
            def at1 = serverDolphin.getAt("PM1").getAt("att1")
            assert at1.baseValue == 'base'
            at1.baseValue = 'changedBase'
            assert serverDolphin.getAt("PM1").getAt("att2").baseValue == 'changedBase'
        }

        clientDolphin.send "changeValue"
        clientDolphin.send "changeBaseValue"

        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testServerSideEventListenerCanChangeSelfValue() {
        def model = clientDolphin.presentationModel("PM1", att1:'base')

        serverDolphin.action "attachListener", { cmd, response ->
            ServerAttribute at1 = serverDolphin.getAt("PM1").getAt("att1")
            at1.addPropertyChangeListener("value") { event ->
                at1.setValue("changed from PCL")
            }
        }

        serverDolphin.action "changeBaseValue", { cmd, response ->
            def at1 = serverDolphin.getAt("PM1").getAt("att1")
            assert at1.baseValue == 'base'
            at1.baseValue = 'changedBase'
            assert serverDolphin.getAt("PM1").getAt("att2").baseValue == 'changedBase'
        }

        clientDolphin.send "attachListener"

        clientDolphin.sync {
            model.getAt("att1").setValue("changed")
            clientDolphin.sync {
                assert model.getAt("att1").getValue() == "changed from PCL"
                context.assertionsDone()
            }
        }
    }


    void testServerSideEventListenerCanRebaseSelf() {
        def model = clientDolphin.presentationModel("PM1", att1:'base')

        serverDolphin.action "attachListener", { cmd, response ->
            ServerAttribute at1 = serverDolphin.getAt("PM1").getAt("att1")
            at1.addPropertyChangeListener("value") { event ->
                at1.setValue("anotherChange")
                at1.rebase()
            }
        }

        serverDolphin.action "assertRebased", { cmd, response ->
            def at1 = serverDolphin.getAt("PM1").getAt("att1")
            assert at1.baseValue == 'anotherChange'
            assert at1.value == 'anotherChange'
            assert at1.dirty == false
        }

        clientDolphin.send "attachListener"

        clientDolphin.sync {
            model.getAt("att1").setValue("changed")
            assert model.getAt("att1").dirty
            clientDolphin.send "assertRebased", {
                def at1 =  model.getAt("att1")
                assert at1.baseValue == 'anotherChange'
                assert at1.value == 'anotherChange'
                assert at1.dirty == false
                context.assertionsDone()
            }
        }
    }

    void testSecondServerActionCanRelyOnPmReset() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.dirty

        serverDolphin.action "reset", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert pm.dirty
            pm.reset()
            assert ! pm.dirty
        }

        serverDolphin.action "assertPristine", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert ! pm.dirty
            assert pm.att1.value == "base"
        }

        clientDolphin.send "reset"
        clientDolphin.send "assertPristine"

        clientDolphin.sync {
            assert ! model.dirty
            assert model.att1.value == "base"
            context.assertionsDone()
        }
    }
    void testSecondServerActionCanRelyOnPmRebase() {
        def model = clientDolphin.presentationModel("PM1", att1:'base' )
        model.att1.value = 'changed'
        assert model.dirty

        serverDolphin.action "rebase", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert pm.dirty
            pm.rebase()
            assert ! pm.dirty
            DefaultServerDolphin.rebase(null, (ServerAttribute) null) // throws no exception but logs and returns
        }

        serverDolphin.action "assertNewPristine", { cmd, response ->
            def pm = serverDolphin.getAt("PM1")
            assert ! pm.dirty
            assert pm.att1.baseValue == "changed"
        }

        clientDolphin.send "rebase"
        clientDolphin.send "assertNewPristine"

        clientDolphin.sync {
            assert ! model.dirty
            assert model.att1.baseValue == "changed"
            context.assertionsDone()
        }
    }

    void testSecondServerActionCanRelyOnPmCreate() {

        def pmWithNullId

        serverDolphin.action "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            def pm = serverDolphin.presentationModel("PM1", null, dto)
            pmWithNullId = serverDolphin.presentationModel(null, "pmType", dto)
            assert pm
            assert pmWithNullId
            assert serverDolphin.getAt("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        serverDolphin.action "assertVisible", { cmd, response ->
            assert serverDolphin.getAt("PM1")
            assert serverDolphin.findAllPresentationModelsByType("pmType").first() == pmWithNullId
        }

        clientDolphin.send "create"
        clientDolphin.send "assertVisible"

        clientDolphin.sync {
            assert clientDolphin.getAt("PM1")
            assert clientDolphin.findAllPresentationModelsByType("pmType").size() == 1
            println clientDolphin.findAllPresentationModelsByType("pmType").first().id
            context.assertionsDone()
        }
    }

    void testServerCreatedAttributeChangesValueOnTheClientSide() {

        AtomicBoolean pclReached = new AtomicBoolean(false)

        serverDolphin.action "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.presentationModel("PM1", null, dto)
            serverDolphin.getAt("PM1").getAt("att1").addPropertyChangeListener("value",{ pclReached.set(true) })
        }

        serverDolphin.action "assertValueChange", { cmd, response ->
            assert pclReached.get()
            assert serverDolphin.getAt("PM1").getAt("att1").value == 2
            context.assertionsDone()
        }

        clientDolphin.send "create", {
            assert clientDolphin.getAt("PM1").getAt("att1").value == 1
            clientDolphin.getAt("PM1").getAt("att1").value = 2

            clientDolphin.send "assertValueChange"
        }

    }

    void testServerSideModelStoreListener() {

        DataflowQueue<CreatePresentationModelCommand> receivedCommands = new DataflowQueue<>()
        DataflowQueue<ModelStoreEvent> receivedEvents   = new DataflowQueue<>()

        serverDolphin.action "registerMSL", { cmd, response ->
            serverDolphin.addModelStoreListener(new ModelStoreListener() {
                @Override void modelStoreChanged(ModelStoreEvent event) {
                    receivedEvents << event
                }
            })
        }

        serverDolphin.register(new DolphinServerAction() {
            @Override
            void registerIn(ActionRegistry registry) {
                registry.register(CreatePresentationModelCommand) { cmd, resp ->
                    receivedCommands << cmd
                }
            }
        })

        serverDolphin.action "create", { cmd, response ->
            def dto = new DTO(new Slot("att1", 1))
            serverDolphin.presentationModel("server-side-with-id", null, dto)
            // will lead to log: [INFO] There already is a PM with id server-side-with-id. Create PM ignored.
            serverDolphin.presentationModel(null, "server-side-without-id", dto)
            // will lead to log: [INFO] Cannot create PM '0-AUTO-SRV' with forbidden suffix. Create PM ignored.
            // at this point, the MSL has been triggered
        }

        clientDolphin.send "registerMSL"

        clientDolphin.presentationModel("client-side-with-id", null, attr1:1)

        clientDolphin.send "create", {
            assert receivedCommands.val.pmId == "client-side-with-id"
            assert receivedCommands.val.pmId == "server-side-with-id"
            assert receivedCommands.val.pmId == "0-AUTO-SRV"
            assert receivedEvents.val.presentationModel.id == "client-side-with-id"
            assert receivedEvents.val.presentationModel.id == "server-side-with-id"
            assert receivedEvents.val.presentationModel.id == "0-AUTO-SRV"
            context.assertionsDone()
        }
    }

    void testServerSidePmRemoval() {

        clientDolphin.presentationModel("client-side-with-id", null, attr1:1)

        serverDolphin.action "remove", { cmd, response ->
            def pm = serverDolphin.getAt("client-side-with-id")
            assert pm
            serverDolphin.remove(pm)
            assert null == serverDolphin.getAt("client-side-with-id") // immediately removed on server
        }

        assert clientDolphin.getAt("client-side-with-id")

        clientDolphin.send "remove", {
            assert null == clientDolphin.getAt("client-side-with-id") // removed from client before callback
            context.assertionsDone()
        }

    }

    void testServerSideAllPmRemoval() {

        clientDolphin.presentationModel(null, "client-side-type", attr1:1)
        clientDolphin.presentationModel(null, "client-side-type", attr1:1)

        serverDolphin.action "remove", { cmd, response ->
            assert serverDolphin.findAllPresentationModelsByType("client-side-type").size() == 2
            serverDolphin.removeAllPresentationModelsOfType("client-side-type")
            // immediately removed on server
            assert serverDolphin.findAllPresentationModelsByType("client-side-type").size() == 0
        }

        assert clientDolphin.findAllPresentationModelsByType("client-side-type").size() == 2

        clientDolphin.send "remove", {
            // removed from client before callback
            assert clientDolphin.findAllPresentationModelsByType("client-side-type").size() == 0
            context.assertionsDone()
        }
    }

    void testServerSideSwitch() {

        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")
        def target = clientDolphin.presentationModel("target", null, attr1:"targetValue")

        source.getAt("attr1").qualifier = "source.qualifier"

        assert source.getAt("attr1").value     != target.getAt("attr1").value
        assert source.getAt("attr1").baseValue != target.getAt("attr1").baseValue
        assert source.getAt("attr1").qualifier != target.getAt("attr1").qualifier

        serverDolphin.action "switch", { cmd, response ->
            def sourcePM = serverDolphin.getAt("source")
            def targetPM = serverDolphin.getAt("target")
            targetPM.syncWith(sourcePM)
            // immediately applied on server
            assert targetPM.getAt("attr1").value     == sourcePM.getAt("attr1").value
            assert targetPM.getAt("attr1").baseValue == sourcePM.getAt("attr1").baseValue
            assert targetPM.getAt("attr1").qualifier == sourcePM.getAt("attr1").qualifier
        }

        clientDolphin.send "switch", {
            // synced on client before callback
            assert source.getAt("attr1").value     == target.getAt("attr1").value
            assert source.getAt("attr1").baseValue == target.getAt("attr1").baseValue
            assert source.getAt("attr1").qualifier == target.getAt("attr1").qualifier
            context.assertionsDone()
        }
    }


    void testServerSideBaseValueChange() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        source.getAt("attr1").baseValue = "sourceValue"

        serverDolphin.action "changeBaseValue", { cmd, response ->
            def attribute = serverDolphin.getAt("source").getAt("attr1")
            attribute.baseValue = "changed"
            // immediately applied on server
            assert attribute.baseValue == "changed"
        }

        clientDolphin.send "changeBaseValue", {
            assert source.getAt("attr1").value     == "sourceValue"
            assert source.getAt("attr1").baseValue == "changed"
            context.assertionsDone()
        }
    }

    void testServerSideQualifierChange() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        source.getAt("attr1").qualifier = "qualifier"

        serverDolphin.action "changeBaseValue", { cmd, response ->
            def attribute = serverDolphin.getAt("source").getAt("attr1")
            attribute.qualifier = "changed"
            // immediately applied on server
            assert attribute.qualifier == "changed"
        }

        clientDolphin.send "changeBaseValue", {
            assert source.getAt("attr1").value     == "sourceValue"
            assert source.getAt("attr1").qualifier == "changed"
            context.assertionsDone()
        }
    }

    void testServerSideAddingOfAttributesToAnExistingModel() {
        def source = clientDolphin.presentationModel("source", null, attr1:"sourceValue")

        serverDolphin.action "addAttribute", { cmd, response ->
            def pm = serverDolphin.getAt("source")
            def attr2 = new ServerAttribute("attr2","initial")
            pm.addAttribute(attr2)
            // immediately applied on server
            assert pm.getAt("attr2").value == "initial"
        }

        clientDolphin.send "addAttribute", {
            assert source.getAt("attr2").value == "initial"
            context.assertionsDone()
        }
    }

    // todo dk: think about these use cases:
    // dolphin.copy(pm) on client and server (done) - todo: make js version use the same approach
    // server-side tagging

}