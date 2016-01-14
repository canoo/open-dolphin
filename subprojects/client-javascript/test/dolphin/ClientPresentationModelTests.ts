/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/ClientAttribute.ts"/>
/// <reference path="../../js/dolphin/ClientPresentationModel.ts"/>


module opendolphin {
    export class ClientPresentationModelTests extends tsUnit.TestClass {

        createPmWithAutomaticId() {
            var pm1 = new ClientPresentationModel(undefined,undefined);
            var pm2 = new ClientPresentationModel(undefined,undefined);
            this.areNotIdentical(pm1.id, pm2.id);
        }

        createPmWithGivenId() {
            var pm1 = new ClientPresentationModel("MyId",undefined);
            this.areIdentical(pm1.id, "MyId");
        }

        createPmWithGivenType() {
            var pm1 = new ClientPresentationModel(undefined,"MyType");
            this.areIdentical(pm1.presentationModelType, "MyType");
        }

        addingClientAttributes() {
            var pm1 = new ClientPresentationModel(undefined,undefined);
            this.areIdentical(pm1.getAttributes().length, 0);
            var firstAttribute = new ClientAttribute("prop", "qual", 0);
            pm1.addAttribute(firstAttribute);
            this.areIdentical(pm1.getAttributes().length, 1);
            this.areIdentical(pm1.getAttributes()[0], firstAttribute);
        }

        invalidateClientPresentationModelEvent(){
            var pm1 = new ClientPresentationModel(undefined,undefined);
            var clientAttribute = new ClientAttribute("prop", "qual", 0);
            pm1.addAttribute(clientAttribute);
            var source;
            pm1.onInvalidated((event:InvalidationEvent) => {
                source=event.source;
            });
            clientAttribute.setValue("newValue");
            this.areIdentical(pm1,source);
        }

        checkPresentationModelIsDirty(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            // attributes are not dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM is not dirty
            this.areIdentical(pm.isDirty(),false);
            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);
        }

        checkPresentationModelIsDirtyAfterRebase(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);

            pm.rebase();
            // attributes should not be dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM should not be dirty
            this.areIdentical(pm.isDirty(),false);
        }

        checkPresentationModelIsDirtyAfterReset(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop2","qual2","value2","VALUE");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);

            // attribute1 is dirty
            ca1.setValue("anotherValue");
            this.areIdentical(ca1.isDirty(),true);

            //PM should be dirty
            this.areIdentical(pm.isDirty(),true);

            pm.reset();
            // attributes should not be dirty
            this.areIdentical(ca1.isDirty(),false);
            this.areIdentical(ca2.isDirty(),false);
            //PM should not be dirty
            this.areIdentical(pm.isDirty(),false);
        }

        findAttributeById(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");

            pm.addAttribute(ca1);
            var result = pm.findAttributeById(ca1.id);
            this.areIdentical(ca1,result);
            // find by invalid id
            result=pm.findAttributeById("no-such-id");
            this.areIdentical(result, null);
        }

        findAttributeByQualifier(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");

            pm.addAttribute(ca1);
            var result = pm.findAttributeByQualifier("qual1");
            this.areIdentical(ca1,result);
            // find by invalid qualifier
            result=pm.findAttributeByQualifier("dummy");
            this.areIdentical(result, null);
        }

        findAttributeByPropertyNameAndTag(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop2", "qual", 0);

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            var result = pm.findAttributeByPropertyNameAndTag("prop1","VALUE");
            this.areIdentical(ca1,result);
            // find by invalid property name
            result=pm.findAttributeByPropertyNameAndTag("dummy","VALUE");
            this.areIdentical(result, null);
        }

        findAllAttributesByPropertyName(){
            var pm = new ClientPresentationModel(undefined,undefined);
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop1","qual2","value2","TOOLTIP");

            pm.addAttribute(ca1);
            pm.addAttribute(ca2);
            var result = pm.findAllAttributesByPropertyName("prop1");
            this.areIdentical(result.length, 2);
            this.areIdentical(ca1,result[0]);
            this.areIdentical(ca2,result[1]);
            // find by invalid property name
            result=pm.findAllAttributesByPropertyName("dummy");
            this.areIdentical(result.length, 0);
        }

        simpleCopy() {
            var ca1 = new ClientAttribute("prop1","qual1","value1","VALUE");
            var ca2 = new ClientAttribute("prop2","qual2","value2","VALUE");
            var pm1 = new ClientPresentationModel("pmId","pmType");
            pm1.addAttribute(ca1);
            pm1.addAttribute(ca2);

            var pm2 = pm1.copy();
            this.areNotIdentical(pm1.id, pm2.id);
            this.areIdentical(true, pm2.clientSideOnly);

            this.areIdentical(pm1.presentationModelType, pm2.presentationModelType) // not sure this is a good idea
            this.areIdentical(pm1.getAttributes().length, pm2.getAttributes().length)
            this.areIdentical(pm1.getAt('prop2').getValue(), pm2.getAt('prop2').getValue()) // a spy would be nice here
        }
    }
}