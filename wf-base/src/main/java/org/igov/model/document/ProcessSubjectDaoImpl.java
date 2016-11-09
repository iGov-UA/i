package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessSubjectDaoImpl extends GenericEntityDao<Long, ProcessSubject> implements ProcessSubjectDao {

    public ProcessSubjectDaoImpl() {
        super(ProcessSubject.class);
    }

}
