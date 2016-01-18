/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.controller.interceptor;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.igov.service.business.escalation.EscalationHistoryService;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.NotificationPatterns;
import org.igov.io.web.HttpRequester;
import org.igov.model.escalation.EscalationHistory;
import org.igov.util.convert.AlgorithmLuna;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import static org.igov.io.Log.oLogBig_Controller;

import static org.igov.io.Log.oLogBig_Interceptor;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import static org.igov.util.Util.sCut;

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
    NotificationPatterns oNotificationPatterns;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private BpServiceHandler bpHandler;
    @Autowired
    private EscalationHistoryService escalationHistoryService;

    private JSONParser parser = new JSONParser();

    @Override
    public boolean preHandle(HttpServletRequest oRequest,
            HttpServletResponse response, Object handler) throws Exception {

        long startTime = System.currentTimeMillis();
        LOG.info("getRequestURL()=" + oRequest.getRequestURL().toString());
                //+ ",nMS_Start=" + System.currentTimeMillis());
        oLogBig_Controller.info("getRequestURL()=" + oRequest.getRequestURL().toString());
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
        LOG.info("getRequestURL()=" + oRequest.getRequestURL().toString()
                + ",nElapsedMS=" + (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        
        oLogBig_Controller.info("getRequestURL()=" + oRequest.getRequestURL().toString()
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
        LOG.info("mRequestParam: " + mRequestParam);
        oLogBig_Controller.info("mRequestParam: " + mRequestParam);
        
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
        LOG.info("sRequestBody: " + sCut(nLen,sRequestBody));
        oLogBig_Controller.info("sRequestBody: " + sRequestBody);
        //LOG.debug("sRequestBody: " + sRequestBody);

        String sResponseBody = oResponse.toString();
        LOG.info("sResponseBody: " + sCut(nLen,sResponseBody));
        oLogBig_Controller.info("sResponseBody: " + (sResponseBody != null ? sResponseBody : "null"));
        
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
            LOG.error("can't save service-history record! ", ex);
            oLogBig_Controller.error("can't save service-history record! ", ex);
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

        String snID_Process = (String) jsonObjectResponse.get("id");
        LOG.info("snID_Process=" + snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        
        params.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());

        HistoricProcessInstance historicProcessInstances =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(snID_Process).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(historicProcessInstances.getProcessDefinitionId()).singleResult();
        
        String sUserTaskName = "(нет назви)";
        sUserTaskName = processDefinition.getName() != null ? processDefinition.getName() : sUserTaskName;
        params.put("sProcessInstanceName", sUserTaskName);
        String snID_Subject = String.valueOf(jsonObjectRequest.get("nID_Subject"));
        LOG.info("snID_Subject=" + snID_Subject);
        params.put("snID_Subject", snID_Subject);
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

        //String sID_Order = generalConfig.sID_Order(AlgorithmLuna.getProtectedNumber(Long.parseLong(sID_Process)));
        //String sID_Order = generalConfig.sID_Order_ByProcess(nID_Server, nID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("sID_Order=" + sID_Order);
        
//        String snID_Server = mParamRequest.get("nID_Server");
//        LOG.info("snID_Server=" + snID_Server);
//        Integer nID_Server = Integer.valueOf(snID_Server);
        //LOG.info("generalConfig.nID_Server()=" + generalConfig.nID_Server());
        //nID_Server = (nID_Server != null) ? nID_Server : "" + generalConfig.nID_Server();
        //params.put("nID_Server", nID_Server); //issue 889
        //LOG.info("nID_Server(fixed)=" + nID_Server);

        String sMailTo = JsonRequestDataResolver.getEmail(jsonObjectRequest);
        LOG.info("Check if ned sendTaskCreatedInfoEmail... (sMailTo={})", sMailTo);
        if (sMailTo != null) {
            /*
            String processDefinitionId = (String)jsonObjectRequest.get("processDefinitionId");
            if(processDefinitionId != null && processDefinitionId.indexOf("common_mreo_2") > -1){
                LOG.info("skip send email for common_mreo_2 proccess");
                return;
            }
            */
            oNotificationPatterns.sendTaskCreatedInfoEmail(sMailTo, sID_Order);
            LOG.info("Sent Email ok!");
        }
        
        historyEventService.addHistoryEvent(sID_Order, sUserTaskName, params);

        LOG.info("ok!");
    }
    
    private void saveClosedTaskInfo(String sRequestBody) throws Exception {
        String taskName;

        Map<String, String> mParam = new HashMap<>();
        JSONObject jsonObjectRequest = (JSONObject) parser.parse(sRequestBody);

        String task_ID = (String) jsonObjectRequest.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();

        //String sID_Process = historicTaskInstance.getProcessInstanceId();
        String snID_Process = historicTaskInstance.getProcessInstanceId();
        LOG.info("snID_Process=" + snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("sID_Order=" + sID_Order);
        
        List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
        boolean isProcessClosed = aTask == null || aTask.size() == 0;
        taskName = isProcessClosed ? "" : aTask.get(0).getName();
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CLOSED.getnID().toString());
        mParam.put("nTimeMinutes", getTotalTimeOfExecution(snID_Process));
        
        String processName = historicTaskInstance.getProcessDefinitionId();
        LOG.info("processName=" + processName);
        if (isProcessClosed && processName.indexOf("system") != 0) {//issue 962
            LOG.info(String.format("start process feedback for process with snID_Process=%s", snID_Process));
            String feedbackProcessId = bpHandler.startFeedbackProcess(task_ID, snID_Process, processName);
            mParam.put("nID_Proccess_Feedback", feedbackProcessId);
            LOG.info("nID_Proccess_Feedback=" + mParam.get("nID_Proccess_Feedback"));
        }
        try {
            if (processName.indexOf(BpServiceHandler.PROCESS_ESCALATION) == 0) {
                //issue 981 -- save history
                EscalationHistory escalationHistory = escalationHistoryService.updateStatus(nID_Process,
                        isProcessClosed ?
                                EscalationHistoryService.STATUS_CLOSED :
                                EscalationHistoryService.STATUS_IN_WORK);
                LOG.info("update escalation history: " + escalationHistory);
                //issue 1038 -- save message
                LOG.info("try to save service message for escalation process with snID_Process=" + snID_Process);
                String serviceMessage = bpHandler.createServiceMessage(task_ID);
                LOG.info("jsonServiceMessage=" + serviceMessage);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        historyEventService.updateHistoryEvent(sID_Order, taskName, false, mParam);//sID_Process
    }

    private void saveUpdatedTaskInfo(String sResponseBody) throws Exception {
        JSONObject jsonObjectResponse = (JSONObject) parser.parse(sResponseBody);
        String task_ID = (String) jsonObjectResponse.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();
        
        //String sID_Process = historicTaskInstance.getProcessInstanceId();
        String snID_Process = historicTaskInstance.getProcessInstanceId();
        LOG.info("snID_Process=" + snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("sID_Order=" + sID_Order);
        
        String sProcessName = historicTaskInstance.getProcessDefinitionId();

        //        String sID_Process = (String) jsonObjectResponse.get("processInstanceId");
        String taskName = HistoryEvent_Service_StatusType.OPENED_ASSIGNED.getsName_UA();
        historyEventService.updateHistoryEvent(sID_Order, taskName, false, null);
        Map<String, String> params = new HashMap<>();
        params.put("nID_StatusType", HistoryEvent_Service_StatusType.OPENED_ASSIGNED.getnID().toString());
        historyEventService.updateHistoryEvent(sID_Order, taskName, false, params);
        
        //
        LOG.info("sProcessName=" + sProcessName);
        try {
            if (sProcessName.indexOf(BpServiceHandler.PROCESS_ESCALATION) == 0) {//issue 981
                LOG.info("begin update escalation history");
                escalationHistoryService
                        .updateStatus(nID_Process, EscalationHistoryService.STATUS_IN_WORK);//Long.valueOf(sID_Process)
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }
    
    protected String getTotalTimeOfExecution(String sID_Process) {
        HistoricProcessInstance foundResult = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(sID_Process).singleResult();

        String sReturn = "-1";
        long nMinutesDurationProcess = 0;
        LOG.info(String.format("Found completed process with sID_Process=%s ", sID_Process));
        if (foundResult != null) {
            nMinutesDurationProcess = nMinutesDurationProcess + foundResult.getDurationInMillis() / (1000 * 60);
            sReturn = Long.valueOf(nMinutesDurationProcess).toString();
        }
        LOG.info(String.format("Calculated time of execution of process sID_Process=%s and nMinutesDurationProcess=%s", sID_Process, nMinutesDurationProcess));

        return sReturn;
    }
    

}
