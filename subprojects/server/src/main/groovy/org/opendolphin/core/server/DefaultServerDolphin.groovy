/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

package org.opendolphin.core.server

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.opendolphin.core.Attribute
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.AbstractDolphin
import org.opendolphin.core.ModelStore
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.AttributeMetadataChangedCommand
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.DeletePresentationModelCommand
import org.opendolphin.core.comm.DeleteAllPresentationModelsOfTypeCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.PresentationModelResetedCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.action.*
import org.opendolphin.core.server.comm.NamedCommandHandler

import java.util.concurrent.atomic.AtomicBoolean

import static org.opendolphin.StringUtil.isBlank

/**
 * The default implementation of the Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */

@CompileStatic
@Log
class DefaultServerDolphin extends AbstractDolphin<ServerAttribute, ServerPresentationModel> implements ServerDolphin{

    /** the server model store is unique per user session */
    final ServerModelStore serverModelStore

    /** the serverConnector is unique per user session */
    final ServerConnector serverConnector

    private AtomicBoolean initialized = new AtomicBoolean(false);

    DefaultServerDolphin(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore
        this.serverConnector = serverConnector
        this.serverConnector.serverModelStore = serverModelStore
    }

    protected DefaultServerDolphin() {
        this(new ServerModelStore(), new ServerConnector())
    }

    @Override
    ModelStore getModelStore() {
        serverModelStore
    }

    void registerDefaultActions() {
        if (initialized.getAndSet(true)) {
            log.warning("attempt to initialize default actions more than once!")
            return;
        }
        register new StoreValueChangeAction()
        register new StoreAttributeAction()
        register new CreatePresentationModelAction()
        register new DeletePresentationModelAction()
        register new DeletedAllPresentationModelsOfTypeAction()
        serverConnector.register new EmptyAction()
    }

    void register(DolphinServerAction action) {
        action.serverDolphin = this
        serverConnector.register(action)
    }

    /** groovy-friendly convenience method to register a named action */
    void action(String name, Closure logic) {
        def serverAction = new ClosureServerAction(name, logic)
        register(serverAction)
    }
    /** java-friendly convenience method to register a named action */
    void action(String name, NamedCommandHandler namedCommandHandler) {
        def serverAction = new NamedServerAction(name, namedCommandHandler)
        register(serverAction)
    }

    /**
     * Adding the model to the model store and if successful, sending the CreatePresentationModelCommand.
     * @param model the model to be added.
     * @return whether the adding was successful, which implies that also the command has been sent
     */
    @Override
    boolean add(ServerPresentationModel model) {
        def result = super.add(model)
        if (result){
            serverModelStore.currentResponse << CreatePresentationModelCommand.makeFrom(model)
        }
        return result
    }

    /**
     * Create a presentation model on the server side, add it to the model store, and send a command to
     * the client, advising him to do the same.
     * @throws IllegalArgumentException if a presentation model for this id already exists. No commands are sent in this case.
     */
    ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto) {
        List<ServerAttribute> attributes = dto.slots.collect { Slot slot ->
            ServerAttribute result = new ServerAttribute(slot.propertyName, slot.baseValue, slot.qualifier, slot.tag)
            result.silently {
                result.value = slot.value
            }
            return result
        }
        ServerPresentationModel model = new ServerPresentationModel(id, attributes, serverModelStore)
        model.presentationModelType = presentationModelType
        add model
        return model
    }

    /** @deprecated use {@link #presentationModelCommand(java.util.List, java.lang.String, java.lang.String, org.opendolphin.core.server.DTO)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
     static void presentationModel(List<Command> response, String id, String presentationModelType, DTO dto){
        presentationModelCommand(response, id, presentationModelType, dto)
    }

    /** Convenience method to let the client (!) dolphin create a presentation model as specified by the DTO.
     * The server model store remains untouched until the client has issued the notification.*/
     static void presentationModelCommand(List<Command> response, String id, String presentationModelType, DTO dto){
        if (null == response) return
        response << new CreatePresentationModelCommand(pmId: id, pmType: presentationModelType, attributes: dto.encodable())
    }

    /** @deprecated use {@link #clientSideModelCommand(java.util.List, java.lang.String, java.lang.String, org.opendolphin.core.server.DTO)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void clientSideModel(List<Command> response, String id, String presentationModelType, DTO dto){
        clientSideModelCommand(response, id, presentationModelType, dto)
    }

    /** Convenience method to let Dolphin create a
     *    <strong> client-side only </strong>
     *  presentation model as specified by the DTO. */
    static void clientSideModelCommand(List<Command> response, String id, String presentationModelType, DTO dto){
        if (null == response) return
        response << new CreatePresentationModelCommand(pmId: id, pmType: presentationModelType, attributes: dto.encodable(), clientSideOnly:true)
    }

    /** @deprecated use {@link #rebaseCommand(java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void rebase(List<Command> response, ServerAttribute attribute){
        rebaseCommand(response, attribute)
    }

    /** Convenience method to let Dolphin rebase the value of an attribute */
    static void rebaseCommand(List<Command> response, ServerAttribute attribute){
        if (null == attribute) {
            log.severe("Cannot rebase null attribute")
            return
        }
        response << new AttributeMetadataChangedCommand(
            attributeId: attribute.id,
            metadataName: Attribute.BASE_VALUE,
            value: attribute.value)
    }

    /** @deprecated use attribute.rebase(). Will be removed in version 1.0! */
    static void rebase(List<Command> response, String attributeId){
        rebaseCommand(response, attributeId)
    }

    /** @deprecated use attribute.rebase(). Will be removed in version 1.0! */
    static void rebaseCommand(List<Command> response, String attributeId){
        throw new UnsupportedOperationException("Direct use of rebaseCommand is no longer supported. Use attribute.rebase()")
    }

    /** Convenience method to let Dolphin remove a presentation model directly on the server and notify the client.*/
    boolean remove(ServerPresentationModel pm){
        boolean deleted = serverModelStore.remove(pm)
        if (deleted) {
            DefaultServerDolphin.deleteCommand(serverModelStore.currentResponse, pm)
        }
        return deleted
    }

    /** Convenience method to let Dolphin remove all presentation models of a given type directly on the server and notify the client.*/
    void removeAllPresentationModelsOfType(String type) {
        // todo: [REF] duplicated with DeleteAllPresentationModelsOfTypeAction, could go into ModelStore
        List<ServerPresentationModel> models = new LinkedList( findAllPresentationModelsByType(type)) // work on a copy
        for (ServerPresentationModel model in models ){
            serverModelStore.remove(model) // go through the model store to avoid single commands being sent to the client
        }
        DefaultServerDolphin.deleteAllPresentationModelsOfTypeCommand(serverModelStore.currentResponse, type)
    }

    /** @deprecated use {@link #deleteCommand(java.util.List, org.opendolphin.core.server.ServerPresentationModel)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void delete(List<Command> response, ServerPresentationModel pm){
        deleteCommand(response, pm)
    }

    /** Convenience method to let Dolphin delete a presentation model on the client side */
    static void deleteCommand(List<Command> response, ServerPresentationModel pm){
        if (null == pm) {
            log.severe("Cannot delete null presentation model")
            return
        }
        deleteCommand(response, pm.id)
    }

    /** @deprecated use {@link #deleteCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void delete(List<Command> response, String pmId){
        deleteCommand(response, pmId)
    }

    /** Convenience method to let Dolphin delete a presentation model on the client side */
    static void deleteCommand(List<Command> response, String pmId){
        if (null == response || isBlank(pmId)) return
        response << new DeletePresentationModelCommand(pmId: pmId)
    }

    /** @deprecated use {@link #deleteAllPresentationModelsOfTypeCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void deleteAllPresentationModelsOfType(List<Command> response, String pmType){
        deleteAllPresentationModelsOfTypeCommand(response, pmType)
    }

    /** Convenience method to let Dolphin delete all presentation models of a given type on the client side */
    static void deleteAllPresentationModelsOfTypeCommand(List<Command> response, String pmType){
        if (null == response || isBlank(pmType)) return
        response << new DeleteAllPresentationModelsOfTypeCommand(pmType: pmType)
    }

    /** @deprecated use {@link #resetCommand(java.util.List, org.opendolphin.core.server.ServerPresentationModel)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void reset(List<Command> response, ServerPresentationModel pm){
        resetCommand(response, pm)
    }

    /** Convenience method to let Dolphin reset a presentation model */
    static void resetCommand(List<Command> response, ServerPresentationModel pm){
        if (null == pm) {
            log.severe("Cannot reset null presentation model")
            return
        }
        resetCommand(response, pm.id)
    }

    /** @deprecated use {@link #resetCommand(java.util.List, java.lang.String)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void reset(List<Command> response, String pmId){
        resetCommand(response, pmId)
    }

    /** Convenience method to let Dolphin reset a presentation model */
    static void resetCommand(List<Command> response, String pmId){
        if (null == response || isBlank(pmId)) return
        response << new PresentationModelResetedCommand(pmId: pmId)
    }

    /** @deprecated use {@link #resetCommand(java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void reset(List<Command> response, ServerAttribute attribute) {
        resetCommand(response, attribute)
    }

    /** Convenience method to let Dolphin reset the value of an attribute */
    static void resetCommand(List<Command> response, ServerAttribute attribute) {
        if (null == response || null == attribute) {
            log.severe("Cannot reset null attribute")
            return
        }
        response << new ValueChangedCommand(
            attributeId: attribute.id,
            oldValue: attribute.value,
            newValue: attribute.baseValue
        )
    }

    /** @deprecated use {@link #changeValueCommand(java.util.List, org.opendolphin.core.server.ServerAttribute, java.lang.Object)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void changeValue(List<Command>response, ServerAttribute attribute, value){
        changeValueCommand(response, attribute, value)
    }

    /**
     * Convenience method to change an attribute value on the server side.
     * @param response must not be null or the method silently ignores the call
     * @param attribute must not be null
     */
    static void changeValueCommand(List<Command>response, ServerAttribute attribute, value){
        if (null == response) return
        if (null == attribute) {
            log.severe("Cannot change value on a null attribute to '$value'")
            return
        }
        forceChangeValue(value, response, attribute)
    }

    /** @deprecated use {@link #forceChangeValueCommand(java.lang.Object, java.util.List, org.opendolphin.core.server.ServerAttribute)}. You can use the "inline method refactoring". Will be removed in version 1.0! */
    static void forceChangeValue(value, List<Command> response, ServerAttribute attribute) {
        forceChangeValueCommand(value, response, attribute)
    }

    /** @deprecated use {@link #changeValueCommand(java.util.List, org.opendolphin.core.server.ServerAttribute, java.lang.Object)}, which enforces the value change by default. Will be removed in version 1.0! */
    static void forceChangeValueCommand(value, List<Command> response, ServerAttribute attribute) {
        value = BaseAttribute.checkValue(value)
        response << new ValueChangedCommand(attributeId: attribute.id, newValue: value, oldValue: attribute.value)
    }

    /** Convenience method for the InitializeAttributeCommand */
    static void initAt(List<Command>response, String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        initAtCommand(response, pmId, propertyName, qualifier, newValue, tag)
    }

    /** Convenience method for the InitializeAttributeCommand */
    static void initAtCommand(List<Command>response, String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        if (null == response) return
        response << new InitializeAttributeCommand(pmId: pmId, propertyName: propertyName, qualifier: qualifier, newValue: newValue, tag: tag)
    }

    /** The id of the server dolphin, which is identical to the id of its server model store. */
    int getId() {
        serverModelStore.id
    }
}
