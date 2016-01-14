/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ChangeAttributeMetadataCommand.ts"/>


module opendolphin {
    export class ChangeAttributeMetadataCommandTests extends tsUnit.TestClass {

        createChangedAttrMetaDataCommandWithGivenParameter(){
            var changedAttrMDCommand = new ChangeAttributeMetadataCommand("10", "MDName", 20);
            this.areIdentical(changedAttrMDCommand.id,"ChangeAttributeMetadata");
            this.areIdentical(changedAttrMDCommand.className,"org.opendolphin.core.comm.ChangeAttributeMetadataCommand");
            this.areIdentical(changedAttrMDCommand.attributeId, "10");
            this.areIdentical(changedAttrMDCommand.metadataName,"MDName");
            this.areIdentical(changedAttrMDCommand.value,20);
        }

    }
}