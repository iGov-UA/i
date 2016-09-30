package org.igov.model.action.task.core.entity;

import javax.persistence.Column;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class ActionProcessCount extends AbstractEntity {

    @JsonProperty(value = "sID_BP")
    @Column(name = "sID_BP")
    private String sID_BP;

    @JsonProperty(value = "nID_Service")
    @Column(name = "nID_Service")
    private Integer nID_Service;

    @JsonProperty(value = "nCountYear")
    @Column(name = "nCountYear")
    private Integer nCountYear;

    @JsonProperty(value = "nYear")
    @Column(name = "nYear")
    private Integer nYear;

    public String getsID_BP() {
        return sID_BP;
    }

    public void setsID_BP(String sID_BP) {
        this.sID_BP = sID_BP;
    }

    public Integer getnID_Service() {
        return nID_Service;
    }

    public void setnID_Service(Integer nID_Service) {
        this.nID_Service = nID_Service;
    }

    public Integer getnCountYear() {
        return nCountYear;
    }

    public void setnCountYear(Integer nCountYear) {
        this.nCountYear = nCountYear;
    }

    public Integer getnYear() {
        return nYear;
    }

    public void setnYear(Integer nYear) {
        this.nYear = nYear;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((nCountYear == null) ? 0 : nCountYear.hashCode());
        result = prime * result
                + ((nID_Service == null) ? 0 : nID_Service.hashCode());
        result = prime * result + ((nYear == null) ? 0 : nYear.hashCode());
        result = prime * result + ((sID_BP == null) ? 0 : sID_BP.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionProcessCount other = (ActionProcessCount) obj;
        if (nCountYear == null) {
            if (other.nCountYear != null) {
                return false;
            }
        } else if (!nCountYear.equals(other.nCountYear)) {
            return false;
        }
        if (nID_Service == null) {
            if (other.nID_Service != null) {
                return false;
            }
        } else if (!nID_Service.equals(other.nID_Service)) {
            return false;
        }
        if (nYear == null) {
            if (other.nYear != null) {
                return false;
            }
        } else if (!nYear.equals(other.nYear)) {
            return false;
        }
        if (sID_BP == null) {
            if (other.sID_BP != null) {
                return false;
            }
        } else if (!sID_BP.equals(other.sID_BP)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ActionProcessCount [id=" + this.getId() + ", sID_BP=" + sID_BP + ", nID_Service="
                + nID_Service + ", nCountYear=" + nCountYear + ", nYear="
                + nYear + "]";
    }

}
