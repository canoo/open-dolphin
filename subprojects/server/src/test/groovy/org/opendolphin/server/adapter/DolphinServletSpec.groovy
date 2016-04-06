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

import org.opendolphin.core.comm.Codec
import org.opendolphin.core.server.ServerConnector
import org.opendolphin.core.server.DefaultServerDolphin
import spock.lang.Specification

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.util.logging.Level

class DolphinServletSpec extends Specification {

    void "calling doPost in new session must reach registration of custom actions - no logging"() {
        given:
        DolphinServlet.log.level = Level.OFF // for full branch coverage
        def (servlet, req, resp) = mockServlet(null)

        when:
        servlet.doPost(req, resp)

        then:
        servlet.registerReached
    }

    void "calling doPost in new session must reach registration of custom actions - all logging for branch coverage"() {
        given:
        DolphinServlet.log.level = Level.ALL // for full branch coverage
        def (servlet, req, resp) = mockServlet(null)

        when:
        servlet.doPost(req, resp)

        then:
        servlet.registerReached

    }

    void "calling doPost in existing session must not reach registration of custom actions"() {
        given:
        DolphinServlet.log.level = Level.ALL // for full branch coverage
        def (servlet, req, resp) = mockServlet(new DefaultServerDolphin(null, mockServerConnector()))

        when:
        servlet.doPost(req, resp)

        then:
        servlet.registerReached == false

    }

    def mockServerConnector() {
        [
            getCodec: { [encode: {}, decode: { [null] }] as Codec },
            receive: { [] },
            serverModelStore: { setCurrentResponse: { } }
        ] as ServerConnector
    }

    def mockServlet(DefaultServerDolphin serverDolphin) {
        DefaultServerDolphin.metaClass.getServerConnector = {-> mockServerConnector() }

        def servlet = new TestDolphinServlet()
        def session = [
            setAttribute : { key, value -> },
            getAttribute : { serverDolphin },
            getId : { null }
        ] as HttpSession
        def reader = new BufferedReader({} as Reader)
        reader.metaClass.getText = {-> ' '}
        def req = [ getSession: {session}, getReader: { reader }, setCharacterEncoding: { } ] as HttpServletRequest
        def oS = {} as ServletOutputStream
        oS.metaClass.leftShift = {}
        oS.metaClass.close = {->}
        def resp = [ getOutputStream: { oS } ] as HttpServletResponse
        return [ servlet, req, resp ]
    }

}

class TestDolphinServlet extends DolphinServlet {
    boolean registerReached

    @Override
    protected void registerApplicationActions(DefaultServerDolphin serverDolphin) {
        registerReached = true
    }

    def getLog = [info: {}]
}
