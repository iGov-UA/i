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
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.igov.io.GeneralConfig;
import org.igov.io.Log;
import org.igov.io.mail.NotificationPatterns;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.igov.service.business.escalation.EscalationHistoryService;
import org.igov.service.business.feedback.FeedBackService;
import org.igov.service.exception.TaskAlreadyUnboundException;
import org.json.simple.JSONArray;
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
import java.util.*;
import java.util.regex.Pattern;

import static org.igov.util.Tool.sCut;

/**
 * @author olya
 */
public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

    private static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|justice_incoming";
    private static final String asID_BP_SkipSendMail = "dnepr_mvk_291_common";
    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessingInterceptor.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("ControllerBig");
    //private static final Logger LOG_BIG = LoggerFactory.getLogger('APP');
    private boolean bFinish = false;

    private static final Pattern TAG_PATTERN_PREFIX = Pattern.compile("runtime/tasks/[0-9]+$");
    private static final Pattern SREQUESTBODY_PATTERN = Pattern.compile("\"assignee\":\"[а-яА-Яa-z_A-z0-9]+\"");
    private final String URI_SYNC_CONTACTS = "/wf/service/subject/syncContacts";
    private static final Long SubjectMessageType_CommentEscalation = 11L;
    private static final String URI_SET_SERVICE_MESSAGE = "/wf/service/subject/message/setServiceMessage";
    private static final String URI_COUNT_CLAIM_HISTORY = "/wf/service/action/event/getCountClaimHistory";

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
    @Autowired
    private FeedBackService feedBackService;

    private JSONParser oJSONParser = new JSONParser();

    @Override
    public boolean preHandle(HttpServletRequest oRequest,
            HttpServletResponse response, Object handler) throws Exception {

        bFinish = false;
        long startTime = System.currentTimeMillis();
        LOG.info("(getMethod()={}, getRequestURL()={})", oRequest.getMethod().trim(), oRequest.getRequestURL().toString());
        LOG_BIG.info("(getMethod()={}, getRequestURL()={})", oRequest.getMethod().trim(), oRequest.getRequestURL().toString());
        oRequest.setAttribute("startTime", startTime);
        protocolize(oRequest, response, false);
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
        bFinish = true;
        LOG.info("(nElapsedMS={})", System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime"));
        LOG_BIG.info("(nElapsedMS={})", System.currentTimeMillis() - (Long) oRequest.getAttribute("startTime"));
        oResponse = ((MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") != null
                ? (MultiReaderHttpServletResponse) oRequest.getAttribute("responseMultiRead") : oResponse);
        protocolize(oRequest, oResponse, true);
    }

    private void protocolize(HttpServletRequest oRequest, HttpServletResponse oResponse, boolean bSaveHistory)
            throws IOException, TaskAlreadyUnboundException {
        LOG.info("Method 'protocolize' started");
        int nLen = generalConfig.isSelfTest() ? 300 : 200;

        Map<String, String> mRequestParam = new HashMap<>();
        Enumeration paramsName = oRequest.getParameterNames();
        while (paramsName.hasMoreElements()) {
            String sKey = (String) paramsName.nextElement();
            mRequestParam.put(sKey, oRequest.getParameter(sKey));
        }

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
        String sURL = oRequest.getRequestURL().toString();
        LOG.info("sURL: " + sURL);
        String snTaskId = null;
        //getting task id from URL, if URL matches runtime/tasks/{taskId} (#1234)
        String sRequestBody = osRequestBody.toString();
        //LOG.info("sRequestBody: " + sRequestBody + " oRequest.getRequestURL(): " + oRequest.getRequestURL() + " oRequest.getMethod(): " + oRequest.getMethod());
        LOG.info("oRequest.getRequestURL(): " + oRequest.getRequestURL() + " oRequest.getMethod(): " + oRequest.getMethod());
        if (TAG_PATTERN_PREFIX.matcher(oRequest.getRequestURL()).find()) {
            snTaskId = sURL.substring(sURL.lastIndexOf("/") + 1);
            LOG.info("snTaskId: " + snTaskId);
            LOG.info("Request.getMethod().trim(): " + oRequest.getMethod().trim());
            if ("PUT".equalsIgnoreCase(oRequest.getMethod().trim()) && SREQUESTBODY_PATTERN.matcher(sRequestBody).find()) {
                LOG.info("URL is like runtime/tasks/{taskId}, getting task id from url, task id is " + snTaskId);
                Task task = taskService.createTaskQuery().taskId(snTaskId).singleResult();
                if (task.getAssignee() != null) {
                    LOG.info("task.getAssignee(): " + task.getAssignee());
                    throw new TaskAlreadyUnboundException(HttpStatus.FORBIDDEN + " Вибачте, звернення вже взято в роботу");
                }
            }
        }
        if (snTaskId != null && mRequestParam.get("taskId") == null) {
            mRequestParam.put("taskId", snTaskId);
        }

        if (!bFinish) {
            LOG.info("(mRequestParam={})", mRequestParam);
            LOG.info("(sRequestBody={})", sCut(nLen, sRequestBody));
            if (sURL.endsWith("/service/document/setDocumentFile")
                    || sURL.contains("/service/object/file/")) {
            } else {
                LOG_BIG.debug("(sRequestBody={})", sRequestBody);
            }
        }

        String sResponseBody = !bFinish ? null : oResponse.toString();
        if (bFinish) {
            LOG.info("(sResponseBody={})", sCut(nLen, sResponseBody));
            //https://region.igov.org.ua/wf/service/form/form-data
            if (sURL.endsWith("/service/action/item/getService")
                    || sURL.endsWith("/service/action/item/getServicesTree")
                    || (sURL.endsWith("/service/form/form-data")
                    && "GET".equalsIgnoreCase(oRequest.getMethod().trim()))
                    || sURL.endsWith("/service/repository/process-definitions")
                    || sURL.endsWith("/service/action/task/getStartFormData")
                    || sURL.endsWith("/service/action/task/getOrderMessages_Local")
                    || sURL.endsWith("/service/action/flow/getFlowSlots_ServiceData")
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
                    || sURL.contains("/service/document/getDocumentAbstract")) {
            } else {
                LOG_BIG.debug("(sResponseBody={})", sResponseBody);
            }
        }

        String sType = "";
        try {
            LOG.info("URL: {} method: {}", oRequest.getRequestURL(), oRequest.getMethod());
            if (!bSaveHistory || !(oResponse.getStatus() >= HttpStatus.OK.value()
                    && oResponse.getStatus() < HttpStatus.BAD_REQUEST.value())) {
                LOG.info("returning from protocolize block: bSaveHistory:{} oResponse.getStatus():{}", bSaveHistory, oResponse.getStatus());
//                return;
            }
            if (isSaveTask(oRequest, sResponseBody)) {
                sType = "Save";
                LOG.info("saveNewTaskInfo block started");
                if (oResponse.getStatus() < 200 || oResponse.getStatus() >= 300) {
                    try {
                        new Log(this.getClass(), LOG)//this.getClass()
                                ._Case("Activiti_FailStartTask")
                                ._Status(Log.LogStatus.ERROR)
                                ._Head("Error hapened while start process!")
                                ._Body(oResponse.toString())
                                ._Param("sRequestBody", sRequestBody)
                                ._Param("sResponseBody", sResponseBody)
                                ._Param("mRequestParam", mRequestParam)
                                .save();
                    } catch (Exception ex) {
                        LOG.error("Can't save error to MSG", ex);
                    }
                    return;
                } else {
                    saveNewTaskInfo(sRequestBody, sResponseBody, mRequestParam);
                }
                LOG.info("saveNewTaskInfo block finished");
            } else if (isCloseTask(oRequest, sResponseBody)) {
                sType = "Close";
                LOG.info("saveClosedTaskInfo block started");
                saveClosedTaskInfo(sRequestBody, snTaskId, bSaveHistory);
                LOG.info("saveClosedTaskInfo block finished");
            } else if (isUpdateTask(oRequest)) {
                sType = "Update";
                LOG.info("saveUpdatedTaskInfo block started");
                saveUpdatedTaskInfo(sResponseBody, mRequestParam);
                LOG.info("saveUpdatedTaskInfo block finished");
            }
        } catch (Exception oException) {
            LOG_BIG.error("Can't save service-history record: {}", oException.getMessage());
            LOG_BIG.error("FAIL:", oException);
            try {
                new Log(oException, LOG)//this.getClass()
                        ._Case("IC_Task" + sType)
                        ._Status(Log.LogStatus.ERROR)
                        ._Head("Can't save service-history record")
                        //                    ._Body(oException.getMessage())
                        ._Param("sURL", sURL)
                        ._Param("mRequestParam", mRequestParam)
                        ._Param("sRequestBody", sRequestBody)
                        ._Param("sResponseBody", sResponseBody)
                        ._LogTrace()
                        .save();
            } catch (Exception e) {
                LOG.error("Cann't send an error message to service MSG\n", e);
            }

        }
        LOG.info("Method 'protocolize' finished");
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
        //LOG.info("(is save task sResponseBody {}, '/form/form-data' {}. Method {} )", sResponseBody, oRequest.getRequestURL().toString().indexOf("/form/form-data"),oRequest.getMethod());
        return (bFinish && sResponseBody != null && !"".equals(sResponseBody))
                && oRequest.getRequestURL().toString().indexOf("/form/form-data") > 0
                && "POST".equalsIgnoreCase(oRequest.getMethod().trim());
    }

    private void saveNewTaskInfo(String sRequestBody, String sResponseBody, Map<String, String> mParamRequest)
            throws Exception {
        if (sResponseBody == null) {
            LOG.warn("sResponseBody=null!!! (sRequestBody={},mParamRequest={})", sRequestBody, mParamRequest);
        }
        Map<String, String> mParam = new HashMap<>();
        JSONObject omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);
        JSONObject omResponseBody = (JSONObject) oJSONParser.parse(sResponseBody);
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());

        String snID_Process = String.valueOf(omResponseBody.get("id")); //разобраться чего получаем нал в некоторых случаях
        if (snID_Process != null && !"null".equalsIgnoreCase(snID_Process)) {
            Long nID_Process = Long.valueOf(snID_Process);
            String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
            String snID_Subject = String.valueOf(omRequestBody.get("nID_Subject"));
            mParam.put("nID_Subject", snID_Subject);

            LOG.info("(sID_Order={},nID_Subject={})", sID_Order, snID_Subject);

            String snID_Service = mParamRequest.get("nID_Service");
            if (snID_Service != null) {
                mParam.put("nID_Service", snID_Service);
            }

            String sID_UA = mParamRequest.get("sID_UA");
            if (sID_UA != null) {
                mParam.put("sID_UA", sID_UA);
            }

            //TODO: need remove in future
            String snID_Region = mParamRequest.get("nID_Region");
            if (snID_Region != null) {
                mParam.put("nID_Region", snID_Region);
            }

            String snID_ServiceData = mParamRequest.get("nID_ServiceData");
            if (snID_ServiceData != null) {
                mParam.put("nID_ServiceData", snID_ServiceData);
            }

            HistoricProcessInstance oHistoricProcessInstance
                    = historyService.createHistoricProcessInstanceQuery().processInstanceId(snID_Process).singleResult();
            ProcessDefinition oProcessDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(oHistoricProcessInstance.getProcessDefinitionId()).singleResult();
            String sProcessName = oProcessDefinition.getName() != null ? oProcessDefinition.getName() : "";
            //mParam.put("sProcessInstanceName", sProcessInstanceName);
            mParam.put("sHead", sProcessName);

            List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).active().list();
            boolean bProcessClosed = aTask == null || aTask.size() == 0;
            String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();//"(нет назви)"

            String sMailTo = JsonRequestDataResolver.getEmail(omRequestBody);
            String sPhone = String.valueOf(JsonRequestDataResolver.getPhone(omRequestBody));
            String bankIdFirstName = JsonRequestDataResolver.getBankIdFirstName(omRequestBody);
            String bankIdLastName = JsonRequestDataResolver.getBankIdLastName(omRequestBody);
            //dnepr_mvk_291_common

            if (sMailTo != null) {
                if (!asID_BP_SkipSendMail.contains(oProcessDefinition.getKey())) {
                    ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, oProcessDefinition.getKey(), Long.valueOf(snID_Service));
                    LOG.info("Send notification mail... (sMailTo={})", sMailTo);
                    oNotificationPatterns.sendTaskCreatedInfoEmail(sMailTo, sID_Order, bankIdFirstName, bankIdLastName);
                } else {
                    LOG.info("SKIP Send notification mail... (sMailTo={}, oProcessDefinition.getKey()={})", sMailTo, oProcessDefinition.getKey());
                }
            }

            if (sMailTo != null || sPhone != null) {
                try {
                    Map<String, String> mParamSync = new HashMap<String, String>();
                    mParamSync.put("snID_Subject", snID_Subject);
                    mParamSync.put("sMailTo", sMailTo);
                    mParamSync.put("sPhone", sPhone);
                    LOG.info("Вносим параметры в коллекцию (sMailTo {}, snID_Subject {}, sPhone {})", sMailTo, snID_Subject,
                            sPhone);
                    String sURL = generalConfig.getSelfHostCentral() + URI_SYNC_CONTACTS;
                    LOG.info("(Подключаемся к центральному порталу)");
                    String sResponse = httpRequester.getInside(sURL, mParamSync);
                    LOG.info("(Подключение осуществлено)");

                } catch (Exception ex) {
                    LOG.warn("(isSaveTask exception {})", ex.getMessage());
                }

            }

            historyEventService.addHistoryEvent(sID_Order, sUserTaskName, mParam);
            //LOG.info("ok!");
            LOG.info("Before calling set action process count {}, {}", mParam, oProcessDefinition.getKey());
            if (DNEPR_MVK_291_COMMON_BP.contains(oProcessDefinition.getKey())) {
                ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, oProcessDefinition.getKey(), Long.valueOf(snID_Service));
            }
        }
    }


    /*
     *  Сохранение комментария эскалации. Как определяется что это комментарий эскалации:
     *  
     *  В historic-task-instances для этой заявки есть значение вида:
     *   "processDefinitionId":"system_escalation:16:23595004" здесь ключевое слово system_escalation
     *  
     *  Тело запроса имеет вид:
     *  {
     *    "taskId": "23737517",
     *    "properties": [
     *  {
     *       "id": "comment",			// В теле запроса присутствует комментарий
     *        "value": "zaqxsw2222"
     *      },
     *      {
     *        "id": "nCountDays",
     *        "value": "1"
     *      }
     *    ]
     *  } 
     */
    private void saveCommentSystemEscalation(String sID_Order, JSONObject omRequestBody, HistoricTaskInstance oHistoricTaskInstance) {
        String sComment = null;
        String sTaskId = (String) omRequestBody.get("taskId");
        String sProcessDefinitionId = null;
        Boolean isSystem_escalation = false;

        if (sID_Order == null) {
            LOG.error("sID_Order первичной заявки эскалации равен null");
            return;
        }

        // Блок определения - это эскалация или нет
        if (oHistoricTaskInstance != null) {
            sProcessDefinitionId = oHistoricTaskInstance.getProcessDefinitionId(); // строка вида: system_escalation:16:23595004
            LOG_BIG.debug("getProcessDefinitionId = {}", sProcessDefinitionId);
            if (sProcessDefinitionId != null && sProcessDefinitionId.contains(BpServiceHandler.PROCESS_ESCALATION)) {
                isSystem_escalation = true;
            }
        }
        if (!isSystem_escalation) {
            LOG_BIG.debug("Это не процесс эскалации");
            return;
        }

        // Блок получения комментария эскалации
        JSONArray properties = (JSONArray) omRequestBody.get("properties");
        @SuppressWarnings("unchecked")
        Iterator<JSONObject> iterator = properties.iterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = iterator.next();

            String sId = (String) jsonObject.get("id");
            String sValue = (String) jsonObject.get("value");

            if ("comment".equals(sId)) {
                sComment = sValue;
                LOG_BIG.debug("sTaskId = {}, sComment = {}", sTaskId, sComment);
                break;
            }
        }
        if (sComment == null) {
            LOG.error("Комментарий эскалации равен null");
            return;
        }

        LOG.debug("Попытка записи комментария эскалации. sID_Order={}, sComment={}, SubjectMessageType={}", sID_Order, sComment,
                SubjectMessageType_CommentEscalation);

        Map<String, String> mParamComment = new HashMap<>();
        mParamComment.put("sID_Order", sID_Order);
        mParamComment.put("sBody", sComment);
        mParamComment.put("nID_SubjectMessageType", Long.toString(SubjectMessageType_CommentEscalation));

        String sURL = generalConfig.getSelfHostCentral() + URI_SET_SERVICE_MESSAGE;

        try {
            String sResponse = httpRequester.getInside(sURL, mParamComment);

            LOG_BIG.debug("sResponse = {}", sResponse);

            JSONObject oResponseJson = (JSONObject) oJSONParser.parse(sResponse);
            String sCode = (String) oResponseJson.get("code");
            if ("200".equals(sCode)) {
                LOG.info("Добавлен комментарий эскалации: {}", sComment);
            } else {
                String sMessage = (String) oResponseJson.get("message");
                LOG.error("Ошибка при добавлении коммменатирия эскалации: {}", sMessage);
            }

        } catch (Exception e) {
            LOG.error("Ошибка при добавлении коммменатирия эскалации:", e);
        }

    }

    //(#1234) added additional parameter snClosedTaskId
    private void saveClosedTaskInfo(String sRequestBody, String snClosedTaskId, boolean bSaveHistory) throws Exception {
        LOG.info("Method saveClosedTaskInfo started");
        Map<String, String> mParam = new HashMap<>();
        JSONObject omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);

        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CLOSED.getnID().toString());

        String snID_Task = (String) omRequestBody.get("taskId");
        if ((snID_Task == null) && (snClosedTaskId != null)) {
            snID_Task = snClosedTaskId.trim();
            LOG.info("Task id from requestbody is null, so using task id from url - " + snID_Task);
        }
        LOG.info("Task id is - " + snID_Task);
        if (snID_Task != null) {
            HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .taskId(snID_Task).singleResult();

            String snID_Process = oHistoricTaskInstance.getProcessInstanceId();
            closeEscalationProcessIfExists(snID_Process);
            if (snID_Process != null) {
                LOG.info("Parsing snID_Process: " + snID_Process + " to long");
                Long nID_Process = Long.valueOf(snID_Process);
                String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);

                //------------
                HistoricTaskInstance taskDetails = historyService
                        .createHistoricTaskInstanceQuery()
                        .includeProcessVariables().taskId(snID_Task)
                        .singleResult();
                LOG_BIG.debug("taskDetails = {}", taskDetails);
                if (taskDetails != null) {
                    Map<String, Object> processVariables = taskDetails.getProcessVariables();
                    if (processVariables != null) {
                        String sProcessID = (String) processVariables.get("processID");
                        LOG.info("sProcessID ={}", sProcessID);
                        if (sProcessID != null) {
                            Long nID_ProcessV = Long.valueOf(sProcessID);
                            LOG.info("nID_ProcessV={}", nID_ProcessV);
                            sID_Order = generalConfig.getOrderId_ByProcess(nID_ProcessV);
                            LOG.info("sID_Order={}", sID_Order);
                        }
                    }
                }
                //--------------
                String snMinutesDurationProcess = getTotalTimeOfExecution(snID_Process);
                mParam.put("nTimeMinutes", snMinutesDurationProcess);
                LOG.info("(sID_Order={},nMinutesDurationProcess={})", sID_Order, snMinutesDurationProcess);
                List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();

                boolean bProcessClosed = (aTask == null || aTask.isEmpty());
                String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();
                LOG.info("11111sUserTaskName: " + sUserTaskName);
                String sProcessName = oHistoricTaskInstance.getProcessDefinitionId();
                LOG.info("sProcessName: " + sProcessName);
                try {
                    if (bProcessClosed && sProcessName.indexOf("system") != 0) {//issue 962
                        LOG_BIG.debug(String.format("start process feedback for process with snID_Process=%s", snID_Process));
                        /*                       if (!generalConfig.isSelfTest()) {
                            String snID_Proccess_Feedback = bpHandler
                                    .startFeedbackProcessNew(snID_Process);*/
                        String jsonHistoryEvent = historyEventService.getHistoryEvent(sID_Order);
                        JSONObject ojsonHistoryEvent = (JSONObject) oJSONParser.parse(jsonHistoryEvent);
                        LOG.info("ojsonHistoryEventmmmmmmmmmmmmmmmmmmmm = {}", ojsonHistoryEvent);
                        Long nID_Service = (Long) ojsonHistoryEvent.get("nID_Service");
                        String sID_UA = (String) ojsonHistoryEvent.get("sID_UA");
                        Map<String, String> mParamforcountClaim = new HashMap<>();
                        mParamforcountClaim.put("sID_UA", sID_UA);
                        mParamforcountClaim.put("nID_Service", String.valueOf(nID_Service));
                        mParamforcountClaim.put("nID_StatusType", HistoryEvent_Service_StatusType.CLOSED.getnID().toString());

                        String sURL = generalConfig.getSelfHostCentral() + URI_COUNT_CLAIM_HISTORY;

                        try {
                            String sResponse = httpRequester.getInside(sURL, mParamforcountClaim);
                            LOG.info("mParamforcountClaimmmmmmmmmmmmmmmmmmmm = {}", sResponse);

                            LOG_BIG.debug("sResponse = {}", sResponse);

                            Long countClaim = Long.valueOf(sResponse);
                            LOG.info("countClaimmmmmmmmmmmmmmmm ", countClaim);
                            if (countClaim.compareTo(50L) > 0) {
                                String snID_Proccess_Feedback = feedBackService.runFeedBack(snID_Task);

                                /* String snID_Proccess_Feedback = bpHandler
                                                              .startFeedbackProcess(snID_Task, snID_Process, sProcessName);*/
                                if (snID_Proccess_Feedback != null) {
                                    mParam.put("nID_Proccess_Feedback", snID_Proccess_Feedback);
                                    LOG.info("Create Feedback process! (sProcessName={}, nID_Proccess_Feedback={})",
                                            sProcessName,
                                            snID_Proccess_Feedback);
                                } else {
                                    LOG.info("Feedback process not start! (sProcessName={}, nID_Proccess_Feedback={})",
                                            sProcessName,
                                            snID_Proccess_Feedback);
                                }
                            }

                        } catch (Exception e) {
                            LOG.error("Ошибка при добавлении коммменатирия эскалации:", e);
                        }

                        //     } 
//                       else {
//                            LOG.info("SKIPED(test)!!! Create escalation process! (sProcessName={})", sProcessName);
//                        }
                    }
                } catch (Exception oException) {
                    new Log(oException, LOG)//this.getClass()
                            ._Case("IC_CreateEscalation")
                            ._Status(Log.LogStatus.ERROR)
                            ._Head("Can't create escalation process")
                            ._Param("nID_Process", nID_Process)
                            ._LogTrace()
                            .save();
                }

                // Сохраняем только после выполнения запроса afterCompletion 
                if (bSaveHistory) {
                    // Cохранение нового события для задачи
                    HistoryEvent_Service_StatusType status;
                    if (bProcessClosed) {
                        status = HistoryEvent_Service_StatusType.CLOSED;
                    } else {
                        status = HistoryEvent_Service_StatusType.OPENED;
                    }
                    LOG.info("Saving closed task");
                    mParam.put("sUserTaskName", sUserTaskName);
                    try {
                        if (!(sProcessName.contains(BpServiceHandler.PROCESS_ESCALATION) && status == HistoryEvent_Service_StatusType.CLOSED)) {
                            historyEventService.updateHistoryEvent(sID_Order, status, mParam);
                        }
                    } catch (Exception oException) {
                        new Log(oException, LOG)._Case("IC_SaveTaskHistoryEvent")._Status(Log.LogStatus.ERROR)
                                ._Head("Can't save history event for task")._Param("nID_Process", nID_Process).save();
                    }

                    // Сохранение комментария эскалации
                    if (sProcessName.contains(BpServiceHandler.PROCESS_ESCALATION)) {
                        try {
                            escalationHistoryService.updateStatus(nID_Process, bProcessClosed
                                    ? EscalationHistoryService.STATUS_CLOSED : EscalationHistoryService.STATUS_IN_WORK);
                        } catch (Exception oException) {
                            new Log(oException, LOG)// this.getClass()
                                    ._Case("IC_SaveEscalation")._Status(Log.LogStatus.ERROR)
                                    ._Head("Can't save status for escalation")
                                    ._Param("nID_Process", nID_Process).save();
                        }
                        try {
                            saveCommentSystemEscalation(sID_Order, omRequestBody, oHistoricTaskInstance);
                        } catch (Exception oException) {
                            new Log(oException, LOG)._Case("IC_SaveCommentVolonter")._Status(Log.LogStatus.ERROR)
                                    ._Head("Can't save volonter's comment")._Param("nID_Process", nID_Process).save();
                        }
                    }
                }
                LOG.info("Method saveClosedTaskInfo finished");
            }
        }
    }

    private void saveUpdatedTaskInfo(String sResponseBody, Map<String, String> mRequestParam) throws Exception {
        Map<String, String> mParam = new HashMap<>();
        JSONObject omResponseBody = (JSONObject) oJSONParser.parse(sResponseBody);
        String snID_Task = (String) omResponseBody.get("taskId");
        if (snID_Task == null && mRequestParam.containsKey("taskId")) {
            LOG.info("snID_Task is NULL, looking for it in mRequestParam");
            snID_Task = (String) mRequestParam.get("taskId");
            LOG.info("Found taskId in mRequestParam {}", snID_Task);
        }

        LOG.info("Looking for a task with ID {}", snID_Task);

        if (snID_Task == null && mRequestParam.containsKey("taskId")) {
            snID_Task = (String) mRequestParam.get("taskId");
            LOG.info("Found taskId in mRequestParam {}", snID_Task);
        }

        HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(snID_Task)
                .singleResult();

        mParam.put("sUserTaskName", oHistoricTaskInstance.getName());
        String snID_Process = oHistoricTaskInstance.getProcessInstanceId();
        closeEscalationProcessIfExists(snID_Process);
        Long nID_Process = Long.valueOf(snID_Process);
        String sSubjectInfo = mRequestParam.get("sSubjectInfo");
        if (sSubjectInfo != null) {
            mParam.put("sSubjectInfo", sSubjectInfo);
        }
        if (mRequestParam.get("nID_Subject") != null) {
            String nID_Subject = String.valueOf(mRequestParam.get("nID_Subject"));
            mParam.put("nID_Subject", nID_Subject);
        }
        LOG_BIG.info("mParams: {}", mParam.toString());
        String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
        LOG.info("(sID_Order={})", sID_Order);
        historyEventService.updateHistoryEvent(sID_Order, HistoryEvent_Service_StatusType.OPENED_ASSIGNED, mParam);
        LOG.info("historyEventService.updateHistoryEvent finished");
        String sProcessName = oHistoricTaskInstance.getProcessDefinitionId();
        try {
            LOG.info("Update escalation history... (sProcessName={})", sProcessName);
            if (sProcessName.indexOf(BpServiceHandler.PROCESS_ESCALATION) == 0) {//issue 981
                escalationHistoryService
                        .updateStatus(nID_Process, EscalationHistoryService.STATUS_IN_WORK);//Long.valueOf(sID_Process)
            } else { //issue 1297
                LOG.trace("BpServiceHandler.PROCESS_ESCALATION = {}", BpServiceHandler.PROCESS_ESCALATION);
            }
        } catch (Exception oException) {
            new Log(oException, LOG)//this.getClass()
                    ._Case("IC_UpdateEscalation")
                    ._Status(Log.LogStatus.ERROR)
                    ._Head("Can't update escalation history")
                    ._Param("nID_Process", nID_Process)
                    ._LogTrace()
                    .save();
        }
    }

    protected String getTotalTimeOfExecution(String sID_Process) {
        HistoricProcessInstance oHistoricProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(sID_Process).singleResult();
        String sReturn = "-1";
        try {
            LOG_BIG.debug("Found completed process with sID_Process = {} ", sID_Process);
            if (oHistoricProcessInstance != null) {
                Long nMinutesDurationProcess = oHistoricProcessInstance.getDurationInMillis();
                LOG_BIG.debug("nMinutesDurationProcess = {}, ms", nMinutesDurationProcess);

                if (nMinutesDurationProcess != null) {
                    nMinutesDurationProcess = nMinutesDurationProcess / (1000 * 60);
                    LOG_BIG.debug("nMinutesDurationProcess = {}, min", nMinutesDurationProcess);

                    sReturn = Long.toString(nMinutesDurationProcess);
                }
            }

        } catch (Exception oException) {

            new Log(oException, LOG)//this.getClass()
                    ._Case("IC_TimeExecution")
                    ._Status(Log.LogStatus.ERROR)
                    ._Head("Can't close escalation for task")
                    ._Param("sID_Process", sID_Process)
                    ._LogTrace()
                    .save();
        }
        return sReturn;
    }

    protected void closeEscalationProcessIfExists(String sID_Process) {
        LOG.info("Looking for escalation processes for process {}", sID_Process);
        List<ProcessInstance> escalationProceses = runtimeService.createProcessInstanceQuery().active().variableValueEquals("processID", sID_Process).list();

        if (escalationProceses != null && escalationProceses.size() > 0) {
            LOG.info("Found {} escalation processes", escalationProceses.size());

            Map<String, String> mParam = new HashMap<>();
            mParam.put("nID_Proccess_Escalation", "-1");
            LOG.info(" >>Clearing nID_Proccess_Escalation field for the process . (sID_Process={})", sID_Process);
            try {
                LOG.info(" updateHistoryEvent: " + sID_Process + " mParam: " + mParam);
                historyEventService.updateHistoryEvent(generalConfig.getOrderId_ByProcess(Long.valueOf(sID_Process)), null, false, HistoryEvent_Service_StatusType.UNKNOWN, mParam);
            } catch (Exception oException) {

                new Log(oException, LOG)//this.getClass()
                        ._Case("IC_CloseEscalation")
                        ._Status(Log.LogStatus.ERROR)
                        ._Head("Can't close escalation for task")
                        ._Param("sID_Process", sID_Process)
                        ._Param("mParam", mParam)
                        ._LogTrace()
                        .save();
            }

            for (ProcessInstance process : escalationProceses) {
                LOG.info("Removing process with ID:Key {}:{} ", process.getProcessInstanceId(), process.getProcessDefinitionKey());
                runtimeService.deleteProcessInstance(process.getProcessInstanceId(), "State of initial process has been changed. Removing escalaton process");
            }
        }
        LOG.info(" not found escalations for process {}", sID_Process);
    }

}
