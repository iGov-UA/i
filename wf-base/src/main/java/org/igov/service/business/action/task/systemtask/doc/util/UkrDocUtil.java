package org.igov.service.business.action.task.systemtask.doc.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.task.Attachment;
import org.igov.io.GeneralConfig;
import org.igov.io.web.RestRequest;
import org.igov.service.exception.DocumentNotFoundException;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UkrDocUtil {

    private static final String DOWNLOAD_FILE_FROM_PATTERN = "%s/wf/service/object/file/download_file_from_storage_static?sId=%s&sFileName=%s&sType=%s";
    private final static Logger LOG = LoggerFactory.getLogger(UkrDocUtil.class);

    public static String getSessionId(String login, String password, String uriSid) {
        String sessionId;

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n"
                + "<session><user auth='EXCL' login='" + login + "' password='" + password + "'/></session>";

        LOG.info("Sending request to SID generator. URL:{}, request:{}", uriSid, xml);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(mediaTypes);
        String xmlResponse = new RestRequest().post(uriSid, xml, MediaType.TEXT_XML,
                StandardCharsets.UTF_8, String.class, httpHeaders);

        LOG.info("Response from SID generator: {}", xmlResponse);

        sessionId = getSidFromXml(xmlResponse);

        return sessionId;
    }

    private static String getSidFromXml(String xmlDocument) {
        //todo simplify parsing
        String result;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        org.w3c.dom.Document doc;
        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlDocument));
            doc = builder.parse(is);
            Node nodeId = doc.getElementsByTagName("id").item(0);
            result = nodeId.getAttributes().getNamedItem("value").getNodeValue();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new DocumentNotFoundException("Can't parse Session ID.", e);
        }
        return result;

    }

    public static Map<String, Object> makeJsonRequestObject(String sHeadValue, String sBodyValue, String sLoginAuthorValue,
            String nID_PatternValue, List<Attachment> attachmentsIds, String taskId, GeneralConfig generalConfig,
            String sID_Order_GovPublicValue, String sSourceChannelValue, String shortFIO, String fullIO,
            String sDepartNameFull, String sSex, String sAddress,
            String sZipCode, String sPlace, String sDateAppealValue) {

        Map<String, Object> res = new LinkedHashMap<String, Object>();

        Map<String, Object> content = new LinkedHashMap<String, Object>();
        content.put("name", sHeadValue);
        content.put("text", sBodyValue);
        content.put("paragraphs", new JSONArray());

        res.put("content", content);

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("Автор", sLoginAuthorValue);
        attributes.put("Регистрационный номер заявки", sID_Order_GovPublicValue);
        attributes.put("Название канала", sSourceChannelValue);
        attributes.put("Краткое ФИО заявителя", shortFIO);
        attributes.put("Имя и отчество заявителя", fullIO);
        attributes.put("Полное название управления/департамента", sDepartNameFull);
        //attributes.put("Пол заявителя", sSex);
        //attributes.put("Адрес проживания", sAddress);
        attributes.put("Индекс", sZipCode);
        attributes.put("Населенный пункт", sPlace);
        attributes.put("Остальной адрес", sAddress);
        attributes.put("Дата заявки", sDateAppealValue);
        attributes.put("sHost", generalConfig.getSelfHost());

        Map<String, Object> extensions = new HashMap<String, Object>();
        if (attachmentsIds != null && !attachmentsIds.isEmpty()) {
            Map<String, List<?>> tables = new HashMap<String, List<?>>();
            List<List<String>> attachmentsInfo = new LinkedList<List<String>>();
            for (Attachment attachInfo : attachmentsIds) {
                LOG.info("attachInfo getId: " + attachInfo.getId() + " getName: " + attachInfo.getName() + " getType: " + attachInfo.getType());
                if (attachInfo.getName() != null && !attachInfo.getName().contains("user form")) {
                    List<String> info = new LinkedList<String>();
                    info.add(URLEncoder.encode(attachInfo.getName()));
                    info.add(String.format(DOWNLOAD_FILE_FROM_PATTERN, generalConfig.getSelfHost(), URLEncoder.encode(((AttachmentEntity) attachInfo).getContentId()), URLEncoder.encode(attachInfo.getName()), URLEncoder.encode(attachInfo.getType())));
                    attachmentsInfo.add(info);
                }
            }
            tables.put("Приложения", attachmentsInfo);
            extensions.put("tables", tables);
        }

        extensions.put("attributes", attributes);

        content.put("extensions", extensions);

        res.put("actors", new HashMap<String, Object>());

        Map<String, Object> template = new HashMap<String, Object>();
        template.put("template", Integer.valueOf(nID_PatternValue));

        res.put("details", template);

        return res;
    }

}
