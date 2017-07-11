package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.springframework.stereotype.Component;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 * @author BW
 */
@Component("MailTaskWithAttachments")
public class MailTaskWithAttachments extends Abstract_MailTaskCustom {

    private Expression saAttachmentsForSend;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        sendMailWithAttachments(oExecution);
        LOG.info("MailTaskWithAttachments ok!");
    }

    protected void sendMailWithAttachments(DelegateExecution oExecution) throws Exception {
        String sAttachmentsForSend = getStringFromFieldExpression(this.saAttachmentsForSend, oExecution);
        
        if (oExecution.getProcessDefinitionId().split(":")[0].equals("subsidies_Ukr_result") && (sAttachmentsForSend == null || "".equals(sAttachmentsForSend.trim()))) {
            throw new RuntimeException("Не найден файл фля отправки в письме! Он обязателен!!!Can't find any attach!!!");
        }
        LOG.info("Process id is {} sAttachmentsForSend after arriving in MailTaskWithAttachments {}",
                oExecution.getProcessInstanceId(), sAttachmentsForSend);
        sendMail(oExecution, sAttachmentsForSend);
    }
}
