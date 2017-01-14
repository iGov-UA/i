package org.igov.model.process;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kovilin
 */
@Repository

public class ProcessSubjectStatusDaoImpl extends GenericEntityDao<Long, ProcessSubjectStatus> implements ProcessSubjectStatusDao {
    
    public ProcessSubjectStatusDaoImpl() {
        super(ProcessSubjectStatus.class);
    }
}
