package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.Subject;

public interface SubjectDao extends EntityDao<Subject> {

    Subject getSubject(Long nID);

    Subject getSubject(String sID);

    Subject saveOrUpdateSubject(Subject subject);

}
