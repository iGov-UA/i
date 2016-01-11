package org.igov.model.access;

import javax.persistence.Column;
import org.igov.model.core.Entity;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 20:47
 */
@javax.persistence.Entity
public class AccessServiceLoginRight extends Entity {

    @Column
    private String sLogin;

    @Column
    private String sService;

    @Column
    private String sHandlerBean;

    public String getsLogin() {
        return sLogin;
    }

    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public String getsService() {
        return sService;
    }

    public void setsService(String sService) {
        this.sService = sService;
    }

    public String getsHandlerBean() {
        return sHandlerBean;
    }

    public void setsHandlerBean(String sHandlerBean) {
        this.sHandlerBean = sHandlerBean;
    }
}
