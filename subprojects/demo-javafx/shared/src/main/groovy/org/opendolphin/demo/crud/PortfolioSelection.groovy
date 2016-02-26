package org.opendolphin.demo.crud

import groovy.transform.Canonical
import org.opendolphin.core.Attribute
import org.opendolphin.core.Dolphin
import org.opendolphin.core.PresentationModel

@Canonical
final class PortfolioSelection {

    PresentationModel presentationModel

    public static final String ATT_PORTFOLIO_ID = 'portfolioId'
    public static final String PM_ID_SELECTED = PortfolioConstants.unique 'selected'

    Attribute portfolioId() {
        presentationModel.getAt(ATT_PORTFOLIO_ID)
    }
    String getPortfolioId() {
        portfolioId().value
    }
    void setPortfolioId(String name) {
        portfolioId().value = name
    }

    static PortfolioSelection selection(Dolphin dolphin) {
        new PortfolioSelection(dolphin.getAt(PM_ID_SELECTED))
    }

}
