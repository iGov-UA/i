package org.igov.model.subject;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class SubjectAccount extends AbstractEntity {

    @Column
    private String sLogin;

    @Column
    private String sNote;

    @JsonProperty(value = "subjectAccountType")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_SubjectAccountType", insertable = false, updatable = false)
    private SubjectAccountType subjectAccountType;

    @JsonIgnore
    @Column(name = "nID_SubjectAccountType", nullable = true)
    private Long nID_SubjectAccountType;
    
    @Column
    private Long nID_Server;

    @Column
    private Long nID_Subject;

    public String getsLogin() {
	return sLogin;
    }

    public void setsLogin(String sLogin) {
	this.sLogin = sLogin;
    }

    public String getsNote() {
	return sNote;
    }

    public void setsNote(String sNote) {
	this.sNote = sNote;
    }

    public Long getnID_Server() {
	return nID_Server;
    }

    public void setnID_Server(Long nID_Server) {
	this.nID_Server = nID_Server;
    }

    public SubjectAccountType getSubjectAccountType() {
	return subjectAccountType;
    }

    public void setnID_SubjectAccountType(Long nID_SubjectAccountType) {
        this.nID_SubjectAccountType = nID_SubjectAccountType;
    }

    public Long getnID_Subject() {
	return nID_Subject;
    }

    public void setnID_Subject(Long nID_Subject) {
	this.nID_Subject = nID_Subject;
    }
}
