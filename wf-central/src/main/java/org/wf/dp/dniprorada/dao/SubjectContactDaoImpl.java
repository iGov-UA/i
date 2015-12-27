package org.wf.dp.dniprorada.dao;

import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.Subject;
import org.wf.dp.dniprorada.model.SubjectContact;

import java.util.List;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 18:20
 */
@Repository
public class SubjectContactDaoImpl extends GenericEntityDao<SubjectContact> implements SubjectContactDao {

    public SubjectContactDaoImpl() {
        super(SubjectContact.class);
    }

    @Override
    public List<SubjectContact> findContacts(Subject subject) {
        return findAllBy("subject", subject);
    }
}
