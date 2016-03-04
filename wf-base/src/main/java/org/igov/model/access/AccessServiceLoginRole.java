package org.igov.model.access;

import org.igov.model.core.Entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Links user login to {@link AccessServiceRole}
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 18:39
 */
@javax.persistence.Entity
public class AccessServiceLoginRole extends Entity {

    /**
     * Login of user which require access to service
     */
    @Column
    private String sLogin;

    /**
     * Access service role of user
     */
    @ManyToOne(targetEntity = AccessServiceRole.class)
    @JoinColumn(name="nID_AccessServiceRole", nullable = false, updatable = false)
    private AccessServiceRole accessServiceRole;

    public String getsLogin() {
        return sLogin;
    }
    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public AccessServiceRole getAccessServiceRole() {
        return accessServiceRole;
    }
    public void setAccessServiceRole(AccessServiceRole accessServiceRole) {
        this.accessServiceRole = accessServiceRole;
    }
}
