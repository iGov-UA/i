package org.igov.model.subject;

import org.igov.model.core.EntityDao;

import java.util.List;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 18:18
 */
public interface SubjectContactDao extends EntityDao<SubjectContact> {

    List<SubjectContact> findContacts(Subject subject);
}
