package org.igov.service.business.action.task.systemtask.mail;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.task.Attachment;
import org.apache.commons.mail.ByteArrayDataSource;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.igov.io.mail.Mail;

import javax.activation.DataSource;
import java.io.InputStream;
import java.util.List;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;

/**
 * @author BW
 */
@Component("mailTaskWithAttachment")
public class MailTaskWithAttachment extends Abstract_MailTaskCustom {

    private final static Logger LOG = LoggerFactory.getLogger(MailTaskWithAttachment.class);

    private Expression docName;

    @Override
    public void execute(DelegateExecution oExecution) throws Exception {

        //MultiPartEmail oMultiPartEmail = MultiPartEmail_BaseFromTask(oExecution);
        Mail oMail = Mail_BaseFromTask(oExecution);

        List<Attachment> aAttachment = oExecution.getEngineServices().getTaskService()
                .getProcessInstanceAttachments(oExecution.getProcessInstanceId());
        InputStream oInputStream_Attachment = null;
        String sFileName = "document";
        String sFileExt = "txt";
        for (Attachment oAttachment : aAttachment) {
            sFileName = oAttachment.getName(); 
            String sExt="";
            int nAt=sFileName.lastIndexOf(".");
            if(nAt>=0){
                sExt = sFileName.substring(nAt);
            }
            
            if (sFileName != null && !sFileName.toLowerCase().endsWith(".xml") && !sFileName.toLowerCase().endsWith(".rpl")) {
                sFileName = "Attach_"+oAttachment.getId()+sExt; //
            }
            
            sFileExt = oAttachment.getType();
            oInputStream_Attachment = oExecution.getEngineServices().getTaskService()
                    .getAttachmentContent(oAttachment.getId());
            if (oInputStream_Attachment == null) {
                throw new ActivitiObjectNotFoundException(
                        "Attachment with id '" + oAttachment.getId() + "' doesn't have content associated with it.",
                        Attachment.class);
            }
        }
        String sAttachName = getStringFromFieldExpression(this.docName, oExecution);
        LOG.info("(sAttachName={})", sAttachName);
        if (aAttachment != null && !aAttachment.isEmpty()) {
            String sFileExtNew = sFileExt.startsWith("imag") ? sFileExt.substring(11) : sFileExt.substring(25);
            DataSource oDataSource = new ByteArrayDataSource(oInputStream_Attachment, "application/" + sFileExtNew);
            // add the attachment
            LOG.info("(sFileName={})", sFileName);
            LOG.info("(sFileExt={})", sFileExt);
            LOG.info("(sFileExtNew={})", sFileExtNew);

            oMail._Attach(oDataSource, sFileName + "." + sFileExtNew, sAttachName);
            //oMultiPartEmail.attach(oDataSource, sFileName + "." + sFileExtNew, sAttachName);
        } else {
            throw new ActivitiObjectNotFoundException("add the file to send");
        }

        // send the email
        //oMultiPartEmail.send();
        oMail.send();
    }

}
