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
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

class VehiclePushActions extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register VehicleConstants.CMD_PULL, { NamedCommand command, List<Command> response ->
            vehicles.each { String pmId ->
                presentationModel( pmId, VehicleConstants.TYPE_VEHICLE, new DTO (
                    new Slot(VehicleConstants.ATT_X,        rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_X)),
                    new Slot(VehicleConstants.ATT_Y,        rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_Y)),
                    new Slot(VehicleConstants.ATT_WIDTH,    80),
                    new Slot(VehicleConstants.ATT_HEIGHT,   25),
                    new Slot(VehicleConstants.ATT_ROTATE,   rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_ROTATE)),
                    new Slot(VehicleConstants.ATT_COLOR,    pmId,   VehicleConstants.qualify(pmId, VehicleConstants.ATT_COLOR))
                ))
            }
        }
        registry.register VehicleConstants.CMD_UPDATE, { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = serverDolphin.findPresentationModelById(vehicles.first())
            changeValue pm[VehicleConstants.ATT_X],        rand()
            changeValue pm[VehicleConstants.ATT_Y],        rand()
            changeValue pm[VehicleConstants.ATT_ROTATE],   rand()
        }

    }

}
