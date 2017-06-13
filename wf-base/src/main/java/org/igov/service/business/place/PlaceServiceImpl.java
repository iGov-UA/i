/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.place;

import java.util.HashMap;
import java.util.Map;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author HS issue 1350
 */
@Service
public class PlaceServiceImpl implements PlaceService {

    private static final Logger LOG = LoggerFactory.getLogger(PlaceServiceImpl.class);
    
    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;
    
    @Override
    public String getPlaceByProcess(String sID_Process) {
        Map<String, String> mParam = new HashMap<>();
        mParam.put("nID_Process", sID_Process);
        mParam.put("nID_Server", generalConfig.getSelfServerId().toString());
        String sURL = generalConfig.getSelfHostCentral() + "/wf/service/object/place/getPlaceByProcess";
        LOG.info("(sURL={},mParam={})", sURL, mParam);
        String soResponse;
        String sName = null;
        try {
            soResponse = httpRequester.getInside(sURL, mParam);
            LOG.info("soResponse={}", soResponse);
            Map mReturn = JsonRestUtils.readObject(soResponse, Map.class);
            LOG.info("mReturn={}" + mReturn);
            
            if(mReturn.get("fullName") != null){
                return (String) mReturn.get("fullName");
            }
            else{
                sName = (String) mReturn.get("sName");
            }
            
            LOG.info("sName={}", sName);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
        return sName;//soResponse
    }

}
