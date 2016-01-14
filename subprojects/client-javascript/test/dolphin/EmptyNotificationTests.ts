/// <reference path="../../testrunner/tsUnit.ts"/>
/// <reference path="../../js/dolphin/EmptyNotification.ts"/>


module opendolphin {
    export class EmptyNotificationTests extends tsUnit.TestClass {

        createEmptyNotificationWithGivenParameter(){
            var emptyNotification = new EmptyNotification();
            this.areIdentical(emptyNotification.id,"Empty");
            this.areIdentical(emptyNotification.className,"org.opendolphin.core.comm.EmptyNotification");
        }

    }
}