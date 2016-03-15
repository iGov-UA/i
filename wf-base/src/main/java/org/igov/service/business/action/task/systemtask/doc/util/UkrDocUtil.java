package org.igov.service.business.action.task.systemtask.doc.util;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.igov.io.web.RestRequest;
import org.igov.service.exception.DocumentNotFoundException;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class UkrDocUtil {
	
	private final static Logger LOG = LoggerFactory.getLogger(UkrDocUtil.class);

	public static String getSessionId(String login, String password, String uriSid) {
        String sessionId;

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n" +
                "<session><user auth='EXCL' login='" + login + "' password='" + password + "'/></session>";

        LOG.info("Sending request to SID generator. URL:{}, request:{}", uriSid, xml);
        
        String xmlResponse = new RestRequest().post(uriSid, xml, MediaType.TEXT_XML,
                StandardCharsets.UTF_8, String.class, null);
        
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
			String nID_PatternValue) {
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		
		Map<String, Object> content = new LinkedHashMap<String, Object>();
		content.put("name", sHeadValue);
		content.put("text", sBodyValue);
		content.put("paragraphs", new JSONArray());
		content.put("extensions", new HashMap<Object,Object>());
		
		res.put("content", content);
		
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("Автор", sLoginAuthorValue);
		Map<String, Object> extensions = new HashMap<String, Object>();
		extensions.put("attributes", attributes);
		
		content.put("extensions", extensions);
		
		res.put("actors", new HashMap<String, Object>());
		
		Map<String, Object> template = new HashMap<String, Object>();
		template.put("template", Integer.valueOf(nID_PatternValue));
		
		res.put("details", template);
		
		return res;
	}
	
}
