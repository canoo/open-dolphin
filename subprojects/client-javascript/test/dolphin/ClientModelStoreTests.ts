/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ClientPresentationModel.ts"/>
/// <reference path="../../js/dolphin/ClientConnector.ts"/>
/// <reference path="../../js/dolphin/Map.ts"/>
/// <reference path="../../js/dolphin/ClientDolphin.ts"/>
/// <reference path="../../js/dolphin/ClientModelStore.ts"/>
/// <reference path="../../js/dolphin/ClientAttribute.ts"/>
/// <reference path="../../js/dolphin/Command.ts"/>
/// <reference path="../../js/dolphin/SignalCommand.ts"/>


module opendolphin {

    class TestTransmitter implements Transmitter {
        constructor(public clientCommands, public serverCommands) {
        }

        signal(command:SignalCommand) : void { /* do nothing */; }
        reset(successHandler:OnSuccessHandler) : void { /** do nothing */ }

        transmit(commands:Command[], onDone:(result:Command[]) => void):void {
            this.clientCommands = commands;
            onDone(this.serverCommands);
        }
    }
    export class ClientModelStoreTests extends tsUnit.TestClass {

        addAndRemovePresentationModel() {
            var serverCommand:Command[]=[];//to test
            var transmitter = new TestTransmitter(undefined, serverCommand);
            var clientDolphin = new ClientDolphin();
            var clientConnector = new ClientConnector(transmitter,clientDolphin);
            var clientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientConnector(clientConnector);
            clientDolphin.setClientModelStore(clientModelStore);

            var type:Type;
            var pm:ClientPresentationModel;
            clientModelStore.onModelStoreChange((evt:ModelStoreEvent) => {
                type = evt.eventType;
                pm = evt.clientPresentationModel;
            })

            var pm1 = new ClientPresentationModel("id1", "type");
            var pm2 = new ClientPresentationModel("id2", "type");
            clientModelStore.add(pm1);
            this.areIdentical(type, Type.ADDED);
            this.areIdentical(pm, pm1);

            clientModelStore.add(pm2);
            this.areIdentical(type, Type.ADDED);
            this.areIdentical(pm, pm2);

            var ids:string[] = clientModelStore.listPresentationModelIds();
            this.areIdentical(ids.length, 2);
            this.areIdentical(ids[1], "id2");

            var pms:ClientPresentationModel[] = clientModelStore.listPresentationModels();
            this.areIdentical(pms.length, 2);
            this.areIdentical(pms[0], pm1);

            var pm = clientModelStore.findPresentationModelById("id2");
            this.areIdentical(pm, pm2);
            this.isTrue(clientModelStore.containsPresentationModel("id1"));

            clientModelStore.remove(pm1);
            this.areIdentical(type, Type.REMOVED);
            this.areIdentical(pm, pm1);

            var ids:string[] = clientModelStore.listPresentationModelIds();
            this.areIdentical(ids.length, 1);
            this.areIdentical(ids[0], "id2");

            var pms:ClientPresentationModel[] = clientModelStore.listPresentationModels();
            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0], pm2);

            this.isFalse(clientModelStore.containsPresentationModel("id1"));
        }

        listenForPresentationModelChangesByType() {
            var serverCommand:Command[]=[];//to test
            var transmitter = new TestTransmitter(undefined, serverCommand);
            var clientDolphin = new ClientDolphin();
            var clientConnector = new ClientConnector(transmitter,clientDolphin);
            var clientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientConnector(clientConnector);
            clientDolphin.setClientModelStore(clientModelStore);

            var type:Type;
            var pm:ClientPresentationModel;
            // only listen for a specific type
            clientModelStore.onModelStoreChangeForType("type", (evt:ModelStoreEvent) => {
                type = evt.eventType;
                pm = evt.clientPresentationModel;
            })

            var pm1 = new ClientPresentationModel("id1", "type");
            var pm2 = new ClientPresentationModel("id2", "type");
            var pm3 = new ClientPresentationModel("id3", "some other type");

            clientModelStore.add(pm1);
            this.areIdentical(type, Type.ADDED);
            this.areIdentical(pm, pm1);

            clientModelStore.add(pm2);
            this.areIdentical(type, Type.ADDED);
            this.areIdentical(pm, pm2);

            clientModelStore.add(pm3);
            this.areIdentical(pm, pm2); // adding pm3 did not change the last pm !!!

            // but it is in the model store
            var ids:string[] = clientModelStore.listPresentationModelIds();
            this.areIdentical(ids.length, 3);
            this.areIdentical(ids[2], "id3");

            var pms:ClientPresentationModel[] = clientModelStore.listPresentationModels();
            this.areIdentical(pms.length, 3);
            this.areIdentical(pms[0], pm1);

            var pm = clientModelStore.findPresentationModelById("id3");
            this.areIdentical(pm, pm3);
            this.isTrue(clientModelStore.containsPresentationModel("id3"));

            clientModelStore.remove(pm1);
            this.areIdentical(type, Type.REMOVED);
            this.areIdentical(pm, pm1);

            clientModelStore.remove(pm3); // listener ist _not_ triggered!
            this.areIdentical(pm, pm1);
        }

        addAndRemovePresentationModelByType() {
            var pm1 = new ClientPresentationModel("id1", "type");
            var pm2 = new ClientPresentationModel("id2", "type");

            var clientDolphin = new ClientDolphin();
            var clientModelStore = new ClientModelStore(clientDolphin);

            clientModelStore.addPresentationModelByType(pm1);
            var pms:ClientPresentationModel[] = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);

            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id1");

            clientModelStore.addPresentationModelByType(pm2);
            var pms = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);
            this.areIdentical(pms.length, 2);
            this.areIdentical(pms[0].id, "id1");
            this.areIdentical(pms[1].id, "id2");

            clientModelStore.removePresentationModelByType(pm1);
            var pms = clientModelStore.findAllPresentationModelByType(pm1.presentationModelType);
            this.areIdentical(pms.length, 1);
            this.areIdentical(pms[0].id, "id2");
        }

        addAndRemoveAttributeById() {
            var clientDolphin = new ClientDolphin();
            var clientModelStore = new ClientModelStore(clientDolphin);

            var attr1 = new ClientAttribute("prop1", "qual1", 0);
            var attr2 = new ClientAttribute("prop2", "qual2", 0);

            clientModelStore.addAttributeById(attr1);
            clientModelStore.addAttributeById(attr2);

            var result1 = clientModelStore.findAttributeById(attr1.id);
            this.areIdentical(attr1, result1);

            var result2 = clientModelStore.findAttributeById(attr2.id);
            this.areIdentical(attr2, result2);

            clientModelStore.removeAttributeById(attr1);
            var result1 = clientModelStore.findAttributeById(attr1.id);
            this.areIdentical(result1, undefined);

        }

        addAndRemoveClientAttributeByQualifier() {
            var attr1 = new ClientAttribute("prop1", "qual1", 0);
            var attr2 = new ClientAttribute("prop2", "qual2", 0);

            var attr3 = new ClientAttribute("prop3", "qual1", 0);
            var attr4 = new ClientAttribute("prop4", "qual2", 0);

            var clientDolphin = new ClientDolphin();
            var clientModelStore = new ClientModelStore(clientDolphin);

            clientModelStore.addAttributeByQualifier(attr1);
            clientModelStore.addAttributeByQualifier(attr2);
            clientModelStore.addAttributeByQualifier(attr3);
            clientModelStore.addAttributeByQualifier(attr4);

            var clientAttrs1:ClientAttribute[] = clientModelStore.findAllAttributesByQualifier("qual1");

            this.areIdentical(clientAttrs1.length, 2);
            this.areIdentical(clientAttrs1[0].getQualifier(), "qual1");
            this.areIdentical(clientAttrs1[1].getQualifier(), "qual1");

            var clientAttrs2:ClientAttribute[] = clientModelStore.findAllAttributesByQualifier("qual2");

            this.areIdentical(clientAttrs2.length, 2);
            this.areIdentical(clientAttrs2[0].getQualifier(), "qual2");
            this.areIdentical(clientAttrs2[1].getQualifier(), "qual2");

            clientModelStore.removeAttributeByQualifier(attr1);
            var clientAttrs1:ClientAttribute[] = clientModelStore.findAllAttributesByQualifier("qual1");
            this.areIdentical(clientAttrs1.length, 1);
            this.areIdentical(clientAttrs1[0].getQualifier(), "qual1");
            this.areIdentical(clientAttrs1[1], undefined);


        }

        checkAttributeBaseValueChangeForSameQualifier(){
            var serverCommand:Command[]=[];//to test
            var transmitter = new TestTransmitter(undefined, serverCommand);
            var clientDolphin = new ClientDolphin();
            var clientConnector = new ClientConnector(transmitter,clientDolphin);
            var clientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientConnector(clientConnector);
            clientDolphin.setClientModelStore(clientModelStore);

            var pm1 = new ClientPresentationModel(undefined,undefined);
            var clientAttr1 = new ClientAttribute("property1","same-qualifier","value1","VALUE");

            var pm2 = new ClientPresentationModel(undefined,undefined);
            var clientAttr2 = new ClientAttribute("property2","same-qualifier","value2","VALUE");

            pm1.addAttribute(clientAttr1);
            pm2.addAttribute(clientAttr2);

            clientModelStore.add(pm1);
            clientModelStore.add(pm2);

            clientAttr1.setValue("Test");
            this.areIdentical(clientAttr1.getBaseValue(), "value1");
            this.isTrue(clientAttr1.isDirty());
            clientAttr1.rebase();

            this.areIdentical(clientAttr1.getValue(), "Test");
            this.areIdentical(clientAttr1.getBaseValue(), "Test");
            this.isFalse(clientAttr1.isDirty());


            this.areIdentical(clientAttr2.getValue(), "Test");
            this.areIdentical(clientAttr2.getBaseValue(), "Test");
            this.isFalse(clientAttr2.isDirty());


        }

    }
}