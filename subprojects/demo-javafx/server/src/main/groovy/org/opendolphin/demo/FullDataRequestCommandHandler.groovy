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

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.DataCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.comm.NamedCommandHandler

class FullDataRequestCommandHandler implements NamedCommandHandler {

    private final int numEntries

    public FullDataRequestCommandHandler(int numEntries) {
        this.numEntries = numEntries
    }

    @Override
    void handleCommand(NamedCommand command, List<Command> response) {
        for (int i=0; i < numEntries; i++) {
            response.add(new DataCommand(new HashMap(id: i)))
        }
    }
}
