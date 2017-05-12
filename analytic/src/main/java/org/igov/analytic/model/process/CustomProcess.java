package org.igov.analytic.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.AbstractEntity;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by dpekach on 26.12.16.
 */
@Entity
@Table(name = "Custom Process")
public class CustomProcess extends AbstractEntity {

    @JsonProperty
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nID_Process")
    @Cascade(CascadeType.ALL)
    private Process oProcess;

    @JsonProperty
    @Column
    private String sProcessInstanceId;

    @JsonProperty
    @Column
    private String sBusinessKey;

    @JsonProperty
    @Column
    private String sProcessDefinitionId;

    @JsonProperty
    @Column
    private Long nDuration;

    @JsonProperty
    @Column
    private String sStartUserId;

    @JsonProperty
    @Column
    private String sStartActivityId;

    @JsonProperty
    @Column
    private String sEndActivityId;

    @JsonProperty
    @Column
    private String sSuperProcessInstanceId;

    @JsonProperty
    @Column
    private String sDeleteReason;

    @JsonProperty
    @Column
    private String sTenantId;

    @JsonProperty
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
                ", sProcessInstanceId='" + sProcessInstanceId + '\'' +
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
