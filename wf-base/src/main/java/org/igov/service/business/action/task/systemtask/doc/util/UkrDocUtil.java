package org.igov.service.business.action.task.systemtask.doc.util;

import org.igov.io.web.RestRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import org.w3c.dom.Node;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.igov.service.exception.DocumentNotFoundException;
import org.json.simple.JSONArray;

public class UkrDocUtil {

	public static String getSessionId(String login, String password, String uriSid) {
        String sessionId;

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n" +
                "<session><user auth='EXCL' login='" + login + "' password='" + password + "'/></session>";

        String xmlResponse = new RestRequest().post(uriSid, xml, MediaType.TEXT_XML,
                StandardCharsets.UTF_8, String.class, null);
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
		Map<String, Object> res = new HashMap<String, Object>();
		
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("name", sHeadValue);
		content.put("text", sHeadValue);
		content.put("paragraphs", new JSONArray());
		content.put("extensions", new HashMap<Object,Object>());
		
		res.put("content", content);
		
		Map<String, Object> actors = new HashMap<String, Object>();
		actors.put("paragraphs", new HashMap<Object,Object>());
		actors.put("ratifiers", new JSONArray());
		actors.put("reconcilers", new JSONArray());
		actors.put("addressee", new HashMap<Object,Object>());
		actors.put("readers", new JSONArray());
		
		Map<String, String> author = new HashMap<String, String>();
		author.put("id", sLoginAuthorValue);
		
		actors.put("author", author);
		
		res.put("actors", actors);
		
		Map<String, String> template = new HashMap<String, String>();
		template.put("template", nID_PatternValue);
		
		res.put("details", template);
		
		return res;
	}
	
}
