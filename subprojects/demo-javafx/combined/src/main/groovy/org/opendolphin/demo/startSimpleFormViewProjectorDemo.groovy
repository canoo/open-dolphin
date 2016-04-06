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

import org.opendolphin.core.Tag
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.demo.projector.SimpleFormView

import static org.opendolphin.demo.MyProps.ATT.*

def config = new JavaFxInMemoryConfig()
def dolphin = config.serverDolphin
println dolphin

// example for setting tags at startup
dolphin.action "init", { cmd, response ->

    dolphin.presentationModel('person', null, new DTO(
        new Slot(NAME,      'John'),
        new Slot(NAME,      "First name: ",         null, Tag.LABEL),
        new Slot(NAME,      ".*a.*",                null, Tag.REGEX),
        new Slot(NAME,      "must contain an 'a' ", null, Tag.TOOLTIP),

        new Slot(LASTNAME,  'Smith'),
        new Slot(LASTNAME,  "Last name: ",          null, Tag.LABEL),
    ))

    dolphin.presentationModel('person.actions', null, new DTO( // we could also use pm types
        new Slot('submit',  'person.toGerman'),      // the action to be triggered on submit
        new Slot('submit',  "sets all labels to german ", null, Tag.TOOLTIP),
        new Slot('submit',  "German",                null, Tag.LABEL),

        new Slot('reset',   'person.reset'),         // the action to be triggered on reset
        new Slot('reset',   "setting contents and labels back to english ", null, Tag.TOOLTIP),
        new Slot('reset',   "Reset",                 null, Tag.LABEL),
    ))
}

// example for changing tags at runtime
dolphin.action "person.toGerman", { cmd, response ->
    def person = dolphin.getAt('person')
    person.getAt(NAME,     Tag.LABEL   ).value = "Vorname: "
    person.getAt(LASTNAME, Tag.LABEL   ).value = "Nachname: "
    person.getAt(NAME,     Tag.TOOLTIP ).value = "muss ein 'a' enthalten "
}
dolphin.action "person.reset", { cmd, response ->
    dolphin.getAt('person')?.reset()
}

new SimpleFormView().show(config.clientDolphin)