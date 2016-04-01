package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.igov.model.core.Entity;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.joda.time.DateTime;

/**
 * User: lyashenkoGS
 * Date: 31.03.2016
 * Time: 21:40
 */
public class NewSubjectContact extends Entity {

    private SubjectContactType subjectContactType;

    private String sValue;

    @JsonProperty(value="sDate")
    @JsonSerialize(using=JsonDateTimeSerializer.class)
    @JsonDeserialize(using=JsonDateTimeDeserializer.class)
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
