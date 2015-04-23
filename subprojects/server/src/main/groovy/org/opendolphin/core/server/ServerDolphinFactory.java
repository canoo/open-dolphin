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

/**
 * Factory that creates a server dolphin instance. Application developers should always use this method to create a dolphin on server side.
 */
public class ServerDolphinFactory {

    private ServerDolphinFactory() {}

    /**
     * Creates a new server dolphin instance
     * @return the new server dolphin instance
     */
    public static ServerDolphin create() {
        return new GServerDolphin();
    }

    /**
     * Creates a new server dolphin instance that is configured by the given {@link org.opendolphin.core.server.ServerModelStore} and {@link org.opendolphin.core.server.ServerConnector}
     * @param serverModelStore THE MODEL STORE
     * @param serverConnector THE CONNECTOR
     * @return the new server dolphin instance
     */
    public static ServerDolphin create(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        ServerAttribute a = create().createAttribute("", "");
        a.getPresentationModel();
        return new GServerDolphin(serverModelStore, serverConnector);
    }
}
