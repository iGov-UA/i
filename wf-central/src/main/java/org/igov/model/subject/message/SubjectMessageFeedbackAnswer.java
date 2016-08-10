package org.igov.model.subject.message;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.igov.model.core.AbstractEntity;

import javax.persistence.*;

@Entity
@Table(name = "SubjectMessageFeedbackAnswer")
public class SubjectMessageFeedbackAnswer extends AbstractEntity {

    @JsonProperty(value = "oSubjectMessage")
    @OneToOne
    @JoinColumn(name = "nID_SubjectMessage", nullable = true)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private SubjectMessage oSubjectMessage;

    @JsonProperty(value = "oSubjectMessageFeedback")
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "nID_SubjectMessageFeedback", nullable = true)
    private SubjectMessageFeedback oSubjectMessageFeedback;

    @JsonProperty("bSelf")
    @Column
    boolean bSelf;

    public SubjectMessage getoSubjectMessage() {
        return oSubjectMessage;
    }

    public void setoSubjectMessage(SubjectMessage oSubjectMessage) {
        this.oSubjectMessage = oSubjectMessage;
    }

    public SubjectMessageFeedback getoSubjectMessageFeedback() {
        return oSubjectMessageFeedback;
    }

    public void setoSubjectMessageFeedback(SubjectMessageFeedback oSubjectMessageFeedback) {
        this.oSubjectMessageFeedback = oSubjectMessageFeedback;
    }

    public boolean isbSelf() {
        return bSelf;
    }

    public void setbSelf(boolean bSelf) {
        this.bSelf = bSelf;
    }


}
