package org.igov.model.subject;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectContactPlacePointTypeDaoImpl extends GenericEntityDao<Long, SubjectContactPlacePointType> implements SubjectContactPlacePointTypeDao {
    public SubjectContactPlacePointTypeDaoImpl() {
        super(SubjectContactPlacePointType.class);
    }

    @Override
    public SubjectContactPlacePointType findByNameEN(String sName_EN) {
        Criteria criteria = getSession().createCriteria(SubjectContactPlacePointType.class);
        if (sName_EN != null) {
            criteria.add(Restrictions.eq("sName_EN", sName_EN)).setMaxResults(1).uniqueResult();
        }
        return (SubjectContactPlacePointType) criteria;
    }
}
