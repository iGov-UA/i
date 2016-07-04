package org.igov.model.subject.message;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectMessageFeedbackDaoImpl extends GenericEntityDao<Long, SubjectMessageFeedback> implements SubjectMessageFeedbackDao {

    protected SubjectMessageFeedbackDaoImpl() {
        super(SubjectMessageFeedback.class);
    }

    @Override
    public void setMessage(SubjectMessageFeedback subjectMessageFeedback) {
        saveOrUpdate(subjectMessageFeedback);
    }
}
