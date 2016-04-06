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
package org.opendolphin.binding


import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.comm.InMemoryClientConnector
import org.opendolphin.core.server.ServerConnector
import spock.lang.Specification

import javax.swing.*


class BindingSpec extends Specification {

    // exposes http://www.canoo.com/jira/browse/DOL-26
    def 'binding the text property of a Swing component to an Attribute should not throw Exceptions'() {
        given:
        def dolphin = new ClientDolphin()
        dolphin.clientModelStore = new ClientModelStore(dolphin)
        dolphin.clientConnector = new InMemoryClientConnector(dolphin, [:] as ServerConnector)
        ClientPresentationModel loginPM = dolphin.presentationModel("loginPM", [name: "abc"])

        JTextField txtName = new JTextField()

        expect:
        Binder.bind("name").of(loginPM).to("text").of(txtName)
        Binder.bind("text").of(txtName).to("name").of(loginPM)
    }
}