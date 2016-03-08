package org.igov.model.access.vo;

import org.igov.model.access.AccessServiceRight;
import org.igov.model.access.AccessServiceRoleRight;
import org.igov.service.business.access.handler.AccessServiceLoginRightHandler;

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
}
