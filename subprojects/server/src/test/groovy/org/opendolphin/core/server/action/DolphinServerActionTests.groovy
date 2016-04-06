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
package org.opendolphin.core.server.action

import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.comm.ActionRegistry

class DolphinServerActionTests extends GroovyTestCase {

    DolphinServerAction action;

    @Override
    protected void setUp() throws Exception {
        action = new DolphinServerAction() {
            @Override
            void registerIn(ActionRegistry registry) {

            }
        }
        action.dolphinResponse = []
    }

    void testCreatePresentationModel() {
        action.presentationModel('p1', 'person', new DTO())
        assert 1 == action.dolphinResponse.size()
        assert CreatePresentationModelCommand == action.dolphinResponse.first().class
        assert 'p1' == action.dolphinResponse.first().pmId
        assert 'person' == action.dolphinResponse.first().pmType
    }

    void testChangeValue() {
        action.changeValue(new ServerAttribute('attr', 'initial'), 'newValue')
        assert 1 == action.dolphinResponse.size()
        assert ValueChangedCommand == action.dolphinResponse.first().class
    }

    void testInitializeAt() {
        action.initAt('p1', 'attr', 'qualifier')
        assert 1 == action.dolphinResponse.size()
        assert InitializeAttributeCommand == action.dolphinResponse.first().class

    }

}
