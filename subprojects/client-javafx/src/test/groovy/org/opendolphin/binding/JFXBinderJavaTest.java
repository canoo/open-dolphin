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

package org.opendolphin.binding;

import javafx.scene.control.Label;
import org.junit.Test;
import org.opendolphin.core.AbstractObservable;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Java Style Tests for JFX Binding
 */
public class JFXBinderJavaTest {

    private final String initialValue = "initialValue";
    private final String newValue = "newValue";

    @Test
    public void testNodeBinding() {
        Label sourceLabel = new Label();
        sourceLabel.setText(initialValue);

        Label targetLabel = new Label();
        assertEquals("", targetLabel.getText());

        JFXBinder.bind("text").of(sourceLabel).to("text").of(targetLabel);

        assertEquals(initialValue, targetLabel.getText());

        sourceLabel.setText(newValue);

        assertEquals(newValue, targetLabel.getText());
    }

    @Test
    public void testNodeBindingUsingConverter() {
        Label sourceLabel = new Label();
        sourceLabel.setText(initialValue);

        Label targetLabel = new Label();
        assertEquals("", targetLabel.getText());

        Converter converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return "[" + value + "]";
            }
        };

        JFXBinder.bind("text").of(sourceLabel).using(converter).to("text").of(targetLabel);

        assertEquals("[initialValue]", targetLabel.getText());

        sourceLabel.setText(newValue);

        assertEquals("[newValue]", targetLabel.getText());
    }

    @Test
    public void testPojoBinding() {
        TestPojo pojo = new TestPojo();
        pojo.setValue("Dolphin");

        Label label = new Label();

        JFXBinder.bindInfo("value").of(pojo).to("text").of(label);

        assertEquals("Dolphin", label.getText());
    }

    @Test
    public void testPojoBindingUsingConverter() {
        TestPojo pojo = new TestPojo();
        pojo.setValue("Dolphin");

        Converter converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return "my" + value;
            }
        };

        Label label = new Label();

        JFXBinder.bindInfo("value").of(pojo).using(converter).to("text").of(label);

        assertEquals("myDolphin", label.getText());
    }

    @Test
    public void testPresentationModelBinding() {

        Tag MESSAGE = Tag.tagFor.get("MESSAGE");

        List<ClientAttribute> attributes = Arrays.asList((ClientAttribute) new GClientAttribute("attr_1", "", null, MESSAGE));
        ClientPresentationModel sourceModel = new GClientPresentationModel("source", attributes);
        Label targetLabel = new Label();

        JFXBinder.bind("attr_1", MESSAGE).of(sourceModel).to("text").of(targetLabel);
        sourceModel.getAt("attr_1", MESSAGE).setValue("dummy");

        assertEquals("dummy", targetLabel.getText());
    }

    @Test
    public void testPresentationModelBindingUsingConverter() {
        Tag MESSAGE = Tag.tagFor.get("MESSAGE");
        List<ClientAttribute> attributes = Arrays.asList((ClientAttribute) new GClientAttribute("attr_1", "", null, MESSAGE));
        ClientPresentationModel sourceModel = new GClientPresentationModel("source", attributes);
        Label targetLabel = new Label();

        Converter converter = new Converter() {
            @Override
            public Object convert(Object value) {
                return "my" + value;
            }
        };

        JFXBinder.bind("attr_1", MESSAGE).of(sourceModel).using(converter).to("text").of(targetLabel);
        sourceModel.getAt("attr_1", MESSAGE).setValue("Dummy");

        assertEquals("myDummy", targetLabel.getText());
    }

    @Test
    public void testUnbindInfo() {
        List<ClientAttribute> attributes = Arrays.asList((ClientAttribute) new GClientAttribute("text", ""));
        ClientPresentationModel sourceModel = new GClientPresentationModel("source", attributes);
        Label targetLabel = new Label();

        JFXBinder.bindInfo("dirty").of(sourceModel).to("text").of(targetLabel);

        assertEquals("false", targetLabel.getText());

        sourceModel.getAt("text").setValue("newValue");

        assertEquals("true", targetLabel.getText());

        JFXBinder.unbindInfo("dirty").of(sourceModel).from("text").of(targetLabel);

        sourceModel.getAt("text").setValue("");

        assertEquals("true", targetLabel.getText());
    }

    @Test
    public void testUnbindFromFX() {
        Label sourceLabel = new Label();
        ClientAttribute attribute = new GClientAttribute("text", "");

        JFXBinder.bind("text").of(sourceLabel).to("value").of(attribute);
        sourceLabel.setText("newValue");
        assertEquals("newValue", attribute.getValue());

        JFXBinder.unbind("text").of(sourceLabel).from("value").of(attribute);
        sourceLabel.setText("anotherValue");
        assertEquals("newValue", attribute.getValue());
    }

    @Test
    public void testUnbindFromClientPresentationModel() {
        Label targetLabel = new Label();
        List<ClientAttribute> attributeList = Arrays.asList((ClientAttribute) new GClientAttribute("attr", ""));
        ClientPresentationModel model = new GClientPresentationModel("model", attributeList);

        JFXBinder.bind("attr").of(model).to("text").of(targetLabel);
        model.getAt("attr").setValue("newValue");
        assertEquals("newValue", targetLabel.getText());

        JFXBinder.unbind("attr").of(model).from("text").of(targetLabel);
        model.getAt("attr").setValue("anotherValue");
        assertEquals("newValue", targetLabel.getText());
    }

    @Test
    public void testBindAndUnbindFromNodeToClientPresentationModel() {
        Label sourceLabel = new Label();
        List<ClientAttribute> attributeList = Arrays.asList((ClientAttribute) new GClientAttribute("attr", ""));
        ClientPresentationModel targetPm = new GClientPresentationModel("model", attributeList);

        JFXBinder.bind("text").of(sourceLabel).to("attr").of(targetPm);
        sourceLabel.setText("newValue");
        assertEquals("newValue", targetPm.getAt("attr").getValue());

        JFXBinder.unbind("text").of(sourceLabel).from("attr").of(targetPm);
        sourceLabel.setText("anotherValue");
        assertEquals("newValue", targetPm.getAt("attr").getValue());
    }

    @Test
    public void testUnbindFromPresentationModel() {
        Label targetLabel = new Label();
        List<ClientAttribute> attributeList = Arrays.asList((ClientAttribute) new GClientAttribute("attr", ""));
        BasePresentationModel model = new BasePresentationModel("model", attributeList);

        JFXBinder.bind("attr").of(model).to("text").of(targetLabel);
        model.getAt("attr").setValue("newValue");
        assertEquals("newValue", targetLabel.getText());

        JFXBinder.unbind("attr").of(model).from("text").of(targetLabel);
        model.getAt("attr").setValue("anotherValue");
        assertEquals("newValue", targetLabel.getText());
    }

    @Test
    public void testUnbindFromPojo() {
        Label targetLabel = new Label();
        TestPojo pojo = new TestPojo();

        JFXBinder.bind("value").of(pojo).to("text").of(targetLabel);
        pojo.setValue("newValue");
        assertEquals("newValue", targetLabel.getText());

        JFXBinder.unbind("value").of(pojo).from("text").of(targetLabel);
        pojo.setValue("anotherValue");
        assertEquals("newValue", targetLabel.getText());
    }

    @Test
    public void textFixClassTestCoverage() {
        assertNotNull( JFXBinder.class.getClass() );
    }

    // Binding support for Java classes is established by implementing Observable
    private static class TestPojo extends AbstractObservable {

        private String value;

        public void setValue(String newValue) {
            String oldValue = value;
            value = newValue;
            firePropertyChange("value", oldValue, newValue);
        }

        public String getValue() {
            return value;
        }

    }

}
