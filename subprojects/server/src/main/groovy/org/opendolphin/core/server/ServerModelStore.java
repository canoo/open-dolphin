package org.opendolphin.core.server;

import org.opendolphin.core.ModelStore;
import org.opendolphin.core.ModelStoreConfig;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ServerModelStore is a {@link org.opendolphin.core.ModelStore} with customized behavior appropriate to the
 * server side of a Dolphin connection.  There is one ServerModelSore for each user session.
 * The ServerModelStore self-assigns a unique ID which identifies each user session.
 */
public class ServerModelStore extends ModelStore<ServerAttribute, ServerPresentationModel> {

    protected List<Command> currentResponse = null;

    /** Used to create unique presentation model ids within one server model store. */
    protected long pmInstanceCount = 0L;

    /** thread safe unique store count across all sessions in order to create unique store ids.*/
    private static final AtomicInteger storeCount = new AtomicInteger(0);

    /** unique identification of the current user session. */
    public final int id = storeCount.getAndIncrement();

    public ServerModelStore() {
    }

    public ServerModelStore(ModelStoreConfig config) {
        super(config);
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    protected List<Command> getCurrentResponse() {
        return currentResponse;
    }

    /** A shared mutable state that is safe to use since we are thread-confined */
    protected void setCurrentResponse(List<Command> currentResponse) {
        this.currentResponse = currentResponse;
    }

    @Override
    public boolean add(ServerPresentationModel model) {
        boolean added = super.add(model);
        if (! added) return false;
        model.modelStore = this;
        return true;
    }
}
