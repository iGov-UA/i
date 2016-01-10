package org.igov.model.subject.message;

import org.igov.model.core.EntityDao;

import java.util.List;

public interface SubjectMessagesDao extends EntityDao<SubjectMessage> {

    void setMessage(SubjectMessage message);

    List<SubjectMessage> getMessages();

    List<SubjectMessage> getMessages(Long nID_HistoryEvent_Service);
    
    SubjectMessage getMessage(Long nID);
    
    List tranferDataFromMailToSubjectMail();
}
