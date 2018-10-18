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

}
