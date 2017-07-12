package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.delegate.DelegateExecution;
import static org.igov.service.business.action.task.systemtask.mail.Abstract_MailTaskCustom.LOG;
import org.springframework.stereotype.Component;

/**
 * @author Elena отправка тела без атачмента - html text
 *
 */
@Component("MailTaskWithoutAttachment")
public class MailTaskWithoutAttachment extends Abstract_MailTaskCustom {

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        LOG.info("Process id is {} in MailTaskWithoutAttachment {}",
                oExecution.getProcessInstanceId());
        sendMail(oExecution, null);
        LOG.info("MailTaskWithoutAttachment ok!");
    }
    /*private void reWriteCode(DelegateExecution oExecution) throws Exception { //отправка в тексте письма содержимого первого поля на форме типа файлХтмл
        Mail oMail = null;
        String sJsonMongo = loadFormPropertyFromTaskHTMLText(oExecution);
        LOG.info("sJsonMongo: {}", sJsonMongo);
        String sBodyFromMongoResult = getHtmlTextFromMongo(sJsonMongo);
        LOG.info("sBodyFromMongoResult: {}", sBodyFromMongoResult);
        if (sBodyFromMongoResult != null) {
            try {
                oMail = sendToMail_FileHtml(oExecution);
            } catch (Exception ex) {
                LOG.error("MailTaskWithoutAttachment: ", ex);
            }
        } else {
            try {
                oMail = mail_BaseFromTask(oExecution);
            } catch (Exception ex) {
                LOG.error("MailTaskWithoutAttachment: ", ex);
            }
        }
    }*/
    /**
     * Метод получения из монго текст письма
     *
     * @param sJsonHtml
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws RecordInmemoryException
     * @throws ClassNotFoundException
     * @throws CRCInvalidException
     * @throws RecordNotFoundException
     */
    /*public String getHtmlTextFromMongo(String sJsonHtml) throws IOException, ParseException, RecordInmemoryException,
            ClassNotFoundException, CRCInvalidException, RecordNotFoundException {
        String sBodyFromMongo = null;
        JSONObject sJsonHtmlInFormatMongo = new JSONObject(sJsonHtml);
        LOG.info("sJsonHtmlInFormatMongo: {}", sJsonHtmlInFormatMongo);
        try {
            InputStream oAttachmet_InputStream = oAttachmetService.getAttachment(null, null,
                    sJsonHtmlInFormatMongo.getString("sKey"), sJsonHtmlInFormatMongo.getString("sID_StorageType"))
                    .getInputStream();

            sBodyFromMongo = IOUtils.toString(oAttachmet_InputStream, "UTF-8");
        } catch (JSONException e) {
            LOG.error("JSONException: {}", e.getMessage());
            return null;
        }
        return sBodyFromMongo;

    }*/
}
