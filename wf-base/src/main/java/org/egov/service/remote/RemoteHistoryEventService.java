package org.egov.service.remote;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.rest.controller.ActivitiExceptionController;
import org.activiti.rest.controller.ActivitiRestException;
import org.egov.service.HistoryEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.wf.dp.dniprorada.base.dao.AccessDataDao;
import org.wf.dp.dniprorada.rest.HttpRequester;
import org.wf.dp.dniprorada.util.GeneralConfig;

/**
 * @author vit@tym.im
 */
@Service
public class RemoteHistoryEventService implements HistoryEventService {
    private static final Logger log = LoggerFactory.getLogger(RemoteHistoryEventService.class);
    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private AccessDataDao accessDataDao;
    @Autowired
    private GeneralConfig generalConfig;

    @Override
    public String getHistoryEvent(String nID_Protected) throws Exception {
        String URI = "/wf/service/services/getHistoryEvent_Service";
        ImmutableMap.Builder<String, String> params = ImmutableMap.<String, String>builder()
                .put("nID_Protected", nID_Protected);
        return doRemoteRequest(URI, params);
    }

    @Override
    public void validateHistoryEventToken(Long nID_Protected,
            @RequestParam(value = "sToken") String sToken) throws Exception {
        String historyEvent = getHistoryEvent(nID_Protected.toString());
        JSONObject fieldsJson = new JSONObject(historyEvent);
        if (fieldsJson.has("sToken")) {
            String tasksToken = fieldsJson.getString("sToken");
            if (tasksToken.isEmpty() || !tasksToken.equals(sToken)) {
                throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Token is wrong");
            }
        } else {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    "Token is absent");
        }
    }

    private String doRemoteRequest(String URI, ImmutableMap.Builder<String, String> params) throws Exception {
        ImmutableMap<String, String> paramsMap = params.build();
        log.info("Getting URL with parameters: " + generalConfig.sHostCentral() + URI + ":" + paramsMap);
        String soJSON_HistoryEvent = httpRequester.get(
                generalConfig.sHostCentral() + URI, paramsMap);
        log.info("soJSON_HistoryEvent=" + soJSON_HistoryEvent);
        return soJSON_HistoryEvent;
    }

    public String doRemoteRequest(String URI, ImmutableMap.Builder<String, String> params, String sID_Process, String sID_Status)
            throws Exception {
        if (sID_Process == null) {
            log.warn("For service operation '%s' nID_Process is null. Operation will not be called!", URI);
            return null;
        } else {
            params.put("nID_Process", sID_Process);
            params.put("sID_Status", sID_Status);
            return doRemoteRequest(URI, params);
        }
    }

    @Override
    public String updateHistoryEvent(String sID_Process, String sID_Status, boolean addAccessKey,
            ImmutableMap.Builder<String, String> params) throws Exception {
        String URI = "/wf/service/services/updateHistoryEvent_Service";
        if (params == null) {
            params = ImmutableMap.builder();
        }
        if (addAccessKey) {
            String sAccessKey_HistoryEvent = accessDataDao.setAccessData(
                    httpRequester.getFullURL(URI, params.build()));
            params.put("sAccessKey", sAccessKey_HistoryEvent);
            log.info("sAccessKey=" + sAccessKey_HistoryEvent);
        }
        return doRemoteRequest(URI, params, sID_Process, sID_Status);
    }

    @Override
    public void addHistoryEvent(String sID_Process,
            String taskName, String sProcessInstanceName, String nID_Subject, String snID_Region, String snID_Service,
            String sID_ua) throws Exception {
        ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
        params.put("sProcessInstanceName", sProcessInstanceName);
        params.put("nID_Subject", nID_Subject);
        //nID_Service, Long nID_Region, String sID_UA
        if (snID_Region != null) {
            params.put("nID_Region", snID_Region);
        }

        if (snID_Service != null) {
            params.put("nID_Service", snID_Service);
        }

        if (sID_ua != null) {
            params.put("sID_UA", sID_ua);
        }

        String URI = "/wf/service/services/addHistoryEvent_Service";
        doRemoteRequest(URI, params, sID_Process, taskName);
    }
}
