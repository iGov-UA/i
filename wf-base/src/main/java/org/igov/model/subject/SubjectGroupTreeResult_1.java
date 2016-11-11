package org.igov.model.subject;

import java.io.Serializable;

public class SubjectGroupTreeResult_1 implements Serializable,IVisitor  {

	private static Long deepLevelSubjectGroupResult = 0L;
	private static Long deepLevelParentSubjectGroup = 0L;
	private static Long deepLevelChildSubjectGroup = 0L;
	
	
	
	@Override
	public void deepLevel(SubjectGroupResult_1 subjectGroupResult) {
		SubjectGroupTreeResult_1.setDeepLevelSubjectGroupResult(deepLevelSubjectGroupResult+1);
		
	}

	@Override
	public void deepLevel(ParentSubjectGroup parentSubjectGroup) {
		SubjectGroupTreeResult_1.setDeepLevelParentSubjectGroup(deepLevelParentSubjectGroup+1);
		
	}

	@Override
	public void deepLevel(ChildSubjectGroup childSubjectGroup) {
		SubjectGroupTreeResult_1.setDeepLevelChildSubjectGroup(deepLevelChildSubjectGroup+1);
		
	}
	
	
	
	public static Long getDeepLevelSubjectGroupResult() {
		return deepLevelSubjectGroupResult;
	}

	public static void setDeepLevelSubjectGroupResult(Long deepLevelSubjectGroupResult) {
		SubjectGroupTreeResult_1.deepLevelSubjectGroupResult = deepLevelSubjectGroupResult;
	}

	public static Long getDeepLevelParentSubjectGroup() {
		return deepLevelParentSubjectGroup;
	}

	public static void setDeepLevelParentSubjectGroup(Long deepLevelParentSubjectGroup) {
		SubjectGroupTreeResult_1.deepLevelParentSubjectGroup = deepLevelParentSubjectGroup;
	}

	public static Long getDeepLevelChildSubjectGroup() {
		return deepLevelChildSubjectGroup;
	}

	public static void setDeepLevelChildSubjectGroup(Long deepLevelChildSubjectGroup) {
		SubjectGroupTreeResult_1.deepLevelChildSubjectGroup = deepLevelChildSubjectGroup;
	}

	

}
