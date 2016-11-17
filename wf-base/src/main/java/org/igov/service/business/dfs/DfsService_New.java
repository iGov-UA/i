/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.dfs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.rpc.holders.ByteArrayHolder;
import javax.xml.rpc.holders.StringHolder;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.igov.service.controller.ActionTaskCommonController;
import org.igov.util.swind.GateSoapProxy;
import org.igov.util.swind.ProcessResult;
import org.igov.util.swind.holders.ArrayOfStringHolder;
import org.igov.util.swind.holders.ProcessResultHolder;

/**
 *
 * @author olga
 */
@Service
public class DfsService_New {

    private static final Logger LOG = LoggerFactory.getLogger(DfsService_New.class);

    private final static String URL = "http://109.237.89.107:1220/gate.asmx";

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    public String getAnswer(String sID_Task, String sID_Process, String sINN) throws Exception {
        StringBuilder asID_Attach_Dfs = new StringBuilder();
        List<ByteArrayMultipartFile> multipartFiles = getAnswer(sINN);
        LOG.info("multipartFiles.size: " + multipartFiles.size());
        try {
            Attachment attachmentDocument = taskService.getAttachment((String) runtimeService.getVariable(sID_Process, "oFile_XML_SWinEd"));
            if (attachmentDocument != null) {
                String attachmentDocumentName = attachmentDocument.getName();
                LOG.info("attachmentDocumentName: " + attachmentDocumentName);
                attachmentDocumentName = attachmentDocumentName.replaceAll(".xml", "");
                for (ByteArrayMultipartFile multipartFile : multipartFiles) {
                    LOG.info("multipartFile.getOriginalFilename(): " + multipartFile.getOriginalFilename()
                            + " attachmentDocumentName: " + attachmentDocumentName);
                    if (multipartFile.getOriginalFilename().contains(attachmentDocumentName)) {
                        Attachment attachment = taskService.createAttachment(multipartFile.getContentType() + ";" + multipartFile.getExp(),
                                sID_Task, sID_Process,
                                multipartFile.getOriginalFilename(), multipartFile.getName(), multipartFile.getInputStream());
                        if (attachment != null) {
                            asID_Attach_Dfs.append(attachment.getId()).append(",");
                            LOG.info("attachment: " + attachment.getId());
                        }
                    } else{
                        LOG.info("SKIP multipartFile.getOriginalFilename(): " + multipartFile.getOriginalFilename()
                            + " attachmentDocumentName: " + attachmentDocumentName);
                    }
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ActionTaskCommonController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (asID_Attach_Dfs.length() > 0) {
            String sID_Attach_Dfs = asID_Attach_Dfs.deleteCharAt(asID_Attach_Dfs.length() - 1).toString();
            runtimeService.setVariable(sID_Process, "anID_Attach_Dfs", sID_Attach_Dfs);
            taskService.setVariable(sID_Task, "anID_Attach_Dfs", sID_Attach_Dfs);
        } else{
            runtimeService.setVariable(sID_Process, "anID_Attach_Dfs", " ");
            taskService.setVariable(sID_Task, "anID_Attach_Dfs", " ");
        }
        return asID_Attach_Dfs.toString();
    }

    public List<ByteArrayMultipartFile> getAnswer(String INN) throws Exception {
        List<ByteArrayMultipartFile> result = new ArrayList<>();
        GateSoapProxy gate = new GateSoapProxy(URL);
        ProcessResultHolder receiveResult = new ProcessResultHolder();
        ArrayOfStringHolder messagesList = new ArrayOfStringHolder();
        gate.getMessages(INN.getBytes(), receiveResult, messagesList);
        LOG.info("messagesList: " + messagesList);
        for (String resultMessage : messagesList.value) {
            if (resultMessage != null) {
                receiveResult = new ProcessResultHolder();
                StringHolder fileName = new StringHolder();
                ByteArrayHolder messageData = new ByteArrayHolder();
                gate.receive(resultMessage.getBytes(), receiveResult, fileName, messageData);
                LOG.info("receive fileName: " + fileName);
                if (fileName.value != null && messageData.value != null && messageData.value.length > 0) {
                        ByteArrayMultipartFile oByteArrayMultipartFile = new ByteArrayMultipartFile(messageData.value, fileName.value,
                                fileName.value, "text/plain");
                        result.add(oByteArrayMultipartFile);
                        ProcessResult deleteResult = gate.delete(resultMessage.getBytes());
                        LOG.info("deleteResult: " + deleteResult.getValue());
                }
            }
        }
        return result;
    }
}
