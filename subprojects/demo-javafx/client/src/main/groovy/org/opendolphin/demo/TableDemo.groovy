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

import javafx.application.Application
import javafx.beans.property.Property
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.util.Callback
import javafx.scene.control.*

/**
 * How it works:
 * The TableView hast to work on an ObservableList.
 *
 * The ObservableList consists of a List of keys, not the real entities like a Person.
 *
 * Special CellValueFactories are using the provided key to make a lookup in a 'PersonPool'. 
 * This is done on-demand, depending on the displayed rows.
 *
 * Maybe we can use this for ULCFx. For example in a 'Search use case': A SearchCommand is transferred to the server. 
 * The server responds with a list of identifiers.
 * For the displayed elements the client asks for the PMs. Only the PMs needed are transferred to the client.
 *
 */
public class TableDemo extends Application {

    public static class Person {
        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty email;

        private Person(String firstName, String lastName, String email) {
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public Property<String> getFirstNameProperty() {
            return firstName;
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public Property<String> getLastNameProperty() {
            return lastName;
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String fName) {
            email.set(fName);
        }

        public Property<String> getEmailProperty() {
            return email;
        }

    }

    private final Map<Integer, Person> personPool = new HashMap<Integer, Person>();
    private final ObservableList<Integer> keyList = FXCollections.observableArrayList();


    public static void main(String[] args) {
        launch(TableDemo, args);
    }

    private Person getPerson(Integer index) {
        Person p = personPool.get(index);
        if (p == null) {
            p = new Person("Jacob_" + index, "Smith_" + index, "jacob.smith@example.com");
            personPool.put(index, p);
        }
        return p;
    }

    @Override
    public void start(Stage stage) {
        for (int i = 0; i < 10000; i=i+2) {
            keyList.add(i);
        }

        Scene scene = new Scene(new Group());
        stage.setTitle("Table View Sample");
        stage.setWidth(400);
        stage.setHeight(600);

        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        TableColumn<Integer, String> firstNameCol = new TableColumn<Integer, String>("First Name");
        firstNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Integer, String> key) {
                // here's the lazy-loading
                return getPerson(key.getValue()).getFirstNameProperty();
            }
        });

        TableColumn<Integer, String> lastNameCol = new TableColumn<Integer, String>("Last name");
        lastNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Integer, String> key) {
                return getPerson(key.getValue()).getLastNameProperty();
            }
        });

        TableColumn<Integer, String> emailCol = new TableColumn<Integer, String>("Email address");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Integer, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Integer, String> key) {
                return getPerson(key.getValue()).getEmailProperty();
            }
        });

        final TableView<Integer> table = new TableView<Integer>();
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        table.setItems(keyList);

        final TextField addFirstName = new TextField();
        addFirstName.setPromptText("Last Name");
        addFirstName.setMaxWidth(firstNameCol.getPrefWidth());
        final TextField addLastName = new TextField();
        addLastName.setMaxWidth(lastNameCol.getPrefWidth());
        addLastName.setPromptText("Last Name");
        final TextField addEmail = new TextField();
        addEmail.setMaxWidth(emailCol.getPrefWidth());
        addEmail.setPromptText("Email");

        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Integer newKey = keyList.size() * 2 + 2;
                personPool.put(newKey, new Person(addFirstName.getText() + newKey, addLastName.getText() + newKey, addEmail.getText()));
                keyList.add(newKey);
                addFirstName.setText("");
                addLastName.setText("");
                addEmail.setText("");
            }
        });

        final HBox hb = new HBox();
        hb.getChildren().addAll(addFirstName, addLastName, addEmail, addButton);
        hb.setSpacing(3);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table, hb);
        vbox.setPadding(new Insets(10, 0, 0, 10));

        vbox.setMaxHeight(Double.MAX_VALUE);
        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

}
