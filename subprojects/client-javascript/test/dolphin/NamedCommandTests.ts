/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/NamedCommand.ts"/>


module opendolphin {
    export class NamedCommandTests extends tsUnit.TestClass {

        createNamedCommandWithGivenParameter(){
            var namedCommand = new NamedCommand("CustomId");
            this.areIdentical(namedCommand.id,"CustomId");
            this.areIdentical(namedCommand.className,"org.opendolphin.core.comm.NamedCommand");
        }

    }
}