package org.igov.model.subject.organ;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

/**
 * Created by D.Zh. on 05.12.15.
 * dmitry.zhuravlyov@yahoo.com
 */
@javax.persistence.Entity
public class SubjectOrganJoinAttribute implements Entity<SubjectOrganJoinAttributeId> {

    @EmbeddedId
    private SubjectOrganJoinAttributeId id = new SubjectOrganJoinAttributeId();

    @JsonProperty(value = "sValue")
    @Column(name = "sValue", length = 150, unique = false)
    private String value;

    @Override
    @JsonIgnore
    public SubjectOrganJoinAttributeId getId() {
        return id;
    }

    @Override
    public void setId(SubjectOrganJoinAttributeId id) {
        this.id = id;
    }

    public Long getSubjectOrganJoinId() {
        return id.getSubjectOrganJoinId();
    }

    public void setSubjectOrganJoinId(Long subjectOrganJoinId) {
        id.setSubjectOrganJoinId(subjectOrganJoinId);
    }

    @JsonProperty(value = "sName")
    public String getName() {
        return id.getName();
    }

    public void setName(String name) {
        id.setName(name);
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
                "subjectOrganJoinId=" + id.getSubjectOrganJoinId() +
                ", name='" + id.getName() + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}