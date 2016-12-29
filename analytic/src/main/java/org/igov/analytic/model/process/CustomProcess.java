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
public class CustomProcess extends AbstractEntity {

    @JsonProperty(value = "oProcess")
    @OneToOne(fetch = FetchType.LAZY)
    private Process oProcess;


    @JsonProperty(value = "nProcessInstanceId")
    @Column
    private Integer nProcessInstanceId;

    @JsonProperty(value = "sBusinessKey")
    @Column
    private String sBusinessKey;

    @JsonProperty(value = "sProcessDefinitionId")
    @Column
    private String sProcessDefinitionId;


    @JsonProperty(value = "nDuration")
    @Column
    private Long nDuration;

    @JsonProperty(value = "sStartUserId")
    @Column
    private String sStartUserId;

    @JsonProperty(value = "sStartActionId")
    @Column
    private String sStartActionId;

    @JsonProperty(value = "sEndActionId")
    @Column
    private String sEndActionId;

    @JsonProperty(value = "sSuperProcessInstanceId")
    @Column
    private String sSuperProcessInstanceId;

    @JsonProperty(value = "sDeleteReason")
    @Column
    private String sDeleteReason;

    @JsonProperty(value = "sTenantId")
    @Column
    private String sTenantId;

    @JsonProperty(value = "sName")
    @Column
    private String sName;

    public Process getoProcess() {
        return oProcess;
    }

    public void setoProcess(Process oProcess) {
        this.oProcess = oProcess;
    }

    public Integer getnProcessInstanceId() {
        return nProcessInstanceId;
    }

    public void setnProcessInstanceId(Integer nProcessInstanceId) {
        this.nProcessInstanceId = nProcessInstanceId;
    }

    public String getsBusinessKey() {
        return sBusinessKey;
    }

    public void setsBusinessKey(String sBusinessKey) {
        this.sBusinessKey = sBusinessKey;
    }

    public String getsProcessDefinitionId() {
        return sProcessDefinitionId;
    }

    public void setsProcessDefinitionId(String sProcessDefinitionId) {
        this.sProcessDefinitionId = sProcessDefinitionId;
    }

    public Long getnDuration() {
        return nDuration;
    }

    public void setnDuration(Long nDuration) {
        this.nDuration = nDuration;
    }

    public String getsStartUserId() {
        return sStartUserId;
    }

    public void setsStartUserId(String sStartUserId) {
        this.sStartUserId = sStartUserId;
    }

    public String getsStartActionId() {
        return sStartActionId;
    }

    public void setsStartActionId(String sStartActionId) {
        this.sStartActionId = sStartActionId;
    }

    public String getsEndActionId() {
        return sEndActionId;
    }

    public void setsEndActionId(String sEndActionId) {
        this.sEndActionId = sEndActionId;
    }

    public String getsSuperProcessInstanceId() {
        return sSuperProcessInstanceId;
    }

    public void setsSuperProcessInstanceId(String sSuperProcessInstanceId) {
        this.sSuperProcessInstanceId = sSuperProcessInstanceId;
    }

    public String getsDeleteReason() {
        return sDeleteReason;
    }

    public void setsDeleteReason(String sDeleteReason) {
        this.sDeleteReason = sDeleteReason;
    }

    public String getsTenantId() {
        return sTenantId;
    }

    public void setsTenantId(String sTenantId) {
        this.sTenantId = sTenantId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    @Override
    public String toString() {
        return "CustomProcess{" +
                "oProcess=" + oProcess +
                ", nProcessInstanceId=" + nProcessInstanceId +
                ", sBusinessKey='" + sBusinessKey + '\'' +
                ", sProcessDefinitionId='" + sProcessDefinitionId + '\'' +
                ", nDuration=" + nDuration +
                ", sStartUserId='" + sStartUserId + '\'' +
                ", sStartActionId='" + sStartActionId + '\'' +
                ", sEndActionId='" + sEndActionId + '\'' +
                ", sSuperProcessInstanceId='" + sSuperProcessInstanceId + '\'' +
                ", sDeleteReason='" + sDeleteReason + '\'' +
                ", sTenantId='" + sTenantId + '\'' +
                ", sName='" + sName + '\'' +
                '}';
    }
}
