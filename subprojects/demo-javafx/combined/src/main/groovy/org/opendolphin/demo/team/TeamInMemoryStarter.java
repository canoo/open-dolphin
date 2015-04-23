package org.opendolphin.demo.team;

import groovyx.gpars.agent.Agent;
import javafx.application.Application;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.demo.JavaFxInMemoryConfig;

import java.util.LinkedList;
import java.util.List;

public class TeamInMemoryStarter {
    public static void main(String[] args) throws Exception {

        EventBus teamBus = new EventBus();
        final Agent<List<DTO>> history = new Agent<List<DTO>>(new LinkedList<DTO>());

        final JavaFxInMemoryConfig config = new JavaFxInMemoryConfig();
        ClientDolphin clientDolphin = config.getClientDolphin();
        ((InMemoryClientConnector) clientDolphin.getClientConnector()).setSleepMillis(0);
        ServerDolphin serverDolphin = config.getServerDolphin();
        serverDolphin.register(new TeamMemberActions(teamBus, history));
        serverDolphin.getServerConnector().register(new TeamBusRelease(teamBus));

        TeamApplication.clientDolphin = clientDolphin;
        Application.launch(TeamApplication.class);
    }
}
