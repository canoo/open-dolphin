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

import org.opendolphin.core.client.comm.UiThreadHandler;
import org.opendolphin.core.comm.DefaultInMemoryConfig;

import java.util.concurrent.CountDownLatch;

public class JavaDemo {
    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultInMemoryConfig inMemoryConfig = new DefaultInMemoryConfig();

        inMemoryConfig.getClientDolphin().getClientConnector().setUiThreadHandler(new UiThreadHandler() {
            @Override
            public void executeInsideUiThread(Runnable runnable) {
                System.out.println("going inside ui");
                // do inside the UI thread:
                runnable.run();
                latch.countDown();
            }
        });
        inMemoryConfig.getServerDolphin().registerDefaultActions();
        inMemoryConfig.getServerDolphin().getServerConnector().register(new JavaAction());

        ConsoleView.show(inMemoryConfig.getClientDolphin());
        System.out.println("waiting to finish");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


