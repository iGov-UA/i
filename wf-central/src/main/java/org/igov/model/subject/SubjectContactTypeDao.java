package org.igov.model.subject;

import org.igov.model.core.EntityDao;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 17:52
 */
public interface SubjectContactTypeDao extends EntityDao<SubjectContactType> {

    public SubjectContactType getEmailType();

    public SubjectContactType getPhoneType();

}
