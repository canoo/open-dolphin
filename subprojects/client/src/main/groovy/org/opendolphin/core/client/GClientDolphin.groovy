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

package org.opendolphin.core.client

import org.opendolphin.core.AbstractDolphin
import org.opendolphin.core.ModelStore
import org.opendolphin.core.Tag
import org.opendolphin.core.client.comm.ClientConnector
import org.opendolphin.core.client.comm.OnFinishedHandler
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter
import org.opendolphin.core.comm.AttributeCreatedNotification
import org.opendolphin.core.comm.EmptyNotification
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.SignalCommand

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
// makes use of dynamic dispatch, do not use @CompileStatic
public class GClientDolphin extends AbstractDolphin<ClientAttribute, ClientPresentationModel> implements ClientDolphin {

    // todo dk: the client model store should become a secret of the ClientDolphin
    ClientModelStore clientModelStore

    ClientConnector clientConnector

    @Override
    ModelStore getModelStore() {
        return clientModelStore
    }

    ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        def result = new GClientPresentationModel(id, attributeNames.collect() { new GClientAttribute(it)})
        clientModelStore.add result
        return result
    }

    /** groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values*/
    ClientPresentationModel presentationModel(String id, String presentationModelType = null, Map<String, Object> attributeNamesAndValues) {
        presentationModel(attributeNamesAndValues, id, presentationModelType)
    }

    /** groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values*/
    ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id, String presentationModelType = null) {
        def attributes = attributeNamesAndValues.collect {key, value -> new GClientAttribute(key, value) }
        def result = new GClientPresentationModel(id, attributes)
        result.presentationModelType = presentationModelType
        clientModelStore.add result
        return result
    }

    ClientPresentationModel presentationModel(String id, String presentationModelType = null, ClientAttribute... attributes) {
        def result = new GClientPresentationModel(id, attributes as List)
        result.presentationModelType = presentationModelType
        clientModelStore.add result
        return result
    }

    void send(String commandName, OnFinishedHandler onFinished = null) {
        clientConnector.send new NamedCommand(commandName), onFinished
    }

    /** groovy-friendly convenience method for sending a named command that expects only PM responses */
    void send(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), new OnFinishedHandlerAdapter(){
            void onFinished(List<GClientPresentationModel> presentationModels) {
                onFinished(presentationModels)
            }
        })
    }

    void sync(Runnable runnable) {
        clientConnector.send(new EmptyNotification(), new OnFinishedHandlerAdapter() {
            void onFinished(List<ClientPresentationModel> presentationModels) {
               runnable.run()
           }
        })
    }

    /** groovy-friendly convenience method for sending a named command that expects only data responses*/
    void data(String commandName, Closure onFinished) {
        clientConnector.send(new NamedCommand(commandName), new OnFinishedHandlerAdapter(){
            void onFinishedData(List<Map> data) {
                onFinished(data)
            }
        })
    }

    /** start of a fluent api: apply source to target. Use for selection changes in master-detail views. */
    ApplyToAble apply(ClientPresentationModel source) {
        new ApplyToAble(dolphin: this, source: source)
    }

   public void delete(ClientPresentationModel modelToDelete) {
        clientModelStore.delete(modelToDelete)
    }

    /** Removes the models of a given type from the model store just like delete()
     * but sends only one notification to the server */
    public void deleteAllPresentationModelsOfType(String presentationModelType) {
        clientModelStore.deleteAllPresentationModelsOfType(presentationModelType)
    }

    /**
     * Tags the attribute by
     * adding a new attribute with the given tag and value to the model store
     * inside the given presentation model and for the given property name.
     * @return the ClientAttribute that carries the tag value
     */
    // todo: make this available on the server side as well
    public ClientAttribute tag(ClientPresentationModel model, String propertyName, Tag tag, def value) {
        def attribute = new GClientAttribute(propertyName, value, null, tag)
        addAttributeToModel(model, attribute)
        return attribute
    }

    public void addAttributeToModel(ClientPresentationModel presentationModel, ClientAttribute attribute) {
        presentationModel._internal_addAttribute(attribute)
        clientModelStore.registerAttribute(attribute)
        if (!presentationModel.clientSideOnly) {
            clientConnector.send new AttributeCreatedNotification(
                    pmId: presentationModel.id,
                    attributeId: attribute.id,
                    propertyName: attribute.propertyName,
                    newValue: attribute.value,
                    qualifier: attribute.qualifier,
                    tag: attribute.tag
            )
        }
    }

    // todo dk: compare with JS version, todo: same on server
    protected ClientAttribute copyAttribute(ClientAttribute sourceAttribute) {
        def result = new GClientAttribute(sourceAttribute.propertyName,sourceAttribute.baseValue, sourceAttribute.qualifier, sourceAttribute.tag)
        result.value = sourceAttribute.value
        return result
    }

    public ClientPresentationModel copy(ClientPresentationModel sourcePM) {
        def attrs  = sourcePM.attributes.collect{ copyAttribute(it) }
        ClientPresentationModel result = new GClientPresentationModel(null, attrs)
        result.presentationModelType = sourcePM.presentationModelType
        result.clientSideOnly = sourcePM.clientSideOnly
        clientModelStore.add(result)
        return result
    }

    public void startPushListening(String pushActionName, String releaseActionName) {
        if (null == pushActionName) {
            // todo dk: think about logging here
//            log.warning("You must set a pushListener on the client connector if you want to listen for push events")
            return
        }
        if (null == releaseActionName) {
//            log.warning("Please set releaseCommand in client connector or we cannot release the send lock.")
            return
        }
        clientConnector.pushListener   = new NamedCommand(pushActionName);
        clientConnector.releaseCommand = new SignalCommand(releaseActionName);
        clientConnector.pushEnabled    = true
        clientConnector.listen()
    }

    public void stopPushListening() {
        clientConnector.pushEnabled = false
    }

    public boolean isPushListening() {
        return clientConnector.pushEnabled
    }

    @Override
    ClientPresentationModel createPresentationModel(List<ClientAttribute> attributes) {
        return new GClientPresentationModel(attributes)
    }

    @Override
    ClientPresentationModel createPresentationModel(String id, List<ClientAttribute> attributes) {
        return new GClientPresentationModel(id, attributes)
    }

    @Override
    ClientAttribute createAttribute(String propertyName) {
        return new GClientAttribute(propertyName)
    }

    @Override
    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, qualifier, tag)
    }

    @Override
    ClientAttribute createAttribute(String propertyName, Object initialValue, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, tag)
    }

    @Override
    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier) {
        return new GClientAttribute(propertyName, initialValue, qualifier)
    }

    @Override
    ClientAttribute createAttribute(String propertyName, Object initialValue) {
        return new GClientAttribute(propertyName, initialValue)
    }

    @Override
    ClientAttribute createAttribute(Map props) {
        return new GClientAttribute(props)
    }
}

class ApplyToAble {
    ClientDolphin dolphin
    ClientPresentationModel source

    void to(ClientPresentationModel target) {
        target.syncWith source
        // at this point, all notifications about value and meta-inf changes
        // have been sent and that way the server is synchronized
    }
}
