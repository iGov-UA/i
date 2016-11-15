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
    //private final String URI_GET_PLACE_BY_PROCESS = "/wf/service/object/place/getPlaceByProcess";
    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;

    /*@Override
    public String getPlaceByProcess(String nID_Process)
            throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("nID_Process", nID_Process);
        return doRemoteRequest(URI_GET_PLACE_BY_PROCESS, params);
    }*/
    
    @Override
    public String getPlaceByProcess(String sID_Process) {
        Map<String, String> mParam = new HashMap<String, String>();
        mParam.put("nID_Process", sID_Process);
        //LOG.info("2sID_Process: " + sID_Process);
        mParam.put("nID_Server", generalConfig.getSelfServerId().toString());
        //LOG.info("3generalConfig.getSelfServerId().toString(): " + generalConfig.getSelfServerId().toString());
        String sURL = generalConfig.getSelfHostCentral() + "/wf/service/object/place/getPlaceByProcess";
        //LOG.info("ssURL: " + sURL);
        LOG.info("(sURL={},mParam={})", sURL, mParam);
        String soResponse = null;
        String sName = null;
        try {
            soResponse = httpRequester.getInside(sURL, mParam);
            LOG.info("soResponse={}", soResponse);
            Map mReturn = JsonRestUtils.readObject(soResponse, Map.class);
            LOG.info("mReturn={}" + mReturn);
            sName = (String) mReturn.get("sName");
            LOG.info("sName={}", sName);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
        //LOG.info("(soResponse={})", soResponse);
        return sName;//soResponse
    }
    
     private String doRemoteRequest(String sServiceContext, Map<String, String> mParam) throws Exception {
        String soResponse = "";
        if (!generalConfig.getSelfHostCentral().contains("ksds.nads.gov.ua") && !generalConfig.getSelfHostCentral().contains("staff.igov.org.ua")) {
            String sURL = generalConfig.getSelfHostCentral() + sServiceContext;
            LOG.info("(sURL={},mParam={})", sURL, mParam);
            soResponse = httpRequester.getInside(sURL, mParam);
            LOG.info("(soResponse={})", soResponse);
        }
        return soResponse;
    }

}
