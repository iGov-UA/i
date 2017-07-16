package org.igov.io.web.integration.queue.qlogic;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityCover;
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
    private String urlGetServicesCenterList = "http://%s:%s/QueueService.svc/json_pre_reg/GetServiceCenterList?organisationGuid={%s}";
    private String urlGetServicesList = "http://%s:%s/QueueService.svc/json_pre_reg/GetServiceList?organisationGuid={%s}&serviceCenterId=%s";
    private String urlGetDaysList = "http://%s:%s/QueueService.svc/json_pre_reg/GetDayList?organisationGuid={%s}&serviceCenterId=%s&serviceId=%s";
    private String urlGetTimeList = "http://%s:%s/QueueService.svc/json_pre_reg/GetTimeList?organisationGuid={%s}&serviceCenterId=%s&serviceId=%s&date=%s";
    private String urlRegCustomer = "http://%s:%s/QueueService.svc/json_pre_reg/RegCustomer?organisationGuid={%s}&serviceCenterId=%s&serviceId=%s&date=%s";
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
        String url = String.format(urlGetServicesCenterList, host, port, sOrganisationGuid);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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
    
    public String getServiceList(String sOrganizatonGuid,
			String sServiceCenterId) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetServicesList, host, port, sOrganizatonGuid, sServiceCenterId);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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
    
    public String getDaysList(String sOrganizatonGuid, String sServiceCenterId,
			String sServiceId) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetDaysList, host, port, sOrganizatonGuid, sServiceCenterId, sServiceId);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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
	
    public String getTimeList(String sOrganizatonGuid, String sServiceCenterId,
			String sServiceId, String sDate) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String url = String.format(urlGetTimeList, host, port, sOrganizatonGuid, sServiceCenterId, sServiceId, sDate);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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
    
    public String regCustomer(String sOrganizatonGuid, String sServiceCenterId,
			String sServiceId, String sDate, String sTime) throws Exception {
    	HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        String sFullDateTime = String.format("%s %s", sDate, sTime);
        
        String url = String.format(urlRegCustomer, host, port, sOrganizatonGuid, sServiceCenterId, sServiceId, sFullDateTime);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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

	public String getOrganizationState(String sOrganizatonGuid) throws Exception {
		HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.setAcceptCharset(Arrays.asList(new Charset[] { StandardCharsets.UTF_8 }));
        
        String url = String.format(urlGetOrganisationState, host, port, sOrganizatonGuid);
        HttpEntityCover oHttpEntityCover = new HttpEntityCover(url)
                ._Header(oHttpHeaders)
                ._Send();
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
