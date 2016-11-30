package org.igov.model.action.task.core.entity;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author NickVeremeichyk
 * @since 2016-02-07.
 */
@Repository
@SuppressWarnings("unchecked")
public class ActionTaskLinkDaoImpl extends GenericEntityDao<Long, ActionTaskLink> implements ActionTaskLinkDao {
    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskLinkDaoImpl.class);
    protected ActionTaskLinkDaoImpl() {
        super(ActionTaskLink.class);
    }

    @Transactional
    @Override
    public ActionTaskLink getByCriteria(Long nIdProcess, String sKey, Long nIdSubjectHolder) {
        Criteria criteria = getSession().createCriteria(ActionTaskLink.class);
        if (nIdProcess != null) {
            criteria.add(Restrictions.eq("nIdProcess", nIdProcess));
        }
        if (sKey != null) {
            criteria.add(Restrictions.eq("sKey", sKey));
        }
        if (nIdSubjectHolder != null) {
            criteria.add(Restrictions.eq("nIdSubjectHolder", nIdSubjectHolder));
        }

        return (ActionTaskLink)criteria.uniqueResult();
    }

    @Transactional
    @Override
    public ActionTaskLink setActionTaskLink(Long nIdProcess, String sKey, Long nIdSubjectHolder) {
//        ActionTaskLink actionTaskLink = getByKey(nIdProcess, sKey, nIdSubjectHolder);
        ActionTaskLink actionTaskLink = new ActionTaskLink();

        if (actionTaskLink != null && actionTaskLink.getnIdProcess() != nIdProcess)
            actionTaskLink.setnIdProcess(nIdProcess);
        if (actionTaskLink != null && actionTaskLink.getsKey() != sKey)
            actionTaskLink.setsKey(sKey);
        if (actionTaskLink != null && actionTaskLink.getnIdSubjectHolder() != nIdSubjectHolder)
            actionTaskLink.setnIdSubjectHolder(nIdSubjectHolder);


        actionTaskLink = saveOrUpdate(actionTaskLink);
        LOG.info("actionTaskLink {} is upload", actionTaskLink);
        return actionTaskLink;
    }

    @Override
    public void removeByKey(String nId, Long sIdUA) {

    }

    @Override
    public ActionTaskLink getByKey(Long nIdProcess, String sKey, Long nIdSubjectHolder) {
        if (nIdProcess != null) {
            return findBy("nIdProcess", nIdProcess).or(new ActionTaskLink());
        } else if (sKey != null) {
            return findBy("sKey", sKey).or(new ActionTaskLink());
        } else if (nIdSubjectHolder != null) {
            return findBy("nIdSubjectHolder", nIdSubjectHolder).or(new ActionTaskLink());
        } else
            throw new IllegalArgumentException("All args are null!");
    }
}
