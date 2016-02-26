package org.opendolphin.demo.crud

import groovy.transform.Canonical
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Attribute

@Canonical
class Portfolio {

    PresentationModel presentationModel

    Attribute name() {
        presentationModel.getAt(PortfolioConstants.ATT.NAME)
    }
    String getName() {
        name().value
    }
    void setName(String name) {
        name().value = name
    }

    Attribute domainId() {
        presentationModel.getAt(PortfolioConstants.ATT.DOMAIN_ID)
    }
    int getDomainId() {
        domainId().value
    }
    void setDomainId(int domainId) {
        domainId().value = domainId
    }


    Attribute fixed() {
        presentationModel.getAt(PortfolioConstants.ATT.FIXED)
    }
    boolean getFixed() {
        fixed().value
    }
    void setFixed(boolean fixed) {
        fixed().value = fixed
    }


    Attribute total() {
        presentationModel.getAt(PortfolioConstants.ATT.TOTAL)
    }
    int getTotal() {
        total().value
    }
    void setTotal(int total) {
        total().value = total
    }

    static TYPE = PortfolioConstants.TYPE.PORTFOLIO

}
