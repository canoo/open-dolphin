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
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.DOMAIN_ID
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.FIXED
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.NAME
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.TOTAL
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static org.opendolphin.demo.crud.PositionConstants.ATT.INSTRUMENT
import static org.opendolphin.demo.crud.PositionConstants.ATT.PORTFOLIO_DOMAIN_ID
import static org.opendolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static org.opendolphin.demo.crud.PositionConstants.CMD.PULL
import static org.opendolphin.demo.crud.PositionConstants.TYPE.POSITION
import static javafx.scene.layout.GridPane.REMAINING

@SuppressWarnings("GroovyAssignabilityCheck")
class PortfolioEditor {

    ClientPresentationModel portfolioPM

    private javafx.scene.Node view
    private ClientDolphin clientDolphin
    private ObservableList<ClientPresentationModel> observableListOfPositions  = FXCollections.observableArrayList()
    private plus, minus, nameField, tableBox, positions, totalField, fixedField, chart

    private ClientPresentationModel selectedPosition = null

    PortfolioEditor(ClientPresentationModel portfolioPM, ClientDolphin clientDolphin) {
        this.portfolioPM = portfolioPM
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
                selectedPosition = newModel
            } as ChangeListener )

            // bind available positions to table
            clientDolphin.addModelStoreListener POSITION, { ModelStoreEvent event ->
                PresentationModel position = event.presentationModel
                if (position[PORTFOLIO_DOMAIN_ID].value != portfolioPM[DOMAIN_ID].value) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << position
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        observableListOfPositions.remove position
                        break
                }
            }

            // bind available positions to chart
            clientDolphin.addModelStoreListener POSITION, { ModelStoreEvent event ->
                PresentationModel position = event.presentationModel
                if (position[PORTFOLIO_DOMAIN_ID].value != portfolioPM[DOMAIN_ID].value) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        def pieDataPoint = new PieChart.Data("",0)
                        bind INSTRUMENT of position to FX.NAME      of pieDataPoint
                        bind WEIGHT     of position to FX.PIE_VALUE of pieDataPoint, { it.toDouble() }

                        position[INSTRUMENT].addPropertyChangeListener FX.VALUE, { // Workaround for http://javafx-jira.kenai.com/browse/RT-26845
                            def index = chart.data.indexOf pieDataPoint
                            def newDataPoint = new PieChart.Data(it.newValue, pieDataPoint.pieValue)
                            bind INSTRUMENT of position to FX.NAME      of newDataPoint
                            bind WEIGHT     of position to FX.PIE_VALUE of newDataPoint, { it.toDouble() }
                            chart.data[index] = newDataPoint       // consider unbinding pieDataPoint
                        }
                        chart.data.add pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        def entry = chart.data.find { dataPoint ->
                           dataPoint[FX.NAME]      == position[INSTRUMENT].value && // todo dk: check &&
                           dataPoint[FX.PIE_VALUE] == position[WEIGHT].value
                        }
                        chart.data.remove entry
                        break
                }
            }

            plus.onAction {
                clientDolphin.presentationModel(null, POSITION,
                    instrument:'changeme',
                    weight:10,
                    portfolioId:portfolioPM[DOMAIN_ID].value
                )
            }

            minus.onAction {
                if (! selectedPosition) return
                clientDolphin.delete(selectedPosition)
            }
        }
    }

    private void pull(SceneGraphBuilder sgb) {
        setCurrentPortfolio()
        clientDolphin.send PULL, {
            sgb.fadeTransition(1.s, node: view, to: 1).playFromStart()
        }
    }

    def void setCurrentPortfolio() {
        def visiblePortfolio = clientDolphin.findPresentationModelById(SELECTED)
        visiblePortfolio[PORTFOLIO_DOMAIN_ID].value = portfolioPM.id
    }

}
