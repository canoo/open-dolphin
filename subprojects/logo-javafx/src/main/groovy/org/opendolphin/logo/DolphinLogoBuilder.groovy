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
package org.opendolphin.logo

import javafx.scene.Group

class DolphinLogoBuilder {
    long width = 400
    long height = 400

    DolphinLogoBuilder width(double width) {
        this.width = width
        return this
    }

    DolphinLogoBuilder height(double height) {
        this.height = height
        return this
    }

    Group build() {
        def paths = new DolphinLogoPaths(width, height).paths()

        def group = new Group()
        group.prefHeight width
        group.prefHeight height
        group.getChildren().addAll(paths)

        return group;
    }
}