package org.igov.service.business.action.task.systemtask.export;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.business.export.AgroholdingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author askosyr
 *
 */
@Component("Transfer_DocumentVacation")
public class Transfer_DocumentVacation implements JavaDelegate, TaskListener {

    private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);
   
    @Autowired
    AgroholdingService agroholdingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        agroholdingService.transferDocumentVacation();
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            agroholdingService.transferDocumentVacation();
        } catch (Exception ex) {
            LOG.info(EVENTNAME_CREATE);
        }
    }
}
