/*
 * Copyright 2012-2017 Canoo Engineering AG.
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

import groovy.util.logging.Log
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpResponseException
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.FileEntity
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.comm.Command

@Log
class HttpClientConnector extends ClientConnector {

    String servletUrl = "http://localhost:8080/dolphin-grails/dolphin/"
    String charset = "UTF-8"

    private DefaultHttpClient httpClient = new DefaultHttpClient()

    /** A second channel for the sole purpose of sending SignalCommands */
    private DefaultHttpClient signalHttpClient = new DefaultHttpClient()

    private SessionAffinityCheckingResponseHandler responseHandler = null
    private SimpleResponseHandler signalResponseHandler = null

    HttpClientConnector(ClientDolphin clientDolphin, String servletUrl) {
        this(clientDolphin, null, servletUrl)
    }

    HttpClientConnector(ClientDolphin clientDolphin, ICommandBatcher commandBatcher, String servletUrl) {
        super(clientDolphin, commandBatcher)
        this.servletUrl = servletUrl
        this.responseHandler = new SessionAffinityCheckingResponseHandler()
        this.signalResponseHandler = new SimpleResponseHandler()
    }

    void setThrowExceptionOnSessionChange(boolean throwExceptionOnSessionChange) {
        this.responseHandler.throwExceptionOnSessionChange = throwExceptionOnSessionChange
    }

    List<Command> transmit(List<Command> commands) {
        def result
        try {
            def url = "$servletUrl"

            def content = codec.encode(commands)

            HttpPost httpPost = new HttpPost(url)
            StringEntity entity = new StringEntity(content, charset)
            httpPost.setEntity(entity)

            String response

            if (commands.size() == 1 && commands.first() == releaseCommand) { // todo dk: ok, this is not nice...
                signalHttpClient.execute(httpPost, signalResponseHandler)
            } else {
                response = httpClient.execute(httpPost, responseHandler)

                def cookieStore = httpClient.cookieStore
                if( cookieStore ) {
                    signalHttpClient.cookieStore = cookieStore;
                }

                log.finest response
                result = codec.decode(response)
            }
        }
        catch (ex) {
            log.severe("cannot transmit")
            ex.printStackTrace()
            throw ex
        }
        return result
    }

    String uploadFile(File file, URI handler) {
        def result
        try {
            HttpPost httpPost = new HttpPost(handler)
            httpPost.entity = new FileEntity(file, charset)

            result = httpClient.execute(httpPost, responseHandler)
            log.finest result
        }
        catch (ex) {
            log.severe("cannot transmit")
            ex.printStackTrace()
            throw ex
        }
        return result
    }
}

@Log
class SessionAffinityCheckingResponseHandler implements ResponseHandler<String> {

    boolean throwExceptionOnSessionChange = true

    private String lastSessionId = null

    @Override
    String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        String result = entity == null ? null : EntityUtils.toString(entity);

        String sessionID = response?.getFirstHeader("Set-Cookie")?.value
        if (! sessionID) return result
        if (null == lastSessionId) {
            lastSessionId = sessionID
        } else {
            String msg;
            if (sessionID != lastSessionId) {
                msg = "Http session must not change but did. Old: $lastSessionId, new: $sessionID.\nFull response: $response"
                log.severe msg
                if (throwExceptionOnSessionChange) {
                    throw new IOException(msg)
                }
            }
        }
        return result
    }
}
@Log
class SimpleResponseHandler implements ResponseHandler<String> {
    @Override
    String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        String result = entity == null ? null : EntityUtils.toString(entity);
        return result
    }
}