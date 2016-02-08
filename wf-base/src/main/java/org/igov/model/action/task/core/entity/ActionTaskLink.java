package org.igov.model.action.task.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * @author NickVeremeichyk
 * @since 2016-02-07.
 */
@javax.persistence.Entity
public class ActionTaskLink extends Entity {

    @JsonProperty(value = "nID_Process")
    @Column(name = "nID_Process", nullable = false)
    private Long nIdProcess;

    @JsonProperty(value = "sKey")
    @Column(name = "sKey", nullable = false)
    private String sKey;

    @JsonProperty(value = "nID_Subject_Holder")
    @Column(name = "nID_Subject_Holder", nullable = false)
    private Long nIdSubjectHolder;

    public Long getnIdProcess() {
        return nIdProcess;
    }

    public void setnIdProcess(Long nIdProcess) {
        this.nIdProcess = nIdProcess;
    }

    public String getsKey() {
        return sKey;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public Long getnIdSubjectHolder() {
        return nIdSubjectHolder;
    }

    public void setnIdSubjectHolder(Long nIdSubjectHolder) {
        this.nIdSubjectHolder = nIdSubjectHolder;
    }

    @Override
    public String toString() {
        return "ActionTaskLink{nID=" + getId() + ", "
                + "nID_Process='" + getnIdProcess() + "\'" + ", "
                + "sKey='" + getsKey() + "\'"
                + "nID_Subject_Holder='" + getnIdSubjectHolder() + "\'" + "}";

    }

    @Override
    public int hashCode() {
        int result = getnIdProcess() != null ? getnIdProcess().hashCode() : 0;
        result = 31 * result + (getsKey() != null ? getsKey().hashCode() : 0);
        result = 31 * result + (getnIdSubjectHolder() != null ? getnIdSubjectHolder().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ActionTaskLink actionTaskLink = (ActionTaskLink) obj;

        if (getnIdProcess() != null ? !getnIdProcess().equals(actionTaskLink.getnIdProcess()) : actionTaskLink.getnIdProcess() != null)
            return false;
        if (getsKey() != null ? !getsKey().equals(actionTaskLink.getsKey()) : actionTaskLink.getsKey() != null)
            return false;
        if (getnIdSubjectHolder() != null ? !getnIdSubjectHolder().equals(actionTaskLink.getnIdSubjectHolder()) : actionTaskLink.getnIdSubjectHolder() != null)
            return false;
        return true;
    }

}
