package org.igov.service.business.action.task.systemtask.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.igov.io.mail.Mail;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.action.task.form.QueueDataFormType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Elena отправка тела без атачмента - html text
 *
 */
@Component("MailTaskWithoutAttachment")
public class MailTaskWithoutAttachment extends Abstract_MailTaskCustom {

    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithoutAttachment.class);

    @Autowired
    ActionTaskService oActionTaskService;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {

        try {
            Map<String, Object> mExecutionVaraibles = oExecution.getVariables();
            LOG.info("mExecutionVaraibles={}", mExecutionVaraibles);

            FormData oFormData = oExecution.getEngineServices()
                    .getFormService().getStartFormData(oExecution.getProcessDefinitionId());
            //переменные, которые будем сетить обратно в екзекьюшен
            Map<String, Object> mOnlyDateVariables = new HashMap<>();
            LOG.info("oFormData size={}", oFormData.getFormProperties().size());

            if (oFormData != null) {
                List<FormProperty> aoFormProperties = oFormData.getFormProperties();
                aoFormProperties.forEach(oFormProperty -> {
                    String sFormPropertyTypeName = oFormProperty.getType().getName();
                    String sFormPropertyId = oFormProperty.getId();
                    String sFormPropertyValue = oFormProperty.getValue();

                    if (sFormPropertyTypeName.equals("date")) {
                        LOG.info("Date catched. id={}, value={}",
                                sFormPropertyId, sFormPropertyValue);
                        Date oDate = (Date) mExecutionVaraibles.get(sFormPropertyId);
                        LOG.info("Date got from execution and casted. {}", oDate);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, kk:mm", new Locale("uk", "UA"));
                        String sDate = sdf.format(oDate);
                        mOnlyDateVariables.put(sFormPropertyId, sDate);

                    } else if (sFormPropertyTypeName.equals("queueData")) {
                        LOG.info("queueData catched.id={}, value={}",
                                sFormPropertyId, sFormPropertyValue);
                        Map<String, Object> mQueueData = QueueDataFormType
                                .parseQueueData((String) mExecutionVaraibles.get(sFormPropertyId));
                        LOG.info("QueueData got from execution {}", mQueueData);
                        String sQueueDate = (String) mQueueData.get("queueData");
                        LOG.info("Got Date from queueData {}", sQueueDate);
                        DateTime oDateTime = DateTime.parse(sQueueDate,
                                DateTimeFormat.forPattern("yyyy-MM-dd kk:mm:ss.SS"));
                        String sDate = oDateTime.toString("dd MMMM yyyy, kk:mm", new Locale("uk", "UA"));
                        LOG.info("sDate formated={}", sDate);
                        mOnlyDateVariables.put(sFormPropertyId, sDate);
                    }
                });
            }
            LOG.info("mOnlyDateVariables={}", mOnlyDateVariables);
            oExecution.setVariables(mOnlyDateVariables);
        } catch (Exception oException) {
            LOG.error("Error: date not formated {}", oException.getMessage());
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
