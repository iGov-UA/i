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
public class Custom_ProcessTask extends AbstractEntity {

    @JsonProperty(value = "oProcessTask")
    @OneToOne(fetch = FetchType.LAZY)
    private ProcessTask oProcessTask;

    @JsonProperty(value = "nProc_def_id")
    @Column
    private String nProc_def_id;

    @JsonProperty(value = "sTask_def_key")
    @Column
    private String sTask_def_key;

    @JsonProperty(value = "nProc_inst_id")
    @Column
    private Integer nProc_inst_id;

    @JsonProperty(value = "nExecution_id")
    @Column
    private Integer nExecution_id;

    @JsonProperty(value = "sName")
    @Column
    private String sName;

    @JsonProperty(value = "nParent_task_id")
    @Column
    private Integer nParent_task_id;

    @JsonProperty(value = "sDescription")
    @Column
    private String sDescription;

    @JsonProperty(value = "sOwner")
    @Column
    private String sOwner;

    @JsonProperty(value = "sAssignee")
    @Column
    private String sAssignee;

    @JsonProperty(value = "oClaim_time")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oClaim_time;

    @JsonProperty(value = "nDuration")
    @Column
    private Long nDuration;

    @JsonProperty(value = "sDelete")
    @Column
    private String sDelete_reason;

    @JsonProperty(value = "nPriority")
    @Column
    private Integer nPriority;

    @JsonProperty(value = "oDue_Date")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oDue_Date;

    @JsonProperty(value = "nForm_key")
    @Column
    private Integer nForm_key;

    @JsonProperty(value = "sCategory")
    @Column
    private String sCategory;

    @JsonProperty(value = "nTenant_id")
    @Column
    private Integer nTenant_id;

    public ProcessTask getoProcessTask() {
        return oProcessTask;
    }

    public void setoProcessTask(ProcessTask oProcessTask) {
        this.oProcessTask = oProcessTask;
    }

    public String getnProc_def_id() {
        return nProc_def_id;
    }

    public void setnProc_def_id(String nProc_def_id) {
        this.nProc_def_id = nProc_def_id;
    }

    public String getsTask_def_key() {
        return sTask_def_key;
    }

    public void setsTask_def_key(String sTask_def_key) {
        this.sTask_def_key = sTask_def_key;
    }

    public Integer getnProc_inst_id() {
        return nProc_inst_id;
    }

    public void setnProc_inst_id(Integer nProc_inst_id) {
        this.nProc_inst_id = nProc_inst_id;
    }

    public Integer getnExecution_id() {
        return nExecution_id;
    }

    public void setnExecution_id(Integer nExecution_id) {
        this.nExecution_id = nExecution_id;
    }

    public Integer getnPriority() {
        return nPriority;
    }

    public void setnPriority(Integer nPriority) {
        this.nPriority = nPriority;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public Integer getnParent_task_id() {
        return nParent_task_id;
    }

    public void setnParent_task_id(Integer nParent_task_id) {
        this.nParent_task_id = nParent_task_id;
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

    public DateTime getoClaim_time() {
        return oClaim_time;
    }

    public void setoClaim_time(DateTime oClaim_time) {
        this.oClaim_time = oClaim_time;
    }

    public Long getnDuration() {
        return nDuration;
    }

    public void setnDuration(Long nDuration) {
        this.nDuration = nDuration;
    }

    public String getsDelete_reason() {
        return sDelete_reason;
    }

    public void setsDelete_reason(String sDelete_reason) {
        this.sDelete_reason = sDelete_reason;
    }


    public DateTime getoDue_Date() {
        return oDue_Date;
    }

    public void setoDue_Date(DateTime oDue_Date) {
        this.oDue_Date = oDue_Date;
    }

    public Integer getnForm_key() {
        return nForm_key;
    }

    public void setnForm_key(Integer nForm_key) {
        this.nForm_key = nForm_key;
    }

    public String getsCategory() {
        return sCategory;
    }

    public void setsCategory(String sCategory) {
        this.sCategory = sCategory;
    }

    public Integer getnTenant_id() {
        return nTenant_id;
    }

    public void setnTenant_id(Integer nTenant_id) {
        this.nTenant_id = nTenant_id;
    }
}
