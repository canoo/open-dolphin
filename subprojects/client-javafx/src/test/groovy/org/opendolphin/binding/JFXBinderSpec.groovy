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

package org.opendolphin.binding

import groovy.beans.Bindable
import org.opendolphin.core.client.ClientDolphinFactory
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.GClientPresentationModel
import org.opendolphin.core.client.comm.InMemoryClientConnector
import spock.lang.Specification

import javax.swing.*

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.unbind

class JFXBinderSpec extends Specification {

    // exposes http://www.canoo.com/jira/browse/DOL-26
    def 'binding the text property of a Swing component to an Attribute should not throw Exceptions'() {
        given:
        def dolphin = ClientDolphinFactory.create()
        dolphin.clientModelStore = new ClientModelStore(dolphin)
        dolphin.clientConnector = new InMemoryClientConnector(dolphin)
        GClientPresentationModel loginPM = dolphin.presentationModel("loginPM", [name: "abc"])

        JTextField txtName = new JTextField()

        expect:
        Binder.bind("name").of(loginPM).to("text").of(txtName)
        Binder.bind("text").of(txtName).to("name").of(loginPM)
    }

    def 'bind and unbind jfx Node to POJO'() {
        given:
        def initialValue = 'pojo'
        def sourceNode = new javafx.scene.control.Label()
        sourceNode.text = initialValue
        def targetPojo = new Pojo2()

        when:
        bind 'text' of sourceNode to 'value' of targetPojo
        then: // values are sync immediately when bound
        targetPojo.value == sourceNode.text

        when:
        sourceNode.text = 'newValue'
        then: // values are sync on source change
        targetPojo.value == sourceNode.text

        when:
        unbind 'text' of sourceNode from 'value' of targetPojo
        sourceNode.text = 'anotherValue'
        then:
        sourceNode.text == 'anotherValue'
        targetPojo.value != sourceNode.text
        targetPojo.value == 'newValue'
    }
}

class Pojo2 {
    @Bindable
    String value
}