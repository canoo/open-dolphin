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

import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static org.opendolphin.demo.crud.PortfolioConstants.*
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.DOMAIN_ID
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.TOTAL
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static org.opendolphin.demo.crud.PortfolioConstants.TYPE.PORTFOLIO
import static org.opendolphin.demo.crud.PositionConstants.ATT.PORTFOLIO_ID
import static org.opendolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static org.opendolphin.demo.crud.PositionConstants.TYPE.POSITION

class CrudActions extends DolphinServerAction {

    CrudService crudService

    void registerIn(ActionRegistry registry) {

        /*
         * Pull all portfolios from service lookup
         */
        serverDolphin.action PortfolioConstants.CMD.PULL, { cmd, response ->
            def portfolios = crudService.listPortfolios(1L) // fixed value until we have users
            portfolios.eachWithIndex { portfolioDTO, index ->
                presentationModel pmId(PORTFOLIO, index), PORTFOLIO, portfolioDTO
            }
        }

        /*
         * Pull all positions for the selected portfolio from service lookup
         */
        serverDolphin.action PositionConstants.CMD.PULL, { cmd, response ->
            def visiblePortfolio  = serverDolphin.findPresentationModelById(SELECTED)
            def selectedPortfolio = serverDolphin.findPresentationModelById(visiblePortfolio[PORTFOLIO_ID].value as String)
            def positions = crudService.listPositions(selectedPortfolio[DOMAIN_ID].value.toLong())
            positions.eachWithIndex { positionDTO, index ->
                positionDTO.slots << new Slot(PORTFOLIO_ID, selectedPortfolio[DOMAIN_ID].value)
                presentationModel null, POSITION, positionDTO
            }
        }

        /*
         * Whenever a position is added or removed or its weight is changed, we need to update the total
         */

        serverDolphin.addModelStoreListener POSITION, { ModelStoreEvent event ->
            recalculateTotal(event.presentationModel)
        }

        registry.register(ValueChangedCommand) { ValueChangedCommand cmd, resp ->
            // make sure we have a value change for a position weight
            def attribute = serverDolphin.serverModelStore.findAttributeById(cmd.attributeId)
            if (! attribute) return
            if (WEIGHT   != attribute.propertyName) return
            def position = attribute.presentationModel
            if (POSITION != position.presentationModelType) return
            recalculateTotal(position)
        }
    }

    protected void recalculateTotal(PresentationModel position) {

        def portfolioDomainId = position[PORTFOLIO_ID].value
        def allPortfolios     = serverDolphin.findAllPresentationModelsByType(PORTFOLIO);
        def currentPortfolio  = allPortfolios.find { it[DOMAIN_ID].value == portfolioDomainId }
        if (! currentPortfolio) {
            println "cannot find portfolio for position ${position.dump()}"
            return
        }
        def allPositions = serverDolphin.findAllPresentationModelsByType(POSITION)
        def positions    = allPositions.findAll { it[PORTFOLIO_ID].value == portfolioDomainId }
        def total        = positions.sum { it[WEIGHT].value }

        currentPortfolio[TOTAL].value = total
    }
}
