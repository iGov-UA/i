package org.igov.model.process;

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
        ProcessSubject processSubject = new ProcessSubject();
        processSubject.setSnID_Process_Activiti(snID_Process_Activiti_Parent);
        processSubject.setsLogin(sLogin);

        if (sDatePlan != null) {
            processSubject.setsDatePlan(sDatePlan);
        }
        if (nOrder != null) {
            processSubject.setnOrder(nOrder);
        }
        LOG.info(String.format("The new instance of ProcessSubject with "
                + "snID_Process_Activiti=%s, sLogin=%s, sDatePlan=%s, nOrder=%s was created",
                snID_Process_Activiti_Parent, sLogin, sDatePlan.toString("D"), nOrder));
        Long idEntity = saveOrUpdate(processSubject).getId();
        LOG.info(String.format("Entity was added with id=%s", idEntity));
        return idEntity;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectLogin(String snID_Process_Activiti, String sLogin) {
        ProcessSubject processSubject = new ProcessSubject();
        processSubject.setSnID_Process_Activiti(snID_Process_Activiti);
        processSubject.setsLogin(sLogin);
        processSubject = saveOrUpdate(processSubject);
        LOG.info(String.format("Login=%s to entity with snID_Process_Activiti=%s was added",
                sLogin, snID_Process_Activiti));
        return processSubject;
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectOrder(String snID_Process_Activiti, Long nOrder) {
        ProcessSubject processSubject = new ProcessSubject();
        processSubject.setSnID_Process_Activiti(snID_Process_Activiti);
        processSubject.setnOrder(nOrder);
        processSubject = saveOrUpdate(processSubject);
        LOG.info(String.format("Order=%s to entity with snID_Process_Activiti=%s was added",
                nOrder, snID_Process_Activiti));
        return processSubject;
    }

    @Override
    public ProcessSubject setProcessSubjectStatus(String snID_Process_Activiti, Long nID_ProcessSubjectStatus) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Transactional
    @Override
    public ProcessSubject setProcessSubjectDatePlan(String snID_Process_Activiti, DateTime sDatePlan) {
        ProcessSubject processSubject = new ProcessSubject();
        processSubject.setSnID_Process_Activiti(snID_Process_Activiti);
        processSubject.setsDatePlan(sDatePlan);
        processSubject = saveOrUpdate(processSubject);
        LOG.info(String.format("DatePlan=%s to entity with snID_Process_Activiti=%s was added",
                sDatePlan.toString("D"), snID_Process_Activiti));
        return processSubject;
    }

}
