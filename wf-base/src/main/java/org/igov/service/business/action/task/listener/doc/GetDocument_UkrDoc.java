package org.igov.service.business.action.task.listener.doc;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.web.RestRequest;
import org.igov.service.business.action.task.core.AbstractModelTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.activiti.engine.impl.util.json.JSONObject;
import static org.igov.service.business.action.task.core.AbstractModelTask.getStringFromFieldExpression;
import org.igov.service.business.promin.ProminSession_Singleton;
import org.springframework.http.ResponseEntity;

@Component("GetDocument_UkrDoc")
public class GetDocument_UkrDoc extends AbstractModelTask implements TaskListener {

    private final static Logger LOG = LoggerFactory.getLogger(GetDocument_UkrDoc.class);

    private Expression sID_Document;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProminSession_Singleton prominSession_Singleton;

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        String sID_Document = getStringFromFieldExpression(this.sID_Document, execution);

        LOG.info("Parameters of the task sID_Document:{}", sID_Document);

        String sessionId = prominSession_Singleton.getSid_Auth_UkrDoc_SED();

        String[] documentIDs = sID_Document.split(":");
        if (documentIDs.length > 1) {
            String url = String.format("/%s/%s/content", documentIDs[1], documentIDs[0]);

            LOG.info("Retrieved session ID:{} and created URL to request: {}", sessionId, url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "promin.privatbank.ua/EXCL " + sessionId);
            headers.set("Content-Type", "application/json; charset=utf-8");

            String resp = new RestRequest().get(generalConfig.getURL_UkrDoc_SED() + url, null, StandardCharsets.UTF_8, String.class, headers);

            LOG.info("Ukrdoc response getDocument:" + resp);
            JSONObject respJson = new JSONObject(resp);
            Object content = respJson.get("content");
            Object actors = respJson.get("actors");

            if (content != null) {

                String name = (String) ((JSONObject) content).get("name");
                runtimeService.setVariable(execution.getProcessInstanceId(), "sHead_Document_UkrDoc", name);
                String text = (String) ((JSONObject) content).get("text");
                runtimeService.setVariable(execution.getProcessInstanceId(), "sDocument_Body_UkrDoc", text);
                String actor = (String) ((JSONObject) actors).get("id");
                runtimeService.setVariable(execution.getProcessInstanceId(), "sDocument_Actor_UkrDoc", actor);
                JSONArray ratifiers = (JSONArray) ((JSONObject) actors).get("ratifiers");
                if (ratifiers != null && ratifiers.length() > 0) {
                    JSONObject ratifier = (JSONObject) ratifiers.get(0);
                    runtimeService.setVariable(execution.getProcessInstanceId(), "sDocument_Ratifier_UkrDoc", ratifier.get("id"));
                } else {
                    runtimeService.setVariable(execution.getProcessInstanceId(), "sDocument_Ratifier_UkrDoc", "None");
                }
                try {
                    LOG.info("class: " + ((JSONObject) ((JSONObject) content).get("extensions")).get("files").getClass());
                    JSONArray files = (JSONArray) ((JSONObject) ((JSONObject) content).get("extensions")).get("files");
                    //получение контента файла и прикрипление его в качестве атача к таске
                    if (files != null && files.length() > 0) {
                        StringBuilder anID_Attach_UkrDoc = new StringBuilder();
                        for (int i = 0; i < files.length(); i++) {
                            JSONObject file = (JSONObject) files.get(i);
                            String view_url = file.getString("view_url").replaceFirst("/docs", ""); //docs/2016/10300131/files/10300000/content?type=.jpg
                            String fileNameOrigin = file.getString("file"); //a10300000.jpg
                            String fileName = file.getString("name");

                            LOG.info("view_url:" + generalConfig.getURL_UkrDoc_SED() + view_url + " fileName: " + fileName);
                            ResponseEntity<byte[]> responseEntity = new RestRequest().getResponseEntity(generalConfig.getURL_UkrDoc_SED() + view_url, MediaType.APPLICATION_JSON,
                                    StandardCharsets.UTF_8, byte[].class, headers);
                            LOG.info("Ukrdoc response getContentFile getBody: " + responseEntity.getBody()
                                    + " getHeaders: " + responseEntity.getHeaders().entrySet());
                            try {
                                //ByteArrayMultipartFile oByteArrayMultipartFile
                                //        = new ByteArrayMultipartFile(contentStringToByte(resp), fileName, fileNameOrigin, "application/octet-stream");
                                ByteArrayMultipartFile oByteArrayMultipartFile
                                        = new ByteArrayMultipartFile(responseEntity.getBody(), fileName, fileNameOrigin, responseEntity.getHeaders().getContentType().toString());

                                Attachment attachment = taskService.createAttachment(oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp(),
                                        delegateTask.getId(), execution.getProcessInstanceId(),
                                        fileNameOrigin, fileName, oByteArrayMultipartFile.getInputStream());

                                if (attachment != null) {
                                    anID_Attach_UkrDoc.append(attachment.getId()).append(",");
                                    LOG.info("attachment: " + attachment.getId());
                                }
                            } catch (Exception ex) {
                                java.util.logging.Logger.getLogger(GetDocument_UkrDoc.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (anID_Attach_UkrDoc.length() > 0) {
                            String sID_Attach_UkrDoc = anID_Attach_UkrDoc.deleteCharAt(anID_Attach_UkrDoc.length() - 1).toString();
                            runtimeService.setVariable(execution.getProcessInstanceId(), "anID_Attach_UkrDoc", sID_Attach_UkrDoc);
                            taskService.setVariable(delegateTask.getId(), "anID_Attach_UkrDoc", sID_Attach_UkrDoc);
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("error getFiles: ", ex);
                }

            }
        }
        LOG.info("close task authomaticaly: " + delegateTask.getId() + "...");
        //taskService.complete(delegateTask.getId(), new HashMap());
        LOG.info("close task authomaticaly: " + delegateTask.getId() + " ok!");
    }
}
