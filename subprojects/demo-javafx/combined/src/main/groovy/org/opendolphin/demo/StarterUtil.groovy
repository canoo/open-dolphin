package org.opendolphin.demo

import org.opendolphin.LogConfig
import org.opendolphin.core.ModelStoreConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientDolphinFactory
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.GClientDolphin
import org.opendolphin.core.client.comm.BlindCommandBatcher
import org.opendolphin.core.client.comm.HttpClientConnector
import org.opendolphin.core.comm.JsonCodec

class StarterUtil {

    static ClientDolphin setupForRemote() {
        LogConfig.logCommunication()
        def dolphin = ClientDolphinFactory.create()
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
        return dolphin
    }
}
