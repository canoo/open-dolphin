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

import org.opendolphin.core.comm.*;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.action.ServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;

import static org.opendolphin.demo.team.TeamMemberConstants.*;

/**
 * The sole responsibility of the TeamBusRelease is to release any "push action" that
 * may currently listen to push events in this session such that commands that are waiting on
 * the client side can be sent.
 * Since it may run concurrently in the same session, it must not have access to any unsecured mutable shared state.
 * For that reason, it is _not_ extending DolphinServerAction.
 * Sharing the teamBus is safe.
 */
public class TeamBusRelease implements ServerAction {

    private EventBus teamBus;

    public TeamBusRelease(EventBus teamBus) {
        this.teamBus = teamBus;
    }

    @Override
    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_RELEASE, new CommandHandler<SignalCommand>() {
            @Override
            public void handleCommand(SignalCommand command, List<Command> response) {
                // seeing the response is ok but we should not see any serverDolphin here
                teamBus.publish(null, new TeamEvent(TeamEvent.Type.RELEASE, null));
            }
        });

    }
}


