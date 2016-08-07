package org.igov.service.business.action.item;

import java.io.Serializable;
import java.util.List;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 17:46
 */
public class ServiceTagTree implements Serializable {

    List<ServiceTagTreeNode> rootTagNodes;

    public ServiceTagTree(List<ServiceTagTreeNode> rootTags) {
        this.rootTagNodes = rootTags;
    }

    public List<ServiceTagTreeNode> getRootTagNodes() {
        return rootTagNodes;
    }
}
