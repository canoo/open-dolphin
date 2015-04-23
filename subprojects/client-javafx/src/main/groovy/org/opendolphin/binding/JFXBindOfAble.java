package org.opendolphin.binding;

import javafx.scene.Node;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.GClientAttribute;
import org.opendolphin.core.client.GClientPresentationModel;

public class JFXBindOfAble {
    public JFXBindToAble of(Node source) {
        return new JFXBindToAble(source, sourcePropertyName);
    }

    public BindToAble of(PresentationModel source) {
        return Binder.bind(sourcePropertyName, tag).of(source);
    }

    public BindClientToAble of(GClientPresentationModel source) {
        return new BindClientToAble((GClientAttribute) source.findAttributeByPropertyNameAndTag(sourcePropertyName, tag));
    }

    public BindPojoToAble of(Object source) {
        return Binder.bind(sourcePropertyName, tag).of(source);
    }

    public JFXBindOfAble(String sourcePropertyName, Tag tag) {
        this.sourcePropertyName = sourcePropertyName;
        this.tag = tag;
    }

    private final String sourcePropertyName;
    private final Tag tag;
}
