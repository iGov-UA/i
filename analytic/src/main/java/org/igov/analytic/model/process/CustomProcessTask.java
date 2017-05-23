package org.igov.analytic.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by dpekach on 26.12.16.
 */
@Entity
@Table(name = "Custom Process Task")
public class CustomProcessTask extends AbstractEntity {

    @JsonProperty
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nID_ProcessTask")
    @Cascade(value = CascadeType.ALL)
    private ProcessTask oProcessTask;

    @JsonProperty
    @Column
    private String sProcessDefinitionId;

    @JsonProperty
    @Column
    private String sTaskDefinitionKey;

    @JsonProperty
    @Column
    private String sProcessInstanceId;

    @JsonProperty
    @Column
    private String sExecutionId;

    @JsonProperty
    @Column
    private String sName;

    @JsonProperty
    @Column
    private String sParentTaskId;

    @JsonProperty
    @Column
    private String sDescription;

    @JsonProperty
    @Column
    private String sOwner;

    @JsonProperty
    @Column
    private String sAssignee;

    @JsonProperty
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oClaimTime;

    @JsonProperty
    @Column
    private Long nDuration;

    @JsonProperty
    @Column
    private String sDeleteReason;

    @JsonProperty
    @Column
    private Integer nPriority;

    @JsonProperty
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oDueDate;

    @JsonProperty
    @Column
    private String sFormKey;

    @JsonProperty
    @Column
    private String sCategory;

    @JsonProperty
    @Column
    private String sTenantId;


    public ProcessTask getoProcessTask() {
        return oProcessTask;
    }

    public void setoProcessTask(ProcessTask oProcessTask) {
        this.oProcessTask = oProcessTask;
    }

    public String getsProcessDefinitionId() {
        return sProcessDefinitionId;
    }

    public void setsProcessDefinitionId(String sProcessDefinitionId) {
        this.sProcessDefinitionId = sProcessDefinitionId;
    }

    public String getsTaskDefinitionKey() {
        return sTaskDefinitionKey;
    }

    public void setsTaskDefinitionKey(String sTaskDefinitionKey) {
        this.sTaskDefinitionKey = sTaskDefinitionKey;
    }

    public String getsProcessInstanceId() {
        return sProcessInstanceId;
    }

    public void setsProcessInstanceId(String sProcessInstanceId) {
        this.sProcessInstanceId = sProcessInstanceId;
    }

    public String getsExecutionId() {
        return sExecutionId;
    }

    public void setsExecutionId(String sExecutionId) {
        this.sExecutionId = sExecutionId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsParentTaskId() {
        return sParentTaskId;
    }

    public void setsParentTaskId(String sParentTaskId) {
        this.sParentTaskId = sParentTaskId;
    }

    public String getsDescription() {
        return sDescription;
    }

    public void setsDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    public String getsOwner() {
        return sOwner;
    }

    public void setsOwner(String sOwner) {
        this.sOwner = sOwner;
    }

    public String getsAssignee() {
        return sAssignee;
    }

    public void setsAssignee(String sAssignee) {
        this.sAssignee = sAssignee;
    }

    public DateTime getoClaimTime() {
        return oClaimTime;
    }

    public void setoClaimTime(DateTime oClaimTime) {
        this.oClaimTime = oClaimTime;
    }

    public Long getnDuration() {
        return nDuration;
    }

    public void setnDuration(Long nDuration) {
        this.nDuration = nDuration;
    }

    public String getsDeleteReason() {
        return sDeleteReason;
    }

    public void setsDeleteReason(String sDeleteReason) {
        this.sDeleteReason = sDeleteReason;
    }

    public Integer getnPriority() {
        return nPriority;
    }

    public void setnPriority(Integer nPriority) {
        this.nPriority = nPriority;
    }

    public DateTime getoDueDate() {
        return oDueDate;
    }

    public void setoDueDate(DateTime oDueDate) {
        this.oDueDate = oDueDate;
    }

    public String getsFormKey() {
        return sFormKey;
    }

    public void setsFormKey(String sFormKey) {
        this.sFormKey = sFormKey;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

    public String getsTenantId() {
        return sTenantId;
    }

    public void setsTenantId(String sTenantId) {
        this.sTenantId = sTenantId;
    }

    @Override
    public String toString() {
        return "CustomProcessTask{" +
                "oProcessTask=" + oProcessTask +
                ", sProcessDefinitionId='" + sProcessDefinitionId + '\'' +
                ", sTaskDefinitionKey='" + sTaskDefinitionKey + '\'' +
                ", sProcessInstanceId='" + sProcessInstanceId + '\'' +
                ", sExecutionId='" + sExecutionId + '\'' +
                ", sName='" + sName + '\'' +
                ", sParentTaskId='" + sParentTaskId + '\'' +
                ", sDescription='" + sDescription + '\'' +
                ", sOwner='" + sOwner + '\'' +
                ", sAssignee='" + sAssignee + '\'' +
                ", oClaimTime=" + oClaimTime +
                ", nDuration=" + nDuration +
                ", sDeleteReason='" + sDeleteReason + '\'' +
                ", nPriority=" + nPriority +
                ", oDueDate=" + oDueDate +
                ", nFormKey='" + sFormKey + '\'' +
                ", sCategory='" + sCategory + '\'' +
                ", sTenantId='" + sTenantId + '\'' +
                '}';
    }
}
