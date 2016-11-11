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
public class VSubjectGroupResultNode implements Serializable, IVisitable {

	private SubjectGroup group;
	private List<VSubjectGroupParentNode> children = new ArrayList<>();

	public void addChild(VSubjectGroupParentNode parent) {
		children.add(parent);
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		for (VSubjectGroupParentNode vSubjectGroupParentNode : children) {
			vSubjectGroupParentNode.accept(visitor);
		}

	}

	public SubjectGroup getGroup() {
		return group;
	}

	public void setGroup(SubjectGroup group) {
		this.group = group;
	}

	public List<VSubjectGroupParentNode> getChildren() {
		return children;
	}

	public void setChildren(List<VSubjectGroupParentNode> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "VSubjectGroupParentNode [rootSubjectNodes=" + children + "]";
	}

}
