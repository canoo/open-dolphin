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

package org.opendolphin.demo;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.GClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.comm.NamedCommand;

import java.util.List;

public class ConsoleView {

    public static void show(ClientDolphin clientDolphin) {
        NamedCommand cmd = new NamedCommand();
        cmd.setId("javaAction");
        OnFinishedHandler callback = new OnFinishedHandlerAdapter() {
            public void onFinished(List<ClientPresentationModel> pms) {
                ClientPresentationModel pm = pms.iterator().next();
                System.out.println("pm = " + pm);
            }
        };
        clientDolphin.getClientConnector().send(cmd, callback);
    }
}