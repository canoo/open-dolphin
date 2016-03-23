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

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static org.opendolphin.core.Tag.ENABLED
import static org.opendolphin.core.Tag.LABEL
import static org.opendolphin.demo.MasterDetailConstants.*
import static com.xlson.groovycsv.CsvParser.parseCsv

public class MasterDetailActions extends DolphinServerAction {

    @Override
    public void registerIn(ActionRegistry registry) {

        def soccerPlayersData = getSoccerPlayerData()
        registry.register CMD_PULL, { NamedCommand command, List<Command> response ->
            soccerPlayersData.eachWithIndex { SoccerPlayer soccerPlayer, index ->
                def pmId = index.toString()
                presentationModel(pmId, TYPE_SOCCERPLAYER, new DTO(
                        new Slot(ATT_RANK,          soccerPlayer.rank,          qualify(pmId, ATT_RANK)),
                        new Slot(ATT_RANK,          false,                      null,   ENABLED),
                        new Slot(ATT_RANK,          "Rank",                     null,   LABEL),

                        new Slot(ATT_NAME,          soccerPlayer.name,          qualify(pmId, ATT_NAME)),
                        new Slot(ATT_NAME,          true,                      null,    ENABLED),
                        new Slot(ATT_NAME,          "Name",                     null,   LABEL),

                        new Slot(ATT_YEAROFBIRTH,   soccerPlayer.yearOfBirth,   qualify(pmId, ATT_YEAROFBIRTH)),
                        new Slot(ATT_YEAROFBIRTH,   false,                      null,   ENABLED),
                        new Slot(ATT_YEAROFBIRTH,   "Year of birth",            null,   LABEL),

                        new Slot(ATT_COUNTRY,       soccerPlayer.country,       qualify(pmId, ATT_COUNTRY)),
                        new Slot(ATT_COUNTRY,       false,                      null,   ENABLED),
                        new Slot(ATT_COUNTRY,       "Country",                  null,   LABEL),

                        new Slot(ATT_MATCHESFIFA,   soccerPlayer.matchesFIFA,   qualify(pmId, ATT_MATCHESFIFA)),
                        new Slot(ATT_MATCHESFIFA,   false,                      null,   ENABLED),
                        new Slot(ATT_MATCHESFIFA,   "FIFA matches",             null,   LABEL),

                        new Slot(ATT_MATCHESRSSSF,  soccerPlayer.matchesRSSSF,  qualify(pmId, ATT_MATCHESRSSSF)),
                        new Slot(ATT_MATCHESRSSSF,  false,                      null,   ENABLED),
                        new Slot(ATT_MATCHESRSSSF,  "RSSSF matches",            null,   LABEL)
                ))

            }
        }
    }

    private List<SoccerPlayer> getSoccerPlayerData() {
        def soccerPlayerList = new ArrayList<SoccerPlayer>()

        def resourceString = MasterDetailActions.getResource("soccerplayers/SoccerPlayer.csv").getText('UTF-8')

        def data = parseCsv(resourceString, autoDetect: true)
        for (line in data) {
            soccerPlayerList.add new SoccerPlayer(
                    name:           line[ATT_NAME],
                    rank:           line[ATT_RANK],
                    yearOfBirth:    line[ATT_YEAROFBIRTH],
                    country:        line[ATT_COUNTRY],
                    matchesFIFA:    line[ATT_MATCHESFIFA],
                    matchesRSSSF:   line[ATT_MATCHESRSSSF]
            )
        }
        return soccerPlayerList
    }

    class SoccerPlayer {
        def name
        def rank
        def yearOfBirth
        def country
        def matchesFIFA
        def matchesRSSSF
    }
}
