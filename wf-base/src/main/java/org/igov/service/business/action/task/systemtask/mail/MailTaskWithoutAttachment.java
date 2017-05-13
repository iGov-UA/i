package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;

/**
 * @author Elena
 * отправка тела без атачмента - html text
 * 
 */
@Component("MailTaskWithoutAttachment")
public class MailTaskWithoutAttachment extends Abstract_MailTaskCustom {
	
	 @Autowired
	 GeneralConfig generalConfig;
    
    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithoutAttachment.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
    	
	    	Mail oMail = null;
	    	String sJsonMongo = loadFormPropertyFromTaskHTMLText(oExecution);
	    	String sBodyFromMongoResult = getHtmlTextFromMongo(sJsonMongo); 
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
	    	//если тестовый сервер - не отправлять письмо чиновнику
	    	if(generalConfig.isSelfTest()) {
	    		if(oMail.getBody()!=null && !oMail.getBody().contains("Шановний колего!")) {
	    			sendMailOfTask(oMail, oExecution);
	    			LOG.info("MailTaskWithoutAttachment ok!");
	    		}else {
	    			LOG.info("did not send to clerk body: {}",oMail.getBody());
	    		}
	    		
	    	}else {
	    		sendMailOfTask(oMail, oExecution);
    			LOG.info("MailTaskWithoutAttachment ok!");
	    	}
    }
    
}
