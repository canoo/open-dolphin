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
package org.opendolphin.demo.crud


import org.opendolphin.LogConfig
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.TestInMemoryConfig
import spock.lang.Specification

import java.util.concurrent.TimeUnit

import static org.opendolphin.demo.crud.PortfolioConstants.*
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static org.opendolphin.demo.crud.PositionConstants.*


class CrudTests extends Specification {

    volatile TestInMemoryConfig app
    ClientDolphin clientDolphin

    protected TestInMemoryConfig initApp() {
        def result = new TestInMemoryConfig()
        result.serverDolphin.register(new CrudActions(crudService: new CrudInMemoryService()))
        result.clientDolphin.presentationModel(SELECTED, null, portfolioId: null)
        result.syncPoint(1)
        result
    }

    // make sure we have an in-memory setup with the server-side wired for the crud app
    protected void setup() {
        LogConfig.noLogs()
        app = initApp()
        clientDolphin = app.clientDolphin
    }

    // make sure the tests only count as ok if context.assertionsDone() has been reached
    protected void cleanup() {
        clientDolphin.sync { app.assertionsDone() }
        assert app.done.await(2, TimeUnit.SECONDS) // max waiting time for async operations to have finished
    }

    void "initialize app and check the initial values"() {
        when: "we call init"
        app.sendSynchronously PortfolioConstants.CMD.PULL
        then: "we have 4 portfolios with 4 positions each"
        def portfolios = clientDolphin.findAllPresentationModelsByType(PortfolioConstants.TYPE.PORTFOLIO)
        portfolios.size() == 4
        portfolios.each { portfolio ->
            portfolio.getAttributes().size() == 4
        }
        and: "there is no selection"
        clientDolphin[SELECTED].portfolioId.value == null
    }

    void "when we select a portfolio and change a position, the total is updated"() {
        given: "we call init"
        app.sendSynchronously PortfolioConstants.CMD.PULL

        when: "we select a portfolio and pull its positions"
        def portfolio = clientDolphin.findAllPresentationModelsByType(PortfolioConstants.TYPE.PORTFOLIO).first()
        clientDolphin[SELECTED].portfolioId.value = portfolio.id
        app.sendSynchronously PositionConstants.CMD.PULL

        then: "the total is 100"
        portfolio[PortfolioConstants.ATT.TOTAL].value == 100

        when: "we add 10 to one position"
        def positions = clientDolphin.findAllPresentationModelsByType(PositionConstants.TYPE.POSITION)
        def domId = portfolio[PortfolioConstants.ATT.DOMAIN_ID].value
        def position = positions.find { it[PositionConstants.ATT.PORTFOLIO_ID].value == domId }
        position[PositionConstants.ATT.WEIGHT].value += 10

        then: "the total is 110"
        app.syncPoint(1) // since a server-side listener needs to be triggered, we have to wait for the roundtrip
        portfolio[PortfolioConstants.ATT.TOTAL].value == 110
    }


}