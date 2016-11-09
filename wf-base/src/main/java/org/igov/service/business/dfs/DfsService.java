/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.dfs;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import static org.igov.util.ToolWeb.base64_encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.igov.util.ToolWeb.base64_encode;
import org.igov.util.VariableMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author olga
 */
@Service
public class DfsService {

    private static final Logger LOG = LoggerFactory.getLogger(DfsService.class);

    private final static String URL = "http://109.237.89.107:1220/gate.asmx";

    private final static String CONTENT_TYPE = "text/xml; charset=utf-8";

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private HttpRequester oHttpRequester;

    public String send(String content, String fileName, String email) throws Exception {
        LOG.info("content: " + content + " fileName: " + fileName + " email: " + email);
        String body = createBody_Send(content, fileName, email);
        return oHttpRequester.postInside(URL, null, body, CONTENT_TYPE);
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

    public VariableMultipartFile getAnswer(String inn) throws Exception {
        String result = getMessages(inn);
        String massageID = "";
        if (massageID != null) {
            result = receive(massageID);
            String fileName = "";
            byte[] content = null;
            if (content != null && content.length > 0) {
                delete(massageID);
            }
        }
        return null;
    }

    private String getMessages(String inn) throws Exception {
        LOG.info("inn: " + inn);
        String body = createBody_GetMessages(inn);
        return oHttpRequester.postInside(URL, null, body, CONTENT_TYPE);
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
        return oHttpRequester.postInside(URL, null, body, CONTENT_TYPE);
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
        return oHttpRequester.postInside(URL, null, body, CONTENT_TYPE);
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
}
