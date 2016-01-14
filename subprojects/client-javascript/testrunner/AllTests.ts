/// <reference path="tsUnit.ts"/>
/// <reference path="../test/dolphin/ClientAttributeTests.ts"/>
/// <reference path="../test/dolphin/ClientPresentationModelTests.ts"/>
/// <reference path="../test/dolphin/NamedCommandTests.ts"/>
/// <reference path="../test/dolphin/ValueChangedCommandTests.ts"/>
/// <reference path="../test/dolphin/ChangeAttributeMetadataCommandTests.ts"/>
/// <reference path="../test/dolphin/EmptyNotificationTests.ts"/>
/// <reference path="../test/dolphin/CreatePresentationModelCommandTests.ts"/>
/// <reference path="../test/dolphin/ClientDolphinTests.ts"/>
/// <reference path="../test/dolphin/ClientConnectorTests.ts"/>
/// <reference path="../test/dolphin/CommandBatcherTests.ts"/>
/// <reference path="../test/dolphin/MapTests.ts"/>
/// <reference path="../test/dolphin/ClientModelStoreTests.ts"/>
/// <reference path="../test/dolphin/CodecTest.ts"/>
/// <reference path="../test/dolphin/DolphinBuilderTest.ts"/>

module allTests {
    export function testAll() {
        var test = new tsUnit.Test();

        // add your test class (you can call this multiple times)
        test.addTestClass(new opendolphin.ClientAttributeTests(), "ClientAttributeTests");
        test.addTestClass(new opendolphin.ClientPresentationModelTests(), "ClientPresentationModelTests");
        test.addTestClass(new opendolphin.NamedCommandTests(), "NamedCommandTests");
        test.addTestClass(new opendolphin.ValueChangedCommandTests(), "ValueChangedCommandTests");
        test.addTestClass(new opendolphin.ChangeAttributeMetadataCommandTests(), "ChangeAttributeMetadataCommandTests");
        test.addTestClass(new opendolphin.EmptyNotificationTests(), "EmptyNotificationTests");
        test.addTestClass(new opendolphin.CreatePresentationModelCommandTests(), "CreatePresentationModelCommandTests");
        test.addTestClass(new opendolphin.ClientDolphinTests(), "ClientDolphinTests");
        test.addTestClass(new opendolphin.ClientConnectorTests(), "ClientConnectorTests");
        test.addTestClass(new opendolphin.CommandBatcherTests(), "CommandBatcherTests");
        test.addTestClass(new opendolphin.MapTests(), "MapTests");
        test.addTestClass(new opendolphin.ClientModelStoreTests(), "ClientModelStoreTests");
        test.addTestClass(new opendolphin.CodecTest(), "CodecTest");
        test.addTestClass(new opendolphin.DolphinBuilderTest(), "DolphinBuilder");

        return test.run();
    }
}

