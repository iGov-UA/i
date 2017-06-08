package org.igov.service.business.promin;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.io.web.HttpRequester;
import org.igov.io.web.RestRequest;
import org.igov.service.exception.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Сессия ПК "Проминь"
 *
 * @author olga
 */
@Service
@Scope("prototype")
public class ProminSession_Singleton {
    
    private final static Logger LOG = LoggerFactory.getLogger(ProminSession_Singleton.class);
    
    private String sid_Auth_UkrDoc_SED;
    private String sid_Auth_Receipt_PB_Bank;
    private String aSID_Auth_PB_SMS;
    private String sid_Auth_PB_ObjectSubPlace = null;
    
    private long nTimeCreatedMS;
    private final long nTimeLiveLimitMS = 1000 * 60 * 20;

    private long nTimeCreatedMS_PB_ObjectSubPlace = 0;
    private final long nTimeLiveLimitMS_PB_ObjectSubPlace = 1000 * 60 * 20;
    
    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    HttpRequester httpRequester;
    
    public String getSid_Auth_UkrDoc_SED() {
        checkAndUpdateSid();        
        return sid_Auth_UkrDoc_SED;
    }
    
    public String getSid_Auth_Receipt_PB_Bank() {
        checkAndUpdateSid();
        return sid_Auth_Receipt_PB_Bank;
    }
    
    public String getSID_Auth_PB_SMS() {
        LOG.info("getSID_Auth_PB_SMS: start");
        checkAndUpdateSid();
        return aSID_Auth_PB_SMS;
    }
    
    public String getSid_Auth_PB_ObjectSubPlace() {
        LOG.info("Auth Promin begin. sid_Auth_PB_ObjectSubPlace={}, nTimeCreatedMS_PB_ObjectSubPlace={}, nTimeLiveLimitMS_PB_ObjectSubPlace={}", 
        	sid_Auth_PB_ObjectSubPlace, nTimeCreatedMS_PB_ObjectSubPlace, nTimeLiveLimitMS_PB_ObjectSubPlace);

        if (sid_Auth_PB_ObjectSubPlace == null || (System.currentTimeMillis() - nTimeCreatedMS_PB_ObjectSubPlace) > nTimeLiveLimitMS_PB_ObjectSubPlace) {
            nTimeCreatedMS_PB_ObjectSubPlace = System.currentTimeMillis();

            LOG.debug("Auth Promin, sid refresh");
            
        }
        LOG.info("Auth Promin end. sid_Auth_PB_ObjectSubPlace={}, nTimeCreatedMS_PB_ObjectSubPlace={}, nTimeLiveLimitMS_PB_ObjectSubPlace={}", 
        	sid_Auth_PB_ObjectSubPlace, nTimeCreatedMS_PB_ObjectSubPlace, nTimeLiveLimitMS_PB_ObjectSubPlace);
	
        return sid_Auth_PB_ObjectSubPlace;
    }
    
    private void checkAndUpdateSid() {
        LOG.info("getSID ... " + toString());

        if (sid_Auth_UkrDoc_SED == null || (System.currentTimeMillis() - nTimeCreatedMS) > nTimeLiveLimitMS) {

            nTimeCreatedMS = System.currentTimeMillis();
            sid_Auth_UkrDoc_SED = getSessionId(generalConfig.getLogin_Auth_UkrDoc_SED(),
                    generalConfig.getPassword_Auth_UkrDoc_SED(),
                    generalConfig.getURL_GenerateSID_Auth_UkrDoc_SED() + "?lang=UA");
            
            sid_Auth_Receipt_PB_Bank = getSessionId(generalConfig.getLogin_Auth_Receipt_PB_Bank(),
                    generalConfig.getPassword_Auth_Receipt_PB_Bank(),
                    generalConfig.getURL_GenerateSID_Auth_Receipt_PB_Bank() + "?lang=UA");

            aSID_Auth_PB_SMS = getSessionId(generalConfig.getLogin_Auth_PB_SMS(),
                    generalConfig.getPassword_Auth_PB_SMS(),
                    generalConfig.getURL_GenerateSID_Auth_PB_SMS() + "?lang=UA");

        }
        LOG.info(toString() + " ok!");
    }
    
    @Override
    public String toString() {
        String sCase = "SID_ESC";
        return "[" + sCase + "]: sid_Auth_UkrDoc_SED=" + sid_Auth_UkrDoc_SED + ", sid_Auth_Receipt_PB_Bank= " + sid_Auth_Receipt_PB_Bank
                + ", nTimeCreatedMS=" + nTimeCreatedMS + ", nTimeLiveLimitMS=" + nTimeLiveLimitMS;
    }
    
    private String getSessionId(String login, String password, String uriSid) {
        String sessionId = null;
        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n"
                + "<session><user auth='EXCL' login='" + login + "' password='" + password + "'/></session>";
        LOG.info("Sending request to SID generator. URL:{}, request:{}", uriSid, xml);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(mediaTypes);
        //String xmlResponse = new RestRequest().post(uriSid, xml, MediaType.TEXT_XML,
        //        StandardCharsets.UTF_8, String.class, httpHeaders);
        try {
            String xmlResponse = httpRequester.postInside(uriSid, null, xml,
                    "text/xml; charset=utf-8", null, null);
            LOG.info("Response from SID generator: {}", xmlResponse);
            sessionId = getSidFromXml(xmlResponse);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
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
    
}
