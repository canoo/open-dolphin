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
import static org.opendolphin.demo.MyProps.ATT.*

def config = new JavaFxInMemoryConfig()
def dolphin = config.serverDolphin

// example for setting tags at startup
dolphin.action "init", { cmd, response ->
    dolphin.initAt response, 'person', NAME,     null, "First name: ", Tag.LABEL
    dolphin.initAt response, 'person', LASTNAME, null, "Last name: ",  Tag.LABEL

    dolphin.initAt response, 'person', NAME, null, ".*a.*",  Tag.REGEX
    dolphin.initAt response, 'person', NAME, null, "must contain an 'a' ", Tag.TOOLTIP
}

// example for changing tags at runtime
dolphin.action "german", { cmd, response ->
    def model = dolphin.getAt('person')
    dolphin.changeValueCommand response, model.getAt(NAME,     Tag.LABEL), "Vorname: "
    dolphin.changeValueCommand response, model.getAt(LASTNAME, Tag.LABEL), "Nachname: "
    dolphin.changeValueCommand response, model.getAt(NAME,     Tag.TOOLTIP), "muss ein 'a' enthalten "
}

new AttributeTagView().show(config.clientDolphin)