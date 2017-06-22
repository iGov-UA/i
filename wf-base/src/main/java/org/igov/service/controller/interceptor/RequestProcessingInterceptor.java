/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.controller.interceptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

import org.apache.commons.mail.EmailException;

import org.igov.io.GeneralConfig;
import org.igov.io.Log;
import org.igov.io.mail.NotificationPatterns;
import org.igov.io.web.HttpRequester;

import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightDao;
import org.igov.model.process.ProcessSubject;
import org.igov.model.process.ProcessSubjectDao;
import org.igov.model.subject.SubjectGroup;
import org.igov.model.subject.SubjectGroupDao;

import org.igov.service.business.action.event.ActionEventHistoryService;
import org.igov.service.business.action.event.CloseTaskEvent;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.escalation.EscalationHistoryService;
import org.igov.service.exception.TaskAlreadyUnboundException;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * @author olya
 */
public class RequestProcessingInterceptor extends HandlerInterceptorAdapter implements ConstantsInterceptor {
	
    private static final String DNEPR_MVK_291_COMMON_BP = "dnepr_mvk_291_common|_test_UKR_DOC|dnepr_mvk_889|justice_incoming|_doc_justice_171";
    private static final Logger LOG = LoggerFactory.getLogger(RequestProcessingInterceptor.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("ControllerBig");
    private boolean bFinish = false;
    private static final String FORM_FORM_DATA = "/form/form-data";
    private static final String START_PROCESS = "/startProcess";
    private static final String DOCUMENT_SERVICE = "/action/task/setDocument";
    private static final String RUNTIME_TASKS = "/runtime/tasks";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String GET = "GET";

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
    private ActionTaskService actionTaskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private EscalationHistoryService escalationHistoryService;
    @Autowired
    private CloseTaskEvent closeTaskEvent;
    @Autowired
    private ActionEventHistoryService oActionEventHistoryService;
    @Autowired
    @Qualifier("documentStepDao")
    private GenericEntityDao<Long, DocumentStep> documentStepDao;
    @Autowired
    private DocumentStepSubjectRightDao oDocumentStepSubjectRightDao;
    
    @Autowired
    private ProcessSubjectDao oProcessSubjectDao;
    
    @Autowired
    private SubjectGroupDao oSubjectGroupDao;
    
    @Value("${asID_BP_SendMail}")
    private String[] asID_BP_SendMail;

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
        documentHistoryPreProcessing(oRequest, response);
        processSubjectStatusHistoryWritingPreHandle(oRequest);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest oRequest,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        
        processSubjectStatusHistoryWritingPostHandle(oRequest);
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
        documentHistoryPostProcessing(oRequest, oResponse);
    }

    private void documentHistoryPostProcessing(HttpServletRequest oRequest, HttpServletResponse oResponse) {
        try {
            Map<String, String> mRequestParam = new HashMap<>();
            Enumeration<String> paramsName = oRequest.getParameterNames();

            while (paramsName.hasMoreElements()) {
                String sKey = (String) paramsName.nextElement();
                mRequestParam.put(sKey, oRequest.getParameter(sKey));
            }
            String sResponseBody = !bFinish ? "" : oResponse.toString();
            
            if (isCloseTask(oRequest, sResponseBody) || isDocumentSubmit(oRequest) || isUpdateTask(oRequest)
                    || ((mRequestParam.containsKey("sID_BP") || mRequestParam.containsKey("snID_Process_Activiti"))
                        && mRequestParam.get("sID_BP") != null && mRequestParam.get("sID_BP").startsWith("_doc"))
            ) {
                
                StringBuilder osRequestBody = new StringBuilder();
                BufferedReader oReader = oRequest.getReader();
                String line;

                if (oReader != null) {
                    while ((line = oReader.readLine()) != null) {
                        osRequestBody.append(line);
                    }
                }

                String sRequestBody = osRequestBody.toString();

                String sURL = oRequest.getRequestURL().toString();

                JSONObject omRequestBody = null;
                JSONObject omResponseBody = null;

                try {
                    if (!sRequestBody.trim().equals("")) {
                        omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);
                    }
                } catch (Exception ex) {
                    LOG.info("Error parsing sRequestBody: {}", ex);
                    //LOG.info("sRequestBody is: {}", sRequestBody);
                }

                try {
                    if (!sResponseBody.trim().equals("")) {
                        omResponseBody = (JSONObject) oJSONParser.parse(sResponseBody);
                    }
                } catch (Exception ex) {
                    LOG.debug("Error parsing sResponseBody: {}", ex);
                    //LOG.debug("sRequestBody is: {}", sResponseBody);
                }

                if (isCloseTask(oRequest, sResponseBody)) {
                    if (omRequestBody != null && omRequestBody.containsKey("taskId") && mRequestParam.isEmpty()) {
                        String sTaskId = (String) omRequestBody.get("taskId");
                        LOG.info("sTaskId is: {}", sTaskId);
                        HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(sTaskId).singleResult();
                        String processInstanceId = oHistoricTaskInstance.getProcessInstanceId();

                        LOG.info("oHistoricTaskInstance.getProcessDefinitionId {}", oHistoricTaskInstance.getProcessDefinitionId());

                        if (oHistoricTaskInstance.getProcessDefinitionId().startsWith("_doc_")) {

                            LOG.info("Close document is started...");
                            Map<String, String> mParam = new HashMap<>();
                            String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(processInstanceId));
                            mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());

                            List<Task> aTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();

                            boolean bProcessClosed = aTask == null || aTask.size() == 0;
                            String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                            if (aTask.isEmpty()) {
                                oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 18L);
                            }
                        }
                    }
                }

                /*if (isUpdateTask(oRequest)) {
                    LOG.info("--------------ALL PARAMS IN SUBMIT DOCUMENT (POSTPROCESSING)--------------");
                    LOG.info("protocolize sURL is: " + sURL);
                    /*LOG.info("-----------------------------------------------");
                    LOG.info("sRequestBody: {}", sRequestBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("sResponseBody: {}", sResponseBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("mRequestParam {}", mRequestParam);
                    LOG.info("-----------------------------------------------");
                }*/

                if (isDocumentSubmit(oRequest)) {
                    if (omRequestBody != null && omRequestBody.containsKey("taskId") && mRequestParam.isEmpty()) {
                        
                        String sTaskId = (String) omRequestBody.get("taskId");
                        LOG.info("sTaskId is: {}", sTaskId);
                        
                        HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(sTaskId).singleResult();
                        String processInstanceId = oHistoricTaskInstance.getProcessInstanceId();
                        
                        if (oHistoricTaskInstance.getProcessDefinitionId().startsWith("_doc_")) {
                            
                            /*LOG.info("--------------isDocumentSubmit (POSTPROCESSING)--------------");
                            LOG.info("sRequestBody: {}", sRequestBody);


                            List<HistoricVariableInstance> aHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                                    .processInstanceId(processInstanceId).list();

                            for(HistoricVariableInstance oHistoricVariableInstance : aHistoricVariableInstance){
                                LOG.info("oHistoricVariableInstance.getId {}", oHistoricVariableInstance.getId());
                                LOG.info("oHistoricVariableInstance.getVariableName {}", oHistoricVariableInstance.getVariableName());
                                LOG.info("oHistoricVariableInstance.getVariableTypeName {}", oHistoricVariableInstance.getVariableTypeName());
                            }

                            HistoricVariableInstance oHistoricVariableInstance =
                                historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableNameLike("%bOrder=true%").singleResult();

                            if(oHistoricVariableInstance != null){
                                LOG.info("oHistoricVariableInstance.getVariableName with like {}", oHistoricVariableInstance.getVariableName());
                            }*/
                            
                            runtimeService.setVariable(processInstanceId, "sLogin_LastSubmited", oHistoricTaskInstance.getAssignee());
                        }
                    }
                }

                if (((mRequestParam.containsKey("sID_BP") || mRequestParam.containsKey("snID_Process_Activiti"))
                        && mRequestParam.get("sID_BP") != null && mRequestParam.get("sID_BP").startsWith("_doc"))) {
                    LOG.info("--------------ALL REQUEST DOCUMENT PARAMS (POSTPROCESSING)--------------");
                    sURL = oRequest.getRequestURL().toString();
                    /*LOG.info("protocolize sURL is: " + sURL);
                    LOG.info("-----------------------------------------------");
                    LOG.info("sRequestBody: {}", sRequestBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("sResponseBody: {}", sResponseBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("mRequestParam {}", mRequestParam);
                    LOG.info("-----------------------------------------------");*/

                    String sID_Process = null;
                    //String sID_Order = null;

                    if (omResponseBody != null) {
                        sID_Process = (String) omResponseBody.get("snID_Process");
                        if (sID_Process != null) {
                            String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(sID_Process));
                            HistoricProcessInstance oHistoricProcessInstance
                                    = historyService.createHistoricProcessInstanceQuery().processInstanceId(sID_Process).singleResult();
                            ProcessDefinition oProcessDefinition = repositoryService.createProcessDefinitionQuery()
                                    .processDefinitionId(oHistoricProcessInstance.getProcessDefinitionId()).singleResult();
                            String sProcessName = oProcessDefinition.getName() != null ? oProcessDefinition.getName() : "";

                            List<Task> aTask = taskService.createTaskQuery().processInstanceId(sID_Process).active().list();
                            boolean bProcessClosed = aTask == null || aTask.size() == 0;
                            String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                            Map<String, String> mParam = new HashMap<>();

                            LOG.info("document nID_StatusType in interceptor {}", HistoryEvent_Service_StatusType.CREATED.getnID());
                            mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                            LOG.info("document sID_Process in interceptor {}", sID_Process);
                            LOG.info("document sID_Order in interceptor {}", sID_Order);
                            LOG.info("document sUserTaskName in interceptor {}", sUserTaskName);

                            if (!(oResponse.getStatus() < 200 || oResponse.getStatus() >= 300
                                    || (sResponseBody != null && sResponseBody.contains(SYSTEM_ERR)))) {
                                if (isSetDocumentService(oRequest, sResponseBody)) {
                                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 11L);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOG.info("Error during document processing in interceptor: {} ", ex);
        }
    }

    private void documentHistoryPreProcessing(HttpServletRequest oRequest, HttpServletResponse oResponse) {
        try {

            Map<String, String> mRequestParam = new HashMap<>();
            Enumeration<String> paramsName = oRequest.getParameterNames();

            while (paramsName.hasMoreElements()) {
                String sKey = (String) paramsName.nextElement();
                mRequestParam.put(sKey, oRequest.getParameter(sKey));
            }
            
            if (isUpdateTask(oRequest) || isDocumentSubmit(oRequest)) {

                StringBuilder osRequestBody = new StringBuilder();
                BufferedReader oReader = oRequest.getReader();
                String line;

                if (oReader != null) {
                    while ((line = oReader.readLine()) != null) {
                        osRequestBody.append(line);
                    }
                }

                String sRequestBody = osRequestBody.toString();
                //String sResponseBody = !bFinish ? "" : oResponse.toString();

                String sURL = oRequest.getRequestURL().toString();

                JSONObject omRequestBody = null;
                //JSONObject omResponseBody = null;

                try {
                    if (!sRequestBody.trim().equals("")) {
                        omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);
                    }
                } catch (Exception ex) {
                    LOG.info("Error parsing sRequestBody: {}", ex);
                    //LOG.info("sRequestBody is: {}", sRequestBody);
                }

               /*try {
                    if (!sResponseBody.trim().equals("")) {
                        omResponseBody = (JSONObject) oJSONParser.parse(sResponseBody);
                    }
                } catch (Exception ex) {
                    LOG.info("Error parsing sResponseBody: {}", ex);
                    //LOG.info("sRequestBody is: {}", sResponseBody);
                }

                if (isUpdateProcess(oRequest)){

                    LOG.info("--------------ALL PARAMS IN UPDATE PROCESS(REGION)--------------");
                    LOG.info("protocolize sURL is: " + sURL);
                    LOG.info("-----------------------------------------------");
                    LOG.info("sRequestBody: {}", sRequestBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("sResponseBody: {}", sResponseBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("mRequestParam {}", mRequestParam);        
                    LOG.info("-----------------------------------------------");

                    Map mJsonBody = JsonRestUtils.readObject(sRequestBody, Map.class);
                    if(mJsonBody != null){
                            if (mJsonBody.containsKey("taskId")){
                                    LOG.info("Processsing task with ID: " + mJsonBody.get("taskId"));
                                    List<Task> tasks = taskService.createTaskQuery().taskId((String) mJsonBody.get("taskId")).list();
                            String executionId = null;
                            if (tasks != null && tasks.size() > 0){
                                    Task firstTask = tasks.get(0);
                                    executionId = firstTask.getExecutionId();

                                    String sID_Order = generalConfig.getOrderId_ByProcess(Long.valueOf(firstTask.getProcessInstanceId()));

                                    Map<String, String> mParam = new HashMap<>();

                                LOG.info("updateProcess nID_StatusType in interceptor {}", HistoryEvent_Service_StatusType.CREATED.getnID());
                                mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                                LOG.info("updateProcess sID_Process in interceptor {}", firstTask.getProcessInstanceId().toString());
                                LOG.info("updateProcess sID_Order in interceptor {}", sID_Order);
                                LOG.info("updateProcess sHead in interceptor {}", firstTask.getName());
                                mParam.put("sHead", firstTask.getName());

                                    oActionEventHistoryService.addHistoryEvent(sID_Order, firstTask.getName(), mParam, 11L);
                            }
                            }
                    }
                }*/
                if (isUpdateTask(oRequest)) {
                    LOG.info("--------------ALL PARAMS IN SUBMIT DOCUMENT (PREPROCESSING)--------------");
                    LOG.info("protocolize sURL is: " + sURL);
                    /*LOG.info("-----------------------------------------------");
                    LOG.info("sRequestBody: {}", sRequestBody);
                    LOG.info("-----------------------------------------------");
                    //LOG.info("sResponseBody: {}", sResponseBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("mRequestParam {}", mRequestParam);
                    LOG.info("-----------------------------------------------");*/
                }

                if (isDocumentSubmit(oRequest)) {

                    LOG.info("--------------ALL PARAMS IN SUBMIT(REGION - PreProcessing)--------------");
                    LOG.info("protocolize sURL is: " + sURL);
                    /*LOG.info("-----------------------------------------------");
                    LOG.info("sRequestBody: {}", sRequestBody);
                    LOG.info("-----------------------------------------------");
                    //LOG.info("sResponseBody: {}", sResponseBody);
                    LOG.info("-----------------------------------------------");
                    LOG.info("mRequestParam {}", mRequestParam);
                    LOG.info("-----------------------------------------------");*/

                    processDocumentSubmit(mRequestParam, omRequestBody);

                    /*if(isCloseTask(oRequest, sResponseBody)){
                        if (omRequestBody != null && omRequestBody.containsKey("taskId") && mRequestParam.isEmpty()) {
                            String sTaskId = (String) omRequestBody.get("taskId");
                            LOG.info("sTaskId is: {}", sTaskId);
                            HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(sTaskId).singleResult();
                            String processInstanceId = oHistoricTaskInstance.getProcessInstanceId();

                            LOG.info("oHistoricTaskInstance.getProcessDefinitionId {}", oHistoricTaskInstance.getProcessDefinitionId());

                            if (oHistoricTaskInstance.getProcessDefinitionId().startsWith("_doc_")) {

                                LOG.info("Close document is started...");
                                Map<String, String> mParam = new HashMap<>();
                                String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(processInstanceId));
                                mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());

                                List<Task> aTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
                                boolean bProcessClosed = aTask == null || aTask.size() == 0;
                                String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                                oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 18L);
                            }
                        }
                    }*/
                }
            }
        } catch (Exception ex) {
            LOG.info("Error during document processing in interceptor: {} ", ex);
        }
    }

    private void processDocumentSubmit(Map<String, String> mRequestParam, JSONObject omRequestBody) throws Exception {

        if (omRequestBody != null && omRequestBody.containsKey("taskId") && mRequestParam.isEmpty()) {
            String sTaskId = (String) omRequestBody.get("taskId");
            LOG.info("sTaskId is: {}", sTaskId);
            HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(sTaskId).singleResult();
            
            String processInstanceId = oHistoricTaskInstance.getProcessInstanceId();
            String executionId = oHistoricTaskInstance.getExecutionId();
            LOG.info("oHistoricTaskInstance.getProcessDefinitionId {}", oHistoricTaskInstance.getProcessDefinitionId());
            LOG.info("oHistoricTaskInstance.processInstanceId {}", processInstanceId);
            LOG.info("oHistoricTaskInstance.getExecutionId {}", executionId);

            List<HistoricVariableInstance> aHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                                    .processInstanceId(processInstanceId).list();

            for(HistoricVariableInstance oHistoricVariableInstance : aHistoricVariableInstance){
                LOG.info("oHistoricVariableInstance.getId {}", oHistoricVariableInstance.getId());
                LOG.info("oHistoricVariableInstance.getVariableName {}", oHistoricVariableInstance.getVariableName());
                LOG.info("oHistoricVariableInstance.getVariableTypeName {}", oHistoricVariableInstance.getVariableTypeName());
            }

            HistoricVariableInstance oHistoricVariableInstance =
                                historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableNameLike("%bOrder=true%").singleResult();

            if(oHistoricVariableInstance != null){
                LOG.info("oHistoricVariableInstance.getVariableName with like {}", oHistoricVariableInstance.getVariableName());
            }
            
            if (oHistoricTaskInstance.getProcessDefinitionId().startsWith("_doc_")) {
                LOG.info("We catch document submit (ECP)");
                JSONArray properties = (JSONArray) omRequestBody.get("properties");
                
                Iterator<JSONObject> iterator = properties.iterator();
                String sKey_Step_Document = null;
                while (iterator.hasNext()) {
                    JSONObject jsonObject = iterator.next();

                    String sId = (String) jsonObject.get("id");
                    String sValue = (String) jsonObject.get("value");
                    
                    if (sId.equals("sKey_Step_Document")) {
                        sKey_Step_Document = sValue;
                        break;
                    }
                }

                LOG.info("sKey_Step_Document is {}", sKey_Step_Document);

                if (sKey_Step_Document != null) {
                    List<DocumentStep> aDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", processInstanceId);
                    LOG.info("aDocumentStep in interceptor is {}", aDocumentStep);

                    DocumentStep oCurrDocumentStep = null;

                    for (DocumentStep oDocumentStep : aDocumentStep) {

                        if (oDocumentStep.getsKey_Step().equals(sKey_Step_Document)) {
                            oCurrDocumentStep = oDocumentStep;
                            break;
                        }
                    }

                    LOG.info("oCurrDocumentStep in interceptor is {}", oCurrDocumentStep);

                    String sAssignLogin = oHistoricTaskInstance.getAssignee();
                    LOG.info("sAssignLogin in interceptor is {}", sAssignLogin);
                    List<Group> aUserGroup = identityService.createGroupQuery().groupMember(sAssignLogin).list();

                    LOG.info("aUserGroup is {}", aUserGroup);

                    //runtimeService.setVariable(executionId, "sLogin_LastSubmited", sAssignLogin);
                    if (oCurrDocumentStep != null) {
                        List<DocumentStepSubjectRight> aDocumentStepSubjectRight = oCurrDocumentStep.aDocumentStepSubjectRight();
                        for (DocumentStepSubjectRight oDocumentStepSubjectRight : aDocumentStepSubjectRight) {
                            for (Group oGroup : aUserGroup) {
                                LOG.info("oGroup name: {}", oGroup.getName());
                                LOG.info("oGroup id: {}", oGroup.getId());
                                if (oGroup.getId().equals(oDocumentStepSubjectRight.getsKey_GroupPostfix())) {
                                    List<User> aUser = identityService.createUserQuery().memberOfGroup(oDocumentStepSubjectRight.getsKey_GroupPostfix()).list();
                                    LOG.info("oDocumentStepSubjectRight.getsKey_GroupPostfix {}", oDocumentStepSubjectRight.getsKey_GroupPostfix());
                                    for (User oUser : aUser) {
                                        LOG.info("oUser id is {}", oUser.getId());
                                        if (oUser.getId().equals(sAssignLogin)) {
                                            LOG.info("We set date for login: {}", sAssignLogin);
                                            oDocumentStepSubjectRight.setsDate(new DateTime());
                                            oDocumentStepSubjectRight.setsLogin(sAssignLogin);
                                            oDocumentStepSubjectRightDao.saveOrUpdate(oDocumentStepSubjectRight);
                                            runtimeService.setVariable(processInstanceId, "sLogin_LastSubmited", sAssignLogin);
                                            taskService.setVariable(sTaskId, "sLogin_LastSubmited", sAssignLogin);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        LOG.info("oCurrDocumentStep.getRights() in interceptor is {}", oCurrDocumentStep.aDocumentStepSubjectRight());
                        List<Task> aTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
                        boolean bProcessClosed = aTask == null || aTask.size() == 0;
                        String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                        Map<String, String> mParam = new HashMap<>();
                        String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(processInstanceId));
                        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                        mParam.put("sLogin", sAssignLogin);
                        if (!aDocumentStep.get(1).getsKey_Step().equals(oCurrDocumentStep.getsKey_Step())) {
                            oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 13L);
                        } else {
                            oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 12L);
                        }
                    }
                    /*else{
                        List<Task> aTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
                        boolean bProcessClosed = aTask == null || aTask.size() == 0;
                        String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                        Map<String, String> mParam = new HashMap<>();
                        String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(processInstanceId));
                        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                        mParam.put("sLogin", sAssignLogin);
                        oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 12L);
                    }*/

                    List<DocumentStep> aNewDocumentStep = documentStepDao.findAllBy("snID_Process_Activiti", processInstanceId);
                    LOG.info("aDocumentStep new in interceptor is {}", aNewDocumentStep);

                    for (DocumentStep oNewCurrDocumentStep : aNewDocumentStep) {
                        LOG.info("aDocumentStep new rights  in interceptor is {}", oNewCurrDocumentStep.aDocumentStepSubjectRight());
                    }

                }
                /*else{
                        List<Task> aTask = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
                        boolean bProcessClosed = aTask == null || aTask.size() == 0;
                        String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();

                        Map<String, String> mParam = new HashMap<>();
                        String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(processInstanceId));
                        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                        String sAssignLogin = oHistoricTaskInstance.getAssignee();
                        mParam.put("sLogin", sAssignLogin);
                        oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 12L);
                    }*/
            }
        }
    }

    private void protocolize(HttpServletRequest oRequest, HttpServletResponse oResponse, boolean bSaveHistory)
            throws IOException, TaskAlreadyUnboundException {
        LOG.info("Method 'protocolize' started");
        int nLen = generalConfig.isSelfTest() ? 300 : 200;

        Map<String, String> mRequestParam = new HashMap<>();
        Enumeration<String> paramsName = oRequest.getParameterNames();
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
        }
        String sURL = oRequest.getRequestURL().toString();
        LOG.info("protocolize sURL is: " + sURL);
        String snTaskId = null;
        //getting task id from URL, if URL matches runtime/tasks/{taskId} (#1234)
        String sRequestBody = osRequestBody.toString();
        LOG.info("oRequest.getRequestURL(): " + oRequest.getRequestURL() + " oRequest.getMethod(): " + oRequest.getMethod());
        if (TAG_PATTERN_PREFIX.matcher(oRequest.getRequestURL()).find()) {
            snTaskId = sURL.substring(sURL.lastIndexOf("/") + 1);
            LOG.info("snTaskId: " + snTaskId);
            LOG.info("Request.getMethod().trim(): " + oRequest.getMethod().trim());
            if (PUT.equalsIgnoreCase(oRequest.getMethod().trim()) && SREQUESTBODY_PATTERN.matcher(sRequestBody).find()) {
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
            //LOG.info("(mRequestParam={})", mRequestParam);
            //LOG.info("(sRequestBody={})", sCut(nLen, sRequestBody));
            if (sURL.endsWith("/service/document/setDocumentFile")
                    || sURL.contains("/service/object/file/")) {
            } else {
                LOG_BIG.debug("(sRequestBody={})", sRequestBody);
            }
        }

        String sResponseBody = !bFinish ? null : oResponse.toString();
        if (bFinish) {
            //LOG.info("(sResponseBody={})", sCut(nLen, sResponseBody));
            if (sURL.endsWith(SERVICE_ACTION_ITEM_GET_SERVICE)
                    || sURL.endsWith(SERVICE_ACTION_ITEM_GET_SERVICES_TREE)
                    || (sURL.endsWith(SERVICE_FORM_FORM_DATA)
                    && "GET".equalsIgnoreCase(oRequest.getMethod().trim()))
                    || sURL.endsWith(SERVICE_REPOSITORY_PROCESS_DEFINITIONS)
                    || sURL.endsWith(SERVICE_ACTION_TASK_GET_START_FORM_DATA)
                    || sURL.endsWith(SERVICE_ACTION_TASK_GET_ORDER_MESSAGES_LOCAL)
                    || sURL.endsWith(SERVICE_ACTION_FLOW_GET_FLOW_SLOTS_SERVICE_DATA)
                    || sURL.contains(SERVICE_RUNTIME_TASKS)
                    || sURL.endsWith(SERVICE_HISTORY_HISTORIC_TASK_INSTANCES)
                    || sURL.endsWith(SERVICE_ACTION_TASK_GET_LOGIN_B_PS)
                    || sURL.endsWith(SERVICE_SUBJECT_MESSAGE_GET_MESSAGES)
                    || sURL.endsWith(SERVICE_SUBJECT_MESSAGE_GET_SERVICE_MESSAGES)
                    || sURL.endsWith(SERVICE_OBJECT_PLACE_GET_PLACES_TREE)
                    || sURL.endsWith(SERVICE_ACTION_EVENT_GET_LAST_TASK_HISTORY)
                    || sURL.endsWith(SERVICE_ACTION_EVENT_GET_HISTORY_EVENTS_SERVICE)
                    || sURL.endsWith(SERVICE_ACTION_EVENT_GET_HISTORY_EVENTS)
                    || sURL.endsWith(SERVICE_DOCUMENT_GET_DOCUMENT_CONTENT)
                    || sURL.endsWith(SERVICE_DOCUMENT_GET_DOCUMENT_FILE)
                    || sURL.endsWith(SERVICE_DOCUMENT_GET_DOCUMENTS)
                    || sURL.endsWith(SERVICE_DOCUMENT_SET_DOCUMENT_FILE)
                    || sURL.contains(SERVICE_OBJECT_FILE)
                    || sURL.contains(SERVICE_DOCUMENT_GET_DOCUMENT_ABSTRACT)) {
            } else {
                //LOG_BIG.debug("(sResponseBody={})", sResponseBody);
            }
        }
        String sType = "";
        
        try {
            LOG.info("URL: {} method: {}", oRequest.getRequestURL(), oRequest.getMethod());
            if (!bSaveHistory || !(oResponse.getStatus() >= HttpStatus.OK.value()
                    && oResponse.getStatus() < HttpStatus.BAD_REQUEST.value())) {
                LOG.info("returning from protocolize block: bSaveHistory:{} oResponse.getStatus():{}", bSaveHistory, oResponse.getStatus());
            }

            //LOG.info("isSaveTask(oRequest, sResponseBody): " + isSaveTask(oRequest, sResponseBody));
            //LOG.info("oRequest.getRequestURL: " + oRequest.getRequestURL().toString());
            //LOG.info("sResponseBody before SaveTask: " + sResponseBody);

            if (isSaveTask(oRequest, sResponseBody)) {
                sType = "Save";
                LOG.info("saveNewTaskInfo block started");
                if (oResponse.getStatus() < 200 || oResponse.getStatus() >= 300
                        || (sResponseBody != null && sResponseBody.contains(SYSTEM_ERR))) { //SYSTEM_ERR
                    try {
                        new Log(this.getClass(), LOG)//this.getClass()
                                ._Case("Activiti_FailStartTask")
                                ._Status(Log.LogStatus.ERROR)
                                ._Head("Error hapened while start process!")
                                ._Body(oResponse.toString())
                                //._Param("sRequestBody", sRequestBody)
                                //._Param("sResponseBody", sResponseBody)
                                ._Param("mRequestParam", mRequestParam)
                                .save();
                    } catch (Exception ex) {
                        LOG.error("Can't save error to MSG", ex.getMessage());
                    }
                    return;
                } else {
                    //LOG.info("sRequestBody {}", sRequestBody);
                    //LOG.info("sResponseBody {}", sRequestBody);
                    //LOG.info("mRequestParam {}", sRequestBody);
                    saveNewTaskInfo(sRequestBody, sResponseBody, mRequestParam);
                }
                //{nID_Service=25, nID_Subject=255289, nID_ServiceData=542, sID_BP=dms_0025_ID2 545_iGov:1:1, sID_UA=1210100000}
                LOG.info("saveNewTaskInfo block finished");
            } else if (isCloseTask(oRequest, sResponseBody)) {
                LOG.info("saveClosedTaskInfo block started");
                List<String> aTaskId = new ArrayList<>();
                
                if(oRequest.getRequestURL().toString().indexOf(SERVICE_CANCELTASK) > 0){
                    LOG.info("We catch cancel task...");
                    //LOG.info("mRequestParam {}", mRequestParam);
                    String nID_Order = mRequestParam.get("nID_Order");
                    LOG.info("nID_Order {}", nID_Order);
                    
                    aTaskId = actionTaskService.getTaskIdsByProcessInstanceId(
                            actionTaskService.getOriginalProcessInstanceId(Long.parseLong(nID_Order)));
                    
                    List<Task> aTask = taskService.createTaskQuery().processInstanceId(
                            actionTaskService.getOriginalProcessInstanceId(Long.parseLong(nID_Order))).active().list();
                    boolean bProcessClosed = aTask == null || aTask.size() == 0;
                    String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();
                    LOG.info("sUserTaskName in close event is {}", sUserTaskName);
                    //for(String taskId : aTaskId){
                    LOG.info("taskId {}", aTaskId.get(aTaskId.size() - 1));
                    Map<String, String> mParam = new HashMap<>();
                    String sID_Order = generalConfig.getOrderId_ByOrder(generalConfig.getSelfServerId(), Long.parseLong(nID_Order));
                    LOG.info("sID_Order for cancel flowslot {}", sID_Order);
                    mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                    closeTaskEvent.doWorkOnCloseTaskEvent(bSaveHistory,  aTaskId.get(aTaskId.size() - 1), null, true);
                    //oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 19L);
                }
                sType = "Close";
                if(aTaskId.isEmpty()){
                    saveClosedTaskInfo(sRequestBody, snTaskId, bSaveHistory);
                }
                LOG.info("saveClosedTaskInfo block finished");
            } else if (isUpdateTask(oRequest)) {
                sType = "Update";
                LOG.info("saveUpdatedTaskInfo block started");
                //LOG.info("oRequest URL: {}", oRequest.getRequestURL().toString());
                //LOG.info("oRequest mRequestParam: {}", mRequestParam);
                saveUpdatedTaskInfo(sResponseBody, mRequestParam);
                LOG.info("saveUpdatedTaskInfo block finished");
            }
        } catch (Exception oException) {
            LOG_BIG.error("Can't save service-history record: {}", oException.getMessage());
            //LOG_BIG.error("FAIL:", oException);
            /*try {
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
                LOG.error("Can't send an error message to service MSG\n", e);
            }*/

        }
        LOG.info("Method 'protocolize' finished");
    }

    /**
     * сохранение информации таска
     *
     * @param sRequestBody
     * @param sResponseBody
     * @param mParamRequest
     * @throws Exception
     */
    private void saveNewTaskInfo(String sRequestBody, String sResponseBody, Map<String, String> mParamRequest)
            throws Exception {
        
        LOG.info("saveNewTaskInfo started in " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        
        //LOG.info("sRequestBody {}", sRequestBody);// 
        //LOG.info("sResponseBody {}", sResponseBody);
        //LOG.info("mParamRequest {}", mParamRequest);
        
        if (sResponseBody == null) {
            //LOG.warn("sResponseBody=null!!! (sRequestBody={},mParamRequest={})", sRequestBody, mParamRequest);
        }
        Map<String, String> mParam = new HashMap<>();
        //LOG.info("sRequestBody {}", sRequestBody);
        JSONObject omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);
        
       // LOG.info("omRequestBody >>>>>>>>>>>>>> {}", omRequestBody );
        
        JSONArray properties = (JSONArray) omRequestBody.get("properties");
      //  LOG.info("properties >>>>>>>>>>>>>> {}", properties );
        
        if(properties == null){
            properties = (JSONArray) omRequestBody.get("aFormProperty");
        }
        
        Iterator<JSONObject> iterator = properties.iterator();
        String sID_Public_SubjectOrganJoin = null;
        
        while (iterator.hasNext()) {
            JSONObject jsonObject = iterator.next();

            if(jsonObject.get("value")instanceof java.lang.String) {
	            String sId = (String) jsonObject.get("id");
	            String sValue = (String) jsonObject.get("value");
	
	            if (sId.equals("sID_Public_SubjectOrganJoin")) {
	            	sID_Public_SubjectOrganJoin = sValue;
	            	 break;
	            }
            }
        }
        LOG.info("RequestProcessingInterceptor sID_Public_SubjectOrganJoin: " + sID_Public_SubjectOrganJoin);
        mParam.put("sID_Public_SubjectOrganJoin", sID_Public_SubjectOrganJoin);
        
        
        JSONObject omResponseBody = (JSONObject) oJSONParser.parse(sResponseBody);
        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());

        //String osnID_Process = omResponseBody.containsKey("id"); //разобраться чего получаем нал в некоторых случаях
        String snID_Process = String.valueOf(omResponseBody.containsKey("id")?omResponseBody.get("id"):omResponseBody.get("snID_Process")); //разобраться чего получаем нал в некоторых случаях
        //if(snID_Process) //{"snID_Process":"23285433","nID_Task":"23285483"}
        if(sRequestBody != null && sRequestBody.contains("sCancelInfo")){
            runtimeService.setVariable(snID_Process, "sCancelInfo", String.format("Заявка актуальна"));
        }
        
        if (snID_Process != null && !"null".equalsIgnoreCase(snID_Process)) {
            Long nID_Process = Long.valueOf(snID_Process);
            LOG.info("snID_Process please be here: " + snID_Process);
            String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
            //String snID_Subject = String.valueOf(omRequestBody.get("nID_Subject"));
            String snID_Subject = String.valueOf(omRequestBody.containsKey("nID_Subject")?omRequestBody.get("nID_Subject"):mParamRequest.get("nID_Subject"));
            mParam.put("nID_Subject", snID_Subject);

            LOG.info("(sID_Order={},nID_Subject={})", sID_Order, snID_Subject);

            String snID_Service = mParamRequest.get("nID_Service");
            LOG.info("nID_Service in RequestProcessingInterceptor: " + snID_Service);
            if (snID_Service != null) {
                mParam.put("nID_Service", snID_Service);
            }

            String sID_UA = mParamRequest.get("sID_UA");
            if (sID_UA != null) {
                mParam.put("sID_UA", sID_UA);
            }
            
            LOG.info("RequestProcessingInterceptor sID_UA: " + sID_UA);
                      
            //TODO: need remove in future
            String snID_Region = mParamRequest.get("nID_Region");
            if (snID_Region != null) {
                LOG.info("nID_Region in saveNewTaskInfo is {}", snID_Region);
                mParam.put("nID_Region", snID_Region);
            }

            LOG.info("RequestProcessingInterceptor snID_Region: " + snID_Region);

            String snID_ServiceData = mParamRequest.get("nID_ServiceData");
            if (snID_ServiceData != null) {
                mParam.put("nID_ServiceData", snID_ServiceData);
            }

            LOG.info("RequestProcessingInterceptor snID_ServiceData: " + snID_ServiceData);

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

            sendMailTo(omRequestBody, sID_Order, snID_Subject, snID_Service, oProcessDefinition);

            historyEventService.addHistoryEvent(sID_Order, sUserTaskName, mParam);
            //LOG.info("Before calling set action process count {}, {}", mParam, oProcessDefinition.getKey());
            if (oProcessDefinition.getKey().startsWith("_doc_") || DNEPR_MVK_291_COMMON_BP.contains(oProcessDefinition.getKey())) {
                //Integer count = ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, oProcessDefinition.getKey(), Long.valueOf(snID_Service));
                //LOG.info("RequestProcessInterceptor process count: " + count.intValue());
            }//2017-05-16_13:56:48.390 
        }
    }

    /**
     * сохранение информации при закрытии таски
     *
     * @param sRequestBody
     * @param snClosedTaskId
     * @param bSaveHistory
     * @throws Exception
     */
    //(#1234) added additional parameter snClosedTaskId
    private void saveClosedTaskInfo(String sRequestBody, String snClosedTaskId, boolean bSaveHistory) throws Exception {
        LOG.info("Method saveClosedTaskInfo started");
        
        //LOG.info("sRequestBody is {}", sRequestBody);
        LOG.info("snClosedTaskId is {}", snClosedTaskId);
        
        JSONObject omRequestBody = null;
        String snID_Task = null;
        try{
            omRequestBody = (JSONObject) oJSONParser.parse(sRequestBody);
            snID_Task = (String) omRequestBody.get("taskId");
        }
        catch(Exception ex){
            LOG.info("sRequestBody in saveClosedTaskInfo is unparsable {}", ex);
        }
        
        if ((snID_Task == null) && (snClosedTaskId != null)) {
                snID_Task = snClosedTaskId.trim();
                LOG.info("Task id from requestbody is null, so using task id from url - " + snID_Task);
        }
        
        LOG.info("Task id is - " + snID_Task);
        if (snID_Task != null) {
            closeTaskEvent.doWorkOnCloseTaskEvent(bSaveHistory, snID_Task, omRequestBody, false);
        }
        LOG.info("Method saveClosedTaskInfo END");
    }

    /**
     * сохранение информации при обновлении таски
     *
     * @param sResponseBody
     * @param mRequestParam
     * @throws Exception
     */
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
        updateEscalationState(oHistoricTaskInstance, nID_Process);
    }

    /**
     *
     * @param omRequestBody
     * @param sID_Order
     * @param snID_Subject
     * @param snID_Service
     * @param oProcessDefinition
     * @throws ParseException
     * @throws EmailException
     */
    public void sendMailTo(JSONObject omRequestBody, String sID_Order, String snID_Subject, String snID_Service,
            ProcessDefinition oProcessDefinition) throws ParseException, EmailException {
        String sMailTo = JsonRequestDataResolver.getEmail(omRequestBody);
        String sPhone = String.valueOf(JsonRequestDataResolver.getPhone(omRequestBody));
        String bankIdFirstName = JsonRequestDataResolver.getBankIdFirstName(omRequestBody);
        String bankIdLastName = JsonRequestDataResolver.getBankIdLastName(omRequestBody);
        
        
        int nID_Server = generalConfig.getSelfServerId();  
        LOG.info("nID_Server in sendMailTo in interceptor is ", nID_Server );
       
        if (sMailTo != null) {
        	 if (Arrays.asList(asID_BP_SendMail).contains(oProcessDefinition.getKey())) {
                ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, oProcessDefinition.getKey(), Long.valueOf(snID_Service));
                //LOG.info("Before send notification mail... (sMailTo={}, oProcessDefinition.getKey()={})", sMailTo, oProcessDefinition.getKey());
                oNotificationPatterns.sendTaskCreatedInfoEmail(sMailTo, sID_Order, bankIdFirstName, bankIdLastName);
                //LOG.info("Send notification mail... (sMailTo={}, oProcessDefinition.getKey()={})", sMailTo, oProcessDefinition.getKey());
            } else {
                //LOG.info("SKIP send notification mail... (sMailTo={}, oProcessDefinition.getKey()={})", sMailTo, oProcessDefinition.getKey());
            }
        }

        if (sMailTo != null || sPhone != null) {
            try {
                Map<String, String> mParamSync = new HashMap<String, String>();
                mParamSync.put("snID_Subject", snID_Subject);
                mParamSync.put("sMailTo", sMailTo);
                mParamSync.put("sPhone", sPhone);
                //LOG.info("Вносим параметры в коллекцию (sMailTo {}, snID_Subject {}, sPhone {})", sMailTo, snID_Subject,
                //        sPhone);
                String sURL = generalConfig.getSelfHostCentral() + URI_SYNC_CONTACTS;
                LOG.info("Подключаемся к центральному порталу by sURL: " + sURL);
                String sResponse = httpRequester.getInside(sURL, mParamSync);
                //LOG.info("Подключение осуществлено.. sResponse is: " + sResponse);
            } catch (Exception ex) {
                LOG.warn("(isSaveTask exception {})", ex.getMessage());
            }

        }
    }

    /**
     * Обновление статуса ескалации
     *
     * @param oHistoricTaskInstance
     * @param nID_Process
     */
    public void updateEscalationState(HistoricTaskInstance oHistoricTaskInstance, Long nID_Process) {
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

    private boolean isUpdateTask(HttpServletRequest oRequest) {
        return (oRequest.getRequestURL().toString().indexOf(RUNTIME_TASKS) > 0
                && PUT.equalsIgnoreCase(oRequest.getMethod().trim()))
                || oRequest.getRequestURL().toString().indexOf("action/task/updateProcess") > 0
                && POST.equalsIgnoreCase(oRequest.getMethod().trim());
    }
    
    private boolean isCloseTask(HttpServletRequest oRequest, String sResponseBody) {
        return POST.equalsIgnoreCase(oRequest.getMethod().trim())
                && (((sResponseBody == null || "".equals(sResponseBody))
                && oRequest.getRequestURL().toString().indexOf(FORM_FORM_DATA) > 0)
                || TAG_PATTERN_PREFIX.matcher(oRequest.getRequestURL()).find()
                || (oRequest.getRequestURL().toString().indexOf(SERVICE_CANCELTASK) > 0));
    }

    private boolean isSaveTask(HttpServletRequest oRequest, String sResponseBody) {
        return (bFinish && sResponseBody != null && !"".equals(sResponseBody))
                //&& oRequest.getRequestURL().toString().indexOf(FORM_FORM_DATA) > 0
                && (oRequest.getRequestURL().toString().indexOf(FORM_FORM_DATA) > 0
                ||
                oRequest.getRequestURL().toString().indexOf(START_PROCESS) > 0
                )
                && POST.equalsIgnoreCase(oRequest.getMethod().trim());
    }

    private boolean isDocumentSubmit(HttpServletRequest oRequest) {
        return (oRequest != null && oRequest.getRequestURL().toString().indexOf(FORM_FORM_DATA) > 0
                && POST.equalsIgnoreCase(oRequest.getMethod().trim()));
    }

    private boolean isUpdateProcess(HttpServletRequest oRequest) {
        return (oRequest != null && oRequest.getRequestURL().toString().indexOf("task/updateProcess") > 0
                && POST.equalsIgnoreCase(oRequest.getMethod().trim()));
    }

    private boolean isSetDocumentService(HttpServletRequest oRequest, String sResponseBody) {
        boolean isNewDocument = (bFinish && sResponseBody != null && !"".equals(sResponseBody))
                && oRequest.getRequestURL().toString().indexOf(DOCUMENT_SERVICE) > 0
                && GET.equalsIgnoreCase(oRequest.getMethod().trim());

        if (isNewDocument) {
            LOG.info("We catch document in requestProcessingInterceptor! Yippie-Kai-Yay!");
        }

        return isNewDocument;
    }

    protected void closeEscalationProcessIfExists(String sID_Process) {
        closeTaskEvent.closeEscalationProcessIfExists(sID_Process);
    }
    
    private boolean isSetProcessSubjectStatus(HttpServletRequest oRequest) {

        return (oRequest != null && oRequest.getRequestURL().toString().indexOf(SERVICE_SUBJECT_PROCESS_SET_PROCESS_SUBJECT_STATUS) > 0
                && GET.equalsIgnoreCase(oRequest.getMethod().trim()));
    }
    
    private void processSubjectStatusHistoryWritingPreHandle(HttpServletRequest oRequest) throws Exception {
        
        try {
            
            if (isSetProcessSubjectStatus(oRequest)) {

                Map<String, String> mRequestParam = new HashMap<>();
                Enumeration<String> paramsName = oRequest.getParameterNames();

                while (paramsName.hasMoreElements()) {
                    String sKey = (String) paramsName.nextElement();
                    mRequestParam.put(sKey, oRequest.getParameter(sKey));
                }

                String snID_Task_Activiti = mRequestParam.get("snID_Task_Activiti");
                String sLoginController = mRequestParam.get("sLoginController");
                String sLoginExecutor = mRequestParam.get("sLoginExecutor");
                String sID_ProcessSubjectStatus = mRequestParam.get("sID_ProcessSubjectStatus");

                if (sLoginController != null || sLoginExecutor != null) {

                    /**
                    *Определяем кто вызвал сервис (исполнитель или контролирующий). Пришел только
                    * логин sLoginExecutor - исполнитель, пришел только логин sLoginController - контролирующий,
                    * если пришло два логина - контролирующий.
                    */
                     String sLoginMain = sLoginController;

                    if (sLoginExecutor != null && sLoginController == null) {          
                        sLoginMain = sLoginExecutor;
                    } 

                    String snID_Process_Activiti = actionTaskService.getProcessInstanceIDByTaskID(snID_Task_Activiti);

                    ProcessSubject oProcessSubjectMain = oProcessSubjectDao.findByProcessActivitiIdAndLogin(snID_Process_Activiti, sLoginMain);

                    String sLoginRoleMain = oProcessSubjectMain.getsLoginRole();

                    if (sLoginRoleMain.equals("Executor") || sLoginRoleMain.equals("Controller")) {

                        HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(snID_Task_Activiti).singleResult();

                        String sProcessInstanceId = oHistoricTaskInstance.getProcessInstanceId();

                        String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(sProcessInstanceId));

                        SubjectGroup oSubjectGroup = oSubjectGroupDao.findByExpected("sID_Group_Activiti", sLoginMain);
                        String sName = oSubjectGroup.getoSubject().getsLabel();

                        List<Task> aTask = taskService.createTaskQuery().processInstanceId(sProcessInstanceId).active().list();

                        boolean bProcessClosed = aTask == null || aTask.isEmpty();

                        //проверка, чтобы выбрать таску по ид, который пришел в запросе
                        String sUserTaskName = bProcessClosed ? "закрита" : aTask.stream().filter(oTask -> oTask.getId().equals(snID_Task_Activiti)).findFirst().toString();

                        JSONObject oTransportObject = new JSONObject();

                        oTransportObject.put("sLoginRole", sLoginRoleMain);
                        oTransportObject.put("sID_Order", sID_Order);
                        oTransportObject.put("sUserTaskName", sUserTaskName);
                        oTransportObject.put("sName", sName);
                        oTransportObject.put("sLogin", sLoginMain);
                        oTransportObject.put("sID_ProcessSubjectStatus", sID_ProcessSubjectStatus);       

                        oRequest.setAttribute("oTransportObject", oTransportObject);

                    }
                }                                   
            }
        } catch (Exception oException) {
            
            LOG.info("Error during document processing in interceptor: {} ", oException.getMessage());
        }
    }
    
    private void processSubjectStatusHistoryWritingPostHandle(HttpServletRequest oRequest) throws Exception {

        try {
            
            if (isSetProcessSubjectStatus(oRequest)) {

                Map<String, Object> mRequestAttribute = new HashMap<>();
                Enumeration<String> aAttributeName = oRequest.getAttributeNames();

                while (aAttributeName.hasMoreElements()) {
                    String sKey = (String) aAttributeName.nextElement();
                    mRequestAttribute.put(sKey, oRequest.getAttribute(sKey));
                }

                JSONObject oTransportObject = (JSONObject) mRequestAttribute.get("oTransportObject");

                String sLoginRole = (String) oTransportObject.get("sLoginRole");
                String sID_Order = (String) oTransportObject.get("sID_Order");
                String sUserTaskName = (String) oTransportObject.get("sUserTaskName");
                String sName = (String) oTransportObject.get("sName");
                String sLogin = (String) oTransportObject.get("sLogin");
                String sID_ProcessSubjectStatus = (String) oTransportObject.get("sID_ProcessSubjectStatus");

                Map<String, String> mParam = new HashMap<>();
                    mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CREATED.getnID().toString());
                    mParam.put("sLoginNew", sLogin);
                    mParam.put("sName", sName);

                if (sID_ProcessSubjectStatus.equals("executed") && sLoginRole.equals("Executor")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 20L);

                } else if (sID_ProcessSubjectStatus.equals("notExecuted") && sLoginRole.equals("Executor")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 21L);

                } else if (sID_ProcessSubjectStatus.equals("unactual") && sLoginRole.equals("Executor")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 22L);

                } else if (sID_ProcessSubjectStatus.equals("requestTransfered") && sLoginRole.equals("Executor")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 23L);

                } else if (sID_ProcessSubjectStatus.equals("transfered") && sLoginRole.equals("Controller")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 24L);

                } else if (sID_ProcessSubjectStatus.equals("rejected") && sLoginRole.equals("Controller")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 25L);

                } else if (sID_ProcessSubjectStatus.equals("executed") && sLoginRole.equals("Controller")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 26L);

                } else if (sID_ProcessSubjectStatus.equals("notExecuted") && sLoginRole.equals("Controller")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 27L);

                } else if (sID_ProcessSubjectStatus.equals("unactual") && sLoginRole.equals("Controller")) {

                    oActionEventHistoryService.addHistoryEvent(sID_Order, sUserTaskName, mParam, 28L);                    
                }
            }
        } catch (Exception oException) {
            
            LOG.info("Error during document processing in interceptor: {} ", oException.getMessage());
        }
    }    
}
