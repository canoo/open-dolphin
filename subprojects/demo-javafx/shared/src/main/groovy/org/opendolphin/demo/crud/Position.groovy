package org.opendolphin.demo.crud

import groovy.transform.Canonical
import groovy.util.logging.Log
import org.opendolphin.core.Attribute
import org.opendolphin.core.Dolphin
import org.opendolphin.core.PresentationModel

@Canonical
@Log
final class Position {

    PresentationModel presentationModel

    Attribute portfolioDomainId() {
        presentationModel.getAt(PositionConstants.ATT.PORTFOLIO_DOMAIN_ID)
    }
    long getPortfolioDomainId() {
        portfolioDomainId().value
    }
    void setPortfolioDomainId(long newValue) {
        portfolioDomainId().value = newValue
    }

    Attribute instrument() {
        presentationModel.getAt(PositionConstants.ATT.INSTRUMENT)
    }
    String getInstrument() {
        instrument().value
    }
    void setInstrument(String newValue) {
        instrument().value = newValue
    }

    Attribute weight() {
        presentationModel.getAt(PositionConstants.ATT.WEIGHT)
    }
    int getWeight() {
        weight().value
    }
    void setWeight(int newValue) {
        weight().value = newValue
    }

    static TYPE = PositionConstants.TYPE.POSITION

    Portfolio findPortfolio(Dolphin dolphin) {
        def allPortfolioPms  = dolphin.findAllPresentationModelsByType(Portfolio.TYPE)
        def portfolioPm = allPortfolioPms.find { new Portfolio(it).getDomainId() == getPortfolioDomainId() }
        if (! portfolioPm) {
            log.warning "cannot find portfolio for position with domain id ${getPortfolioDomainId()}"
            return
        }
        new Portfolio(portfolioPm)
    }

}
