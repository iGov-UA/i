package org.igov.model.process;

import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 *
 * @author Kovilin
 */
@Repository

public class ProcessSubjectTreeDaoImpl extends GenericEntityDao<Long, ProcessSubjectTree> implements ProcessSubjectTreeDao {
    
    public ProcessSubjectTreeDaoImpl() {
        super(ProcessSubjectTree.class);
    }
    
    /*@Override
    public List<ProcessSubjectTree> findChildren(String snID_Process_Activiti){
        return findAllBy("processSubjectChild.snID_Process_Activiti", snID_Process_Activiti);
    }*/
}
