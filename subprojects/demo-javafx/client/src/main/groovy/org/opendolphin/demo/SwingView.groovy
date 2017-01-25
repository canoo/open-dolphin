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

import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import groovy.beans.Bindable
import groovy.swing.SwingBuilder

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.MyProps.ATT.*

class SwingView {

    @Bindable String boundTitle // a stand-in since swing doesn't support listening for the 'text' prop to change...

    void show(ClientDolphin clientDolphin) {

        def pm = clientDolphin.presentationModel('demo', (TITLE):'')

        SwingBuilder builder = new SwingBuilder()
        builder.build {
            frame id:'primaryStage', {
                vbox {
                    label id: 'header'
                    label id: 'label'
                    textField id: 'input' ,
                           actionPerformed: { boundTitle = input.text }
                    button id: 'submit', "Update labels and title",
                           actionPerformed: { pm[TITLE].value = input.text }
                }
            }
        }
        bindPmToViews pm, builder
        pm[TITLE].value = "Some Text: <enter> or <submit>"
        builder.primaryStage.pack()
        builder.primaryStage.visible = true
    }

    void bindPmToViews(ClientPresentationModel pm, builder) {
        builder.with {
            bind TITLE  of pm  to FX.TITLE of primaryStage   // groovy style

            bind(TITLE).of(pm).to(FX.TEXT).of(label)       // java fluent-interface style

            bind TITLE        of pm    to FX.TEXT  of input
            bind TITLE        of pm    to FX.TEXT  of header // a second view onto the same info

            bind 'boundTitle' of this  to TITLE        of pm   // bidirectional bind between pm and observable to ...
                        bind TITLE        of pm    to 'boundTitle' of this // ... work around the swing limitation.
        }
  }
}