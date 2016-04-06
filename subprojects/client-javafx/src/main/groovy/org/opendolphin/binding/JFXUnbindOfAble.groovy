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

import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

class JFXUnbindOfAble {
    private String sourcePropertyName

    JFXUnbindOfAble(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName
    }

    JFXUnbindFromAble of(javafx.scene.Node source) {
        new JFXUnbindFromAble(source, sourcePropertyName)
    }

    UnbindFromAble of(PresentationModel source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }

    UnbindClientFromAble of(ClientPresentationModel source) {
        new UnbindClientFromAble((ClientAttribute) source.findAttributeByPropertyName(sourcePropertyName))
    }

    UnbindPojoFromAble of(Object source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }
}
