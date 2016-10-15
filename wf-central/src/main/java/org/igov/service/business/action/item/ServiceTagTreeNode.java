package org.igov.service.business.action.item;

import org.igov.model.action.item.ServiceTag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 16:46
 */
public class ServiceTagTreeNode implements Serializable {

    private ServiceTag tag;
    private List<ServiceTagTreeNode> children = new ArrayList<>();

    public ServiceTagTreeNode(ServiceTag tag) {
        this.tag = tag;
    }

    public ServiceTag getTag() {
        return tag;
    }

    public void setTag(ServiceTag tag) {
        this.tag = tag;
    }

    public List<ServiceTagTreeNode> getChildren() {
        return children;
    }

    public void addChild(ServiceTagTreeNode childNode) {
        children.add(childNode);
    }
}
