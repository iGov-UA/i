package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import org.igov.model.subject.organ.SubjectOrgan;

/**
 * User: lyashenkoGS Date: 31.03.2016 Time: 21:40
 */
public class NewSubjectAccount {

    @JsonProperty(value = "sAccount")
    private String sAccount;

    @JsonProperty(value = "oSubject")
    private NewSubject oSubject;

    public NewSubject getoSubject() {
        return oSubject;
    }

    public void setoSubject(NewSubject oSubject) {
        this.oSubject = oSubject;
    }

    public String getsAccount() {
        return sAccount;
    }

    public void setsAccount(String sAccount) {
        this.sAccount = sAccount;
    }

    public static NewSubjectAccount getNewSubjectAccount(boolean bSkipDetails, Subject subject, String account, SubjectHuman subjectHuman,
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
        newSubjectAccount.setsAccount(account);
        newSubjectAccount.setoSubject(newSubject);
        return newSubjectAccount;
    }
}
