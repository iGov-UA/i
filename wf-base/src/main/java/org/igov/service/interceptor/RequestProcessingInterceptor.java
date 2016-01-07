/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.interceptor;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.igov.service.adapter.MultiReaderHttpServletResponse;
import org.igov.activiti.bp.BpHandler;
import org.igov.activiti.bp.EscalationHistoryService;
import org.igov.activiti.bp.HistoryEventService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.igov.model.escalation.EscalationHistory;
import org.igov.io.mail.NotificationService;
import org.igov.io.web.HttpRequester;
import org.igov.io.GeneralConfig;
import org.igov.util.convert.AlgorithmLuna;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static org.igov.debug.Log.oLogBig_Interceptor;

/**
 * @author olya
 */
public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessingInterceptor.class);
    private static final Pattern TAG_PATTERN_PREFIX = Pattern.compile("runtime/tasks/[0-9]+$");
    
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    HttpRequester httpRequester;
    @Autowired
    NotificationService notificationService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private BpHandler bpHandler;
    @Autowired
    private EscalationHistoryService escalationHistoryService;

    private JSONParser parser = new JSONParser();

    @Override
    public boolean preHandle(HttpServletRequest oRequest,
            HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        LOG.info("[preHandle]:getRequestURL()=" + oRequest.getRequestURL().toString());
                //+ ",nMS_Start=" + System.currentTimeMillis());
        oLogBig_Interceptor.info("[preHandle]:getRequestURL()=" + oRequest.getRequestURL().toString());
        oRequest.setAttribute("startTime", startTime);
        saveHistory(oRequest, response, false);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest oRequest,
            HttpServletResponse oResponse, Object handler, Exception ex)
            throws Exception {
        LOG.info("[afterCompletion]:getRequestURL()=" + oRequest.getRequestURL().toString()
                + ",nElapsedMS=" + (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        
        oLogBig_Interceptor.info("[afterCompletion]:getRequestURL()=" + oRequest.getRequestURL().toString()
                + ",nElapsedMS=" + (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        
        oResponse = ((MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") != null
                ? (MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") : oResponse);
        saveHistory(oRequest, oResponse, true);
    }

    private void saveHistory(HttpServletRequest request, HttpServletResponse oResponse, boolean saveHistory)
            throws IOException {

        int nLen = generalConfig.bTest() ? 200 : 100;
        
        Map<String, String> mRequestParam = new HashMap<>();
        Enumeration paramsName = request.getParameterNames();
        while (paramsName.hasMoreElements()) {
            String sKey = (String) paramsName.nextElement();
            mRequestParam.put(sKey, request.getParameter(sKey));
        }
        LOG.info("[saveHistory]:mRequestParam: " + mRequestParam);
        oLogBig_Interceptor.info("[saveHistory]:mRequestParam: " + mRequestParam);
        
        StringBuilder osRequestBody = new StringBuilder();
        BufferedReader oReader = request.getReader();
        String line;
        if (oReader != null) {
            while ((line = oReader.readLine()) != null) {
                osRequestBody.append(line);
            }
            //mParamRequest.put("requestBody", buffer.toString()); 
            //TODO temp
        }
        String sRequestBody = osRequestBody.toString();
        LOG.info("[saveHistory]:sRequestBody: " + (sRequestBody != null ?
                (sRequestBody.length() > nLen ? sRequestBody.substring(0, nLen) : sRequestBody) :
                "null"));
        oLogBig_Interceptor.info("[saveHistory]:sRequestBody: " + (sRequestBody != null ? sRequestBody : "null"));
        LOG.debug("[saveHistory]:|||sRequestBody: " + (sRequestBody != null ? sRequestBody : "null"));

        String sResponseBody = oResponse.toString();
        LOG.info("[saveHistory]:sResponseBody: " + (sResponseBody != null ?
                (sResponseBody.length() > nLen ? sResponseBody.substring(0, nLen) : sResponseBody) :
                "null"));
        
        try {
            if (!saveHistory || !(oResponse.getStatus() >= HttpStatus.OK.value()
                    && oResponse.getStatus() < HttpStatus.BAD_REQUEST.value())) {
                return;
            }
            if (isSaveTask(request, sResponseBody)) {
                saveNewTaskInfo(sRequestBody, sResponseBody, mRequestParam);
            } else if (isCloseTask(request, sResponseBody)) {
                saveClosedTaskInfo(sRequestBody);
            } else if (isUpdateTask(request)) {
                saveUpdatedTaskInfo(sResponseBody);
            }
        } catch (Exception ex) {
            LOG.error("[saveHistory]:can't save service-history record! ", ex);
        }
    }

    private boolean isUpdateTask(HttpServletRequest request) {
        return request.getRequestURL().toString().indexOf("/runtime/tasks") > 0
                && "PUT".equalsIgnoreCase(request.getMethod().trim());
    }

    private boolean isCloseTask(HttpServletRequest request, String sResponseBody) {
        return "POST".equalsIgnoreCase(request.getMethod().trim())
                && (((sResponseBody == null || "".equals(sResponseBody))
                && request.getRequestURL().toString().indexOf("/form/form-data") > 0)
                || TAG_PATTERN_PREFIX.matcher(request.getRequestURL()).find());
    }

    private boolean isSaveTask(HttpServletRequest request, String sResponseBody) {
        return (sResponseBody != null && !"".equals(sResponseBody))
                && request.getRequestURL().toString().indexOf("/form/form-data") > 0
                && "POST".equalsIgnoreCase(request.getMethod().trim());
    }

    private void saveNewTaskInfo(String sRequestBody, String sResponseBody, Map<String, String> mParamRequest)
            throws Exception {
        Map<String, String> params = new HashMap<>();
        JSONObject jsonObjectRequest = (JSONObject) parser.parse(sRequestBody);
        JSONObject jsonObjectResponse = (JSONObject) parser.parse(sResponseBody);

        String sID_Process = (String) jsonObjectResponse.get("id");
        String taskName = "Заявка подана";

        HistoricProcessInstance historicProcessInstances =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(sID_Process).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicProcessInstances.getProcessDefinitionId()).singleResult();
        params.put("sProcessInstanceName", processDefinition.getName() != null ? processDefinition.getName() + "!" :
                "Non name!");
        params.put("nID_Subject", String.valueOf(jsonObjectRequest.get("nID_Subject")));
        //nID_Service, Long nID_Region, String sID_UA
        String snID_Region = mParamRequest.get("nID_Region");
        if (snID_Region != null) {
            params.put("nID_Region", snID_Region);
        }

        String snID_Service = mParamRequest.get("nID_Service");
        if (snID_Service != null) {
            params.put("nID_Service", snID_Service);
        }

        String sID_UA = mParamRequest.get("sID_UA");
        if (sID_UA != null) {
            params.put("sID_UA", sID_UA);
        }

        String nID_Server = mParamRequest.get("nID_Server");
        LOG.info("[saveNewTaskInfo]:nID_Server=" + nID_Server);
        LOG.info("[saveNewTaskInfo]:generalConfig.nID_Server()=" + generalConfig.nID_Server());
        nID_Server = (nID_Server != null) ? nID_Server : "" + generalConfig.nID_Server();
        params.put("nID_Server", nID_Server); //issue 889
        LOG.info("[saveNewTaskInfo]:nID_Server(fixed)=" + nID_Server);

        historyEventService.addHistoryEvent(sID_Process, taskName, params);

        String taskCreatorEmail = JsonRequestDataResolver.getEmail(jsonObjectRequest);
        LOG.info("[saveNewTaskInfo]:sendTaskCreatedInfoEmail... taskCreatorEmail = " + taskCreatorEmail);
        if (taskCreatorEmail != null) {
            /*
            String processDefinitionId = (String)jsonObjectRequest.get("processDefinitionId");
            if(processDefinitionId != null && processDefinitionId.indexOf("common_mreo_2") > -1){
                LOG.info("skip send email for common_mreo_2 proccess");
                return;
            }
            */
            Long nID_Protected = AlgorithmLuna.getProtectedNumber(Long.parseLong(sID_Process));
            notificationService.sendTaskCreatedInfoEmail(taskCreatorEmail, nID_Protected);
            LOG.info("[saveNewTaskInfo]:sent Email ok!");
        }
        LOG.info("[saveNewTaskInfo]:ok!");
    }
    
    private void saveClosedTaskInfo(String sRequestBody) throws Exception {
        String taskName;

        Map<String, String> mParam = new HashMap<>();
        JSONObject jsonObjectRequest = (JSONObject) parser.parse(sRequestBody);

        String task_ID = (String) jsonObjectRequest.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();

        String sID_Process = historicTaskInstance.getProcessInstanceId();
        List<Task> aTask = taskService.createTaskQuery().processInstanceId(sID_Process).list();
        boolean isProcessClosed = aTask == null || aTask.size() == 0;
        taskName = isProcessClosed ? "Заявка виконана" : aTask.get(0).getName();
        mParam.put("nTimeMinutes", getTotalTimeOfExecution(sID_Process));
        String processName = historicTaskInstance.getProcessDefinitionId();
        LOG.info("[saveClosedTaskInfo]:processName=" + processName);
        if (isProcessClosed && processName.indexOf("system") != 0) {//issue 962
            LOG.info(String.format("[saveClosedTaskInfo]:start process feedback for process with sID_Process=%s", sID_Process));
            String feedbackProcessId = bpHandler.startFeedbackProcess(task_ID, sID_Process, processName);
            mParam.put("nID_Proccess_Feedback", feedbackProcessId);
            LOG.info("[saveClosedTaskInfo]:nID_Proccess_Feedback=" + mParam.get("nID_Proccess_Feedback"));
        }
        try {
            if (processName.indexOf(BpHandler.PROCESS_ESCALATION) == 0) {//issue 981
                EscalationHistory escalationHistory = escalationHistoryService.updateStatus(Long.valueOf(sID_Process),
                        isProcessClosed ?
                                EscalationHistoryService.STATUS_CLOSED :
                                EscalationHistoryService.STATUS_IN_WORK);
                LOG.info("[saveClosedTaskInfo]:update escalation history: " + escalationHistory);
            }
        } catch (Exception e) {
            LOG.error("[saveClosedTaskInfo]:", e);
        }
        historyEventService.updateHistoryEvent(sID_Process, taskName, false, mParam);
    }

    protected String getTotalTimeOfExecution(String sID_Process) {
        HistoricProcessInstance foundResult = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(sID_Process).singleResult();

        String sReturn = "-1";
        long nMinutesDurationProcess = 0;
        LOG.info(String.format("[getTotalTimeOfExecution]:Found completed process with sID_Process=%s ", sID_Process));
        if (foundResult != null) {
            nMinutesDurationProcess = nMinutesDurationProcess + foundResult.getDurationInMillis() / (1000 * 60);
            sReturn = Long.valueOf(nMinutesDurationProcess).toString();
        }
        LOG.info(String.format("[getTotalTimeOfExecution]:Calculated time of execution of process sID_Process=%s and nMinutesDurationProcess=%s", sID_Process, nMinutesDurationProcess));

        return sReturn;
    }

    private void saveUpdatedTaskInfo(String sResponseBody) throws Exception {
        JSONObject jsonObjectResponse = (JSONObject) parser.parse(sResponseBody);
        String task_ID = (String) jsonObjectResponse.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();
        String sID_Process = historicTaskInstance.getProcessInstanceId();
        String sProcessName = historicTaskInstance.getProcessDefinitionId();

        //        String sID_Process = (String) jsonObjectResponse.get("processInstanceId");
        String taskName = jsonObjectResponse.get("name") + " (у роботi)";
        historyEventService.updateHistoryEvent(sID_Process, taskName, false, null);
        //
        LOG.info("[saveUpdatedTaskInfo]:sProcessName=" + sProcessName);
        try {
            if (sProcessName.indexOf(BpHandler.PROCESS_ESCALATION) == 0) {//issue 981
                LOG.info("[saveUpdatedTaskInfo]:begin update escalation history");
                escalationHistoryService
                        .updateStatus(Long.valueOf(sID_Process), EscalationHistoryService.STATUS_IN_WORK);
            }
        } catch (Exception e) {
            LOG.error("[saveUpdatedTaskInfo]:", e);
        }
    }

}
