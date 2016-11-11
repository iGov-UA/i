package org.igov.model.subject;
public interface IVisitor {
	 public void deepLevel(VSubjectGroupResult vSubjectGroupResult);
	 public void deepLevel(VSubjectGroupNode vSubjectGroupNode);
	 public void deepLevel(SubjectGroup subjectGroup);
	
}