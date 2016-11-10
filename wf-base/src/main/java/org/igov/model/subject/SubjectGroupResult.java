package org.igov.model.subject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SubjectGroupResult implements IVisitable {
	
	private static final Log LOG = LogFactory.getLog(SubjectGroupResult.class);

	
	private String nameGroupActiviti;


	private List<ParentSubjectGroup> parentSubjectGroups = new ArrayList<>();
	
	

	public SubjectGroupResult(String nameGroupActiviti) {
		this.nameGroupActiviti = nameGroupActiviti;
	}

	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
			for (ParentSubjectGroup c : parentSubjectGroups) {
				c.accept(visitor);
			}

	}

	public List<ParentSubjectGroup> getParentSubjectGroups() {
		return parentSubjectGroups;
	}

	public void setParentSubjectGroups(List<ParentSubjectGroup> parentSubjectGroups) {
		this.parentSubjectGroups = parentSubjectGroups;
	}

	
	public String getNameGroupActiviti() {
		return nameGroupActiviti;
	}

	public void setNameGroupActiviti(String nameGroupActiviti) {
		this.nameGroupActiviti = nameGroupActiviti;
	}

	public void addParentSubjectGroup(ParentSubjectGroup parentSubjectGroup) {
		parentSubjectGroups.add(parentSubjectGroup);
	}

	@Override
	public String toString() {
		return "SubjectGroupResult [nameGroupActiviti=" + nameGroupActiviti + ", parentSubjectGroups="
				+ parentSubjectGroups + "]";
	}
	
	

}