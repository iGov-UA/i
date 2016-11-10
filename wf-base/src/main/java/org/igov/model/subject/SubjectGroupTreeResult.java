package org.igov.model.subject;

import java.io.Serializable;

public class SubjectGroupTreeResult implements Serializable,IVisitor  {

	private static Long deepLevelSubjectGroupResult = 0L;
	private static Long deepLevelParentSubjectGroup = 0L;
	private static Long deepLevelChildSubjectGroup = 0L;
	
	
	
	@Override
	public void deepLevel(SubjectGroupResult subjectGroupResult) {
		setDeepLevelSubjectGroupResult(deepLevelSubjectGroupResult+1);
		
	}

	@Override
	public void deepLevel(ParentSubjectGroup parentSubjectGroup) {
		setDeepLevelParentSubjectGroup(deepLevelParentSubjectGroup+1);
		
	}

	@Override
	public void deepLevel(ChildSubjectGroup childSubjectGroup) {
		setDeepLevelChildSubjectGroup(deepLevelChildSubjectGroup+1);
		
	}
	
	
	
	public static Long getDeepLevelSubjectGroupResult() {
		return deepLevelSubjectGroupResult;
	}

	public static void setDeepLevelSubjectGroupResult(Long deepLevelSubjectGroupResult) {
		SubjectGroupTreeResult.deepLevelSubjectGroupResult = deepLevelSubjectGroupResult;
	}

	public static Long getDeepLevelParentSubjectGroup() {
		return deepLevelParentSubjectGroup;
	}

	public static void setDeepLevelParentSubjectGroup(Long deepLevelParentSubjectGroup) {
		SubjectGroupTreeResult.deepLevelParentSubjectGroup = deepLevelParentSubjectGroup;
	}

	public static Long getDeepLevelChildSubjectGroup() {
		return deepLevelChildSubjectGroup;
	}

	public static void setDeepLevelChildSubjectGroup(Long deepLevelChildSubjectGroup) {
		SubjectGroupTreeResult.deepLevelChildSubjectGroup = deepLevelChildSubjectGroup;
	}

	

}
