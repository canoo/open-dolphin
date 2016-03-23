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
package org.opendolphin.demo.team;

import org.opendolphin.core.server.DTO;

public class TeamEvent {

    enum Type { NEW, CHANGE, REBASE, REMOVE, RELEASE }

    public Type type;
    public String qualifier;
    public Object value;
    public DTO    dto;

    public TeamEvent(Type type, DTO dto) {
        this.type = type;
        this.dto = dto;
    }

    public TeamEvent(Type type, String qualifier, Object value) {
        this.type      = type;
        this.qualifier = qualifier;
        this.value     = value;
    }
}

