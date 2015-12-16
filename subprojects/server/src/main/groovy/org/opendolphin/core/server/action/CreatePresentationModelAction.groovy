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

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.comm.ActionRegistry

@Log
@CompileStatic
class CreatePresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(CreatePresentationModelCommand) { CreatePresentationModelCommand command, response ->
            createPresentationModel(command, serverDolphin) // closure wrapper for correct scoping and extracted method for static compilation
        }
    }

    private static void createPresentationModel(CreatePresentationModelCommand command, DefaultServerDolphin serverDolphin) {
        if(serverDolphin.getAt(command.pmId) != null) {
            log.info("Ignoring create PM '$command.pmId' since it is already in the model store.")
            return
        }
        if (command.pmId.endsWith(ServerPresentationModel.AUTO_ID_SUFFIX)) {
            log.info("Creating the PM '$command.pmId' with reserved server-auto-suffix.")
        }
        List<ServerAttribute> attributes = new LinkedList()
        for (Map<String, Object> attr in command.attributes) {
            ServerAttribute attribute = new ServerAttribute((String) attr.propertyName, attr.value, (String) attr.qualifier, Tag.tagFor[(String) attr.tag])
            attribute.id = attr.id
            attributes << attribute
        }
        PresentationModel model = new ServerPresentationModel(command.pmId, attributes, serverDolphin.serverModelStore)
        model.presentationModelType = command.pmType
        if (serverDolphin.serverModelStore.containsPresentationModel(model.id)) {
            log.info("There already is a PM with id ${model.id}. Create PM ignored.")
        } else {
            serverDolphin.serverModelStore.add(model)
        }
    }
}
