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
import javax.persistence.FetchType;
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
    
    @JsonProperty(value = "sText")
    @Column
    private String sText;
    
    @JsonProperty(value = "oProcessSubjectStatus")
    @ManyToOne(targetEntity = ProcessSubjectStatus.class)
    @JoinColumn(name = "nID_ProcessSubjectStatus")
    private ProcessSubjectStatus oProcessSubjectStatus;

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
    
    @JsonProperty(value = "sDatePlanNew")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime sDatePlanNew;
    
    @JsonProperty(value = "aUser")
    @Transient
    private List<ProcessUser> aUser;
    
    @JsonProperty(value = "aProcessSubjectChild")
    @Transient
    private List<ProcessSubject> aProcessSubjectChild;
    
    @JsonProperty(value = "sTextType")
    @Column
    private String sTextType;
    
    @JsonProperty(value = "sLoginRole")
    @Column
    private String sLoginRole;
    
    @JsonProperty(value = "nID_ProcessSubjectTask")
    @Column
    private Long nID_ProcessSubjectTask;
    
    /*@JsonProperty(value = "oProcessSubjectTask")
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = ProcessSubjectTask.class)
    @JoinColumn(name = "nID_ProcessSubjectTask")
    private ProcessSubjectTask oProcessSubjectTask;*/
    
    @JsonProperty(value = "snID_Task_Activiti")
    @Column
    private String snID_Task_Activiti;
    
    public List<ProcessSubject> getaProcessSubjectChild() {
	return aProcessSubjectChild;
    }

    public void setaProcessSubjectChild(List<ProcessSubject> aProcessSubjectChild) {
	this.aProcessSubjectChild = aProcessSubjectChild;
    }

    public String getSnID_Process_Activiti() {
        return snID_Process_Activiti;
    }

    public void setSnID_Process_Activiti(String snID_Process_Activiti) {
        this.snID_Process_Activiti = snID_Process_Activiti;
    }

    public ProcessSubjectStatus getoProcessSubjectStatus() {
        return oProcessSubjectStatus;
    }

    public void setoProcessSubjectStatus(ProcessSubjectStatus oProcessSubjectStatus) {
        this.oProcessSubjectStatus = oProcessSubjectStatus;
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
    
    public String getsText() {
        return sText;
    }

    public void setsText(String sText) {
        this.sText = sText;
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
        if (getoProcessSubjectStatus().getsID().equalsIgnoreCase("closed")) {
            return getsDateEdit();
        } else {
            return null;
        }
    }

    public String getsTextType() {
        return sTextType;
    }

    public void setsTextType(String sTextType) {
        this.sTextType = sTextType;
    }
    
    /*public ProcessSubjectTask getoProcessSubjectTask() {
        return oProcessSubjectTask;
    }

    public void setoProcessSubjectTask(ProcessSubjectTask oProcessSubjectTask) {
        this.oProcessSubjectTask = oProcessSubjectTask;
    }*/

    public Long getnID_ProcessSubjectTask() {
        return nID_ProcessSubjectTask;
    }

    public void setnID_ProcessSubjectTask(Long nID_ProcessSubjectTask) {
        this.nID_ProcessSubjectTask = nID_ProcessSubjectTask;
    }

    public DateTime getsDatePlanNew() {
        return sDatePlanNew;
    }

    public void setsDatePlanNew(DateTime sDatePlanNew) {
        this.sDatePlanNew = sDatePlanNew;
    }

    public String getsLoginRole() {
        return sLoginRole;
    }

    public void setsLoginRole(String sLoginRole) {
        this.sLoginRole = sLoginRole;
    }

    public void setsDateFact(DateTime sDateFact) {
        this.sDateFact = sDateFact;
    }

    public String getSnID_Task_Activiti() {
        return snID_Task_Activiti;
    }

    public void setSnID_Task_Activiti(String snID_Task_Activiti) {
        this.snID_Task_Activiti = snID_Task_Activiti;
    }
    
   
    /* @Override
     public String toString() {
         return "ProcessSubject{" + "id=" + getId()
                 + ", snID_Process_Activiti=" + snID_Process_Activiti
                 + ", sText=" + sText
                 + ", oProcessSubjectStatus=" + oProcessSubjectStatus
                 + ", nOrder=" + nOrder
                 + ", sLogin=" + sLogin
                 + ", sDateFact=" + sDateFact
                 + ", sDateEdit=" + sDateEdit
                 + ", sDatePlan=" + sDatePlan
                 + ", sDatePlanNew=" + sDatePlanNew
                 + ", aUser=" + aUser
                 + ", sTextType=" + sTextType
                 + ", sLoginRole=" + sLoginRole
                 + ", snID_Task_Activiti=" + snID_Task_Activiti + '}';
     }*/
    
        
}
