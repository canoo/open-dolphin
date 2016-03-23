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
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.opendolphin.core.PresentationModel

@Canonical
class JFXBinderChangeListener implements ChangeListener {
    javafx.scene.Node source
    String sourcePropertyName
    Object target
    String targetPropertyName
    Converter converter

    void update() {
        if (target instanceof PresentationModel) {
            target[targetPropertyName].value = convert(source[sourcePropertyName])
        } else {
            target[targetPropertyName] = convert(source[sourcePropertyName])
        }
    }

    void changed(ObservableValue oe, Object oldValue, Object newValue) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }

    // we have equals(o) and hashCode() from @Canonical

}
