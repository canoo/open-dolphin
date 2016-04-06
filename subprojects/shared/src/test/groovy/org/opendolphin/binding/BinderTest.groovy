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

import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.BasePresentationModel
import groovy.beans.Bindable

import static org.opendolphin.binding.Binder.bind

class BinderTest extends GroovyTestCase {
    void testPojoBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePojo = new BindablePojo(value: initialValue)
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == newValue
    }

    // TODO (DOL-93) remove legacy code
    void testPojoBindingUsingConverter_Closure_OldStyle() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo, { "[" + it + "]"}

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testPojoBindingUsingConverter_Closure() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo using { "[" + it + "]"} to "value" of targetPojo

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    // TODO (DOL-93) remove legacy code
    void testPojoBindingUsingConverter_Interface_OldStyle() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "value" of sourcePojo to "value" of targetPojo, converter

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testPojoBindingUsingConverter_Interface() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "value" of sourcePojo using converter to "value" of targetPojo

        assert targetPojo.value == "[initialValue]"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "[newValue]"
    }

    // Converter chaining is possible. Currently only the last converter is taken into account
    void testPojoBindingUsingConverter_Chaining() {
        given:
        def sourcePojo = new BindablePojo(value: 'initialValue')
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "value" of sourcePojo using { "[" + it + "]"} using {"<" + it + ">"} to "value" of targetPojo

        assert targetPojo.value == "<initialValue>"

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPojo.value == "<newValue>"
    }

    void testAttributeBindingPmToPojo() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text')])
        sourcePm.text.value = initialValue
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo

        assert targetPojo.value == initialValue

        def newValue = "newValue"
        sourcePm.text.value = newValue

        then:
        assert targetPojo.value == newValue
    }

    // TODO (DOL-93) remove legacy code
    void testAttributeBindingPmToPojoUsingConverter_Closure_OldStyle() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo, {"[" + it + "]"}

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testAttributeBindingPmToPojoUsingConverter_Closure() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        assert !targetPojo.value

        when:
        bind "text" of sourcePm using {"[" + it + "]"} to "value" of targetPojo

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

    // TODO (DOL-93) remove legacy code
    void testAttributeBindingPmToPojoUsingConverter_Interface_OldStyle() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "text" of sourcePm to "value" of targetPojo, converter

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

    void testAttributeBindingPmToPojoUsingConverter_Interface() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetPojo.value

        when:
        bind "text" of sourcePm using converter to "value" of targetPojo

        assert targetPojo.value == "[initialValue]"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "[newValue]"
    }

    // Converter chaining is possible. Currently only the last converter is taken into account
    void testAttributeBindingPmToPojoUsingConverter_Chaining() {
        given:
        def sourcePm = new BasePresentationModel("1",[new SimpleAttribute('text', 'initialValue')])
        def targetPojo = new BindablePojo()

        when:
        bind "text" of sourcePm using {"[" + it + "]"} using {"<" + it + ">"} to "value" of targetPojo

        assert targetPojo.value == "<initialValue>"

        sourcePm.text.value = "newValue"

        then:
        assert targetPojo.value == "<newValue>"
    }

    void testAttributeBindingPojoToPm() {
        given:
        def initialValue = "Andres&Dierk"
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])

        sourcePojo.value = initialValue

        assert !targetPm.text.value

        when:
        bind "value" of sourcePojo to "text" of targetPm

        assert targetPm.text.value == initialValue

        def newValue = "newValue"
        sourcePojo.value = newValue

        then:
        assert targetPm.text.value == newValue
    }

    // TODO (DOL-93) remove legacy code
    void testAttributeBindingPojoToPmUsingConverter_Closure_OldStyle() {
        given:
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])

        sourcePojo.value = "initialValue"
        assert !targetPm.text.value

        when:
        bind "value" of sourcePojo to "text" of targetPm, {"[" + it + "]"}

        assert targetPm.text.value == "[initialValue]"

        sourcePojo.value = "newValue"

        then:
        assert targetPm.text.value == "[newValue]"
    }

    void testAttributeBindingPojoToPmUsingConverter_Closure() {
        given:
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])

        sourcePojo.value = "initialValue"
        assert !targetPm.text.value

        when:
        bind "value" of sourcePojo using {"[" + it + "]"} to "text" of targetPm

        assert targetPm.text.value == "[initialValue]"

        sourcePojo.value = "newValue"

        then:
        assert targetPm.text.value == "[newValue]"
    }

    // TODO (DOL-93) remove legacy code
    void testAttributeBindingPojoToPmUsingConverter_Interface_OldStyle() {
        given:
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        sourcePojo.value = "initialValue"
        assert !targetPm.text.value

        when:
        bind "value" of sourcePojo to "text" of targetPm, converter

        assert targetPm.text.value == "[initialValue]"

        sourcePojo.value = "newValue"

        then:
        assert targetPm.text.value == "[newValue]"
    }

    void testAttributeBindingPojoToPmUsingConverter_Interface() {
        given:
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        sourcePojo.value = "initialValue"
        assert !targetPm.text.value

        when:
        bind "value" of sourcePojo using converter to "text" of targetPm

        assert targetPm.text.value == "[initialValue]"

        sourcePojo.value = "newValue"

        then:
        assert targetPm.text.value == "[newValue]"
    }

    // Converter chaining is possible. Currently only the last converter is taken into account
    void testAttributeBindingPojoToPmUsingConverter_Chaining() {
        given:
        def sourcePojo = new BindablePojo()
        def targetPm = new BasePresentationModel("1",[new SimpleAttribute('text')])
        sourcePojo.value = "initialValue"

        when:
        bind "value" of sourcePojo using {"[" + it + "]"} using {"<" + it + ">"} to "text" of targetPm

        assert targetPm.text.value == "<initialValue>"

        sourcePojo.value = "newValue"

        then:
        assert targetPm.text.value == "<newValue>"
    }

}

class BindablePojo {
    @Bindable String value
}

class SimpleAttribute extends BaseAttribute {
    SimpleAttribute(String propertyName) {
        super(propertyName)
    }

    SimpleAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    @Override
    String getOrigin() {
        return "S"
    }
}