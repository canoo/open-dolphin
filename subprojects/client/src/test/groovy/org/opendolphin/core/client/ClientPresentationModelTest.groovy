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
package org.opendolphin.core.client;

public class ClientPresentationModelTest extends GroovyTestCase{

    void testStandardCtor() {
        def model = new ClientPresentationModel('x',[])
        assert model.id == 'x'
    }
    void testNullIdCtor() {
        def model1 = new ClientPresentationModel([])
        def model2 = new ClientPresentationModel([])
        assert model1.id != model2.id
    }
    void testBadIdCtor() {
        shouldFail(IllegalArgumentException) {
            new ClientPresentationModel("1000-AUTO-CLT",[])
        }
    }
}
