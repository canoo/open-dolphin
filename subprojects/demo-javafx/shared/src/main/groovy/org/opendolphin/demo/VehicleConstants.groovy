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

/**
 * Only here to make semantic dependencies code dependencies
 */
class VehicleConstants {

    static final String CMD_PULL    = unique 'pullVehicles'
    static final String CMD_UPDATE  = unique 'longPoll'
    static final String CMD_CLEAR   = unique 'clear'

    static final String ID_SELECTED = unique 'selected'

    static final String TYPE_VEHICLE = unique('')

    static final String ATT_X       = "x"
    static final String ATT_Y       = "y"
    static final String ATT_WIDTH   = "width"
    static final String ATT_HEIGHT  = "height"
    static final String ATT_ROTATE  = "rotate"
    static final String ATT_COLOR   = "fill"

    static final List<String> ALL_ATTRIBUTES = [ATT_X, ATT_Y, ATT_WIDTH, ATT_HEIGHT, ATT_ROTATE, ATT_COLOR]

    static String unique(String part) {
        VehicleConstants.name + '-' + part
    }

    static String qualify(String id, String attributeName) {
        unique id + '.' + attributeName
    }
}
