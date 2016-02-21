package org.igov.model.access;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.igov.model.core.GenericEntityDao;

/**
 * User: goodg_000
 * Date: 06.10.2015
 * Time: 22:34
 */
@Repository
public class AccessServiceLoginRightDaoImpl extends GenericEntityDao<AccessServiceLoginRight>
        implements AccessServiceLoginRightDao {

    protected AccessServiceLoginRightDaoImpl() {
        super(AccessServiceLoginRight.class);
    }

    @Override
    public AccessServiceLoginRight getAccessServiceLoginRight(String sLogin, String sService) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sLogin", sLogin));
        criteria.add(Restrictions.eq("sService", sService));

        return (AccessServiceLoginRight) criteria.uniqueResult();
    }

    @Override
    public List<String> getAccessibleServices(String sLogin) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sLogin", sLogin));
        criteria.setProjection(Projections.property("sService"));

        return criteria.list();
    }
}
