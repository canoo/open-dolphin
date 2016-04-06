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

class LazyLoadingConstants {

    static class TYPE {
        public static final String LAZY = 'LAZY'
    }

    static class CMD {
        public static final String PULL = "fullDataRequest"
    }

    static class ATT {
        public static final String ID           = 'id'
        public static final String FIRST        = 'first'
        public static final String LAST         = 'last'
        public static final String FIRST_LAST   = 'first_last'
        public static final String LAST_FIRST   = 'last_first'
        public static final String CITY         = 'city'
        public static final String PHONE        = 'phone'
    }

}
