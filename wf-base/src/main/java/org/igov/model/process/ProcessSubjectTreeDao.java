package org.igov.model.process;
import org.igov.model.core.EntityDao;

/**
 *
 * @author Kovilin
 */
public interface ProcessSubjectTreeDao extends EntityDao<Long, ProcessSubject>{
    
    Long setProcessSubject(ProcessSubject processSubjectParent, ProcessSubject processSubjectChild);

}
