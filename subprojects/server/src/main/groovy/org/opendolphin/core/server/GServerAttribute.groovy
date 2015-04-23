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

package org.opendolphin.core.server

import org.opendolphin.core.Attribute
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag
import groovy.transform.CompileStatic
import org.opendolphin.core.comm.AttributeMetadataChangedCommand

@CompileStatic
class GServerAttribute extends BaseAttribute implements ServerAttribute {

    private boolean notifyClient = true;

    GServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    public GServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag){
        super(propertyName, baseValue, qualifier, tag)
    }

    @Override /** casting for convenience */
    ServerPresentationModel getPresentationModel() {
        (ServerPresentationModel) super.getPresentationModel()
    }

    @Override
    void setValue(Object value) {
        if (notifyClient) {
            GServerDolphin.changeValueCommand(presentationModel.getServerModelStore().currentResponse, this, value)
        }
        super.setValue(value)
    }

    @Override
    void setBaseValue(Object value) {
        super.setBaseValue(value)
        if (notifyClient) {
            presentationModel.getServerModelStore().currentResponse << new AttributeMetadataChangedCommand(attributeId: id, metadataName: Attribute.BASE_VALUE, value:value)
        }
    }

    @Override
    void setQualifier(String value) {
        super.setQualifier(value)
        if (notifyClient) {
            presentationModel.getServerModelStore().currentResponse << new AttributeMetadataChangedCommand(attributeId: id, metadataName: Attribute.QUALIFIER_PROPERTY, value:value)
        }
    }

    @Override
    void reset() {
        super.reset()
        if (notifyClient) {
            GServerDolphin.reset(presentationModel.getServerModelStore().currentResponse, this)
        }
    }

    @Override
    void rebase() {
        super.rebase()
        if (notifyClient) {
            GServerDolphin.rebaseCommand(presentationModel.getServerModelStore().currentResponse, this)
        }
        if (qualifier) { // other attributes with the same qualifier must also rebase
            for (ServerAttribute sameQualified in (List<ServerAttribute>) presentationModel.getServerModelStore().findAllAttributesByQualifier(qualifier)) {
                if (sameQualified.isDirty()) {
                    sameQualified.rebase()
                }
            }
        }
    }

    public String getOrigin(){
        return "S";
    }

    /** Do the applyChange without create commands that are sent to the client */
    public void silently(Runnable applyChange) {
        def temp = notifyClient
        notifyClient = false
        applyChange.run()
        notifyClient = temp
    }
}
