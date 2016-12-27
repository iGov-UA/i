package org.igov.analytic.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * Created by dpekach on 26.12.16.
 */
@Entity
public class Custom_Process extends AbstractEntity {

    @JsonProperty(value = "oProcess")
    @OneToOne(fetch = FetchType.LAZY)
    private Process oProcess;

    @JsonProperty(value = "nProcessDefinitionId")
    @Column
    private Integer nProcessDefinitionId;

    @JsonProperty(value = "nExecutionId")
    @Column
    private Integer nExecutionId;

    @JsonProperty(value = "nActionId")
    @Column
    private Integer nActionId;

    @JsonProperty(value = "nTaskId")
    @Column
    private Integer nTaskId;

    @JsonProperty(value = "nCallProcInstId")
    @Column
    private Integer nCallProcInstId;

    @JsonProperty(value = "sActionName")
    @Column
    private String sActionName;

    @JsonProperty(value = "sActionType")
    @Column
    private String sActionType;

    @JsonProperty(value = "sAssignee")
    @Column
    private String sAssignee;

    @JsonProperty(value = "nDuration")
    @Column
    private Long nDuration;

    @JsonProperty(value = "nTenantId")
    @Column
    private Integer nTenantId;

    public Process getoProcess() {
        return oProcess;
    }

    public void setoProcess(Process oProcess) {
        this.oProcess = oProcess;
    }

    public Integer getnProcessDefinitionId() {
        return nProcessDefinitionId;
    }

    public void setnProcessDefinitionId(Integer nProcessDefinitionId) {
        this.nProcessDefinitionId = nProcessDefinitionId;
    }

    public Integer getnExecutionId() {
        return nExecutionId;
    }

    public void setnExecutionId(Integer nExecutionId) {
        this.nExecutionId = nExecutionId;
    }

    public Integer getnActionId() {
        return nActionId;
    }

    public void setnActionId(Integer nActionId) {
        this.nActionId = nActionId;
    }

    public Integer getnTaskId() {
        return nTaskId;
    }

    public void setnTaskId(Integer nTaskId) {
        this.nTaskId = nTaskId;
    }

    public Integer getnCallProcInstId() {
        return nCallProcInstId;
    }

    public void setnCallProcInstId(Integer nCallProcInstId) {
        this.nCallProcInstId = nCallProcInstId;
    }

    public String getsActionName() {
        return sActionName;
    }

    public void setsActionName(String sActionName) {
        this.sActionName = sActionName;
    }

    public String getsActionType() {
        return sActionType;
    }

    public void setsActionType(String sActionType) {
        this.sActionType = sActionType;
    }

    public String getsAssignee() {
        return sAssignee;
    }

    public void setsAssignee(String sAssignee) {
        this.sAssignee = sAssignee;
    }

    public Long getnDuration() {
        return nDuration;
    }

    public void setnDuration(Long nDuration) {
        this.nDuration = nDuration;
    }

    public Integer getnTenantId() {
        return nTenantId;
    }

    public void setnTenantId(Integer nTenantId) {
        this.nTenantId = nTenantId;
    }
}
