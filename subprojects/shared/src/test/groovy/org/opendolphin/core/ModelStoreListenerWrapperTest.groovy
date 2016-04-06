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
package org.opendolphin.core;

public class ModelStoreListenerWrapperTest extends GroovyTestCase {

    void testEquals() {
        ModelStoreListener listener = {} as ModelStoreListener
        def wrapper = new ModelStoreListenerWrapper('no-type', listener)
        assert wrapper == wrapper
        assert wrapper == new ModelStoreListenerWrapper('no-type', listener)
        assert wrapper != new Object()
        assert wrapper != new ModelStoreListenerWrapper('other-type', listener)
        assert wrapper != new ModelStoreListenerWrapper('no-type', {} as ModelStoreListener)
    }
}
