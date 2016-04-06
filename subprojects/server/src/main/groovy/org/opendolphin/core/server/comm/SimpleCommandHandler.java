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
package org.opendolphin.core.server.comm;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;

import java.util.List;

/**
 * Convenience class for all command handlers that do not need any info
 * from the command nor access the response.
 */

public abstract class SimpleCommandHandler implements NamedCommandHandler {
    @Override
    public void handleCommand(NamedCommand command, List<Command> response) {
        handleCommand();
    }
    public abstract void handleCommand();
}
