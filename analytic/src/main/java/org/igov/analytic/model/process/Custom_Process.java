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


    @JsonProperty(value = "nProcInstId")
    @Column
    private Integer nProc_inst_id;

    @JsonProperty(value = "sBusiness_key")
    @Column
    private String sBusiness_key;

    @JsonProperty(value = "sProc_def_id")
    @Column
    private String sProc_def_id;


    @JsonProperty(value = "nDuration")
    @Column
    private Long nDuration;

    @JsonProperty(value = "sStart_user_id")
    @Column
    private String sStart_user_id;

    @JsonProperty(value = "sStart_act_id")
    @Column
    private String sStart_act_id;

    @JsonProperty(value = "sEnd_act_id")
    @Column
    private String sEnd_act_id;

    @JsonProperty(value = "sSuper_process_instance_id")
    @Column
    private String sSuper_process_instance_id;

    @JsonProperty(value = "sDelete_reason")
    @Column
    private String sDelete_reason;

    @JsonProperty(value = "sTenant_id")
    @Column
    private String sTenant_id;

    @JsonProperty(value = "sName")
    @Column
    private String sName;

    public Process getoProcess() {
        return oProcess;
    }

    public void setoProcess(Process oProcess) {
        this.oProcess = oProcess;
    }

    public Integer getnProc_inst_id() {
        return nProc_inst_id;
    }

    public void setnProc_inst_id(Integer nProc_inst_id) {
        this.nProc_inst_id = nProc_inst_id;
    }

    public String getsBusiness_key() {
        return sBusiness_key;
    }

    public void setsBusiness_key(String sBusiness_key) {
        this.sBusiness_key = sBusiness_key;
    }

    public String getsProc_def_id() {
        return sProc_def_id;
    }

    public void setsProc_def_Id(String sProc_def_Id) {
        this.sProc_def_id = sProc_def_Id;
    }

    public Long getnDuration() {
        return nDuration;
    }

    public void setnDuration(Long nDuration) {
        this.nDuration = nDuration;
    }

    public String getsStart_user_Id() {
        return sStart_user_id;
    }

    public void setsStart_user_Id(String sStart_user_Id) {
        this.sStart_user_id = sStart_user_Id;
    }

    public String getsStart_act_Id() {
        return sStart_act_id;
    }

    public void setsStart_act_Id(String sStart_act_Id) {
        this.sStart_act_id = sStart_act_Id;
    }

    public String getsEnd_act_Id() {
        return sEnd_act_id;
    }

    public void setsEnd_act_Id(String sEnd_act_Id) {
        this.sEnd_act_id = sEnd_act_Id;
    }

    public String getsSuper_process_instance_id() {
        return sSuper_process_instance_id;
    }

    public void setsSuper_process_instance_id(String sSuper_process_instance_id) {
        this.sSuper_process_instance_id = sSuper_process_instance_id;
    }

    public String getsDelete_reason() {
        return sDelete_reason;
    }

    public void setsDelete_reason(String sDelete_reason) {
        this.sDelete_reason = sDelete_reason;
    }

    public String getsTenant_id() {
        return sTenant_id;
    }

    public void setsTenant_id(String sTenant_id) {
        this.sTenant_id = sTenant_id;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
}
