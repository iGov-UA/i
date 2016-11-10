package org.igov.model.subject;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChildSubjectGroup implements IVisitable {
	
	private static final Log LOG = LogFactory.getLog(ChildSubjectGroup.class);
	
	private SubjectGroup childSubjectGroup;
	
	private Long deepLevel;
	
	private List<ChildSubjectGroup> childrens = new ArrayList<>();
	
	
	public ChildSubjectGroup(SubjectGroup childSubjectGroup,Long deepLevel) {
		this.childSubjectGroup = childSubjectGroup;
		this.deepLevel = deepLevel;
	}


	@Override
	public void accept(IVisitor visitor) {
		visitor.deepLevel(this);
		if (SubjectGroupTreeResult.getDeepLevelSubjectGroupResult().compareTo(getDeepLevel()) != 0
				&& SubjectGroupTreeResult.getDeepLevelSubjectGroupResult().compareTo(getDeepLevel()) < 0) {
			LOG.info("SubjectGroupTreeResult.getDeepLevelSubjectGroupResulttttttttttttttttt "+SubjectGroupTreeResult.getDeepLevelSubjectGroupResult());
			LOG.info("getDeepLevellllllllllllllllllll "+getDeepLevel());
		for(ChildSubjectGroup childSubjectGroup:childrens) {
			LOG.info("ChildSubjectGroupppppppppppp "+childSubjectGroup.childSubjectGroup);
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


	public SubjectGroup getChildSubjectGroup() {
		return childSubjectGroup;
	}


	public void setChildSubjectGroup(SubjectGroup childSubjectGroup) {
		this.childSubjectGroup = childSubjectGroup;
	}


}
