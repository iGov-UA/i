package org.igov.model.subject.message;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface SubjectMessageFeedbackDao extends EntityDao<Long, SubjectMessageFeedback> {

    void setMessage(SubjectMessageFeedback subjectMessageFeedback);

    SubjectMessageFeedback getFeedbackExternalById(Long nId);

    List<SubjectMessageFeedback> getAllSubjectMessageFeedback();
}
