/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author inna
 */
public class VSubjectGroupNode implements Serializable,IVisitable {
    
	private SubjectGroup oSubjectGroup_Root;
    private List<SubjectGroup> aSubjectGroup_Child = new ArrayList<>();
    
    
	public SubjectGroup getoSubjectGroup_Root() {
		return oSubjectGroup_Root;
	}
	public void setoSubjectGroup_Root(SubjectGroup oSubjectGroup_Root) {
		this.oSubjectGroup_Root = oSubjectGroup_Root;
	}
	public List<SubjectGroup> getaSubjectGroup_Child() {
		return aSubjectGroup_Child;
	}
	public void setaSubjectGroup_Child(List<SubjectGroup> aSubjectGroup_Child) {
		this.aSubjectGroup_Child = aSubjectGroup_Child;
	}
    
    public void addChild(SubjectGroup child) {
    	aSubjectGroup_Child.add(child);
    }
	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		for(SubjectGroup subjectGroup: aSubjectGroup_Child) {
			subjectGroup.accept(visitor);
		}
		
	}
	@Override
	public String toString() {
		return "VSubjectGroupNode [aSubjectGroup_Child=" + aSubjectGroup_Child + "]";
	}
	
}
