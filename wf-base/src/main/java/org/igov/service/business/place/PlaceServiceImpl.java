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
import org.igov.service.business.access.AccessDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author HS
 */
@Service
public class PlaceServiceImpl implements PlaceService {

     private static final Logger LOG = LoggerFactory.getLogger(PlaceServiceImpl.class);
    private final String URI_GET_PLACE_BY_PROCESS = "/wf/service/business/place/getPlaceByProcess";
    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private AccessDataService accessDataDao;
    @Autowired
    private GeneralConfig generalConfig;

    @Override
    public String getPlaceByProcess(String sPlace)
            throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sPlace", sPlace);
        return doRemoteRequest(URI_GET_PLACE_BY_PROCESS, params);
    }

    public String doRemoteRequest(String sURL, Map<String, String> mParam, String sPlace, String sUserTaskName)
            throws Exception {
        mParam.put("sPlace", sPlace);
        if (sUserTaskName != null) {
            mParam.put("sUserTaskName", sUserTaskName);
        }
        return doRemoteRequest(sURL, mParam);
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
