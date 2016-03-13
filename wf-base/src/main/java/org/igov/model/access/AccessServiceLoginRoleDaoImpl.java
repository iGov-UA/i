package org.igov.model.access;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: goodg_000
 * Date: 28.02.2016
 * Time: 18:51
 */
@Repository
public class AccessServiceLoginRoleDaoImpl extends GenericEntityDao<AccessServiceLoginRole>
        implements AccessServiceLoginRoleDao {


    public AccessServiceLoginRoleDaoImpl() {
        super(AccessServiceLoginRole.class);
    }

    @Override
    public List<AccessServiceLoginRole> getUserRoles(String sLogin) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sLogin", sLogin));

        return criteria.list();
    }

    @Override
    public AccessServiceLoginRole findLoginRole(String sLogin, Long nID_AccessServiceRole) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sLogin", sLogin));
        criteria.add(Restrictions.eq("accessServiceRole.id", nID_AccessServiceRole));

        return (AccessServiceLoginRole)criteria.uniqueResult();
    }
}
