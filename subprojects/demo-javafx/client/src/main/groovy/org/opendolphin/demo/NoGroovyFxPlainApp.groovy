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

import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage

import static org.opendolphin.binding.Binder.bind
import static org.opendolphin.demo.MyProps.ATT.*
import static org.opendolphin.demo.MyProps.TITLE

public class NoGroovyFxPlainApp extends Application {

    @Override public void start(Stage stage) {

        // construct the view
        stage.title = ""

        Group root = new Group()
        Scene scene = new Scene(root)
        stage.scene = scene

        def vbox = new VBox()
        def label = new Label("")
        def textField = new TextField()
        vbox.children << label
        vbox.children << textField
        root.children << vbox
        
        // construct the PM
        def titleAttr = new ClientAttribute(TITLE)
        def pm = new ClientPresentationModel('demo',[titleAttr])
        pm[TITLE].value = "Hello JavaFX"

        stage.titleProperty().bind(label.textProperty()) // JavaFX: changes to label will be propagated to the stage title

        // bind the view onto the PM
        bind TITLE  of pm  to FX.TEXT  of label      // groovy style
        bind(TITLE).of(pm).to(FX.TEXT).of(textField) // java style

        textField.onAction = { titleAttr.value = textField.text } as EventHandler

        // let the show begin
        stage.show()
    }
    


    public static void main(String[] args) {
        Application.launch(NoGroovyFxPlainApp, args)
    }
}


