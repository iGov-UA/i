package org.igov.service.business.action.task.listener.doc;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.model.document.DocumentStepSubjectRight;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.document.DocumentStepService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.igov.service.business.process.ProcessSubjectService;
import org.igov.util.Tool;

/**
 *
 * @author Kovilin
 */
@Component("CloneDocumentRights")
public class CloneDocumentRights implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sKey_GroupPostfix;
    private Expression sKey_GroupPostfix_New;
    private Expression sKey_Step;
    
    @Autowired
    private DocumentStepService oDocumentStepService;

    @Override
    public void notify(DelegateTask delegateTask) {

        LOG.info("CloneDocumentRights start..." + delegateTask.getProcessInstanceId());

        String sKey_GroupPostfix_Value = "";
        try {
            sKey_GroupPostfix_Value
                    = (this.sKey_GroupPostfix != null) ? getStringFromFieldExpression(this.sKey_GroupPostfix, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_Value: ", ex);
        }

        String sKey_GroupPostfix_New_Value = "";
        try {
            sKey_GroupPostfix_New_Value = (this.sKey_GroupPostfix_New != null) ? getStringFromFieldExpression(this.sKey_GroupPostfix_New, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_New_Value: ", ex);
        }
        
        String sKey_Step_Value = "";
        try {
            sKey_Step_Value = (this.sKey_Step != null) ? getStringFromFieldExpression(this.sKey_Step, delegateTask.getExecution()) : "";
        } catch (Exception ex) {
            LOG.error("sKey_GroupPostfix_New_Value: ", ex);
        }
        
       oDocumentStepService.cloneDocumentStepSubject(delegateTask.getProcessInstanceId(), sKey_GroupPostfix_Value, sKey_GroupPostfix_New_Value, sKey_Step_Value);
       LOG.info("CloneDocumentRights finished");
    }
}
