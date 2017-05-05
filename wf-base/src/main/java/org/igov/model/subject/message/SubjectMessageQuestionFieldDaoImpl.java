package org.igov.model.subject.message;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectMessageQuestionFieldDaoImpl extends GenericEntityDao<Long, SubjectMessageQuestionField> implements SubjectMessageQuestionFieldDao  {

	public SubjectMessageQuestionFieldDaoImpl() {
		super(SubjectMessageQuestionField.class);
		
	}

}
