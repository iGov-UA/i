package org.igov.model.subject;

import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubjectContactPlacePointDaoImpl extends GenericEntityDao<Long, SubjectContactPlacePoint> implements SubjectContactPlacePointDao {
    public SubjectContactPlacePointDaoImpl() {
        super(SubjectContactPlacePoint.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SubjectContactPlacePoint> findBySubject(Long nID_Subject) {

        return getSession().createCriteria(SubjectContactPlacePoint.class, "SubjectContactPlacePoint")
                .createAlias("SubjectContactPlacePoint.oSubject", "Subject")
                .add(Restrictions.eq("Subject.id", nID_Subject))
                .setReadOnly(true)
                .list();
    }
}
