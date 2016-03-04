/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

import java.util.logging.*

/**
 * Helper class that can be used to configure the logging of the Open Dolphin library
 */
class LogConfig {

    /**
     * Root logger of Open Dolphin
     */
    private static final Logger OPEN_DOLPHIN_ROOT_LOGGER = Logger.getLogger("org.opendolphin")

    /**
     * Defines OFF as logging {@link Level} for all Open Dolphin classes
     */
    static noLogs() {
        logOnLevel(OPEN_DOLPHIN_ROOT_LOGGER, Level.OFF)
    }

    /**
     * Defines INFO as logging {@link Level} for all Open Dolphin classes
     */
    static logCommunication() {
        logOnLevel(OPEN_DOLPHIN_ROOT_LOGGER, Level.INFO)
    }

    /**
     * Defines the given {@link Level} as logging {@link Level} for all Open Dolphin classes
     */
    static logOnLevel(Level level) {
        logOnLevel(OPEN_DOLPHIN_ROOT_LOGGER, level)
    }

    /**
     * Helper method to define the given {@link Level} for the given {@link Logger}. In addition a
     * short logging {@link Formatter} is defined.
     */
    static logOnLevel(Logger logger, Level level) {
        logger.level = level
        logger.handlers.each { it.setLevel(level) }
        logger.handlers.grep(ConsoleHandler).each { it.formatter = new ShortFormatter() }
    }
}

/**
 * A {@link Formatter} that formats a logging message in a minimized format. The output contains the level and the message
 */
class ShortFormatter extends SimpleFormatter {
    synchronized String format(LogRecord record) {
        "[$record.level] $record.message\n"
    }
}