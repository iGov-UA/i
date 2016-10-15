package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceRight;

import javax.persistence.Column;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 12:52
 */
public class AccessRightVO {

    private Long nID;
    private String sName;
    private String sService;
    private String saMethod;
    private String sHandlerBean;
    private boolean bDeny;
    private Integer nOrder;

    public AccessRightVO() {
        // constructor needed for json deserialization
    }

    public AccessRightVO(AccessServiceRight right) {
        nID = right.getId();
        sName = right.getName();
        sService = right.getsService();
        saMethod = right.getSaMethod();
        sHandlerBean = right.getsHandlerBean();
        bDeny = right.isbDeny();
        nOrder = right.getnOrder();
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

    public String getsService() {
        return sService;
    }
    public void setsService(String sService) {
        this.sService = sService;
    }

    public String getSaMethod() {
        return saMethod;
    }
    public void setSaMethod(String saMethod) {
        this.saMethod = saMethod;
    }

    public String getsHandlerBean() {
        return sHandlerBean;
    }
    public void setsHandlerBean(String sHandlerBean) {
        this.sHandlerBean = sHandlerBean;
    }

    public boolean isbDeny() {
        return bDeny;
    }
    public void setbDeny(boolean bDeny) {
        this.bDeny = bDeny;
    }

    public Integer getnOrder() {
        return nOrder;
    }
    public void setnOrder(Integer nOrder) {
        this.nOrder = nOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessRightVO that = (AccessRightVO) o;

        if (bDeny != that.bDeny) return false;
        if (nID != null ? !nID.equals(that.nID) : that.nID != null) return false;
        if (sName != null ? !sName.equals(that.sName) : that.sName != null) return false;
        if (sService != null ? !sService.equals(that.sService) : that.sService != null) return false;
        if (saMethod != null ? !saMethod.equals(that.saMethod) : that.saMethod != null) return false;
        if (sHandlerBean != null ? !sHandlerBean.equals(that.sHandlerBean) : that.sHandlerBean != null) return false;
        return nOrder != null ? nOrder.equals(that.nOrder) : that.nOrder == null;

    }

    @Override
    public int hashCode() {
        int result = nID != null ? nID.hashCode() : 0;
        result = 31 * result + (sName != null ? sName.hashCode() : 0);
        result = 31 * result + (sService != null ? sService.hashCode() : 0);
        result = 31 * result + (saMethod != null ? saMethod.hashCode() : 0);
        result = 31 * result + (sHandlerBean != null ? sHandlerBean.hashCode() : 0);
        result = 31 * result + (bDeny ? 1 : 0);
        result = 31 * result + (nOrder != null ? nOrder.hashCode() : 0);
        return result;
    }
}
