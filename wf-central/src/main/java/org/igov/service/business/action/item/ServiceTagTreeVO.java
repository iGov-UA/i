package org.igov.service.business.action.item;

import org.igov.model.action.item.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 16:38
 */
public class ServiceTagTreeVO {

    private List<ServiceTagTreeNodeVO> aNode = new ArrayList<>();
    private List<Service> aService;

    public List<ServiceTagTreeNodeVO> getaNode() {
        return aNode;
    }
    public void setaNode(List<ServiceTagTreeNodeVO> aNode) {
        this.aNode = aNode;
    }

    public List<Service> getaService() {
        return aService;
    }
    public void setaService(List<Service> aService) {
        this.aService = aService;
    }
}
