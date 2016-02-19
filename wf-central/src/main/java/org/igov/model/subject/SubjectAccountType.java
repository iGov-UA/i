package org.igov.model.subject;

import javax.persistence.Column;

import org.igov.model.core.Entity;

@javax.persistence.Entity
public class SubjectAccountType extends Entity {
    
    @Column
    private String sID;
    
    @Column
    private String sName;

    public String getsID() {
	return sID;
    }

    public void setsID(String sID) {
	this.sID = sID;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

}
