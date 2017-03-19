package org.igov.model.subject.organ;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Composite id of {@link SubjectOrganJoinAttribute} <br>
 * User: goodg_000
 * Date: 30.04.2016
 * Time: 18:04
 */
@Embeddable
public class SubjectOrganJoinAttributeId implements Serializable {

    @Column(name = "nID_SubjectOrganJoin", length = 30, unique = false)
    private Long subjectOrganJoinId;

    @Column(name = "sName", length = 15, unique = false)
    private String name;

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
}
