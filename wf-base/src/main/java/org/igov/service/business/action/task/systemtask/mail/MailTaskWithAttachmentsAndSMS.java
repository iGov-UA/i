package org.igov.service.business.action.task.systemtask.mail;

import static org.igov.util.ToolLuna.getProtectedNumber;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataSource;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.task.Attachment;
import org.apache.commons.mail.ByteArrayDataSource;
import org.igov.io.mail.Mail;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
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
        
        Mail oMail = Mail_BaseFromTask(oExecution);
        
        try {
            System.setProperty("mail.mime.address.strict", "false");

            //MultiPartEmail oMultiPartEmail = MultiPartEmail_BaseFromTask(oExecution);
            
            String sPhone_SMS_Value = getStringFromFieldExpression(this.sPhone_SMS, oExecution);
            if (sPhone_SMS_Value != null) {
                String sText_SMS_Value = getStringFromFieldExpression(this.sText_SMS, oExecution);
                if (sText_SMS_Value != null) {
                    sText_SMS_Value = replaceTags(sText_SMS_Value, oExecution);
                    String sReturn;
                    sPhone_SMS_Value = sPhone_SMS_Value.replaceAll("\\ ", "");

                    //sReturn = oManagerSMS.sendSMS(generalConfig.getOrderId_ByOrder(getProtectedNumber(Long.valueOf(oExecution.getProcessInstanceId()))),
                    //        sPhone_SMS_Value, sText_SMS_Value);
                    //if (generalConfig.isSelfTest()) {
                    //    sReturn = "DISABLED!";
                    //} else {
                    sReturn = ManagerSMS.sendSms(sPhone_SMS_Value, sText_SMS_Value, 
                         generalConfig.getOrderId_ByOrder(getProtectedNumber(Long.valueOf(oExecution.getProcessInstanceId()))), generalConfig.isTest_LiqPay());//, generalConfig.isSelfTest()
     
                    //} 
                    LOG.info("(sReturn={})", sReturn);
                }
            }
            
        } catch (Exception ex) {
            LOG.error("Eror during sms sending in MailTaskWithAttachmentsAndSMS:", ex);
            throw ex;
        }
        
        String sAttachmentsForSend = getStringFromFieldExpression(this.saAttachmentsForSend, oExecution);
        
        
        try{
            
            LOG.info("sOldAttachmentsForSend: in MailTaskWithAttachmentsAndSMS: " + sAttachmentsForSend.trim());

            String sOldAttachmentsForSend = sAttachmentsForSend.replaceAll("\\{(.*?)\\}\\,", "").replaceAll("\\{(.*?)\\}", "")
                    .replaceAll("^\"|\"$", "").trim();
            LOG.info("MailTaskWithAttachmentsAndSMS: " + sOldAttachmentsForSend);
            
            if(!sOldAttachmentsForSend.trim().equals(""))
            {
                List<Attachment> aAttachment = findAddedAttachments(sOldAttachmentsForSend, oExecution.getId());
                if (!aAttachment.isEmpty()) {
                    InputStream oInputStream_Attachment = null;
                    String sFileName;
                    String sFileExt;
                    String sDescription;
                    for (Attachment oAttachment : aAttachment) {
                        sFileName = oAttachment.getName();
                        String sExt="";
                        int nAt=sFileName.lastIndexOf(".");
                        if(nAt>=0){
                            sExt = sFileName.substring(nAt);
                        }

                        if (sFileName != null && !sFileName.toLowerCase().endsWith(".xml") && !sFileName.toLowerCase().endsWith(".rpl")) {
                            sFileName = "Attach_" + oAttachment.getId()+sExt; //
                        }

                        sFileExt = oAttachment.getType().split(";")[0];
                        sDescription = oAttachment.getDescription();
                        if (sDescription == null || "".equals(sDescription.trim())) {
                            sDescription = "(no description)";
                        }
                        LOG.info("Old attach whith name: " + sFileName + " and with id: " + oAttachment.getId()); 

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
            }
        }
        catch(Exception ex){
            LOG.info("Error during old file mail processing ", ex);
        }
        
        try{
            LOG.info ("sAttachmentsForSend after parsing: " + sAttachmentsForSend);
            JSONObject oJsonTaskAttachVO = null;
            JSONParser parser = new JSONParser(); 
            
            Pattern pattern = Pattern.compile("\\{(.*?)\\}");
            Matcher match = pattern.matcher(sAttachmentsForSend);
            
            while(match.find())
            {
               String sJsonAttach = match.group(0);
               LOG.info("match.group : " + sJsonAttach);
               try {
                    oJsonTaskAttachVO = (JSONObject)parser.parse(sJsonAttach);
                    MultipartFile oMultipartFile = null;

                    if(oJsonTaskAttachVO != null && oJsonTaskAttachVO.get("sID_StorageType") != null)
                    {
                        String sFileName = (String)oJsonTaskAttachVO.get("sFileNameAndExt");

                        oMultipartFile = oAttachmetService
                            .getAttachment(null, null, (String)oJsonTaskAttachVO.get("sKey"), (String)oJsonTaskAttachVO.get("sID_StorageType"));

                        InputStream oInputStream_Attachment = oMultipartFile.getInputStream();

                        DataSource oDataSource = new ByteArrayDataSource(oInputStream_Attachment, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        oMail._Attach(oDataSource, sFileName, "(no description)");
                        LOG.info("oMultiPartEmail.attach was sending by new schema!");

                    }
                    else
                    {
                        LOG.error("aAttachment has nothing!");
                        throw new ActivitiObjectNotFoundException("add the file to send");
                    }

                } catch (Exception ex) {
                    LOG.info("There aren't TaskAttachVO objects in mail - JSON parsing error: ", ex);
                }
           }
        }
        catch(Exception ex){
            LOG.info("Error during new file mail processing ", ex);
        }

        //oMail.send();
        sendMailOfTask(oMail, oExecution);
            
            /*
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
            LOG.info("email send ok!"); */

    }

}
