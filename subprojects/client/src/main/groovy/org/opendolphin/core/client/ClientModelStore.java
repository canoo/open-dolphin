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

import org.opendolphin.core.ModelStore;
import org.opendolphin.core.ModelStoreConfig;
import org.opendolphin.core.client.comm.*;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.DeletedAllPresentationModelsOfTypeNotification;
import org.opendolphin.core.comm.DeletedPresentationModelNotification;
import org.opendolphin.core.comm.GetPresentationModelCommand;

import java.util.LinkedList;
import java.util.List;

public class ClientModelStore extends ModelStore<ClientAttribute, ClientPresentationModel> {
    private final ClientDolphin clientDolphin;
    protected final AttributeChangeListener attributeChangeListener;

    public ClientModelStore(ClientDolphin clientDolphin) {
        this(clientDolphin, new ModelStoreConfig());
    }

    public ClientModelStore(ClientDolphin clientDolphin, ModelStoreConfig config) {
        super(config);
        this.clientDolphin = clientDolphin;
        attributeChangeListener = new AttributeChangeListener();
        attributeChangeListener.setClientConnector(getClientConnector());
        attributeChangeListener.setClientModelStore(this);
    }

    protected ClientConnector getClientConnector() {
        ClientConnector clientConnector = clientDolphin.getClientConnector();
        if (null == attributeChangeListener.getClientConnector()){
            attributeChangeListener.setClientConnector(clientConnector);
        }
        return clientConnector;
    }

    @Override
    public boolean add(ClientPresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            List<ClientAttribute> attributes = model.getAttributes();
            for (ClientAttribute attribute : attributes) {
                attribute.addPropertyChangeListener(attributeChangeListener);
            }
            if (!model.isClientSideOnly()) {
                getClientConnector().send(CreatePresentationModelCommand.makeFrom(model));
            }
        }
        return success;
    }

    @Override
    public boolean remove(ClientPresentationModel model) {
        boolean success = super.remove(model);
        for (ClientAttribute attribute : model.getAttributes()) {
            attribute.removePropertyChangeListener(attributeChangeListener);
        }
        return success;
    }

    @Override
    public void registerAttribute(ClientAttribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener(attributeChangeListener);
    }

    public void withPresentationModel(final String requestedPmId, final WithPresentationModelHandler withPmHandler) {
        if (withPresentationModelFromStore(requestedPmId, withPmHandler)) return;
        // when the PM is not in the store, we have to fetch it
        GetPresentationModelCommand cmd = new GetPresentationModelCommand();
        cmd.setPmId(requestedPmId);
        getClientConnector().send(cmd); // sending with null handler to allow optimizations

        clientDolphin.sync( new Runnable() {
            @Override
            public void run() {
                withPresentationModelFromStore(requestedPmId, withPmHandler);
            }
        });
    }

    private boolean withPresentationModelFromStore(String requestedPmId, WithPresentationModelHandler withPmHandler) {
        ClientPresentationModel result = findPresentationModelById(requestedPmId);
        if (result != null) {
            withPmHandler.onFinished(result);
            return true;
        }
        return false;
    }

    public void delete(ClientPresentationModel model) {
        delete(model, true);
    }

    public void delete(ClientPresentationModel model, boolean notify) {
        if (model == null) return;
        if (containsPresentationModel(model.getId())) {
            remove(model);
            if (!notify) return;
            if (model.isClientSideOnly()) return;
            getClientConnector().send(new DeletedPresentationModelNotification(model.getId()));
        }
    }

    public void deleteAllPresentationModelsOfType(String presentationModelType) {
        List<ClientPresentationModel> models = new LinkedList<ClientPresentationModel>(findAllPresentationModelsByType(presentationModelType));
        for (ClientPresentationModel model: models) {
            delete(model, false);
        }
        getClientConnector().send(new DeletedAllPresentationModelsOfTypeNotification(presentationModelType));
    }

}
