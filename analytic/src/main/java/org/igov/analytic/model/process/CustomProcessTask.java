package org.igov.analytic.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * Created by dpekach on 26.12.16.
 */
@Entity
public class CustomProcessTask extends AbstractEntity {

    @JsonProperty(value = "oProcessTask")
    @OneToOne(fetch = FetchType.LAZY)
    private ProcessTask oProcessTask;

    @JsonProperty(value = "nProcessDefinitionId")
    @Column
    private String nProcessDefinitionId;

    @JsonProperty(value = "sTaskDefinitionKey")
    @Column
    private String sTaskDefinitionKey;

    @JsonProperty(value = "nProcessInstanceId")
    @Column
    private Integer nProcessInstanceId;

    @JsonProperty(value = "nExecutionId")
    @Column
    private Integer nExecutionId;

    @JsonProperty(value = "sName")
    @Column
    private String sName;

    @JsonProperty(value = "nParentTaskId")
    @Column
    private Integer nParentTaskId;

    @JsonProperty(value = "sDescription")
    @Column
    private String sDescription;

    @JsonProperty(value = "sOwner")
    @Column
    private String sOwner;

    @JsonProperty(value = "sAssignee")
    @Column
    private String sAssignee;

    @JsonProperty(value = "oClaimTime")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oClaimTime;

    @JsonProperty(value = "nDuration")
    @Column
    private Long nDuration;

    @JsonProperty(value = "sDelete")
    @Column
    private String sDeleteReason;

    @JsonProperty(value = "nPriority")
    @Column
    private Integer nPriority;

    @JsonProperty(value = "oDueDate")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oDueDate;

    @JsonProperty(value = "nFormKey")
    @Column
    private Integer nFormKey;

    @JsonProperty(value = "sCategory")
    @Column
    private String sCategory;

    @JsonProperty(value = "nTenantId")
    @Column
    private Integer nTenantId;

    public ProcessTask getoProcessTask() {
        return oProcessTask;
    }

    public void setoProcessTask(ProcessTask oProcessTask) {
        this.oProcessTask = oProcessTask;
    }

    public String getnProcessDefinitionId() {
        return nProcessDefinitionId;
    }

    public void setnProcessDefinitionId(String nProcessDefinitionId) {
        this.nProcessDefinitionId = nProcessDefinitionId;
    }

    public String getsTaskDefinitionKey() {
        return sTaskDefinitionKey;
    }

    public void setsTaskDefinitionKey(String sTaskDefinitionKey) {
        this.sTaskDefinitionKey = sTaskDefinitionKey;
    }

    public Integer getnProcessInstanceId() {
        return nProcessInstanceId;
    }

    public void setnProcessInstanceId(Integer nProcessInstanceId) {
        this.nProcessInstanceId = nProcessInstanceId;
    }

    public Integer getnExecutionId() {
        return nExecutionId;
    }

    public void setnExecutionId(Integer nExecutionId) {
        this.nExecutionId = nExecutionId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public Integer getnParentTaskId() {
        return nParentTaskId;
    }

    public void setnParentTaskId(Integer nParentTaskId) {
        this.nParentTaskId = nParentTaskId;
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

    public Integer getnFormKey() {
        return nFormKey;
    }

    public void setnFormKey(Integer nFormKey) {
        this.nFormKey = nFormKey;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

    public Integer getnTenantId() {
        return nTenantId;
    }

    public void setnTenantId(Integer nTenantId) {
        this.nTenantId = nTenantId;
    }

    @Override
    public String toString() {
        return "CustomProcessTask{" +
                "oProcessTask=" + oProcessTask +
                ", nProcessDefinitionId='" + nProcessDefinitionId + '\'' +
                ", sTaskDefinitionKey='" + sTaskDefinitionKey + '\'' +
                ", nProcessInstanceId=" + nProcessInstanceId +
                ", nExecutionId=" + nExecutionId +
                ", sName='" + sName + '\'' +
                ", nParentTaskId=" + nParentTaskId +
                ", sDescription='" + sDescription + '\'' +
                ", sOwner='" + sOwner + '\'' +
                ", sAssignee='" + sAssignee + '\'' +
                ", oClaimTime=" + oClaimTime +
                ", nDuration=" + nDuration +
                ", sDeleteReason='" + sDeleteReason + '\'' +
                ", nPriority=" + nPriority +
                ", oDueDate=" + oDueDate +
                ", nFormKey=" + nFormKey +
                ", sCategory='" + sCategory + '\'' +
                ", nTenantId=" + nTenantId +
                '}';
    }
}
