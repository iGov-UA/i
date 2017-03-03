package org.igov.service.business.action.task.listener.doc;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
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
            List<DocumentStep> aResDocumentStep = oDocumentStepService.checkDocumentInit(execution);
            
            List<String> asGroup = new ArrayList<>();
            
            for(DocumentStep oDocumentStep : aResDocumentStep){
                
                List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oDocumentStep.getRights();
                if(aDocumentStepSubjectRight != null){
                    for(DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight){
                        asGroup.add(oDocumentStepSubjectRight.getsKey_GroupPostfix());
                    } 
                }
            }
            
            LOG.info("asGroup in DocumentInit_iDoc {}", asGroup);
            
            delegateTask.addCandidateGroups(asGroup);
            
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
