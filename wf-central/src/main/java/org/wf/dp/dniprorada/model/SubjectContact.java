package org.wf.dp.dniprorada.model;

import org.wf.dp.dniprorada.base.model.Entity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 13:34
 */
@javax.persistence.Entity
public class SubjectContact extends Entity {

    @ManyToOne
    @JoinColumn(name = "nID_Subject")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "nID_SubjectContactType ")
    private SubjectContactType subjectContactType;

    @Column
    private String sValue;

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
}
