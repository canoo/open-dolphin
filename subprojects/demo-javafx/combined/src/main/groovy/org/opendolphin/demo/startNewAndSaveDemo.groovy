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
package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot

def config = new JavaFxInMemoryConfig()
def dolphin = config.serverDolphin

dolphin.action "saveNewSelectedPerson", { cmd, List<Command> response ->
    def selectedPerson = dolphin.getAt('selectedPerson')

    // here: store a new person domain object with the attributes from above and get a new persistent id in return
    def pmId = "person-1" // for demo purposes assume a fixed value

    def slots = selectedPerson.attributes.collect {
        new Slot(it.propertyName, it.value,"${pmId}.${it.propertyName}", it.tag)
    }
    dolphin.presentationModelCommand(response, pmId, null, new DTO(slots))
}

new NewAndSaveView().show(config.clientDolphin)