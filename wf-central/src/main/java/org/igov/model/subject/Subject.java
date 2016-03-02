package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.igov.model.core.Entity;

import javax.persistence.Column;

@javax.persistence.Entity
public class Subject extends Entity {

    @JsonProperty(value = "sID")
    @Column(name = "sID", nullable = true)
    private String sID;

    @JsonProperty(value = "sLabel")
    @Column(name = "sLabel", nullable = true)
    private String sLabel;

    @JsonProperty(value = "sLabelShort")
    @Column(name = "sLabelShort", nullable = true)
    private String sLabelShort;
    
    @JsonProperty(value = "aSubjectAccountContact")
    private transient List<SubjectContact> aSubjectAccountContact;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsLabel() {
        return sLabel;
    }

    public void setsLabel(String sLabel) {
        this.sLabel = sLabel;
    }

    public String getsLabelShort() {
        return sLabelShort;
    }

    public void setsLabelShort(String sLabelShort) {
        this.sLabelShort = sLabelShort;
    }

    public void setaSubjectAccountContact(List<SubjectContact> aSubjectAccountContact) {
        this.aSubjectAccountContact = aSubjectAccountContact;
    }

    public List<SubjectContact> getaSubjectAccountContact() {
        return aSubjectAccountContact;
    }
}
