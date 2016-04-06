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
/*
 * Java-Copy "startPushDemo.groovy", community contribution
 * @author  Guenter Paul, gp@guenterpaul.de
 * date:   07.11.2012
 */
package org.opendolphin.demo;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;

public class StartPushDemoJava {
    private StartPushDemoJava() { } // Schnickschnack :)

    public static void main(String[] args) {
        // my company name convention
        JavaFxInMemoryConfig lJavaFxInMemoryConfig = new JavaFxInMemoryConfig();
        DefaultServerDolphin lServerDolphin = lJavaFxInMemoryConfig.getServerDolphin();
        ClientDolphin lClientDolphin = lJavaFxInMemoryConfig.getClientDolphin();

        DolphinServerAction action = new VehiclePushActions();
        action.setServerDolphin(lServerDolphin);
        lServerDolphin.getServerConnector().register(action);

        PushView.show(lClientDolphin);
    }
}
