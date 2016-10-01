package org.igov.service.business.action.task.listener.doc;

import java.nio.charset.StandardCharsets;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component("SendDocument_SWinEd")
public class SendDocument_SWinEd extends AbstractModelTask implements TaskListener {
    
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(SendDocument_SWinEd.class);
    
    @Autowired
    RuntimeService runtimeService;
    
    @Autowired
    TaskService taskService;
    
    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    FormService formService;

    @Autowired
    private IBytesDataStorage durableBytesDataStorage;
    
    private Expression sID_File_XML_SWinEd;
    
    @Override
    public void notify(DelegateTask delegateTask) {

        //достать по ид атача ид в монге и достать контент из монги.
        DelegateExecution execution = delegateTask.getExecution();
        String sID_File_XML_SWinEdValue = getStringFromFieldExpression(this.sID_File_XML_SWinEd, execution);
        try {
            LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue);
            byte[] oFile_XML_SWinEd = durableBytesDataStorage.getData(sID_File_XML_SWinEdValue);
            if (oFile_XML_SWinEd != null) {
                String content = new String(oFile_XML_SWinEd);
                String body = createBody(content);
                LOG.info("body: " + body);
                String URL = "http://217.76.198.151/Websrvgate/gate.asmx";
                HttpHeaders headers = new HttpHeaders();
                //headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
                headers.set("Content-Type", "text/xml; charset=utf-8");
                headers.set("SOAPAction", "http://govgate/Send");
                String resp = new RestRequest().post(URL, body,
                        null, StandardCharsets.UTF_8, String.class, headers);
                
                LOG.info("Ukrdoc response:" + resp);
                execution.setVariable("result", "[none]");
            } else {
                LOG.info("oFile_XML_SWinEd is null!!!");
            }
            //поместить тело в хмл и отправить рест запрос
            //сохранение результата в поле процесса
        } catch (Exception ex) {
            LOG.error("!!! Error/ Cfn't get attach from DataStorage with sID_File_XML_SWinEdValue=" + sID_File_XML_SWinEdValue, ex);
        }
    }
    
    private String createBody(String content) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n")
                .append("<soap:Body>")
                .append("<Send xmlns=\"http://govgate/\">")
                .append("<fileName>fileName</fileName>")
                .append("<senderEMail>olga.kuzminova87@gmail.com</senderEMail>")
                .append("<data>").append(content).append("</data>")
                .append("</Send>")
                .append("</soap:Body>")
                .append("</soap:Envelope>").toString();
        return result;
    }
    
}
