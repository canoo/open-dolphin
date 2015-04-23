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

package org.opendolphin.core.server;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.NamedCommandHandler;

import java.util.List;

/**
 * The dolphin class on server side. This class should be used as a facade when working with Open Dolphin on server side.
 *
 */
public interface ServerDolphin extends Dolphin<ServerAttribute, ServerPresentationModel> {

    /**
     * Methods that internally register all default methods.
     * This method will be removed in a fufure version (see DOL-143)
     */
    @Deprecated
    void registerDefaultActions();

    /**
     * Registers the given {@code DolphinServerAction}
     * @param action the action
     */
    void register(DolphinServerAction action);

    /**
     * Registers a {@link org.opendolphin.core.server.comm.NamedCommandHandler} to this server dolphin. The handler will
     * be called whenever a command with the given name was triggered. The name must be unique for one server dolphin instance.
     *
     * example:
     * serverDolphin.action("myCommand", (command, response) -&gt; System.out.println("action command was triggered"));
     *
     *
     * @param name Name of the Command. This must be unique for one server dolphin instance
     * @param namedCommandHandler The handler that will handle the command on server side.
     */
    void action(String name, NamedCommandHandler namedCommandHandler);

    /**
     * Creates a presentation model and adds it to the internal model store.
     * @param id the id of the new presentation model
     * @param presentationModelType the type of the new presentation model
     * @param dto
     * @return the created and added presentation model
     */
    //TODO: Doc for DTO is needed
    //TODO:
    ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto);

    /**
     * Removes all presentation models of the given type from the internal model store
     * @param type the type
     */
    //TODO: Maybe this method can return the list of the removed models
    void removeAllPresentationModelsOfType(String type);

    //TODO: Should we remove this method from the interface?
    ServerModelStore getServerModelStore();

    //TODO: Should we remove this method from the interface?
    ServerConnector getServerConnector();

    //TODO: Should we remove this method from the interface? Normally a presentation model on server side is created by using DTO.
    //TODO: I like this way much more than using DTO because by doing so you have different apis on client and server
    ServerAttribute createAttribute(String propertyName, Object initialValue);

    //TODO: Should we remove this method from the interface? Normally a presentation model on server side is created by using DTO.
    //TODO: I like this way much more than using DTO because by doing so you have different apis on client and server
    ServerAttribute createAttribute(String propertyName, Object baseValue, String qualifier, Tag tag);

    //TODO: Should we remove this method from the interface? I don't know if it in general makes sense to create a presentation model that isn't added to the model store
    ServerPresentationModel createPresentationModel(String id, List<ServerAttribute> attributes);

    //TODO: Should we remove this method from the interface? I don't know if it in general makes sense to create a presentation model that isn't added to the model store
    ServerPresentationModel createPresentationModel(String id, List<ServerAttribute> attributes, String presentationModelType);

}