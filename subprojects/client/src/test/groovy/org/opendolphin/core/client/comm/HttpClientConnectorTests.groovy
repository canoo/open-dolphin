/*
 * Copyright 2012-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opendolphin.core.client.comm

import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicStatusLine
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.JsonCodec
import org.opendolphin.core.comm.SignalCommand

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HttpClientConnectorTests extends GroovyTestCase {

    HttpClientConnector connector

    @Override
    protected void setUp() throws Exception {
        connector = new HttpClientConnector(new ClientDolphin(), 'dummyURL')
        connector.throwExceptionOnSessionChange = false
        connector.codec = new JsonCodec()

    }

    void testCallConnector() {
        connector.httpClient = new DefaultHttpClient() {
            @Override
            def <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
                return """
                        [{"pmId":"p1","clientSideOnly":false,"id":"CreatePresentationModel","attributes":[],"pmType":null,"className":"org.opendolphin.core.comm.CreatePresentationModelCommand"}]
                        """
            }
        }
        def result = connector.transmit([new CreatePresentationModelCommand(pmId: 'p1')])
        assert 1 == result.size()
        assert result[0] instanceof CreatePresentationModelCommand
        assert 'p1' == result[0].pmId
    }

    void testSignal() {
        connector.setReleaseCommand(new SignalCommand("test signal"))
        connector.waiting = true
        CountDownLatch httpWasCalled = new CountDownLatch(1)
        connector.signalHttpClient = new DefaultHttpClient() {
            @Override
            def <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
                StatusLine statusLine = [ getStatusCode: {200}, getReasonPhrase: {"OK"}] as StatusLine
                StringEntity entity = new StringEntity("ok")
                HttpResponse response = [ getStatusLine: {statusLine}, getEntity: {entity} ] as HttpResponse
                responseHandler.handleResponse(response)
                httpWasCalled.countDown()
                return "[]"
            }
        }
        connector.release()
        httpWasCalled.await(2, TimeUnit.SECONDS)
        assert 0 == httpWasCalled.count
    }

    void testSignalWithBadReturnCodeMustThrowException() {
        connector.setReleaseCommand(new SignalCommand("test signal"))
        connector.waiting = true
        CountDownLatch httpWasCalled = new CountDownLatch(1)
        connector.signalHttpClient = new DefaultHttpClient() {
            @Override
            def <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
                StatusLine statusLine = [ getStatusCode: {500}, getReasonPhrase: {"Internal Server Error"}] as StatusLine
                StringEntity entity = new StringEntity("failed")
                HttpResponse response = [ getStatusLine: {statusLine}, getEntity: {entity} ] as HttpResponse
                try {
                    responseHandler.handleResponse(response)
                } catch (e) {
                    httpWasCalled.countDown()
                }
                return "[]"
            }
        }
        connector.release()
        httpWasCalled.await(2, TimeUnit.SECONDS)
        assert 0 == httpWasCalled.count
    }


    void testCallWithException() {
        try {
            connector.transmit([new Command()])
            fail()
        } catch (Exception e) {
            // ignore
        }

    }
}
