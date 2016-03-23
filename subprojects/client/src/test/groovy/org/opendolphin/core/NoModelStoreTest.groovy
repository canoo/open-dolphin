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
package org.opendolphin.core

import org.opendolphin.core.client.ClientDolphin
import spock.lang.Specification

class NoModelStoreTest extends Specification {

    void "calling the no-model store stores no models"() {
        given:
        def modelStore = new NoModelStore(new ClientDolphin());
        when:
        def added = modelStore.add(null)
        then:
        added == false
        modelStore.listPresentationModels().size() == 0
        when:
        def removed = modelStore.remove(null)
        then:
        removed == false
    }
}
