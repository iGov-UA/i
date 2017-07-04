package org.igov.service.business.action.task.systemtask.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.igov.io.mail.Mail;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Elena отправка тела без атачмента - html text
 *
 */
@Component("MailTaskWithoutAttachment")
public class MailTaskWithoutAttachment extends Abstract_MailTaskCustom {

    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithoutAttachment.class);

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {

        try {
            FormData oTaskFormData = oExecution.getEngineServices()
                    .getFormService()
                    .getStartFormData(oExecution.getProcessDefinitionId());
            
            if (oTaskFormData != null && oTaskFormData.getFormProperties() != null) {                
                Map<String, Object> mOnlyDateVariables = new HashMap<>();              
                for (FormProperty oFormProperty : oTaskFormData.getFormProperties()) {
                    LOG.info("MailTaskWithoutAttachment property (Id={},Name={},Type={},Value={})",
                            oFormProperty.getId(), oFormProperty.getName(),
                            oFormProperty.getType().getName(),
                            oFormProperty.getValue());
                    
                    if (oFormProperty.getType().getName().equalsIgnoreCase("date")) {
                        LOG.info("Date catched");
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM yyyy, kk:mm");
                        DateTime dt = formatter.parseDateTime(oFormProperty.toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, kk:mm", new Locale("uk", "UA"));
                        String sDate = sdf.format((Date) oFormProperty);
                        LOG.info("sDate={}", sDate);
                        mOnlyDateVariables.put(oFormProperty.getId(), sDate);
                    } else if (oFormProperty.getType().getName().equalsIgnoreCase("queueData")) {
                        LOG.info("queueData catched={}", oFormProperty) ;
                    }        
                }
                LOG.info("mOnlyDateVariables={}", mOnlyDateVariables);
            }
        } catch (Exception e) {
            LOG.error("Error: {}, occured while looking for a start form for a process.",
                    e.getMessage());
        }

        /*
    	Map<String, Object> mExecutionVaraibles = oExecution.getVariables();
        LOG.info("mExecutionVaraibles={}", mExecutionVaraibles);
        if (!mExecutionVaraibles.isEmpty()) {
            Map<String, Object> mOnlyDateVariables = new HashMap<>();       
            // выбираем все переменные типа Date, приводим к нужному формату 
            mExecutionVaraibles.forEach((sKey, oValue) -> {
                if (oValue != null) {
                    String sClassName = oValue.getClass().getName();
                    LOG.info("Variables: sClassName={} sKey={} oValue={}", sClassName, sKey, oValue);
                    if (sClassName.endsWith("Date")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, kk:mm", new Locale("uk","UA"));
                        String sDate = sdf.format((Date) oValue);
                        mOnlyDateVariables.put(sKey, sDate);
                    } else if (sClassName.contains("queueData")) {
                        LOG.info("queueData found");
                    }
                }
            });
            //сетим отформатированные переменные в екзекьюшен
            oExecution.setVariables(mOnlyDateVariables);
        }*/
        Mail oMail = null;
        String sJsonMongo = loadFormPropertyFromTaskHTMLText(oExecution);
        LOG.info("sJsonMongo: {}", sJsonMongo);
        String sBodyFromMongoResult = getHtmlTextFromMongo(sJsonMongo);
        LOG.info("sBodyFromMongoResult: {}", sBodyFromMongoResult);
        if (sBodyFromMongoResult != null) {
            try {
                oMail = sendToMailFromMongo(oExecution);
            } catch (Exception ex) {
                LOG.error("MailTaskWithoutAttachment: ", ex);
            }
        } else {
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
