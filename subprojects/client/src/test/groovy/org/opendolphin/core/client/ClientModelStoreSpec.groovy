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

package org.opendolphin.core.client

import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.ModelStoreListener
import org.opendolphin.core.client.comm.InMemoryClientConnector
import org.opendolphin.core.client.comm.WithPresentationModelHandler
import spock.lang.Specification

/**
 * @author Dieter Holz
 */
class ClientModelStoreSpec extends Specification {
    def modelStore, pmType, pm, listener

    def setup() {
        def clientDolphin = ClientDolphinFactory.create()
        modelStore = new ClientModelStore(clientDolphin)
        clientDolphin.clientModelStore = modelStore
        clientDolphin.clientConnector = new InMemoryClientConnector(clientDolphin)

        pmType = 'myType'
        pm = new GClientPresentationModel('myId', [])
        pm.setPresentationModelType(pmType)

        listener = Mock(ModelStoreListener)
        modelStore.addModelStoreListener(pmType, listener)
    }

    void "listeners are notified if PM is added to the clientModelStore"() {
        when:
        modelStore.add(pm)

        then:
        1 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
    }

    void "listeners are notified if PM is removed from clientModelStore"() {
        given:
        modelStore.add(pm)

        when:
        modelStore.remove(pm)

        then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
        1 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
    }

    void "listeners are not notified for different pmTypes"() {
        given:
        def otherPm = new GClientPresentationModel('otherId', [])
        otherPm.setPresentationModelType('otherType')

        when:
        modelStore.add(otherPm)
        modelStore.remove(otherPm)

        then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.ADDED, pm))
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
    }

    // when not in store, we need a functional test
    void "withPresentationModel returns known one if in store"() {
        given:
        modelStore.add pm
        def found = null
        when:
        modelStore.withPresentationModel(pm.id, { pm -> found = pm } as WithPresentationModelHandler)
        then:
        pm == found
    }

    void "trying to delete a pm that is not known to the store is silently ignored"() {
        when:
        modelStore.delete(null)
        modelStore.delete(pm) // has not been added!
        then:
        0 * listener.modelStoreChanged(new ModelStoreEvent(ModelStoreEvent.Type.REMOVED, pm))
    }


}
