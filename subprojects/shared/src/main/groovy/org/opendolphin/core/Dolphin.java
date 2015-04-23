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

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Basic Open Dolphin facade interface. The interface defines general methods that can be used on server and client side.
 *
 * @param <A> type of the internal {@link org.opendolphin.core.Attribute} instances.
 * @param <P> type of the internal {@link org.opendolphin.core.PresentationModel} instances.
 */
public interface Dolphin<A extends Attribute, P extends PresentationModel<A>> {

    /**
     * Adds a presentation model to the model store.<br/>
     * Presentation model ids should be unique. This method guarantees this condition by disallowing
     * models with duplicate ids to be added.
     *
     * @param model the model to be added.
     * @return if the add operation was successful or not.
     */
    boolean add(P model);

    /**
     * Removes a presentation model from the model store.<br/>
     *
     * @param model the model to be removed from the store.
     * @return if the remove operation was successful or not.
     */
    boolean remove(P model);

    /**
     * Finds an attribute by its id.<br/>
     * <strong>WARNING:</strong> this method may return {@code null} if no match is found.
     *
     * @param id the id to search for.
     * @return an attribute whose id matches the parameter, {@code null} otherwise.
     */
    A findAttributeById(String id);

    /**
     * Returns a {@code List} of all attributes that share the same qualifier.<br/>
     * Never returns null. The returned {@code List} is immutable.
     *
     * @param qualifier the qualifier to search for
     * @return a {@code List} of all attributes for which their qualifier was a match.
     */
    List<A> findAllAttributesByQualifier(String qualifier);

    /**
     * Returns all ids of the presentation models that are part of the model store.
     *
     * @return {@code Set} of all presentation model ids
     */
    Set<String> listPresentationModelIds();

    /**
     * Returns a {@code Collection} of all presentation models that are part of the model store.
     *
     * @return {@code Collection} of all presentation models
     */
    Collection<P> listPresentationModels();

    /**
     * Returns a {@code List} of all presentation models that share the same type.<br/>
     * Never returns null. The returned {@code List} is immutable.
     *
     * @param presentationModelType the type to search for
     * @return a {@code List} of all found presentation models
     */
    List<P> findAllPresentationModelsByType(String presentationModelType);

    /**
     * Returns the presentation model with the given id. <br>
     * If no presentation model can be found this method returns {@code null}
     *
     * @param id the id
     * @return the presentation model
     */
    //TODO: should we remove this method? It's duplicated by findPresentationModelById
    P getAt(String id);

    /**
     * Returns the presentation model with the given id <br>
     * If no presentation model can be found this method returns {@code null}
     *
     * @param id the id
     * @return the presentation model
     */
    P findPresentationModelById(String id);

    /**
     * Removes the given {@code ModelStoreListener} from the model store
     *
     * @param listener the lister that should be removed
     */
    void removeModelStoreListener(ModelStoreListener listener);

    /**
     * Removes the given {@code ModelStoreListener} for a specific presentation model type from the model store
     *
     * @param presentationModelType the presentation model type
     * @param listener              the lister that should be removed
     */
    void removeModelStoreListener(String presentationModelType, ModelStoreListener listener);

    /**
     * Checks if the given listener is registered
     *
     * @param listener the listener
     * @return {@code true} is the lister is registered, {@code false} otherwise.
     */
    boolean hasModelStoreListener(ModelStoreListener listener);

    /**
     * Registeres the given listener for a specific presentation model type
     *
     * @param presentationModelType the presentation model type
     * @param listener              the listener
     */
    void addModelStoreListener(String presentationModelType, ModelStoreListener listener);

    /**
     * Checks if the given listener is registered for the given presentation model type
     *
     * @param presentationModelType the presentation model type
     * @param listener              the listener
     * @return {@code true} is the lister is registered for the type, {@code false} otherwise.
     */
    boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener);

    /**
     * Registers a listener
     *
     * @param listener the listener
     */
    void addModelStoreListener(ModelStoreListener listener);

    /**
     * For every attribute in the given presentation model, proliferate the attribute value to
     * all attributes that bear the same qualifier and tag.
     *
     * @param presentationModel the presentation model that contains the attributes for the update
     */
    void updateQualifiers(P presentationModel);
}
