package org.igov.service.listener;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 *
 * @author cepera
 */

@Component("assignUser")
public class AssignUserToTaskListener implements TaskListener{
    
    private static final transient Logger LOG = LoggerFactory.getLogger(AssignUserToTaskListener.class);
    
    @Autowired
    private RuntimeService runtimeService;
    
    private Expression loginAssignee;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String loginAssignee = getStringFromFieldExpression(this.loginAssignee, execution);
        LOG.info("Variable " + "_" + loginAssignee + "_ include task owner");
        String sTaskOwner = runtimeService.getVariable(execution.getProcessInstanceId(), loginAssignee).toString();
        //String sTaskOwner = (String) execution.getVariable(loginAssignee);
        LOG.info("Task owner is " + sTaskOwner);
        delegateTask.setAssignee(sTaskOwner);      
        
    }
    
}
