package org.igov.model.subject;

import org.igov.model.core.EntityDao;

public interface SubjectRightBPDao extends EntityDao<Long, SubjectRightBP> {
    
    public SubjectRightBP getSubjectRightBP(String sID_BP, String sLogin);
    
}
