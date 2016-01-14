/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ValueChangedCommand.ts"/>


module opendolphin {
    export class ValueChangedCommandTests extends tsUnit.TestClass {

        createValueChangedCommandWithGivenParameter(){
            var valueChangedCommand = new ValueChangedCommand("10", 10, 20);
            this.areIdentical(valueChangedCommand.id,"ValueChanged");
            this.areIdentical(valueChangedCommand.className,"org.opendolphin.core.comm.ValueChangedCommand");
            this.areIdentical(valueChangedCommand.attributeId, "10");
            this.areIdentical(valueChangedCommand.oldValue,10);
            this.areIdentical(valueChangedCommand.newValue,20);
        }

    }
}