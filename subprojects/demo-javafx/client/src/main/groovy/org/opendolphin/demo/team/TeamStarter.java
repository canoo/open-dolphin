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

import javafx.application.Application;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.demo.util.StarterUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;

// todo: make url base configurable from command line

public class TeamStarter {
    public static void main(String[] args) throws Exception {

        final String servletUrl = "http://localhost:8080/dolphin-grails/dolphin/";
//        final String servletUrl = "https://klondike.canoo.com/dolphin-grails/dolphin/";

        ClientDolphin clientDolphin = new ClientDolphin();
        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
        BlindCommandBatcher batcher = new BlindCommandBatcher();
        batcher.setMergeValueChanges(true);
        batcher.setDeferMillis(100);
        HttpClientConnector connector = new HttpClientConnector(clientDolphin, batcher, servletUrl);
        final JsonCodec codec = new JsonCodec();
        connector.setCodec(codec);
        final JavaFXUiThreadHandler uiThreadHandler = new JavaFXUiThreadHandler();
        connector.setUiThreadHandler(uiThreadHandler);
        clientDolphin.setClientConnector(connector);

        TeamApplication.clientDolphin = clientDolphin;

//        String result = connector.uploadFile(new File("/data.txt"), DefaultGroovyMethods.toURI("http://localhost:8080/dolphin-grails/fileUpload/save"));
//        System.out.println("uploaded and returned: "+result);

        StarterUtil.macFontWorkaround();


        Application.launch(TeamApplication.class);

    }
}
