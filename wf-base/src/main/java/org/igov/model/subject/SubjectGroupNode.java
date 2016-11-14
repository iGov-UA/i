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
public class SubjectGroupNode implements Serializable {
    
	private SubjectGroup group;
    private List<SubjectGroupNode> children = new ArrayList<>();
    
    
    public SubjectGroupNode(SubjectGroup group) {
		this.group = group;
	}

	public SubjectGroup getGroup() {
		return group;
	}

	public void setGroup(SubjectGroup group) {
		this.group = group;
	}

	public List<SubjectGroupNode> getChildren() {
		return children;
	}

	public void setChildren(List<SubjectGroupNode> children) {
		this.children = children;
	}

	public void addChild(SubjectGroupNode childNode) {
        children.add(childNode);
    }
}
