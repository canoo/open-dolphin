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
package org.opendolphin.demo

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel

import static groovyx.javafx.GroovyFX.start

/**
 * The simplest demo showing how to model a tree recursively as a list of presentation models,
 * and how to bind the tree to a TreeView.
 * <p/>
 * Note: see startBindTreeDemo.groovy where the PULL_TREE command is defined.
 * Also see PullTreeActionHandler (makePM method) to see how the tree data is recursively encoded
 * using DTO's and Slots.
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

            dolphin.send "PULL_TREE", { List<ClientPresentationModel> pms ->
                def root = pms.find { it.parent.value == null }
                tree.root = filledTreeItem(pms, root)
                tree.root.expanded = true
                tree.showRoot = true
            }

            primaryStage.show()
        }
    }

    /**
     *
     * @param allNodeModels  list of all node models in the tree.
     * @param current        presentation model corresponding to the current node
     * @return
     */
    static TreeItem filledTreeItem(allNodeModels, current) {
        // create one TreeItem for the current node.
        def result = new TreeItem<String>(current.id)
        // scan all nodes in the tree looking for nodes whose parent is the current node;
        // such nodes are children of the current node, so recursively add their children to them.
        allNodeModels.findAll { it.parent.value == current.id }.each {
            result.children << filledTreeItem(allNodeModels, it)
        }
        return result
    }
}
