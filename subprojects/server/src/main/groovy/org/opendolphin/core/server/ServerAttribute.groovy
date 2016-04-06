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
package org.opendolphin.core.server

import org.opendolphin.core.Attribute
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.AttributeMetadataChangedCommand

//CompileStatic
class ServerAttribute extends BaseAttribute {

    private boolean notifyClient = true;

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag){
        super(propertyName, baseValue, qualifier, tag)
    }

    @Override /** casting for convenience */
    ServerPresentationModel getPresentationModel() {
        (ServerPresentationModel) super.getPresentationModel()
    }

    @Override
    void setValue(Object newValue) {
        if (notifyClient) {
            DefaultServerDolphin.changeValueCommand(presentationModel.modelStore.currentResponse, this, newValue)
        }
        super.setValue(newValue)
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves
        forAllQualified { if (newValue != it.value) it.setValue(newValue) }
    }

    @Override
    void setBaseValue(Object value) {
        if (notifyClient) {
            presentationModel.modelStore.currentResponse << new AttributeMetadataChangedCommand(attributeId: id, metadataName: Attribute.BASE_VALUE, value:value)
        }
        super.setBaseValue(value)
        // on the server side, we have no listener on the model store to care for the distribution of
        // baseValue changes to all attributes of the same qualifier so we must care for that ourselves
        forAllQualified { if (value != it.baseValue) it.setBaseValue(value) }
    }

    protected void forAllQualified(Closure yield) {
        if (! qualifier) return
        if (! presentationModel) return // we may not know the pm, yet
        for (ServerAttribute sameQualified in (List<ServerAttribute>) presentationModel.modelStore.findAllAttributesByQualifier(qualifier)) {
            if (sameQualified.is(this)) continue
            yield sameQualified
        }
    }

    @Override
    void setQualifier(String value) {
        super.setQualifier(value)
        if (notifyClient) {
            presentationModel.modelStore.currentResponse << new AttributeMetadataChangedCommand(attributeId: id, metadataName: Attribute.QUALIFIER_PROPERTY, value:value)
        }
    }

    @Override
    void reset() {
        super.reset()
        if (notifyClient) {
            DefaultServerDolphin.resetCommand(presentationModel.modelStore.currentResponse, this)
        }
    }

    /**
     * Rebasing on the server side must set the base value to the current value as seen on the server side.
     * This will send a command to the client that instructs him to also set his base value to the exact
     * same (server-side) value.
     * NB: This is subtly different from just calling "rebase" on the client side since the attribute value
     * on the client side may have changed due to user input or value change listeners to a state that the
     * server has not yet seen.
     */
    @Override
    void rebase() { // todo dk: delete before 1.0 final
        super.rebase()
        // we are no longer sending RebaseCommand
    }

    public String getOrigin(){
        return "S";
    }

    /** Do the applyChange without creating commands that are sent to the client */
    public void silently(Runnable applyChange) {
        def temp = notifyClient
        notifyClient = false
        applyChange.run()
        notifyClient = temp
    }

    /** Do the applyChange with enforced creation of commands that are sent to the client */
    protected void verbosely(Runnable applyChange) {
        def temp = notifyClient
        notifyClient = true
        applyChange.run()
        notifyClient = temp
    }

    /**
     * Overriding the standard behavior of PCLs such that firing is enforced to be done
     * verbosely. This is safe since on the server side PCLs are never used for the control
     * of the client notification as part of the OpenDolphin infrastructure
     * (as opposed to the java client).
     * That is: all remaining PCLs are application specific and _must_ be called verbosely.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        verbosely {
            super.firePropertyChange(propertyName, oldValue, newValue)
        }
    }
}
