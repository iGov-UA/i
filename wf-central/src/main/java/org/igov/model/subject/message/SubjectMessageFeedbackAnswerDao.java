package org.igov.model.subject.message;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface SubjectMessageFeedbackAnswerDao extends EntityDao<Long, SubjectMessageFeedbackAnswer> {

    SubjectMessageFeedbackAnswer save(SubjectMessageFeedbackAnswer subjectMessageFeedback);

    SubjectMessageFeedbackAnswer getSubjectMessageFeedbackAnswerById(Long nId);

    SubjectMessageFeedbackAnswer update(SubjectMessageFeedbackAnswer subjectMessageFeedback);
}
