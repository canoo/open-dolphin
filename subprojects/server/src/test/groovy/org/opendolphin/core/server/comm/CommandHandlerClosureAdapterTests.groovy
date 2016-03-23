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
package org.opendolphin.core.server.comm

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.NamedCommand

class CommandHandlerClosureAdapterTests extends GroovyTestCase {

    void testEqualsAndHashcodeImplementation() {
        def closureOne = { Command cmd, List<Command> response -> response << new NamedCommand('result')}
        def closureTwo = { Command cmd, List<Command> response -> }
        CommandHandlerClosureAdapter adapter = new CommandHandlerClosureAdapter(closureOne)
        assert adapter.equals(new CommandHandlerClosureAdapter(closureOne))
        assert adapter.equals(adapter)
        assert !adapter.equals(new CommandHandlerClosureAdapter(closureTwo))
        assert !adapter.equals('')
        assert !adapter.equals(null)
        assert adapter.hashCode() == closureOne.hashCode()
    }

    void testCallClosure() {
        def closureOne = { Command cmd, List<Command> response -> response << new NamedCommand('result')}
        CommandHandlerClosureAdapter adapter = new CommandHandlerClosureAdapter(closureOne)
        def response = []
        adapter.handleCommand(new NamedCommand('cmd'), response)
        assert 1 == response.size()
        assert 'result' == response.first().id
        assert closureOne == adapter.getClosure()
    }


}
