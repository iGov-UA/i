/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.igov.service.business.action.task.core.ActionTaskService;
import static org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom.LOG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author iDoc-2
 */
@Component("MailTaskWithoutAttachmentAndWithSms")
public class MailTaskWithoutAttachmentAndWithSms extends MailTaskWithoutAttachment  {
    
    protected Expression sPhone_SMS;
    protected Expression sText_SMS;
   
    @Autowired
    ActionTaskService oActionTaskService;
    

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        LOG.info("Process id is {} in MailTaskWithoutAttachment {}",
                oExecution.getProcessInstanceId());
        sendMail(oExecution, null);
        sendSms(oExecution, sPhone_SMS, sText_SMS);
        LOG.info("MailTaskWithoutAttachmentAndWithSms ok!");
    }
    
}
