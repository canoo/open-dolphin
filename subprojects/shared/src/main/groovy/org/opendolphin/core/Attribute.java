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

/**
 * Defines an attribute in a presentation model. Each attribute can store a value and is identified by its property name,
 * qualifier and tag.
 */
public interface Attribute extends Observable {

    /**
     * Defines the property name for the qualifier property of an attribute. This can be used to register a {@link java.beans.PropertyChangeListener} for example.
     */
    public static final String QUALIFIER_PROPERTY   = "qualifier";

    /**
     * Defines the property name for the dirty property of an attribute. This can be used to register a {@link java.beans.PropertyChangeListener} for example.
     */
    public static final String DIRTY_PROPERTY       = "dirty";

    /**
     * Defines the property name for the baseValue property of an attribute. This can be used to register a {@link java.beans.PropertyChangeListener} for example.
     */
    public static final String BASE_VALUE           = "baseValue";

    /**
     * Defines the property name for the value property of an attribute. This can be used to register a {@link java.beans.PropertyChangeListener} for example.
     */
    public static final String VALUE                = "value";

    /**
     * Returns the value of the attribute
     * @return the value
     */
    Object getValue();

    /**
     * Sets the value of the attribute
     * @param value the new value
     */
    void setValue(Object value);

    /**
     * Returns the property name of the attribute
     * @return
     */
    String getPropertyName();

    /**
     * Returns the qualifier of the attribute
     * @return
     */
    String getQualifier();

    //TODO: Should we remove this method from the interface? The Id is an internal value
    String getId();

    /**
     * Returns the tag of the attribute
     * @return the tag
     */
    Tag getTag();

    /**
     * Synchronizes this attribute with the given one
     * @param source the attribute
     */
    void syncWith(Attribute source);

    //TODO: Should we remove this method from the interface?
    boolean isDirty();

    /**
     * Returns the base value of this attribute
     * @return the base value
     */
    Object getBaseValue();

    //TODO: Should we remove this method from the interface?
    void   setBaseValue(Object newValue);

    /**
     * Returns the presentation model that contains this attribute or {@code null} if this attribute isn't part of a
     * presentation model.
     * @return the presentation model
     */
    PresentationModel getPresentationModel();

    /**
     * Setting the base value to the current value, effectively providing a new base for "dirty" calculations
     */
    //TODO: Should we remove this method from the interface?
    void rebase();

    /**
     * Setting the current value back to the last known base, which is the base value
     */
    //TODO: Should we remove this method from the interface?
    void reset();
}