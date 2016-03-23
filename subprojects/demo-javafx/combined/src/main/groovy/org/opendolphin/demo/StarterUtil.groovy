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
package org.opendolphin.demo

import org.opendolphin.LogConfig
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.comm.BlindCommandBatcher
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.comm.JsonCodec
import org.opendolphin.core.comm.ZippedJsonCodec

class StarterUtil {

    static ClientDolphin setupForRemote() {
        LogConfig.logCommunication()
        def dolphin = new ClientDolphin()
        ModelStoreConfig config = new ModelStoreConfig(
//            pmCapacity: 1024 * 8,
//            attributeCapacity: 1024 * 8 * 16,
//            typeCapacity: 2,
//            qualifierCapacity: 2,
            )
        dolphin.setClientModelStore(new ClientModelStore(dolphin, config))
        def url = System.properties.remote ?: "http://localhost:8080/dolphin-grails/dolphin/"
        println " connecting to  $url "
        println "use -Dremote=... to override"
        def batcher = new BlindCommandBatcher(deferMillis: 50, mergeValueChanges: false)
        def connector = new HttpClientConnector(dolphin, batcher, url)
        connector.codec = new JsonCodec()
        dolphin.clientConnector = connector

        org.opendolphin.demo.util.StarterUtil.macFontWorkaround()
        return dolphin
    }
}
