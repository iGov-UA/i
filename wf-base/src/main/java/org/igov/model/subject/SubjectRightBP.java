package org.igov.model.subject;

import javax.persistence.Column;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonProperty;



@javax.persistence.Entity
public class SubjectRightBP extends AbstractEntity {

    @JsonProperty(value = "sID_BP")
    @Column (name="sID_BP", nullable = false)
    private String sID_BP;
    
    @JsonProperty(value = "sID_Place_UA")
    @Column (name="sID_Place_UA", nullable = true)
    private String sID_Place_UA;
    
    @JsonProperty(value = "sID_Group")
    @Column(name="sID_Group", nullable = false)
    private String sID_Group;
    

        
    public SubjectRightBP() {
		
	}

	public String getsID_BP() {
		return sID_BP;
	}

	public void setsID_BP(String sID_BP) {
		this.sID_BP = sID_BP;
	}

	public String getsID_Place_UA() {
		return sID_Place_UA;
	}

	public void setsID_Place_UA(String sID_Place_UA) {
		this.sID_Place_UA = sID_Place_UA;
	}

	public String getsID_Group() {
		return sID_Group;
	}

	public void setsID_Group(String sID_Group) {
		this.sID_Group = sID_Group;
	}
	    
}
