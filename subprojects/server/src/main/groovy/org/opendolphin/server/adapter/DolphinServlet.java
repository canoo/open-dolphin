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
package org.opendolphin.server.adapter;

import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.core.server.ServerModelStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles incoming Dolphin requests.
 */
public abstract class DolphinServlet extends HttpServlet {
    private static String SERVER_DOLPHIN_ATTRIBUTE_ID = DolphinServlet.class.getName();
    private static final Logger LOG = Logger.getLogger(DolphinServlet.class.getName());

    // TODO: should this method be final?
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        preProcessRequest(request);

        DefaultServerDolphin serverDolphin = resolveServerDolphin(request);

        String input = readInput(request);
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("received json: " + input);
        }

        List<Command> commands = decodeInput(serverDolphin.getServerConnector().getCodec(), input);

        List<Command> results = handleCommands(serverDolphin.getServerConnector(), commands);

        String output = encodeOutput(serverDolphin.getServerConnector().getCodec(), results);

        writeHeaders(request, response, results);

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("sending json response: " + output);
        }
        writeOutput(response, output);

        postProcessResponse(response);
    }

    /**
     * Prepares the {@code request} before reading from it.
     *
     * @param request - an HttpServletRequest object that contains the request the client has made of the servlet
     *
     * @throws IOException      - if an input or output error is detected when the servlet handles the request
     * @throws ServletException - if the request for the POST could not be handled
     */
    protected void preProcessRequest(HttpServletRequest request) throws ServletException, IOException {
        request.setCharacterEncoding(getCharset());
    }

    /**
     * Updates {@code response} headers if needed.
     *
     * @param request  - an HttpServletRequest object that contains the request the client has made of the servlet
     * @param response -  an HttpServletResponse object that contains the response the servlet sends to the client
     * @param results  - the list of commands to be sent to the client after the response was processed
     *
     * @throws IOException      - if an input or output error is detected when the servlet handles the request
     * @throws ServletException - if the request for the POST could not be handled
     */
    protected void writeHeaders(HttpServletRequest request, HttpServletResponse response, List<Command> results) throws ServletException, IOException {
        // empty on purpose
    }

    /**
     * Updates the {@code response} after writing to it.
     *
     * @param response -  an HttpServletResponse object that contains the response the servlet sends to the client
     *
     * @throws IOException      - if an input or output error is detected when the servlet handles the request
     * @throws ServletException - if the request for the POST could not be handled
     */
    protected void postProcessResponse(HttpServletResponse response) throws ServletException, IOException {
        // empty on purpose
    }

    /**
     * Resolves the {@code DefaultServerDolphin} to be used. An instance of {@code DefaultServerDolphin}
     * should be available inside the HttpSession under the key {@code .DolphinServletSERVER_DOLPHIN_ATTRIBUTE_ID}.
     * A new instance will be created if such value cannot be found.
     *
     * @param request - an HttpServletRequest object that contains the request the client has made of the servlet
     *
     * @return an instance of {@code DefaultServerDolphin} either found in the HTTP session or newly created.
     */
    protected DefaultServerDolphin resolveServerDolphin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        DefaultServerDolphin serverDolphin = (DefaultServerDolphin) session.getAttribute(SERVER_DOLPHIN_ATTRIBUTE_ID);

        if (serverDolphin == null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("creating new dolphin for session " + session.getId());
            }
            serverDolphin = createServerDolphin();
            configureServerDolphin(serverDolphin);
            session.setAttribute(SERVER_DOLPHIN_ATTRIBUTE_ID, serverDolphin);
        }

        return serverDolphin;
    }

    /**
     * Creates a new instance of {@code DefaultServerDolphin}.
     * Subclasses may override this method to customize how this instance should be created.
     *
     * @return a newly created {@code DefaultServerDolphin}.
     */
    protected DefaultServerDolphin createServerDolphin() {
        ServerModelStore modelStore = createServerModelStore();
        ServerConnector connector = createServerConnector(modelStore, createCodec());
        return new DefaultServerDolphin(modelStore, connector);
    }

    /**
     * Configures a freshly created {@code DefaultServerDolphin}. Typically default actions
     * and application actions are added in this step. This method is called only once per instance.
     *
     * @param serverDolphin - the {@code DefaultServerDolphin} to be configured
     */
    protected void configureServerDolphin(DefaultServerDolphin serverDolphin) {
        serverDolphin.registerDefaultActions();
        registerApplicationActions(serverDolphin);
    }

    /**
     * Registers application specific actions on {@code DefaultServerDolphin}.
     * This method is called only once per instance.
     *
     * @param serverDolphin - the {@code DefaultServerDolphin} to be configured
     */
    protected abstract void registerApplicationActions(DefaultServerDolphin serverDolphin);

    /**
     * Reads the body of the {@code response}.
     *
     * @param request - an HttpServletRequest object that contains the request the client has made of the servlet
     *
     * @return the contents of the {@code response}
     *
     * @throws IOException      - if an input or output error is detected when the servlet handles the request
     * @throws ServletException - if the request for the POST could not be handled
     */
    protected String readInput(HttpServletRequest request) throws ServletException, IOException {
        StringBuilder input = new StringBuilder();
        String line = null;
        while ((line = request.getReader().readLine()) != null) {
            input.append(line).append("\n");
        }
        return input.toString();
    }

    /**
     * Decodes the given input.
     *
     * @param codec - the {@code Codec} used to decode {@code input}
     * @param input - the incoming encoded commands
     *
     * @return a collection of decoded commands
     */
    protected List<Command> decodeInput(Codec codec, String input) {
        return codec.decode(input);
    }

    /**
     * Processes incoming commands and creates outgoing commands.
     *
     * @param serverConnector - the {@code ServerConnector} required to han dle each command
     * @param commands        - the collection of incoming commands to be handled
     *
     * @return - a collection of outgoing commands
     */
    protected List<Command> handleCommands(ServerConnector serverConnector, List<Command> commands) {
        List<Command> results = new ArrayList<Command>();
        for (Command command : commands) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("processing " + command);
            }
            // TODO: add a catch-all exception handler here ?
            results.addAll(serverConnector.receive(command));
        }
        return results;
    }

    /**
     * Encodes the given output.
     *
     * @param codec   - the {@code Codec} used to endecode {@code output}
     * @param results - the outgoing collection of commands to be encoded.
     *
     * @return a literal representation of the encoded commands
     */
    protected String encodeOutput(Codec codec, List<Command> results) {
        return codec.encode(results);
    }

    /**
     * Writes the encoded outgoing commands to the {@code response}.
     *
     * @param response -  an HttpServletResponse object that contains the response the servlet sends to the client
     * @param output   - the encoded collection of commands to be sent back to the client
     *
     * @throws IOException      - if an input or output error is detected when the servlet handles the request
     * @throws ServletException - if the request for the POST could not be handled
     */
    protected void writeOutput(HttpServletResponse response, String output) throws ServletException, IOException {
        response.getWriter().print(output);
        response.getWriter().flush();
    }

    /**
     * Defines the CharSet to be used in the {@code request}.
     * Default values is {@code UTF-8}.
     *
     * @return the CharSet to be used in the request.
     */
    protected String getCharset() {
        return "UTF-8";
    }

    /**
     * Creates a new {@code Codec}.
     * Subclasses may override this method to customize how this instance should be created.
     *
     * @return a freshly created {@code Codec}.
     */
    protected Codec createCodec() {
        return new JsonCodec();
    }

    /**
     * Creates a new {@code ServerModelStore}.
     * Subclasses may override this method to customize how this instance should be created.
     *
     * @return a freshly created {@code ServerModelStore}.
     */
    protected ServerModelStore createServerModelStore() {
        return new ServerModelStore();
    }

    /**
     * Creates a new {@code ServerConnector}.
     * Subclasses may override this method to customize how this instance should be created.
     *
     * @param modelStore - the {@code ServerModelStore} to be attached to the {@code ServerConnector}.
     * @param codec      - the {@code Codec} to be attached to the {@code ServerConnector}.
     *
     * @return a freshly created {@code ServerConnector}.
     */
    protected ServerConnector createServerConnector(ServerModelStore modelStore, Codec codec) {
        ServerConnector connector = new ServerConnector();
        connector.setServerModelStore(modelStore);
        connector.setCodec(codec);
        return connector;
    }

    /**
     * Returns the {@code Codec} to use on a newly created {@code ServerConnector}.
     *
     * @return a freshly created {@code Codec}.
     *
     * @deprecated use {@code createCodec} instead.
     */
    @Deprecated
    protected Codec getCodec() {
        return createCodec();
    }

    public static Logger getLog() {
        return LOG;
    }
}