package org.igov.model.action.execute.item;

import javax.persistence.Column;

import org.igov.model.core.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class ActionExecuteStatus extends Entity {

	@JsonProperty(value = "sID")
	@Column(name = "sID", nullable = false)
	private String sID;
	
	@JsonProperty(value = "sName")
	@Column(name = "sName", nullable = false)
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
