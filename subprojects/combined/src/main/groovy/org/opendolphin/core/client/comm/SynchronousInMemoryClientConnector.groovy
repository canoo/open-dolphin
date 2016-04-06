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
package org.opendolphin.core.client.comm

import groovy.transform.InheritConstructors
import groovy.util.logging.Log
import org.opendolphin.core.comm.Command

/** An in-memory client connector without any asynchronous calls such that
 * technologies like GWT, Vaadin, or ULC can use it safely on the server side
 * without leaving the request thread.
 * It may also be useful for unit-testing purposes.
 */

@Log @InheritConstructors
class SynchronousInMemoryClientConnector extends InMemoryClientConnector {

    protected void startCommandProcessing() {
        /* do nothing! */
    }

    void send(Command command, OnFinishedHandler callback = null) {
        def answer = transmit([command])
        processResults(answer, [ new CommandAndHandler(command: command, handler: callback) ] )
    }
}
