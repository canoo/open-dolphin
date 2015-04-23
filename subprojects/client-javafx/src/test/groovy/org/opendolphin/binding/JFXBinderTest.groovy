/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

import groovy.beans.Bindable
import javafx.scene.paint.Color
import org.opendolphin.core.BasePresentationModel
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute

import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.GClientAttribute
import org.opendolphin.core.client.GClientPresentationModel

import static org.opendolphin.binding.JFXBinder.*

class JFXBinderTest extends GroovyTestCase {

    void testNodeBinding() {
        given:
        def initialValue = "Andres&Dierk"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        assert !targetLabel.text

        when:
        bind "text" of sourceLabel to "text" of targetLabel

        assert targetLabel.text == initialValue

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == newValue
    }

    // TODO (DOL-93) remove legacy code
    void testNodeBindingWithConverter_Closure_OldStyle() {
        given:
        def initialValue = "initialValue"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        assert !targetLabel.text

        when:
        bind "text" of sourceLabel to "text" of targetLabel, { "[" + it + "]" }

        assert targetLabel.text == "[initialValue]"

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == "[newValue]"
    }

    void testNodeBindingWithConverter_Closure() {
        given:
        def initialValue = "initialValue"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        assert !targetLabel.text

        when:
        bind "text" of sourceLabel using { "[" + it + "]" } to "text" of targetLabel

        assert targetLabel.text == "[initialValue]"

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == "[newValue]"
    }

    // TODO (DOL-93) remove legacy code
    void testNodeBindingWithConverter_Interface_OldStyle() {
        given:
        def initialValue = "initialValue"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetLabel.text

        when:
        bind "text" of sourceLabel to "text" of targetLabel, converter

        assert targetLabel.text == "[initialValue]"

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == "[newValue]"
    }

    void testNodeBindingWithConverter_Interface() {
        given:
        def initialValue = "initialValue"
        def sourceLabel = new javafx.scene.control.Label()
        sourceLabel.text = initialValue
        def targetLabel = new javafx.scene.control.Label()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return "[" + value + "]"
            }
        }
        assert !targetLabel.text

        when:
        bind "text" of sourceLabel using converter to "text" of targetLabel

        assert targetLabel.text == "[initialValue]"

        def newValue = "newValue"
        sourceLabel.text = newValue

        then:
        assert targetLabel.text == "[newValue]"
    }

    void testPojoBinding() {
        given:

        def bean = new PojoBean(value: 'Dolphin')
        def label = new javafx.scene.control.Label()

        when:

        bindInfo 'value' of bean to 'text' of label

        then:

        assert label.text == 'Dolphin'
    }

    // TODO (DOL-93) remove legacy code
    void testPojoBindingWithConverterClosure_OldStyle() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        when:

        bindInfo 'value' of bean to 'textFill' of label, { it == 'white' ? Color.WHITE : Color.BLACK }

        then:

        assert label.textFill == Color.WHITE

        nextWhen:

        bean.value = 'foo'

        nextThen:

        assert label.textFill == Color.BLACK
    }

    void testPojoBindingWithConverterClosure() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        when:

        bindInfo 'value' of bean using { it == 'white' ? Color.WHITE : Color.BLACK } to 'textFill' of label

        then:

        assert label.textFill == Color.WHITE

        nextWhen:

        bean.value = 'foo'

        nextThen:

        assert label.textFill == Color.BLACK
    }

    // TODO (DOL-93) remove legacy code
    void testPojoBindingWithConverter_Interface_OldStyle() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return value == 'white' ? Color.WHITE : Color.BLACK
            }
        }
        when:

        bindInfo 'value' of bean to 'textFill' of label, converter

        then:

        assert label.textFill == Color.WHITE

        nextWhen:

        bean.value = 'foo'

        nextThen:

        assert label.textFill == Color.BLACK
    }

    void testPojoBindingWithConverter_Interface() {
        given:

        def bean = new PojoBean(value: 'white')
        def label = new javafx.scene.control.Label()

        def converter = new Converter() {
            @Override
            Object convert(Object value) {
                return value == 'white' ? Color.WHITE : Color.BLACK
            }
        }
        when:

        bindInfo 'value' of bean using converter to 'textFill' of label

        then:

        assert label.textFill == Color.WHITE

        nextWhen:

        bean.value = 'foo'

        nextThen:

        assert label.textFill == Color.BLACK
    }

    void testPresentationModelBinding() {
        final Tag MESSAGE = Tag.tagFor.MESSAGE
        ClientPresentationModel sourceModel = new GClientPresentationModel('source', [new GClientAttribute('attr_1', "", null, MESSAGE)])
        def targetLabel = new javafx.scene.control.Label()

        bind 'attr_1', MESSAGE of sourceModel to 'text' of targetLabel
        sourceModel.getAt('attr_1', MESSAGE).value = 'dummy'
        assert targetLabel.text == 'dummy'
    }

    // TODO (DOL-93) remove legacy code
    void testPresentationModelBindingUsingConverter_OldStyle() {
        ClientPresentationModel sourceModel = new GClientPresentationModel('source', [new GClientAttribute('attr_1', "", null, Tag.tagFor.MESSAGE)])
        def targetLabel = new javafx.scene.control.Label()

        bind 'attr_1', Tag.tagFor.MESSAGE of sourceModel to 'text' of targetLabel, { 'my' + it }
        sourceModel.getAt('attr_1', Tag.tagFor.MESSAGE).value = 'Dummy'
        assert targetLabel.text == 'myDummy'
    }

    void testPresentationModelBindingUsingConverter() {
        ClientPresentationModel sourceModel = new GClientPresentationModel('source', [new GClientAttribute('attr_1', "", null, Tag.tagFor.MESSAGE)])
        def targetLabel = new javafx.scene.control.Label()

        bind 'attr_1', Tag.tagFor.MESSAGE of sourceModel using { 'my' + it } to 'text' of targetLabel
        sourceModel.getAt('attr_1', Tag.tagFor.MESSAGE).value = 'Dummy'
        assert targetLabel.text == 'myDummy'
    }

    void testUnbindInfo() {
        ClientPresentationModel sourceModel = new GClientPresentationModel('source', [new GClientAttribute('text', "")])
        def targetLabel = new javafx.scene.control.Label()
        bindInfo 'dirty' of sourceModel to 'text' of targetLabel
        assert 'false' == targetLabel.text
        sourceModel.getAt('text').value = 'newValue'
        assert 'true' == targetLabel.text
        unbindInfo 'dirty' of sourceModel from 'text' of targetLabel
        sourceModel.getAt('text').value = ''
        assert 'true' == targetLabel.text
    }

    void testUnbindFromFX() {
        def sourceLabel = new javafx.scene.control.Label()
        ClientAttribute attribute = new GClientAttribute('text', '')
        bind 'text' of sourceLabel to 'value' of attribute
        sourceLabel.text = 'newValue'
        assert 'newValue' == attribute.value
        unbind 'text' of sourceLabel from 'value' of attribute
        sourceLabel.text = 'anotherValue'
        assert 'newValue' == attribute.value

    }

    void testUnbindFromClientPresentationModel() {
        def targetLabel = new javafx.scene.control.Label()
        ClientPresentationModel model = new GClientPresentationModel('model', [new GClientAttribute('attr', '')])
        bind 'attr' of model to 'text' of targetLabel
        model.getAt('attr').value = 'newValue'
        assert 'newValue' == targetLabel.text
        unbind 'attr' of model from 'text' of targetLabel
        model.getAt('attr').value = 'anotherValue'
        assert 'newValue' == targetLabel.text
    }

    void testBindAndUnbindFromNodeToClientPresentationModel() {
        def sourceLabel = new javafx.scene.control.Label()
        ClientPresentationModel targetPm = new GClientPresentationModel('model', [new GClientAttribute('attr', '')])
        bind 'text' of sourceLabel to 'attr' of targetPm
        sourceLabel.text = 'newValue'
        assert 'newValue' == targetPm.attr.value
        unbind 'text' of sourceLabel from 'attr' of targetPm
        sourceLabel.text = 'anotherValue'
        assert 'newValue' == targetPm.attr.value
    }

    void testUnbindFromPresentationModel() {
        def targetLabel = new javafx.scene.control.Label()
        PresentationModel model = new BasePresentationModel('model', [new GClientAttribute('attr', '')])
        bind 'attr' of model to 'text' of targetLabel
        model.getAt('attr').value = 'newValue'
        assert 'newValue' == targetLabel.text
        unbind 'attr' of model from 'text' of targetLabel
        model.getAt('attr').value = 'anotherValue'
        assert 'newValue' == targetLabel.text
    }

    void testUnbindFromPojo() {
        def targetLabel = new javafx.scene.control.Label()
        def pojo = new PojoBean()
        bind 'value' of pojo to 'text' of targetLabel
        pojo.value = 'newValue'
        assert 'newValue' == targetLabel.text
        unbind 'value' of pojo from 'text' of targetLabel
        pojo.value = 'anotherValue'
        assert 'newValue' == targetLabel.text
    }


}

class PojoBean {
    @Bindable
    String value
}
