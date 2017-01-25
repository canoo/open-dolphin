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

package org.opendolphin.demo

import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientDolphin
import javafx.scene.control.Tooltip

import java.beans.PropertyChangeListener

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JFXBinder.bindInfo
import static org.opendolphin.core.Attribute.DIRTY_PROPERTY
import static org.opendolphin.core.Tag.LABEL
import static org.opendolphin.core.Tag.TOOLTIP
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.*
import static groovyx.javafx.GroovyFX.start

/**
 * This example illustrates the use of attribute tags.
 * <p/>
 * A presentation model contains any number of attributes.  Normally, when one thinks of an attribute,
 * the attribute's value is meant.  But metadata about an attribute can also be stored in a presentation model.
 * Such metadata can include whether or not the value is dirty, what the value's original (base) value was,
 * whether or not the field displaying the attribute is visible or enabled, and so forth.
 * <p/>
 * Such metadata is stored in the presentation model by use of attribute tags.  In this example, a person's first
 * and last name (VALUE tags) are stored, as well as the labels that describe the first and last name fields.
 * So the presentation model actually contains the following attributes:
 * <table align="left"><tr><th align="left">Property name</th><th align="left">Tag</th><th align="left">contains</th></tr>
 <tr><td>NAME (string "name")</td><td>VALUE</td><td>first name</td></tr>
 <tr><td>NAME (string "name")</td><td>LABEL</td><td>label value ("First name:" (English) or "Vorname:" (Deutsch))</td></tr>
 <tr><td>NAME (string "name")</td><td>TOOLTIP</td><td>tooltip text for name field</td></tr>
 <tr><td>NAME (string "name")</td><td>REGEX</td><td>tooltip text for name field</td></tr>
 <tr><td>LASTNAME (string "lastname")</td><td>VALUE</td><td>last name</td></tr>
 <tr><td>LASTNAME (string "lastname")</td><td>LABEL</td><td>label value ("Lastname:" or "Nachname:")</td></tr>
 </table>
 * Note that Dolphin itself does not take any special action based on the tag type, except to assume a tag type of VALUE
 * if none is otherwise specified.  The view code must supply the logic to enforce tag meanings.
 * <p/>
 * Hence, this example not only binds the values (VALUE tags) and dirty properties, but also binds the
 * labels to the LABEL attributes in the presentation model.
 * <p/>
 * In addition, the "first name" input field gets a tooltip via dolphin tag TOOLTIP and a validator
 * that uses the REGEX tag. When the first name contains an 'a', it is considered valid and displayed in black,
 * otherwise in red.
 * <p/>Pressing the "German" button sets the label tags to their German translation.  This illustrates a technique
 * for localization where the localized strings reside on the server and are able to be changed on the fly, i.e., without
 * needing to reconstruct the scene.
 * <p/>
 * The dirty state of the presentation model must be independent of the labels now being "dirty".
 * Hitting "Undo" reverts all changes including the label changes and dirty states.
 * <p>Note: there's a bug; when the line
 * <code>
    putStyle(sgb.nameInput, !matches, 'invalid')</code> is executed, the style isn't applied until another event occurs.
 * @see org.opendolphin.core.Attribute
 * @see org.opendolphin.core.Tag
 */
class AttributeTagView {
    static show(ClientDolphin dolphin) {
        start { app ->

            def model = dolphin.presentationModel 'person', (NAME):'', (LASTNAME):'Smith'

            stage {
                scene {
                    gridPane {

                        label       id: 'header',        row: 0, column: 1, 'Person Form'

                        label       id: 'nameLabel',     row: 1, column: 0, ' ' * 20
                        textField   id: 'nameInput',     row: 1, column: 1

                        label       id: 'lastnameLabel', row: 2, column: 0, ' ' * 20
                        textField   id: 'lastnameInput', row: 2, column: 1

                        hbox row: 3, column: 1, spacing:5, {
                            button id: 'german', 'German', onAction: { dolphin.send 'german' }
                            button id: 'reset',  'Undo',   onAction: { model.reset() }
                        }
                    }
                }
            }
            def sgb = delegate
            style sgb

            // binding the values
            bind NAME     of model         to FX.TEXT  of nameInput
            bind LASTNAME of model         to FX.TEXT  of lastnameInput

            dolphin.send 'init', { pms ->        // only do binding after server has initialized the tags
                bind FX.TEXT  of nameInput     to NAME     of model, { newVal ->
                    boolean matches = newVal ==~ model.getAt(NAME, Tag.REGEX).value
                    putStyle(sgb.nameInput, !matches, 'invalid')
                    return newVal
                }
                bind FX.TEXT  of lastnameInput to LASTNAME of model

                bind NAME,     LABEL   of model to FX.TEXT    of nameLabel
                bind NAME,     TOOLTIP of model to FX.TOOLTIP of nameInput, { new Tooltip(it as String) }

                bind LASTNAME, LABEL   of model to FX.TEXT    of lastnameLabel
            }

            // binding meta properties
            model[NAME].addPropertyChangeListener        DIRTY_PROPERTY, {
                putStyle sgb.nameLabel,     it.newValue, DIRTY_PROPERTY
            }
            model[LASTNAME].addPropertyChangeListener    DIRTY_PROPERTY, {
                putStyle sgb.lastnameLabel, it.newValue, DIRTY_PROPERTY
            }
            bindInfo DIRTY_PROPERTY of model using { it ? '** DIRTY **': '' } to FX.TITLE   of primaryStage
            bindInfo DIRTY_PROPERTY of model using { !it }                    to FX.DISABLE of reset

            primaryStage.show()
        }
    }

    static void putStyle(node, boolean addOrRemove, String styleClassName) {
        if (addOrRemove) {
            node.styleClass.add(styleClassName)
        } else {
            node.styleClass.remove(styleClassName)
        }
    }

}
