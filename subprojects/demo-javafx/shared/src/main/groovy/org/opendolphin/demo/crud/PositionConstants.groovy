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
package org.opendolphin.demo.crud

class PositionConstants {

    static class TYPE {
        public static final String POSITION         = PositionConstants.unique 'type'
    }

    static class ATT {
        public static final String PORTFOLIO_ID     = 'portfolioId'
        public static final String INSTRUMENT       = 'instrument'
        public static final String WEIGHT           = 'weight'
    }

    static class CMD {
        public static final String PULL             = PositionConstants.unique 'pull'
    }

    static String unique(String s) { PositionConstants.class.name + '.'+ s }


}
