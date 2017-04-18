package org.igov.model.subject.message;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
public class SubjectMessageQuestionField extends AbstractEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
    @ManyToOne(targetEntity = SubjectMessage.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_SubjectMessage")
    private SubjectMessage subjectMessage;

	@JsonProperty(value = "sID")
    @Column(name = "sID", nullable = false)
    private String sID;

    @JsonProperty(value = "sName")
    @Column(name = "sName", nullable = false)
    private String sName;

    @JsonProperty(value = "sType")
    @Column(name = "sType", nullable = false)
    private String sType;
    
    @JsonProperty(value = "sValue")
    @Column(name = "sValue", nullable = false)
    private String sValue;

    @JsonProperty(value = "sValueNew")
    @Column(name = "sValueNew", nullable = false)
    private String sValueNew; 
    
    @JsonProperty(value = "sNotify")
    @Column(name = "sNotify", nullable = true)
    private String sNotify;

	public SubjectMessage getSubjectMessage() {
		return subjectMessage;
	}

	public void setSubjectMessage(SubjectMessage subjectMessage) {
		this.subjectMessage = subjectMessage;
	}

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

	public String getsType() {
		return sType;
	}

	public void setsType(String sType) {
		this.sType = sType;
	}

	public String getsValue() {
		return sValue;
	}

	public void setsValue(String sValue) {
		this.sValue = sValue;
	}

	public String getsValueNew() {
		return sValueNew;
	}

	public void setsValueNew(String sValueNew) {
		this.sValueNew = sValueNew;
	}

	public String getsNotify() {
		return sNotify;
	}

	public void setsNotify(String sNotify) {
		this.sNotify = sNotify;
	}

}
