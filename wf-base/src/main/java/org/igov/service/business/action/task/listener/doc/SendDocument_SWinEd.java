package org.igov.service.business.action.task.listener.doc;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.activiti.engine.FormService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.web.HttpRequester;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.igov.service.business.action.task.core.ActionTaskService;
import static org.igov.util.ToolWeb.base64_encode;
import org.igov.util.VariableMultipartFile;
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

    //@Autowired
    //private IBytesDataStorage durableBytesDataStorage;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;

    private Expression sID_File_XML_SWinEd;

    @Autowired
    private ActionTaskService oActionTaskService;
    
    @Autowired
    private HttpRequester oHttpRequester;

    @Override
    public void notify(DelegateTask delegateTask) {

        //достать по ид атача ид в монге и достать контент из монги.
        DelegateExecution execution = delegateTask.getExecution();
        String sID_File_XML_SWinEdValue = getStringFromFieldExpression(this.sID_File_XML_SWinEd, execution);
        String resp = "[none]";
        String URL = "http://217.76.198.151/Websrvgate/gate.asmx";
        try {
            LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue);
            byte[] oFile_XML_SWinEd = oBytesDataInmemoryStorage.getBytes(sID_File_XML_SWinEdValue);
            if (oFile_XML_SWinEd != null) {
                String content = new String(oFile_XML_SWinEd);
                resp += " content: " + content;
                LOG.info("content: " + content);
                resp = oHttpRequester.postInside(URL, null, createBody(content), "text/xml; charset=utf-8");
                LOG.info("!!!response:" + resp);
            } else {
                LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue + " oFile_XML_SWinEd is null!!!");
            }
            execution.setVariable("result", resp);
        } catch (Exception ex) {
            LOG.error("!!! Error in SendDocument_SWinEd sID_File_XML_SWinEdValue=" + sID_File_XML_SWinEdValue, ex);
            execution.setVariable("result", resp);
        }
    }

    public void notify_old(DelegateTask delegateTask) {

        //достать по ид атача ид в монге и достать контент из монги.
        DelegateExecution execution = delegateTask.getExecution();
        String sID_File_XML_SWinEdValue = getStringFromFieldExpression(this.sID_File_XML_SWinEd, execution);
        try {
            byte[] oFile_XML_SWinEd = oBytesDataInmemoryStorage.getBytes(sID_File_XML_SWinEdValue);
            //byte[] oFile_XML_SWinEd = durableBytesDataStorage.getData(sID_File_XML_SWinEdValue);
            // Выбираем по процессу прикрепленные файлы
            String resp = "delegateTask.getId(): " + delegateTask.getId() + " delegateTask.getProcessInstanceId(): " + delegateTask.getProcessInstanceId();
            LOG.info("sID_File_XML_SWinEdValue: " + sID_File_XML_SWinEdValue + " resp: " + resp);
            Attachment attachmentRequested;
            try {
                attachmentRequested = oActionTaskService.getAttachment(sID_File_XML_SWinEdValue, 1, delegateTask.getProcessInstanceId());
                String sFileName = attachmentRequested.getName();
                String description = attachmentRequested.getDescription();
                String type = attachmentRequested.getType();
                String id = attachmentRequested.getId();
                resp = resp + " id: " + id;
                InputStream attachmentStream = taskService
                        .getAttachmentContent(id);
                VariableMultipartFile multipartFile = new VariableMultipartFile(
                        attachmentStream, description,
                        sFileName, type);
                if (multipartFile.getBytes() != null) {
                    String content = new String(multipartFile.getBytes());
                    resp += " content: " + content;
                    LOG.info("content: " + content);
                    String body = createBody(content);
                    LOG.info("body: " + body);
                    String URL = "http://217.76.198.151/Websrvgate/gate.asmx";
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "text/xml; charset=utf-8");
                    headers.set("SOAPAction", "http://govgate/Send");
                    resp = new RestRequest().post(URL, body,
                            null, StandardCharsets.UTF_8, String.class, headers);
                    LOG.info("Ukrdoc response:" + resp);
                } else {
                    LOG.info("oFile_XML_SWinEd is null!!!");
                }
                execution.setVariable("result", resp);
            } catch (Exception ex) {
                LOG.error("error: ", ex);
                LOG.info("!!!getProcessInstanceAttachments: " + taskService.getProcessInstanceAttachments(delegateTask.getProcessInstanceId()));
                LOG.info("!!!getTaskAttachments: " + taskService.getTaskAttachments(delegateTask.getId()));
            }
            execution.setVariable("result", resp);
        } catch (Exception ex) {
            LOG.error("!!! Error/ Cfn't get attach from DataStorage with sID_File_XML_SWinEdValue=" + sID_File_XML_SWinEdValue, ex);
        }
    }

    private String createBody(String content) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">")
                .append("<soap:Body>")
                .append("<Send xmlns=\"http://govgate/\">")
                .append("<fileName>23013194700944F1301801100000000151220152301.xml</fileName>")
                .append("<senderEMail>olga.kuzminova87@gmail.com</senderEMail>")
                .append("<data>").append(base64_encode(content)).append("</data>")
                //.append("<data>").append(content).append("</data>")
                .append("</Send>")
                .append("</soap:Body>")
                .append("</soap:Envelope>").toString();
        return result;
    }

}
