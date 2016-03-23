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

import groovy.lang.Closure;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asType;

public abstract class AbstractDolphin<A extends Attribute, P extends PresentationModel<A>> implements Dolphin<A, P> {

    protected abstract ModelStore<A, P> getModelStore();

    /**
     * Adds a presentation model to the model store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     */
    public boolean add(P model) {
        return getModelStore().add(model);
    }

    /**
     * Removes a presentation model from the model store.<br/>
     *
     * @param model the model to be removed from the store.
     * @return if the remove operation was successful or not.
     */
    public boolean remove(P model) {
        return getModelStore().remove(model);
    }

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return an attribute whose id matches the parameter, {@code null} otherwise.
     */
    public A findAttributeById(String id) {
        return (A)getModelStore().findAttributeById(id);
    }

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns null. The returned {@code List} is immutable.
     *
     * @return a {@code List} of all attributes for which their qualifier was a match.
     */
    public List<A> findAllAttributesByQualifier(String qualifier) {
        return (List<A>)getModelStore().findAllAttributesByQualifier(qualifier);
    }



    public Set<String> listPresentationModelIds() {
        return getModelStore().listPresentationModelIds();
    }

    public Collection<P> listPresentationModels() {
         return (Collection<P>) getModelStore().listPresentationModels();
    }

    public List<P> findAllPresentationModelsByType(String presentationModelType) {
        return (List<P>) getModelStore().findAllPresentationModelsByType(presentationModelType);
    }

    /**
     * alias for findPresentationModelById
     */
    public P getAt(String id) {
        return findPresentationModelById(id);
    }

    public P findPresentationModelById(String id) {
        return (P) getModelStore().findPresentationModelById(id);
    }

    public void removeModelStoreListener(ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(listener);
    }

    public void removeModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(presentationModelType, listener);
    }

    public boolean hasModelStoreListener(ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(listener);
    }

    public void addModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().addModelStoreListener(presentationModelType, listener);
    }

    public void addModelStoreListener(String presentationModelType, Closure listener) {
        getModelStore().addModelStoreListener(presentationModelType, asType(listener, ModelStoreListener.class));
    }

    public boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(presentationModelType, listener);
    }

    public void addModelStoreListener(ModelStoreListener listener) {
        getModelStore().addModelStoreListener(listener);
    }

    public void addModelStoreListener(Closure listener) {
        getModelStore().addModelStoreListener(asType(listener, ModelStoreListener.class));
    }

    /**
     * For every attribute in the given presentation model, proliferate the attribute value to
     * all attributes that bear the same qualifier and tag.
     */
    // todo dk: not quite sure whether this should be called automatically in some handle() methods
    public void updateQualifiers(P presentationModel) {
        for (A source : presentationModel.getAttributes()) {
            if (null == source.getQualifier()) continue;
            for (A target : getModelStore().findAllAttributesByQualifier(source.getQualifier())) {
                if (target.getTag() != source.getTag()) continue;
                target.setValue(source.getValue());
            }
        }
    }
}
