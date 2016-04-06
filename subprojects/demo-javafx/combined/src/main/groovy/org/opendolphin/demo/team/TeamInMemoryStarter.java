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
package org.opendolphin.demo.team;

import groovyx.gpars.agent.Agent;
import javafx.application.Application;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.demo.JavaFxInMemoryConfig;

import java.util.LinkedList;
import java.util.List;

public class TeamInMemoryStarter {
    public static void main(String[] args) throws Exception {

        EventBus teamBus = new EventBus();
        final Agent<List<DTO>> history = new Agent<List<DTO>>(new LinkedList<DTO>());

        final JavaFxInMemoryConfig config = new JavaFxInMemoryConfig();
        ClientDolphin clientDolphin = config.getClientDolphin();
        ((InMemoryClientConnector)clientDolphin.getClientConnector()).setSleepMillis(0);
        DefaultServerDolphin serverDolphin = config.getServerDolphin();
        serverDolphin.register(new TeamMemberActions(teamBus, history));
        serverDolphin.getServerConnector().register(new TeamBusRelease(teamBus));

        TeamApplication.clientDolphin = clientDolphin;
        Application.launch(TeamApplication.class);
    }
}
