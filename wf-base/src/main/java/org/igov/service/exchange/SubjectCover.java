/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.model.core.NamedEntity;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ольга
 */


@Component
public class SubjectCover {
    
    private final String URI_GET_GetSubjects = "/wf/service/subject/getSubjectsByAccount";

    private static final Logger LOG = LoggerFactory.getLogger(SubjectCover.class);
    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;
    
    
    public Map getSubjects(Set<String> logins, Set<String> groups) {
        Map<String, Map> result = null;
        String URL = String.format(URI_GET_GetSubjects, logins, groups, generalConfig.nID_Server());
        Map<String, String> param = new HashMap();
        try {
            param.put("saLogin", JsonRestUtils.toJson(logins));
            param.put("saGroup", JsonRestUtils.toJson(groups));
            param.put("nID_Server", String.valueOf(generalConfig.nID_Server())); 
            String responce = doRemoteRequest(URL, param);
            result = JsonRestUtils.readObject(responce, Map.class); 
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(SubjectCover.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SubjectCover.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    private String doRemoteRequest(String sServiceContext, Map<String, String> mParam){
        String sURL = generalConfig.sHostCentral() + sServiceContext;
        LOG.info("(sURL={},mParam={})", sURL, mParam);
        String soResponse = null;
        try {
            soResponse = httpRequester.getInside(sURL, mParam);
        } catch (Exception ex) {
            LOG.error("[doRemoteRequest]: ", ex);
        }
        LOG.info("(soResponse={})", soResponse);
        return soResponse;
    }
    
}
