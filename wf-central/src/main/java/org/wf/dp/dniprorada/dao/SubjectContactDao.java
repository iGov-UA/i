package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.Subject;
import org.wf.dp.dniprorada.model.SubjectContact;

import java.util.List;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 18:18
 */
public interface SubjectContactDao extends EntityDao<SubjectContact> {

    List<SubjectContact> findContacts(Subject subject);
}
