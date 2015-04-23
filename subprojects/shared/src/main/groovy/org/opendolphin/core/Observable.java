/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

package org.opendolphin.core;

import java.beans.PropertyChangeListener;

/**
 * Basic interface that adds support for {@link java.beans.PropertyChangeListener}.
 */
public interface Observable {

    /**
     * Registers the given listener
     * @param listener the listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Registers the given listener for a specific property
     * @param propertyName the name of the property
     * @param listener the listener
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Deregisters the given listener
     * @param listener the lister that should be deregistered
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Deregisters the given listener for a specific property
     * @param propertyName name of the property
     * @param listener the listener
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Returns an array of all registered listeners
     * @return array of all registered listeners
     */
    PropertyChangeListener[] getPropertyChangeListeners();

    /**
     * Returns an array of all registered listeners for the a specific property
     * @param propertyName the name of the property
     * @return array of all registered listeners
     */
    PropertyChangeListener[] getPropertyChangeListeners(String propertyName);
}
