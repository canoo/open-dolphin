package org.opendolphin.core.client.comm

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Dataflow
import org.opendolphin.core.comm.GetPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand

import java.util.concurrent.atomic.AtomicBoolean
/**
 * A command batcher that puts all commands in one packet that
 * have no onFinished handler attached (blind commands), which is the typical case
 * for value change and create presentation model commands
 * when synchronizing back to the server.
 */

@Log @CompileStatic
class BlindCommandBatcher extends CommandBatcher {

    final protected Agent<LinkedList<CommandAndHandler>> agent = Agent.agent(new LinkedList<CommandAndHandler>())

    /** Time allowed to fill the queue before a batch is assembled */
    long deferMillis = 10
    /** Must be > 0*/
    int maxBatchSize = 100
    /** when attribute x changes its value from 0 to 1 and then from 1 to 2, merge this into one change from 0 to 2 */
    boolean mergeValueChanges = false

    protected final AtomicBoolean inProcess      = new AtomicBoolean(false) // whether we started to batch up commands
    protected final AtomicBoolean deferralNeeded = new AtomicBoolean(false) // whether we need to give commands the opportunity to enter the queue
    protected boolean shallWeEvenTryToMerge      = false // do not even try if there is no value change cmd in the batch

    @Override
    void batch(CommandAndHandler commandWithHandler) {
        log.finest "batching $commandWithHandler.command with${commandWithHandler.handler ? '' : 'out' } handler"

        if (canBeDropped(commandWithHandler)) {
            log.finest "dropping duplicate GetPresentationModelCommand"
            return
        }

        agent << { List<CommandAndHandler> commandsAndHandlers ->
            commandsAndHandlers << commandWithHandler
        }
        if (commandWithHandler.isBatchable()) {
            deferralNeeded.set(true)
            if (inProcess.get()) return
            processDeferred()
        } else {
            processBatch()
        }
    }

    protected final int MAX_GET_PM_CMD_CACHE_SIZE = 200
    protected LinkedList<CommandAndHandler> cacheGetPmCmds = new LinkedList()

    // the only command we can safely drop is a GetPmCmd where a second one for the same
    // pmId is already in the batch with the exact same onFinished handler or no handler at all
    protected boolean canBeDropped(CommandAndHandler commandWithHandler ) {
        if (! (commandWithHandler.command instanceof GetPresentationModelCommand)) return false
        def pmId = ((GetPresentationModelCommand)commandWithHandler.command).pmId
        def handler = commandWithHandler.handler
        def found = cacheGetPmCmds.any { CommandAndHandler it ->
            ((GetPresentationModelCommand) it.command).pmId == pmId &&
            ((handler == null) || handler.is(it.handler))
        }
        if (!found) {
            cacheGetPmCmds.push commandWithHandler // front adding makes lookup faster
            if (cacheGetPmCmds.size() > MAX_GET_PM_CMD_CACHE_SIZE) cacheGetPmCmds.removeLast()
        }
        return found
    }

    protected void processDeferred() {
        inProcess.set(true)
        Dataflow.task {
            if (deferralNeeded.get()) {
                deferralNeeded.set(false)
                sleep(deferMillis)          // while we sleep, new requests may have arrived that request deferral
            }
            processBatch()
            inProcess.set(false)
        }
    }

    protected void processBatch() {
        agent << {  List<CommandAndHandler> commandsAndHandlers ->
            def last = batchBlinds(commandsAndHandlers) // always send leading blinds first
            if (last) {                                 // we do have a trailing command with handler and batch it separately
                waitingBatches << [last]
            }
            if ( ! commandsAndHandlers.empty) {
                processBatch()                          // this is not so much like recursion, more like a trampoline
            }
        }
    }

    protected CommandAndHandler batchBlinds(List<CommandAndHandler> queue) {
        if(queue.empty) return
        List<CommandAndHandler> blindCommands = new LinkedList()
        int counter = maxBatchSize                      // we have to check again, since new ones may have arrived since last check
        def val = take(queue)
        shallWeEvenTryToMerge = false
        while (counter-- && val?.isBatchable()) {      // we do have a blind
            addToBlindsOrMerge(blindCommands, val)
            val = counter ? take(queue) : null
        }
        log.finest "batching ${blindCommands.size()} blinds"
        if (blindCommands) waitingBatches << blindCommands
        return val // may be null or a cwh that has a handler
    }

    protected void addToBlindsOrMerge(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if ( ! wasMerged(blindCommands,val)) {
            blindCommands << val
            if (val.command in ValueChangedCommand) shallWeEvenTryToMerge = true
        }
    }

    protected boolean wasMerged(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if ( ! mergeValueChanges)                   return false
        if ( ! shallWeEvenTryToMerge )              return false
        if (blindCommands.empty)                    return false
        if ( ! val.command)                         return false
        if (! (val.command in ValueChangedCommand)) return false
        ValueChangedCommand valCmd = (ValueChangedCommand) val.command

        shallWeEvenTryToMerge = true

        def mergeable = blindCommands.find { CommandAndHandler cah ->           // this has O(n*n) and can become costly
            cah.command != null &&
            cah.command instanceof ValueChangedCommand &&
            ((ValueChangedCommand)cah.command).attributeId == valCmd.attributeId &&
            ((ValueChangedCommand)cah.command).newValue == valCmd.oldValue
        }
        if (! mergeable) return false

        ValueChangedCommand mergeableCmd = (ValueChangedCommand) mergeable.command
        log.finest("merging value changed command for attribute ${mergeableCmd.attributeId} with new values ${mergeableCmd.newValue} -> ${valCmd.newValue}")
        mergeableCmd.newValue = valCmd.newValue

        return true
    }

    protected CommandAndHandler take(List<CommandAndHandler> intern) {
        if (intern.empty) return null
   		return intern.remove(0)
   	}
}
