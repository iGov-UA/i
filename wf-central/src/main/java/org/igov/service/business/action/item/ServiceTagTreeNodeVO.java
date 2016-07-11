package org.igov.service.business.action.item;

import org.igov.model.action.item.Service;
import org.igov.model.action.item.ServiceTag;

import java.util.ArrayList;
import java.util.List;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 16:38
 */
public class ServiceTagTreeNodeVO {

    private ServiceTag oServiceTag_Root;
    private List<ServiceTag> aServiceTag_Child = new ArrayList<>();
    private List<Service> aService;

    public ServiceTag getoServiceTag_Root() {
        return oServiceTag_Root;
    }

    public void setoServiceTag_Root(ServiceTag oServiceTag_Root) {
        this.oServiceTag_Root = oServiceTag_Root;
    }

    public List<ServiceTag> getaServiceTag_Child() {
        return aServiceTag_Child;
    }

    public void setaServiceTag_Child(List<ServiceTag> aServiceTag_Child) {
        this.aServiceTag_Child = aServiceTag_Child;
    }

    public void addChild(ServiceTag child) {
        aServiceTag_Child.add(child);
    }

    public List<Service> getaService() {
        return aService;
    }

    public void setaService(List<Service> aService) {
        this.aService = aService;
    }
}
