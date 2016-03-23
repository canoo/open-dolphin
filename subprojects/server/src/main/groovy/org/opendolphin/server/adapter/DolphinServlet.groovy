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
package org.opendolphin.server.adapter

import groovy.util.logging.Log
import org.opendolphin.core.comm.Codec
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin
import org.opendolphin.core.server.ServerConnector
import org.opendolphin.core.server.ServerModelStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse
import java.nio.charset.Charset

@Log
abstract class DolphinServlet extends HttpServlet {
    private static String DOLPHIN_ATTRIBUTE_ID = DolphinServlet.class.name

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(getCharset())
        def dolphin = checkDolphinInSession(req)
        def requestJson = req.reader.text
        log.finest "received json: $requestJson"
        def commands = dolphin.serverConnector.codec.decode(requestJson)
        def results = new LinkedList()
        for (command in commands) { // a subclass could override this for less defensive exception handling
            log.finest "processing $command"
            results.addAll dolphin.serverConnector.receive(command)
        }
        def jsonResponse = dolphin.serverConnector.codec.encode(results)
        log.finest "sending json response: $jsonResponse"
        resp.outputStream << jsonResponse
        resp.outputStream.close()
    }

    private DefaultServerDolphin checkDolphinInSession(HttpServletRequest request) {
        def session = request.session
        DefaultServerDolphin dolphin = (DefaultServerDolphin) session.getAttribute(DOLPHIN_ATTRIBUTE_ID)
        if (!dolphin) {
            log.info "creating new dolphin for session $session.id"
            def modelStore = new ServerModelStore()
            dolphin = new DefaultServerDolphin(modelStore, new ServerConnector(codec: codec, serverModelStore: modelStore))
            dolphin.registerDefaultActions()
            registerApplicationActions(dolphin)
            session.setAttribute(DOLPHIN_ATTRIBUTE_ID, dolphin)
        }
        return dolphin
    }

    protected Codec getCodec() {
        new JsonCodec()
    }

    protected String getCharset() {
        "UTF-8"
    }

    protected abstract void registerApplicationActions(DefaultServerDolphin serverDolphin)
}
