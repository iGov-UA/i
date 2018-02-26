package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author BW
 */
@Component("MailTaskWithAttachmentsAndSMS")
public class MailTaskWithAttachmentsAndSMS extends MailTaskWithAttachments {

    protected Expression sPhone_SMS;
    protected Expression sText_SMS;

    @Autowired
    ActionTaskService oActionTaskService;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        sendMailWithAttachments(oExecution);
        
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                  trySendSms(oExecution);
                  LOG.info("MailTaskWithAttachmentsAndSMS ok!");
            }
        });
    }

    private void trySendSms(DelegateExecution oExecution) {
        try {
            LOG.info("attempt to send sms");
            sendSms(oExecution, sPhone_SMS, sText_SMS);
        } catch (Exception e) {
            LOG.warn("Error happens while sending sms", e);
        }
    }

    private void wait(DelegateExecution oExecution, String sAttachmentsForSend) {
        try {
            if (sAttachmentsForSend.trim().equals("") || !sAttachmentsForSend.contains("sKey")) {
                LOG.info("Sleeping started..");
                Thread.sleep(2000);
                LOG.info("Variables names {}", oExecution.getVariables() != null
                        ? oExecution.getVariables().keySet() : "null");
                Object oAttachmentsForSendSelected = oExecution.getVariable("result");
                LOG.info("oAttachmentsForSendSelected {}", oAttachmentsForSendSelected != null
                        ? (String) oAttachmentsForSendSelected : "null");
                if (oAttachmentsForSendSelected != null && !((String) oAttachmentsForSendSelected).trim().equals("")) {
                    LOG.info("some sleep always help! {}", oAttachmentsForSendSelected.toString());
                    sAttachmentsForSend = (String) oAttachmentsForSendSelected;
                }
            }
        } catch (Exception ex) {
            LOG.info("Error during sleeping thread in mail {}", ex);
        }
    }
}
