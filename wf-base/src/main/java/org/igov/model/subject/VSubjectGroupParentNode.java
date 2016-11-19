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
public class VSubjectGroupParentNode implements Serializable {

	private SubjectGroup group;

	List<SubjectGroup> children = new ArrayList<>();


	public VSubjectGroupParentNode() {
	}

	public void addChild(SubjectGroup child) {
		children.add(child);
	}

	
	public SubjectGroup getGroup() {
		return group;
	}

	public void setGroup(SubjectGroup group) {
		this.group = group;
	}

	public List<SubjectGroup> getChildren() {
		return children;
	}

	public void setChildren(List<SubjectGroup> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "VSubjectGroupParentNode [children=" + children + "]";
	}

}
