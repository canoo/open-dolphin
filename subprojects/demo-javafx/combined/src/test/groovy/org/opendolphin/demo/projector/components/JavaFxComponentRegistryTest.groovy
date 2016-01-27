package org.opendolphin.demo.projector.components

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.demo.projector.UiControl
import spock.lang.Specification

class JavaFxComponentRegistryTest extends Specification {

    void "calling create with null componentId"() {
        given:
        def componentId = null;
        def properties = []

        when:
        JavaFxComponentRegistry.instance.create(componentId, properties)

        then:
        thrown IllegalArgumentException
    }

    void "calling create with non existing componentId"() {
        given:
        def componentId = "";
        def properties = []

        when:
        JavaFxComponentRegistry.instance.create(componentId, properties)

        then:
        thrown IllegalArgumentException
    }

    void "calling create input field"() {
        given:
        def componentId = UiControl.INPUTFIELD;
        ClientPresentationModel model = new ClientPresentationModel("id", [
                new ClientAttribute("NAME", "John"),
                new ClientAttribute("NAME", "myToolTip",null,Tag.TOOLTIP)
        ])
        String propName = "NAME"

        when:
        TextField textField = JavaFxComponentRegistry.instance.create(componentId, [model: model, propName: propName])

        then:
        assert textField
        assert textField.text == "John"
    }

    void "calling create text area"() {
        given:
        def componentId = UiControl.TEXTAREA;
        ClientPresentationModel model = new ClientPresentationModel("id", [
                new ClientAttribute("NAME", "John")
        ])
        String propName = "NAME"

        when:
        def object = JavaFxComponentRegistry.instance.create(componentId, [model: model, propName: propName])

        then:
        assert object
        assert object.getClass() == TextArea
    }

    void "register new component factory and create component"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [
                new ClientAttribute("NAME", "John"),
                new ClientAttribute("NAME", "myTextArea", null, Tag.WIDGET_HINT)
        ])
        String propName = "NAME"

        def myFactory = Mock(IJavaFxComponentFactory)
        def uiControl = UiControl.uiControlFor(model, propName)

        when:
        JavaFxComponentRegistry.instance.register(uiControl, myFactory);

        then:
        assert JavaFxComponentRegistry.instance.componentRegistry.get(uiControl)

        when:
        JavaFxComponentRegistry.instance.create(uiControl, [model:model, propName: propName])

        then:
        1 * myFactory.createComponent(_)
    }

    void "calling register with null uiControl throws AssertionError"() {
        when:
        JavaFxComponentRegistry.instance.register(null, Mock(IJavaFxComponentFactory))

        then:
        thrown AssertionError
    }
}
