package org.igov.model.process;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.model.core.NamedEntity;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class ProcessSubject extends NamedEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "snID_Process_Activiti")
    @Column
    private String snID_Process_Activiti;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_ProcessSubjectStatus")
    private ProcessSubjectStatus processSubjectStatus;

    @JsonProperty(value = "nOrder")
    @Column
    private Long nOrder;

    @JsonProperty(value = "sLogin")
    @Column
    private String sLogin;

    @JsonProperty(value = "sDateEdit")
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime sDateEdit;

    @JsonProperty(value = "sDatePlan")
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime sDatePlan;
    
    public String getSnID_Process_Activiti() {
        return snID_Process_Activiti;
    }

    public void setSnID_Process_Activiti(String snID_Process_Activiti) {
        this.snID_Process_Activiti = snID_Process_Activiti;
    }

    public ProcessSubjectStatus getProcessSubjectStatus() {
        return processSubjectStatus;
    }

    public void setProcessSubjectStatus(ProcessSubjectStatus processSubjectStatus) {
        this.processSubjectStatus = processSubjectStatus;
    }

    public Long getnOrder() {
        return nOrder;
    }

    public void setnOrder(Long nOrder) {
        this.nOrder = nOrder;
    }

    public String getsLogin() {
        return sLogin;
    }

    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public DateTime getsDateEdit() {
        return sDateEdit;
    }

    public void setsDateEdit(DateTime sDateEdit) {
        this.sDateEdit = sDateEdit;
    }

    public DateTime getsDatePlan() {
        return sDatePlan;
    }

    public void setsDatePlan(DateTime sDatePlan) {
        this.sDatePlan = sDatePlan;
    }

    @Override
    public String toString() {
        return "ProcessSubject{"
                + "id=" + getId()
                + ", snID_Process_Activiti=" + snID_Process_Activiti
                + ", processSubjectStatus=" + processSubjectStatus
                + ", nOrder=" + nOrder
                + ", sLogin=" + sLogin
                + ", sDateEdit=" + sDateEdit
                + ", sDatePlan=" + sDatePlan + '}';
    }

}
