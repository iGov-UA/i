package org.igov.model.subject.message;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.*;
import org.igov.model.core.AbstractEntity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;


@Entity
@Table(name = "SubjectMessageFeedback")
public class SubjectMessageFeedback extends AbstractEntity {

    @JsonProperty(value = "sID_Source")
    @Column(name = "sID_Source", nullable = false)
    private String sID_Source;

    @JsonProperty(value = "sAuthorFIO")
    @Column(name = "sAuthorFIO", nullable = false)
    private String sAuthorFIO;

    @JsonProperty(value = "sMail")
    @Column(name = "sMail", nullable = false)
    private String sMail;

    @JsonProperty(value = "sHead")
    @Column(name = "sHead", nullable = true)
    private String sHead;

    @JsonProperty(value = "sBody")
    @Column(name = "sBody", nullable = false)
    private String sBody;

    @JsonProperty(value = "sPlace")
    @Column(name = "sPlace", nullable = true)
    private String sPlace;

    @JsonProperty(value = "sEmployeeFIO")
    @Column(name = "sEmployeeFIO", nullable = true)
    private String sEmployeeFIO;

    @JsonProperty(value = "nID_Rate")
    @Column(name = "nID_Rate", nullable = false)
    private Long nID_Rate;

    @JsonProperty(value = "nID_Service")
    @Column(name = "nID_Service", nullable = false)
    private Long nID_Service;

    @JsonProperty(value = "sID_Token")
    @Column(name = "sID_Token", nullable = false)
    private String sID_Token;
    
    @JsonProperty(value = "sID_Order")
    @Column(name = "sID_Order", nullable = true)
    private String sID_Order;

    @JsonProperty(value = "sAnswer")
    @Column(name = "sAnswer", nullable = true)
    private String sAnswer;

    @JsonProperty(value = "oSubjectMessage")
    @OneToOne
    @JoinColumn(name = "nID_SubjectMessage", nullable = true)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private SubjectMessage oSubjectMessage;

    @JsonProperty(value = "oSubjectMessageFeedbackAnswers")
    @JsonManagedReference
    @OneToMany(mappedBy = "oSubjectMessageFeedback", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<SubjectMessageFeedbackAnswer> oSubjectMessageFeedbackAnswers;

    public List<SubjectMessageFeedbackAnswer> getoSubjectMessageFeedbackAnswers() {
        return oSubjectMessageFeedbackAnswers;
    }

    public void setoSubjectMessageFeedbackAnswers(
            List<SubjectMessageFeedbackAnswer> oSubjectMessageFeedbackAnswers) {
        this.oSubjectMessageFeedbackAnswers = oSubjectMessageFeedbackAnswers;
    }

    public SubjectMessage getoSubjectMessage() {
        return oSubjectMessage;
    }

    public void setoSubjectMessage(SubjectMessage oSubjectMessage) {
        this.oSubjectMessage = oSubjectMessage;
    }


    public String getsID_Source() {
        return sID_Source;
    }

    public void setsID_Source(String sID_Source) {
        this.sID_Source = sID_Source;
    }

    public String getsAuthorFIO() {
        return sAuthorFIO;
    }

    public void setsAuthorFIO(String sAuthorFIO) {
        this.sAuthorFIO = sAuthorFIO;
    }

    public String getsMail() {
        return sMail;
    }

    public void setsMail(String sMail) {
        this.sMail = sMail;
    }

    public String getsHead() {
        return sHead;
    }

    public void setsHead(String sHead) {
        this.sHead = sHead;
    }

    public String getsBody() {
        return sBody;
    }

    public void setsBody(String sBody) {
        this.sBody = sBody;
    }

    public String getsPlace() {
        return sPlace;
    }

    public void setsPlace(String sPlace) {
        this.sPlace = sPlace;
    }

    public String getsEmployeeFIO() {
        return sEmployeeFIO;
    }

    public void setsEmployeeFIO(String sEmployeeFIO) {
        this.sEmployeeFIO = sEmployeeFIO;
    }

    public Long getnID_Rate() {
        return nID_Rate;
    }

    public void setnID_Rate(Long nID_Rate) {
        this.nID_Rate = nID_Rate;
    }

    public Long getnID_Service() {
        return nID_Service;
    }

    public void setnID_Service(Long nID_Service) {
        this.nID_Service = nID_Service;
    }

    public String getsID_Token() {
        return sID_Token;
    }

    public void setsID_Token(String sID_Token) {
        this.sID_Token = sID_Token;
    }
    
    public String getsID_Order() {
        return sID_Order;
    }

    public void setsID_Order(String sID_Order) {
        this.sID_Order = sID_Order;
    }

    public String getsAnswer() {
        return sAnswer;
    }

    public void setsAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }
 }
