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

import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.server.ServerConnector

import java.util.concurrent.TimeUnit

/**
 * Tests for the sequence between client requests and server responses.
 * They are really more integration tests than unit tests.
 */

class CommunicationTests extends GroovyTestCase {

	ServerConnector     serverConnector
	ClientConnector     clientConnector
    ClientModelStore    clientModelStore
    ClientDolphin       clientDolphin
    TestInMemoryConfig  config

    @Override
	protected void setUp() {
		LogConfig.logCommunication()
		config = new TestInMemoryConfig()
        serverConnector  = config.serverDolphin.serverConnector
        clientConnector  = config.clientDolphin.clientConnector
        clientModelStore = config.clientDolphin.clientModelStore
        clientDolphin    = config.clientDolphin
	}

    @Override
    protected void tearDown() {
        assert config.done.await(2, TimeUnit.SECONDS)
    }

	void testSimpleAttributeChangeIsVisibleOnServer() {
		def ca  = new ClientAttribute('name')
        def cpm = new ClientPresentationModel('model', [ca])
        clientModelStore.add cpm

		Command receivedCommand = null
		def testServerAction = { ValueChangedCommand command, response ->
			receivedCommand = command
		}
		serverConnector.registry.register ValueChangedCommand, testServerAction
		ca.value = 'initial'

        clientDolphin.sync {
            assert receivedCommand
            assert receivedCommand.id == 'ValueChanged'
            assert receivedCommand in ValueChangedCommand
            assert receivedCommand.oldValue == null
            assert receivedCommand.newValue == 'initial'
            config.assertionsDone()
        }
	}

	void testServerIsNotifiedAboutNewAttributesAndTheirPms() {

		Command receivedCommand = null
		def testServerAction = { CreatePresentationModelCommand command, response ->
			receivedCommand = command
		}
		serverConnector.registry.register CreatePresentationModelCommand, testServerAction

        clientModelStore.add new ClientPresentationModel('testPm', [new ClientAttribute('name')])

        clientDolphin.sync {
            assert receivedCommand.id == "CreatePresentationModel"
            assert receivedCommand instanceof CreatePresentationModelCommand
            assert receivedCommand.pmId == 'testPm'
            assert receivedCommand.attributes.name
            config.assertionsDone()
        }
	}

	void testWhenServerChangesValueThisTriggersUpdateOnClient() {
		def ca = new ClientAttribute('name')

		def setValueAction = { CreatePresentationModelCommand command, response ->
			response << new ValueChangedCommand(
					attributeId: command.attributes.id.first(),
					newValue: "set from server",
					oldValue: null
			)
		}

		Command receivedCommand = null
		def valueChangedAction = { ValueChangedCommand command, response ->
			receivedCommand = command
            clientDolphin.sync {                            // there is no onFinished for value changes, so we have to do it here
                assert ca.value == "set from server"        // client is updated
                assert receivedCommand.attributeId == ca.id // client notified server about value change
                config.assertionsDone()
            }
		}

        serverConnector.registry.register CreatePresentationModelCommand, setValueAction
		serverConnector.registry.register ValueChangedCommand, valueChangedAction

        clientModelStore.add new ClientPresentationModel('testPm', [ca]) // trigger the whole cycle
    }

	void testRequestingSomeGeneralCommandExecution() {
		boolean reached = false
		serverConnector.registry.register "ButtonAction", { cmd, resp -> reached = true }
		clientConnector.send(new NamedCommand(id: "ButtonAction"))

        clientDolphin.sync {
            assert reached
            config.assertionsDone()
        }
	}

}
