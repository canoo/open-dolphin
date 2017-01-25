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

package org.opendolphin.core.server.comm;

import org.opendolphin.core.comm.Command;
import groovy.lang.Closure;

import java.util.*;

// todo: think about inlining this into receiver and use get/setProperty to ease registration
public class ActionRegistry {
    // todo: think about proper sizing and synchronization needs
    /** Implementation Note: the type for the values of the following Map could be
    *  List<CommandHandler<? extends Command>>
    * to be really precise. But since this class does not use the CommandHandlers besides registering them, especially
    * it does not call 'handleCommand(...)' on them, it does not matter which Commands it stores. Therefore the
    * type is defined as: List<CommandHandler>
    */
    private final Map<String, List<CommandHandler>> ACTIONS = new HashMap();

    public Map<String, List<CommandHandler>> getActions() {
        return Collections.unmodifiableMap(ACTIONS);
    }

    public void register(String commandId, Closure serverCommand) {
        register(commandId, new CommandHandlerClosureAdapter(serverCommand));
    }

    public void register(Class commandClass, Closure serverCommand) {
        register(Command.idFor(commandClass), new CommandHandlerClosureAdapter(serverCommand));
    }

    public void register(String commandId, CommandHandler serverCommand) {
        List<CommandHandler> actions = getActionsFor(commandId);
        if (!actions.contains(serverCommand)) {
            actions.add(serverCommand);
        }
    }

    public void register(Class commandClass, CommandHandler serverCommand) {
        register(Command.idFor(commandClass), serverCommand);
    }

    public List<CommandHandler> getAt(String commandId) {
        return getActionsFor(commandId);
    }

    public void unregister(String commandId, Closure serverCommand) {
        unregister(commandId, new CommandHandlerClosureAdapter(serverCommand));
    }

    public void unregister(Class commandClass, Closure serverCommand) {
        unregister(Command.idFor(commandClass), new CommandHandlerClosureAdapter(serverCommand));
    }

    public void unregister(String commandId, CommandHandler serverCommand) {
        List<CommandHandler> commandList = getActionsFor(commandId);
        commandList.remove(serverCommand);
    }

    public void unregister(Class commandClass, CommandHandler serverCommand) {
        unregister(Command.idFor(commandClass), serverCommand);
    }

    private List<CommandHandler> getActionsFor(String commandName) {
        List<CommandHandler> actions = ACTIONS.get(commandName);
        if (actions == null) {
            actions = new ArrayList<CommandHandler>();
            ACTIONS.put(commandName, actions);
        }

        return actions;
    }
}
