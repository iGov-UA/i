package org.igov.model.subject.message;

import com.google.common.base.Optional;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubjectMessageFeedbackDaoImpl extends GenericEntityDao<Long, SubjectMessageFeedback> implements SubjectMessageFeedbackDao {

    protected SubjectMessageFeedbackDaoImpl() {
        super(SubjectMessageFeedback.class);
    }

    @Override
    public SubjectMessageFeedback save(SubjectMessageFeedback subjectMessageFeedback) {
        getSession().save(subjectMessageFeedback);
        return subjectMessageFeedback;
    }

    @Override
    public SubjectMessageFeedback getSubjectMessageFeedbackById(Long nId) {
        Optional<SubjectMessageFeedback> feedback = findById(nId);
        if (feedback.isPresent()) {
          return feedback.get();
        }
        return null;
    }

    @Override
    public List<SubjectMessageFeedback> getAllSubjectMessageFeedbackBynID_Service(Long nID_service) {
        return findAllBy("nID_Service", nID_service);
    }

    @Override
    public SubjectMessageFeedback update(SubjectMessageFeedback subjectMessageFeedback) {
        getSession().update(subjectMessageFeedback);
        return subjectMessageFeedback;
    }
}
