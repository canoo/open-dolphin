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

package org.opendolphin.demo.crud

import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.text.Font
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientPresentationModel
import org.opendolphin.demo.FX

import static groovyx.javafx.GroovyFX.start
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.NAME
import static org.opendolphin.demo.crud.PortfolioConstants.CMD.PULL
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED

class CrudView {

    @SuppressWarnings("GroovyAssignabilityCheck")
    static show(ClientDolphin clientDolphin) {

        Font.loadFont(CrudView.getResourceAsStream('/Eurostile-Demi.ttf'), 72)

        ObservableList<GClientPresentationModel> observableListOfPortfolios = FXCollections.observableArrayList()

        // keeping track of the currently visible portfolio (the currently open tab) by
        // having a pm that captures the (dolphin) portfolio id
        // This is used on the server to find out the portfolio context for a named command.
        // A "mold" is not used in this case since "apply" produces too much overhead.
        def visiblePortfolio = clientDolphin.presentationModel(SELECTED, portfolioId: null)

        start { app ->
            def sgb = delegate
            stage title: "Portfolio Manager", {
                scene width: 1000, height: 600, stylesheets: "CrudDemo.css", {
                    splitPane {
                        dividerPosition(index: 0, position: 0.2)
                        vbox alignment: 'top_center', {
                            label "Dierk's Portfolios", id: 'dierk'
                            tableView id: 'portfolios', selectionMode: "single", vgrow: 'always', {
                                value 'name', tableColumn('Portfolio', prefWidth: sgb.bind(portfolios.width() / 2))
                                value 'total', tableColumn('Total', prefWidth: sgb.bind(portfolios.width() / 4))
                                value 'fixed', tableColumn('Fixed', prefWidth: sgb.bind(portfolios.width() / 4))
                            }
                        }
                        stackPane {
                            text "Please select a Portfolio", id: 'welcome'
                            tabPane id: 'portfolioTabs'
                        }
                    }
                }
            }

            portfolios.items = observableListOfPortfolios

            portfolios.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                if (null == selectedPm) return // happens on deselect
                visiblePortfolio.portfolioId.value = selectedPm.id

                def gotoTab = sgb.portfolioTabs.tabs.find { it.id == selectedPm.id }
                if (!gotoTab) {
                    def editor = new PortfolioEditor(selectedPm, clientDolphin)
                    gotoTab = sgb.tab id: selectedPm.id, {
                        editor.initView(sgb)
                    }
                    bind NAME of selectedPm to FX.TEXT of gotoTab
                    sgb.portfolioTabs.tabs << gotoTab
                }
                sgb.portfolioTabs.selectionModel.select(gotoTab)
            } as ChangeListener)

            clientDolphin.send PULL, { portfolioPms ->
                for (pm in portfolioPms) {
                    observableListOfPortfolios << pm
                }
                fadeTransition(1.s, node: portfolios, to: 1).playFromStart()
            }

            primaryStage.show()
        }
    }
}
