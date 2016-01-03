package org.igov.model.flow;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.igov.util.db.QueryBuilder;

import java.util.*;
import org.igov.model.core.GenericEntityDao;

/**
 * User: goodg_000
 * Date: 21.06.2015
 * Time: 15:43
 */
@Repository
public class FlowSlotDaoImpl extends GenericEntityDao<FlowSlot> implements FlowSlotDao {

    protected FlowSlotDaoImpl() {
        super(FlowSlot.class);
    }

    @Override
    public List<FlowSlot> findFlowSlotsByServiceData(Long nID_ServiceData, Long nID_SubjectOrganDepartment,
            DateTime startDate, DateTime stopDate) {

        Criteria criteria = createCriteria();
        criteria.add(Restrictions.ge("sDate", startDate));
        criteria.add(Restrictions.lt("sDate", stopDate));

        Criteria flowCriteria = criteria.createCriteria("flow");
        flowCriteria.add(Restrictions.eq("nID_ServiceData", nID_ServiceData));
        if (nID_SubjectOrganDepartment != null) {
            flowCriteria.add(Restrictions.eq("nID_SubjectOrganDepartment", nID_SubjectOrganDepartment));
        }

        criteria.addOrder(Order.asc("sDate"));

        return criteria.list();
    }

    @Override
    public List<FlowSlot> findFlowSlotsByBP(String sID_BP, Long nID_SubjectOrganDepartment, DateTime startDate,
                                            DateTime stopDate) {

        Criteria criteria = createCriteria();
        criteria.add(Restrictions.ge("sDate", startDate));
        criteria.add(Restrictions.lt("sDate", stopDate));

        Criteria flowCriteria = criteria.createCriteria("flow");
        flowCriteria.add(Restrictions.eq("sID_BP", sID_BP));
        flowCriteria.add(Restrictions.eq("nID_SubjectOrganDepartment", nID_SubjectOrganDepartment));

        criteria.addOrder(Order.asc("sDate"));

        return criteria.list();
    }

    public List<FlowSlot> findFlowSlotsByFlow(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.ge("sDate", startDate));
        criteria.add(Restrictions.lt("sDate", stopDate));
        criteria.add(Restrictions.eq("flow.id", nID_Flow_ServiceData));

        return criteria.list();
    }

    @Override
    public Set<DateTime> findFlowSlotsDates(Long nID_Flow_ServiceData, DateTime startDate, DateTime stopDate) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq("flow.id", nID_Flow_ServiceData));
        criteria.add(Restrictions.ge("sDate", startDate));
        criteria.add(Restrictions.le("sDate", stopDate));
        criteria.setProjection(Projections.property("sDate"));

        return new TreeSet<>(criteria.list());
    }

    public int updateSlots(Long nID_Flow_ServiceData, Collection<DateTime> dates, String newDuration) {
        QueryBuilder qb = new QueryBuilder(getSession(), "update FlowSlot s set ");
        qb.append("s.sDuration = :DURATION ", newDuration);
        qb.append("where s.flow.id = :FLOW_ID and ", nID_Flow_ServiceData);
        qb.appendInSafe("s.sDate", "DATE", new ArrayList<>(dates));
        return qb.toQuery().executeUpdate();
    }
}
