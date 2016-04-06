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
package org.opendolphin.core.client

import javafx.beans.value.ChangeListener
import spock.lang.Specification

class ClientAttributeWrapperSpec extends Specification {
    void "ChangeListener is notified when an attribute value changes"() {
        given:
        def attribute = new ClientAttribute('name')
        def wrapper = new ClientAttributeWrapper(attribute)
        attribute.value = ""
        def changeListener = Mock(ChangeListener)

        when:
        wrapper.addListener(changeListener)
        attribute.value = 'newValue'
        then:
        1 * changeListener.changed(_, _, _)
        attribute.value == 'newValue'
        wrapper.get() == 'newValue'
        wrapper.getName() == 'name'

        when:
        wrapper.set('latestValue')
        then:
        1 * changeListener.changed(_, _, _)
        attribute.value == 'latestValue'
        wrapper.get() == 'latestValue'

        when:
        wrapper = new ClientAttributeWrapper(attribute)
        wrapper.set(null)
        then:
        null ==wrapper.get()
    }
}
