package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceRoleRightInclude;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 12:52
 */
public class AccessRoleIncludeVO {
    private Long nID;

    private AccessRoleVO oRole;

    public AccessRoleIncludeVO() {
        // constructor needed for json deserialization
    }

    public AccessRoleIncludeVO(AccessServiceRoleRightInclude include) {
        nID = include.getId();
        oRole = new AccessRoleVO(include.getAccessServiceRoleInclude());
    }

    public Long getnID() {
        return nID;
    }
    public void setnID(Long nID) {
        this.nID = nID;
    }

    public AccessRoleVO getoRole() {
        return oRole;
    }
    public void setoRole(AccessRoleVO oRole) {
        this.oRole = oRole;
    }
}
