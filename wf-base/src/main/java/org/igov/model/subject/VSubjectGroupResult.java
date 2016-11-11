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
public class VSubjectGroupResult implements Serializable,IVisitable {
    
    List<VSubjectGroupNode> rootSubjectNodes = new ArrayList<>();
    
	public List<VSubjectGroupNode> getRootSubjectNodes() {
		return rootSubjectNodes;
	}

	public void setRootSubjectNodes(List<VSubjectGroupNode> rootSubjectNodes) {
		this.rootSubjectNodes = rootSubjectNodes;
	}

	public void addChild(VSubjectGroupNode rootSubjectNode) {
		rootSubjectNodes.add(rootSubjectNode);
    }

	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		for(VSubjectGroupNode subjectGroupNode:rootSubjectNodes) {
			subjectGroupNode.accept(visitor);
		}
		
	}
	
	@Override
	public String toString() {
		return "VSubjectGroupResult [rootSubjectNodes=" + rootSubjectNodes + "]";
	}
    
    
}
