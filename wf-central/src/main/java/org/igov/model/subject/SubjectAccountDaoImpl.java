package org.igov.model.subject;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.igov.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectAccountDaoImpl extends GenericEntityDao<SubjectAccount> implements SubjectAccountDao {

    public SubjectAccountDaoImpl() {
	super(SubjectAccount.class);
    }

    /**
     * Author: kr110666kai
     * 
     * Возврат списка Аккаунтов согласно кретериям выборки. Обязательные поля
     * или nID_Subject или sLogin Если кретерий не участвует в выборке, то
     * ставить null Например: findSubjectAccounts(1L, null, null, null);
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<SubjectAccount> findSubjectAccounts(Long nID_Subject, String sLogin, Long nID_Server,
	    SubjectAccountType subjectAccountType) {
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

	if (subjectAccountType != null) {
	    oCriteria.add(Restrictions.eq("subjectAccountType", subjectAccountType));
	}

	List<SubjectAccount> subjectAccounts = oCriteria.list();

	return subjectAccounts;
    }

    /**
     * Author: kr110666kai
     * 
     * Сохранение или добавление нового аккаунта. Если задан nID сущности, то это обновление, иначе добавление
     */
    @Override
    public SubjectAccount setSubjectAccount(Long nID, Long nID_Subject, String sLogin, Long nID_Server,
	    SubjectAccountType subjectAccountType, String sNote) {

	SubjectAccount subjectAccount = null;

	if (nID != null) {
	    subjectAccount = findByIdExpected(nID);
	} else {
	    subjectAccount = new SubjectAccount();
	}

	if (nID_Subject != null) {
	    subjectAccount.setnID_Subject(nID_Subject);
	}
	if (sLogin != null) {
	    subjectAccount.setsLogin(sLogin);
	}
	if (nID_Server != null) {
	    subjectAccount.setnID_Server(nID_Server);
	}
	if (subjectAccountType != null) {
	    subjectAccount.setSubjectAccountType(subjectAccountType);
	}
	if (nID_Subject != null) {
	    subjectAccount.setnID_Subject(nID_Subject);
	}
	if (sNote != null) {
	    subjectAccount.setsNote(sNote);
	}

	return saveOrUpdate(subjectAccount);
    }

}
