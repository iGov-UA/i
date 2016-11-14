package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.task.Attachment;
import org.apache.commons.mail.ByteArrayDataSource;
import org.springframework.stereotype.Component;
import org.igov.io.mail.Mail;

import javax.activation.DataSource;

import static org.igov.util.ToolLuna.getProtectedNumber;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
//import org.igov.io.sms.ManagerSMS_New;

/**
 * @author BW
 */
@Component("MailTaskWithAttachmentsAndSMS")
public class MailTaskWithAttachmentsAndSMS extends Abstract_MailTaskCustom {

    //private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithAttachments.class);
    private Expression saAttachmentsForSend;

    protected Expression sPhone_SMS;
    protected Expression sText_SMS;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        try {
            System.setProperty("mail.mime.address.strict", "false");

            //MultiPartEmail oMultiPartEmail = MultiPartEmail_BaseFromTask(oExecution);
            Mail oMail = Mail_BaseFromTask(oExecution);

            String sPhone_SMS_Value = getStringFromFieldExpression(this.sPhone_SMS, oExecution);
            if (sPhone_SMS_Value != null) {
                String sText_SMS_Value = getStringFromFieldExpression(this.sText_SMS, oExecution);
                if (sText_SMS_Value != null) {
                    sText_SMS_Value = replaceTags(sText_SMS_Value, oExecution);
                    String sReturn;
                    sPhone_SMS_Value = sPhone_SMS_Value.replaceAll("\\ ", "");

                    //sReturn = oManagerSMS.sendSMS(generalConfig.getOrderId_ByOrder(getProtectedNumber(Long.valueOf(oExecution.getProcessInstanceId()))),
                    //        sPhone_SMS_Value, sText_SMS_Value);
                    
                    sReturn = ManagerSMS.sendSms(sPhone_SMS_Value, sText_SMS_Value, 
                            generalConfig.getOrderId_ByOrder(getProtectedNumber(Long.valueOf(oExecution.getProcessInstanceId()))), true);
                    
                    LOG.info("(sReturn={})", sReturn);
                }
            }
            String sAttachmentsForSend = getStringFromFieldExpression(this.saAttachmentsForSend, oExecution);
            LOG.info("sAttachmentsForSend on " + oMail.getTo() + " sAttachmentsForSend: " + sAttachmentsForSend);
            sAttachmentsForSend = sAttachmentsForSend == null ? "" : sAttachmentsForSend;
            List<Attachment> aAttachment = new ArrayList<>();
            String[] asID_Attachment = sAttachmentsForSend.split(",");
            for (String sID_Attachment : asID_Attachment) {
                LOG.info("sID_Attachment=" + sID_Attachment);
                if (sID_Attachment != null && !"".equals(sID_Attachment.trim()) && !"null".equals(sID_Attachment.trim())) {
                    String sID_AttachmentTrimmed = sID_Attachment.replaceAll("^\"|\"$", "");
                    if (!"".equals(sID_AttachmentTrimmed)) {
                        LOG.info("(sID_AttachmentTrimmed={})", sID_AttachmentTrimmed);
                        Attachment oAttachment = taskService.getAttachment(sID_AttachmentTrimmed);
                        if (oAttachment != null) {
                            aAttachment.add(oAttachment);
                            LOG.info("aAttachment={} add ok!", sID_AttachmentTrimmed);
                        }
                    }
                } else {
                    LOG.warn("sID_Attachment={} is empty!!!", sID_Attachment);
                }
            }

            if (!aAttachment.isEmpty()) {
                InputStream oInputStream_Attachment;
                String sFileName, sFileExt, sDescription;
                for (Attachment oAttachment : aAttachment) {
                    sFileName = oAttachment.getName();
                    String sExt = "";
                    int nAt = sFileName.lastIndexOf(".");
                    if (nAt >= 0) {
                        sExt = sFileName.substring(nAt);
                    }
                    if (sFileName != null && !sFileName.toLowerCase().endsWith(".xml") && !sFileName.toLowerCase().endsWith(".rpl")) {
                        sFileName = "Attach_"+oAttachment.getId()+sExt; //
                    }
                    sFileExt = oAttachment.getType().split(";")[0];
                    sDescription = oAttachment.getDescription();
                    if (sDescription == null || "".equals(sDescription.trim())) {
                        sDescription = "(no description)";
                    }
                    LOG.info("(oAttachment.getId()={}, sFileName={}, sFileExt={}, sDescription={})",
                            oAttachment.getId(), sFileName, sFileExt, sDescription);
                    oInputStream_Attachment = oExecution.getEngineServices().getTaskService()
                            .getAttachmentContent(oAttachment.getId());
                    if (oInputStream_Attachment == null) {
                        LOG.error("Attachment with (id={}) doesn't have content associated with it.", oAttachment.getId());
                        throw new ActivitiObjectNotFoundException(
                                "Attachment with id '" + oAttachment.getId() + "' doesn't have content associated with it.",
                                Attachment.class);
                    }
                    DataSource oDataSource = new ByteArrayDataSource(oInputStream_Attachment, sFileExt);
                    oMail._Attach(oDataSource, sFileName, sDescription);
                    LOG.info("oMultiPartEmail.attach: Ok!");
                }
            }
            LOG.info("sAttachmentsForSend ok!" + " on email " + oMail.getTo());
            LOG.info("email send...");
            oMail.send();
            LOG.info("email send ok!");
        } catch (Exception ex) {
            LOG.error("Eror!!!", ex);
            throw ex;
        }
    }

}
