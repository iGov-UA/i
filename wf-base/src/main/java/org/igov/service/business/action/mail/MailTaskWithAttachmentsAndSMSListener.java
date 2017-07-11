package org.igov.service.business.action.mail;

import java.util.logging.Level;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.igov.service.business.action.task.systemtask.mail.MailTaskWithAttachmentsAndSMS;

/**
 * @author BW
 */
@Component("MailTaskWithAttachmentsAndSMSListener")
public class MailTaskWithAttachmentsAndSMSListener extends MailTaskWithAttachmentsAndSMS implements TaskListener {

    public Expression sFrom;
    public Expression sTo;
    public Expression sSubject;
    public Expression sText;

    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithAttachmentsAndSMSListener.class);

    @Override
    public void notify(DelegateTask oTask) {
        DelegateExecution oExecution = oTask.getExecution();
        try {
            sendMailWithAttachments(oExecution);
            sendSms(oExecution, sPhone_SMS, sText_SMS);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MailTaskWithAttachmentsAndSMSListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOG.info("MailTaskWithAttachmentsAndSMSListener ok!");
    }

}
