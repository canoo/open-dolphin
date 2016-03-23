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
package org.opendolphin.demo.team

import groovyx.gpars.agent.Agent
import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.TestInMemoryConfig
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
import spock.lang.Specification

import java.util.concurrent.TimeUnit

import static org.opendolphin.demo.team.TeamMemberConstants.ATT_AVAILABLE
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_CONTRACTOR
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_FIRSTNAME
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_FUNCTION
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_LASTNAME
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_SEL_PM_ID
import static org.opendolphin.demo.team.TeamMemberConstants.ATT_WORKLOAD
import static org.opendolphin.demo.team.TeamMemberConstants.CMD_ADD
import static org.opendolphin.demo.team.TeamMemberConstants.CMD_INIT
import static org.opendolphin.demo.team.TeamMemberConstants.ACTION_ON_PUSH
import static org.opendolphin.demo.team.TeamMemberConstants.CMD_REMOVE
import static org.opendolphin.demo.team.TeamMemberConstants.PM_ID_MOLD
import static org.opendolphin.demo.team.TeamMemberConstants.PM_ID_SELECTED
import static org.opendolphin.demo.team.TeamMemberConstants.TYPE_TEAM_MEMBER

class TeamTests extends Specification {

    volatile TestInMemoryConfig app
    ClientDolphin clientDolphin
    Agent<List<DTO>> teamHistory = new Agent<List<DTO>>(new LinkedList<DTO>())
    EventBus teamBus = new EventBus()

    protected TestInMemoryConfig initApp() {
        def result = new TestInMemoryConfig()
        result.serverDolphin.register(new TeamMemberActions(teamBus, teamHistory))
        result.clientDolphin.presentationModel(PM_ID_SELECTED, null, (ATT_SEL_PM_ID): null)
        result.clientDolphin.presentationModel(PM_ID_MOLD, null,
                    (ATT_FIRSTNAME): null,    (ATT_LASTNAME): null,  (ATT_FUNCTION): null,
                    (ATT_AVAILABLE): false, (ATT_CONTRACTOR): false, (ATT_WORKLOAD): 0)
        result.syncPoint(1)
        result
    }

    // make sure we have an in-memory setup with the server-side wired for the team app
    protected void setup() {
        LogConfig.noLogs()
        app = initApp()
        clientDolphin = app.clientDolphin
    }

    // make sure the tests only count as ok if context.assertionsDone() has been reached
    protected void cleanup() {
        clientDolphin.sync { app.assertionsDone() }
        assert app.done.await(2, TimeUnit.SECONDS) // max waiting time for async operations to have finished
    }

    void "initialize app and add a single team member to check the initial values"() {
        when: "we call init"
        app.sendSynchronously CMD_INIT
        then: "we don't have any team members, yet"
        clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 0
        and:  "there is no selection"
        clientDolphin[PM_ID_SELECTED][ATT_SEL_PM_ID].value == null

        when: "we add a team member"
        app.sendSynchronously CMD_ADD
        then: "we see it on the client"
        clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 1

        when: "we hold onto the single one"
        def first = clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).first()
        then: "we see these initial values"
        first[ATT_FIRSTNAME].value    == ''
        first[ATT_LASTNAME].value     == ''
        first[ATT_AVAILABLE].value    == false
        first[ATT_CONTRACTOR].value   == false
        first[ATT_WORKLOAD].value     == 0
        first.isDirty() == false
        and: "the single team member is also the currently selected one"
        clientDolphin[PM_ID_SELECTED][ATT_SEL_PM_ID].value == first.id
    }

    /**
     * Since functional tests against presentation models are UI-toolkit agnostic, this
     * covers not only re-connection outside of the current session but also the "follow-me"
     * use case where a client starts work in one UI (e.g. in the browser) and follows up in
     * a second UI (e.g. JavaFX on the desktop).
     */
    void "re-connect after connection loss retains transient presentation state"() {
        when: "we have a presentation state with a changed value"
        app.sendSynchronously CMD_INIT
        app.sendSynchronously CMD_ADD
        def firstOne = clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).first()
        firstOne[ATT_FIRSTNAME].value = 'changed'
        app.syncPoint(1)

        then: "the transient state is dirty"
        firstOne.isDirty()

        when: "connection is lost and we connect with a new session but same history"
        def secondApp = initApp()
        secondApp.sendSynchronously CMD_INIT
        // new team member is in the history
        def secondOne = secondApp.clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).first()

        then: "we see the no-yet-saved changes and dirtyness as if we had never been away"

        secondApp.clientDolphin.sync {
            assert secondOne != null
            assert secondOne[ATT_FIRSTNAME].value == 'changed'
            assert secondOne[ATT_FIRSTNAME].isDirty()
            assert secondOne.isDirty()
        }

        and: "the selection is not retained (that is on purpose as different apps have different selections)"
        secondApp.clientDolphin[PM_ID_SELECTED][ATT_SEL_PM_ID].value == null
    }

    void "team work: all changes are immediately visible to all team members"() {
        when: "we have an initial presentation state"
        app.sendSynchronously CMD_INIT

        and: "a second worker session connects to work in the same presentation state"
        def secondApp = initApp()
        secondApp.sendSynchronously CMD_INIT
        def secondDolphin = secondApp.clientDolphin

        then: "both see an initially empty state"
        clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 0
        secondDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 0

        when: "one is adding a record and second one is polling (as from a long-poll)"
        app.sendSynchronously CMD_ADD
        secondApp.sendSynchronously ACTION_ON_PUSH

        then: "both see the added record"
        clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 1
        secondDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 1

        when: "second one is changing a value and first one is polling"
        def firstOne  = clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).first()
        def secondOne = secondDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).first()
        secondOne[ATT_FIRSTNAME].value = 'changed'
        //app.sendSynchronously ACTION_ON_PUSH // interestingly, this seems not to be needed

        then: "both see the transient change and the dirtyness"
        clientDolphin.sync {
            firstOne[ATT_FIRSTNAME].value == 'changed'
            firstOne[ATT_FIRSTNAME].isDirty()
            firstOne.isDirty()
        }
        secondDolphin.sync {
            secondOne[ATT_FIRSTNAME].value == 'changed'
            secondOne[ATT_FIRSTNAME].isDirty()
            secondOne.isDirty()
        }

        when: "first one is removing the record and second one polls"
        app.sendSynchronously CMD_REMOVE
        //secondApp.sendSynchronously ACTION_ON_PUSH // interestingly, this seems not to be needed

        then: "both see the empty state again"
        clientDolphin.sync { clientDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 0 }
        secondDolphin.sync { secondDolphin.findAllPresentationModelsByType(TYPE_TEAM_MEMBER).size() == 0 }
    }


}