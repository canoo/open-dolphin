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
package org.opendolphin.core.comm

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StringGroovyMethods

// todo dk: go through all subtypes and apply the new naming convention

/**
 * Commands come in two flavors: *Command (active voice) and *Notification (passive voice).
 * A *Command instructs the other side to do something.
 * A *Notification informs the other side that something has happened.
 * Typically, the server sends commands to the client,
 * the client sends notifications to the server with the notable exception of NamedCommand.
 * Neither commands nor notifications contain any logic themselves.
 * They are only "DTOs" that are sent over the wire.
 * The receiving side is responsible for finding the appropriate action.
 */

//CompileStatic
class Command {

    String getId() { idFor this.class }

    static String idFor(Class commandClass) {
        commandClass.name - commandClass.package.name - "." - "Command" - "Notification"
    }

    String toString() { "Command: $id" }
}
