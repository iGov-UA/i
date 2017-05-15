package org.igov.model.process;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kovilin
 */
@Repository
public class ProcessSubjectTaskDaoImpl extends GenericEntityDao<Long, ProcessSubjectTask> implements ProcessSubjectTaskDao{

    public ProcessSubjectTaskDaoImpl() {
        super(ProcessSubjectTask.class);
    }
    
}
