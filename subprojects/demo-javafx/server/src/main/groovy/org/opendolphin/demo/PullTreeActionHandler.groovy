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
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.comm.NamedCommandHandler

class PullTreeActionHandler implements NamedCommandHandler {

    void handleCommand(NamedCommand command, List<Command> response) {
        // a stand-in for some arbitrary kind of tree in the application domain
        def domainTreeRoot = new NodeBuilder().earth {
            europe {
                switzerland {
                    basel()
                    zurich()
                }
                germany()
            }
            america()
            australia()
        }

        makePM(domainTreeRoot, response)
    }

    /**
     * Recursively encode the given node into a response list of commands.  There will be one
     * presentationModelCommand for every node in the tree; thus one presentation model will be created
     * for each node.  Each presentation model is named with the node's name (hence they must be unique,
     * or a different technique would be necessary -- using a unique identifier of some sort.)  And each
     * child PM carries an attribute named "parent" whose value is the parent's name, or null if the PM is
     * the root of the tree.
     *
     * @param node
     * @param response
     */
    private void makePM(node, List<Command> response) {
        DTO model = new DTO(new Slot("parent", node.parent()?.name()))
        DefaultServerDolphin.presentationModelCommand(response, node.name(), null, model)
        node.children().each {
            makePM it, response // recurse
        }
    }
}