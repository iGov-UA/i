package org.wf.dp.dniprorada.dao;

import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.model.SubjectContactType;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 17:52
 */
public interface SubjectContactTypeDao extends EntityDao<SubjectContactType> {

    public SubjectContactType getEmailType();

    public SubjectContactType getPhoneType();

}
