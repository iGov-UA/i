package org.igov.service.business.action.task.systemtask.mail;

import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.delegate.DelegateExecution;
import org.igov.io.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Elena
 * отправка тела без атачмента - html text
 * 
 */
@Component("MailTaskWithoutAttachment")
public class MailTaskWithoutAttachment extends Abstract_MailTaskCustom {
	
    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithoutAttachment.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        
    	Map<String, Object> mExecutionVaraibles = oExecution.getVariables();
        
        Map<String, Object> mOnlyDateVariables = new HashMap<>();

        mExecutionVaraibles.forEach((key, value) -> {
            mOnlyDateVariables.put(key,value.getClass().getName());
        });
    	LOG.info("mExecutionVaraibles={}", mExecutionVaraibles);
        LOG.info("mOnlyDateVariables={}", mOnlyDateVariables);
    	
        Mail oMail = null;
        String sJsonMongo = loadFormPropertyFromTaskHTMLText(oExecution);
        LOG.info("sJsonMongo: {}", sJsonMongo);
        String sBodyFromMongoResult = getHtmlTextFromMongo(sJsonMongo); 
        LOG.info("sBodyFromMongoResult: {}", sBodyFromMongoResult);
        if(sBodyFromMongoResult!=null){
            try {
                oMail = sendToMailFromMongo(oExecution);
            } catch (Exception ex) {
                LOG.error("MailTaskWithoutAttachment: ", ex);
            }
        } else{
            try {
                oMail = Mail_BaseFromTask(oExecution);
            } catch (Exception ex) {
                LOG.error("MailTaskWithoutAttachment: ", ex);
            }
        }
        
         sendMailOfTask(oMail, oExecution);
         LOG.info("MailTaskWithoutAttachment ok!");
    }
    
}
