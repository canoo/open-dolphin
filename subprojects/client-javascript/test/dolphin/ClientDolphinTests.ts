/// <reference path="../../testsuite/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ClientAttribute.ts"/>
/// <reference path="../../js/dolphin/ClientPresentationModel.ts"/>
/// <reference path="../../js/dolphin/ClientDolphin.ts"/>
/// <reference path="../../js/dolphin/ClientModelStore.ts"/>
/// <reference path="../../js/dolphin/ClientConnector.ts"/>
/// <reference path="../../js/dolphin/NoTransmitter.ts"/>
/// <reference path="../../js/dolphin/Command.ts"/>
/// <reference path="../../js/dolphin/Tag.ts"/>


module opendolphin {
    export class ClientDolphinTests extends tsUnit.TestClass {
        getPmFromFactoryMethod() {
            var clientDolphin:ClientDolphin = new ClientDolphin();
            var clientModelStore:ClientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientModelStore(clientModelStore);
            clientDolphin.setClientConnector(new ClientConnector(new NoTransmitter(), clientDolphin));

            var pm1:ClientPresentationModel = clientDolphin.presentationModel("myId1", "myType");
            this.areIdentical(pm1.id, "myId1");
            this.areIdentical(pm1.getAttributes().length, 0);

            var ca1 = new ClientAttribute("prop1", "qual1", "val");
            var ca2 = new ClientAttribute("prop2", "qual2", "val");

            var pm2:ClientPresentationModel = clientDolphin.presentationModel("myId2", "myType", ca1, ca2);
            this.areIdentical(pm2.id, "myId2");
            this.areIdentical(pm2.getAttributes().length, 2);
        }

        tagTheAttribute() {
            var clientDolphin:ClientDolphin = new ClientDolphin();
            var clientModelStore:ClientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientModelStore(clientModelStore);
            clientDolphin.setClientConnector(new ClientConnector(new NoTransmitter(), clientDolphin));

            var pm:ClientPresentationModel = clientDolphin.presentationModel("myId", "myType");
            this.areIdentical(pm.getAttributes().length, 0);

            clientDolphin.tag(pm,"property","value",Tag.tooltip());
            this.areIdentical(pm.getAttributes().length, 1);
            this.areIdentical(pm.getAttributes()[0].tag, "TOOLTIP");
        }

        createAttributeWithValueTagAsDefault() {
            var clientDolphin:ClientDolphin = new ClientDolphin();
            var clientModelStore:ClientModelStore = new ClientModelStore(clientDolphin);
            clientDolphin.setClientModelStore(clientModelStore);
            clientDolphin.setClientConnector(new ClientConnector(new NoTransmitter(), clientDolphin));

            var attr = clientDolphin.attribute("prop1", "qual1", "val");
            this.areIdentical(Tag.value(), attr.tag);
        }
    }
}