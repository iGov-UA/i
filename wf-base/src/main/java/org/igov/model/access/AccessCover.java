/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.access;

import org.igov.service.security.AuthenticationTokenSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.igov.model.AccessDataDao;
import org.igov.io.GeneralConfig;

import java.util.HashMap;
import java.util.Map;
import org.igov.io.web.HttpRequester;
import org.igov.service.security.AccessContract;

/**
 * @author Ольга
 */
@Component()
public class AccessCover {

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private AccessDataDao accessDataDao;

    public String getAccessKeyCentral(String sData) throws Exception {
        return getAccessKeyCentral(sData, AccessContract.RequestAndLogin);
    }
    public String getAccessKeyCentral(String sData, AccessContract oAccessContract) throws Exception {
        String sURI = "/wf/service/services/getAccessKey";
        Map<String, String> mParam = new HashMap<String, String>();
        mParam.put(AuthenticationTokenSelector.ACCESS_LOGIN, generalConfig.sAuthLogin());//"activiti-master"//mParam.put("sAccessLogin", "activiti-master");
        mParam.put(AuthenticationTokenSelector.ACCESS_CONTRACT, oAccessContract.name());//"RequestAndLogin"//AccessContract.RequestAndLogin//param.put("sAccessContract", "Request");
        mParam.put("sData", sData);
        return httpRequester.get(generalConfig.sHostCentral() + sURI, mParam);
        //JSONParser parser = new JSONParser();
        //JSONObject jsonObject = (JSONObject) parser.parse(soJSON_Merchant);
        //return (String) jsonObject.get("string");
    }

    public String getAccessKey(String sData) throws Exception {
        return accessDataDao.setAccessData(sData);
        
        /*String sURI = "/wf/service/services/getAccessKey";
        Map<String, String> mParam = new HashMap<String, String>();
        
        mParam.put(AuthenticationTokenSelector.ACCESS_LOGIN, generalConfig.sAuthLogin());//"activiti-master"
        mParam.put(AuthenticationTokenSelector.ACCESS_CONTRACT, AccessContract.RequestAndLogin.name());//"RequestAndLogin"
        mParam.put("sData", sData);
        return httpRequester.get(generalConfig.sHost() + sURI, mParam);
        */
    }

}
