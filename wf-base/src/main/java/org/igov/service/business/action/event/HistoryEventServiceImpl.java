package org.igov.service.business.action.event;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.service.business.access.AccessDataService;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
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

    public String doRemoteRequest(String sURL, Map<String, String> mParam, String sID_Order, String sUserTaskName)
            throws Exception {
        mParam.put("sID_Order", sID_Order);
        if (sUserTaskName != null) {
            mParam.put("sUserTaskName", sUserTaskName);
        }
        return doRemoteRequest(sURL, mParam);
    }

    @Override
    public String updateHistoryEvent(String sID_Order, String sUserTaskName, boolean addAccessKey, HistoryEvent_Service_StatusType nID_StatusType, Map<String, String> params)
            throws Exception {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("nID_StatusType", String.valueOf(nID_StatusType.getnID()));
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
    public String updateHistoryEvent(Map<String, String> params, Map<String, Object> body)  throws Exception {
        String soResponse = "";
        if (params == null) {
            params = new HashMap<>();
        }
        String sAccessKey_HistoryEvent = accessDataDao.setAccessData(
                httpRequester.getFullURL(URI_UPDATE_HISTORY_EVENT, params));
        params.put("sAccessKey", sAccessKey_HistoryEvent);
        params.put("sAccessContract", "Request");

        if (!generalConfig.getSelfHostCentral().contains("ksds.nads.gov.ua") && !generalConfig.getSelfHostCentral().contains("staff.igov.org.ua")) {
            String sURL = generalConfig.getSelfHostCentral() + httpRequester.getFullURL(URI_UPDATE_HISTORY_EVENT, params);
            String json = JSONValue.toJSONString(body);
            LOG.info("json from HistoryEventServiceImpl is: {}",json);
            String reqBody = URLEncoder.encode(json, "UTF-8");
            LOG.info("reqBody from HistoryEventServiceImpl is: {}", reqBody);
            LOG.info("START POST Request updateHistoryEvent (sURL={}, body={})", sURL, body);
            try {
                soResponse = httpRequester.postInside(sURL, null, reqBody, "text/html;charset=utf-8");
                LOG.info("(FINISH POST Request updateHistoryEvent soResponse={})", soResponse);
            } catch (Exception e){
                LOG.error("REJECTED POST Request updateHistoryEvent soResponse={})", soResponse);
                throw new Exception(e.getMessage());
            }
        }

        return soResponse;
    }

    @Override
    public String updateHistoryEvent(String sID_order, HistoryEvent_Service_StatusType statusType,
            Map<String, String> mParam) throws Exception {
        if (mParam == null) {
            mParam = new HashMap<>();
        }
        mParam.put("sID_Order", sID_order);
        mParam.put("nID_StatusType", String.valueOf(statusType.getnID()));
        return doRemoteRequest(URI_UPDATE_HISTORY_EVENT, mParam);
    }

    @Override
    public void addHistoryEvent(String sID_Order, String sUserTaskName, Map<String, String> params)
            throws Exception {
        LOG.info("addHistoryEvent params {}", params);
        doRemoteRequest(URI_ADD_HISTORY_EVENT, params, sID_Order, sUserTaskName);
    }

    @Override
    public String addServiceMessage(Map<String, String> mParam) throws Exception {
        String sURL = generalConfig.getSelfHostCentral() + URI_ADD_SERVICE_MESSAGE;
        LOG.info("(sURL={},mParam={})", sURL, mParam);
        String soResponse = httpRequester.getInside(sURL, mParam);
        LOG.info("(soResponse={})", soResponse);
        return soResponse;
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
