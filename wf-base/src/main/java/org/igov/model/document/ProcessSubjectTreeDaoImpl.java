package org.igov.model.document;

import org.igov.model.core.GenericEntityDao;


public class ProcessSubjectTreeDaoImpl extends GenericEntityDao<Long, ProcessSubjectTree> implements ProcessSubjectTreeDao{

    public ProcessSubjectTreeDaoImpl() {
        super(ProcessSubjectTree.class);
    }
    
    
}
