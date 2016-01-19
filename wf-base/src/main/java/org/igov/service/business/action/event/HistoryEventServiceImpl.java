package org.igov.service.business.action.event;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.service.business.access.AccessDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HistoryEventServiceImpl implements HistoryEventService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryEventServiceImpl.class);
    private final String URI_GET_HISTORY_EVENT = "/wf/service/action/event/getHistoryEvent_Service";
    private final String URI_UPDATE_HISTORY_EVENT = "/wf/service/action/event/updateHistoryEvent_Service";
    private final String URI_ADD_HISTORY_EVENT = "/wf/service/action/event/addHistoryEvent_Service";
    private final String URI_ADD_SERVICE_MESSAGE = "/wf/service/subject/message/setServiceMessage";


    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private AccessDataService accessDataDao;
    @Autowired
    private GeneralConfig generalConfig;

    @Override
    public String getHistoryEvent(String sID_Order)
            throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order);
        return doRemoteRequest(URI_GET_HISTORY_EVENT, params);
    }

    public String doRemoteRequest(String URI, Map<String, String> params, String sID_Order, String sUserTaskName)
            throws Exception {
        params.put("sID_Order", sID_Order);
        params.put("sUserTaskName", sUserTaskName);
        return doRemoteRequest(URI, params);
    }

    @Override
    public String updateHistoryEvent(String sID_Order,String sUserTaskName, boolean addAccessKey,Map<String, String> params) 
            throws Exception {
        if (params == null) {
            params = new HashMap<>();
        }
        if (addAccessKey) {
            String sAccessKey_HistoryEvent = accessDataDao.setAccessData(
                    httpRequester.getFullURL(URI_UPDATE_HISTORY_EVENT, params));
            params.put("sAccessKey", sAccessKey_HistoryEvent);
            params.put("sAccessContract", "Request");
            LOG.info("(sAccessKey={})", sAccessKey_HistoryEvent);
        }
        return doRemoteRequest(URI_UPDATE_HISTORY_EVENT, params, sID_Order, sUserTaskName);
    }

    @Override
    public void addHistoryEvent(String sID_Order, String sUserTaskName, Map<String, String> params)
            throws Exception {
        doRemoteRequest(URI_ADD_HISTORY_EVENT, params, sID_Order, sUserTaskName);
    }

    @Override
    public String addServiceMessage(Map<String, String> params) {
        String soResponse = "";
        try {
//            LOG.info("Getting URL with parameters: " + generalConfig.sHostCentral() + URI_ADD_SERVICE_MESSAGE + ":"
//                    + params);
            LOG.info(String.format("Getting URL with parameters: %s:%s",
                    generalConfig.sHostCentral() + URI_ADD_SERVICE_MESSAGE, params));
            soResponse = httpRequester.get(generalConfig.sHostCentral() + URI_ADD_SERVICE_MESSAGE, params);
            LOG.info("(soResponse={})", soResponse);
        } catch (Exception oException) {
            LOG.error("error during setting message!: {}", oException.getMessage());
            soResponse = "{error: " + oException.getMessage() + "}";
        }
        return soResponse;
    }

    private String doRemoteRequest(String URI, Map<String, String> params) throws Exception {
//        LOG.info("Getting URL with parameters: " + generalConfig.sHostCentral() + URI + ":" + params);
        LOG.info(String.format("Getting URL with parameters: %s:%s", generalConfig.sHostCentral() + URI, params));
        String soJSON_HistoryEvent = httpRequester.get(generalConfig.sHostCentral() + URI, params);
        LOG.info("(soJSON_HistoryEvent={})", soJSON_HistoryEvent);
        return soJSON_HistoryEvent;
    }


}
