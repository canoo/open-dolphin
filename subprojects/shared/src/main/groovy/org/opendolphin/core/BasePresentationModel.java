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

import groovy.lang.MissingPropertyException;
import groovy.util.Eval;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A BasePresentationModel is a collection of {@link BaseAttribute}s.
 * PresentationModels are not meant to be extended for the normal use, i.e. you typically don't need something like
 * a specialized "PersonPresentationModel" or so.
 */

public class BasePresentationModel<A extends Attribute> extends AbstractObservable implements PresentationModel<A> {
    protected final List<A> attributes = new LinkedList<A>();
    private final String id;
    private       String presentationModelType;
    private boolean dirty = false;

    /**
     * @throws AssertionError if the list of attributes is null or empty
     */
    public BasePresentationModel(String id, List<A> attributes) {
        this.id = id;
        for (A attr : attributes) {
            _internal_addAttribute(attr);
        }
    }

    public void updateDirty() {
        for (A attr : attributes) {
            if (attr.getTag() == Tag.VALUE && attr.isDirty()) {
                setDirty(true);
                return;
            }
        }
        setDirty(false);
    }

    public void _internal_addAttribute(A attribute) {
        if (null == attribute || attributes.contains(attribute)) return;
        if (null != findAttributeByPropertyNameAndTag(attribute.getPropertyName(), attribute.getTag())) {
            throw new IllegalStateException("There already is an attribute with property name '"
                                            + attribute.getPropertyName()
                                            + "' and tag '" + attribute.getTag()
                                            + "' in presentation model with id '" + this.id + "'.");
        }
        if (attribute.getQualifier() != null && this.findAttributeByQualifier(attribute.getQualifier()) != null) {
            throw  new IllegalStateException("There already is an attribute with qualifier '" + attribute.getQualifier()
                    + "' in presentation model with id '" + this.id + "'.");
        }
        ((BaseAttribute)attribute).setPresentationModel(this);
        attributes.add(attribute);
        if (attribute.getTag() == Tag.VALUE) updateDirty(); // the new attribute may be dirty
    }

    public String getId() {
        return id;
    }

    public String getPresentationModelType() {
        return presentationModelType;
    }

    public void setPresentationModelType(String presentationModelType) {
        this.presentationModelType = presentationModelType;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        firePropertyChange(DIRTY_PROPERTY, this.dirty, this.dirty = dirty);
    }

    public void reset() {
        for (A attr : attributes) {
            attr.reset();
        }
    }

    public void rebase() {
        for (A attr : attributes) {
            attr.rebase();
        }
    }

    /**
     * @return the immutable internal representation
     */
    public List<A> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public A getAt(String propertyName) {
        return findAttributeByPropertyName(propertyName);
    }

    // todo dk: overload with types for defaultValue

    /**
     * Convenience method to get the value of an attribute if it exists or a default value otherwise.
     */
    public int getValue(String attributeName, int defaultValue) {
        A attribute = getAt(attributeName);
        Object attributeValue = (attribute == null) ? null : attribute.getValue();
        return (attributeValue == null) ? defaultValue : Integer.parseInt(attributeValue.toString());
    }

    public A getAt(String propertyName, Tag tag) {
        return findAttributeByPropertyNameAndTag(propertyName, tag);
    }

    public A findAttributeByPropertyName(String propertyName) {
        return findAttributeByPropertyNameAndTag(propertyName, Tag.VALUE);
    }


    public List<A> findAllAttributesByPropertyName(String propertyName) {
        List<A> result = new LinkedList<A>();
        if (null == propertyName) return result;
        for (A attribute : attributes) {
            if (propertyName.equals(attribute.getPropertyName())) {
                result.add(attribute);
            }
        }
        return result;
    }

    public A findAttributeByPropertyNameAndTag(String propertyName, Tag tag) {
        if (null == propertyName) return null;
        if (null == tag) return null;
        for (A attribute : attributes) {
            if (propertyName.equals(attribute.getPropertyName()) && tag.equals(attribute.getTag())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeByQualifier(String qualifier) {
        if (null == qualifier) return null;
        for (A attribute : attributes) {
            if (qualifier.equals(attribute.getQualifier())) {
                return attribute;
            }
        }
        return null;
    }

    public A findAttributeById(String id) {
        for (A attribute : attributes) {
            if (attribute.getId().equals(id)) {
                return attribute;
            }
        }
        return null;
    }

    public Object propertyMissing(String propName) {
        A result = findAttributeByPropertyName(propName);
        if (null == result) {
            String message = "The presentation model doesn't understand '" + propName + "'. \n";
            message += "Known attribute names are: " + Eval.x(attributes, "x.collect{it.propertyName}");
            throw new MissingPropertyException(message, propName, this.getClass());
        }
        return result;
    }

    /**
     * Synchronizes all attributes of the source with all matching attributes of this presentation model
     * @param sourcePresentationModel may not be null since this most likely indicates an error
     */
    public void syncWith(PresentationModel sourcePresentationModel) {
        for (A targetAttribute : attributes) {
            Attribute sourceAttribute = sourcePresentationModel.getAt(targetAttribute.getPropertyName(), targetAttribute.getTag());
            if (sourceAttribute != null) targetAttribute.syncWith(sourceAttribute);
        }
    }

}