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

package org.opendolphin.core.server

import groovy.transform.CompileStatic

@CompileStatic
class DTO {
    List<Slot> slots

    DTO(List<Slot> newSlots) {
        slots = newSlots
    }

    DTO(Slot... newSlots) {
        slots = newSlots as LinkedList

    }

    /**
     * Create the representation that is used within commands.
     */
    List<Map<String, Object>> encodable() {
        (List<Map<String, Object>>) slots.collect(new LinkedList()) {Slot slot -> slot.toMap() }
    }

}
