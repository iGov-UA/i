package org.wf.dp.dniprorada.base.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.wf.dp.dniprorada.base.model.FlowLink;


import java.util.List;

/**
 * Created by Богдан on 25.10.2015.
 */

@Repository
public class FlowLinkDaoImpl extends GenericEntityDao<FlowLink> implements FlowLinkDao {


    public FlowLinkDaoImpl() {
        super(FlowLink.class);
    }

    @Override
    public FlowLink findLinkByService(Long nID_Service, Long nID_SubjectOrganDepartment) {

        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq("nID_Service", nID_Service));
        if (nID_SubjectOrganDepartment!=null){
        	criteria.add(Restrictions.eq("nID_SubjectOrganDepartment", nID_SubjectOrganDepartment));
        }

        List<FlowLink> links = criteria.list();
        return links.isEmpty() ? null : links.get(0);
    }
}
