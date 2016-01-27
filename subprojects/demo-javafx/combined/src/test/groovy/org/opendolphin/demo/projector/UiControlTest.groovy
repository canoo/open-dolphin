package org.opendolphin.demo.projector

import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import spock.lang.Specification


class UiControlTest extends Specification {
    
    void "calling uiControlFor with model attribute widget hint type input field"() {
        given:
        UiControl.uiControlFor(model, propName)

        expect:
        UiControl.INPUTFIELD

        where:
        model                                                                                                 | propName
        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John")])                              | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "inputfield", null, Tag.WIDGET_HINT)]) | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "iNputField", null, Tag.WIDGET_HINT)]) | "NAME"
    }

    void "calling uiControlFor with model attribute widget hint type text area"() {
        given:
        UiControl.uiControlFor(model, propName)

        expect:
        UiControl.TEXTAREA

        where:
        model                                                                                               | propName
        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "textarea", null, Tag.WIDGET_HINT)]) | "NAME"

        new ClientPresentationModel("id", [new ClientAttribute("NAME", "John"),
                                           new ClientAttribute("NAME", "tEXTarea", null, Tag.WIDGET_HINT)]) | "NAME"
    }

    void "calling uiControlFor with null model throws AssertionError"() {
        given:
        ClientPresentationModel model = null
        String propName = ""

        when:
        UiControl.uiControlFor(model, propName)

        then:
        thrown AssertionError
    }

    void "calling uiControlFor with empty model throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [])
        String propName = ""

        when:
        UiControl.uiControlFor(model, propName)

        then:
        thrown AssertionError
    }

    void "calling uiControlFor with null propName throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [])
        String propName = null

        when:
        UiControl.uiControlFor(model, propName)

        then:
        thrown AssertionError
    }

    void "calling uiControlFor with propName that is not in model throws AssertionError"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [new ClientAttribute("NAME", "John")])
        String propName = ""

        when:
        UiControl.uiControlFor(model, propName)

        then:
        thrown AssertionError
    }

    void "calling uiControlFor with invalid widget hint value"() {
        given:
        ClientPresentationModel model = new ClientPresentationModel("id", [
                new ClientAttribute("NAME", "John"),
                new ClientAttribute("NAME", "invalidWidgetHintValue", null, Tag.WIDGET_HINT)
        ])
        String propName = "NAME"

        when:
        UiControl.uiControlFor(model, propName)

        then:
        thrown IllegalArgumentException
    }

}
