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

import org.opendolphin.core.ModelStore
import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.ResetPresentationModelCommand
import org.opendolphin.core.server.ServerModelStore
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.ServerConnector

class ResetPresentationModelActionTests extends GroovyTestCase {

    void testResetModel() {
        def store = new ServerModelStore()
        ServerConnector connector = new ServerConnector(serverModelStore: store)
        ResetPresentationModelAction action = new ResetPresentationModelAction(store)
        store.add(new ServerPresentationModel('p1',[],store))
        connector.register(action)
        List response = connector.receive(new ResetPresentationModelCommand('p1'))
        assert 1 == response.size()
        assert PresentationModelResetedCommand == response.first().class
        assert 'p1' == response.first().pmId

    }
}
