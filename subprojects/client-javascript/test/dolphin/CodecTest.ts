/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/Codec.ts"/>
/// <reference path="../../js/dolphin/ClientAttribute.ts"/>
/// <reference path="../../js/dolphin/ClientPresentationModel.ts"/>
/// <reference path="../../js/dolphin/CreatePresentationModelCommand.ts"/>
/// <reference path="../../js/dolphin/AttributeMetadataChangedCommand.ts"/>
/// <reference path="../../js/dolphin/CallNamedActionCommand.ts"/>
/// <reference path="../../js/dolphin/ChangeAttributeMetadataCommand.ts"/>
/// <reference path="../../js/dolphin/AttributeCreatedNotification.ts"/>
/// <reference path="../../js/dolphin/GetPresentationModelCommand.ts"/>
/// <reference path="../../js/dolphin/DeleteAllPresentationModelsOfTypeCommand.ts"/>
/// <reference path="../../js/dolphin/DeletedAllPresentationModelsOfTypeNotification.ts"/>
/// <reference path="../../js/dolphin/DeletedPresentationModelNotification.ts"/>
/// <reference path="../../js/dolphin/DeletePresentationModelCommand.ts"/>
/// <reference path="../../js/dolphin/EmptyNotification.ts"/>
/// <reference path="../../js/dolphin/InitializeAttributeCommand.ts"/>
/// <reference path="../../js/dolphin/NamedCommand.ts"/>
/// <reference path="../../js/dolphin/SignalCommand.ts"/>
/// <reference path="../../js/dolphin/PresentationModelResetedCommand.ts"/>
/// <reference path="../../js/dolphin/ResetPresentationModelCommand.ts"/>
/// <reference path="../../js/dolphin/SavedPresentationModelNotification.ts"/>
/// <reference path="../../js/dolphin/SwitchPresentationModelCommand.ts"/>
/// <reference path="../../js/dolphin/ValueChangedCommand.ts"/>
/// <reference path="../../js/dolphin/DataCommand.ts"/>

module opendolphin {
    export class CodecTest extends tsUnit.TestClass {

        testCodingCreatePresentationModel() {
            var pm = new ClientPresentationModel("MyId", "MyType");
            var clientAttribute1 = new ClientAttribute("prop1", "qual1", 0);
            var clientAttribute2 = new ClientAttribute("prop2", "qual2", 0);
            pm.addAttribute(clientAttribute1);
            pm.addAttribute(clientAttribute2);
            var createPMCommand = new CreatePresentationModelCommand(pm);

            var codec = new Codec();

            var coded = codec.encode(createPMCommand);
            var decoded = codec.decode(coded);

            this.isTrue(createPMCommand.toString() === decoded.toString());
        }

        testEmpty() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(0));
        }

        testOne() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(1));
        }

        testMany() {
            this.isTrue(CodecTestHelper.testSoManyCommandsEncoding(10));
        }

        testCodingCommands() {
            this.isTrue(CodecTestHelper.testCodingCommand(new AttributeCreatedNotification("pmId", "5", "prop", "äöüéàè", "qualifier", "TOOLTIP")))
            this.isTrue(CodecTestHelper.testCodingCommand(new AttributeMetadataChangedCommand("5", "name", "value")))
            this.isTrue(CodecTestHelper.testCodingCommand(new CallNamedActionCommand("some-action")))
            this.isTrue(CodecTestHelper.testCodingCommand(new CreatePresentationModelCommand(new ClientPresentationModel("MyId", "MyType"))))
            this.isTrue(CodecTestHelper.testCodingCommand(new ChangeAttributeMetadataCommand("5", "name", "value")))
            this.isTrue(CodecTestHelper.testCodingCommand(new GetPresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new DataCommand("test")));
            this.isTrue(CodecTestHelper.testCodingCommand(new DeleteAllPresentationModelsOfTypeCommand("type")))
            this.isTrue(CodecTestHelper.testCodingCommand(new DeletedAllPresentationModelsOfTypeNotification("type")))
            this.isTrue(CodecTestHelper.testCodingCommand(new DeletedPresentationModelNotification("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new DeletePresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new EmptyNotification()))
            this.isTrue(CodecTestHelper.testCodingCommand(new InitializeAttributeCommand("pmId", "prop", "qualifier", "value", "pmType")))
            this.isTrue(CodecTestHelper.testCodingCommand(new NamedCommand("name")))
            this.isTrue(CodecTestHelper.testCodingCommand(new PresentationModelResetedCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new ResetPresentationModelCommand("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new SavedPresentationModelNotification("pmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new SignalCommand("signal")))
            this.isTrue(CodecTestHelper.testCodingCommand(new SwitchPresentationModelCommand("pmId", "sourcePmId")))
            this.isTrue(CodecTestHelper.testCodingCommand(new ValueChangedCommand("5", "oldValue", "newValue")))
        }
    }

    class CodecTestHelper {

        static testSoManyCommandsEncoding(count:number):boolean {
            var codec:Codec = new Codec();
            var commands:AttributeCreatedNotification[] = [];

            for (var i = 0; i < count; i++) {
                commands.push(new AttributeCreatedNotification(i.toString(), "" + i * count, "prop" + i, "value" + i, null));
            }

            var coded = codec.encode(commands);
            var decoded = codec.decode(coded);

            if (commands.toString() === decoded.toString()) {
                return true;
            } else {
                return false;
            }
        }

        static testCodingCommand(command:any) {
            var codec:Codec = new Codec();
            var coded = codec.encode(command);
            var decoded = codec.decode(coded);
            if (command.toString() === decoded.toString()) {
                return true;
            } else {
                return false;
            }
        }
    }
}