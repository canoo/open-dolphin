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

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.ServerDolphin

import java.util.concurrent.TimeUnit

class DeletePresentationModelTests extends GroovyTestCase {

    volatile TestInMemoryConfig context
    ServerDolphin serverDolphin
    ClientDolphin clientDolphin

    @Override
    protected void setUp() {
        context = new TestInMemoryConfig()
        serverDolphin = context.serverDolphin
        clientDolphin = context.clientDolphin
    }

    @Override
    protected void tearDown() {
        assert context.done.await(2, TimeUnit.SECONDS)
    }

    void testCreateAndDeletePresentationModel() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.presentationModel(modelId, someAttribute: "someValue")
        // sanity check: we have a least the client model store listening to changes of someAttribute
        assert model.someAttribute.propertyChangeListeners
        // the model is in the client model store
        def found = clientDolphin.getAt(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getAt(modelId)
        }
        // when we now delete the pm
        clientDolphin.delete(model)
        // ... it is no longer in the client model store
        assert !clientDolphin.findPresentationModelById(modelId)
        // ... all listeners have been detached from model and all its attributes
        assert !model.getPropertyChangeListeners()
        // what is allowed to remain is the "detached" model still listening to its own attribute changes
        model.attributes*.propertyChangeListeners.flatten()*.listener.each {
            assert (it.toString() =~ "PresentationModel")
            // todo dk: the below should also work but there is some weird boxing going on
            // assert it.is(model)
        }
        // the model is also gone from the server model store
        clientDolphin.sync {
            assert !serverDolphin.getAt(modelId)
            context.assertionsDone()
        }
    }


    void testCreateAndDeletePresentationModelFromServer() {
        // create the pm
        String modelId = 'modelId'
        def model = clientDolphin.presentationModel(modelId, someAttribute: "someValue")
        // the model is in the client model store
        def found = clientDolphin.getAt(modelId)
        assert model == found
        // ... and in the server model store after roundtrip
        clientDolphin.sync {
            assert serverDolphin.getAt(modelId)
        }

        serverDolphin.action('triggerDelete') { cmd, List<Command> response ->
            serverDolphin.delete(response, modelId)
        }
        // when we now delete the pm
        clientDolphin.send 'triggerDelete', {
            clientDolphin.sync {
                // ... it is no longer in the client model store
                assert !clientDolphin.getAt(modelId)
            }
            clientDolphin.sync {
                // the model is also gone from the server model store
                assert !serverDolphin.getAt(modelId)
                // we are done
                context.assertionsDone()
            }
        }

    }

}