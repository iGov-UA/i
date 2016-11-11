package org.igov.model.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.joda.time.DateTime;

@Entity
public class ProcessSubject extends AbstractEntity {

    @JsonProperty(value = "snID_Process_Activiti")
    private String snID_Process_Activiti;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_ProcessSubjectStatus")
    private ProcessSubjectStatus processSubjectStatus;

    @JsonProperty(value = "nOrder")
    private Long nOrder;

    @JsonProperty(value = "sLogin")
    private String sLogin;

    @JsonProperty(value = "sDateEdit")
    @Type(type = DATETIME_TYPE)
    private DateTime sDateEdit;

    @JsonProperty(value = "sDatePlan")
    @Type(type = DATETIME_TYPE)
    private DateTime sDatePlan;
    
    /*
    @OneToMany(targetEntity = ProcessSubjectTree.class, mappedBy = "processSubjectChild", cascade = CascadeType.ALL)
    private List<ProcessSubjectTree> processChild;

    @OneToMany(targetEntity = ProcessSubjectTree.class, mappedBy = "processSubjectParent", cascade = CascadeType.ALL)
    private List<ProcessSubjectTree> processParent;  
    

    public List<ProcessSubjectTree> getProcessChild() {
        return processChild;
    }

    public void setProcessChild(List<ProcessSubjectTree> processChild) {
        this.processChild = processChild;
    }

    public List<ProcessSubjectTree> getProcessParent() {
        return processParent;
    }

    public void setProcessParent(List<ProcessSubjectTree> processParent) {
        this.processParent = processParent;
    }
    */

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
