package org.igov.activiti.bp.remote;

import org.igov.activiti.bp.HistoryEventService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.model.AccessDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoteHistoryEventService implements HistoryEventService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteHistoryEventService.class);
    private final String URI_GET_HISTORY_EVENT = "/wf/service/action/event/getHistoryEvent_Service";
    private final String URI_UPDATE_HISTORY_EVENT = "/wf/service/action/event/updateHistoryEvent_Service";
    private final String URI_ADD_HISTORY_EVENT = "/wf/service/action/event/addHistoryEvent_Service";
    private final String URI_ADD_SERVICE_MESSAGE = "/wf/service/messages/setServiceMessage";


    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private AccessDataDao accessDataDao;
    @Autowired
    private GeneralConfig generalConfig;

    @Override
    public String getHistoryEvent(String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server)
            throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order != null ? "" + sID_Order : null);
        params.put("nID_Protected", nID_Protected != null ? "" + nID_Protected : null);
        params.put("nID_Process", nID_Process != null ? "" + nID_Process : null);
        params.put("nID_Server", nID_Server != null ? "" + nID_Server : null);
        return doRemoteRequest(URI_GET_HISTORY_EVENT, params);
    }

    public String doRemoteRequest(String URI, Map<String, String> params, String sID_Process, String sID_Status)
            throws Exception {
        params.put("nID_Process", sID_Process);
        params.put("sID_Status", sID_Status);
        return doRemoteRequest(URI, params);
    }

    @Override
    public String updateHistoryEvent(String sID_Process, String sID_Status, boolean addAccessKey,
            Map<String, String> params) throws Exception {
        if (params == null) {
            params = new HashMap<>();
        }
        if (addAccessKey) {
            String sAccessKey_HistoryEvent = accessDataDao.setAccessData(
                    httpRequester.getFullURL(URI_UPDATE_HISTORY_EVENT, params));
            params.put("sAccessKey", sAccessKey_HistoryEvent);
            params.put("sAccessContract", "Request");
            LOG.info("sAccessKey={}", sAccessKey_HistoryEvent);
        }
        return doRemoteRequest(URI_UPDATE_HISTORY_EVENT, params, sID_Process, sID_Status);
    }

    @Override
    public void addHistoryEvent(String sID_Process, String sID_Status, Map<String, String> params)
            throws Exception {
        doRemoteRequest(URI_ADD_HISTORY_EVENT, params, sID_Process, sID_Status);
    }

    @Override
    public void addServiceMessage(Map<String, String> params) {
        try {
            LOG.info("Getting URL with parameters: {}{} : {}",generalConfig.sHostCentral(),URI_ADD_SERVICE_MESSAGE,
                    params);
            String soResponse = httpRequester.post(generalConfig.sHostCentral() + URI_ADD_SERVICE_MESSAGE, params);
            LOG.info("soResponse={}", soResponse);
            //            doRemoteRequest(URI_ADD_SERVICE_MESSAGE, params);
        } catch (Exception oException) {
            LOG.error("Error during setting message!: {}", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
    }

    private String doRemoteRequest(String URI, Map<String, String> params) throws Exception {
        LOG.info("Getting URL with parameters: {}{} : {}", generalConfig.sHostCentral(), URI,  params);
        String soJSON_HistoryEvent = httpRequester.get(generalConfig.sHostCentral() + URI, params);
        LOG.info("soJSON_HistoryEvent={}", soJSON_HistoryEvent);
        return soJSON_HistoryEvent;
    }


}
