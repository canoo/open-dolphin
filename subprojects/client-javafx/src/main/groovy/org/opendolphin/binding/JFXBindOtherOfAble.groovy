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
package org.opendolphin.binding

class JFXBindOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName
    final Converter converter

    JFXBindOtherOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName, Converter converter) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    void of(Object target) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

}
