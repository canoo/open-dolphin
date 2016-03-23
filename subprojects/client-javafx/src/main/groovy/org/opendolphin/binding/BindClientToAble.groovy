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
package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttribute

class BindClientToAble {
    final ClientAttribute attribute
    final Converter converter

    BindClientToAble(ClientAttribute attribute, Converter converter = null) {
        this.attribute = attribute
        this.converter = converter
    }

    BindClientOtherOfAble to(String targetPropertyName) {
        new BindClientOtherOfAble(attribute, targetPropertyName, converter)
    }

    BindClientToAble using(Closure converter) {
        using(new ConverterAdapter(converter))
    }

    BindClientToAble using(Converter converter) {
        return new BindClientToAble(attribute, converter)
    }

}
