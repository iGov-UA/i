package org.igov.model.subject;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectAccountDaoImpl extends GenericEntityDao<SubjectAccount> implements SubjectAccountDao {

    public SubjectAccountDaoImpl() {
	super(SubjectAccount.class);
    }

}
