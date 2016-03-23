package org.igov.model.access;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Link which connect Role with another included Role.
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 17:38
 */
@Entity
public class AccessServiceRoleRightInclude extends org.igov.model.core.Entity {

    @ManyToOne
    @JoinColumn(name="nID_AccessServiceRole", nullable = false, updatable = false)
    private AccessServiceRole accessServiceRole;

    @ManyToOne
    @JoinColumn(name="nID_AccessServiceRole_Include", nullable = false, updatable = false)
    private AccessServiceRole accessServiceRoleInclude;

    public AccessServiceRole getAccessServiceRole() {
        return accessServiceRole;
    }
    public void setAccessServiceRole(AccessServiceRole accessServiceRole) {
        this.accessServiceRole = accessServiceRole;
    }

    public AccessServiceRole getAccessServiceRoleInclude() {
        return accessServiceRoleInclude;
    }
    public void setAccessServiceRoleInclude(AccessServiceRole accessServiceRoleInclude) {
        this.accessServiceRoleInclude = accessServiceRoleInclude;
    }
}
