package org.opendolphin.demo.projector

import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import spock.lang.Specification

class UiControlTypeTest extends Specification {

    void "calling getValueOf with model attribute widget hint type input field"() {
        given:
        UiControlType.getValueOf(model, propName)

        expect:
        UiControlType.INPUTFIELD

        where:
        model                                                                                                 | propName
        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John")])                              | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "inputfield", null, Tag.WIDGET_HINT)]) | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "iNputField", null, Tag.WIDGET_HINT)]) | "NAME"
    }

    void "calling getValueOf with model attribute widget hint type text area"() {
        given:
        UiControlType.getValueOf(model, propName)

        expect:
        UiControlType.TEXTAREA

        where:
        model                                                                                               | propName
        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "textarea", null, Tag.WIDGET_HINT)]) | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "tEXTarea", null, Tag.WIDGET_HINT)]) | "NAME"
    }

    void "calling getValueOf with null model throws AssertionError"() {
        given:
        ClientPresentationModel model = null
        String propName = ""

        when:
        UiControlType.getValueOf(model, propName)

        then:
        thrown AssertionError
    }

    void "calling getValueOf with empty model throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [])
        String propName = ""

        when:
        UiControlType.getValueOf(model, propName)

        then:
        thrown AssertionError
    }

    void "calling getValueOf with null propName throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [])
        String propName = null

        when:
        UiControlType.getValueOf(model, propName)

        then:
        thrown AssertionError
    }

    void "calling getValueOf with propName that is not in model throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [new ClientAttribute("NAME", "John")])
        String propName = ""

        when:
        UiControlType.getValueOf(model, propName)

        then:
        thrown AssertionError
    }

    void "calling getValueOf with invalid widget hint value"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [
                new ClientAttribute("NAME", "John"),
                new ClientAttribute("NAME", "invalidWidgetHintValue", null, Tag.WIDGET_HINT)
        ])
        String propName = "NAME"

        when:
        UiControlType.getValueOf(model, propName)

        then:
        thrown IllegalArgumentException
    }

}
