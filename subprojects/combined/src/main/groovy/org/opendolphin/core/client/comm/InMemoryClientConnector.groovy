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

package org.opendolphin.core.client.comm

import org.opendolphin.core.comm.Command
import groovy.util.logging.Log
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.server.ServerConnector

@Log
class InMemoryClientConnector extends ClientConnector {

    def sleepMillis = 0
    final ServerConnector serverConnector // must be injected since the class is only available in a "combined" context

    InMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector) {
        super(clientDolphin)
        this.serverConnector = serverConnector
    }

    InMemoryClientConnector(ClientDolphin clientDolphin, ServerConnector serverConnector, ICommandBatcher commandBatcher) {
        super(clientDolphin, commandBatcher)
        this.serverConnector = serverConnector
    }

    @Override
    List<Command> transmit(List<Command> commands) {
        log.finest "transmitting ${commands.size()} commands"
        if (!serverConnector) {
            log.warning "no server connector wired for in-memory connector"
            return Collections.EMPTY_LIST
        }
        if (sleepMillis) sleep sleepMillis
        def result = new LinkedList<Command>()
        for (Command command : commands) {
            log.finest "processing $command"
            result.addAll(serverConnector.receive(command)) // there is no need for encoding since we are in-memory
        }
        result
    }

}
