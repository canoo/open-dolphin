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

package org.opendolphin.core.client

import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag
import groovy.transform.CompileStatic

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */

@CompileStatic
class ClientAttribute extends BaseAttribute {

    /** @deprecated you should not create Client Attributes without initial values */
    ClientAttribute(String propertyName) {
        this(propertyName, null)
    }


    ClientAttribute(String propertyName, Object initialValue, String qualifier = null, Tag tag = Tag.VALUE) {
        super(propertyName, initialValue, qualifier, tag)
    }


    /** @deprecated too much dependent on key names and doesn't allow setting the tag */
    ClientAttribute(Map props) {
        this(props.propertyName.toString(), props.initialValue)
        this.qualifier = props.qualifier
    }

    public String getOrigin(){
        return "C";
    }
}
