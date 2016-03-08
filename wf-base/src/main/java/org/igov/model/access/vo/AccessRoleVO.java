package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceRole;
import org.igov.model.access.AccessServiceRoleRight;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 12:52
 */
public class AccessRoleVO {
    private Long nID;
    private String sName;

    private List<AccessRoleRightVO> aRoleRight = new ArrayList<>();
    private List<AccessRoleIncludeVO> aRoleRightInclude = new ArrayList<>();

    public AccessRoleVO() {
        // constructor needed for json deserialization
    }

    public AccessRoleVO(AccessServiceRole role) {
        nID = role.getId();
        sName = role.getName();

        aRoleRight.addAll(role.getRights().stream().map(AccessRoleRightVO::new).collect(Collectors.toList()));
        aRoleRightInclude.addAll(role.getIncludes().stream().map(AccessRoleIncludeVO::new).collect(Collectors.toList()));
    }

    public Long getnID() {
        return nID;
    }
    public void setnID(Long nID) {
        this.nID = nID;
    }

    public String getsName() {
        return sName;
    }
    public void setsName(String sName) {
        this.sName = sName;
    }

    public List<AccessRoleRightVO> getaRoleRight() {
        return aRoleRight;
    }
    public void setaRoleRight(List<AccessRoleRightVO> aRoleRight) {
        this.aRoleRight = aRoleRight;
    }

    public List<AccessRoleIncludeVO> getaRoleRightInclude() {
        return aRoleRightInclude;
    }
    public void setaRoleRightInclude(List<AccessRoleIncludeVO> aRoleRightInclude) {
        this.aRoleRightInclude = aRoleRightInclude;
    }
}
