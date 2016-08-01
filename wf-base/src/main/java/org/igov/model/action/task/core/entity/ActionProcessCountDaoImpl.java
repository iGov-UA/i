package org.igov.model.action.task.core.entity;

import java.util.Calendar;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author askosyr
 * @since 2016-07-31.
 */
@Repository
public class ActionProcessCountDaoImpl extends GenericEntityDao<Long, ActionProcessCount> implements ActionProcessCountDao {
    private static final Logger LOG = LoggerFactory.getLogger(ActionProcessCountDaoImpl.class);
    protected ActionProcessCountDaoImpl() {
        super(ActionProcessCount.class);
    }

    @Transactional
    @Override
    public ActionProcessCount getByCriteria(String sID_BP, Integer nID_Service, Integer nYear) {
        Criteria criteria = getSession().createCriteria(ActionProcessCount.class);
        if (sID_BP != null) {
            criteria.add(Restrictions.eq("sID_BP", sID_BP));
            LOG.info("Adding sID_BP {}", sID_BP);
        }
        if (nID_Service != null) {
            criteria.add(Restrictions.eq("nID_Service", nID_Service));
            LOG.info("Adding  nID_Service{}", nID_Service);
        }
        if (nYear != null) {
            criteria.add(Restrictions.eq("nYear", nYear));
            LOG.info("Adding  nYear{}", nYear);
        } else {
        	criteria.add(Restrictions.eq("nYear", Calendar.getInstance().get(Calendar.YEAR)));
        	LOG.info("Adding  nYear{}", Calendar.getInstance().get(Calendar.YEAR));
        }

        return (ActionProcessCount)criteria.uniqueResult();
    }

}
