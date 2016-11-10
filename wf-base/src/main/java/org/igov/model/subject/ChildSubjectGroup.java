package org.igov.model.subject;

import java.util.ArrayList;
import java.util.List;

public class ChildSubjectGroup implements IVisitable {
	private SubjectGroup nameChildSubjectGroup;
	
	private Long deepLevel;
	
	private List<ChildSubjectGroup> childrens = new ArrayList<>();
	
	
	public ChildSubjectGroup(SubjectGroup nameChildSubjectGroup,Long deepLevel) {
		this.nameChildSubjectGroup = nameChildSubjectGroup;
		this.deepLevel = deepLevel;
	}


	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		if (SubjectGroupTreeResult.getDeepLevelSubjectGroupResult().compareTo(getDeepLevel()) != 0
				&& SubjectGroupTreeResult.getDeepLevelSubjectGroupResult().compareTo(getDeepLevel()) < 0) {
		for(ChildSubjectGroup childSubjectGroup:childrens) {
			childSubjectGroup.accept(visitor);
		}
		}
		
	}


	public void addChildSubjectGroup(ChildSubjectGroup childSubjectGroup) {
		childrens.add(childSubjectGroup);
	 }


	public List<ChildSubjectGroup> getChildrens() {
		return childrens;
	}


	public void setChildrens(List<ChildSubjectGroup> childrens) {
		this.childrens = childrens;
	}
	
	public Long getDeepLevel() {
		return deepLevel;
	}


	public void setDeepLevel(Long deepLevel) {
		this.deepLevel = deepLevel;
	}


	public SubjectGroup getNameChildSubjectGroup() {
		return nameChildSubjectGroup;
	}


	public void setNameChildSubjectGroup(SubjectGroup nameChildSubjectGroup) {
		this.nameChildSubjectGroup = nameChildSubjectGroup;
	}
	
	
	
	
}
