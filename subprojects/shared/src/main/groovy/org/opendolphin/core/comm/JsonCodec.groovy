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
package org.opendolphin.core.comm

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag

import java.text.SimpleDateFormat

@Log
class JsonCodec implements Codec {


    public static final String DATE_TYPE_KEY = Date.toString();
    public static final String BIGDECIMAL_TYPE_KEY = BigDecimal.toString();
    public static final String FLOAT_TYPE_KEY = Float.toString();
    public static final String DOUBLE_TYPE_KEY = Double.toString();
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    @Override
    String encode(List<Command> commands) {
        def content = commands.collect { Command cmd ->
            log.finest "encoding command $cmd"
            def entry = cmd.properties
            ['class', 'metaClass'].each { entry.remove it }
            entry.className = cmd.class.name
            entry.each { key, value ->              // prepare against invalid entries
                if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap in value) {
                        entryMap.each { entryKey, entryValue ->
                            entryMap[entryKey] = encodeBaseValue(entryValue)
                        }
                    }
                } else if (value instanceof Map) {  // DataCommand has map content
                    value.each { entryKey, entryValue ->
                        value[entryKey] = encodeBaseValue(entryValue)
                    }
                } else {
                    entry[key] = encodeBaseValue(value)
                }
            }
            entry
        }
        JsonBuilder builder = new JsonBuilder(content)
        builder.toString()
    }

    protected Object encodeBaseValue(entryValue) {
        def result = BaseAttribute.checkValue(entryValue);
        if (result instanceof Date) {
            def map = [:];
            map[DATE_TYPE_KEY] = new SimpleDateFormat(ISO8601_FORMAT).format(result)
            result = map
        } else if (result instanceof BigDecimal) {
            def map = [:];
            map[BIGDECIMAL_TYPE_KEY] = result.toString();
            result = map
        } else if (result instanceof Float) {
            def map = [:];
            map[FLOAT_TYPE_KEY] = Float.toString(result);
            result = map
        } else if (result instanceof Double) {
            def map = [:];
            map[DOUBLE_TYPE_KEY] = Double.toString(result);
            result = map
        }
        return result
    }

    @Override
    List<Command> decode(String transmitted) {
        def result = new LinkedList()
        def got = new JsonSlurper().parseText(transmitted)

        def validPackagePrefix = Command.class.getPackage().getName()

        got.findAll { cmd ->
            cmd['className'] != null && String.class.cast(cmd['className']).startsWith(validPackagePrefix)
        }.each { cmd ->
            Command responseCommand = Class.forName(cmd['className']).newInstance()
            cmd.each { key, value ->
                if (key == 'className') return
                if (key == 'id') { // id is only set for NamedCommand and SignalCommand others are dynamic
                    if (responseCommand in NamedCommand || responseCommand instanceof SignalCommand) {
                        responseCommand.id = value
                    }
                    return
                }
                if (key == 'tag') value = Tag.tagFor[value]
                else
                if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap in value) {
                        entryMap.each { entryKey, entryValue ->
                            entryMap[entryKey] = decodeBaseValue(entryValue)
                        }
                    }
                }
                else value = decodeBaseValue(value)
                responseCommand[key] = value
            }
            log.finest "decoded command $responseCommand"
            result << responseCommand
        }
        return result
    }

    Object decodeBaseValue(Object encodedValue) {
        Object result = encodedValue;
        if (encodedValue instanceof Map && encodedValue.size() == 1) {
            if (encodedValue.containsKey(DATE_TYPE_KEY)) {
                result = new SimpleDateFormat(ISO8601_FORMAT).parse(encodedValue[DATE_TYPE_KEY]);
            } else
            if (encodedValue.containsKey(BIGDECIMAL_TYPE_KEY)) {
                result = new BigDecimal(encodedValue[BIGDECIMAL_TYPE_KEY]);
            } else
            if (encodedValue.containsKey(FLOAT_TYPE_KEY)) {
                result = Float.parseFloat(encodedValue[FLOAT_TYPE_KEY]);
            } else
            if (encodedValue.containsKey(DOUBLE_TYPE_KEY)) {
                result = Double.parseDouble(encodedValue[DOUBLE_TYPE_KEY]);
            }
        }
        return result;
    }
}
