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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

        List<String> asnTaskId = oActionTaskService.
                getTaskIdsByProcessInstanceId(oExecution.getProcessInstanceId());
        Map<String, Object> mOnlyDateVariables = new HashMap<>();
        asnTaskId.forEach(snTaskId -> {
            FormData oFormData = oExecution.getEngineServices()
                    .getFormService().getTaskFormData(snTaskId);

            if (oFormData != null) {
                List<FormProperty> aoFormProperties = oFormData.getFormProperties();
                aoFormProperties.forEach(oFormProperty -> {
                    String sFormPropertyTypeName = oFormProperty.getType().getName();
                    String sFormPropertyId = oFormProperty.getId();
                    String sFormPropertyValue = oFormProperty.getValue();

                    if (sFormPropertyTypeName.equals("date")) {
                        LOG.info("Date catched. id={}, value={}",
                                sFormPropertyId, sFormPropertyValue);

                        DateTime oDateTime = DateTime.parse(sFormPropertyValue,
                                DateTimeFormat.forPattern("dd/MM/yyyy"));
                        String sDate = oDateTime.toString("dd MMMM yyyy", new Locale("uk", "UA"));
                        LOG.info("sDate formated={}", sDate);
                        mOnlyDateVariables.put(sFormPropertyId, sDate);

                    } else if (sFormPropertyTypeName.equals("queueData")) {
                        LOG.info("queueData catched.id={}, value={}",
                                sFormPropertyId, sFormPropertyValue);
                    }
                });
            }
        });
        LOG.info("mOnlyDateVariables={}", mOnlyDateVariables);
        oExecution.setVariables(mOnlyDateVariables);
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
