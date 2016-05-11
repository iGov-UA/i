package org.igov.model.subject;

import org.igov.model.core.EntityDao;

public interface SubjectDao extends EntityDao<Long, Subject> {

    Subject getSubject(Long nID);

    Subject getSubject(String sID);

    Subject saveOrUpdateSubject(Subject subject);

}
