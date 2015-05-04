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

import org.opendolphin.core.Tag;

import java.util.List;

public class ServerPresentationModelFactory {

    private ServerPresentationModelFactory() {
    }

    public static ServerAttribute create(String propertyName, Object initialValue) {
        return new GServerAttribute(propertyName, initialValue);
    }

    public static ServerAttribute create(String propertyName, Object baseValue, String qualifier, Tag tag) {
        return new GServerAttribute(propertyName, baseValue, qualifier, tag);
    }

    public static ServerPresentationModel create(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        return new GServerPresentationModel(id, attributes, serverModelStore);
    }

    public static ServerPresentationModel create(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore, String presentationModelType) {
        return new GServerPresentationModel(id, attributes, serverModelStore, presentationModelType);
    }
}
