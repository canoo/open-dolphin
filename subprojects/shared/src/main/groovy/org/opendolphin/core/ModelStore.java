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

import org.opendolphin.StringUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Central data structure to store presentation models and their attributes
 * both on the client (view) and on the server (controller) side for separate access.
 */

public class ModelStore<A extends Attribute, P extends PresentationModel<A>> {

    // We maintain four indexes in this data structure in order to efficiently access
    // - presentation models by id or by type
    // - attributes by id or by qualifier

    private final Map<String, P>        presentationModels;
    private final Map<String, List<P>>  modelsPerType;
    private final Map<String, A>                attributesPerId;
    private final Map<String, List<A>>          attributesPerQualifier;

    private final Set<ModelStoreListenerWrapper<A, P>> modelStoreListeners = new LinkedHashSet<ModelStoreListenerWrapper<A, P>>();

    private final PropertyChangeListener ATTRIBUTE_WORKER = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            A attribute = (A) event.getSource();
            String oldQualifier = (String) event.getOldValue();
            String newQualifier = (String) event.getNewValue();

            if (null != oldQualifier) removeAttributeByQualifier(attribute, oldQualifier);
            if (null != newQualifier) addAttributeByQualifier(attribute);
        }
    };


    public ModelStore() {
        this(new ModelStoreConfig());
    }

    public ModelStore(ModelStoreConfig config) {
        presentationModels      = new HashMap<String, P>        (config.getPmCapacity());
        modelsPerType           = new HashMap<String, List<P>>  (config.getTypeCapacity());
        attributesPerId         = new HashMap<String, A>                (config.getAttributeCapacity());
        attributesPerQualifier  = new HashMap<String, List<A>>          (config.getQualifierCapacity());
    }

    /**
     * Returns a {@code Set} of all known presentation model ids.<br/>
     * Never returns null. The returned {@code Set} is immutable.
     *
     * @return a {@code} Set of all ids of all presentation models contained in this store.
     */
    public Set<String> listPresentationModelIds() {
        return Collections.unmodifiableSet(presentationModels.keySet());
    }

    /**
     * Returns a {@code Collection} of all presentation models found in this store.<br/>
     * Never returns empty. The returned {@code Collection} is immutable.
     *
     * @return a {@code Collection} of all presentation models found in this store.
     */
    public Collection<P> listPresentationModels() {
        return Collections.unmodifiableCollection(presentationModels.values());
    }

    /**
     * Adds a presentation model to this store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     */
    public boolean add(P model) {
        if (null == model) return false;

        if (presentationModels.containsKey(model.getId())) {
            throw new IllegalArgumentException("There already is a PM with id " + model.getId());
        }
        boolean added = false;
        if (!presentationModels.containsValue(model)) {
            presentationModels.put(model.getId(), model);
            addPresentationModelByType(model);
            for (A attribute : model.getAttributes()) {
                addAttributeById(attribute);
                attribute.addPropertyChangeListener(BaseAttribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
                if (!StringUtil.isBlank(attribute.getQualifier())) addAttributeByQualifier(attribute);
            }
            fireModelStoreChangedEvent(model, ModelStoreEvent.Type.ADDED);
            added = true;
        }
        return added;
    }

    /**
     * Removes a presentation model from this store.<br/>
     *
     * @param model the model to be removed from the store.
     * @return if the remove operation was successful or not.
     */
    public boolean remove(P model) {
        if (null == model) return false;
        boolean removed = false;
        if (presentationModels.containsValue(model)) {
            removePresentationModelByType(model);
            presentationModels.remove(model.getId());
            for (A attribute : model.getAttributes()) {
                removeAttributeById(attribute);
                removeAttributeByQualifier(attribute);
                attribute.removePropertyChangeListener(BaseAttribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
            }
            fireModelStoreChangedEvent(model, ModelStoreEvent.Type.REMOVED);
            removed = true;
        }
        return removed;
    }

    protected void addAttributeById(A attribute) {
        if (null == attribute || attributesPerId.containsKey(attribute.getId())) return;
        attributesPerId.put(attribute.getId(), attribute);
    }

    protected void removeAttributeById(A attribute) {
        if (null == attribute) return;
        attributesPerId.remove(attribute.getId());
    }

    protected void addAttributeByQualifier(A attribute) {
        if (null == attribute) return;
        String qualifier = attribute.getQualifier();
        if (StringUtil.isBlank(qualifier)) return;
        List<A> list = attributesPerQualifier.get(qualifier);
        if (null == list) {
            list = new ArrayList<A>();
            attributesPerQualifier.put(qualifier, list);
        }
        if (!list.contains(attribute)) list.add(attribute);
    }

    protected void removeAttributeByQualifier(A attribute) {
        if (null == attribute) return;
        String qualifier = attribute.getQualifier();
        if (StringUtil.isBlank(qualifier)) return;
        List<A> list = attributesPerQualifier.get(qualifier);
        if (null != list) {
            list.remove(attribute);
        }
    }

    protected void addPresentationModelByType(P model) {
        if (null == model) return;
        String type = model.getPresentationModelType();
        if (StringUtil.isBlank(type)) return;
        List<P> list = modelsPerType.get(type);
        if (null == list) {
            list = new ArrayList<P>();
            modelsPerType.put(type, list);
        }
        if (!list.contains(model)) list.add(model);
    }

    protected void removePresentationModelByType(P model) {
        if (null == model) return;
        String type = model.getPresentationModelType();
        if (StringUtil.isBlank(type)) return;
        List<P> list = modelsPerType.get(type);
        if (null == list) return;
        list.remove(model);
        if (list.isEmpty()) {
            modelsPerType.remove(type);
        }
    }

    protected void removeAttributeByQualifier(A attribute, String qualifier) {
        if (StringUtil.isBlank(qualifier)) return;
        List<A> list = attributesPerQualifier.get(qualifier);
        if (null == list) return;
        list.remove(attribute);
        if (list.isEmpty()) {
            attributesPerQualifier.remove(qualifier);
        }
    }

    /**
     * Find a presentation model by the given id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search
     * @return a presentation model instance of there's an id match, {@code null} otherwise.
     */
    public P findPresentationModelById(String id) {
        return presentationModels.get(id);
    }

    /**
     * Finds all presentation models that share the same type.<br/>
     * The returned {@code List} is never null and immutable.
     *
     * @param type the type to search for
     * @return a {@code List} of all presentation models for which there was a match in their type.
     */
    public List<P> findAllPresentationModelsByType(String type) {
        if (StringUtil.isBlank(type) || !modelsPerType.containsKey(type)) return Collections.emptyList();
        return Collections.unmodifiableList(modelsPerType.get(type));
    }

    /**
     * Finds out if a model is contained in this store, based on its id.
     *
     * @param id the id to search in the store.
     * @return true if the model is found in this store, false otherwise.
     */
    public boolean containsPresentationModel(String id) {
        return presentationModels.containsKey(id);
    }

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return an attribute whose id matches the parameter, {@code null} otherwise.
     */
    public A findAttributeById(String id) {
        return attributesPerId.get(id);
    }

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns null. The returned {@code List} is immutable.
     *
     * @return a {@code List} of all attributes fo which their qualifier was a match.
     */
    public List<A> findAllAttributesByQualifier(String qualifier) {
        if (StringUtil.isBlank(qualifier) || !attributesPerQualifier.containsKey(qualifier)) return Collections.emptyList();
        return Collections.unmodifiableList(attributesPerQualifier.get(qualifier));
    }

    public void registerAttribute(A attribute) {
        if (null == attribute) return;
        boolean listeningAlready = false;
        for (PropertyChangeListener listener : attribute.getPropertyChangeListeners(BaseAttribute.QUALIFIER_PROPERTY)) {
            if (ATTRIBUTE_WORKER == listener) {
                listeningAlready = true;
                break;
            }
        }

        if (!listeningAlready) {
            attribute.addPropertyChangeListener(BaseAttribute.QUALIFIER_PROPERTY, ATTRIBUTE_WORKER);
        }

        addAttributeByQualifier(attribute);
        addAttributeById(attribute);
    }

    public void addModelStoreListener(ModelStoreListener<A, P> listener) {
        addModelStoreListener(null, listener);
    }

    public void addModelStoreListener(String presentationModelType, ModelStoreListener<A, P> listener) {
        if (null == listener) return;
        ModelStoreListenerWrapper<A, P> wrapper = new ModelStoreListenerWrapper<A, P>(presentationModelType, listener);
        if (!modelStoreListeners.contains(wrapper)) modelStoreListeners.add(wrapper);
    }

    public void removeModelStoreListener(ModelStoreListener<A, P> listener) {
        removeModelStoreListener(null, listener);
    }

    public void removeModelStoreListener(String presentationModelType, ModelStoreListener<A, P> listener) {
        if (null == listener) return;
        modelStoreListeners.remove(new ModelStoreListenerWrapper<A, P>(presentationModelType, listener));
    }

    public boolean hasModelStoreListener(ModelStoreListener<A, P> listener) {
        return hasModelStoreListener(null, listener);
    }

    public boolean hasModelStoreListener(String presentationModelType, ModelStoreListener<A, P> listener) {
        return null != listener &&
                modelStoreListeners.contains(new ModelStoreListenerWrapper<A, P>(presentationModelType, listener));
    }

    protected void fireModelStoreChangedEvent(P model, ModelStoreEvent.Type eventType) {
        if (modelStoreListeners.isEmpty()) return;
        ModelStoreEvent<A, P> event = new ModelStoreEvent<A, P>(eventType, model);
        for (ModelStoreListener<A, P> listener : modelStoreListeners) {
            listener.modelStoreChanged(event);
        }
    }
}
