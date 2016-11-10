package org.igov.model.subject;
import java.util.ArrayList;
import java.util.List;

public class ParentSubjectGroup implements IVisitable {
	
	private SubjectGroup parentSubjectGroup;
	
	
	
	public ParentSubjectGroup(SubjectGroup parentSubjectGroup) {
		this.parentSubjectGroup = parentSubjectGroup;
	}



	private List<ChildSubjectGroup> childSubjectGroups = new ArrayList<>();

	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		 for (ChildSubjectGroup c : childSubjectGroups) {
	            c.accept(visitor);
	        }
		
	}
	
	
	public void addChildSubjectGroup(ChildSubjectGroup childSubjectGroup) {
		childSubjectGroups.add(childSubjectGroup);
	 }


	public List<ChildSubjectGroup> getChildSubjectGroups() {
		return childSubjectGroups;
	}


	public void setChildSubjectGroups(List<ChildSubjectGroup> childSubjectGroups) {
		this.childSubjectGroups = childSubjectGroups;
	}


	public SubjectGroup getParentSubjectGroup() {
		return parentSubjectGroup;
	}


	public void setParentSubjectGroup(SubjectGroup parentSubjectGroup) {
		this.parentSubjectGroup = parentSubjectGroup;
	}


	
	
	
}