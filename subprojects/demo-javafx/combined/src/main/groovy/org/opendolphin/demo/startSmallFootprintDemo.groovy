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

import org.opendolphin.core.server.EventBus


def writer = new JavaFxInMemoryConfig()
writer.clientDolphin.clientConnector.commandBatcher.deferMillis = 30
writer.clientDolphin.clientConnector.commandBatcher.mergeValueChanges = false
writer.clientDolphin.clientConnector.commandBatcher.maxBatchSize = 10
def serverDolphin = writer.serverDolphin
def clientDolphin = writer.clientDolphin

def bus = new EventBus()
SmallFootprintAction action = new SmallFootprintAction(serverDolphin: serverDolphin).subscribedTo(bus)
serverDolphin.serverConnector.register action

def updateConfig = new JavaFxInMemoryConfig()
def updateServerDolphin = updateConfig.serverDolphin
def updateClientDolphin = updateConfig.clientDolphin

SmallFootprintAction updaterAction = new SmallFootprintAction(serverDolphin: updateServerDolphin).subscribedTo(bus)
updateServerDolphin.serverConnector.register updaterAction

SmallFootprintView.show clientDolphin, updateClientDolphin