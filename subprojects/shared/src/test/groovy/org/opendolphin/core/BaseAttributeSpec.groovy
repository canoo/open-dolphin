/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

package org.opendolphin.core

import spock.lang.Specification
import java.beans.PropertyChangeListener

class BaseAttributeSpec extends Specification {

    def "you can set a presentation model"() {
        given:
        def attribute = new MyAttribute("name")
        def model = new BasePresentationModel("1", [])

        when:
        attribute.setPresentationModel(model)

        then:
        model == attribute.getPresentationModel()
    }

    def "you can set the presentation model only once"() {
        given:
        def attribute = new MyAttribute("name")
        attribute.setPresentationModel(new BasePresentationModel("1", []))

        when:
        attribute.setPresentationModel(new BasePresentationModel("2", []))

        then:
        thrown IllegalStateException
    }

    def "simple constructor with null bean and null value"() {
        when:

        def attribute = new MyAttribute("name")

        then:

        attribute.baseValue == null
        attribute.value == null
        attribute.toString().contains "name"
        attribute.toString().contains " [VALUE] "
    }

    def "check isDirty triggers when value changes (initialValue == null)"() {
        given:

        def attribute = new MyAttribute("name")
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, changeListener)
        attribute.value = 'foo'

        then:

        1 * changeListener.propertyChange(_)
        !attribute.baseValue
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = 'foo'

        then:

        0 * changeListener.propertyChange(_)
        !attribute.baseValue
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = null

        then:

        1 * changeListener.propertyChange(_)
        !attribute.baseValue
        !attribute.value
        !attribute.dirty
    }

    def "check isDirty triggers when value changes (initialValue == bar)"() {
        given:

        def attribute = new MyAttribute("name", 'bar')
        def changeListener = Mock(PropertyChangeListener)

        when:

        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, changeListener)
        attribute.value = 'foo'

        then:

        1 * changeListener.propertyChange(_)
        attribute.baseValue == 'bar'
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = 'foo'

        then:

        0 * changeListener.propertyChange(_)
        attribute.baseValue == 'bar'
        attribute.value == 'foo'
        attribute.dirty

        when:

        attribute.value = null

        then:

        0 * changeListener.propertyChange(_)
        attribute.baseValue == 'bar'
        !attribute.value
        attribute.dirty

        when:

        attribute.value = 'bar'

        then:

        1 * changeListener.propertyChange(_)
        attribute.baseValue == 'bar'
        attribute.value == 'bar'
        !attribute.dirty
    }

    def "saving an attribute updates dirty flag and initial value"() {
        given:

        def attribute = new MyAttribute("name", 'bar')
        def dirtyChecker = Mock(PropertyChangeListener)
        def initialValueChecker = Mock(PropertyChangeListener)
        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, dirtyChecker)
        attribute.addPropertyChangeListener(Attribute.BASE_VALUE, initialValueChecker)

        when:

        attribute.value = 'foo'

        then:

        1 * dirtyChecker.propertyChange(_)
        0 * initialValueChecker.propertyChange(_)
        attribute.dirty
        attribute.baseValue == 'bar'
        attribute.value == 'foo'

        when:

        attribute.rebase()

        then:

        1 * dirtyChecker.propertyChange(_)
        1 * initialValueChecker.propertyChange(_)
        !attribute.dirty
        attribute.baseValue == 'foo'
        attribute.value == 'foo'
    }

    def "resetting an attribute updates dirty flag and value"() {
        given:

        def attribute = new MyAttribute("name", 'bar')
        def dirtyChecker = Mock(PropertyChangeListener)
        def valueChecker = Mock(PropertyChangeListener)
        attribute.addPropertyChangeListener(Attribute.DIRTY_PROPERTY, dirtyChecker)
        attribute.addPropertyChangeListener(Attribute.VALUE, valueChecker)

        when:

        attribute.value = 'foo'

        then:

        1 * dirtyChecker.propertyChange(_)
        1 * valueChecker.propertyChange(_)
        attribute.dirty
        attribute.baseValue == 'bar'
        attribute.value == 'foo'

        when:

        attribute.reset()

        then:

        1 * dirtyChecker.propertyChange(_)
        1 * valueChecker.propertyChange(_)
        !attribute.dirty
        attribute.baseValue == 'bar'
        attribute.value == 'bar'
    }

    def "checkValue() auto-maps values"() {
        given:
        def valueAttribute = new MyAttribute("ValueAttribute", "value")
        def mainAttribute = new MyAttribute("MainAttribute", null)
        when:
        mainAttribute.value = valueAttribute
        then:
        mainAttribute.getValue() == "value"
    }

}

class MyAttribute extends BaseAttribute {
    MyAttribute(String propertyName) {
        super(propertyName)
    }

    MyAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    MyAttribute(String propertyName, Object baseValue, Tag tag) {
        super(propertyName, baseValue, tag)
    }

    @Override
    String getOrigin() {
        return "M"
    }
}