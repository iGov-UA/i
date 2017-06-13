/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io.web;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.igov.io.Log;
import static org.igov.util.Tool.sCut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author bw
 */
public class HttpEntityCover {
    
    static final transient Logger LOG = LoggerFactory.getLogger(HttpEntityCover.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("WebBig");
    
    private HttpHeaders oHttpHeaders = new HttpHeaders();
    private String sURL = null;
    private MultiValueMap<String, Object> mParamObject = null;
    private MultiValueMap<String, ByteArrayResource> mParamByteArray = null;

    
    private ResponseEntity<String> osResponseEntity = null;
            
    public HttpEntityCover(String sURL){
        this.sURL = sURL;
    }

    public HttpEntityCover _Reset(){
        oHttpHeaders = new HttpHeaders();
        sURL = null;
        mParamObject = null;
        mParamByteArray = null;
        return this;
    }
    
    public HttpEntityCover _Header(HttpHeaders oHttpHeaders){
        this.oHttpHeaders = oHttpHeaders;
        return this;
    }

    public HttpEntityCover _Data(MultiValueMap<String, Object> mParamObject){
        this.mParamObject = mParamObject;
        return this;
    }

    public HttpEntityCover _DataArray(MultiValueMap<String, ByteArrayResource> mParamByteArray){
        this.mParamByteArray = mParamByteArray;
        return this;
    }

    public String sReturn(){
        if(osResponseEntity==null){
            return null;
        }
        return osResponseEntity.getBody();
    }

    public Integer nStatus(){
        if(osResponseEntity==null){
            return null;
        }
        return osResponseEntity.getStatusCode().value();
    }

    public Boolean bStatusOk(){
        return nStatus()==200;
    }
    
    public HttpEntityCover _Send(){
        String sRequest = null;
        try{
            StringHttpMessageConverter oStringHttpMessageConverter = new StringHttpMessageConverter();
            HttpMessageConverter<Resource> oHttpMessageConverter = new ResourceHttpMessageConverter();
            FormHttpMessageConverter oFormHttpMessageConverter = new FormHttpMessageConverter();
            oFormHttpMessageConverter.addPartConverter(oHttpMessageConverter);

            RestTemplate oRestTemplate = new RestTemplate(
                    Arrays.asList(oStringHttpMessageConverter, oHttpMessageConverter, oFormHttpMessageConverter));

            
            //Let's construct attachemnts HTTP entities
            if (mParamByteArray != null) {
                Iterator<String> osIterator = mParamByteArray.keySet().iterator();
                for (; osIterator.hasNext(); ) {
                    String sFileName = osIterator.next();
                    HttpHeaders oHttpHeaders_Part = new HttpHeaders();
                    oHttpHeaders_Part.setContentType(new MediaType("application", "octet-stream", StandardCharsets.UTF_8));
                    List<ByteArrayResource> aByteArray_Part = mParamByteArray.get(sFileName);
                    HttpEntity<ByteArrayResource> oByteArray_Part = new HttpEntity<ByteArrayResource>(aByteArray_Part.get(0), oHttpHeaders_Part);
                    mParamObject.add(sFileName, oByteArray_Part);
                }
            }
            
            sRequest = mParamObject.toString();
            
            HttpEntity oHttpEntity = new HttpEntity(mParamObject, oHttpHeaders);
            osResponseEntity = oRestTemplate.postForEntity(sURL, oHttpEntity, String.class);
            
            if(nStatus()!=200){
                new Log(this.getClass(), LOG)
                        ._Case("Web_PostNo200")
                        ._Head("[_Send]:nStatus!=200")
                        ._Status(Log.LogStatus.ERROR)
                        ._Param("sURL", sURL)
                        ._Param("sRequest", sRequest)
                        ._Param("nReturn", nStatus())
                        ._LogTransit()
                        .save()
                        ;
            }
            LOG.info("FINISHED! (nStatus={},sURL={},sRequest(cuted)={},sReturn(cuted)={})",nStatus(),sURL,sCut(100,sRequest),sCut(100,sReturn()));
            LOG_BIG.debug("FINISHED! (nStatus={},sURL={},sRequest={},sReturn()={})",nStatus(),sURL,sRequest,sReturn());
        }catch(Exception oException){
            new Log(oException, LOG)
                    ._Case("Web_Post")
                    ._Head("[_Send]:BREAKED!")
                    ._Status(Log.LogStatus.ERROR)
                    ._Param("sURL", sURL)
                    ._Param("sRequest", sRequest)
                    ._LogTransit()
                    ._LogTrace()
                    .save()
                    ;
            LOG.error("BREAKED: {} (sURL={},sRequest={}):",oException.getMessage(),sURL,sRequest);
            LOG_BIG.error("BREAKED: {} (sURL={},sRequest={}):",oException.getMessage(),sURL,sRequest);
            LOG_BIG.debug("BREAKED:", oException);
            throw oException;
        }
        _Reset();
        return this;
    }
    
    
    
}
