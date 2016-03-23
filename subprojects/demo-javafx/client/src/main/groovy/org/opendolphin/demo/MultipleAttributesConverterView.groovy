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
package org.opendolphin.demo

import org.opendolphin.binding.Converter
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.DemoStyle.style
import static org.opendolphin.demo.MyProps.ATT.getPURPOSE
import static org.opendolphin.demo.MyProps.ATT.getTITLE

class MultipleAttributesConverterView {

    private static String defaultTitle = 'Title will be displayed in upper case'
    private static String defaultPurpose = 'Input is limited to 40 characters'

    static show(ClientDolphin clientDolphin) {

        start { app ->
            // construct the PM
            def titleAttr = new ClientAttribute(TITLE)
            def purposeAttr = new ClientAttribute(PURPOSE)
            def pm = new ClientPresentationModel('demo',[titleAttr, purposeAttr])
            clientDolphin.clientModelStore.add pm

            stage {
                scene {
                    gridPane  {

                        label id: 'header', row: 0, column: 1, 'Using converters with presentation models'

                        label       'Title: ',        row: 1, column: 0
                        label       id: 'titleLabel', row: 1, column: 1, defaultTitle
                        textField   id: 'titleInput', row: 2, column: 1

                        label       'Purpose: ',        row: 3, column: 0
                        label       id: 'purposeLabel', row: 3, column: 1, defaultPurpose
                        label       id: 'remainingLabel', row: 4, column: 0
                        textField   id: 'purposeInput', row: 4, column: 1

            }   }   }

            style delegate

            // java style binding
            Converter titleConverter = new Converter<String, String>() {
                @Override
                String convert(String value) {
                    return value.toUpperCase()
                }
            }

            // converter as interface implementation
            bind(FX.TEXT).of(titleInput).to(TITLE).of(pm)                           // ui -> pm
            bind(TITLE).of(pm).to(FX.TEXT).of(titleInput)                           // pm -> ui
            bind(TITLE).of(pm).using(titleConverter).to(FX.TEXT).of(titleLabel)     // pm -> ui (converter)

            // groovy style binding
            Closure purposeConverter = {it.toUpperCase()}                           // converter as closure
            bind FX.TEXT of purposeInput to PURPOSE of pm                           // ui -> pm
            bind PURPOSE of pm to FX.TEXT of purposeInput                           // pm -> ui
            bind PURPOSE of pm using purposeConverter to FX.TEXT of purposeLabel    // pm -> ui (converter)

            // converter used as validator
            Closure validator = {
                def remaining = 40 - it.length()
                if (remaining > 0) {
                    return remaining
                } else {
                    purposeInput.text = it.substring(0, 40)
                    return 0
                }
            }
            bind PURPOSE of pm using validator to FX.TEXT of remainingLabel    // pm -> ui (converter)

            titleAttr.value   = defaultTitle
            purposeAttr.value = defaultPurpose

            primaryStage.show()
        }

    }

}
