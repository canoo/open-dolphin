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

import org.opendolphin.core.Tag
import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.DefaultServerDolphin

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */

//CompileStatic
abstract class DolphinServerAction implements ServerAction {
    DefaultServerDolphin serverDolphin
    List<Command> dolphinResponse


    void presentationModel(String id, String presentationModelType, DTO dto) {
        DefaultServerDolphin.presentationModelCommand(dolphinResponse, id, presentationModelType, dto)
    }

    void changeValue(ServerAttribute attribute, value) {
        DefaultServerDolphin.changeValueCommand(dolphinResponse, attribute, value)
    }

    /** Convenience method for the InitializeAttributeCommand */
    void initAt(String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        DefaultServerDolphin.initAt(dolphinResponse, pmId, propertyName, qualifier, newValue, tag)
    }
}
