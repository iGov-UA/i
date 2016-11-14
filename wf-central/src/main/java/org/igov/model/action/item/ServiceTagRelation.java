package org.igov.model.action.item;

import org.igov.model.access.AccessServiceRole;
import org.igov.model.core.AbstractEntity;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Tag of Service
 * User: goodg_000
 * Date: 19.06.2016
 * Time: 20:27
 */
@javax.persistence.Entity
public class ServiceTagRelation extends AbstractEntity {

    @ManyToOne(targetEntity = ServiceTag.class)
    @JoinColumn(name="nID_ServiceTag_Parent", nullable = false, updatable = false)
    private ServiceTag oServiceTag_Parent;

    @ManyToOne(targetEntity = ServiceTag.class)
    @JoinColumn(name="nID_ServiceTag_Child", nullable = false, updatable = false)
    private ServiceTag oServiceTag_Child;
    

    public ServiceTag getServiceTag_Parent() {
        return oServiceTag_Parent;
    }
    public void setServiceTag_Parent(ServiceTag oServiceTag) {
        this.oServiceTag_Parent = oServiceTag;
    }
    
    public ServiceTag getServiceTag_Child() {
        return oServiceTag_Child;
    }
    public void setServiceTag_Child(ServiceTag oServiceTag) {
        this.oServiceTag_Child = oServiceTag;
    }
    
}
