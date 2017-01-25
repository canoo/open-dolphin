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

class BasePresentationModelSpec extends Specification {

    def "finding all tag attributes for a given property name"() {
        given:
        def value = new MyAttribute("name",  null, Tag.VALUE)
        def label = new MyAttribute("name",  null, Tag.LABEL)
        def other = new MyAttribute("other", null, Tag.VALUE)


        when:
        def fullModel  = new BasePresentationModel("1", [value, label, other])
        def emptyModel = new BasePresentationModel("2", [])

        then:

        fullModel.findAllAttributesByPropertyName("name")         == [value, label]
        fullModel.findAllAttributesByPropertyName("other")        == [other]
        fullModel.findAllAttributesByPropertyName("no-such-name") == []
        fullModel.findAllAttributesByPropertyName(null)           == []

        emptyModel.findAllAttributesByPropertyName("name")        == []

        value.getPresentationModel().findAllAttributesByPropertyName(value.getPropertyName()) == [value, label]

    }

    def "you can not add an attribute to two presentation models"() {
        given:
        def model1 = new BasePresentationModel("1", [])
        def model2 = new BasePresentationModel("2", [])
        def attribute = new MyAttribute("name")

        when:
        model1._internal_addAttribute(attribute)
        model2._internal_addAttribute(attribute)

        then:
        thrown IllegalStateException
    }

    def "you can not add two attributes with the same property name and tag"() {
        given:
        def model = new BasePresentationModel("1", [])
        def attribute1 = new MyAttribute("name", null, Tag.VALUE)
        def attribute2 = new MyAttribute("name", null, Tag.VALUE)

        when:
        model._internal_addAttribute(attribute1)
        model._internal_addAttribute(attribute2)

        then:
        thrown IllegalStateException
    }


    def "attributes are accessible as properties"() {
        given:

        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:

        pm.attributes.find { it.propertyName == 'myPropName' } == baseAttribute // old style
        pm.myPropName == baseAttribute  // new style
    }

    def "missing attributes throw MissingPropertyException on access"() {
        given:
        def baseAttribute = new MyAttribute('myPropName')
        def pm = new BasePresentationModel('1',[baseAttribute])

        when:
        pm.noSuchAttributeName

        then:
        def exception = thrown(MissingPropertyException)
        exception.message.contains('noSuchAttributeName')
    }

    def "getValue(name,int) convenience method"() {
        given:
        def baseAttribute = new MyAttribute('myInt',1)
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:
        1 == pm.getValue('myInt', 0)
        0 == pm.getValue('no-such-property', 0)
    }

    def "finder methods"() {
        given:
        def baseAttribute = new MyAttribute('myInt',1)
        baseAttribute.qualifier = 'myQualifier'
        def pm = new BasePresentationModel('1',[baseAttribute])

        expect:
        null == pm.findAttributeByPropertyNameAndTag('myInt', null) // null safe
        null == pm.findAttributeByPropertyNameAndTag(null, null) // null safe

        null == pm.findAttributeByQualifier('no-such-qualifier')
        baseAttribute == pm.findAttributeByQualifier('myQualifier')

        null == pm.findAttributeById(Long.MAX_VALUE.toString())
        baseAttribute == pm.findAttributeById(baseAttribute.id)
    }

    def "dirty attributes make the pm dirty too"() {
        given:

        def attr1 = new MyAttribute('one')
        def attr2 = new MyAttribute('two', 2)
        def model = new BasePresentationModel('model', [attr1, attr2])
        def changeListener = Mock(PropertyChangeListener)
        model.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY, changeListener)

        assert !attr1.dirty
        assert !attr2.dirty
        assert !model.dirty

        when:

        attr1.value = 1

        then:

        1 * changeListener.propertyChange(_)
        attr1.dirty
        model.dirty

        when:

        attr1.value = null

        then:

        1 * changeListener.propertyChange(_)
        !attr1.dirty
        !model.dirty

        when:

        attr2.value = 2

        then:

        0 * changeListener.propertyChange(_)
        !attr2.dirty
        !model.dirty

        when:

        attr2.value = 3

        then:

        1 * changeListener.propertyChange(_)
        attr2.dirty
        model.dirty

        when:

        attr1.value = 4

        then:

        0 * changeListener.propertyChange(_)
        attr1.dirty
        attr2.dirty
        model.dirty
    }

    def "rebasing a pm rebases all its attributes"() {
        given:
        def attr1 = new MyAttribute('one', 1)
        def attr2 = new MyAttribute('two', 2)
        def model = new BasePresentationModel('model', [attr1, attr2])

        assert !model.dirty

        when:
        def newValue = 3
        attr1.value = newValue

        then:
        model.dirty

        when:
        model.rebase()

        then:
        assert ! model.dirty
        assert attr1.value == newValue
        assert attr1.baseValue == newValue
    }
}