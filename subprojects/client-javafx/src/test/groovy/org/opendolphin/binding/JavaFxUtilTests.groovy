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

package org.opendolphin.binding

import javafx.collections.FXCollections
import javafx.embed.swing.JFXPanel
import javafx.event.EventType
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TableView
import org.opendolphin.core.client.ClientAttribute

import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.client.GClientAttribute
import org.opendolphin.core.client.GClientPresentationModel

class JavaFxUtilTests extends GroovyTestCase {
    static {
        new JFXPanel()
    }

    void testValue() {
        TableColumn column = new TableColumn()
        JavaFxUtil.value('attr', column)
        ClientAttribute attribute = new GClientAttribute('attr', 'content')
        assert column.cellValueFactory
        def result = column.cellValueFactory.call(new TableColumn.CellDataFeatures(null, column, [attr: attribute]))
        assert 'content' == result.value
    }

    void testCellEdit() {
        ClientPresentationModel model = new GClientPresentationModel([new GClientAttribute('attr', 0)])
        TableView view = new TableView(FXCollections.observableList([model]))
        TableColumn firstColumn = new TableColumn('firstColumn')
        view.getColumns().add(firstColumn)
        def editClosure = JavaFxUtil.cellEdit('attr', { value ->
            return "convertedValue $value"
        })
        def newValue = 'newValue'
        def result = editClosure.call(new TableColumn.CellEditEvent(view, new TablePosition(view, 0, firstColumn), new EventType<TableColumn.CellEditEvent>(), newValue))
        assert 'convertedValue newValue' == result
        assert 'convertedValue newValue' == model.getAt('attr').value
    }
}

