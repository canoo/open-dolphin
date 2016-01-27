/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

class MyProps {

    static class ATT {
        static final String TITLE           = 'title'
        static final String PURPOSE         = 'purpose'
        static final String NAME            = 'name'
        static final String LASTNAME        = 'lastname'
        static final String OBSERVATIONS    = 'observations'
    }

    static class CMD {
        static final String SET_TITLE   = MyProps.unique('setTitle')
        static final String SET_PURPOSE = MyProps.unique('setPurpose')
    }

    static class PM_ID {
        static final String MOLD        = MyProps.unique('mold')
    }

    static String unique(String s) {
        MyProps.class.name + '.'+ s
    }

}
