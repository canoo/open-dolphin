/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

package org.opendolphin.demo

import org.opendolphin.LogConfig
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler


def clientDolphin = StarterUtil.setupForRemote()
clientDolphin.clientConnector.commandBatcher.deferMillis = 70
clientDolphin.clientConnector.commandBatcher.mergeValueChanges = false
clientDolphin.clientConnector.commandBatcher.maxBatchSize = 10
clientDolphin.clientConnector.uiThreadHandler = new JavaFXUiThreadHandler()

def updateClientDolphin = StarterUtil.setupForRemote()
updateClientDolphin.clientConnector.uiThreadHandler = new JavaFXUiThreadHandler()

LogConfig.noLogs()

SmallFootprintView.show clientDolphin, updateClientDolphin