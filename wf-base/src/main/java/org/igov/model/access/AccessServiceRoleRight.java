package org.igov.model.access;

import org.apache.commons.collections.CollectionUtils;
import org.igov.model.core.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;

/**
 * Link which connect Role with Right.
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 17:38
 */
@Entity
public class AccessServiceRoleRight extends org.igov.model.core.Entity {

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
