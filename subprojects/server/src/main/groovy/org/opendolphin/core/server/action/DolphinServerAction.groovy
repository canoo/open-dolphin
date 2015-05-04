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

import org.opendolphin.core.Tag
import org.opendolphin.core.comm.Command
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.GServerAttribute
import org.opendolphin.core.server.GServerDolphin
import groovy.transform.CompileStatic
import org.opendolphin.core.server.ServerAttribute

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */

@CompileStatic
abstract class DolphinServerAction implements ServerAction {
    GServerDolphin serverDolphin
    List<Command> dolphinResponse


    void presentationModel(String id, String presentationModelType, DTO dto) {
        GServerDolphin.presentationModel(dolphinResponse, id, presentationModelType, dto)
    }

    void changeValue(ServerAttribute attribute, value) {
        GServerDolphin.changeValue(dolphinResponse, attribute, value)
    }

    /** Convenience method for the InitializeAttributeCommand */
    void initAt(String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        GServerDolphin.initAt(dolphinResponse, pmId, propertyName, qualifier, newValue, tag)
    }
}
