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

package org.opendolphin

/** Keep logging details in one place **/

class LogConfig {

    private static final Logger ROOT_LOGGER = Logger.getLogger("")
    private static final Logger DOLPHIN_LOGGER = Logger.getLogger("org.opendolphin")

    static noLogs() {
        logOnLevel(DOLPHIN_LOGGER, Level.OFF)
    }

    static logCommunication() {
        logOnLevel(DOLPHIN_LOGGER, Level.INFO)
    }

    static logOnLevel(Level level) {
        logOnLevel(DOLPHIN_LOGGER, level)
    }

    /**
     * One may choose to use DOLPHIN_LOGGER or ROOT_LOGGER.
     */
    static logOnLevel(Logger logger, Level level) {
        logger.level = level
        logger.handlers.each { it.setLevel(level) }
        logger.handlers.grep(ConsoleHandler).each { it.formatter = new ShortFormatter() }
    }
}

import java.util.logging.*

class ShortFormatter extends SimpleFormatter {
    synchronized String format(LogRecord record) {
        "[$record.level] $record.message\n"
    }
}