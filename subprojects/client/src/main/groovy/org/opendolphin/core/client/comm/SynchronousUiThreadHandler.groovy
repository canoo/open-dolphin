package org.opendolphin.core.client.comm

class SynchronousUiThreadHandler implements UiThreadHandler {

    @Override
    void executeInsideUiThread(Runnable runnable) {
        runnable.run()
    }
}
