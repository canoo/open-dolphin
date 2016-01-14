/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/CommandBatcher.ts"/>
/// <reference path="../../js/dolphin/ClientConnector.ts"/>
/// <reference path="../../js/dolphin/Command.ts"/>
/// <reference path="../../js/dolphin/ValueChangedCommand.ts"/>


module opendolphin {

    export class CommandBatcherTests extends tsUnit.TestClass {

        noBatcherDoesNotBatch() {
            var whateverCommandAndHandler : CommandAndHandler = {command: null, handler: null};
            var queue = [ whateverCommandAndHandler, whateverCommandAndHandler, whateverCommandAndHandler ];

            var batcher = new NoCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  2);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  1);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  0);

        }

        simpleBlindBatching() {
            var whateverCommandAndHandler : CommandAndHandler = { command: { id:"x" }, handler: null };
            var queue = [ whateverCommandAndHandler, whateverCommandAndHandler, whateverCommandAndHandler ];

            var batcher = new BlindCommandBatcher();

            var result = batcher.batch(queue);

            this.areIdentical( result.length, 3);
            this.areIdentical( queue.length,  0);
        }

        blindBatchingWithNonBlind() {
            var blind   : CommandAndHandler = { command: { id:"x"}, handler: null };
            var finisher: OnFinishedHandler = { onFinished : null, onFinishedData: null };
            var handled : CommandAndHandler = { command: { id:"x"}, handler: finisher };

            var queue = [ handled, blind, blind, handled, blind, handled ]; // batch sizes 1, 3, 2

            var batcher = new BlindCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 3);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 2);

            this.areIdentical(result[0], blind);  // make sure we have the right sequence
            this.areIdentical(result[1], handled);

            this.areIdentical( queue.length,  0);
        }

        blindFolding() {
            var cmd1    : ValueChangedCommand = new ValueChangedCommand("1", 0, 1);
            var cmd2    : ValueChangedCommand = new ValueChangedCommand("2", 0, 1); // other id, will be batched
            var cmd3    : ValueChangedCommand = new ValueChangedCommand("1", 1, 2); // will be folded

            var queue = [
                { command: cmd1, handler: null },
                { command: cmd2, handler: null },
                { command: cmd3, handler: null }
            ];
            var unfolded = queue[1];

            var batcher = new BlindCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 2);

            this.areIdentical(result[0].command['attributeId'], "1");
            this.areIdentical(result[0].command['oldValue'],    0);
            this.areIdentical(result[0].command['newValue'],    2);
            this.areIdentical(result[1], unfolded);

            this.areIdentical( queue.length,  0);
        }
    }

}