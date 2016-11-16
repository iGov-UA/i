/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.dfs;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.web.HttpRequester;
import org.igov.service.exception.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.igov.util.ToolWeb.base64_encode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.commons.codec.binary.Base64;
import org.igov.service.business.action.task.listener.doc.SendDocument_SWinEd;
import org.igov.service.controller.ActionTaskCommonController;

/**
 *
 * @author olga
 */
@Service
public class DfsService {

    private static final Logger LOG = LoggerFactory.getLogger(DfsService.class);

    private final static String CONTENT_TYPE = "text/xml; charset=utf-8";

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HttpRequester oHttpRequester;

    @Autowired
    private RuntimeService runtimeService;

    public String getAnswer(String sID_Task, String sID_Process, String sINN) {
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
                    if (multipartFile.getOriginalFilename().contains(attachmentDocumentName) 
                            && !multipartFile.getOriginalFilename().endsWith(".xml")) {
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
            //taskService.complete(sID_Task);
        } else{
            runtimeService.setVariable(sID_Process, "anID_Attach_Dfs", "");
            taskService.setVariable(sID_Task, "anID_Attach_Dfs", "");
        }
        return asID_Attach_Dfs.toString();
    }

    public String send(String content, String fileName, String email) throws Exception {
        LOG.info("content: " + content + " fileName: " + fileName + " email: " + email);
        String body = createBody_Send(content, fileName, email);
        return oHttpRequester.postInside(SendDocument_SWinEd.URL, null, body, CONTENT_TYPE);
    }

    private String createBody_Send(String content, String fileName, String email) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">")
                .append("<soap12:Body>")
                .append("<Send xmlns=\"http://govgate/\">")
                .append("<fileName>").append(fileName).append("</fileName>")
                .append("<senderEMail>").append(email).append("</senderEMail>")
                .append("<data>").append(base64_encode(content)).append("</data>")
                .append("</Send>")
                .append("</soap12:Body>")
                .append("</soap12:Envelope>").toString();
        return result;
    }

    public List<ByteArrayMultipartFile> getAnswer(String inn) {
        List<ByteArrayMultipartFile> result = new ArrayList<>();
        try {
            String responseBody = getMessages(inn);
            LOG.info("getMessages responseBody: " + responseBody);
            List<String> resultMessages = getContentFromXml(responseBody, "string");
            LOG.info("getMessages resultMessage: " + resultMessages);
            for (String resultMessage : resultMessages) {
                if (resultMessage != null) {
                    responseBody = receive(resultMessage);
                    LOG.info("receive responseBody: " + responseBody);
                    List<String> fileNames = getContentFromXml(responseBody, "fileName");
                    List<String> fileContents = getContentFromXml(responseBody, "messageData");
                    LOG.info("receive fileNames: " + fileNames);
                    if (fileNames != null && fileNames.size() > 0 && fileContents != null && fileContents.size() > 0) {
                        String fileName = fileNames.get(0);
                        byte[] fileContent = Base64.decodeBase64(fileContents.get(0));
                        if (fileName != null && fileContent != null && fileContent.length > 0) {
                            ByteArrayMultipartFile oByteArrayMultipartFile = new ByteArrayMultipartFile(fileContent, fileName,
                                    fileName, "text/plain");
                            result.add(oByteArrayMultipartFile);
                            responseBody = delete(resultMessage);
                            LOG.info("delete responseBody: " + responseBody);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("getAnswer: ", ex);
        }
        return result;
    }

    private String getMessages(String inn) throws Exception {
        LOG.info("inn: " + inn);
        String body = createBody_GetMessages(inn);
        return oHttpRequester.postInside(SendDocument_SWinEd.URL, null, body, CONTENT_TYPE);
    }

    private String createBody_GetMessages(String inn) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">")
                .append("<soap12:Body>")
                .append("<GetMessages xmlns=\"http://govgate/\">")
                .append("<signedEDRPOU>").append(base64_encode(inn)).append("</signedEDRPOU>")
                .append("</GetMessages>")
                .append("</soap12:Body>")
                .append("</soap12:Envelope>").toString();
        return result;
    }

    private String receive(String massageID) throws Exception {
        LOG.info("massageID: " + massageID);
        String body = createBody_Receive(massageID);
        return oHttpRequester.postInside(SendDocument_SWinEd.URL, null, body, CONTENT_TYPE);
    }

    private String createBody_Receive(String massageID) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">")
                .append("<soap12:Body>")
                .append("<Receive xmlns=\"http://govgate/\">")
                .append("<signedMsgId>").append(base64_encode(massageID)).append("</signedMsgId>")
                .append("</Receive>")
                .append("</soap12:Body>")
                .append("</soap12:Envelope>").toString();
        return result;
    }

    private String delete(String massageID) throws Exception {
        LOG.info("massageID: " + massageID);
        String body = createBody_Delete(massageID);
        return oHttpRequester.postInside(SendDocument_SWinEd.URL, null, body, CONTENT_TYPE);
    }

    private String createBody_Delete(String massageID) {
        String result = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                .append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">")
                .append("<soap12:Body>")
                .append("<Delete xmlns=\"http://govgate/\">")
                .append("<signedMsgId>").append(base64_encode(massageID)).append("</signedMsgId>")
                .append("</Delete>")
                .append("</soap12:Body>")
                .append("</soap12:Envelope>").toString();
        return result;
    }

    private static List<String> getContentFromXml(String xmlDocument, String tagName) {
        List<String> result = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        org.w3c.dom.Document doc;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlDocument));
            doc = builder.parse(is);
            NodeList nodeList = doc.getElementsByTagName(tagName);
            int length = nodeList.getLength();
            for (int i = 0; i < length; i++) {
                Node nodeId = nodeList.item(i);
                result.add(nodeId.getTextContent());
                LOG.info("nodeId.value: " + nodeId.getNodeValue() + " nodeId.getAttributes: " + nodeId.getAttributes().getLength()
                        + " nodeId.getTextContent: " + nodeId.getTextContent() + " nodeId.getNodeName(): " + nodeId.getNodeName());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DocumentNotFoundException("Can't parse Session ID.", e);
        }
        return result;
    }
}
