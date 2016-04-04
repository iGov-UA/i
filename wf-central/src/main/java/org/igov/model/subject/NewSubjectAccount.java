package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import org.igov.model.subject.organ.SubjectOrgan;

/**
 * User: lyashenkoGS Date: 31.03.2016 Time: 21:40
 */
public class NewSubjectAccount {

    @JsonProperty(value = "sLogin")
    private String sLogin;

    @JsonProperty(value = "oSubject")
    private NewSubject oSubject;

    public NewSubject getoSubject() {
        return oSubject;
    }

    public void setoSubject(NewSubject oSubject) {
        this.oSubject = oSubject;
    }

    public String getsLogin() {
        return sLogin;
    }

    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public static NewSubjectAccount getNewSubjectAccount(boolean bSkipDetails, Subject subject, String login, SubjectHuman subjectHuman,
            SubjectOrgan subjectOrgan) {
        
        NewSubject newSubject = new NewSubject();
        newSubject.setsLabelShort(subject.getsLabelShort());
        newSubject.setsLabel(subject.getsLabel());
        newSubject.setsID(subject.getsID());

        List<SubjectContact> subjectContactList = subject.getaSubjectAccountContact();
        newSubject.setaSubjectAccountContact(SubjectContact.getNewSubjectContact(subjectContactList));

        if (!bSkipDetails) {
            if (subjectHuman != null) {
                newSubject.setoSubjectHuman(SubjectHuman.getNewSubjectHuman(subjectHuman));
            }
            if (subjectOrgan != null) {
                newSubject.setoSubjectOrgan(SubjectOrgan.getNewSubjectOrgan(subjectOrgan));
            }
        }

        NewSubjectAccount newSubjectAccount = new NewSubjectAccount();
        newSubjectAccount.setsLogin(login);
        newSubjectAccount.setoSubject(newSubject);
        return newSubjectAccount;
    }
}
