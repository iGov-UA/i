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


    @JsonProperty(value = "sProcessInstanceId")
    @Column
    private String sProcessInstanceId;

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

    @JsonProperty(value = "sStartActivityId")
    @Column
    private String sStartActivityId;

    @JsonProperty(value = "sEndActivityId")
    @Column
    private String sEndActivityId;

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

    public String getsProcessInstanceId() {
        return sProcessInstanceId;
    }

    public void setsProcessInstanceId(String sProcessInstanceId) {
        this.sProcessInstanceId = sProcessInstanceId;
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

    public String getsStartActivityId() {
        return sStartActivityId;
    }

    public void setsStartActivityId(String sStartActivityId) {
        this.sStartActivityId = sStartActivityId;
    }

    public String getsEndActivityId() {
        return sEndActivityId;
    }

    public void setsEndActivityId(String sEndActivityId) {
        this.sEndActivityId = sEndActivityId;
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
                ", sProcessInstanceId=" + sProcessInstanceId +
                ", sBusinessKey='" + sBusinessKey + '\'' +
                ", sProcessDefinitionId='" + sProcessDefinitionId + '\'' +
                ", nDuration=" + nDuration +
                ", sStartUserId='" + sStartUserId + '\'' +
                ", sStartActivityId='" + sStartActivityId + '\'' +
                ", sEndActivityId='" + sEndActivityId + '\'' +
                ", sSuperProcessInstanceId='" + sSuperProcessInstanceId + '\'' +
                ", sDeleteReason='" + sDeleteReason + '\'' +
                ", sTenantId='" + sTenantId + '\'' +
                ", sName='" + sName + '\'' +
                '}';
    }
}
