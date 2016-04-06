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

import org.opendolphin.core.BasePresentationModel
import spock.lang.Specification

import static groovy.test.GroovyAssert.shouldFail
import static org.opendolphin.binding.Binder.bind
import static org.opendolphin.binding.Binder.unbind

class BinderSpec extends Specification {
    def 'bind and unbind on PMs'() {
        given:
        def sourcePm = new BasePresentationModel("1", [new SimpleAttribute('text')])
        sourcePm.text.value = "test"
        def targetPm = new BasePresentationModel("2", [new SimpleAttribute('text')])

        when:
        def fail = shouldFail(IllegalArgumentException) {
            bind "text" of sourcePm to "text" of targetPm
        }

        then:
        fail.message.contains("You attempted to bind a presentation model attribute against a second one.")

    }

    def 'bind and unbind on POJOs'() {
        given:
        def initialValue = 'pojo'
        def sourcePojo = new BindablePojo(value: initialValue)
        def targetPojo = new BindablePojo()

        when:

        bind 'value' of sourcePojo to 'value' of targetPojo

        then:

        // values are sync immediately when bound
        targetPojo.value == sourcePojo.value

        when:

        sourcePojo.value = 'newValue'

        then:

        // values are sync on source change
        targetPojo.value == sourcePojo.value

        when:

        unbind 'value' of sourcePojo from 'value' of targetPojo
        sourcePojo.value = 'anotherValue'

        then:

        sourcePojo.value == 'anotherValue'
        targetPojo.value != sourcePojo.value
        targetPojo.value == 'newValue'

        !sourcePojo.getPropertyChangeListeners('value').find() { it instanceof BinderPropertyChangeListener }
    }

    def 'bind and unbind on POJOs and PMs: Source POJO, target PM'() {
        given:
        def initialValue = 'pojo'
        def sourcePojo = new BindablePojo()
        sourcePojo.value = initialValue;
        def targetPm = new BasePresentationModel("1", [new SimpleAttribute('text')])

        when:
        bind 'value' of sourcePojo to 'text' of targetPm

        then:
        targetPm.text.value == sourcePojo.value


        when:
        sourcePojo.value = 'newValue'

        then:
        targetPm.text.value == sourcePojo.value


        when:
        unbind 'value' of sourcePojo from 'text' of targetPm
        sourcePojo.value = 'anotherValue'

        then:
        sourcePojo.value == 'anotherValue'
        targetPm.text.value == 'newValue'

        !sourcePojo.getPropertyChangeListeners('value').find() { it instanceof BinderPropertyChangeListener }
    }

    def 'bind and unbind on POJOs and PMs: Source PM, target POJO'() {
        given:
        def initialValue = 'pojo'
        def sourcePm = new BasePresentationModel("1", [new SimpleAttribute('text')])
        sourcePm.text.value = initialValue
        def targetPojo = new BindablePojo()

        when:

        bind 'text' of sourcePm to 'value' of targetPojo

        then:

        // values are sync immediately when bound
        targetPojo.value == sourcePm.text.value

        when:

        sourcePm.text.value = 'newValue'

        then:

        // values are sync on source change
        targetPojo.value == sourcePm.text.value

        when:

        unbind 'text' of sourcePm from 'value' of targetPojo
        sourcePm.text.value = 'anotherValue'

        then:

        sourcePm.text.value == 'anotherValue'
        targetPojo.value != sourcePm.text.value
        targetPojo.value == 'newValue'

        !sourcePm.text.getPropertyChangeListeners('value').find() { it instanceof BinderPropertyChangeListener }
    }

    void "binding from a non present PM attribute throws meaningful exception"() {

        given:
        def sourcePM = new BasePresentationModel("1", [])
        def targetPojo = new BindablePojo()

        when:
        def fail = shouldFail(IllegalArgumentException) {
            bind "nonPresentAttribute" of sourcePM to "value" of targetPojo
        }

        then:
        fail.message.contains("there is no attribute for property name 'nonPresentAttribute'")
    }

    void "binding to a non present PM attribute with source=POJO throws meaningful exception"() {

        given:
        def sourcePojo = new BindablePojo(value: "test")
        def targetPM = new BasePresentationModel("1", [new SimpleAttribute('text')])

        when:
        def fail = shouldFail(IllegalArgumentException) {
            bind "value" of sourcePojo to "nonPresentAttribute" of targetPM
        }

        then:
        fail.message.contains("there is no attribute named 'nonPresentAttribute'")
    }

    void "binding to a non present PM attribute with source=PM throws meaningful exception"() {

        given:
        def sourcePm = new BasePresentationModel("1", [new SimpleAttribute('text')])
        def targetPM = new BasePresentationModel("2", [new SimpleAttribute('text')])

        when:
        def fail = shouldFail(IllegalArgumentException) {
            bind "text" of sourcePm to "nonPresentAttribute" of targetPM
        }

        then:
        fail.message.contains("You attempted to bind a presentation model attribute against a second one.")
    }

    void "binding from a non present POJO property throws meaningful exception"() {

        given:
        def sourcePojo = new BindablePojo()
        def targetPojo = new BindablePojo()

        when:
        def fail = shouldFail(IllegalArgumentException) {
            bind "nonPresentProperty" of sourcePojo to "value" of targetPojo
        }

        then:
        fail.message.contains("there is no property named 'nonPresentProperty'")
    }

    void "binding to a non present POJO property with source=POJO throws meaningful exception"() {

        given:
        def sourcePojo = new BindablePojo(value: "test")
        def targetPojo = new BindablePojo()

        when:
        def fail = shouldFail(MissingPropertyException) {
            bind "value" of sourcePojo to "nonPresentProperty" of targetPojo
        }

        then:
        fail.message.contains("No such property: nonPresentProperty")
    }

    void "binding to a non present POJO property with source=PM throws meaningful exception"() {

        given:
        def sourcePm = new BasePresentationModel("1", [new SimpleAttribute('text')])
        def targetPojo = new BindablePojo()

        when:
        def fail = shouldFail(MissingPropertyException) {
            bind "text" of sourcePm to "nonPresentProperty" of targetPojo
        }

        then:
        fail.message.contains("No such property: nonPresentProperty")
    }


}