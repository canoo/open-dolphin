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

public class CityNameGenerator extends AbstractValueGenerator<String> {

    private static final String[] PREFIXES = {
            "North", "South", "West", "East", "Upper", "Lower", "Middle", "New", "Old", "Free", "Little"
    };

    private static final String[] BASE_NAMES = {
            "Ab", "Al", "Ash", "Ax", "Bark", "Barn", "Bed", "Bell", "Bright", "Bu",
            "Castle", "Co", "Dam", "Dol", "Ed", "Elm", "Ever", "Farm", "Field", "Fish", "Ful",
            "Goth", "Hamp", "Hill", "Ice", "Is", "Ken", "Knight", "Lo", "Lon", "Min", "Nor",
            "Or", "Os", "Pel", "Prince", "Red", "River", "Rich", "Rose", "Sea", "Stone", "Swamp",
            "Tall", "Tin", "Tri", "Up", "Val", "Van", "Well", "Wind", "York", "Zip", "Zo"
    };

    private static final String[] FILLERS = {
            "ing", "sing", "er", "y", "ham"
    };

    private static final String[] SUFFIXES = {
            "ford", "town", "ton", "dale", "ster", "borough", "burg", "shire", "port", "bridge", "mont", "ley", "bury", "by"
    };

    public String randomValue() {
        StringBuilder builder = new StringBuilder();
        boolean usePrefix = getRandomBoolean(0.5);
        if (usePrefix) {
            builder.append(getRandomString(PREFIXES));
        }
        boolean useBase = !usePrefix || getRandomBoolean(0.5);
        if (useBase) {
            boolean useSeparator = usePrefix && getRandomBoolean(0.5);
            if (useSeparator) {
                builder.append(" ");
            }
            String baseName = getRandomString(BASE_NAMES);
            if (usePrefix && !useSeparator) {
                baseName = baseName.toLowerCase();
            }
            builder.append(baseName);
        }
        boolean useFiller = getRandomBoolean(0.3);
        if (useFiller) {
            builder.append(getRandomString(FILLERS));
        }
        boolean useSuffix = !useFiller || getRandomBoolean(0.5);
        if (useSuffix) {
            builder.append(getRandomString(SUFFIXES));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        CityNameGenerator generator = new CityNameGenerator();
        for (int i = 1000; i < 2000; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(i + " " + generator.randomValue() + ", ");
            }
            System.out.println();
        }
        System.out.println("CityName: " + PREFIXES.length * BASE_NAMES.length * FILLERS.length * SUFFIXES.length + " variations possible");
    }

}
