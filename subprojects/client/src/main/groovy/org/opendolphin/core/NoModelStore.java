package org.opendolphin.core;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.GClientPresentationModel;

/**
 * A model store that does not store, i.e. neither adds nor removes presentation models.
 * It uses almost no memory.
 * Useful for a second channel like for long-polling that shell not store any presentation models.
 * */

public class NoModelStore extends ClientModelStore {

    public NoModelStore(ClientDolphin clientDolphin) {
        super(clientDolphin);
    }

    @Override
    public boolean add(ClientPresentationModel model) {
        return false;
    }

    @Override
    public boolean remove(ClientPresentationModel model) {
        return false;
    }
}
