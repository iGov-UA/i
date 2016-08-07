package org.igov.model.subject;

import java.util.List;
import org.igov.model.core.EntityDao;

public interface SubjectActionKVEDDao extends EntityDao<Long, SubjectActionKVED> {
    
    List<SubjectActionKVED> getSubjectActionKVED(String sID, String sNote);
    List<SubjectActionKVED> getSubjectActionKVED(String sFind);

}
