package org.igov.model.subject;
public interface IVisitor {
	 public void deepLevel(SubjectGroupResult subjectGroupResult);
	
    public void deepLevel(ParentSubjectGroup parentSubjectGroup);

    public void deepLevel(ChildSubjectGroup childSubjectGroup);
}