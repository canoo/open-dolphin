package org.opendolphin.core.client

import javax.swing.AbstractAction
import java.awt.event.ActionEvent

import static org.opendolphin.binding.Binder.bindInfo
import spock.lang.Specification

class DirtyBindingSpec extends Specification{

    void "binding the dirty state of a presentation model to a swing action"() {
        given:
        def pm = new GClientPresentationModel([])
        def action = new AbstractAction() {
            void actionPerformed(ActionEvent e) {}
        }
        when:
        bindInfo("dirty").of(pm).to("enabled").of(action)
        then:
        action.enabled == false
    }

    void "binding the dirty state of a presentation model to an attribute"() {
        given:
        def sourcePm = new GClientPresentationModel([])
        def targetPm = new GClientPresentationModel([new GClientAttribute("dirt",true)])
        when:
        bindInfo("dirty").of(sourcePm).to("dirt").of(targetPm)
        then:
        targetPm.dirt.value == false
    }
}
