package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;
import org.igov.model.subject.organ.SubjectOrgan;

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

    public static NewSubjectAccount getNewSubjectAccount(boolean bSkipDetails,Subject subject, String login, SubjectHuman subjectHuman,
            SubjectOrgan subjectOrgan) {
        NewSubject newSubject = new NewSubject();
        newSubject.setsLabelShort(subject.getsLabelShort());
        newSubject.setsLabel(subject.getsLabel());
        newSubject.setsID(subject.getsID());

        List<SubjectContact> subjectContactList = subject.getaSubjectAccountContact();
        newSubject.setaSubjectAccountContact(SubjectContact.getNewSubjectContact(subjectContactList));

        if(!bSkipDetails){
        if(subjectHuman!=null){
        newSubject.setoSubjectHuman(SubjectHuman.getNewSubjectHuman(subjectHuman));}
        if(subjectOrgan!=null){
        newSubject.setoSubjectOrgan(SubjectOrgan.getNewSubjectOrgan(subjectOrgan));}}

        NewSubjectAccount newSubjectAccount = new NewSubjectAccount();
        newSubjectAccount.setsLogin(login);
        newSubjectAccount.setoSubject(newSubject);
        return newSubjectAccount;
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
