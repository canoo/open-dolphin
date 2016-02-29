package org.opendolphin.demo.crud

import groovy.transform.Canonical
import org.opendolphin.core.Dolphin
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Attribute

@Canonical
final class Portfolio {

    PresentationModel presentationModel

    Attribute name() {
        presentationModel.getAt(PortfolioConstants.ATT.NAME)
    }
    String getName() {
        name().value
    }
    void setName(String newValue) {
        name().value = newValue
    }

    Attribute domainId() {
        presentationModel.getAt(PortfolioConstants.ATT.DOMAIN_ID)
    }
    long getDomainId() {
        domainId().value
    }
    void setDomainId(long newValue) {
        domainId().value = newValue
    }


    Attribute fixed() {
        presentationModel.getAt(PortfolioConstants.ATT.FIXED)
    }
    boolean getFixed() {
        fixed().value
    }
    void setFixed(boolean newValue) {
        fixed().value = newValue
    }


    Attribute total() {
        presentationModel.getAt(PortfolioConstants.ATT.TOTAL)
    }
    int getTotal() {
        total().value
    }
    void setTotal(int newValue) {
        total().value = newValue
    }

    static TYPE = PortfolioConstants.TYPE.PORTFOLIO

    List<Position> positions(Dolphin dolphin) {
        def all = dolphin.findAllPresentationModelsByType(Position.TYPE)
        def mine = all.collect { new Position(it) }.findAll { pos ->
            pos.getPortfolioDomainId() == getDomainId()
        }
        return mine
    }
}
