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
public class SubjectGroupTreeResult implements Serializable {

	private SubjectGroup subjectGroup_Root;
	private List<SubjectGroup> subjectGroup_Child = new ArrayList<>();

	public SubjectGroup getSubjectGroup_Root() {
		return subjectGroup_Root;
	}

	public void setSubjectGroup_Root(SubjectGroup subjectGroup_Root) {
		this.subjectGroup_Root = subjectGroup_Root;
	}

	public List<SubjectGroup> getSubjectGroup_Child() {
		return subjectGroup_Child;
	}

	public void setSubjectGroup_Child(List<SubjectGroup> subjectGroup_Child) {
		this.subjectGroup_Child = subjectGroup_Child;
	}

	public void addChild(SubjectGroup child) {
		subjectGroup_Child.add(child);
	}

}
