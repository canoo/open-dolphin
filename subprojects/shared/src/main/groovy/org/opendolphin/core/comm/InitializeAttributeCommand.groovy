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

package org.opendolphin.core.comm

import org.opendolphin.core.Tag
import groovy.transform.TupleConstructor

@TupleConstructor
class InitializeAttributeCommand extends Command {

    String pmId
    String propertyName
    String qualifier
    def    newValue
    String pmType
    Tag    tag = Tag.VALUE

    String toString() { super.toString() + " pm '$pmId' pmType'$pmType' property '$propertyName' initial value '$newValue' qualifier $qualifier tag $tag" }
}
