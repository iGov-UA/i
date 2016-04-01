package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

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

    public static NewSubject getNewSubject(Subject subject, String login){
        NewSubject newSubject = new NewSubject();
        newSubject.setaSubjectLogin(login);
        NewSubjectHuman newSubjectHuman = new NewSubjectHuman();
        newSubjectHuman.setsLabelShort(subject.sLabelShort);
        newSubjectHuman.setsLabel(subject.getsLabel());
        newSubjectHuman.setsID(subject.getsID());
        newSubject.setaSubjectHuman(newSubjectHuman);
        newSubject.setaSubjectAccountContact(Subject.getNewSubjectAccountContacts(subject));

        return newSubject;
    }
    private static List<NewSubjectContact> getNewSubjectAccountContacts(Subject subject){
        List<NewSubjectContact> newSubjectContactsList = new ArrayList<>();
        for (SubjectContact subjectContact : subject.getaSubjectAccountContact()) {
            NewSubjectContact newSubjectContact = new NewSubjectContact();
            newSubjectContact.setsDate(subjectContact.getsDate());
            newSubjectContact.setSubjectContactType(subjectContact.getSubjectContactType());
            newSubjectContact.setsValue(subjectContact.getsValue());
            newSubjectContact.setId(subject.getId());
            newSubjectContactsList.add(newSubjectContact);

        }
        return newSubjectContactsList;
    }

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
