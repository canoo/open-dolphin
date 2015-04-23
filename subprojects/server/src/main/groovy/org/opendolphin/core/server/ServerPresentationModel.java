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

package org.opendolphin.core.server;

import org.opendolphin.core.PresentationModel;

/**
 * Defines a presentation model on server side. For more information see {@link org.opendolphin.core.PresentationModel} and
 * {@link org.opendolphin.core.server.ServerDolphin}
 */
public interface ServerPresentationModel extends PresentationModel<ServerAttribute> {

    /**
     * Synchronizes all attributes of the source with all matching attributes of this presentation model
     * @param sourcePresentationModel may not be null since this most likely indicates an error
     */
    void syncWith(ServerPresentationModel sourcePresentationModel);

    /**
     * Adds the given attribute to the presentation model
     * @param attribute the attribute
     */
    void addAttribute(ServerAttribute attribute);

    //TODO: Should this method be part of the interface?
    void rebase();

    //TODO: Should this method be part of the interface?
    ServerModelStore getServerModelStore();

}
