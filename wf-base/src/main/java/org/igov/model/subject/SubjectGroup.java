/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.igov.model.core.NamedEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class SubjectGroup extends NamedEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty(value = "sID_Group_Activiti")
    @Column
    private String sID_Group_Activiti;

    @JsonProperty(value = "sChain")
    @Column
    private String sChain;

    @JsonProperty(value = "aUser")
    @Transient
    private List<SubjectUser> aUser;

    @JsonProperty(value = "aSubjectGroupChilds")
    @Transient
    private List<SubjectGroup> aSubjectGroup;
    
    @JsonProperty(value = "oSubject")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Subject")
    private Subject oSubject;
    
    @JsonProperty(value = "oSubjectHumanPositionCustom")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_SubjectHumanPositionCustom")
    private SubjectHumanPositionCustom oSubjectHumanPositionCustom;

    public List<SubjectUser> getaUser() {
        return aUser;
    }

    public void setaUser(List<SubjectUser> aUser) {
        this.aUser = aUser;
    }

    public List<SubjectGroup> getaSubjectGroup() {
        return aSubjectGroup;
    }

    public void setaSubjectGroup(List<SubjectGroup> aSubjectGroup) {
        this.aSubjectGroup = aSubjectGroup;
    }

    public String getsID_Group_Activiti() {
        return sID_Group_Activiti;
    }

    public void setsID_Group_Activiti(String sID_Group_Activiti) {
        this.sID_Group_Activiti = sID_Group_Activiti;
    }

    public String getsChain() {
        return sChain;
    }

    public void setsChain(String sChain) {
        this.sChain = sChain;
    }

    public SubjectHumanPositionCustom getoSubjectHumanPositionCustom() {
        return oSubjectHumanPositionCustom;
    }

    public void setoSubjectHumanPositionCustom(SubjectHumanPositionCustom oSubjectHumanPositionCustom) {
        this.oSubjectHumanPositionCustom = oSubjectHumanPositionCustom;
    }

    public Subject getoSubject() {
		return oSubject;
	}

	public void setoSubject(Subject oSubject) {
		this.oSubject = oSubject;
	}

	@Override
    public String toString() {
        return "SubjectGroup [sID_Group_Activiti=" + sID_Group_Activiti + ", sChain=" + sChain + ", getName()="
                + getName() + ", nID=" + getId() + ", nID_SubjectHumanPositionCustom=" + oSubjectHumanPositionCustom.getId() + "]";
    }

}
