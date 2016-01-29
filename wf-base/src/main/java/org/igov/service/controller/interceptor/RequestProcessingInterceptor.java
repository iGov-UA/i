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
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import static org.igov.util.Util.sCut;

/**
 * @author olya
 */
public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessingInterceptor.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("ControllerBig");
    //private static final Logger LOG_BIG = LoggerFactory.getLogger('APP');
    
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
        LOG.info("(getRequestURL()={})", oRequest.getRequestURL().toString());
        LOG_BIG.info("(getRequestURL()={})", oRequest.getRequestURL().toString());
                //+ ",nMS_Start=" + System.currentTimeMillis());
        //LOG.debug("getRequestURL()=" + oRequest.getRequestURL().toString());
        //oLogBig_Controller.info("getRequestURL()=" + oRequest.getRequestURL().toString());
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
        LOG.info("(getRequestURL()={}, nElapsedMS={})", oRequest.getRequestURL().toString()
                , (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        LOG_BIG.info("(getRequestURL()={}, nElapsedMS={})", oRequest.getRequestURL().toString()
                , (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        //LOG.debug("(getRequestURL()={}, nElapsedMS={})", oRequest.getRequestURL().toString()
        //        , System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime"));
        //oLogBig_Controller.info("getRequestURL()=" + oRequest.getRequestURL().toString()
        //        + ",nElapsedMS=" + (System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime")));
        
        oResponse = ((MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") != null
                ? (MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") : oResponse);
        saveHistory(oRequest, oResponse, true);
    }

    private void saveHistory(HttpServletRequest oRequest, HttpServletResponse oResponse, boolean saveHistory)
            throws IOException {

        int nLen = generalConfig.bTest() ? 300 : 200;
        
        Map<String, String> mRequestParam = new HashMap<>();
        Enumeration paramsName = oRequest.getParameterNames();
        while (paramsName.hasMoreElements()) {
            String sKey = (String) paramsName.nextElement();
            mRequestParam.put(sKey, oRequest.getParameter(sKey));
        }
        LOG.info("(mRequestParam: {})", mRequestParam);
        //oLogBig_Interceptor.info("mRequestParam: " + mRequestParam);
        
        StringBuilder osRequestBody = new StringBuilder();
        BufferedReader oReader = oRequest.getReader();
        String line;
        if (oReader != null) {
            while ((line = oReader.readLine()) != null) {
                osRequestBody.append(line);
            }
            //mParamRequest.put("requestBody", buffer.toString()); 
            //TODO temp
        }
        String sURL = request.getRequestURL().toString();
        
        String sRequestBody = osRequestBody.toString();
        LOG.info("(sRequestBody: {})", sCut(nLen,sRequestBody));
        if(
                sURL.endsWith("/service/document/setDocumentFile")
                || sURL.contains("/service/object/file/")
                ){
        }else{
            LOG_BIG.debug("(sRequestBody: {})", sRequestBody);
        }        
        //oLogBig_Interceptor.info("sRequestBody: " + sRequestBody);
        //LOG.debug("sRequestBody: " + sRequestBody);

        String sResponseBody = oResponse.toString();
        LOG.info("(sResponseBody: {})", sCut(nLen,sResponseBody));
        //LOG.debug("(sResponseBody: {})", sResponseBody);
        //https://region.igov.org.ua/wf/service/form/form-data
        if(
                sURL.endsWith("/service/action/item/getService")
                || sURL.endsWith("/service/action/item/getServicesTree")
                || (sURL.endsWith("/service/form/form-data") && "GET".equalsIgnoreCase(request.getMethod().trim()))
                || sURL.endsWith("/service/repository/process-definitions")
                || sURL.endsWith("/service/action/task/getStartFormData")
                || sURL.endsWith("/service/action/task/getOrderMessages_Local")
                || sURL.endsWith("/service/action/flow/getFlowSlots_ServiceData")
                //|| sURL.endsWith("/runtime/tasks/9514334/attachments")
                 //|| sURL.contains("/runtime/tasks/")
                || sURL.contains("/service/runtime/tasks")
                || sURL.endsWith("/service/history/historic-task-instances")
                || sURL.endsWith("/service/action/task/getLoginBPs")
                || sURL.endsWith("/service/subject/message/getMessages")
                || sURL.endsWith("/service/subject/message/getServiceMessages")
                || sURL.endsWith("/service/object/place/getPlacesTree")
                || sURL.endsWith("/service/action/event/getLastTaskHistory")
                || sURL.endsWith("/service/action/event/getLastTaskHistory")
                || sURL.endsWith("/service/action/event/getHistoryEventsService")
                || sURL.endsWith("/service/action/event/getHistoryEvents")
                || sURL.endsWith("/service/document/getDocumentContent")
                || sURL.endsWith("/service/document/getDocumentFile")
                || sURL.endsWith("/service/document/getDocumentAbstract")
                || sURL.endsWith("/service/document/getDocuments")
                || sURL.endsWith("/service/document/setDocumentFile")
                || sURL.contains("/service/object/file/")
                ){
        }else{
            LOG_BIG.debug("(sResponseBody: {})", sResponseBody);
        }
        
        //LOG.debug("sResponseBody: " + (sResponseBody != null ? sResponseBody : "null"));
        //oLogBig_Controller.info("sResponseBody: " + (sResponseBody != null ? sResponseBody : "null"));
        
        try {
            if (!saveHistory || !(oResponse.getStatus() >= HttpStatus.OK.value()
                    && oResponse.getStatus() < HttpStatus.BAD_REQUEST.value())) {
                return;
            }
            if (isSaveTask(oRequest, sResponseBody)) {
                saveNewTaskInfo(sRequestBody, sResponseBody, mRequestParam);
            } else if (isCloseTask(oRequest, sResponseBody)) {
                saveClosedTaskInfo(sRequestBody);
            } else if (isUpdateTask(oRequest)) {
                saveUpdatedTaskInfo(sResponseBody);
            }
        } catch (Exception ex) {
            LOG.error("Can't save service-history record: {}",ex.getMessage());
            LOG_BIG.error("Can't save service-history record: {}",ex.getMessage());
            LOG_BIG.trace("FAIL:", ex);
            //oLogBig_Controller.error("can't save service-history record! ", ex);
        }
    }

    private boolean isUpdateTask(HttpServletRequest oRequest) {
        return oRequest.getRequestURL().toString().indexOf("/runtime/tasks") > 0
                && "PUT".equalsIgnoreCase(oRequest.getMethod().trim());
    }

    private boolean isCloseTask(HttpServletRequest oRequest, String sResponseBody) {
        return "POST".equalsIgnoreCase(oRequest.getMethod().trim())
                && (((sResponseBody == null || "".equals(sResponseBody))
                && oRequest.getRequestURL().toString().indexOf("/form/form-data") > 0)
                || TAG_PATTERN_PREFIX.matcher(oRequest.getRequestURL()).find());
    }

    private boolean isSaveTask(HttpServletRequest oRequest, String sResponseBody) {
        return (sResponseBody != null && !"".equals(sResponseBody))
                && oRequest.getRequestURL().toString().indexOf("/form/form-data") > 0
                && "POST".equalsIgnoreCase(oRequest.getMethod().trim());
    }

    private void saveNewTaskInfo(String sRequestBody, String sResponseBody, Map<String, String> mParamRequest)
            throws Exception {
        Map<String, String> mParam = new HashMap<>();
        JSONObject jsonObjectRequest = (JSONObject) parser.parse(sRequestBody);
        JSONObject jsonObjectResponse = (JSONObject) parser.parse(sResponseBody);

        String snID_Process = (String) jsonObjectResponse.get("id");
        //LOG.info("(snID_Process={})", snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("(sID_Order={})", sID_Order);

        HistoricProcessInstance oHistoricProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(snID_Process).singleResult();
        
        ProcessDefinition oProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(oHistoricProcessInstance.getProcessDefinitionId()).singleResult();

        List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
        boolean bProcessClosed = aTask == null || aTask.size() == 0;
        String sUserTaskName = bProcessClosed ? "" : aTask.get(0).getName();//"(нет назви)"
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
        String sProcessName = oProcessDefinition.getName() != null ? oProcessDefinition.getName() : "";
        //mParam.put("sProcessInstanceName", sProcessInstanceName);
        mParam.put("sHead", sProcessName);
        String snID_Subject = String.valueOf(jsonObjectRequest.get("nID_Subject"));
        LOG.info("(snID_Subject={})", snID_Subject);
        mParam.put("nID_Subject", snID_Subject);

        String snID_Service = mParamRequest.get("nID_Service");
        if (snID_Service != null) {
            mParam.put("nID_Service", snID_Service);
        }

        String sID_UA = mParamRequest.get("sID_UA");
        if (sID_UA != null) {
            mParam.put("sID_UA", sID_UA);
        }
        
        //TODO: need remove infuture
        String snID_Region = mParamRequest.get("nID_Region");
        if (snID_Region != null) {
            mParam.put("nID_Region", snID_Region);
        }

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
        historyEventService.addHistoryEvent(sID_Order, sUserTaskName, mParam);
        LOG.info("ok!");
    }
    
    private void saveClosedTaskInfo(String sRequestBody) throws Exception {
        Map<String, String> mParam = new HashMap<>();
        JSONObject jsonObjectRequest = (JSONObject) parser.parse(sRequestBody);

        String task_ID = (String) jsonObjectRequest.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();

        //String sID_Process = historicTaskInstance.getProcessInstanceId();
        String snID_Process = historicTaskInstance.getProcessInstanceId();
        //LOG.info("(snID_Process={})", snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("(sID_Order={})", sID_Order);

        List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
        boolean bProcessClosed = aTask == null || aTask.size() == 0;
        String sUserTaskName = bProcessClosed ? "" : aTask.get(0).getName();
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CLOSED.getnID().toString());
        mParam.put("nTimeMinutes", getTotalTimeOfExecution(snID_Process));

        String sProcessName = historicTaskInstance.getProcessDefinitionId();
        LOG.info("(sProcessName={})", sProcessName);
        if (bProcessClosed && sProcessName.indexOf("system") != 0) {//issue 962
            LOG.info(String.format("start process feedback for process with snID_Process=%s", snID_Process));
            String feedbackProcessId = bpHandler.startFeedbackProcess(task_ID, snID_Process, sProcessName);
            mParam.put("nID_Proccess_Feedback", feedbackProcessId);
            LOG.info("nID_Proccess_Feedback={}", mParam.get("nID_Proccess_Feedback"));
        }
        try {
            if (sProcessName.indexOf(BpServiceHandler.PROCESS_ESCALATION) == 0) {
                //issue 981 -- save history
                EscalationHistory escalationHistory = escalationHistoryService.updateStatus(nID_Process,
                        bProcessClosed ?
                                EscalationHistoryService.STATUS_CLOSED :
                                EscalationHistoryService.STATUS_IN_WORK);
                LOG.info("update escalation history: {}", escalationHistory);
                //issue 1038 -- save message
                LOG.info("try to save service message for escalation process with (snID_Process={})", snID_Process);
                String serviceMessage = bpHandler.createServiceMessage(task_ID);
                LOG.info("jsonServiceMessage={}", serviceMessage);
            }
        } catch (Exception e) {
            LOG.error("Can't save service message for escalation: {}",e.getMessage());
            LOG.trace("FAIL:", e);
        }
        historyEventService.updateHistoryEvent(sID_Order, sUserTaskName, false, mParam);//sID_Process
    }

    private void saveUpdatedTaskInfo(String sResponseBody) throws Exception {
        JSONObject jsonObjectResponse = (JSONObject) parser.parse(sResponseBody);
        String task_ID = (String) jsonObjectResponse.get("taskId");
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(
                task_ID).singleResult();

        //String sID_Process = historicTaskInstance.getProcessInstanceId();
        String snID_Process = historicTaskInstance.getProcessInstanceId();
        //LOG.info("(snID_Process={})", snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
        LOG.info("(sID_Order={})", sID_Order);

        String sUserTaskName = HistoryEvent_Service_StatusType.OPENED_ASSIGNED.getsName_UA();
        historyEventService.updateHistoryEvent(sID_Order, sUserTaskName, false, null);
        Map<String, String> mParam = new HashMap<>();
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.OPENED_ASSIGNED.getnID().toString());
        historyEventService.updateHistoryEvent(sID_Order, sUserTaskName, false, mParam);
        
        //
        String sProcessName = historicTaskInstance.getProcessDefinitionId();
        LOG.info("(sProcessName={})", sProcessName);
        try {
            if (sProcessName.indexOf(BpServiceHandler.PROCESS_ESCALATION) == 0) {//issue 981
                LOG.info("begin update escalation history");
                escalationHistoryService
                        .updateStatus(nID_Process, EscalationHistoryService.STATUS_IN_WORK);//Long.valueOf(sID_Process)
            }
        } catch (Exception e) {
            LOG.error("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
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
