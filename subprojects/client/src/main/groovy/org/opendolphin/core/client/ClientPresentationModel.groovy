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

package org.opendolphin.core.client

import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.Tag
import groovy.transform.CompileStatic

@CompileStatic
final class ClientPresentationModel extends BasePresentationModel<ClientAttribute> {

    public  static final String AUTO_ID_SUFFIX = "-AUTO-CLT"
    boolean clientSideOnly = false
    private static long instanceCount = 0

    ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes)
    }

    /**
     * @param id if id is null or empty, an auto-generated id will be used
     */
    ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(id ?: "" + instanceCount++ + AUTO_ID_SUFFIX, attributes)
        if (id?.endsWith(AUTO_ID_SUFFIX)) {
            throw new IllegalArgumentException("presentation model with self-provided id '$id' may not end with suffix '$AUTO_ID_SUFFIX' since that is reserved.")
        }
    }
}
