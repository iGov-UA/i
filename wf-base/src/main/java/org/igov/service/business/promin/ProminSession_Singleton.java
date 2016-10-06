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
    private long nTimeCreatedMS;
    private final long nTimeLiveLimitMS = 1000 * 60 * 20;

    @Autowired
    GeneralConfig generalConfig;

    public String getSid_Auth_UkrDoc_SED() {
        checkAndUpdateSid();
        return sid_Auth_UkrDoc_SED;
    }

    public String getSid_Auth_Receipt_PB_Bank() {
        checkAndUpdateSid();
        return sid_Auth_Receipt_PB_Bank;
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
        }
        LOG.info(toString() + " ok!");
    }

    @Override
    public String toString() {
        String sCase = "SID_ESC";
        return "[" + sCase + "]: sid_Auth_UkrDoc_SED=" + sid_Auth_UkrDoc_SED + ", sid_Auth_Receipt_PB_Bank= " + sid_Auth_Receipt_PB_Bank 
                + ", nTimeCreatedMS=" + nTimeCreatedMS + ", nTimeLiveLimitMS=" + nTimeLiveLimitMS;
    }

    private static String getSessionId(String login, String password, String uriSid) {
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

}
