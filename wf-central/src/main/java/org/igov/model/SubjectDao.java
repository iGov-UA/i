package org.igov.model;

import org.igov.model.core.EntityDao;

public interface SubjectDao extends EntityDao<Subject> {

    Subject getSubject(Long nID);

    Subject getSubject(String sID);

    Subject saveOrUpdateSubject(Subject subject);

}
