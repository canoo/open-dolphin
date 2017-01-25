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
package org.opendolphin.binding;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConverterJavaTest {

    @Test
    public void testStringToInteger() throws Exception {

        Converter<String, Integer> converter = new Converter<String, Integer>() {
            @Override
            public Integer convert(String value) {
                return (value == null) ? null : Integer.valueOf(value);
            }
        };

        assertEquals("null", null, converter.convert(null));
        assertEquals("valid data", new Integer(25), converter.convert("25"));

        try {
            converter.convert("2.5");
            fail("invalid data, exception expected");
        } catch (Exception ignore) {
            // expected
        }
    }

    @Test
    public void testBooleanToBoolean() throws Exception {

        Converter<Boolean, Boolean> converter = new Converter<Boolean, Boolean>() {
            @Override
            public Boolean convert(Boolean value) {
                return (value == null) ? null : !value;
            }
        };

        assertEquals("null->null", null, converter.convert(null));
        assertFalse("true->false", converter.convert(true));
        assertTrue("false->true", converter.convert(false));
    }

    @Test (expected = ClassCastException.class)
    public void testInvalidDataTypes() throws Exception {

        Converter converter = new Converter<Boolean, Boolean>() {
            @Override
            public Boolean convert(Boolean value) {
                return !value;
            }
        };

        converter.convert("testString");
    }

}
