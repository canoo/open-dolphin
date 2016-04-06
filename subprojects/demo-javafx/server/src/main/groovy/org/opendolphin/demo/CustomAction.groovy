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
import org.opendolphin.core.comm.*
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static VehicleConstants.*

class CustomAction extends DolphinServerAction {


    private Closure impl = { propertyName, NamedCommand command, response ->
        def actual = serverDolphin.findPresentationModelById('actualPm')
        def att = actual.findAttributeByPropertyName(propertyName)

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server")
    }

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }

        registry.register 'setTitle', impl.curry('title')

        registry.register 'setPurpose', impl.curry('purpose')

        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "fill", qualifier: "vehicle-${it}.color")
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "x", qualifier: "vehicle-${it}.x")
            }
        }

        registry.register GetPresentationModelCommand, { GetPresentationModelCommand command, response ->
            if (command.pmId.startsWith('vehicleDetail')) {
                String selector = command.pmId.split('-')[1]
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_WIDTH, newValue: rand(),)
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_X,      qualifier: qualify(selector, ATT_X))
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_Y,      qualifier: qualify(selector, ATT_Y))
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_ROTATE, qualifier: qualify(selector, ATT_ROTATE))
                response << new InitializeAttributeCommand(pmId: command.pmId, propertyName: ATT_COLOR,  qualifier: qualify(selector, ATT_COLOR))
            }
        }
    }

}
