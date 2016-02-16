package org.igov.service.business.action.task.bp;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.igov.service.business.action.task.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.igov.io.web.HttpEntityCover;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @author OlgaPrylypko
 * @since 22.12.2015
 */

@Service
public class BpServiceImpl implements BpService {

    private static final Logger LOG = LoggerFactory.getLogger(BpServiceImpl.class);
    private String uriWf = "/wf";
    private String uriStartProcess = "/service/action/task/start-process/%s?organ=%s";
    private String uriSetProcessVariable = "/service/action/task/setVariable";
    private String uriSetTaskVariable = "/service/action/task/setVariable";
    private String uriGetProcessTasks = "/service/action/task/getTasks";
    private String uriGetServer = "/wf/service/subject/getServer";


    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;

    @Override
    public String startProcessInstanceByKey(Integer nID_Server, String key, Map<String, Object> variables) {

        String organ = variables != null && variables.get("organ") != null ? (String)variables.get("organ") : null;
        String url = getServerUrl(nID_Server) + String.format(uriStartProcess, key, organ);
        LOG.info("Getting URL with parameters: (uri={}, variables={})", url, variables);
        Map<String, String> params = new HashMap<>();
        String jsonProcessInstance = "";
        try {
            jsonProcessInstance = httpRequester.getInside(url, params);
            LOG.info("(jsonProcessInstance={})", jsonProcessInstance);
            String instanceId = "" + new JSONObject(jsonProcessInstance).get("id");
            LOG.info("(instanceId={})", instanceId);
            for (String keyValue : variables.keySet()) {
                Object value = variables.get(keyValue);
                setVariableToProcessInstance(nID_Server, instanceId, keyValue, value);
            }
        } catch (Exception oException) {
            LOG.warn("error!: {}", oException.getMessage());
            LOG.debug("FAIL:", oException);
        }
        return jsonProcessInstance;
    }

    private String getServerUrl(Integer nID_server) {
        String serverUrl = generalConfig.sHost() + uriWf;
        String url = generalConfig.sHostCentral() + uriGetServer;
        Map<String, String> params = new HashMap<>();
        params.put("nID", "" + nID_server);
        try {
            String jsonServer = httpRequester.getInside(url, params);
            LOG.info("(jsonServer={})", jsonServer);
            serverUrl = "" + new JSONObject(jsonServer).get("sURL");
            LOG.info("(serverUrl={})", serverUrl);

        } catch (Exception oException) {
            LOG.warn("error!: {}", oException.getMessage());
            LOG.debug("FAIL:", oException);
        }
        return serverUrl;
    }

    @Override
    public void setVariableToProcessInstance(Integer nID_Server, String instanceId, String key, Object value) {
        if (value != null && !"null".equals(value.toString())) {
            LOG.info(String.format("set value [%s] to [%s] in process with id=%s", key, value, instanceId));
            Map<String, String> params = new HashMap<>();
            String url = getServerUrl(nID_Server) + uriSetProcessVariable;
            params.put("processInstanceId", instanceId);
            params.put("key", key);
            params.put("value", value.toString());
            try {
                String jsonProcessInstance = httpRequester.getInside(url, params);
            } catch (Exception oException) {
                LOG.warn("error!: {}", oException.getMessage());
                LOG.debug("FAIL:", oException);
            }
        }
    }

    @Override
    public List<String> getProcessTasks(Integer nID_Server, String processInstanceId) {
        List<String> result = new LinkedList<>();
        Map<String, String> params = new HashMap<>();
        String url = getServerUrl(nID_Server) + uriGetProcessTasks;
        params.put("processInstanceId", processInstanceId);
        try {
            String jsonProcessInstance = httpRequester.getInside(url, params);
            LOG.info("response: (jsonProcessInstance={})", jsonProcessInstance);
            JSONArray jsonArray = new JSONArray(jsonProcessInstance);
            for (int i = 0; i < jsonArray.length(); i++) {
                String taskId = jsonArray.getString(i);
                result.add(taskId);
            }
        } catch (Exception oException) {
            LOG.warn("error!: {}", oException.getMessage());
            LOG.debug("FAIL:", oException);
        }
        return result;
    }

    @Override
    public void setVariableToActivitiTask(Integer nID_Server, String taskId, String key, Object value) {
        if (value != null && !"null".equals(value.toString())) {
            LOG.info(String.format("set value [%s] to [%s] in task with id=%s", key, value, taskId));
            Map<String, String> params = new HashMap<>();
            String url = getServerUrl(nID_Server) + uriSetTaskVariable;
            params.put("taskId", taskId);
            params.put("key", key);
            params.put("value", value.toString());
            try {
                String jsonProcessInstance = httpRequester.getInside(url, params);
            } catch (Exception oException) {
                LOG.warn("error!: {}", oException.getMessage());
                LOG.debug("FAIL:", oException);
            }
        }
    }

}
