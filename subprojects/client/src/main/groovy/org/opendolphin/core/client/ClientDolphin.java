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

package org.opendolphin.core.client;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;

/**
 * The main Dolphin facade on the client side.
 */
public interface ClientDolphin extends Dolphin<ClientAttribute, ClientPresentationModel> {

    /**
     * Convenience method for a creating a ClientPresentationModel with initial null values for the attributes.
     * The presentation model will be added to the internal model store.
     * @param id the id of the presentation model
     * @param attributeNames list of the names of all attributes that should be part of presentation model
     * @return the new presentation model
     */
    ClientPresentationModel presentationModel(String id, List<String> attributeNames);

    /**
     * Creates a new presentation model and adds it to the internal model store.
     * @param id the id of the presentation model
     * @param presentationModelType the type of the presentation model
     * @param attributes list of attributes that should be part of the presentation model
     * @return the new presentation model
     */
    ClientPresentationModel presentationModel(String id, String presentationModelType, ClientAttribute... attributes);

    /**
     * Creates a new presentation model and adds it to the internal model store.
     * @param id the id of the presentation model
     * @param attributes list of attributes that should be part of the presentation model
     * @return the new presentation model
     */
    ClientPresentationModel presentationModel(String id, ClientAttribute... attributes);

    /**
     * Triggers a command on the server. The {@code OnFinishedHandler} will be called when the command was executed on the server
     * @param commandName the name of the command
     * @param onFinished the hadnler that will be called when the command was executed
     */
    void send(String commandName, OnFinishedHandler onFinished);

    /**
     *  Triggers a command on the server.
     * @param commandName the name of the command
     */
    void send(String commandName);

    //TODO: Should we remove this method from the interface?
    void sync(Runnable runnable);

    //TODO: Should we remove this method from the interface? It's the same as model.syncWith(model). I think we should only have one way to do this.
    ApplyToAble apply(ClientPresentationModel source);

    /**
     * Removes a presentation model from the internal model store.
     * @param modelToDelete the model
     */
    void delete(ClientPresentationModel modelToDelete);

    /**
     * Removes all presentation models with the given type from the internal model store. This method has more performance
     * tha calling {@link #delete(ClientPresentationModel)} in a loop because the internal model store will send only one notification.
     * @param presentationModelType the type
     */
    //TODO: maybe this method should return a list of the removed models
    void deleteAllPresentationModelsOfType(String presentationModelType);

    /**
     * Creates a new attribute and adds it to the given presentation model
     * @param model the presentation model
     * @param propertyName the property name of the new attribute
     * @param tag the tag of the new attribute
     * @param value the property name of the new attribute
     * @return the new attribute that was added to the given presentation model
     */
    //TODO: At the moment there are several methods how an attribute can be added to a presentation model. Maybe we can add more consistence here.
    //TODO: I don't know why this method is named "tag"
    ClientAttribute tag(ClientPresentationModel model, String propertyName, Tag tag, Object value);

    /**
     * Adds the given attribute to the given presentation model.
     * @param presentationModel the presentation model
     * @param attribute the attribute
     */
    //TODO: At the moment there are several methods how an attribute can be added to a presentation model. Maybe we can add more consistence here.
    void addAttributeToModel(ClientPresentationModel presentationModel, ClientAttribute attribute);

    /**
     * Copies the given presentation model. The copy will contain the same attributes as the given presentation model.
     * The copy will be added to the internal model store
     * @param sourcePM the presentation model that should be copied
     * @return the new copy
     */
    ClientPresentationModel copy(ClientPresentationModel sourcePM);

    //TODO: Should we remove this method from the interface?
    void startPushListening(String pushActionName, String releaseActionName);

    //TODO: Should we remove this method from the interface?
    void stopPushListening();

    //TODO: Should we remove this method from the interface?
    boolean isPushListening();

    //TODO: Should we remove this method from the interface?
    ClientConnector getClientConnector();

    //TODO: Should we remove this method from the interface?
    void setClientConnector(ClientConnector connector);

    //TODO: Should we remove this method from the interface?
    void setClientModelStore(ClientModelStore store);

    //TODO: Should we remove this method from the interface?
    ClientModelStore getClientModelStore();

    /**
     * Creates a presentation model that won't be added to the internal model store
     * @param attributes list of attributes that should be contained in the new presentation model
     * @return the presentation model
     */
    ClientPresentationModel createPresentationModel(List<ClientAttribute> attributes);

    /**
     *  Creates a presentation model that won't be added to the internal model store
     * @param id id of the new presentation model
     * @param attributes list of attributes that should be contained in the new presentation model
     * @return the presentation model
     */
    ClientPresentationModel createPresentationModel(String id, List<ClientAttribute> attributes);

    /**
     * Creates a new attribute
     * @param propertyName property name of the attribute
     * @return the new attribute
     */
    @Deprecated
    ClientAttribute createAttribute(String propertyName);

    /**
     * Creates a new attribute
     * @param propertyName property name of the attribute
     * @param initialValue initial value of the new attribute
     * @param qualifier qualifier of the new attribute
     * @param tag tag of the new attribute
     * @return the new attribute
     */
    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier, Tag tag);

    /**
     * Creates a new attribute
     * @param propertyName property name of the attribute
     * @param initialValue initial value of the new attribute
     * @param tag tag of the new attribute
     * @return the new attribute
     */
    ClientAttribute createAttribute(String propertyName, Object initialValue, Tag tag);

    /**
     * Creates a new attribute
     * @param propertyName property name of the attribute
     * @param initialValue initial value of the new attribute
     * @param qualifier qualifier of the new attribute
     * @return the new attribute
     */
    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier);

    /**
     * Creates a new attribute
     * @param propertyName property name of the attribute
     * @param initialValue initial value of the new attribute
     * @return the new attribute
     */
    ClientAttribute createAttribute(String propertyName, Object initialValue);

    //TODO: Should we remove this method from the interface?
    @Deprecated
    ClientAttribute createAttribute(Map props);
}
