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

import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static org.opendolphin.demo.VehicleConstants.*
import static org.opendolphin.demo.VehicleTaskConstants.ATT_DESCRIPTION

class VehicleTaskActions extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def count = 1

        registry.register VehicleTaskConstants.CMD_PULL, { NamedCommand command, response ->
            vehicles.each {
                def id = VehicleTaskConstants.unique(it)
                presentationModel(id, null, new DTO(
                    new Slot(ATT_DESCRIPTION,   "Task-"+ (count++)),
                    new Slot(ATT_COLOR,         null, qualify(it, ATT_COLOR)),
                    new Slot(ATT_X,             null, qualify(it, ATT_X))
                ))
            }
        }
    }
}
