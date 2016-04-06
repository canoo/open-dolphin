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
package org.opendolphin.core.client;

import javafx.beans.property.SimpleObjectProperty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

/**
 * <p>JavaFX property wrapper around an attribute.</p>
 */
public class ClientAttributeWrapper extends SimpleObjectProperty<Object> {
    private final WeakReference<ClientAttribute> attributeRef;
    private final String name;

    public ClientAttributeWrapper(ClientAttribute attribute) {
        this.attributeRef = new WeakReference<ClientAttribute>(attribute);
        // we cache the attribute's propertyName as the property's name
        // because the value does not change and we want to avoid
        // dealing with null values from WR
        this.name = attribute.getPropertyName();
        attribute.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                fireValueChangedEvent();
            }
        });
        // the dirtyness may also change and shall call a re-render as the consumer may rely on that
        attribute.addPropertyChangeListener("dirty", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                fireValueChangedEvent();
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void set(Object value) {
        ClientAttribute attribute = attributeRef.get();
        if (attribute != null) attribute.setValue(value);
    }

    @Override
    public Object get() {
        ClientAttribute attribute = attributeRef.get();
        return attribute != null ? attribute.getValue() : null;
    }
}
