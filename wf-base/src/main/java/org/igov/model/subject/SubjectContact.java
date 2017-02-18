package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;

import java.util.LinkedList;
import java.util.List;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 13:34
 */
@javax.persistence.Entity
public class SubjectContact extends AbstractEntity {

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "nID_Subject")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "nID_SubjectContactType")
    private SubjectContactType subjectContactType;

    @Column
    private String sValue;
    
    @JsonProperty(value="sDate")
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
    @Type(type=DATETIME_TYPE)
    @Column(name="sDate")
    private DateTime sDate;
   
public static NewSubjectContact getNewSubjectContact(SubjectContact subjectContact){
    NewSubjectContact newSubjectContact = new NewSubjectContact();
    newSubjectContact.setsValue(subjectContact.getsValue());
    newSubjectContact.setoSubjectContactType(subjectContact.getSubjectContactType());
    newSubjectContact.setId(subjectContact.getId());
    newSubjectContact.setsDate(subjectContact.getsDate());
    return  newSubjectContact;
}
    public static List<NewSubjectContact> getNewSubjectContact(List<SubjectContact> subjectContactList){
        List<NewSubjectContact> newSubjectContactList = new LinkedList<>();
        for (SubjectContact subjectContact : subjectContactList) {
            NewSubjectContact newSubjectContact = getNewSubjectContact(subjectContact);
            newSubjectContactList.add(newSubjectContact);
        }
      return  newSubjectContactList;
}
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public SubjectContactType getSubjectContactType() {
        return subjectContactType;
    }

    public void setSubjectContactType(SubjectContactType subjectContactType) {
        this.subjectContactType = subjectContactType;
    }

    public String getsValue() {
        return sValue;
    }

    public void setsValue(String sValue) {
        this.sValue = sValue;
    }

    public DateTime getsDate() {
        return sDate;
    }

    public void setsDate() {
        this.sDate = new DateTime();
    }
    
    
}
