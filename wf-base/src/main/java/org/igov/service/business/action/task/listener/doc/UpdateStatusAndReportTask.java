/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.listener.doc;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.model.process.ProcessSubject;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom;
import org.igov.service.business.process.ProcessSubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        
    static final transient Logger LOG = LoggerFactory.getLogger(UpdateStatusAndReportTask.class);

    @Autowired
    private ProcessSubjectService processSubjectService;

    @Override
    public void notify(DelegateTask delegateTask) {
        
        LOG.info("UpdateStatusTask start....");
        
        String sID_ProcessSubjectStatus_Value
                = getStringFromFieldExpression(this.sID_ProcessSubjectStatus, delegateTask.getExecution());
        
        String sReport_Value = getStringFromFieldExpression(this.sReport, delegateTask.getExecution());
        LOG.info("sReport bewore method work: " + sReport_Value);
        
        ProcessSubject oProcessSubject = processSubjectService.setProcessSubjectStatusAndReport(delegateTask.getExecutionId(), sID_ProcessSubjectStatus_Value, sReport_Value);
        
        LOG.info("sReport after method work: " + oProcessSubject.getsReport());
    }

}
