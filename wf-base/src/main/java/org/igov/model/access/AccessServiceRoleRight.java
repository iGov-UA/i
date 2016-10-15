package org.igov.model.access;

import org.igov.model.core.*;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * Link which connect Role with Right.
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 17:38
 */
@Entity
public class AccessServiceRoleRight extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name="nID_AccessServiceRole", nullable = false, updatable = false)
    private AccessServiceRole accessServiceRole;

    @ManyToOne
    @JoinColumn(name="nID_AccessServiceRight", nullable = false, updatable = false)
    private AccessServiceRight accessServiceRight;

    public AccessServiceRole getAccessServiceRole() {
        return accessServiceRole;
    }
    public void setAccessServiceRole(AccessServiceRole accessServiceRole) {
        this.accessServiceRole = accessServiceRole;
    }

    public AccessServiceRight getAccessServiceRight() {
        return accessServiceRight;
    }
    public void setAccessServiceRight(AccessServiceRight accessServiceRight) {
        this.accessServiceRight = accessServiceRight;
    }
}
