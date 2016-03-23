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
package org.opendolphin.demo;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.ModelStore;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.DataCommand;
import org.opendolphin.core.comm.InitializeAttributeCommand;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.server.action.ServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import groovy.lang.Closure;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.demo.DemoSearchProperties.*;

public class DemoSearchAction implements ServerAction {
    protected final ModelStore modelStore;

    public DemoSearchAction(ModelStore modelStore) {
        this.modelStore = modelStore;
    }

    public void registerIn(final ActionRegistry registry) {

        registry.register(FIRST_FILL_CMD, new CommandHandler() {
            public void handleCommand(Command command, List response) {
                for (int i = 0; i < 10; i++) {
                    String pmid = "First " + i;
                    Map data = new HashMap();
                    data.put(TEXT, pmid);
                    response.add(new DataCommand(data));
                }
            }
        });
        registry.register(SECOND_FILL_CMD, new CommandHandler() {
            public void handleCommand(Command command, List response) {
                for (int i = 0; i < 10; i++) {
                    String pmid = "Second " + i;
                    Map data = new HashMap();
                    data.put(TEXT, pmid);
                    response.add(new DataCommand(data));
                }
            }
        });

        registry.register(SEARCH_CMD, new CommandHandler() {
            public void handleCommand(Command command, List response) {
                PresentationModel searchCriteria = modelStore.findPresentationModelById(SEARCH_CRITERIA);
                if (searchCriteria == null) {
                    throw new IllegalStateException("No search criteria known on the server!");
                }
                Attribute attribute = searchCriteria.findAttributeByPropertyName(NAME);
                Object value = (attribute == null) ? null : attribute.getValue();
                String contactName = (value == null) ? "" : value.toString();

                for (int i = 0; i < 10; i++) {
                    String id = contactName + " contact " + i;
                    Map data = new HashMap();
                    data.put("id", id);
                    data.put(CONTACT_NAME, contactName);
                    data.put(CONTACT_DATE, new Date(i * 1000000000).toString());
                    response.add(new DataCommand(data));
                }
            }
        });
    }
}
