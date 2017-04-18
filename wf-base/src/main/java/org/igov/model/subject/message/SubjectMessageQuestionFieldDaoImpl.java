package org.igov.model.subject.message;

import org.igov.model.core.GenericEntityDao;


public class SubjectMessageQuestionFieldDaoImpl extends GenericEntityDao<Long, SubjectMessageQuestionField> implements SubjectMessageQuestionFieldDao  {

	public SubjectMessageQuestionFieldDaoImpl() {
		super(SubjectMessageQuestionField.class);
		
	}

}
