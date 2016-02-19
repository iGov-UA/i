package org.igov.model.subject;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectAccountDaoImpl extends GenericEntityDao<SubjectAccount> implements SubjectAccountDao {

    public SubjectAccountDaoImpl() {
	super(SubjectAccount.class);
    }

    /**
     * Author: kr110666kai
     * 
     * Возврат списка Аккаунтов согласно кретериям выборки 
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SubjectAccount> findSubjectAccounts(Long nID_Subject, String sLogin, Long nID_Server,
	    SubjectAccountType nID_SubjectAccountType) {
	if (nID_Subject == null && sLogin == null) {
	    return null;
	}

	Criteria oCriteria = getSession().createCriteria(SubjectAccount.class);

	if (nID_Subject != null) {
	    oCriteria.add(Restrictions.eq("nID_Subject", nID_Subject));
	} else {
	    oCriteria.add(Restrictions.eq("sLogin", sLogin));
	}
	
	if (nID_Server != null) {
	    oCriteria.add(Restrictions.eq("nID_Server", nID_Server));
	}

	if (nID_SubjectAccountType != null) {
	    oCriteria.add(Restrictions.eq("subjectAccountType", nID_SubjectAccountType));
	}

	List<SubjectAccount> subjectAccounts =  oCriteria.list();
	
	return subjectAccounts;
    }

}
