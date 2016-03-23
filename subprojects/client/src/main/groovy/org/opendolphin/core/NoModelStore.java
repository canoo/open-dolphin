/*
 * Copyright 2012-2016 Canoo Engineering AG.
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

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;

/**
 * A model store that does not store, i.e. neither adds nor removes presentation models.
 * It uses almost no memory.<br>
 * Useful for a second channel (e.g., for long-polling) that does not store any presentation models.
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
