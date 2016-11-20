package org.igov.model.process;

import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.igov.model.core.GenericEntityDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProcessSubjectDaoImpl extends GenericEntityDao<Long, ProcessSubject> implements ProcessSubjectDao {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessSubjectDaoImpl.class);

    public ProcessSubjectDaoImpl() {
        super(ProcessSubject.class);
    }

    @Transactional
    @Override
    public Long setProcessSubject(String snID_Process_Activiti_Parent, String sLogin, DateTime sDatePlan, Long nOrder) {
        Long idEntity = null;
        try {
            ProcessSubject processSubject = new ProcessSubject();
            processSubject.setID_Process_Activiti(snID_Process_Activiti_Parent);
            processSubject.setLogin(sLogin);
            processSubject.setDateEdit(new DateTime(new Date()));

            ProcessSubjectStatus processSubjectStatus = new ProcessSubjectStatus();
            processSubjectStatus.setId(nOrder);
            processSubjectStatus.setName(sLogin);
            processSubject.setProcessSubjectStatus(processSubjectStatus);

            if (sDatePlan != null) {
                processSubject.setDatePlan(sDatePlan);
            }
            if (nOrder != null) {
                processSubject.setOrder(nOrder);
            }
            LOG.info(String.format("The new instance of ProcessSubject with "
                    + "snID_Process_Activiti=%s, sLogin=%s, sDatePlan=%s, nOrder=%s was created",
                    snID_Process_Activiti_Parent, sLogin, sDatePlan.toString("D"), nOrder));
            idEntity = saveOrUpdate(processSubject).getId();
            LOG.info(String.format("Entity was added with id=%s", idEntity));
        } catch (Exception e) {
            LOG.warn("(Fail set process {})", e.getMessage());
        }
        return idEntity;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        try {
            if (processSubject != null) {
                processSubject.setLogin(sLogin);
                processSubject = saveOrUpdate(processSubject);
                LOG.info(String.format("Login=%s to entity with snID_Process_Activiti=%s was added",
                        sLogin, snID_Process_Activiti));
            } else {
                LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                        snID_Process_Activiti));
            }
        } catch (Exception e) {
            LOG.warn("(Fail update process {})", e.getMessage());
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        try {
            if (processSubject != null) {
                processSubject.setOrder(nOrder);
                processSubject = saveOrUpdate(processSubject);
                LOG.info(String.format("Order=%s to entity with snID_Process_Activiti=%s was added",
                        nOrder, snID_Process_Activiti));
            } else {
                LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                        snID_Process_Activiti));
            }
        } catch (Exception e) {
            LOG.warn("(Fail update process {})", e.getMessage());
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, Long nID_ProcessSubjectStatus) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        try {
            if (processSubject != null) {
                ProcessSubjectStatus processSubjectStatus = null;
                Criteria criteria = getSession().createCriteria(ProcessSubjectStatus.class);
                criteria.add(Restrictions.eq("nID", nID_ProcessSubjectStatus));
                List<ProcessSubjectStatus> list = (List<ProcessSubjectStatus>) criteria.list();
                if (list.size() > 0) {
                    processSubjectStatus = list.get(0);
                } else {
                    processSubjectStatus = new ProcessSubjectStatus();
                    processSubjectStatus.setId(nID_ProcessSubjectStatus);
                }
                processSubject.setProcessSubjectStatus(processSubjectStatus);
                processSubject = saveOrUpdate(processSubject);
                LOG.info(String.format("ProcessSubjectStatus=%s to entity with snID_Process_Activiti=%s was added",
                        nID_ProcessSubjectStatus, snID_Process_Activiti));
            } else {
                LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                        snID_Process_Activiti));
            }
        } catch (Exception e) {
            LOG.warn("(Fail update process {})", e.getMessage());
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, DateTime sDatePlan) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        try {
            if (processSubject != null) {
                processSubject.setDatePlan(sDatePlan);
                processSubject = saveOrUpdate(processSubject);
                LOG.info(String.format("DatePlan=%s to entity with snID_Process_Activiti=%s was added",
                        sDatePlan.toString("D"), snID_Process_Activiti));
            } else {
                LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                        snID_Process_Activiti));
            }
        } catch (Exception e) {
            LOG.warn("(Fail update process {})", e.getMessage());
        }
        return processSubject;
    }
    
    private ProcessSubject findByProcessActiviti(String snID_Process_Activiti) throws HibernateException {
        Criteria criteria = getSession().createCriteria(ProcessSubject.class);
        if (snID_Process_Activiti != null) {
            criteria.add(Restrictions.eq("snID_Process_Activiti", snID_Process_Activiti));
        }
        ProcessSubject processSubject = (ProcessSubject) criteria.uniqueResult();
        return processSubject;
    }


}
