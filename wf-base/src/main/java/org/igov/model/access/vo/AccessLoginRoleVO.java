package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceLoginRole;
import org.igov.model.access.AccessServiceRole;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 12:52
 */
public class AccessLoginRoleVO {
    private Long nID;
    private String sLogin;

    private AccessRoleVO oRole;

    public AccessLoginRoleVO() {
        // constructor needed for json deserialization
    }

    public AccessLoginRoleVO(AccessServiceLoginRole accessServiceLoginRole) {
        nID = accessServiceLoginRole.getId();
        sLogin = accessServiceLoginRole.getsLogin();
        oRole = new AccessRoleVO(accessServiceLoginRole.getAccessServiceRole());
    }

    public Long getnID() {
        return nID;
    }
    public void setnID(Long nID) {
        this.nID = nID;
    }

    public String getsLogin() {
        return sLogin;
    }
    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public AccessRoleVO getoRole() {
        return oRole;
    }
    public void setoRole(AccessRoleVO oRole) {
        this.oRole = oRole;
    }
}
