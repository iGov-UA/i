package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceRoleRight;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 12:52
 */
public class AccessRoleRightVO {
    private Long nID;

    private AccessRightVO oRight;

    public AccessRoleRightVO() {
        // constructor needed for json deserialization
    }

    public AccessRoleRightVO(AccessServiceRoleRight roleRight) {
        nID = roleRight.getId();
        oRight = new AccessRightVO(roleRight.getAccessServiceRight());
    }

    public Long getnID() {
        return nID;
    }
    public void setnID(Long nID) {
        this.nID = nID;
    }

    public AccessRightVO getoRight() {
        return oRight;
    }
    public void setoRight(AccessRightVO oRight) {
        this.oRight = oRight;
    }
}
