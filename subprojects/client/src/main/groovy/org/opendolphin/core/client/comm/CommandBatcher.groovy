package org.opendolphin.core.client.comm

import groovyx.gpars.dataflow.DataflowQueue

class CommandBatcher implements ICommandBatcher {

    final DataflowQueue<List<CommandAndHandler>> waitingBatches = new DataflowQueue<>()

	void batch(CommandAndHandler commandAndHandler) {
        waitingBatches << [commandAndHandler]
	}

	boolean isEmpty() {
		return waitingBatches.length() < 1
	}

}
