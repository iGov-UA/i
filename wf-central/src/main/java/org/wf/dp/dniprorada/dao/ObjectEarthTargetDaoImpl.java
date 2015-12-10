package org.wf.dp.dniprorada.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.dao.GenericEntityDao;
import org.wf.dp.dniprorada.model.ObjectEarthTarget;

import java.util.List;

/**
 * @author grigoriy-romanenko
 */
@Repository
public class ObjectEarthTargetDaoImpl extends GenericEntityDao<ObjectEarthTarget>
        implements ObjectEarthTargetDao {

    protected ObjectEarthTargetDaoImpl() {
        super(ObjectEarthTarget.class);
    }

    @SuppressWarnings("unchecked")
    public List<ObjectEarthTarget> getObjectEarthTargets(String sID_UA, String sName_UA) {
        Criteria criteria = getSession().createCriteria(ObjectEarthTarget.class);
        if (sID_UA != null) {
            criteria.add(Restrictions.eq("sID_UA", sID_UA));
        }
        if (sName_UA != null) {
            criteria.add(Restrictions.like("sName_UA", sName_UA, MatchMode.ANYWHERE));
        }
        return (List<ObjectEarthTarget>) criteria.list();
    }

}
