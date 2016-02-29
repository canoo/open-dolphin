package org.opendolphin.demo.crud

import javafx.beans.value.ChangeListener
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.demo.FX
import groovyx.javafx.SceneGraphBuilder
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart
import javafx.scene.control.TableView

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JavaFxUtil.cellEdit
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.*
import static org.opendolphin.demo.crud.PositionConstants.ATT.*
import static org.opendolphin.demo.crud.PositionConstants.CMD.PULL
import static javafx.scene.layout.GridPane.REMAINING

@SuppressWarnings("GroovyAssignabilityCheck")
class PortfolioEditor {

    final ClientPresentationModel portfolioPM
    final Portfolio portfolio

    private javafx.scene.Node view
    private ClientDolphin clientDolphin
    private ObservableList<ClientPresentationModel> observableListOfPositions  = FXCollections.observableArrayList()
    private plus, minus, nameField, tableBox, positions, totalField, fixedField, chart

    private ClientPresentationModel selectedPositionPm = null

    PortfolioEditor(ClientPresentationModel portfolioPM, ClientDolphin clientDolphin) {
        this.portfolioPM = portfolioPM
        this.portfolio = new Portfolio(portfolioPM)
        this.clientDolphin = clientDolphin
    }

    javafx.scene.Node initView(SceneGraphBuilder sgb) {
        if (! view) {
            view = createView sgb
            bindings          sgb
            attachListeners   sgb
            pull              sgb
        }
        return view
    }

    private javafx.scene.Node createView(SceneGraphBuilder sgb) {
        sgb.with {
            javafx.scene.Node result = gridPane hgap:10, vgap:12, padding: 20, {
                columnConstraints     minWidth: 80, halignment: "right"
                label       "Portfolio",    row: 0, column: 0
                nameField = textField       row: 0, column: 1, minHeight:32
                label       "Positions",    row: 1, column: 0
                tableBox  = vbox            row: 1, column: 1, {
                    positions = tableView   selectionMode:"single", editable:true, styleClass:'noBorder', id:'table', {
                        value INSTRUMENT, tableColumn('Instrument', editable:true,
                                                        prefWidth: sgb.bind(table.width() * 2 / 3),
                                                        onEditCommit: cellEdit(INSTRUMENT, { it.toString() } ) )
                        value WEIGHT    , tableColumn('Weight',     editable:true,
                                                        prefWidth: sgb.bind(table.width() / 3 - 1),
                                                        onEditCommit: cellEdit(WEIGHT,     { it.toInteger() } ) )
                    }
                    hbox {
                        plus  = button '+', styleClass:"bottomButton"
                        minus = button '-', styleClass:"bottomButton"
                    }
                }
                label       'Total',        row: 2, column: 0
                totalField = text           row: 2, column: 1
                label       'Fixed',        row: 3, column: 0
                fixedField = checkBox       row: 3, column: 1
                chart      = pieChart       row: 0, column: 2, rowSpan:REMAINING, animated: true
            }
            positions.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            positions.items = observableListOfPositions
            result.opacity  = 0.3d
            return result
        }
    }

    private void bindings(SceneGraphBuilder sgb) {
        sgb.with {
            bind FIXED       of portfolioPM to FX.SELECTED  of fixedField
            bind FX.SELECTED of fixedField  to FIXED        of portfolioPM

            bind NAME        of portfolioPM to FX.TEXT      of nameField
            bind FX.TEXT     of nameField   to NAME         of portfolioPM

            bind TOTAL       of portfolioPM to FX.TEXT      of totalField

            [nameField, positions, plus, minus].each { control ->
                bind FIXED   of portfolioPM to FX.DISABLE  of control
            }
        }
    }

    private void attachListeners(SceneGraphBuilder sgb) {
        sgb.with {
            def chart = chart // do not delete! Needed for local reference lookup.
            def observableListOfPositions = observableListOfPositions
            TableView<ClientPresentationModel> positions = positions

            positions.selectionModel.selectedItemProperty().addListener( { val, oldModel, newModel ->
                selectedPositionPm = newModel
            } as ChangeListener )

            // bind available positions to table
            clientDolphin.addModelStoreListener Position.TYPE, { ModelStoreEvent event ->
                PresentationModel positionPm = event.presentationModel
                Position position = new Position(positionPm)
                if (position.getPortfolioDomainId() != portfolio.getDomainId()) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << positionPm
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        observableListOfPositions.remove positionPm
                        break
                }
            }

            // bind available positions to chart
            clientDolphin.addModelStoreListener Position.TYPE, { ModelStoreEvent event ->
                PresentationModel positionPm = event.presentationModel
                Position position = new Position(positionPm)
                if (position.getPortfolioDomainId() != portfolio.getDomainId()) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        def pieDataPoint = new PieChart.Data("",0)
                        bind INSTRUMENT of positionPm to FX.NAME      of pieDataPoint
                        bind WEIGHT     of positionPm to FX.PIE_VALUE of pieDataPoint, { it.toDouble() }

                        position.instrument().addPropertyChangeListener FX.VALUE, { // Workaround for http://javafx-jira.kenai.com/browse/RT-26845
                            def index = chart.data.indexOf pieDataPoint
                            def newDataPoint = new PieChart.Data(it.newValue, pieDataPoint.pieValue)
                            bind INSTRUMENT of positionPm to FX.NAME      of newDataPoint
                            bind WEIGHT     of positionPm to FX.PIE_VALUE of newDataPoint, { it.toDouble() }
                            chart.data[index] = newDataPoint       // consider unbinding pieDataPoint
                        }
                        chart.data.add pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        def entry = chart.data.find { dataPoint ->
                           dataPoint[FX.NAME]      == position.getInstrument() &&
                           dataPoint[FX.PIE_VALUE] == position.getWeight()
                        }
                        chart.data.remove entry
                        break
                }
            }

            plus.onAction {
                clientDolphin.presentationModel(null, Position.TYPE,
                    (INSTRUMENT):         'changeme',
                    (WEIGHT):              10,
                    (PORTFOLIO_DOMAIN_ID): portfolio.getDomainId()
                )
            }

            minus.onAction {
                if (! selectedPositionPm) return
                clientDolphin.delete(selectedPositionPm)
            }
        }
    }

    private void pull(SceneGraphBuilder sgb) {
        PortfolioSelection.select(clientDolphin, portfolio)
        clientDolphin.send PULL, {
            sgb.fadeTransition(1.s, node: view, to: 1).playFromStart()
        }
    }


}
