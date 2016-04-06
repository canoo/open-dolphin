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

import javax.swing.AbstractAction
import java.awt.event.ActionEvent

import static org.opendolphin.binding.Binder.bindInfo
import spock.lang.Specification

class DirtyBindingSpec extends Specification{

    void "binding the dirty state of a presentation model to a swing action"() {
        given:
        def pm = new ClientPresentationModel([])
        def action = new AbstractAction() {
            void actionPerformed(ActionEvent e) {}
        }
        when:
        bindInfo("dirty").of(pm).to("enabled").of(action)
        then:
        action.enabled == false
    }

    void "binding the dirty state of a presentation model to an attribute"() {
        given:
        def sourcePm = new ClientPresentationModel([])
        def targetPm = new ClientPresentationModel([new ClientAttribute("dirt",true)])
        when:
        bindInfo("dirty").of(sourcePm).to("dirt").of(targetPm)
        then:
        targetPm.dirt.value == false
    }
}
