package org.wf.dp.dniprorada.base.model;

import javax.persistence.Column;

/**
 * Created by Богдан on 23.10.2015.
 */

@javax.persistence.Entity
public class FlowLink extends NamedEntity {

    @Column
    private Long nID;


    @Column
    private Long nID_Flow_ServiceData;

    @Column
    private String nID_Service;

    public Long getnID() {
        return nID;
    }

    public void setnID(Long nID) {
        this.nID = nID;
    }

    public Long getnID_Flow_ServiceData() {
        return nID_Flow_ServiceData;
    }

    public void setnID_Flow_ServiceData(Long nID_Flow_ServiceData) {
        this.nID_Flow_ServiceData = nID_Flow_ServiceData;
    }

    public String getnID_Service() {
        return nID_Service;
    }

    public void setnID_Service(String nID_Service) {
        this.nID_Service = nID_Service;
    }
}
