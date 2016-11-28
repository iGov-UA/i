package org.igov.model.process;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kovilin
 */
@Repository

public class ProcessSubjectTreeDaoImpl extends GenericEntityDao<Long, ProcessSubjectTree> implements ProcessSubjectTreeDao {
    
    public ProcessSubjectTreeDaoImpl() {
        super(ProcessSubjectTree.class);
    }
}
