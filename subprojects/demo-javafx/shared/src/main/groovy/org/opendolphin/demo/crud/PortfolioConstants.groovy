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

class PortfolioConstants {

    static class TYPE {
        public static final String PORTFOLIO = PortfolioConstants.unique 'type'
    }

    static class ATT {
        public static final String NAME      = 'name'
        public static final String FIXED     = 'fixed'
        public static final String TOTAL     = 'total'
        public static final String DOMAIN_ID = 'domainId'
    }

    static class CMD {
        public static final String PULL      = PortfolioConstants.unique 'pull'
        public static final String UPDATE    = PortfolioConstants.unique 'update'
    }

    static class PM_ID {
        public static final String SELECTED  = PortfolioConstants.unique 'selected'
    }

    static String unique(String s) { PortfolioConstants.class.name + '.'+ s }

    static String pmId(String type, int index) { type + "-" + index}

}
