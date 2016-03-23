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

class JFXBindToAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final Converter converter

    JFXBindToAble(javafx.scene.Node source, String sourcePropertyName, Converter converter = null) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.converter = converter
    }

    JFXBindOtherOfAble to(String targetPropertyName) {
        new JFXBindOtherOfAble(source, sourcePropertyName, targetPropertyName, converter)
    }

    JFXBindToAble using(Closure converter) {
        using(new ConverterAdapter(converter))
    }

    JFXBindToAble using(Converter converter) {
        return new JFXBindToAble(source, sourcePropertyName, converter)
    }

}
