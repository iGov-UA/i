package org.igov.model.subject.message;

import com.google.common.base.Optional;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubjectMessageFeedbackAnswerDaoImpl extends GenericEntityDao<Long, SubjectMessageFeedbackAnswer> implements SubjectMessageFeedbackAnswerDao {

    protected SubjectMessageFeedbackAnswerDaoImpl() {
        super(SubjectMessageFeedbackAnswer.class);
    }

    @Override
    public SubjectMessageFeedbackAnswer save(SubjectMessageFeedbackAnswer subjectMessageFeedback) {
        getSession().save(subjectMessageFeedback);
        return subjectMessageFeedback;
    }


    @Override
    public SubjectMessageFeedbackAnswer getSubjectMessageFeedbackAnswerById(Long nId) {
        Optional<SubjectMessageFeedbackAnswer> feedback = findById(nId);
        if (feedback.isPresent()) {
          return feedback.get();
        }
        return null;
    }

    @Override
    public SubjectMessageFeedbackAnswer update(SubjectMessageFeedbackAnswer subjectMessageFeedback) {
        getSession().update(subjectMessageFeedback);
        return subjectMessageFeedback;
    }
}
