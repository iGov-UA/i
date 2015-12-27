package org.wf.dp.dniprorada.dao;

import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.SubjectContactType;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 17:53
 */
@Repository
public class SubjectContactTypeDaoImpl extends GenericEntityDao<SubjectContactType> implements SubjectContactTypeDao {

    private final static String NAME_FIELD = "sName_EN";

    public SubjectContactTypeDaoImpl() {
        super(SubjectContactType.class);
    }

    @Override
    public SubjectContactType getEmailType() {

        return findBy(NAME_FIELD, "Email").get();
    }

    @Override
    public SubjectContactType getPhoneType() {
        return findBy(NAME_FIELD, "Phone").get();
    }
}
