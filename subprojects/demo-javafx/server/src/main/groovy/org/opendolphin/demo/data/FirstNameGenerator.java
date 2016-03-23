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
package org.opendolphin.demo.data;

public class FirstNameGenerator extends AbstractValueGenerator<String> {

    private static final String[] FIRST_PREFIX = {
            "Al", "An", "Ben", "Bo", "Chris", "Det", "Di", "El", "Hei", "How",
            "Isa", "Mo", "O", "Re", "Sam", "Su", "Tor", "U", "Wen", "Y", "Zo"
    };

    private static final String[] FIRST_SUFFIX = {
            "ard", "bel", "di", "dy", "dres", "erk", "et", "ja", "ke", "lef", "mo",
            "na", "ni", "san", "son", "sten", "ti", "ton", "toph", "ris", "wa", "we"
    };

    private static final String[] SECOND_PREFIX = {
            "An", "Ben", "Bo", "De", "Di", "Ed", "El", "Fred", "Mo", "Sam", "Su"
    };

    private static final String[] SECOND_SUFFIX = {
            "a", "e", "er", "gar", "ke", "la", "o", "on", "ra", "te", "us"
    };

    private static final String[] INITIALS = {
            "A.", "B.", "C.", "D.", "E.", "F.", "G.", "H.", "I.", "J.", "K.", "L.", "M.",
            "N.", "O.", "P.", "Q.", "R.", "S.", "T.", "U.", "V.", "W.", "X.", "Y.", "Z."
    };

    public String randomValue() {
        StringBuilder builder = new StringBuilder();
        boolean useFirstPrefix = getRandomBoolean(0.8);
        if (useFirstPrefix) {
            builder.append(getRandomString(FIRST_PREFIX));
        }
        boolean useFirstSuffix = useFirstPrefix && getRandomBoolean(0.5);
        if (useFirstSuffix) {
            builder.append(getRandomString(FIRST_SUFFIX));
        }
        boolean useSecondPrefix = getRandomBoolean(0.4);
        if (useSecondPrefix) {
            String secondPrefix = getRandomString(SECOND_PREFIX);
            if (useFirstPrefix) {
                secondPrefix = secondPrefix.toLowerCase();
            }
            builder.append(secondPrefix);
        }
        boolean prefixUsed = useFirstPrefix || useSecondPrefix;
        boolean useSecondSuffix = prefixUsed && getRandomBoolean(0.5);
        if (useSecondSuffix) {
            builder.append(getRandomString(SECOND_SUFFIX));
        }
        boolean useInitials = !prefixUsed || getRandomBoolean(0.2);
        if (useInitials) {
            if (prefixUsed) {
                builder.append(" ");
            }
            builder.append(getRandomString(INITIALS));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        FirstNameGenerator generator = new FirstNameGenerator();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(generator.randomValue() + ", ");
            }
            System.out.println();
        }
        System.out.println("FirstName: " + FIRST_PREFIX.length * SECOND_PREFIX.length * FIRST_SUFFIX.length * SECOND_SUFFIX.length * INITIALS.length + " variations possible");
    }

}
