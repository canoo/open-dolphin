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

import java.util.List;

/**
 * Defines a presentation model. A presentation model is identified by its unique id and a model type. The model type
 * can ben used to define logical groups and types of presentation models.
 * Each presentation models contains a list of {@link org.opendolphin.core.Attribute} instances.
 * @param <A> type of the internal {@link org.opendolphin.core.Attribute} instances.
 */
public interface PresentationModel<A extends Attribute> extends Observable {

    /**
     * Defines the property name for the dirty property of an presentation model. This can be used to register a {@link java.beans.PropertyChangeListener} for example.
     */
    public static final String DIRTY_PROPERTY = "dirty";

    /**
     * Returns the id of the presentation model
     * @return the id
     */
    String getId();

    /**
     * Returns an immutable {@code List} of all {@code Attribute} instances
     * @return the immutable internal representation
     */
    List<A> getAttributes();

    /**
     * Returns the {@code Attribute} with the given property name and the {@link org.opendolphin.core.Tag#VALUE} tag.
     * The method will return {@code null} if no {@code Attribute} can be found
     * @param propertyName the {@code Attribute} property name
     * @return the presentation model or {@code null}
     */
    //TODO: should we remove this method? It's duplicated by findAttributeByPropertyName
    A getAt(String propertyName);

    /**
     * Returns the {@code Attribute} with the given property name and the given {@code Tag}.
     *  The method will return {@code null} if no {@code Attribute} can be found
     * @param propertyName the property name
     * @param tag the tag
     * @return the {@code Attribute} or {@code null}
     */
    //TODO: should we remove this method? It's duplicated by findAttributeByPropertyNameAndTag
    A getAt(String propertyName, Tag tag);

    //TODO: should we remove this method? Otherwise we need to define similar methods for all other base types
    int getValue(String attributeName, int defaultValue);

    /**
     * Returns the {@code Attribute} with the given property name and the {@link org.opendolphin.core.Tag#VALUE} tag.
     * The method will return {@code null} if no or {@code Attribute} can be found
     * @param propertyName the presentation model property name
     * @return the {@code Attribute} or {@code null}
     */
    A findAttributeByPropertyName(String propertyName);

    /**
     * Returns an immutable {@code List} of all attributes with the given property name.
     * The method will return an empty list if no {@code Attribute} can be found
     * @param propertyName the attribute property name
     * @return the found attributes
     */
    List<A> findAllAttributesByPropertyName(String propertyName);

    /**
     * Returns the {@code Attribute} with the given property name and tag.
     * The method will return {@code null} if no attribute can be found
     * @param propertyName the property name
     * @param tag the tag
     * @return the found {@code Attribute} or {@code null}
     */
    A findAttributeByPropertyNameAndTag(String propertyName, Tag tag);

    /**
     * Returns the {@code Attribute} with the given qualifer.
     * The method will return {@code null} if no attribute can be found
     * @param qualifier the qualifier
     * @return the found {@code Attribute} or {@code null}
     */
    A findAttributeByQualifier(String qualifier);

    //TODO: should we remove this method? The id is an internal definition.
    A findAttributeById(String id);

    /**
     * Synchronizes all attributes of the source with all matching attributes of this presentation model
     * @param sourcePresentationModel may not be null since this most likely indicates an error
     */
    void syncWith(PresentationModel other);

    /**
     * Returns the type of this presentation model
     * @return the presentation model type
     */
    String getPresentationModelType();

    //TODO: Should this method be part of the interface?
    void setPresentationModelType(String type);

    /**
     * Warning: should only be called from the open-dolphin command layer, not from applications,
     * since it does not register all required listeners. Consider using ClientDolphin.addAttributeToModel().
     * @param attribute
     */
    //TODO: This method must be removed
    void _internal_addAttribute(A attribute);

    //TODO: JavaDoc is needed
    boolean isDirty();

    //TODO: Should this method be part of the interface?
    void    updateDirty();
}
