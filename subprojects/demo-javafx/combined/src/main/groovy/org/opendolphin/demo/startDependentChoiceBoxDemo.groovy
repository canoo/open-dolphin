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

package org.opendolphin.demo

import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.DTO

def config = new JavaFxInMemoryConfig()

config.serverDolphin.action "fillFirst", { cmd, response ->
    DTO pm1 = new DTO(new Slot("value","even"))
    DefaultServerDolphin.presentationModelCommand(response,"First 1", null, pm1)
    DTO pm2 = new DTO(new Slot("value","odd"))
    DefaultServerDolphin.presentationModelCommand(response,"First 2", null, pm2)
}

config.serverDolphin.action "fillRelation", { cmd, response ->
    [0,2,4,6,8].each {
        DTO pm = new DTO(
            new Slot("first", "even" ),
            new Slot("second", "Second $it" )
        )
        DefaultServerDolphin.presentationModelCommand(response,null, "FirstSecondRelation", pm)
    }
    [1,3,5,7,9].each {
        DTO pm = new DTO(
            new Slot("first", "odd" ),
            new Slot("second", "Second $it" )
        )
        DefaultServerDolphin.presentationModelCommand(response,null, "FirstSecondRelation", pm)
    }
}

DependentChoiceBoxView.show(config.clientDolphin)