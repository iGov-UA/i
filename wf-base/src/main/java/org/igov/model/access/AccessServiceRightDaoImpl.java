package org.igov.model.access;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: goodg_000
 * Date: 08.03.2016
 * Time: 15:42
 */
@Repository
public class AccessServiceRightDaoImpl extends GenericEntityDao<AccessServiceRight> implements AccessServiceRightDao {

    protected AccessServiceRightDaoImpl() {
        super(AccessServiceRight.class);
    }

    @Override
    public List<AccessServiceRight> getAccessServiceRights(Long nID, String sService, String saMethod, String sHandlerBean) {
        Criteria criteria = createCriteria();
        if (nID != null) {
            criteria.add(Restrictions.eq("id", nID));
        }
        if (sService != null) {
            criteria.add(Restrictions.like("sService", sService, MatchMode.ANYWHERE));
        }
        if (saMethod != null) {
            criteria.add(Restrictions.ilike("saMethod", saMethod, MatchMode.ANYWHERE));
        }
        if (sHandlerBean != null) {
            criteria.add(Restrictions.eq("sHandlerBean", sHandlerBean));
        }

        return criteria.list();
    }
}
