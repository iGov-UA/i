/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.dfs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.engine.ActivitiException;
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
import org.apache.commons.io.IOUtils;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.service.business.action.event.HistoryEventService;
import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import org.igov.service.business.action.task.listener.doc.SendDocument_SWinEd;
import org.igov.service.controller.ActionTaskCommonController;
import org.igov.service.exception.FileServiceIOException;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private IBytesDataStorage durableBytesDataStorage;
    
    public String getAnswer(String sID_Task, String snID_Process, String sINN) {
        StringBuilder asID_Attach_Dfs = new StringBuilder();
        List<ByteArrayMultipartFile> aByteArrayMultipartFile = getAnswer(sINN);
        LOG.info("aByteArrayMultipartFile.size()=" + aByteArrayMultipartFile.size());
        try {
            Attachment oAttachment_Document = taskService.getAttachment((String) runtimeService.getVariable(snID_Process, "oFile_XML_SWinEd")); //sFileName_XML_SWinEd_Answer=F1401801
            if (oAttachment_Document != null) {
                String sID_Order=generalConfig.getOrderId_ByProcess(Long.valueOf(snID_Process));
                String sAttachmentName_Document = oAttachment_Document.getName();
                LOG.info("sAttachmentName_Document=" + sAttachmentName_Document + ", sID_Order=" + sID_Order);
                sAttachmentName_Document = sAttachmentName_Document.replaceAll(".xml", "");
                
                for (ByteArrayMultipartFile oByteArrayMultipartFile : aByteArrayMultipartFile) {
                    String sFileName = oByteArrayMultipartFile.getOriginalFilename();
                    String sFileContentType = oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp();
                    if ((sFileName.contains(sAttachmentName_Document) && !sFileName.endsWith(".xml"))
                            || sFileName.contains((String) runtimeService.getVariable(snID_Process, "sFileName_XML_SWinEd_Answer"))) { //"F1401801"
                        LOG.info("ToAttach-PROCESS sFileName=" + sFileName + " sAttachmentName_Document=" + sAttachmentName_Document);
                        Attachment oAttachment = taskService.createAttachment(sFileContentType,
                                sID_Task, snID_Process, sFileName, oByteArrayMultipartFile.getName(), oByteArrayMultipartFile.getInputStream());
                        if (oAttachment != null) {
                            asID_Attach_Dfs.append(oAttachment.getId()).append(",");
                            LOG.info("oAttachment.getId()=" + oAttachment.getId());
                        }
                    } else {
                        LOG.info("ToAttach-SKIP sFileName=" + sFileName + " sAttachmentName_Document=" + sAttachmentName_Document);
                    }
                }
                
                try {
                    for (ByteArrayMultipartFile oByteArrayMultipartFile : aByteArrayMultipartFile) {
                        String sFileName = oByteArrayMultipartFile.getOriginalFilename();
                        String sFileContentType = oByteArrayMultipartFile.getContentType() + ";" + oByteArrayMultipartFile.getExp();
                        if ((sFileName.contains(sAttachmentName_Document) && !sFileName.endsWith(".xml"))
                                || sFileName.contains((String) runtimeService.getVariable(snID_Process, "sFileName_XML_SWinEd_Answer"))) { //"F1401801"
                            LOG.info("ToJournal-PROCESS sFileName=" + sFileName + " sAttachmentName_Document=" + sAttachmentName_Document);
                            try {
                                String sMail="";
                                /*
                                byte[] aByte_FileContent = null;
                                try {
                                    byte[] aByte_FileContent_Redis = oBytesDataInmemoryStorage.getBytes(sID_File);
                                    oByteArrayMultipartFile                                    
                                    
                                    LOG.info("Size of bytes: {}", aByte_FileContent_Redis.length);
                                    ByteArrayMultipartFile oByteArrayMultipartFile = null;
                                    oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aByte_FileContent_Redis);
                                    if (oByteArrayMultipartFile != null) {
                                        aByte_FileContent = oByteArrayMultipartFile.getBytes();
                                        LOG.info("Size of multi part content: {}", aByte_FileContent_Redis.length);
                                    } else {
                                        LOG.error("oByteArrayMultipartFile==null! sID_File={}", sID_File);
                                        throw new FileServiceIOException(
                                                FileServiceIOException.Error.REDIS_ERROR, "oByteArrayMultipartFile==null! sID_File=" + sID_File);
                                    }
                                } catch (RecordInmemoryException e) {
                                    LOG.warn("Error: {}", e.getMessage(), e);
                                    throw new FileServiceIOException(
                                            FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
                                } catch (ClassNotFoundException | IOException e) {
                                    LOG.error("Error: {}", e.getMessage(), e);
                                    throw new ActivitiException(e.getMessage(), e);
                                }
                                sID_DataLink = accessDataDao.setAccessData(aByte_FileContent);                                
                                */

                                BufferedInputStream oBufferedInputStream = new BufferedInputStream(oByteArrayMultipartFile.getInputStream());
                                byte[] aByte = IOUtils.toByteArray(oBufferedInputStream);

                                
                                /*
                                BufferedInputStream oBufferedInputStream = new BufferedInputStream(oByteArrayMultipartFile.getInputStream());
                                byte[] byteFile = IOUtils.toByteArray(oBufferedInputStream);

                                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteFile);
                                ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
                                ByteArrayMultipartFile contentMultipartFile = (ByteArrayMultipartFile) ois.readObject();
                                ois.close();
                                //return contentMultipartFile;
                                byte[] aByte = contentMultipartFile.getBytes();
                                */
                                
                                
//                                byte[] aByte = oByteArrayMultipartFile.getBytes();
                                
                                saveServiceMessage_EncryptedFile("Отримана відповідь від Державної Фіскальної Служби"
                                        , "Отримана відповідь від Державної Фіскальної Служби у вигляді криптопакету: "+sFileName
                                        , aByte
                                        , sID_Order
                                        , sMail
                                        , sFileName
                                        , sFileContentType
                                                );
                            } catch (Exception ex) {
                                LOG.error("ToJournal sFileName=" + sFileName + " sAttachmentName_Document=" + sAttachmentName_Document);
                                java.util.logging.Logger.getLogger(ActionTaskCommonController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            LOG.info("ToJournal-SKIP sFileName=" + sFileName + " sAttachmentName_Document=" + sAttachmentName_Document);
                        }
                    }
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(ActionTaskCommonController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ActionTaskCommonController.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        if (asID_Attach_Dfs.length() > 0) {
            String sID_Attach_Dfs = asID_Attach_Dfs.deleteCharAt(asID_Attach_Dfs.length() - 1).toString();
            runtimeService.setVariable(snID_Process, "anID_Attach_Dfs", sID_Attach_Dfs);
            taskService.setVariable(sID_Task, "anID_Attach_Dfs", sID_Attach_Dfs);
            //taskService.complete(sID_Task);
        } else {
            runtimeService.setVariable(snID_Process, "anID_Attach_Dfs", "");
            taskService.setVariable(sID_Task, "anID_Attach_Dfs", "");
        }
        return asID_Attach_Dfs.toString();
    }

    protected void saveServiceMessage_EncryptedFile(String sHead, String sBody, byte[] aByte, String sID_Order, String sMail, String sFileName, String sFileContentType) {

        /*if (sBody != null && sBody.contains("Шановний колего!")) {
            //Не сохраняем в истории заявки гражданина письмо чиновнику //Юлия
            return;
        }*/

        final Map<String, String> mParam = new HashMap<>();
        mParam.put("sHead", sHead);//"Відправлено листа"
        mParam.put("sBody", sBody);
        mParam.put("sID_Order", sID_Order);
        mParam.put("sMail", sMail);
        mParam.put("sFileName", sFileName);
        mParam.put("sFileContentType", sFileContentType);
        //mParam.put("nID_Subject", "0");
        //mParam.put("sContacts", "0");
        //params.put("sData", "0");
        
        mParam.put("nID_SubjectMessageType", "" + 12L);
        mParam.put("sID_DataLinkSource", "Region");
        mParam.put("sID_DataLinkAuthor", "SFS");
        String sID_DataLink;
        sID_DataLink = durableBytesDataStorage.saveData(aByte); //sBody.getBytes(Charset.forName("UTF-8"))
        mParam.put("sID_DataLink", sID_DataLink);
        
        mParam.put("RequestMethod", RequestMethod.GET.name());
        
        LOG.info("ToJournal-PROCESS mParam=" + mParam);

        ScheduledExecutorService oScheduledExecutorService = Executors
                .newSingleThreadScheduledExecutor();
        Runnable oRunnable = new Runnable() {

            @Override
            public void run() {
                LOG.info(
                        "try to save service message with params with a delay: (params={})",
                        mParam);
                String jsonServiceMessage;
                try {
                    jsonServiceMessage = historyEventService
                            .addServiceMessage(mParam);
                    LOG.info("(jsonServiceMessage={})", jsonServiceMessage);
                } catch (Exception e) {
                    LOG.error("( saveServiceMessage error={})", e.getMessage());
                }
            }
        };
        // run saving message in 10 seconds so history event will be in the
        // database already by that time
        oScheduledExecutorService.schedule(oRunnable, 10, TimeUnit.SECONDS);
        oScheduledExecutorService.shutdown();

        LOG.info(
                "Configured thread to run in 10 seconds with params: (params={})",
                mParam);
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
