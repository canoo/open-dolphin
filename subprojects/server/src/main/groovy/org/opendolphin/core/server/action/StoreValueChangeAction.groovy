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

import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log

//@CompileStatic
@Log
class StoreValueChangeAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand) { ValueChangedCommand command, response ->
            GServerAttribute attribute = serverDolphin.findAttributeById(command.attributeId)
            if (attribute) {
                if (attribute.value != command.oldValue) {
                    log.warning("S: updating attribute with id '$command.attributeId' to new value '$command.newValue' even though its old command value '$command.oldValue' does not conform to the old value of '$attribute.value'. Client overrules server.")
                }
                attribute.silently {
                    attribute.value = command.newValue
                }
            } else {
                log.severe("S: cannot find attribute with id '$command.attributeId' to change value from '$command.oldValue' to '$command.newValue'. " +
                           "Known attribute ids are: "+ serverDolphin.serverModelStore.listPresentationModels()*.attributes*.id )
            }
        }
    }
}
