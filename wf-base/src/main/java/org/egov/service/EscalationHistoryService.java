package org.egov.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wf.dp.dniprorada.base.dao.EscalationHistoryDao;
import org.wf.dp.dniprorada.base.model.EscalationHistory;

/**
 * @author OlgaPrylypko
 * @since 2015-12-08.
 */

@Service
public class EscalationHistoryService {
    private static final Logger LOG = Logger.getLogger(EscalationHistoryService.class);

    @Autowired
    EscalationHistoryDao escalationHistoryDao;

    public EscalationHistory create(Long processInstanceId, Long taskId, Long escalationInstanceId) {
        EscalationHistory escalationHistory = new EscalationHistory();
        escalationHistory.setnIdProcessRoot(processInstanceId);
        escalationHistory.setnIdProcess(escalationInstanceId);
        escalationHistory.setnIdEscalationStatus(2L);//{"sNote":"БП создан","nID":2,"sID":"BP_Created"},
        escalationHistory.setnIdUserTask(taskId);
        escalationHistory.setsDate(new DateTime());
        escalationHistoryDao.saveOrUpdate(escalationHistory);
        return escalationHistory;
    }
    /*
    public EscalationHistory updateStatus(Long escalationInstanceId, Long newStatus) {
        EscalationHistory escalationHistory = escalationHistoryDao.getByProcessInstanceId(escalationInstanceId);
        escalationHistory.setnIdEscalationStatus(newStatus);
        escalationHistory.setsDate(new DateTime());
        escalationHistoryDao.saveOrUpdate(escalationHistory);
        return escalationHistory;
    }*/

}
