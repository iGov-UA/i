package org.igov.model.access;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;

import java.util.List;

/**
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 18:51
 */
public class AccessServiceLoginRoleDaoImpl extends GenericEntityDao<AccessServiceLoginRole>
        implements AccessServiceLoginRoleDao {


    protected AccessServiceLoginRoleDaoImpl() {
        super(AccessServiceLoginRole.class);
    }

    @Override
    public List<AccessServiceLoginRole> getUserRoles(String sLogin) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sLogin", sLogin));

        return criteria.list();
    }
}
