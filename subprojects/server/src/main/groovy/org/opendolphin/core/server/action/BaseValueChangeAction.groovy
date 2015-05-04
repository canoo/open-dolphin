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

package org.opendolphin.core.server.action

import org.opendolphin.core.comm.BaseValueChangedCommand
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log

@Log
class BaseValueChangeAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(BaseValueChangedCommand) { BaseValueChangedCommand command, response ->
            def modelStore = serverDolphin.serverModelStore
            GServerAttribute attribute = modelStore.findAttributeById(command.attributeId)
            if (attribute) {
                attribute.silently {
                    attribute.rebase()
                }
                log.finest "S: attribute $attribute.id for $attribute.propertyName with value $attribute.value is dirty? : $attribute.dirty"
            }
            else log.warning("Could not find attribute with id '$command.attributeId' to change its base value.")
        }
    }
}
