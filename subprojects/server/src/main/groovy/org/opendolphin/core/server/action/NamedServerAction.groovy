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

import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.NamedCommandHandler

/**
 * Java-friendly action handling
 */

class NamedServerAction extends DolphinServerAction {
    final String name
    final NamedCommandHandler namedCommandHandler

    NamedServerAction(String name, NamedCommandHandler namedCommandHandler) {
        this.name = name
        this.namedCommandHandler = namedCommandHandler
    }

    @Override
    void registerIn(ActionRegistry registry) {
        registry.register(name, namedCommandHandler)
    }
}
