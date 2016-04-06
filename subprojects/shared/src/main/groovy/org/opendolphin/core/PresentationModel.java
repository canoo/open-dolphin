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
package org.opendolphin.core;

import java.util.List;

/**
 * A presentation model is uniquely identified by a string ID, and consists of a list of attributes.
 * The presentation model may also be given a type.
 * @param <A>
 * @see Attribute
 * @see Tag
 */
public interface PresentationModel<A extends Attribute> extends Observable {
    String DIRTY_PROPERTY = "dirty";

    /**
     *
     * @return the presentation model's unique ID.
     */
    String getId();

    /**
     *
     * @return the presentation model's list of attributes.
     */
    List<A> getAttributes();

    /**
     * Convenience (shorthand) method for finding a value attribute by property name.
     * @see #findAttributeByPropertyName(String)
     * @param propertyName attribute's property name
     * @return value attribute for the given property; null if non-existent
     */
    A getAt(String propertyName);

    /**
     * Convenience (shorthand) method for finding an attribute with the specified tag by property name.
     * @see #findAttributeByPropertyName(String)
     * @param propertyName attribute's property name
     * @param tag          attribute's tag
     * @return attribute for the given property; null if non-existent
     * @see #findAttributeByPropertyNameAndTag(String, Tag)
     */
    A getAt(String propertyName, Tag tag);

    /**
     * Convenience method to get the value of an attribute if it exists, or a default value if it does not.
     */
    public int getValue(String attributeName, int defaultValue);

    /**
     * @param propertyName attribute's property name
     * @return value attribute for the given property; null if non-existent
     */
    A findAttributeByPropertyName(String propertyName);

    /**
     * Retusn a list of all attributes with the given property name.  Each attribute will have a different tag value.
     * @param propertyName attribute's property name
     * @return list of all attributes with the given property name.
     */
    List<A> findAllAttributesByPropertyName(String propertyName);

    /**
     *
     * @param propertyName attribute's property name
     * @param tag          attribute's tag
     * @return attribute for the given property; null if non-existent
     */
    A findAttributeByPropertyNameAndTag(String propertyName, Tag tag);

    /**
     * Returns the first attribute whose qualifier matches the supplied value.
     */
    A findAttributeByQualifier(String qualifier);

    /**
     * Returns the attribute whose ID matches the supplied value.
     */
    A findAttributeById(String id);

    /**
     * Copies (synchronizes) all attributes from the other presentation model to this model.
     * @see Attribute#syncWith(Attribute)
     */
    void syncWith(PresentationModel other);

    /**
     * Returns the type (a String value) of the presentation model.  The type defaults to null.
     * @return
     */
    String getPresentationModelType();

    /**
     * Warning: should only be called from the open-dolphin command layer, not from applications,
     * since it does not register all required listeners. Consider using ClientDolphin.addAttributeToModel().
     * @param attribute
     */
    void _internal_addAttribute(A attribute);

    /**
     *
     * @return true if any attributes are dirty.
     * @see Attribute#isDirty()
     */
    boolean isDirty();

    /**
     * Sets the presentation model's dirty flag if any of its attributes are dirty, else clears the dirty flag.
     */
    void    updateDirty();
}
