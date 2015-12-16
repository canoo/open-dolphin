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

import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@CompileStatic
@Log
class StoreAttributeAction extends DolphinServerAction {
    void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedNotification) { AttributeCreatedNotification command, response ->
            def modelStore = serverDolphin.serverModelStore

            def existing = modelStore.findAttributeById(command.attributeId)
            if (existing) {
                log.info "trying to store an already existing attribute: $command"
                return
            }

            def attribute = new ServerAttribute(command.propertyName, command.newValue, command.qualifier, command.tag)
            attribute.id = command.attributeId
            def pm = serverDolphin.findPresentationModelById(command.pmId)
            if (null == pm) {
                pm = new ServerPresentationModel(command.pmId, [], modelStore)
                modelStore.add(pm)
            }
            pm._internal_addAttribute(attribute)
            modelStore.registerAttribute(attribute)
        }

        registry.register(ChangeAttributeMetadataCommand) { ChangeAttributeMetadataCommand command, response ->
            def attribute = serverDolphin.findAttributeById(command.attributeId)
            if (!attribute) {
                log.warning("Cannot find attribute with id '$command.attributeId'. Metadata remains unchanged.")
                return
            }
            attribute.silently {
                attribute[command.metadataName] = command.value
            }
        }
    }
}
