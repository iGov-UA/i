/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io.web;

import java.nio.charset.Charset;
import org.igov.io.GeneralConfig;
import org.igov.service.business.action.task.systemtask.SendAttachToDocuments;
import org.igov.util.ToolWeb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author bw
 */
@Service
public class HttpEntityInsedeCover {
    
    private final static Logger LOG = LoggerFactory.getLogger(HttpEntityInsedeCover.class);
    
    @Autowired
    private GeneralConfig generalConfig;
    

    public HttpHeaders oHttpHeaders(){
        String sUser = generalConfig.sAuthLogin();
        String sPassword = generalConfig.sAuthPassword();
        String sAuth = ToolWeb.base64_encode(sUser + ":" + sPassword);
        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.add("Authorization", "Basic " + sAuth);
        return oHttpHeaders;
    }
    
    public Long nReturn_RequestPost_ByMap(String sURL, MultiValueMap<String, Object> mParam){
        Long nReturn = null;
        try{
            HttpHeaders oHttpHeaders = oHttpHeaders();
            HttpEntity<Object> oHttpEntity = new HttpEntity<Object>(mParam, oHttpHeaders);
            RestTemplate oRestTemplate = new RestTemplate();
            LOG.info("Calling URL with (parametes:{}|{}", sURL, mParam);
            nReturn = oRestTemplate.postForObject(sURL, oHttpEntity, Long.class);
            LOG.info("Received response from (setDocumentFile={})", nReturn);
            return nReturn;
        }catch(Exception oException){
            LOG.info("FAIL: {} (sURL={},mParam={}(", oException.getMessage(), sURL, mParam.toString());
            throw oException;
        }
    }

    public ResponseEntity<String> oReturn_RequestGet_JSON(String sURL){
        ResponseEntity<String> oReturn = null;
        try{
            HttpHeaders oHttpHeaders = oHttpHeaders();
            oHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> osHttpEntity = new HttpEntity<>(oHttpHeaders);
            RestTemplate oRestTemplate = new RestTemplate();
            oRestTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            LOG.info("Calling URL with parametes:{}", sURL);
            oReturn = oRestTemplate.exchange(sURL, HttpMethod.GET, osHttpEntity, String.class);
            LOG.info("Received response from setDocumentFile:{}", oReturn);
            return oReturn;
        }catch(Exception oException){
            LOG.info("FAIL: {} (sURL={})", oException.getMessage(), sURL);
            throw oException;
        }
    }
    
    
}
