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
package org.opendolphin.binding;

import javafx.scene.Node;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientPresentationModel;

public class JFXBindOfAble {
    public JFXBindToAble of(Node source) {
        return new JFXBindToAble(source, sourcePropertyName);
    }

    public BindToAble of(PresentationModel source) {
        return Binder.bind(sourcePropertyName, tag).of(source);
    }

    public BindClientToAble of(ClientPresentationModel source) {
        return new BindClientToAble((ClientAttribute) source.findAttributeByPropertyNameAndTag(sourcePropertyName, tag));
    }

    public BindPojoToAble of(Object source) {
        return Binder.bind(sourcePropertyName, tag).of(source);
    }

    public JFXBindOfAble(String sourcePropertyName, Tag tag) {
        this.sourcePropertyName = sourcePropertyName;
        this.tag = tag;
    }

    private final String sourcePropertyName;
    private final Tag tag;
}
