package org.igov.model.subject.message;

import com.google.common.base.Optional;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.exception.EntityNotFoundException;
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

    @Override
    public SubjectMessageFeedback getFeedbackExternalById(Long nId) {
        Optional<SubjectMessageFeedback> feedback = findById(nId);
        if (feedback.isPresent()) {
          return feedback.get();
        }
        return null;
    }
}
