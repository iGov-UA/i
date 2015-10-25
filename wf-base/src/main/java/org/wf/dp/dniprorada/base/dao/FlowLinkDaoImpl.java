package org.wf.dp.dniprorada.base.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.model.FlowLink;

/**
 * Created by Богдан on 25.10.2015.
 */

@Repository
public class FlowLinkDaoImpl extends GenericEntityDao<FlowLink> implements FlowLinkDao {


    public FlowLinkDaoImpl() {
        super(FlowLink.class);
    }

    @Override
    public long findServiceDataByService(Long nID_Service) throws Exception {

        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("nID_Service", nID_Service));
        criteria.setProjection(Projections.id());

        return (Long) criteria.uniqueResult();
    }
}
