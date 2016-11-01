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
//import org.igov.io.sms.ManagerSMS_New;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

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

            String sPhone_SMS = getStringFromFieldExpression(this.sPhone_SMS, oExecution);
            if (sPhone_SMS != null) {
                String sText_SMS = getStringFromFieldExpression(this.sText_SMS, oExecution);
                if (sText_SMS != null) {
                    sText_SMS = replaceTags(sText_SMS, oExecution);
                    String sReturn;
                    sPhone_SMS = sPhone_SMS.replaceAll("\\ ", "");

                    sReturn = oManagerSMS.sendSMS(generalConfig.getOrderId_ByOrder(getProtectedNumber(Long.valueOf(oExecution.getProcessInstanceId()))),
                	    sPhone_SMS, sText_SMS);
                    LOG.info("(sReturn={})", sReturn);
                }
            }
            LOG.info("sAttachmentsForSend...");
            String sAttachmentsForSend = this.saAttachmentsForSend == null || "".equals(this.saAttachmentsForSend) ? "" : getStringFromFieldExpression(this.saAttachmentsForSend, oExecution);
            LOG.info("(sAttachmentsForSend={})", sAttachmentsForSend);
            List<Attachment> aAttachment = new ArrayList<>();
            String[] asID_Attachment = sAttachmentsForSend.split(",");
            for (String sID_Attachment : asID_Attachment) {
                //log.info("sID_Attachment=" + sID_Attachment);
                if (sID_Attachment != null && !"".equals(sID_Attachment.trim()) && !"null".equals(sID_Attachment.trim())) {
                    String sID_AttachmentTrimmed = sID_Attachment.replaceAll("^\"|\"$", "");
                    LOG.info("(sID_AttachmentTrimmed={})", sID_AttachmentTrimmed);
                    Attachment oAttachment = taskService.getAttachment(sID_AttachmentTrimmed);
                    if (oAttachment != null) {
                        aAttachment.add(oAttachment);
                    }
                } else {
                    LOG.warn("(sID_Attachment={})", sID_Attachment);
                }
            }

            if (!aAttachment.isEmpty()) {
                InputStream oInputStream_Attachment = null;
                String sFileName = "document";
                String sFileExt = "txt";
                String sDescription = "";
                for (Attachment oAttachment : aAttachment) {
                    sFileName = oAttachment.getName();
                    String sExt = "";
                    int nAt = sFileName.lastIndexOf(".");
                    if (nAt >= 0) {
                        sExt = sFileName.substring(nAt);
                    }
                    sFileName = "Attach_" + oAttachment.getId() + sExt; //
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
                    if (oDataSource == null) {
                        LOG.error("Attachment: oDataSource == null");
                    }

                    //oMail._Attach(oDataSource, sFileName + "." + sFileExt, sDescription);
                    oMail._Attach(oDataSource, sFileName, sDescription);

                    LOG.info("oMultiPartEmail.attach: Ok!");
                }
            }
            LOG.info("sAttachmentsForSend ok!");
            LOG.info("email send...");
            oMail.send();
            LOG.info("email send ok!");
        } catch (Exception ex) {
            LOG.error("Eror!!!", ex);
            throw ex;
        }
    }

}
