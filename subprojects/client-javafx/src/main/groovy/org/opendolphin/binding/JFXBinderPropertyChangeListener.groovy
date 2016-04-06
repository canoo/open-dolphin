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

import groovy.transform.Canonical
import org.opendolphin.core.Attribute

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

@Canonical
class JFXBinderPropertyChangeListener implements PropertyChangeListener {
    Attribute attribute
    Object target
    String targetPropertyName
    Converter converter

    void update() {
        target[targetPropertyName] = convert(attribute.value)
    }

    void propertyChange(PropertyChangeEvent evt) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }
    // we have equals(o) and hashCode() from @Canonical
}
