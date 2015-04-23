package org.opendolphin.core

import org.opendolphin.core.client.ClientDolphinFactory
import org.opendolphin.core.client.ClientPresentationModel
import spock.lang.Specification

class NoModelStoreTest extends Specification {

    void "calling the no-model store stores no models"() {
        given:
        def modelStore = new NoModelStore(ClientDolphinFactory.create());
        when:
        def added = modelStore.add((ClientPresentationModel) null)
        then:
        added == false
        modelStore.listPresentationModels().size() == 0
        when:
        def removed = modelStore.remove((ClientPresentationModel) null)
        then:
        removed == false
    }
}
