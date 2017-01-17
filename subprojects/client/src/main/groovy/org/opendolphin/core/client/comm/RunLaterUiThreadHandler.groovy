package org.opendolphin.core.client.comm

import groovyx.gpars.agent.Agent

class RunLaterUiThreadHandler implements UiThreadHandler {

    protected final runner = Agent.agent(1)

    @Override
    void executeInsideUiThread(Runnable runnable) {
        runner << { runnable.run() }
    }
}
