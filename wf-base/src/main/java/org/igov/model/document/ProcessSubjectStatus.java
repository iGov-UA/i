package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.igov.model.core.NamedEntity;

@Entity
public class ProcessSubjectStatus extends NamedEntity {

    @JsonProperty(value = "nID")
    @Column
    private String nID;

    @OneToMany(targetEntity = ProcessSubject.class, mappedBy = "processSubjectStatus", cascade = CascadeType.ALL)
    private List<ProcessSubject> status;

    public List<ProcessSubject> getStatus() {
        return status;
    }

    public void setStatus(List<ProcessSubject> status) {
        this.status = status;
    }

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
    }

    @Override
    public String toString() {
        return "ProcessSubjectStatus{ "
                + "nID = " + nID
                + ", sName = " + getName()
                + "}";
    }

}
