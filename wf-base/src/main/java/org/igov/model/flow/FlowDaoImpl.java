package org.igov.model.flow;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.springframework.stereotype.Repository;

/**
 * User: goodg_000
 * Date: 20.09.2015
 * Time: 19:21
 */
@Repository
public class FlowDaoImpl extends GenericEntityDao<Long, Flow> implements FlowDao {

    public FlowDaoImpl() {
        super(Flow.class);
    }

    @Override
    public Long findFlowId(String sID_BP, Long nID_SubjectOrganDepartment) {

        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("sID_BP", sID_BP));
        criteria.add(Restrictions.eq("nID_SubjectOrganDepartment", nID_SubjectOrganDepartment));
        criteria.setProjection(Projections.id());

        return (Long) criteria.uniqueResult();
    }
}
