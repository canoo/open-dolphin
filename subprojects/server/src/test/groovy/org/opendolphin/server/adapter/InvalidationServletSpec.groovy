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

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class InvalidationServletSpec extends Specification {

    boolean invalidateWasReached = false

    void "with 'invalidate' in the path info, the session should be invalidated"() {
        given:
        def (servlet, req, resp) = mockServlet(url)

        when:
        servlet.doPost(req, resp)

        then:
        invalidateWasReached == outcome

        where:
        url                   | outcome
        "whatever/invalidate" | true
        "whatever/no-inval"   | false
    }


    def mockServlet(String url) {

        def servlet = new InvalidationServlet()
        def session = [
            invalidate : { -> invalidateWasReached = true }
        ] as HttpSession
        def req = [
            getSession:     { -> session },
            getRequestURL:  { -> new StringBuffer(url) }
        ] as HttpServletRequest
        def resp = [
            getWriter:   { -> new MockPrintWriter() }
        ] as HttpServletResponse
        return [ servlet, req, resp ]
    }

}