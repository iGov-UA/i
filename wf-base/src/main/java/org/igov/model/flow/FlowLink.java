package org.igov.model.flow;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.igov.model.core.Entity;

/**
 * Created by Богдан on 23.10.2015.
 */

@javax.persistence.Entity
public class FlowLink extends Entity {

    @ManyToOne(targetEntity = Flow_ServiceData.class)
    @JoinColumn(name = "nID_Flow_ServiceData")
    private Flow_ServiceData flow_ServiceData;

    @Column
    private Long nID_Service;
    
    @Column
    private Long nID_SubjectOrganDepartment;
	
    public Flow_ServiceData getFlow_ServiceData() {
        return flow_ServiceData;
    }

    public void setFlow_ServiceData(Flow_ServiceData flow_ServiceData) {
        this.flow_ServiceData = flow_ServiceData;
    }

    public Long getnID_Service() {
        return nID_Service;
    }

    public void setnID_Service(Long nID_Service) {
        this.nID_Service = nID_Service;
    }

	public Long getnID_SubjectOrganDepartment() {
		return nID_SubjectOrganDepartment;
	}

	public void setnID_SubjectOrganDepartment(Long nID_SubjectOrganDepartment) {
		this.nID_SubjectOrganDepartment = nID_SubjectOrganDepartment;
	}
    }
