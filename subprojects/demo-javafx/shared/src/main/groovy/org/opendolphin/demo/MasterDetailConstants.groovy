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

class MasterDetailConstants {
    static final String CMD_PULL            = unique 'pullSoccerPlayers'

    static final String TYPE_SOCCERPLAYER   = unique('soccerPlayer')

    static final String ATT_NAME            = 'Name'
    static final String ATT_RANK            = 'Rank'
    static final String ATT_YEAROFBIRTH     = 'YearOfBirth'
    static final String ATT_COUNTRY         = 'Country'
    static final String ATT_MATCHESFIFA     = 'Matches(FIFA)'
    static final String ATT_MATCHESRSSSF    = 'Matches(RSSSF)'


    static String unique(String part) {
        MasterDetailConstants.name + '-' + part
    }

    static String qualify(String id, String attributeName) {
        unique id + '.' + attributeName
    }
}
