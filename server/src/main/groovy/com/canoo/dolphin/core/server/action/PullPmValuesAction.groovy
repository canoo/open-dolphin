package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.comm.ActionRegistry

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.comm.PullPmAttributesCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand

class PullPmValuesAction {

    def registerIn(ActionRegistry registry) {
        registry.register PullPmAttributesCommand, { PullPmAttributesCommand command, response ->

            BasePresentationModel pm = StoreAttributeAction.instance.modelStore[command.pmId]
            pm.attributes.each {
                response << new InitializeAttributeCommand(pmId: pm.id, propertyName: it.propertyName, newValue: it.value)
            }
        }
    }
}
