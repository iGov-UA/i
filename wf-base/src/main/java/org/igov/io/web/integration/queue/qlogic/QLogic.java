package org.igov.io.web.integration.queue.qlogic;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityCover;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Provides integration with Qlogic system 
 * Created by Oleksandr Skosyr on 16.07.2017.
 */

@Component
public class QLogic {

    @Autowired
    GeneralConfig generalConfig;

    private String host;
    private String port;
    private String urlGetServicesCenterList = "http://%s:%s/QueueService.svc/json_pre_reg/GetServiceCenterList?organisationGuid={sOrganisationGuid}";
    private String urlGetServicesList = "http://%s:%s/QueueService.svc/json_pre_reg/GetServiceList?organisationGuid={sOrganisationGuid}&serviceCenterId=%s";
    private String urlGetDaysList = "http://%s:%s/QueueService.svc/json_pre_reg/GetDayList?organisationGuid={sOrganisationGuid}&serviceCenterId=%s&serviceId=%s";
    private String urlGetTimeList = "http://%s:%s/QueueService.svc/json_pre_reg/GetTimeList?organisationGuid={sOrganisationGuid}&serviceCenterId=%s&serviceId=%s&date=%s";
    private String urlRegCustomer = "http://%s:%s/QueueService.svc/json_pre_reg/RegCustomer?organisationGuid={sOrganisationGuid}&serviceCenterId=%s&serviceId=%s&date=%s";
    private String urlGetOrganisationState = "http://%s:%s/VideoAd/GetOrganisationState?orgKey=%s";
    
    final static private Logger LOG = LoggerFactory.getLogger(QLogic.class);

    @PostConstruct
    public void initialize() {
        this.host = generalConfig.getQlogicHost();
        if (StringUtils.isBlank(host)) {
            LOG.error("Please check qLogic host property");
        }
        this.port = generalConfig.getQlogicPort();
        if (StringUtils.isBlank(port)) {
            LOG.error("Please check qLogic port property");
        }
    }

    public String getServiceCenterList(String sOrganisationGuid) throws Exception {
        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetServicesCenterList, host, port);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        LOG.info("Result:{}", sReturn);
        return sReturn;
    }
    
    public String getServiceList(String sOrganisationGuid,
			String sServiceCenterId) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetServicesList, host, port, sServiceCenterId);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        LOG.info("Result:{}", sReturn);
        return sReturn;
	}
    
    public JSONArray getDaysList(String sOrganisationGuid, String sServiceCenterId,
			String sServiceId) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetDaysList, host, port, sServiceCenterId, sServiceId);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        JSONParser oJSONParser = new JSONParser();
        JSONObject oJSONObjectGot;
        JSONArray oaJSONArrayReturn = new JSONArray();
        if(sReturn!=null){
            try {

                oJSONObjectGot = (JSONObject) oJSONParser.parse(sReturn);

                oaJSONArrayReturn = (JSONArray) oJSONObjectGot.get("d");
            } catch (ParseException e) {
                LOG.error("Error parsing response = {}", sReturn, e);
                throw new Exception("Error parsing response for: [sendRequest](sURL=" + url + "): nStatus()="
                        + oHttpEntityCover.nStatus());
            }
        }
        
        LOG.info("Result:{}", oaJSONArrayReturn);
        return oaJSONArrayReturn;
	}
	
    public String getTimeList(String sOrganisationGuid, String sServiceCenterId,
			String sServiceId, String sDate) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetTimeList, host, port, sServiceCenterId, sServiceId, sDate);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        LOG.info("Result:{}", sReturn);
        return sReturn;
	}
    
    public String regCustomer(String sOrganisationGuid, String sServiceCenterId,
			String sServiceId, String sDate, String sTime) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String sFullDateTime = String.format("%s %s", sDate, sTime);
        
        String url = String.format(urlRegCustomer, host, port, sServiceCenterId, sServiceId, sFullDateTime);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        LOG.info("Result:{}", sReturn);
        return sReturn;
	}

	public String getOrganizationState(String sOrganisationGuid) throws Exception {
		HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetOrganisationState, host, port, sOrganisationGuid);
        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("sOrganisationGuid", "{" + sOrganisationGuid + "}");
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._UrlVariable(urlVariables)
                ._SendGET();
        String sReturn = oHttpEntityCover.sReturn();
        if (!oHttpEntityCover.bStatusOk()) {
            LOG.error("RESULT FAIL! (sURL={}, nReturn={}, sReturn(cuted)={})",
            		url,
                    oHttpEntityCover.nStatus(), sReturn);
            throw new Exception("[sendRequest](sURL=" + url + "): nStatus()="
                    + oHttpEntityCover.nStatus());
        }

        LOG.info("Result:{}", sReturn);
        return sReturn;
	}

}
