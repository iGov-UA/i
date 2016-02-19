package org.igov.model.subject;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.igov.model.core.Entity;

@javax.persistence.Entity
public class SubjectAccount extends Entity {

    @Column
    private String sLogin;

    @Column
    private String sNote;

    @ManyToOne
    @JoinColumn(name = "nID_SubjectAccountType")
    private SubjectAccountType subjectAccountType;

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

    public void setSubjectAccountType(SubjectAccountType subjectAccountType) {
	this.subjectAccountType = subjectAccountType;
    }

    public Long getnID_Subject() {
	return nID_Subject;
    }

    public void setnID_Subject(Long nID_Subject) {
	this.nID_Subject = nID_Subject;
    }
}
