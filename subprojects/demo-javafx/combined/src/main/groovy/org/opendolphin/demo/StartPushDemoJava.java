/*
 * Java-Copy "startPushDemo.groovy", community contribution
 * @author  Guenter Paul, gp@guenterpaul.de
 * date:   07.11.2012
 */
package org.opendolphin.demo;

import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.action.DolphinServerAction;

public class StartPushDemoJava {
    private StartPushDemoJava() {
    } // Schnickschnack :)

    public static void main(String[] args) {
        // my company name convention
        JavaFxInMemoryConfig lJavaFxInMemoryConfig = new JavaFxInMemoryConfig();
        ServerDolphin lServerDolphin = lJavaFxInMemoryConfig.getServerDolphin();
        ClientDolphin lClientDolphin = lJavaFxInMemoryConfig.getClientDolphin();

        DolphinServerAction action = new VehiclePushActions();
        action.setServerDolphin(lServerDolphin);
        lServerDolphin.getServerConnector().register(action);

        PushView.show(lClientDolphin);
    }
}
