/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.model.ItemDefinition;
import org.activiti.bpmn.model.MessageFlow;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.DiagramElement;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.igov.io.GeneralConfig;
import org.igov.io.Log;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.service.business.action.task.bp.handler.BpServiceHandler;
import org.igov.service.business.escalation.EscalationHistoryService;
import org.igov.service.business.feedback.FeedBackService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloseTaskEvent {

    private static final Logger LOG = LoggerFactory.getLogger(CloseTaskEvent.class);
    private static final Logger LOG_BIG = LoggerFactory.getLogger("ControllerBig");
    private static final String URI_COUNT_CLAIM_HISTORY = "/wf/service/action/event/getCountClaimHistory";
    private static final Long SubjectMessageType_CommentEscalation = 11L;
    private static final String URI_SET_SERVICE_MESSAGE = "/wf/service/subject/message/setServiceMessage";

    @Autowired
    private FeedBackService feedBackService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryEventService historyEventService;

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    private EscalationHistoryService escalationHistoryService;

    @Autowired
    protected RuntimeService runtimeService;

    private final JSONParser oJSONParser = new JSONParser();

    public void doWorkOnCloseTaskEvent(boolean bSaveHistory, String snID_Task, JSONObject omRequestBody, boolean bCloseAnyWay)
            throws ParseException {
        LOG.info("Method doWorkOnCloseTaskEvent started");

        Map<String, String> mParam = new HashMap<>();

        mParam.put("nID_StatusType", HistoryEvent_Service_StatusType.CLOSED.getnID().toString());

        HistoricTaskInstance oHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(snID_Task).singleResult();

        String snID_Process = oHistoricTaskInstance.getProcessInstanceId();
        LOG.info(String.format("snID_Process is [%s]", snID_Process));
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
                        LOG.info(String.format("nID_ProcessV [%s]", nID_ProcessV));
                        sID_Order = generalConfig.getOrderId_ByProcess(nID_ProcessV);
                        LOG.info(String.format("sID_Order [%s]", sID_Order));
                    }
                }
            }
            //--------------
            String snMinutesDurationProcess = getTotalTimeOfExecution(snID_Process);
            mParam.put("nTimeMinutes", snMinutesDurationProcess);
            LOG.info("(sID_Order={},nMinutesDurationProcess={})", sID_Order, snMinutesDurationProcess);
            List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
            String sProcessName = oHistoricTaskInstance.getProcessDefinitionId();
            String oTaskDefinitionKey = oHistoricTaskInstance.getTaskDefinitionKey();
            LOG.info("oTaskDefinitionKey {}", oTaskDefinitionKey);

            Map<String, DiagramElement> mBpSchema = repositoryService.getProcessDiagramLayout(sProcessName).getElements();

            for (String key : mBpSchema.keySet()) {
                DiagramElement oDiagramElement = mBpSchema.get(key);
                LOG.info("DiagramElement {}", oDiagramElement.getId());
                LOG.info("BpSchema key {}", oDiagramElement.getId());
            }

            List<HistoricProcessInstance> aHistoricProcessInstance
                    = historyService.createHistoricProcessInstanceQuery().processInstanceId(snID_Process).finished().list();

            for (HistoricProcessInstance oHistoricProcessInstance : aHistoricProcessInstance) {
                LOG.info("oHistoricProcessInstance.getId {}", oHistoricProcessInstance.getId());
                LOG.info("oHistoricProcessInstance.getProcessDefinitionId {}", oHistoricProcessInstance.getProcessDefinitionId());
            }

            boolean bProcessClosed = (aHistoricProcessInstance != null && !aHistoricProcessInstance.isEmpty());

            String sUserTaskName = bProcessClosed ? "закрита" : aTask.get(0).getName();
            LOG.info("11111sUserTaskName: " + sUserTaskName);

            LOG.info("sProcessName: " + sProcessName);
            try {
                if (bProcessClosed && sProcessName.indexOf("system") != 0) {//issue 962
                    LOG_BIG.debug(String.format("start process feedback for process with snID_Process=%s", snID_Process));
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
                        LOG.info(String.format("countClaimmmmmmmmmmmmmmmm [%s]", countClaim));
                        if (countClaim.compareTo(50L) > 0) {
                            String snID_Proccess_Feedback = feedBackService.runFeedBack(snID_Task);
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
                if (bProcessClosed || bCloseAnyWay) {

                    status = HistoryEvent_Service_StatusType.CLOSED;
                    LOG.info("HistoryEvent_Service_StatusType is CLOSED ", status.toString());

                    if (bCloseAnyWay) {
                        mParam.put("soData", "TaskCancelByUser");
                    }

                } else {
                    status = HistoryEvent_Service_StatusType.OPENED;
                    LOG.info("HistoryEvent_Service_StatusType is OPENED ", status.toString());
                }
                LOG.info("Saving closed task", status);
                mParam.put("sUserTaskName", sUserTaskName);
                try {
                    if (!(sProcessName.contains(BpServiceHandler.PROCESS_ESCALATION) && status == HistoryEvent_Service_StatusType.CLOSED)) {

                        LOG.info("mParam in CloseTaskEvent is {}", mParam);
                        LOG.info("status in CloseTaskEvent is {}", status);
                        historyEventService.updateHistoryEvent(sID_Order, status, mParam);

                        LOG.info(" historyEventService.updateHistoryEvent", sID_Order, status);
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
                                ._Head("Can't save status for escalation")._Param("nID_Process", nID_Process).save();
                    }
                    try {

                        if (omRequestBody != null) {
                            saveCommentSystemEscalation(sID_Order, omRequestBody, oHistoricTaskInstance);
                        }
                    } catch (Exception oException) {
                        new Log(oException, LOG)._Case("IC_SaveCommentVolunteer")._Status(Log.LogStatus.ERROR)
                                ._Head("Can't save volunteer's comment")._Param("nID_Process", nID_Process).save();
                    }
                }
            }
            LOG.info("Method doWorkOnCloseTaskEvent finished");
        }

    }

    public void closeEscalationProcessIfExists(String sID_Process) {
        LOG.info("Looking for escalation processes for process {}", sID_Process);
        List<ProcessInstance> escalationProceses = runtimeService.createProcessInstanceQuery().active()
                .variableValueEquals("processID", sID_Process).list();

        if (escalationProceses != null && escalationProceses.size() > 0) {
            LOG.info("Found {} escalation processes", escalationProceses.size());

            Map<String, String> mParam = new HashMap<>();
            mParam.put("nID_Process_Escalation", "-1");
            LOG.info(" >>Clearing nID_Process_Escalation field for the process . (sID_Process={})", sID_Process);
            try {
                LOG.info(" updateHistoryEvent: " + sID_Process + " mParam: " + mParam);
                historyEventService.updateHistoryEvent(generalConfig.getOrderId_ByProcess(Long.valueOf(sID_Process)),
                        null, false, HistoryEvent_Service_StatusType.UNKNOWN, mParam);
            } catch (Exception oException) {

                new Log(oException, LOG)// this.getClass()
                        ._Case("IC_CloseEscalation")._Status(Log.LogStatus.ERROR)
                        ._Head("Can't close escalation for task")._Param("sID_Process", sID_Process)
                        ._Param("mParam", mParam)._LogTrace().save();
            }

            for (ProcessInstance process : escalationProceses) {
                LOG.info("Removing process with ID:Key {}:{} ", process.getProcessInstanceId(),
                        process.getProcessDefinitionKey());
                runtimeService.deleteProcessInstance(process.getProcessInstanceId(),
                        "State of initial process has been changed. Removing escalaton process");
            }
        }
        LOG.info(" not found escalations for process {}", sID_Process);
    }

    // ============================
    public String getTotalTimeOfExecution(String sID_Process) {
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

            new Log(oException, LOG)// this.getClass()
                    ._Case("IC_TimeExecution")._Status(Log.LogStatus.ERROR)._Head("Can't close escalation for task")
                    ._Param("sID_Process", sID_Process)._LogTrace().save();
        }
        return sReturn;
    }

    /*
	 * Сохранение комментария эскалации. Как определяется что это комментарий
	 * эскалации:
	 * 
	 * В historic-task-instances для этой заявки есть значение вида:
	 * "processDefinitionId":"system_escalation:16:23595004" здесь ключевое
	 * слово system_escalation
	 * 
	 * Тело запроса имеет вид: { "taskId": "23737517", "properties": [ { "id":
	 * "comment", // В теле запроса присутствует комментарий "value":
	 * "zaqxsw2222" }, { "id": "nCountDays", "value": "1" } ] }
     */
    public void saveCommentSystemEscalation(String sID_Order, JSONObject omRequestBody,
            HistoricTaskInstance oHistoricTaskInstance) {
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
            sProcessDefinitionId = oHistoricTaskInstance.getProcessDefinitionId(); // строка
            // вида:
            // system_escalation:16:23595004
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
}
