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
package org.opendolphin.core.server

import org.opendolphin.core.Dolphin
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.NamedCommandHandler


public interface ServerDolphin extends Dolphin<ServerAttribute, ServerPresentationModel> {

    void registerDefaultActions()

    void register(DolphinServerAction action)

    void action(String name, NamedCommandHandler namedCommandHandler)

    ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto)

    void removeAllPresentationModelsOfType(String type)
}