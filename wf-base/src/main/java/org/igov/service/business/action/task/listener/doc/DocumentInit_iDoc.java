package org.igov.service.business.action.task.listener.doc;


import java.util.logging.Level;
import java.util.List;
import org.igov.model.document.DocumentStep;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
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
    
    private Expression sKey_GroupPostfix;
    
    private Expression sKey_GroupPostfix_New;
    
    @Override
    public void notify(DelegateTask delegateTask) {
        
        DelegateExecution execution = delegateTask.getExecution();

        String sKey_GroupPostfix_Value = null;
        String sKey_GroupPostfix_New_Value = null;
        
        try {
            sKey_GroupPostfix_Value
                    = (this.sKey_GroupPostfix != null) ? getStringFromFieldExpression(this.sKey_GroupPostfix, delegateTask.getExecution()) : "";
            LOG.info("sKey_GroupPostfix_Value in clone listener is {}", sKey_GroupPostfix_Value);
            
            sKey_GroupPostfix_New_Value = (this.sKey_GroupPostfix_New != null) 
                    ? getStringFromFieldExpression(this.sKey_GroupPostfix_New, delegateTask.getExecution()) : "";
            LOG.info("sKey_GroupPostfix_New_Value in clone listener is {}", sKey_GroupPostfix_New_Value);
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_Value error: {}", ex);
        }
        
        try {
            List<DocumentStep> aResDocumentStep = oDocumentStepService.checkDocumentInit(execution, sKey_GroupPostfix_Value, sKey_GroupPostfix_New_Value);
            LOG.info("aResDocumentStep in DocumentInit_iDoc is {}", aResDocumentStep);
            oDocumentStepService.syncDocumentGroups(delegateTask, aResDocumentStep);
        } catch (Exception oException) {
            LOG.error("DocumentInit_iDoc: ", oException);
            java.util.logging.Logger.getLogger(DocumentInit_iDoc.class.getName()).log(Level.SEVERE, null, oException);
        }
    }

}
