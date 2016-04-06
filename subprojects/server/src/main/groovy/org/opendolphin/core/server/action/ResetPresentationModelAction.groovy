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
package org.opendolphin.core.server.action

import org.opendolphin.core.ModelStore
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.ResetPresentationModelCommand
import org.opendolphin.core.server.comm.ActionRegistry

// todo: check whether this is needed at all or superseded by the MetaDataChangeCommand
class ResetPresentationModelAction implements ServerAction {
    private final ModelStore modelStore

    ResetPresentationModelAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    void registerIn(ActionRegistry registry) {
        registry.register(ResetPresentationModelCommand) { ResetPresentationModelCommand command, response ->
            PresentationModel model = modelStore.findPresentationModelById(command.pmId)
            // todo: trigger application specific persistence
            // todo: deal with potential persistence errors
            response << doWithPresentationModel(model)
        }
    }

    Command doWithPresentationModel(PresentationModel model) {
        new PresentationModelResetedCommand(pmId: model.id)
    }
}
