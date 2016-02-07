//package org.igov.model.action.task.core.entity;
//
//import org.igov.model.core.GenericEntityDao;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Repository;
//
///**
// * @author NickVeremeichyk
// * @since 2016-02-07.
// */
//@Repository
//@SuppressWarnings("unchecked")
//public class ActionTaskLinkDaoImpl extends GenericEntityDao<ActionTaskLink> implements ActionTaskLinkDao {
//    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskLinkDaoImpl.class);
//    protected ActionTaskLinkDaoImpl() {
//        super(ActionTaskLink.class);
//    }
//
//    @Override
//    public ActionTaskLink setSubjectOrganJoinTax(Long nIdProcess, String sKey, Long nIdSubjectHolder) {
//        ActionTaskLink actionTaskLink = getByKey(null, nIdProcess, sKey, nIdSubjectHolder);
//
//        if (actionTaskLink != null && actionTaskLink.getnIdProcess() != nIdProcess)
//            actionTaskLink.setnIdProcess(nIdProcess);
//        if (actionTaskLink != null && actionTaskLink.getsKey() != sKey)
//            actionTaskLink.setsKey(sKey);
//        if (actionTaskLink != null && actionTaskLink.getnIdSubjectHolder() != nIdSubjectHolder)
//            actionTaskLink.setnIdSubjectHolder(nIdSubjectHolder);
//
//
//        actionTaskLink = saveOrUpdate(actionTaskLink);
//        LOG.info("actionTaskLink {} is upload", actionTaskLink);
//        return actionTaskLink;
//    }
//
//    @Override
//    public void removeByKey(String nId, Long sIdUA) {
//
//    }
//
//    @Override
//    public ActionTaskLink getByKey(Long nId, Long nIdProcess, String sKey, Long nIdSubjectHolder) {
//        if (nId != null) {
//            return findById(nId).or(new ActionTaskLink());
//        } else if (nIdProcess != null) {
//            return findBy("nIdProcess", nIdProcess).or(new ActionTaskLink());
//        } else if (sKey != null) {
//            return findBy("sKey", sKey).or(new ActionTaskLink());
//        } else if (nIdSubjectHolder != null) {
//            return findBy("nIdSubjectHolder", nIdSubjectHolder).or(new ActionTaskLink());
//        } else
//            throw new IllegalArgumentException("All args are null!");
//    }
//}
