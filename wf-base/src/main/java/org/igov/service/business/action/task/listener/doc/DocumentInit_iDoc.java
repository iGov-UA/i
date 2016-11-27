package org.igov.service.business.action.task.listener.doc;


import java.util.logging.Level;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.document.DocumentStepService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("DocumentInit_iDoc")
public class DocumentInit_iDoc extends AbstractModelTask implements TaskListener {

    private final static Logger LOG = LoggerFactory.getLogger(CreateDocument_UkrDoc.class);

    @Autowired
    private DocumentStepService oDocumentStepService;
    
    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    FormService formService;

    @Autowired
    TaskService taskService;
    
    @Autowired
    HttpRequester httpRequester;
    
    @Override
    public void notify(DelegateTask delegateTask) {
        
        DelegateExecution execution = delegateTask.getExecution();
        try {
            oDocumentStepService.checkDocumentInit(execution.getProcessInstanceId());
        } catch (Exception oException) {
            LOG.error("", oException);
            try {
                throw oException;
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(DocumentInit_iDoc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
