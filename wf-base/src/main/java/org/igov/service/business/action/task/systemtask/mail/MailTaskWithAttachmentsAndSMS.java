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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.activiti.engine.delegate.JavaDelegate;
import org.igov.service.business.action.mail.MailTaskWithAttachmentsAndSMSService;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author BW
 */
@Component("MailTaskWithAttachmentsAndSMS")
public class MailTaskWithAttachmentsAndSMS extends Abstract_MailTaskCustom implements JavaDelegate{

    /*private Expression saAttachmentsForSend;
    protected Expression sPhone_SMS;
    protected Expression sText_SMS;*/

    @Autowired
    MailTaskWithAttachmentsAndSMSService oMailTaskWithAttachmentsAndSMSService;
    
    @Override
    public void execute(DelegateExecution oExecution) throws Exception {
        oMailTaskWithAttachmentsAndSMSService.MailTaskWithAttachmentsAndSMS(oExecution);
    }

}
