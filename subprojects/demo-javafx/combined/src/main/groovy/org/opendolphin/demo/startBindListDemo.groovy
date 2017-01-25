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

import org.opendolphin.core.comm.DeletePresentationModelCommand

import static VehicleConstants.*

def config = new JavaFxInMemoryConfig()
config.serverDolphin.action CMD_PULL, new PullVehiclesActionHandler()
config.serverDolphin.action CMD_CLEAR, { cmd, response ->
    for(pm in config.serverDolphin.findAllPresentationModelsByType(TYPE_VEHICLE)) {
        response << new DeletePresentationModelCommand(pm.id)
    }
}

BindListView.show config.clientDolphin
