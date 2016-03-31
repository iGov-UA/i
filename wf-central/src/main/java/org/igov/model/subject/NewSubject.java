package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class NewSubject {

    @JsonProperty(value="aSubjectHuman")
    private NewSubjectHuman aSubjectHuman;

    @JsonProperty(value = "aSubjectAccountContact")
    private transient List<NewSubjectContact> aSubjectAccountContact;

    public List<NewSubjectContact> getaSubjectAccountContact() {
        return aSubjectAccountContact;
    }

    public void setaSubjectAccountContact(List<NewSubjectContact> aSubjectAccountContact) {
        this.aSubjectAccountContact = aSubjectAccountContact;
    }

    public NewSubjectHuman getaSubjectHuman() {
        return aSubjectHuman;
    }

    public void setaSubjectHuman(NewSubjectHuman aSubjectHuman) {
        this.aSubjectHuman = aSubjectHuman;
    }
}
