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

package org.opendolphin.core.server

import groovy.util.logging.Log
import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.comm.SwitchPresentationModelCommand

//CompileStatic
@Log
class ServerPresentationModel extends BasePresentationModel<ServerAttribute> {

    public static final String AUTO_ID_SUFFIX = "-AUTO-SRV"

    public ServerModelStore modelStore

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    ServerPresentationModel(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        super(id ?: makeId(serverModelStore), attributes)
        if (id?.endsWith(AUTO_ID_SUFFIX)) {
            log.info("Creating a PM with self-provided id '$id' even though it ends with a reserved suffix.")
            // the IAE should no longer be thrown to better handle back notifications.
            // We may want to re-enable this as soon as commands and notifications are better separated.
//            throw new IllegalArgumentException("presentation model with self-provided id '$id' may not end with suffix '$AUTO_ID_SUFFIX' since that is reserved.")
        }
        modelStore = serverModelStore
    }

    static String makeId(ServerModelStore serverModelStore) {
        def newId = serverModelStore.pmInstanceCount++
        return "$newId"+AUTO_ID_SUFFIX
    }

    @Override
    void syncWith(PresentationModel sourcePresentationModel) {
        super.syncWith(sourcePresentationModel) // this may already trigger some value changes and metadata changes
        modelStore.currentResponse << new SwitchPresentationModelCommand(sourcePmId: sourcePresentationModel.id, pmId: id)
    }

    void addAttribute(ServerAttribute attribute) {
        _internal_addAttribute(attribute)
        modelStore.registerAttribute(attribute)
        DefaultServerDolphin.initAt(modelStore.currentResponse, id, attribute.propertyName, attribute.qualifier, attribute.value, attribute.tag)
    }

    public void rebase() {
        for (ServerAttribute attr : getAttributes()) {
            attr.rebase()
        }
    }
}
