package org.opendolphin.core.client;

public class ClientPresentationModelTest extends GroovyTestCase{

    void testStandardCtor() {
        def model = new GClientPresentationModel('x',[])
        assert model.id == 'x'
    }
    void testNullIdCtor() {
        def model1 = new GClientPresentationModel([])
        def model2 = new GClientPresentationModel([])
        assert model1.id != model2.id
    }
    void testBadIdCtor() {
        shouldFail(IllegalArgumentException) {
            new GClientPresentationModel("1000-AUTO-CLT",[])
        }
    }
}
