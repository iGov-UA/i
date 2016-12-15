/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.process.ProcessSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author olga
 */
@Component("UpdateStatusTask")
public class UpdateStatusAndReportTask implements TaskListener {

    private Expression sID_ProcessSubjectStatus;
    
    private Expression sReport; 

    @Autowired
    private ProcessSubjectService processSubjectService;

    @Override
    public void notify(DelegateTask delegateTask) {
        String sID_ProcessSubjectStatus_Value
                = getStringFromFieldExpression(this.sID_ProcessSubjectStatus, delegateTask.getExecution());
        
        String sReport_Value = getStringFromFieldExpression(this.sReport, delegateTask.getExecution());
        
        processSubjectService.setProcessSubjectStatusAndReport(delegateTask.getExecutionId(), sID_ProcessSubjectStatus_Value, sReport_Value);
    }

}
