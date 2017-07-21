package org.igov.service.business.action.task.systemtask.mail;


import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.io.mail.Mail;

/**
 * @author Elena
 * отправка тела без атачмента
 * 
 */
@Component("MailTaskWithoutAttachmentBase")
public class MailTaskWithoutAttachmentBase extends Abstract_MailTaskCustom {
    
    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithoutAttachmentBase.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        try {
        	
            Mail oMail = Mail_BaseFromTask(oExecution);
            sendMailOfTask(oMail, oExecution);
            LOG.info("MailTaskWithoutAttachment ok!");
        } catch (Exception ex) {
            LOG.error("MailTaskWithoutAttachment: ", ex);
        }
    }
    
}
