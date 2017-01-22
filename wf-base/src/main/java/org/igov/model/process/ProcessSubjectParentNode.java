/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author inna
 */
public class ProcessSubjectParentNode implements Serializable {

	private ProcessSubject group;

	private List<ProcessSubject> children = new ArrayList<>();


	public ProcessSubjectParentNode() {
	}

	public void addChild(ProcessSubject child) {
		children.add(child);
	}

	public ProcessSubject getGroup() {
		return group;
	}

	public void setGroup(ProcessSubject group) {
		this.group = group;
	}

	public List<ProcessSubject> getChildren() {
		return children;
	}

	public void setChildren(List<ProcessSubject> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "group=" + group + ", children=" + children;
	}


}
