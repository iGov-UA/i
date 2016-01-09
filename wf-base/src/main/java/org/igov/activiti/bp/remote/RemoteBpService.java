package org.igov.activiti.bp.remote;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.igov.activiti.bp.BpService;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author OlgaPrylypko
 * @since 22.12.2015
 */

@Service
public class RemoteBpService implements BpService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteBpService.class);
    private String uriWf = "/wf";
    private String uriStartProcess = "/service/action/task/start-process/%s";
    private String uriSetProcessVariable = "/service/action/task/setVariable";
    private String uriSetTaskVariable = "/service/action/task/setVariable";
    private String uriGetProcessTasks = "/service/action/task/getTasks";
    private String uriGetServer = "/wf/service/subject/getServer";


    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private TaskService taskService;

    @Override
    public String startProcessInstanceByKey(Integer nID_Server, String key, Map<String, Object> variables) {

        String url = getServerUrl(nID_Server) + String.format(uriStartProcess, key);
        LOG.info("Getting URL with parameters: " + url + ":" + variables);
        Map<String, String> params = new HashMap<>();
        String jsonProcessInstance = "";
        try {
            jsonProcessInstance = httpRequester.get(url, params);
            LOG.info("jsonProcessInstance=" + jsonProcessInstance);
            String instanceId = "" + new JSONObject(jsonProcessInstance).get("id");
            LOG.info("instanceId=" + instanceId);
            for (String keyValue : variables.keySet()) {
                Object value = variables.get(keyValue);
                setVariableToProcessInstance(nID_Server, instanceId, keyValue, value);
            }
        } catch (Exception e) {
            LOG.warn("error!", e);
        }
        return jsonProcessInstance;
    }

    private String getServerUrl(Integer nID_server) {
        String serverUrl = generalConfig.sHost() + uriWf;
        String url = generalConfig.sHostCentral() + uriGetServer;
        Map<String, String> params = new HashMap<>();
        params.put("nID", "" + nID_server);
        try {
            String jsonServer = httpRequester.get(url, params);
            LOG.info("jsonServer=" + jsonServer);
            serverUrl = "" + new JSONObject(jsonServer).get("sURL");
            LOG.info("serverUrl=" + serverUrl);

        } catch (Exception e) {
            LOG.warn("error!", e);
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
                String jsonProcessInstance = httpRequester.get(url, params);
            } catch (Exception e) {
                LOG.warn("error!", e);
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
            String jsonProcessInstance = httpRequester.get(url, params);
            LOG.info("response=" + jsonProcessInstance);
            JSONArray jsonArray = new JSONArray(jsonProcessInstance);
            for (int i = 0; i < jsonArray.length(); i++) {
                String taskId = jsonArray.getString(i);
                result.add(taskId);
            }
        } catch (Exception e) {
            LOG.warn("error!", e);
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
                String jsonProcessInstance = httpRequester.get(url, params);
            } catch (Exception e) {
                LOG.warn("error!", e);
            }
        }
    }

}
