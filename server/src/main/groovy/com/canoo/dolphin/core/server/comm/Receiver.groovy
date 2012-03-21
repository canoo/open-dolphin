package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Command
import groovy.util.logging.Log

@Log
class Receiver {

    ActionRegistry registry = new ActionRegistry()

    /** doesn't fail on missing commands **/
    List<Command> receive(Command command) {
        log.info "S: received: $command"
        List<Command> response = new LinkedList() // collecting parameter pattern
        def action = registry[command.id]
        if (null == action){
            log.warning "S: there is no server action registered for received command: $command"
            return response
        }
        action command, response
        return response
    }

}