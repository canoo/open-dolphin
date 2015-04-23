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

package org.opendolphin.core.client.comm

import groovy.util.logging.Log
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.client.*
import org.opendolphin.core.comm.*

import java.beans.PropertyChangeEvent
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ClientConnectorTests extends GroovyTestCase {

    TestClientConnector clientConnector
    ClientDolphin dolphin
    AttributeChangeListener attributeChangeListener

    /**
     * Since command transmission is done in parallel to test execution thread the test method might finish
     * before the command processing is complete. Therefore {@link #tearDown()} waits for this CountDownLatch
     * (which btw. is initialized in {@link #setUp()} and decremented in the handler of a {@code dolphin.sync ( )} call).
     * Also putting asserts in the callback handler of a {@code dolphin.sync ( )} call seems not to be reliable since JUnit
     * seems not to be informed (reliably) of failing assertions.
     *
     * Therefore the following approach for the test methods has been taken to:
     * - initialize the CountDownLatch in {@code # setup ( )}
     * - after the 'act' section of a test method: call {@code syncAndWaitUntilDone ( )} which releases the latch inside a dolphin.sync handler and then (in the main thread) waits for the latch
     * - performs all assertions
     */
    CountDownLatch syncDone


    @Override
    protected void setUp() {

        dolphin = ClientDolphinFactory.create()
        clientConnector = new TestClientConnector(dolphin)
        clientConnector.uiThreadHandler = new RunLaterUiThreadHandler()
        dolphin.clientConnector = clientConnector
        dolphin.clientModelStore = new ClientModelStore(dolphin)
        attributeChangeListener = dolphin.clientModelStore.@attributeChangeListener

        initLatch()
    }

    private void initLatch() {
        syncDone = new CountDownLatch(1)
    }

    private boolean waitForLatch() {
        return syncDone.await(2, TimeUnit.SECONDS)
    }

    void syncAndWaitUntilDone() {
        dolphin.sync {
            syncDone.countDown()
        }
        assert waitForLatch()
    }

    void assertCommandsTransmitted(int count) {
        assert clientConnector.getTransmitCount() == count
    }

    void assertOnlySyncCommandWasTransmitted() {
        assertCommandsTransmitted(1)
        // 1 command was sent because of the sent sync (resulting in a EMPTY command):
        assert clientConnector.transmittedCommands[0] instanceof EmptyNotification
    }

    void testSevereLogWhenCommandNotFound() {
        clientConnector.handle(new Command())
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testHandleSimpleCreatePresentationModelCommand() {
        final myPmId = "myPmId"
        assert null == dolphin.findPresentationModelById(myPmId)
        CreatePresentationModelCommand command = new CreatePresentationModelCommand()
        command.pmId = myPmId
        def result = clientConnector.handle(command)
        assert myPmId == result.id
        assert dolphin.findPresentationModelById(myPmId)
        syncAndWaitUntilDone()
        assertCommandsTransmitted(2)
    }

    void testDefaultOnExceptionHandler() {
        clientConnector.uiThreadHandler = { it() } as UiThreadHandler
        String exceptionMessage = 'TestException thrown on purpose'
        def msg = shouldFail(RuntimeException) {
            clientConnector.onException(new RuntimeException(exceptionMessage))
        }
        assert msg == exceptionMessage
    }

    void testPropertyChange_DirtyPropertyIgnored() {
        attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", BaseAttribute.DIRTY_PROPERTY, null, null))
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testValueChange_OldAndNewValueSame() {
        attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", BaseAttribute.VALUE, 'sameValue', 'sameValue'))
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testValueChange_noQualifier() {
        ClientAttribute attribute = dolphin.createAttribute('attr', 'initialValue')
        dolphin.clientModelStore.registerAttribute(attribute)
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, BaseAttribute.VALUE, attribute.value, 'newValue'))
        syncAndWaitUntilDone()
        assertCommandsTransmitted(2)
        assert attribute.value == 'initialValue'
        assert clientConnector.transmittedCommands.any { it instanceof ValueChangedCommand }
    }

    void testValueChange_withQualifier() {
        syncDone = new CountDownLatch(1)

        ClientAttribute attribute = dolphin.createAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(attribute)
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, BaseAttribute.VALUE, attribute.value, 'newValue'))
        syncAndWaitUntilDone()

        assertCommandsTransmitted(3)
        assert attribute.value == 'newValue'
        assert clientConnector.transmittedCommands.any { it instanceof ValueChangedCommand }
    }

    void testAddAttributeToPresentationModel_ClientSideOnly() {
        def clientPM = clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true, attributes: [[propertyName: '1', value: 'initialValue1', qualifier: 'qualifier']]))
        dolphin.addAttributeToModel(clientPM, dolphin.createAttribute('2', 'initialValue2'))
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testAddTwoAttributesWithSameQualifierToSamePMIsNotAllowed() {
        shouldFail(IllegalStateException) {
            ClientPresentationModel presentationModel = clientConnector.clientDolphin.presentationModel("1", clientConnector.clientDolphin.createAttribute("a", "0", "QUAL"))
            dolphin.addAttributeToModel(presentationModel, clientConnector.clientDolphin.createAttribute("c", "0", "QUAL"))
        }
    }

    void testAddTwoAttributesInConstructorWithSameQualifierToSamePMIsNotAllowed() {
        shouldFail(IllegalStateException) {
            dolphin.presentationModel("1", clientConnector.clientDolphin.createAttribute("a", "0", "QUAL"), clientConnector.clientDolphin.createAttribute("b", "0", "QUAL"))
        }
    }

    void testBaseValueChange_OldAndNewValueSame() {
        attributeChangeListener.propertyChange(new PropertyChangeEvent("dummy", BaseAttribute.BASE_VALUE, 'sameValue', 'sameValue'))
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testBaseValueChange() {
        ClientAttribute attribute = dolphin.createAttribute('attr', 'initialValue', 'qualifier')
        ClientAttribute secondAttWithSameQualifier = dolphin.createAttribute('attr2', 'otherInitialValue', 'qualifier')
        attribute.value = 'newValue'
        assert attribute.baseValue == 'initialValue'
        dolphin.clientModelStore.registerAttribute(attribute)
        dolphin.clientModelStore.registerAttribute(secondAttWithSameQualifier)
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, BaseAttribute.BASE_VALUE, 'old_base_value', 'new_base_value'))
        syncAndWaitUntilDone()
        assertCommandsTransmitted(3 + 1)
        assert attribute.baseValue == 'new_base_value'
        assert secondAttWithSameQualifier.baseValue == 'new_base_value'
        assert clientConnector.transmittedCommands.any { it instanceof BaseValueChangedCommand }
    }

    void test_that_notWellKnown_property_causes_MetaDataChange() {
        ClientAttribute attribute = new ExtendedAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(attribute)
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, 'additionalParam', null, 'newTag'))
        syncAndWaitUntilDone()
        assertCommandsTransmitted(2)
        assert ChangeAttributeMetadataCommand == clientConnector.transmittedCommands[0].class
        assert 'newTag' == attribute.additionalParam
    }

    void testMetaDataChange_UnregisteredAttribute() {
        ClientAttribute attribute = new ExtendedAttribute('attr', 'initialValue', 'qualifier')
        attribute.additionalParam = 'oldValue'
        attributeChangeListener.propertyChange(new PropertyChangeEvent(attribute, 'additionalParam', null, 'newTag'))
        syncAndWaitUntilDone()
        assertCommandsTransmitted(2)
        assert ChangeAttributeMetadataCommand == clientConnector.transmittedCommands[0].class
        assert 'oldValue' == attribute.additionalParam
    }

    void testHandle_PresentationModelReseted() {
        dolphin.presentationModel('p1')
        assert clientConnector.handle(new PresentationModelResetedCommand(pmId: 'p1'))
    }

    void testHandle_PresentationModelReseted_PmNotExists() {
        assert !clientConnector.handle(new PresentationModelResetedCommand(pmId: 'notExist'))
    }

    void testHandle_InitializeAttribute() {
        def syncedAttribute = dolphin.createAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(syncedAttribute)
        clientConnector.handle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', 'newValue'))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('newProp')
        assert 'newValue' == dolphin.getAt('p1').getAt('newProp').value
        assert 'newValue' == syncedAttribute.value

    }

    void testHandle_InitializeAttribut_ExistingAttributeValueIsSet() {
        clientConnector.handle(new InitializeAttributeCommand('p1', 'prop', null, 'initialValue'))
        clientConnector.handle(new InitializeAttributeCommand('p1', 'prop', null, 'updatedValue'))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('prop')
        assert 'updatedValue' == dolphin.getAt('p1').getAt('prop').value
    }

    void testHandle_InitializeAttribute_NewValueNotSet() {
        def syncedAttribute = dolphin.createAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(syncedAttribute)
        clientConnector.handle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', null))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('newProp')
        assert 'initialValue' == dolphin.getAt('p1').getAt('newProp').value
        assert 'initialValue' == syncedAttribute.value

    }

    void testHandle_InitializeAttribute_NewValueNotSet_and_firstOtherAttributeValueIsNull() {
        def syncedAttribute1 = dolphin.createAttribute('attr', null, 'qualifier')
        def syncedAttribute2 = dolphin.createAttribute('attr2', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(syncedAttribute1)
        dolphin.clientModelStore.registerAttribute(syncedAttribute2)
        // null from 'syncedAttribute1' will be synchronized to other attributes since it is the first in the list of attributes with qualifier 'qualifier'
        clientConnector.handle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', null))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('newProp')
        assert null == dolphin.getAt('p1').getAt('newProp').value
        assert null == syncedAttribute1.value
        assert null == syncedAttribute2.value
    }

    void testHandle_SwitchPresentationModel_PmNotExists() {
        assert !clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
        dolphin.presentationModel('p2')
        assert !clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
    }

    void testHandle_SwitchPresentationModel() {
        ClientPresentationModel model_one = dolphin.presentationModel('p1')
        model_one._internal_addAttribute(dolphin.createAttribute('attr', 'one'))
        ClientPresentationModel model_two = dolphin.presentationModel('p2')
        model_two._internal_addAttribute(dolphin.createAttribute('attr', 'two'))
        assert clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
        assert 'one' == dolphin.getAt('p2').getAt('attr').value
    }

    void testHandle_InitialValueChanged_AttrNotExists() {
        def attribute = dolphin.createAttribute('attr', 'initialValue')
        assert !clientConnector.handle(new BaseValueChangedCommand(attributeId: attribute.id))
    }

    void testHandle_InitialValueChanged() {
        def attribute = dolphin.createAttribute('attr', 'initialValue')
        attribute.value = 'newValue'
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.handle(new BaseValueChangedCommand(attributeId: attribute.id))
        assert 'newValue' == attribute.baseValue
    }

    void testHandle_ValueChanged_AttrNotExists() {
        assert !clientConnector.handle(new ValueChangedCommand(attributeId: 0, oldValue: 'oldValue', newValue: 'newValue'))
    }

    void testHandle_ValueChangedWithBadBaseValueIsIgnored() {
        def attribute = dolphin.createAttribute('attr', 'initialValue')
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.handle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'no-such-base-value', newValue: 'newValue'))
        assert 'initialValue' == attribute.value
    }

    void testHandle_ValueChanged() {
        def attribute = dolphin.createAttribute('attr', 'initialValue')
        dolphin.clientModelStore.registerAttribute(attribute)
        assert !clientConnector.handle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'initialValue', newValue: 'newValue'))
        assert 'newValue' == attribute.value
    }

    void testHandle_CreatePresentationModelTwiceFails() {
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        def msg = shouldFail {
            clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        }
        assert "There already is a presentation model with id 'p1' known to the client." == msg
    }

    void testHandle_CreatePresentationModel() {
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('attr')
        assert 'initialValue' == dolphin.getAt('p1').getAt('attr').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr').qualifier
        syncAndWaitUntilDone()
        assertCommandsTransmitted(2)
        assert CreatePresentationModelCommand == clientConnector.transmittedCommands[0].class
    }

    void testHandle_CreatePresentationModel_ClientSideOnly() {
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true, attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('attr')
        assert 'initialValue' == dolphin.getAt('p1').getAt('attr').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr').qualifier
        syncAndWaitUntilDone()
        assertOnlySyncCommandWasTransmitted()
    }

    void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
        dolphin.presentationModel('p1')
        shouldFail(IllegalStateException) {
            clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: []))
        }
    }

    void testHandle_DeletePresentationModel() {
        ClientPresentationModel p1 = dolphin.presentationModel('p1')
        p1.clientSideOnly = true
        ClientPresentationModel p2 = dolphin.presentationModel('p2')
        clientConnector.handle(new DeletePresentationModelCommand(pmId: null))
        def model = new GClientPresentationModel('p3', [])
        clientConnector.handle(new DeletePresentationModelCommand(pmId: model.id))
        clientConnector.handle(new DeletePresentationModelCommand(pmId: p1.id))
        clientConnector.handle(new DeletePresentationModelCommand(pmId: p2.id))
        assert !dolphin.getAt(p1.id)
        assert !dolphin.getAt(p2.id)
        syncAndWaitUntilDone()
        // 3 commands will have been transferred:
        // 1: delete of p1 (causes no DeletedPresentationModelNotification since client side only)
        // 2: delete of p2
        // 3: DeletedPresentationModelNotification caused by delete of p2
        assertCommandsTransmitted(4)
        assert 1 == clientConnector.transmittedCommands.findAll {
            it instanceof DeletedPresentationModelNotification
        }.size()
    }

    void testHandle_DeleteAllPresentationModelsOfType() {
        ClientPresentationModel p1 = dolphin.presentationModel('p1', 'type')
        ClientPresentationModel p2 = dolphin.presentationModel('p2', 'type')
        clientConnector.handle(new DeleteAllPresentationModelsOfTypeCommand(pmType: 'type'))
        assert !dolphin.getAt(p1.id)
        assert !dolphin.getAt(p2.id)
        syncAndWaitUntilDone()
        assertCommandsTransmitted(4) // two creates, one deleteAll, one empty
        assert 1 == clientConnector.transmittedCommands.findAll {
            it instanceof DeletedAllPresentationModelsOfTypeNotification
        }.size()
    }

    void testHandle_DataCommand() {
        def data = [k: 'v']
        assert data == clientConnector.handle(new DataCommand(data))
    }

    void testHandleSavedPMNotificationForKnownPm() {
        def pm = dolphin.presentationModel('wasSaved', null, a: 1, b: 1)
        pm.a.value = 2
        pm.b.value = 2
        def result = clientConnector.handle(new SavedPresentationModelNotification(pmId: 'wasSaved'))
        assert pm.a.value == 2
        assert !pm.a.dirty
        assert pm.b.value == 2
        assert !pm.b.dirty
        assert result == pm
    }

    void testHandleSavedPMNotificationForUnKnownPmIsSilentlyIgnored() {
        assert null == clientConnector.handle(new SavedPresentationModelNotification(pmId: 'no-such-id'))
    }

}

@Log
class TestClientConnector extends ClientConnector {

    List<Command> transmittedCommands = []

    TestClientConnector(ClientDolphin clientDolphin) {
        super(clientDolphin)
    }

    int getTransmitCount() {
        transmittedCommands.size()
    }

    List<Command> transmit(List<Command> commands) {
        println "transmit: ${commands.size()}"
        def result = new LinkedList<Command>()
        commands.each() { Command cmd ->
            result.addAll(transmitCommand(cmd))
        }
        result
    }

    List<Command> transmitCommand(Command command) {
        println "transmitCommand: $command"
        transmittedCommands << command
        return construct(command)
    }

    List construct(ChangeAttributeMetadataCommand command) {
        [new AttributeMetadataChangedCommand(attributeId: command.attributeId, metadataName: command.metadataName, value: command.value)]
    }

    List construct(Command command) {
        []
    }

    List construct(ResetPresentationModelCommand command) {
        [new PresentationModelResetedCommand(pmId: command.pmId)]
    }

}

class ExtendedAttribute extends GClientAttribute {
    String additionalParam

    ExtendedAttribute(String propertyName, Object initialValue, String qualifier) {
        super(propertyName, initialValue, qualifier)
    }
}
