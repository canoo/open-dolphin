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

import org.opendolphin.core.client.ClientAttribute

class BindClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropertyName
    final Converter converter

    BindClientOtherOfAble(ClientAttribute attribute, String targetPropertyName, Converter converter) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

}
