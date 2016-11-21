package org.igov.model.subject.message;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface SubjectMessageFeedbackDao extends EntityDao<Long, SubjectMessageFeedback> {

    SubjectMessageFeedback save(SubjectMessageFeedback subjectMessageFeedback);

    SubjectMessageFeedback getSubjectMessageFeedbackById(Long nId);

    List<SubjectMessageFeedback> getAllSubjectMessageFeedbackBynID_Service(Long nID_service);

    List<SubjectMessageFeedback> getAllSubjectMessageFeedback_Filtered(Long nID_service, Long nID__LessThen_Filter, Integer nRowsMax);
    
    SubjectMessageFeedback update(SubjectMessageFeedback subjectMessageFeedback);
    
    SubjectMessageFeedback findByOrder(String sID_Order);
}
