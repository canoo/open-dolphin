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

package org.opendolphin.core.comm

import org.opendolphin.LogConfig
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.BlindCommandBatcher
import org.opendolphin.core.client.comm.InMemoryClientConnector
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter
import org.opendolphin.core.client.comm.RunLaterUiThreadHandler
import org.opendolphin.core.client.comm.SynchronousInMemoryClientConnector
import org.opendolphin.core.client.comm.UiThreadHandler
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.GServerDolphin
import org.opendolphin.core.server.GServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Showcase for how to test an application without the GUI by
 * issuing the respective commands and model changes against the
 * ClientModelStore
 */

class FunctionalPresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    GServerDolphin serverDolphin
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
        assert context.done.await(10, TimeUnit.SECONDS)
    }

    void testQualifiersInClientPMs() {
        def modelA = clientDolphin.presentationModel("1", new ClientAttribute("a", 0, "QUAL"))
        def modelB = clientDolphin.presentationModel("2", new ClientAttribute("b", 0, "QUAL"))

        modelA.a.value = 1

        assert modelB.b.value == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testValueChangeWithQualifiersInClientSideOnlyPMs() {
        def modelA = new ClientPresentationModel("1", [new ClientAttribute("a", 0, "QUAL")])
        modelA.clientSideOnly = true
        clientDolphin.add modelA

        def modelB = clientDolphin.presentationModel("2", new ClientAttribute("b", 0))
        modelB.clientSideOnly = true
        clientDolphin.addAttributeToModel(modelB, new ClientAttribute("bLate", 0, "QUAL"))

        modelA.a.value = 1

        assert modelB.bLate.value == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testValueRebaseWithQualifiersInClientSideOnlyPMs() {
        def modelA = new ClientPresentationModel("1", [new ClientAttribute("a", 0, "QUAL")])
        modelA.clientSideOnly = true
        clientDolphin.add modelA

        def modelB = clientDolphin.presentationModel("2", new ClientAttribute("b", 0))
        modelB.clientSideOnly = true
        clientDolphin.addAttributeToModel(modelB, new ClientAttribute("bLate", 0, "QUAL"))

        modelA.a.value = 1
        assert modelB.bLate.baseValue == 0
        modelA.a.rebase()

        assert modelA.a.baseValue == 1
        assert modelB.bLate.baseValue == 1
        context.assertionsDone() // make sure the assertions are really executed
    }

    void testPerformanceWithStandardCommandBatcher() {
        doTestPerformance()
    }

    void testPerformanceWithBlindCommandBatcher() {
        def batcher = new BlindCommandBatcher(mergeValueChanges:true, deferMillis: 100)
        def connector = new InMemoryClientConnector(context.clientDolphin, batcher)
        connector.uiThreadHandler = new RunLaterUiThreadHandler()
        connector.serverConnector = serverDolphin.serverConnector
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void testPerformanceWithSynchronousConnector() {
        def connector = new SynchronousInMemoryClientConnector(context.clientDolphin)
        connector.uiThreadHandler = { fail "should not reach here! " } as UiThreadHandler
        connector.serverConnector = serverDolphin.serverConnector
        context.clientDolphin.clientConnector = connector
        doTestPerformance()
    }

    void doTestPerformance() {
        long id = 0
        serverDolphin.action "performance", { cmd, response ->
            100.times { attr ->
                serverDolphin.presentationModel(response, "id_${id++}".toString(), null, new DTO(new Slot("attr_$attr", attr)))
            }
        }
        def start = System.nanoTime()
        100.times { soOften ->
            clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
                assert pms.size() == 100
                pms.each { pm -> clientDolphin.delete(pm) }
            }
        }
        clientDolphin.send "performance", { List<ClientPresentationModel> pms ->
            assert pms.size() == 100
            println ((System.nanoTime() - start).intdiv(1_000_000))
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testCreationRoundtripDefaultBehavior() {
        serverDolphin.action "create", { cmd, response ->
            serverDolphin.presentationModel(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        serverDolphin.action "checkNotificationReached", { cmd, response ->
            assert 1 == serverDolphin.listPresentationModels().size()
            assert serverDolphin.getAt("id")
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert pms.size() == 1
            assert 'attr' == pms.first().getAt("attr").value
            clientDolphin.send "checkNotificationReached", { List<ClientPresentationModel> xxx ->
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testCreationRoundtripForTags() {
        serverDolphin.action "create", { cmd, response ->
            def NO_TYPE = null
            def NO_QUALIFIER = null
            serverDolphin.presentationModel(response, "id".toString(), NO_TYPE, new DTO(new Slot("attr", true, NO_QUALIFIER, Tag.VISIBLE)))
        }
        serverDolphin.action "checkTagIsKnownOnServerSide", { cmd, response ->
            assert serverDolphin.getAt("id").getAt('attr', Tag.VISIBLE)
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert clientDolphin.getAt("id").getAt('attr', Tag.VISIBLE)
            clientDolphin.send "checkTagIsKnownOnServerSide", { List<ClientPresentationModel> xxx ->
                context.assertionsDone()
            }
        }
    }

    void testCreationNoRoundtripWhenClientSideOnly() {
        serverDolphin.action "create", { cmd, response ->
            serverDolphin.clientSideModel(response, "id".toString(), null, new DTO(new Slot("attr", 'attr')))
        }
        serverDolphin.action "checkNotificationReached", { cmd, response ->
            assert 0 == serverDolphin.listPresentationModels().size()
        }

        clientDolphin.send "create", { List<ClientPresentationModel> pms ->
            assert pms.size() == 1
            assert 'attr' == pms.first().getAt("attr").value
            clientDolphin.send "checkNotificationReached", { List<ClientPresentationModel> xxx ->
                context.assertionsDone() // make sure the assertions are really executed
            }
        }
    }

    void testFetchingAnInitialListOfData() {
        serverDolphin.action "fetchData", { cmd, response ->
            ('a'..'z').each {
                DTO dto = new DTO(new Slot('char',it))
                // sending CreatePresentationModelCommand _without_ adding the pm to the server model store
                serverDolphin.presentationModel(response, it, null, dto)
            }
        }
        clientDolphin.send "fetchData", { List<ClientPresentationModel> pms ->
            assert pms.size() == 26
            assert pms.collect { it.id }.sort(false) == pms.collect { it.id }   // pmIds from a single action should come in sequence
            assert 'a' == context.clientDolphin.findPresentationModelById('a').char.value
            assert 'z' == context.clientDolphin.findPresentationModelById('z').char.value
            context.assertionsDone() // make sure the assertions are really executed
        }
    }

    void testLoginUseCase() {
        serverDolphin.action "loginCmd", { cmd, response ->
            def user = context.serverDolphin.findPresentationModelById('user')
            if (user.name.value == 'Dierk' && user.password.value == 'Koenig') {
                GServerDolphin.changeValue(response, user.loggedIn, 'true')
            }
        }
        def user = clientDolphin.presentationModel 'user', name: null, password: null, loggedIn: null
        clientDolphin.send "loginCmd", {
            assert !user.loggedIn.value
        }
        user.name.value = "Dierk"
        user.password.value = "Koenig"

        clientDolphin.send "loginCmd", {
            assert user.loggedIn.value
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionOnTheServer() {
        LogConfig.logCommunication()
        def count = 0
        clientDolphin.clientConnector.onException = { count++ }

        serverDolphin.action "someCmd", { cmd, response ->
            throw new RuntimeException("EXPECTED: some arbitrary exception on the server")
        }

        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached in this case"
        }
        clientDolphin.sync {
            assert count == 1
        }

        // provoke a second exception
        clientDolphin.send "someCmd", {
            fail "the onFinished handler will not be reached either"
        }
        clientDolphin.sync {
            assert count == 2
        }
        clientDolphin.sync {
            context.assertionsDone()
        }
    }

    void testAsynchronousExceptionInOnFinishedHandler() {

        clientDolphin.clientConnector.uiThreadHandler = { it() } as UiThreadHandler // not "run later" we need it immediately here
        clientDolphin.clientConnector.onException = { context.assertionsDone() }

        serverDolphin.action "someCmd", { cmd, response ->
            // nothing to do
        }
        clientDolphin.send "someCmd", {
            throw new RuntimeException("EXPECTED: some arbitrary exception in the onFinished handler")
        }
    }

    void testUnregisteredCommandWithLog() {
        serverDolphin.serverConnector.log.level = Level.ALL
        clientDolphin.send "no-such-action-registered", {
            // unknown actions are silently ignored and logged as warnings on the server side.
        }
        context.assertionsDone()
    }
    void testUnregisteredCommandWithoutLog() {
        serverDolphin.serverConnector.log.level = Level.OFF
        clientDolphin.send "no-such-action-registered"
        context.assertionsDone()
    }

    void testRebaseIsTransferred() {
        ClientPresentationModel person = clientDolphin.presentationModel("person",null,name:'Dierk',other:'Dierk')

        person.name.qualifier  = 'qualifier'
        person.other.qualifier = 'qualifier'

        assert person.name.value == "Dierk"
        person.name.value = "Mittie"
        assert person.name.dirty


        clientDolphin.sync { assert serverDolphin["person"].name.value == "Mittie" }
        person.name.rebase()
        assert ! person.name.dirty
        assert person.name.value      == "Mittie" // value unchanged
        assert person.other.value     == "Mittie" // proliferated to attributes with same qualifier
        assert person.name.baseValue  == "Mittie" // base value changed
        assert person.other.baseValue == "Mittie" // proliferated to attributes with same qualifier
        clientDolphin.sync {
            assert serverDolphin["person"].name.baseValue  == "Mittie" // rebase is done on server
            assert serverDolphin["person"].other.baseValue == "Mittie" // rebase is done on server
            context.assertionsDone()
        }
    }

    // silly and only for the coverage, we test behavior when id is wrong ...
    void testIdNotFoundInVariousCommands() {
        clientDolphin.clientConnector.send new BaseValueChangedCommand(attributeId: 0)
        clientDolphin.clientConnector.send new ValueChangedCommand(attributeId: 0)
        GServerDolphin.changeValue(null, null, null)
        GServerDolphin.changeValue(null, new GServerAttribute('a',42), 42)
        context.assertionsDone()
    }

    void testApplyPm() {
        serverDolphin.action("checkPmWasApplied") { cmd, resp ->
            assert 1 == serverDolphin['second'].getAt('a',Tag.VALUE).value
            context.assertionsDone()
        }
        def first = clientDolphin.presentationModel("first", null, a:1)
        def second = clientDolphin.presentationModel("second", null, a:2)
        clientDolphin.apply first to second
        assert 1 == second.a.value
        clientDolphin.send "checkPmWasApplied"
    }

    void testPmCreationWithNullValuesAndTagIt() {
        def nullValuePM = clientDolphin.presentationModel("someId", ['a', 'b', 'c'])
        assert null == nullValuePM.c.value
        clientDolphin.tag(nullValuePM, 'a', Tag.tagFor.MESSAGE, "the 'a' message")
        assert nullValuePM.getAt('a', Tag.tagFor.MESSAGE).value == "the 'a' message"
        context.assertionsDone()
    }

    void testDataRequest() {
        serverDolphin.action("myData") { cmd, resp ->
            resp << new DataCommand([a:1, b:2])
        }
        clientDolphin.data "myData", { data ->
            assert data.size() == 1
            assert data[0].a == 1
            assert data[0].b == 2
            context.assertionsDone()
        }
    }

    void testPmReset() {
        def myPm = clientDolphin.presentationModel("myPm", null, a:1)
        assert ! myPm.dirty
        myPm.a.value = 2
        assert myPm.dirty
        myPm.reset()
        assert myPm.a.value == 1
        assert ! myPm.dirty
        myPm.a.value = 1
        assert ! myPm.dirty
        context.assertionsDone()
    }

    void testPmRebase() {
        def myPm = clientDolphin.presentationModel("myPm", null, a:1)
        myPm.a.value = 2
        assert myPm.dirty
        myPm.rebase()
        assert myPm.a.value == 2
        assert ! myPm.dirty
        context.assertionsDone()
    }

    void testWithPresentationModelFetchedFromServer() {
        serverDolphin.serverConnector.registry.register(GetPresentationModelCommand) { GetPresentationModelCommand cmd, response ->
            serverDolphin.presentationModel(response, "newPm", null, new DTO(new Slot('a','1')))
        }
        clientDolphin.modelStore.withPresentationModel("newPm", { pm ->
            assert pm.id == 'newPm'
            assert pm.a.value == '1'
            context.assertionsDone()
        } as WithPresentationModelHandler)
    }

    void testActionAndSendJavaLike() {
        boolean reached = false
        serverDolphin.action("java", new NamedCommandHandler() {
            @Override
            void handleCommand(NamedCommand command, List<Command> response) {
                reached = true
            }
        })
        clientDolphin.send("java", new OnFinishedHandlerAdapter() {
            @Override
            void onFinished(List<ClientPresentationModel> presentationModels) {
                assert reached
                context.assertionsDone()
            }
        })
    }

    void testAttributeRebase() {
        ClientPresentationModel pm = clientDolphin.presentationModel('pm', attr: 1)
        assert pm.attr.value == 1
        assert pm.attr.baseValue == 1

        serverDolphin.action('rebase') { cmd, response ->
            serverDolphin.rebase(response, serverDolphin['pm'].attr)
        }
        pm.attr.value = 2
        assert pm.attr.value == 2
        assert pm.attr.baseValue == 1

        clientDolphin.send 'rebase', {
            // dk: it is a bit odd that we have nest this...
            clientDolphin.sync {
                assert serverDolphin['pm'].attr.baseValue == 2
                context.assertionsDone()
            }
        }
    }

    void testAttributeReset() {
        ClientPresentationModel pm = clientDolphin.presentationModel('pm', attr: 1)

        serverDolphin.action('reset') { cmd, response ->
            serverDolphin.reset(response, serverDolphin['pm'].attr)
        }
        pm.attr.value = 2

        clientDolphin.send 'reset', {
            assert pm.attr.value == 1
            assert ! pm.attr.dirty
            context.assertionsDone()
        }
    }

    void testPresentationModelReset() {
        ClientPresentationModel pm = clientDolphin.presentationModel('pm', attr: 1)

        serverDolphin.action('reset') { cmd, response ->
            serverDolphin.reset(response, serverDolphin['pm'])
        }
        pm.attr.value = 2
        assert pm.dirty

        clientDolphin.send 'reset', {
            assert pm.attr.value == 1
            assert ! pm.dirty
            context.assertionsDone()
        }
    }

    void testDeletePresentationModel() {
        clientDolphin.presentationModel('pm', attr: 1)

        serverDolphin.action('delete') { cmd, response ->
            serverDolphin.delete(response, serverDolphin['pm'])
        }
        assert clientDolphin['pm']

        clientDolphin.send 'delete', {
            assert clientDolphin['pm'] == null
            context.assertionsDone()
        }
    }

    void testDeleteAllPresentationModelsOfTypeFromServer() {
        clientDolphin.presentationModel('pm1', 'type', attr: 1)
        clientDolphin.presentationModel('pm2', 'type', attr: 1)

        serverDolphin.action('deleteAll') { cmd, response ->
            serverDolphin.deleteAllPresentationModelsOfType(response, 'type')
        }
        assert clientDolphin['pm1']
        assert clientDolphin['pm2']

        clientDolphin.send 'deleteAll', {
            assert clientDolphin['pm1'] == null
            assert clientDolphin['pm2'] == null
            context.assertionsDone()
        }
    }

    void testDeleteAllPresentationModelsOfTypeFromClient() {

        serverDolphin.action('assert1') { cmd, response ->
            assert 1 == serverDolphin.findAllPresentationModelsByType('PmType').size()
        }
        serverDolphin.action('assert0') { cmd, response ->
            assert 0 == serverDolphin.findAllPresentationModelsByType('PmType').size()
        }

        clientDolphin.send 'assert0'

        clientDolphin.presentationModel('pm', 'PmType', attr: 1)
        assert clientDolphin['pm']

        clientDolphin.send 'assert1'

        clientDolphin.deleteAllPresentationModelsOfType('PmType')
        assert new DeletedAllPresentationModelsOfTypeNotification('PmType') =~ /pmType PmType/


        clientDolphin.send 'assert0', {
            assert clientDolphin['pm'] == null
            context.assertionsDone()
        }

    }

    void testCopyPresentationModelOnClient() {

        ClientAttribute ca = new ClientAttribute('attr1', true, 'qualifier', Tag.ENABLED)
        ca.value = false
        def pm1 = clientDolphin.presentationModel("PM1", "type", ca)
        clientDolphin.addAttributeToModel(pm1, ca)
        def pm2 = clientDolphin.copy(pm1)

        assert pm1.id != pm2.id
        assert pm1.presentationModelType == pm2.presentationModelType
        assert pm1.attributes.size()    == pm2.attributes.size()
        def orig = pm1.getAt('attr1', Tag.ENABLED)
        def copy = pm2.getAt('attr1', Tag.ENABLED)
        assert orig.value     == copy.value
        assert orig.baseValue == copy.baseValue
        assert orig.qualifier == copy.qualifier
        assert orig.tag       == copy.tag

        serverDolphin.action('assert') { cmd, response ->
            def pms = serverDolphin.findAllPresentationModelsByType('type')
            assert pms.size() == 2
            def spm1 = pms[0]
            def spm2 = pms[1]
            assert spm1.id != spm2.id
            assert spm1.presentationModelType == spm2.presentationModelType
            assert spm1.attributes.size()    == spm2.attributes.size()
            def sorig = spm1.getAt('attr1', Tag.ENABLED)
            def scopy = spm2.getAt('attr1', Tag.ENABLED)
            assert sorig.value     == scopy.value
            assert sorig.baseValue == scopy.baseValue
            assert sorig.qualifier == scopy.qualifier
            assert sorig.tag       == scopy.tag
        }

        clientDolphin.send 'assert', {
            context.assertionsDone()
        }
    }

    void testWithNullResponses() {
        clientDolphin.presentationModel('pm', attr: 1)

        serverDolphin.action('arbitrary') { cmd, response ->
            serverDolphin.rebase(null, serverDolphin['pm'].attr)
            serverDolphin.rebase([], null)
            serverDolphin.reset(null, serverDolphin['pm'])
            serverDolphin.reset([], '')
            serverDolphin.reset([], (GServerAttribute) null)
            serverDolphin.reset([], (GServerPresentationModel) null)
            serverDolphin.delete([], (GServerPresentationModel) null)
            serverDolphin.delete([], '')
            serverDolphin.delete(null, '')
            serverDolphin.presentationModel(null, null,null,null)
            serverDolphin.clientSideModel(null, null, null, null)
            serverDolphin.changeValue([], null, null)
            serverDolphin.initAt(null, '', '', '')
        }
        clientDolphin.send('arbitrary'){
            context.assertionsDone()
        }
    }

    void testStateConflictBetweenClientAndServer() {
        LogConfig.logCommunication()
        def latch = new CountDownLatch(1)
        def pm = clientDolphin.presentationModel('pm', attr: 1)
        def attr = pm.getAt('attr')

        serverDolphin.action('set2') { cmd, response ->
            latch.await() // mimic a server delay such that the client has enough time to change the value concurrently
            serverDolphin.getAt('pm').getAt('attr').value == 1
            serverDolphin.getAt('pm').getAt('attr').value  = 2
            serverDolphin.getAt('pm').getAt('attr').value == 2 // immediate change of server state
        }
        serverDolphin.action('assert3') { cmd, response ->
            assert serverDolphin.getAt('pm').getAt('attr').value == 3
        }

        clientDolphin.send('set2') // a conflict could arise when the server value is changed ...
        attr.value = 3            // ... while the client value is changed concurrently
        latch.countDown()
        clientDolphin.send('assert3') // since from the client perspective, the last change was to 3, server and client should both see the 3

        // in between these calls a conflicting value change could be transferred, setting both value to 2

        clientDolphin.send('assert3'){
            assert attr.value == 3
            context.assertionsDone()
        }


    }

}