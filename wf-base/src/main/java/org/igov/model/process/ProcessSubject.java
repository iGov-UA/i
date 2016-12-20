package org.igov.model.process;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import javax.persistence.Transient;

@javax.persistence.Entity
public class ProcessSubject extends AbstractEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "snID_Process_Activiti")
    @Column
    private String snID_Process_Activiti;
    
    @JsonProperty(value = "sReport")
    @Column
    private String sReport;
    
    @JsonProperty(value = "oProcessSubjectStatus")
    @ManyToOne(targetEntity = ProcessSubjectStatus.class)
    @JoinColumn(name = "nID_ProcessSubjectStatus")
    private ProcessSubjectStatus processSubjectStatus;

    @JsonProperty(value = "nOrder")
    @Column
    private Long nOrder;

    @JsonProperty(value = "sLogin")
    @Column
    private String sLogin;

    @JsonProperty(value = "sDateFact")
    @Transient
    private DateTime sDateFact;
        
    @JsonProperty(value = "sDateEdit")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime sDateEdit;

    @JsonProperty(value = "sDatePlan")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime sDatePlan;
    
    @JsonProperty(value = "aUser")
    @Transient
    private List<ProcessUser> aUser;
    
    @JsonProperty(value = "aProcessSubjects")
    @Transient
    private List<ProcessSubject> aProcessSubj;
    
    public List<ProcessSubject> getaProcessSubj() {
	return aProcessSubj;
    }

    public void setaProcessSubj(List<ProcessSubject> aProcessSubj) {
	this.aProcessSubj = aProcessSubj;
    }

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
    
    public String getsReport() {
        return sReport;
    }

    public void setsReport(String sReport) {
        this.sReport = sReport;
    }

    public DateTime getsDatePlan() {
        return sDatePlan;
    }

    public void setsDatePlan(DateTime sDatePlan) {
        this.sDatePlan = sDatePlan;
    }

    public List<ProcessUser> getaUser() {
        return aUser;
    }

    public void setaUser(List<ProcessUser> aUser) {
        this.aUser = aUser;
    }

    public DateTime getsDateFact() {
        return sDateFact;
    }

    public DateTime getsDateFact(DateTime sDateFact) {
        if (getProcessSubjectStatus().getsID().equalsIgnoreCase("closed")) {
            return getsDateEdit();
        } else {
            return null;
        }
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
