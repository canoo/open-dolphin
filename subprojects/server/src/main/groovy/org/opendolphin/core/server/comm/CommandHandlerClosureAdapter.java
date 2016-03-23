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
import groovy.lang.Closure;

import java.util.List;

public class CommandHandlerClosureAdapter implements CommandHandler<Command> {
    private final Closure closure;

    public CommandHandlerClosureAdapter(Closure closure) {
        this.closure = closure;
    }

    public Closure getClosure() {
        return closure;
    }

    @Override
    public void handleCommand(Command command, List<Command> response) {
        closure.call(command, response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommandHandlerClosureAdapter that = (CommandHandlerClosureAdapter) o;

        if (!closure.equals(that.closure)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return closure.hashCode();
    }
}
