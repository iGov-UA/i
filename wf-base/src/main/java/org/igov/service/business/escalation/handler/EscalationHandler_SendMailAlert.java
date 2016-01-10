package org.igov.service.business.escalation.handler;

import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.util.Util;

import java.io.IOException;
import java.util.Map;

@Component("EscalationHandler_SendMailAlert")
public class EscalationHandler_SendMailAlert implements EscalationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHandler_SendMailAlert.class);
    @Autowired
    GeneralConfig oGeneralConfig;
    @Autowired
    private Mail oMail;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) {
        //create email body
        String sBody = null;
        try {
            byte[] bytes = Util.getPatternFile(sPatternFile);
            sBody = Util.sData(bytes);
        } catch (IOException e) {
            LOG.error("error during finding the pattern file! path=" + sPatternFile, e);
        }
        if (sBody == null) {
            sBody = "[aField]";
        }
        
        if(sBody.contains("[aField]")){
            sBody = sBody.concat("<br>");
            for (String key : mParam.keySet()) {
                sBody = sBody.concat(key+"="+mParam.get(key)+"<br>");
            }
        }
        
        String sHead = String
                .format((oGeneralConfig.bTest() ? "(TEST)" : "") + "Зависла заявка № %s:%s ! Прийміть міри!",
                        mParam.get("sID_BP"),
                        mParam.get("nID_task_activiti").toString());

        for (String sKey : mParam.keySet()) {
            if (sBody.contains(sKey) && mParam.get(sKey) != null) {
                LOG.info("replace key [" + sKey + "] by value " + mParam.get(sKey));
                //s = (String) mParam.get(key);
                String s = "";
                try{
                    s = mParam.get(sKey)+"";
                    if(s==null){
                        s="";
                    }
                }catch(Exception oException){
                    LOG.warn("cast key [" + sKey + "]: " + oException.getMessage());
                }
                sBody = sBody.replace("[" + sKey + "]", s);
                //sBody = sBody.replace("[" + key + "]", mParam.get(key).toString());
            }
        }
        LOG.info("@Autowired oMail=" + oMail);
        oMail = oMail == null ? new Mail() : oMail;
        LOG.info("oMail=" + oMail);
        for (String recipient : asRecipientMail) {
            try {
                sendEmail(sHead, sBody, recipient);
            } catch (EmailException e) {
                LOG.error("error sending email!", e);
            }
        }

    }

    private void sendEmail(String sHead, String sBody, String recipient) throws EmailException {

        oMail.reset();
        oMail
                ._To(recipient)
                ._Head(sHead)
                ._Body(sBody);
        oMail.send();
    }
}
