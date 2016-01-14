/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ClientAttribute.ts"/>
/// <reference path="../../js/dolphin/ClientPresentationModel.ts"/>
/// <reference path="../../js/dolphin/CreatePresentationModelCommand.ts"/>


module opendolphin {
    export class CreatePresentationModelCommandTests extends tsUnit.TestClass {

        createPresentationModelCommandWithGivenParameter(){
            var pm = new ClientPresentationModel("MyId","MyType");
            var clientAttribute1 = new ClientAttribute("prop1", "qual1", 0);
            var clientAttribute2 = new ClientAttribute("prop2", "qual2", 0);
            pm.addAttribute(clientAttribute1);
            pm.addAttribute(clientAttribute2);
            var createPMCommand = new CreatePresentationModelCommand(pm);
            this.areIdentical(createPMCommand.id,"CreatePresentationModel");
            this.areIdentical(createPMCommand.className,"org.opendolphin.core.comm.CreatePresentationModelCommand");
            this.areIdentical(createPMCommand.pmId,"MyId");
            this.areIdentical(createPMCommand.pmType,"MyType");

            this.areIdentical(createPMCommand.attributes.length,2);
            this.areIdentical(createPMCommand.attributes[0].propertyName,"prop1");
            this.areIdentical(createPMCommand.attributes[1].propertyName,"prop2");
        }

    }
}