package org.igov.model.action.task.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * @author NickVeremeichyk
 * @since 2016-02-07.
 */
@javax.persistence.Entity
public class ActionTaskLinkType extends Entity {

    @JsonProperty(value = "sID")
    @Column(name = "sID", nullable = true, unique = true)
    private String sId;

    @JsonProperty(value = "sName")
    @Column(name = "sName", nullable = true)
    private String sName;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    @Override
    public String toString() {
        return "ActionTaskLinkType{nID=" + getId() + ", "
                + "sID='" + getsId() + "\'" + ", "
                + "sName='" + getsName() + "\'" + "}";

    }

    @Override
    public int hashCode() {
        int result = getsId() != null ? getsId().hashCode() : 0;
        result = 31 * result + (getsName() != null ? getsName().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ActionTaskLinkType actionTaskLinkType = (ActionTaskLinkType) obj;

        if (getsId() != null ? !getsId().equals(actionTaskLinkType.getsId()) : actionTaskLinkType.getsId() != null)
            return false;
        if (getsName() != null ? !getsName().equals(actionTaskLinkType.getsName()) : actionTaskLinkType.getsName() != null)
            return false;
        return true;
    }
}
