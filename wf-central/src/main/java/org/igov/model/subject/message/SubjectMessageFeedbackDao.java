package org.igov.model.subject.message;

import org.igov.model.core.EntityDao;

public interface SubjectMessageFeedbackDao extends EntityDao<Long, SubjectMessageFeedback> {

    void setMessage(SubjectMessageFeedback subjectMessageFeedback);

    SubjectMessageFeedback getFeedbackExternalById(Long nId);
}
