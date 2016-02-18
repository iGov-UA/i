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
    private Long nID_SubjectHuman;
    
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
    public Long getnID_SubjectHuman() {
        return nID_SubjectHuman;
    }
    public void setnID_SubjectHuman(Long nID_SubjectHuman) {
        this.nID_SubjectHuman = nID_SubjectHuman;
    }

    public SubjectAccountType getSubjectAccountType() {
        return subjectAccountType;
    }
    public void setSubjectAccountType(SubjectAccountType subjectAccountType) {
        this.subjectAccountType = subjectAccountType;
    }
    
}
