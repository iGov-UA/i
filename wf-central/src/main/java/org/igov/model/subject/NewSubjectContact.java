package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 13:34
 */
@javax.persistence.Entity
public class NewSubjectContact extends Entity {

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

    public void setsDate(DateTime sDate) {
        this.sDate = sDate;
    }
}
