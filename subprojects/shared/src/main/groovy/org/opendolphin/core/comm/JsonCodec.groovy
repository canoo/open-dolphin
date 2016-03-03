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

package org.opendolphin.core.comm

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag

import java.text.SimpleDateFormat

/**
 *
 * OpenDolphin JSON encoding:
 *   type encoding is language agnostic (=> no java.lang.BigDecimal types in the encoded JSON)
 *
 * OpenDolphin number encoding:
 *
 *   we encode floating-point vales into whatever BigDecimal can handle. JsonBuilder will emit
 * floating/integer numbers even bigger that a "long" or "double" can handle into a JSON number, which can grow very large and is
 * not limited in size. The receiving party has to cope with anything that is bigger than what common floats or doubles (usually 64bit) are.
 * As an example JavaScript numbers are always stored as double precision floating point numbers, following the international IEEE 754 standard.
 *
 * - supported types are: date, boolean, integer and floating type numbers
 * - encoding should not introduce conversion artifacts
 *   - a float like 0.1f, should not end up as a double like 0.10000000149011612 => Double.valueOf(0.1f) => 0.10000000149011612
 *
 * on the JVM side we use BigDecimal and JsonBuilder will convert it to something
 * that is free of artifacts binary encoded floats introduce.
 *
 * for the test testProperTypeEnAndDecoding in JsonCodecTest:
 * assertCorrectEnAndDecoding(new BigDecimal("0.100000001490116123456"), new BigDecimal("0.10000000149011612"))
 *
 * the encoding through JsonBuilder generates:
 * [{"newValue":0.10000000149011612,"oldValue":0.100000001490116123456,"id":"ValueChanged","attributeId":"bla","className":"org.opendolphin.core.comm.ValueChangedCommand"}]
 *
 * the fun of binary encoded floats:
 *
 *  Double.valueOf(0.1f) => 0.10000000149011612
 *  new BigDecimal("0.100000001490116123456").doubleValue() => 0.10000000149011612
 *  new BigDecimal("0.100000001490116123456").toString()    => 0.100000001490116123456
 *  Float.valueOf(0.1f).doubleValue() => 0.10000000149011612
 *
 */

@Log
class JsonCodec implements Codec {

    public static final String OD_DATE = "urn:org:opendolphin:type:date"
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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
            map[OD_DATE] = new SimpleDateFormat(ISO8601_FORMAT).format(result)
            result = map
        } else if (result instanceof Float) {
            result = new BigDecimal(Float.toString(result))
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
                else if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap in value) {
                        entryMap.each { entryKey, entryValue ->
                            entryMap[entryKey] = decodeBaseValue(entryValue)
                        }
                    }
                } else value = decodeBaseValue(value)
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
            if (encodedValue.containsKey(OD_DATE)) {
                result = new SimpleDateFormat(ISO8601_FORMAT).parse(encodedValue[OD_DATE]);
            }
        }
        return result;
    }
}
