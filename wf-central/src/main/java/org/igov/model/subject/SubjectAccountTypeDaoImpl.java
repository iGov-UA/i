package org.igov.model.subject;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectAccountTypeDaoImpl extends GenericEntityDao<SubjectAccountType> implements SubjectAccountTypeDao {

    public SubjectAccountTypeDaoImpl() {
        super(SubjectAccountType.class);
    }
   
}
 