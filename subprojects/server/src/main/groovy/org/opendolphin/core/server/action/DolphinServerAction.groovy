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

package org.opendolphin.core.server.action

import org.opendolphin.core.Tag
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.InitializeAttributeCommand
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

    /**
     * @Deprecated use presentationModelCommand. Will be removed in 1.0 final!
     */
    void presentationModel(String id, String presentationModelType, DTO dto) {
        presentationModelCommand(id, presentationModelType, dto)
    }

    /**
     * Advises the client to create a presentation model.
     * Does <b>not</b> create a presentation model instantly such that it would appear in the model store right
     * after calling this method! This only happens after client roundtrip.
     */
    void presentationModelCommand(String id, String presentationModelType, DTO dto) {
        DefaultServerDolphin.presentationModelCommand(dolphinResponse, id, presentationModelType, dto)
    }


    /**
     * @Deprecated use changeValueCommand. Will be removed in 1.0 final!
     */
    void changeValue(ServerAttribute attribute, value) {
        changeValueCommand(attribute, value)
    }

    /**
     * Advises the client to set the attribute to the provided value.
     * Does <b>not</b> change the value instantly such that the new value would be instantly visible or value
     * change listeners would be triggered! This only happens after client roundtrip.
     */
    void changeValueCommand(ServerAttribute attribute, value) {
        DefaultServerDolphin.changeValueCommand(dolphinResponse, attribute, value)
    }

    /** Convenience method for the InitializeAttributeCommand */
    void initAt(String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        DefaultServerDolphin.initAt(dolphinResponse, pmId, propertyName, qualifier, newValue, tag)
    }
}
