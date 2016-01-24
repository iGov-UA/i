package org.igov.model.subject.organ;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * Created by D.Zh. on 05.12.15.
 * dmitry.zhuravlyov@yahoo.com
 */
@javax.persistence.Entity
public class SubjectOrganJoinAttribute extends Entity {

    @Column(name = "nID_SubjectOrganJoin", length = 30, unique = false)
    private Long subjectOrganJoinId;

    @JsonProperty(value = "sName")
    @Column(name = "sName", length = 15, unique = false)
    private String name;

    @JsonProperty(value = "sValue")
    @Column(name = "sValue", length = 150, unique = false)
    private String value;

    public Long getSubjectOrganJoinId() {
        return subjectOrganJoinId;
    }

    public void setSubjectOrganJoinId(Long subjectOrganJoinId) {
        this.subjectOrganJoinId = subjectOrganJoinId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SubjectOrganJoinAttribute{" +
                "subjectOrganJoinId=" + subjectOrganJoinId +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}