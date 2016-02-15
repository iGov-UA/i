package org.igov.service.business.escalation.handler;

import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.util.Tool;

import java.util.Map;
import static org.igov.io.fs.FileSystemData.getFileData_Pattern;

@Component("EscalationHandler_SendMailAlert")
public class EscalationHandler_SendMailAlert implements EscalationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalationHandler_SendMailAlert.class);
    
    @Autowired
    GeneralConfig oGeneralConfig;
    @Autowired
    private Mail oMail;

    @Override
    public void execute(Map<String, Object> mParam, String[] asRecipientMail, String sPatternFile) throws Exception {
        String sBody = null;
        try {
            byte[] bytes = getFileData_Pattern(sPatternFile);
            sBody = Tool.sData(bytes);
        } catch (Exception oException) {
            LOG.warn("Can't get pattern-file: {} (sPatternFile={})", oException.getMessage(), sPatternFile);
            LOG.debug("FAIL:", oException);
        }
        if (sBody == null) {
            sBody = "[aField]";
        }
        if(sBody.contains("[aField]")){
            sBody = sBody.concat("<br>");
            for (String sKey : mParam.keySet()) {
                sBody = sBody.concat(sKey+"="+mParam.get(sKey)+"<br>");
            }
        }
        
        String sHead = String
                .format((oGeneralConfig.bTest() ? "(TEST)" : "") + "Зависла заявка № %s:%s ! Прийміть міри!",
                        mParam.get("sID_BP"),
                        mParam.get("nID_task_activiti").toString());

        for (String sKey : mParam.keySet()) {
            if (sBody.contains(sKey) && mParam.get(sKey) != null) {
                String s = "";
                try{
                    s = mParam.get(sKey)+"";
                    if(s==null){
                        s="";
                    }
                }catch(Exception oException){
                    LOG.warn("Can't get param for replace tag: {}, (sKey={},mParam={})", oException.getMessage(), sKey, mParam.toString());
                    LOG.debug("FAIL:", oException);
                }
                LOG.debug("Replace tag to param-value (sKey={}, mParam.get(sKey)={})", sKey, s);
                sBody = sBody.replace("[" + sKey + "]", s);
            }
        }
        LOG.info("Sending... (asRecipientMail={}, sHead={})", asRecipientMail, sHead);
        int nFailSend=0;
        if(asRecipientMail != null){
            for (String sRecipientMail : asRecipientMail) {
                try {
                    sendEmail(sHead, sBody, sRecipientMail);
                } catch (Exception e) {
                    LOG.error("Can't send: {} (sRecipientMail={}, sHead={})", e.getMessage(), sRecipientMail, sHead);
                    LOG.debug("FAIL:", e);
                    nFailSend++;
                }
            }
        }
        if(nFailSend>0){
            throw new Exception("Has fails! (nFailSend="+nFailSend+")");
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
