package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.igov.service.business.process.ProcessSubjectService;

/**
 *
 * @author Kovilin
 */
@Component("SetTasks")
public class SetTasks implements TaskListener {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(SetTasks.class);

    private Expression sTaskProcessDefinition;

    private Expression sID_Attachment;

    private Expression sContent;

    private Expression sAutorResolution;

    private Expression sTextResolution;

    private Expression sDateExecution;

    @Autowired
    private ProcessSubjectService processSubjectService;

    @Override
    public void notify(DelegateTask delegateTask) {
        LOG.info("SetTasks start..." + delegateTask.getProcessInstanceId());
        String sTaskProcessDefinition_Value
                = getStringFromFieldExpression(this.sTaskProcessDefinition, delegateTask.getExecution());
        String sID_Attachment_Value
                = getStringFromFieldExpression(this.sID_Attachment, delegateTask.getExecution());
        String sContent_Value
                = getStringFromFieldExpression(this.sContent, delegateTask.getExecution());
        String sAutorResolution_Value
                = getStringFromFieldExpression(this.sAutorResolution, delegateTask.getExecution());
        String sTextResolution_Value
                = getStringFromFieldExpression(this.sTextResolution, delegateTask.getExecution());
        String sDateExecution_Value
                = getStringFromFieldExpression(this.sDateExecution, delegateTask.getExecution());

        processSubjectService.setProcessSubjects(sTaskProcessDefinition_Value, sID_Attachment_Value, sContent_Value, 
                sAutorResolution_Value, sTextResolution_Value, sDateExecution_Value, delegateTask.getExecution().getId());
        
        LOG.info("SetTasks finished" + delegateTask.getProcessInstanceId());
    }
}
