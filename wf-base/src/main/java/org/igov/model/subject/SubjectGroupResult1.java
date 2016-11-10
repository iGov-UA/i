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
public class SubjectGroupResult1 implements Serializable {
    
    List<SubjectGroupNode> rootSubjectNodes = new ArrayList<>();
    
	public SubjectGroupResult1(List<SubjectGroupNode> rootSubjectNodes) {
		this.rootSubjectNodes = rootSubjectNodes;
	}

	public List<SubjectGroupNode> getRootSubjectNodes() {
		return rootSubjectNodes;
	}

	public void setRootSubjectNodes(List<SubjectGroupNode> rootSubjectNodes) {
		this.rootSubjectNodes = rootSubjectNodes;
	}

	@Override
	public String toString() {
		return "SubjectGroupResult [rootSubjectNodes=" + rootSubjectNodes + "]";
	}
    
    
}
