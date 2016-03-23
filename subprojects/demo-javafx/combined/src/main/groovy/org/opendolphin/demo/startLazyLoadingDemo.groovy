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
package org.opendolphin.demo

import org.opendolphin.LogConfig

def numEntries = 10000;

def config = new JavaFxInMemoryConfig()
def serverDolphin = config.serverDolphin
def clientDolphin = config.clientDolphin
clientDolphin.clientConnector.sleepMillis = 0
clientDolphin.clientConnector.commandBatcher.deferMillis = 30

serverDolphin.action   LazyLoadingConstants.CMD.PULL, new FullDataRequestCommandHandler(numEntries)
serverDolphin.register new LazyLoadingAction(numEntries)

LogConfig.noLogs()

LazyLoadingView.show clientDolphin
