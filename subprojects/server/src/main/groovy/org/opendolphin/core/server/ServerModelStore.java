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

import org.opendolphin.core.ModelStore;
import org.opendolphin.core.ModelStoreConfig;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        if (! added) return added;
        ((GServerPresentationModel)model).setServerModelStore(this);
        return true;
    }
}
