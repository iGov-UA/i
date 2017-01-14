package org.igov.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import org.igov.model.core.NamedEntity;

@javax.persistence.Entity
public class ProcessSubjectStatus extends NamedEntity {

    @JsonProperty(value = "sID")
    @Column
    private String sID;

    private static final long serialVersionUID = 1L;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

}
