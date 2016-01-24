/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.access;

import org.igov.service.controller.security.AuthenticationTokenSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.io.GeneralConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.igov.io.web.HttpRequester;
import org.igov.service.controller.security.AccessContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ольга
 */
@Component()
public class AccessKeyService {

    private static final Logger LOG = LoggerFactory.getLogger(AccessKeyService.class);
    
    @Autowired
    HttpRequester httpRequester;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private AccessDataService accessDataDao;

    public String getAccessKeyCentral(String sData) throws Exception {
        return getAccessKeyCentral(sData, AccessContract.RequestAndLogin);
    }
    public String getAccessKeyCentral(String sData, AccessContract oAccessContract) throws Exception {
        String sURI = "/wf/service/access/getAccessKey";
        Map<String, String> mParam = new HashMap<String, String>();
        mParam.put(AuthenticationTokenSelector.ACCESS_LOGIN, generalConfig.sAuthLogin());//"activiti-master"//mParam.put("sAccessLogin", "activiti-master");
        mParam.put(AuthenticationTokenSelector.ACCESS_CONTRACT, oAccessContract.name());//"RequestAndLogin"//AccessContract.RequestAndLogin//param.put("sAccessContract", "Request");
        mParam.put("sData", sData);
        return httpRequester.getInside(generalConfig.sHostCentral() + sURI, mParam);
        //JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(soJSON_Merchant);
        //return (String) jsonObject.get("string");
    }

    public String getAccessKey(String sData) throws Exception {
        return accessDataDao.setAccessData(sData);
        
        /*String sURI = "/wf/service/access/getAccessKey";
        Map<String, String> mParam = new HashMap<String, String>();
        
        mParam.put(AuthenticationTokenSelector.ACCESS_LOGIN, generalConfig.sAuthLogin());//"activiti-master"
        mParam.put(AuthenticationTokenSelector.ACCESS_CONTRACT, AccessContract.RequestAndLogin.name());//"RequestAndLogin"
        mParam.put("sData", sData);
        return httpRequester.get(generalConfig.sHost() + sURI, mParam);
        */
    }

}
