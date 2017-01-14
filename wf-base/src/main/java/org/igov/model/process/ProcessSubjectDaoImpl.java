package org.igov.model.process;

import java.util.Date;
import org.hibernate.HibernateException;
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
    public ProcessSubject setProcessSubject(String snID_Process_Activiti, String sLogin, DateTime sDatePlan,
            Long nOrder, ProcessSubjectStatus processSubjectStatus) {
        ProcessSubject processSubject = null;
        try {
            processSubject = new ProcessSubject();
            processSubject.setSnID_Process_Activiti(snID_Process_Activiti);
            processSubject.setsLogin(sLogin);
            processSubject.setsDateEdit(new DateTime(new Date()));
            processSubject.setProcessSubjectStatus(processSubjectStatus);

            if (sDatePlan != null) {
                processSubject.setsDatePlan(sDatePlan);
            }
            if (nOrder != null) {
                processSubject.setnOrder(nOrder);
            }
            LOG.info(String.format("The new instance of ProcessSubject with "
                    + "snID_Process_Activiti=%s, sLogin=%s, sDatePlan=%s, nOrder=%s was created",
                    snID_Process_Activiti, sLogin, sDatePlan.toString("D"), nOrder));
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("Entity was added with id=%s", processSubject.getId()));
        } catch (Exception e) {
            LOG.warn("(Fail set process {})", e.getMessage());
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        if (processSubject != null) {
            processSubject.setsLogin(sLogin);
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("Login=%s to entity with snID_Process_Activiti=%s was added",
                    sLogin, snID_Process_Activiti));
        } else {
            LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                    snID_Process_Activiti));
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        if (processSubject != null) {
            processSubject.setnOrder(nOrder);
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("Order=%s to entity with snID_Process_Activiti=%s was added",
                    nOrder, snID_Process_Activiti));
        } else {
            LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                    snID_Process_Activiti));
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, ProcessSubjectStatus processSubjectStatus) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        if (processSubject != null) {
            processSubject.setProcessSubjectStatus(processSubjectStatus);
            processSubject.setsDateEdit(new DateTime(new Date()));
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("ProcessSubjectStatus=%s to entity with snID_Process_Activiti=%s was added",
                    processSubjectStatus.getId(), snID_Process_Activiti));
        } else {
            LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                    snID_Process_Activiti));
        }
        return processSubject;
    }
    
    @Transactional
    @Override
    public ProcessSubject setProcessSubjectStatusAndReport(String snID_Process_Activiti, ProcessSubjectStatus processSubjectStatus, String sReport) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        if (processSubject != null) {
            processSubject.setProcessSubjectStatus(processSubjectStatus);
            processSubject.setsReport(sReport);
            processSubject.setsDateEdit(new DateTime(new Date()));
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("ProcessSubjectStatus=%s to entity with snID_Process_Activiti=%s  and sReport=%s was added",
                    processSubjectStatus.getId(), snID_Process_Activiti, sReport));
        } else {
            LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                    snID_Process_Activiti));
        }
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, DateTime sDatePlan) {
        ProcessSubject processSubject = findByProcessActiviti(snID_Process_Activiti);
        if (processSubject != null) {
            processSubject.setsDatePlan(sDatePlan);
            processSubject = saveOrUpdate(processSubject);
            LOG.info(String.format("DatePlan=%s to entity with snID_Process_Activiti=%s was added",
                    sDatePlan.toString("D"), snID_Process_Activiti));
        } else {
            LOG.warn(String.format("Entity with snID_Process_Activiti = %s is absent!",
                    snID_Process_Activiti));
        }
        return processSubject;
    }

    private ProcessSubject findByProcessActiviti(String snID_Process_Activiti) throws HibernateException {
        return findByExpected("snID_Process_Activiti", snID_Process_Activiti);
    }
    
    //whitout exception
    @Override
    public ProcessSubject findByProcessActivitiId(String snID_Process_Activiti){
        return findBy("snID_Process_Activiti", snID_Process_Activiti).orNull();
    }
}
