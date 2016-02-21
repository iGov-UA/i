package org.igov.model.escalation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;

import javax.persistence.Column;
import org.igov.model.core.Entity;

/**
 * @author NickVeremeichyk
 * @since 2015-12-05.
 */
@javax.persistence.Entity
public class EscalationHistory extends Entity {
    /**
     * sDate - дата
     */
    @Column(name="sDate")
    @Type(type = DATETIME_TYPE)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @JsonProperty(value = "sDate")
    private DateTime sDate;

    /**
     * nID_Process - номер-ИД процесса
     */
    @Column(name = "nID_Process")
    @JsonProperty(value = "nID_Process")
    private Long nIdProcess;

    /**
     * nID_Process_Root - номер-ИД процесса (корневого, из-за которого запущена эскалация)
     */
    @Column(name = "nID_Process_Root")
    @JsonProperty(value = "nID_Process_Root")
    private Long nIdProcessRoot;

    /**
     * nID_UserTask - номер-ИД юзертаски
     */
    @Column(name = "nID_UserTask")
    @JsonProperty(value = "nID_UserTask")
    private Long nIdUserTask;

    /**
     * nID_EscalationStatus - номер-ИД статуса эскалации
     */
    @Column(name = "nID_EscalationStatus")
    @JsonProperty(value = "nID_EscalationStatus")
    private Long nIdEscalationStatus;

    public DateTime getsDate() {
        return sDate;
    }

    public void setsDate(DateTime sDate) {
        this.sDate = sDate;
    }

    public Long getnIdProcess() {
        return nIdProcess;
    }

    public void setnIdProcess(Long nIdProcess) {
        this.nIdProcess = nIdProcess;
    }

    public Long getnIdProcessRoot() {
        return nIdProcessRoot;
    }

    public void setnIdProcessRoot(Long nIdProcessRoot) {
        this.nIdProcessRoot = nIdProcessRoot;
    }

    public Long getnIdUserTask() {
        return nIdUserTask;
    }

    public void setnIdUserTask(Long nIdUserTask) {
        this.nIdUserTask = nIdUserTask;
    }

    public Long getnIdEscalationStatus() {
        return nIdEscalationStatus;
    }

    public void setnIdEscalationStatus(Long nIdEscalationStatus) {
        this.nIdEscalationStatus = nIdEscalationStatus;
    }

    @Override
    public String toString() {
        return "{" +
                "sDate=" + sDate +
                ", nId=" + getId() +
                ", nIdProcess=" + nIdProcess +
                ", nIdProcessRoot=" + nIdProcessRoot +
                ", nIdUserTask=" + nIdUserTask +
                ", nIdEscalationStatus=" + nIdEscalationStatus +
                "} ";
    }
}
