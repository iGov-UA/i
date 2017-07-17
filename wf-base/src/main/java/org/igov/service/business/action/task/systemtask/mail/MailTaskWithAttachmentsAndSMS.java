package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.igov.service.business.action.task.core.ActionTaskService;

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
        sendSms(oExecution, sPhone_SMS, sText_SMS);
        LOG.info("MailTaskWithAttachmentsAndSMS ok!");
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
