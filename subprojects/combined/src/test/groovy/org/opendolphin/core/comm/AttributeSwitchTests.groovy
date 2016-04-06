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
package org.opendolphin.core.comm

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
/**
 * Tests for the approach of using plain attributes as switches by sharing the id.
 */

class AttributeSwitchTests extends GroovyTestCase {

    ClientPresentationModel switchPm
    ClientPresentationModel sourcePm
    ClientModelStore clientModelStore

    protected void setUp() {
        def config = new TestInMemoryConfig()
        clientModelStore = config.clientDolphin.clientModelStore

        switchPm = new ClientPresentationModel([new ClientAttribute(propertyName: 'name', qualifier: 'dataid1')])
        sourcePm = new ClientPresentationModel([new ClientAttribute(propertyName: 'name', qualifier: 'dataid2')])
        clientModelStore.add switchPm
        clientModelStore.add sourcePm
    }

    /** switching needs to set both, id and value! **/

    void testWritingToASwitchAlsoWritesBackToTheSource() {
        assert switchPm.name.value == null  //
        assert sourcePm.name.value == null

        switchPm.name.syncWith sourcePm.name

        assert switchPm.name.value == null
        assert sourcePm.name.value == null

        switchPm.name.value = 'newValue'

        assert sourcePm.name.value == 'newValue'
    }

    void testWritingToTheSourceAlsoUpdatesTheSwitch() {

        switchPm.name.syncWith sourcePm.name

        sourcePm.name.value = 'newValue'

        assert switchPm.name.value == 'newValue'
    }

    void testWritingToSwitchesWithSwitchingSources() {

        def otherPm = new ClientPresentationModel([new ClientAttribute(propertyName: 'name', qualifier: 'dataid3')])
        clientModelStore.add otherPm

        switchPm.name.syncWith sourcePm.name

        switchPm.name.value = 'firstValue'

        assert sourcePm.name.value == 'firstValue'
        assert otherPm.name.value == null           // untouched

        switchPm.name.syncWith otherPm.name

        assert switchPm.name.value == null
        assert sourcePm.name.value == 'firstValue'   // untouched

        // updating the selection should update the referred-to attribute but not the old one
        switchPm.name.value = 'secondValue'
        assert sourcePm.name.value == 'firstValue'   // untouched
        assert otherPm.name.value == 'secondValue'

        // updating the new source should update the switch but not the no-longer-referred-to source
        otherPm.name.value = 'otherValue'
        assert switchPm.name.value == 'otherValue'
        assert sourcePm.name.value == 'firstValue'
    }
}
