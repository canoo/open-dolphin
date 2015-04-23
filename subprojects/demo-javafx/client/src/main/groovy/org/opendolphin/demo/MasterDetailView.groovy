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

package org.opendolphin.demo

import groovyx.javafx.SceneGraphBuilder
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientPresentationModel

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.core.Tag.ENABLED
import static org.opendolphin.core.Tag.LABEL
import static org.opendolphin.demo.DemoStyle.blueStyle
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.MasterDetailConstants.*

/**
 * A demo that shows how to easily create a master-detail view with the standard Dolphin on-board means.
 * Qualifier set (Consistent immediate updates)
 * Usage of Tags
 * Bidirectional JFX binding (not bidirectional binding of views!)
 */

class MasterDetailView {

    static show(ClientDolphin dolphin) {

        GClientPresentationModel dataMold = dolphin.presentationModel('dataMold',
                [
                    ATT_NAME,
                    ATT_RANK,
                    ATT_YEAROFBIRTH,
                    ATT_COUNTRY,
                    ATT_MATCHESFIFA,
                    ATT_MATCHESRSSSF
                ]
        )
        // defaults
        dolphin.tag(dataMold, ATT_NAME,         ENABLED, false)
        dolphin.tag(dataMold, ATT_NAME,         LABEL, "Name")
        dolphin.tag(dataMold, ATT_RANK,         ENABLED, false)
        dolphin.tag(dataMold, ATT_RANK,         LABEL, "Rank")
        dolphin.tag(dataMold, ATT_YEAROFBIRTH,  ENABLED, false)
        dolphin.tag(dataMold, ATT_YEAROFBIRTH,  LABEL, "Year of birth")
        dolphin.tag(dataMold, ATT_COUNTRY,      ENABLED, false)
        dolphin.tag(dataMold, ATT_COUNTRY,      LABEL, "Country")
        dolphin.tag(dataMold, ATT_MATCHESFIFA,  ENABLED, false)
        dolphin.tag(dataMold, ATT_MATCHESFIFA,  LABEL, "FIFA matches")
        dolphin.tag(dataMold, ATT_MATCHESRSSSF, ENABLED, false)
        dolphin.tag(dataMold, ATT_MATCHESRSSSF, LABEL, "RSSSF matches")

        ObservableList<Integer> observableList = FXCollections.observableArrayList()

        start { app ->
            SceneGraphBuilder sgb = delegate

            stage title:"Dolphin master-detail demo", {
                scene width: 750, height: 500, {
                    borderPane {
                        center margin:10, {
                            int row = 0
                            gridPane styleClass: 'form', {
                                columnConstraints  halignment: "right"
                                columnConstraints  halignment: "left"

                                label     row: row  , column: 0, id: 'rankLabel'
                                textField row: row++, column: 1, id: 'rankField'

                                label     row: row  , column: 0, id: 'nameLabel'
                                textField row: row++, column: 1, id: 'nameField'

                                label     row: row  , column: 0, id: 'yearOfBirthLabel'
                                textField row: row++, column: 1, id: 'yearOfBirthField'

                                label     row: row  , column: 0, id: 'countryLabel'
                                textField row: row++, column: 1, id: 'countryField'

                                label     row: row  , column: 0, id: 'matchesFIFALabel'
                                textField row: row++, column: 1, id: 'matchesFIFAField'

                                label     row: row  , column: 0, id: 'matchesRSSSFLabel'
                                textField row: row++, column: 1, id: 'matchesRSSSFField'
                            }
                        }
                        left margin:10, {
                            tableView(id: 'table') {
                                value ATT_RANK, tableColumn(id:'rankCol', property:'rank', prefWidth: 45 )
                                value ATT_NAME, tableColumn(id:'nameCol', property:'name', prefWidth: 270 )
                            }
                        }
            }   }   }
            blueStyle sgb

            [rankField, nameField, yearOfBirthField, countryField, matchesFIFAField, matchesRSSSFField].each { it.prefColumnCount = 15 }

            // bind preferred table width to contained column widths
            table.prefWidthProperty().bind(
                    rankCol.prefWidthProperty()
                            .add(nameCol.prefWidthProperty())
                            .add(new SimpleIntegerProperty(16))
            )

            def inverter = { boolean enableStatus -> !enableStatus }
            // all the bindings ...
            bind ATT_NAME           of dataMold to FX.TEXT of nameField
            bind ATT_NAME, ENABLED  of dataMold to FX.DISABLE of nameField, inverter
            bind ATT_NAME, LABEL    of dataMold to FX.TEXT of nameLabel
            bind ATT_NAME, LABEL    of dataMold to FX.TEXT of nameCol
            bind FX.TEXT            of nameField to ATT_NAME of dataMold

            bind ATT_RANK           of dataMold to FX.TEXT of rankField
            bind ATT_RANK, ENABLED  of dataMold to FX.DISABLE of rankField, inverter
            bind ATT_RANK, LABEL    of dataMold to FX.TEXT of rankLabel
            bind ATT_RANK, LABEL    of dataMold to FX.TEXT of rankCol

            bind ATT_COUNTRY            of dataMold to FX.TEXT of countryField
            bind ATT_COUNTRY, ENABLED   of dataMold to FX.DISABLE of countryField, inverter
            bind ATT_COUNTRY, LABEL     of dataMold to FX.TEXT of countryLabel

            bind ATT_YEAROFBIRTH            of dataMold to FX.TEXT of yearOfBirthField
            bind ATT_YEAROFBIRTH, ENABLED   of dataMold to FX.DISABLE of yearOfBirthField, inverter
            bind ATT_YEAROFBIRTH, LABEL     of dataMold to FX.TEXT of yearOfBirthLabel

            bind ATT_MATCHESFIFA            of dataMold to FX.TEXT of matchesFIFAField
            bind ATT_MATCHESFIFA, ENABLED   of dataMold to FX.DISABLE of matchesFIFAField, inverter
            bind ATT_MATCHESFIFA, LABEL     of dataMold to FX.TEXT of matchesFIFALabel

            bind ATT_MATCHESRSSSF           of dataMold to FX.TEXT of matchesRSSSFField
            bind ATT_MATCHESRSSSF, ENABLED  of dataMold to FX.DISABLE of matchesRSSSFField, inverter
            bind ATT_MATCHESRSSSF, LABEL    of dataMold to FX.TEXT of matchesRSSSFLabel

            table.items = observableList

            // when a table row is selected, we fill the mold and the detail view gets updated
            table.selectionModel.selectedItemProperty().addListener( { selectionModel, oldVal, selectedPm ->
                if (selectedPm == null) return
                dolphin.apply selectedPm to dataMold
            } as ChangeListener )


            dolphin.send CMD_PULL, { pms ->
                for (pm in pms) {
                    observableList << pm
                }
            }

            primaryStage.show()
        }
    }
}
