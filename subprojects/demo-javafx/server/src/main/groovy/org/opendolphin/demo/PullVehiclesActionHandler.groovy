/*
 * Copyright 2012-2017 Canoo Engineering AG.
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
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.comm.NamedCommandHandler

import static VehicleConstants.*

class PullVehiclesActionHandler implements NamedCommandHandler {
    int rand() { return (Math.random() * 350).toInteger() }

    void handleCommand(NamedCommand command, List<Command> response) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        vehicles.each { String pmId ->
            DTO model = new DTO(
                new Slot(ATT_X,        rand(),  "vehicle-${ pmId }.x"),
                new Slot(ATT_Y,        rand(),  "vehicle-${ pmId }.y"),
                new Slot(ATT_WIDTH,    80),
                new Slot(ATT_HEIGHT,   25),
                new Slot(ATT_ROTATE,   rand(),  "vehicle-${ pmId }.rotate"),
                new Slot(ATT_COLOR,    pmId,    "vehicle-${ pmId }.color")
            )
            DefaultServerDolphin.presentationModelCommand(response, pmId, TYPE_VEHICLE, model)
        }
    }
}