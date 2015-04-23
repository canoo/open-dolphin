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

package org.opendolphin.demo

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.GClientPresentationModel

import static groovyx.javafx.GroovyFX.start

/**
 * The simplemost demo how to have a tree modeled as presentation models
 * and binding it to a treeview.
 */

class BindTreeView {

    static show(ClientDolphin dolphin) {

        start { app ->
            TreeView tree
            stage {
                scene width: 300, height: 300, {
                    tree = treeView()
                }
            }

            dolphin.send "PULL_TREE", { List<GClientPresentationModel> pms ->
                def root = pms.find { it.parent.value == null }
                tree.root = filledTreeItem(pms, root)
                tree.root.expanded = true
                tree.showRoot = true
            }

            primaryStage.show()
        }
    }

    static TreeItem filledTreeItem(allNodeModels, current) {
        def result = new TreeItem<String>(current.id)
        allNodeModels.findAll { it.parent.value == current.id }.each {
            result.children << filledTreeItem(allNodeModels, it)
        }
        return result
    }
}
