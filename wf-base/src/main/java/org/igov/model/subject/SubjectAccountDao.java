package org.igov.model.subject;

import java.util.List;

import org.igov.model.core.EntityDao;

public interface SubjectAccountDao extends EntityDao<Long, SubjectAccount> {
    List<SubjectAccount> findSubjectAccounts(Long nID_Subject, String sLogin, Long nID_Server,
	    Long nID_SubjectAccountType);
    SubjectAccount setSubjectAccount(Long nID, Long nID_Subject, String sLogin, Long nID_Server,
	    Long nID_SubjectAccountType, String sNote);
}
