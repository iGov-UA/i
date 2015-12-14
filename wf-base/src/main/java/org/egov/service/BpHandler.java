package org.egov.service;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wf.dp.dniprorada.base.model.EscalationHistory;
import org.wf.dp.dniprorada.util.GeneralConfig;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;

import java.util.*;

/**
 * @author OlgaPrylypko
 * @since 2015-12-01.
 */
@Service
public class BpHandler {

    public static final String PROCESS_ESCALATION = "system_escalation";
    private static final Logger LOG = Logger.getLogger(BpHandler.class);
    private static final String PROCESS_FEEDBACK = "system_feedback";
    private static final String ESCALATION_FIELD_NAME = "nID_Proccess_Escalation";
    private static final String BEGIN_GROUPS_PATTERN = "${";
    private static final String END_GROUPS_PATTERN = "}";

    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private EscalationHistoryService escalationHistoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryEventService historyEventService;

    public String startFeedbackProcess(String sID_task, String sID_Process, String processName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nID_Proccess_Feedback", sID_Process);
        variables.put("processName", processName);
        //get process variables
        HistoricTaskInstance details = historyService
                .createHistoricTaskInstanceQuery()
                .includeProcessVariables().taskId(sID_task)
                .singleResult();
        if (details != null && details.getProcessVariables() != null) {
            Map<String, Object> processVariables = details.getProcessVariables();
            variables.put("nID_Protected", "" + AlgorithmLuna.getProtectedNumber(Long.valueOf(sID_Process)));
            variables.put("bankIdfirstName", processVariables.get("bankIdfirstName"));
            variables.put("bankIdmiddleName", processVariables.get("bankIdmiddleName"));
            variables.put("bankIdlastName", processVariables.get("bankIdlastName"));
            variables.put("phone", "" + processVariables.get("phone"));
            variables.put("email", processVariables.get("email"));
            variables.put("organ", getCandidateGroups(processName, sID_task, processVariables));
        }
        LOG.info(String.format(" >> start process [%s] with params: %s", PROCESS_FEEDBACK, variables));
        ProcessInstance feedbackProcess = runtimeService.startProcessInstanceByKey(PROCESS_FEEDBACK, variables);
        return feedbackProcess.getProcessInstanceId();
    }

    public void checkBpAndStartEscalationProcess(final Map<String, Object> mTaskParam) {
        String sID_Process = (String) mTaskParam.get("sProcessInstanceId");
        String processName = (String) mTaskParam.get("sID_BP_full");
        try {
            String jsonHistoryEvent = historyEventService
                    .getHistoryEvent(null, null, Long.valueOf(sID_Process), generalConfig.nID_Server());
            LOG.info("TEST: get history event for bp: " + jsonHistoryEvent);
            JSONObject historyEvent = new JSONObject(jsonHistoryEvent);
            Object escalationId = historyEvent.get(ESCALATION_FIELD_NAME);
            if (!(escalationId == null || "null".equals(escalationId.toString()))) {
                LOG.info(String.format("For bp [%s] escalation process (with id=%s) has already started!",
                        processName, escalationId));
                //return;
            }
        } catch (Exception e) {
            LOG.error("ex!", e);
        }
        String taskName = (String) mTaskParam.get("sTaskName");
        String escalationProcessId = startEscalationProcess(mTaskParam, sID_Process, processName);
        Map<String, String> params = new HashMap<>();
        params.put(ESCALATION_FIELD_NAME, escalationProcessId);
        LOG.info(" >>Start escalation process. nID_Proccess_Escalation=" + escalationProcessId);
        try {
            historyEventService.updateHistoryEvent(sID_Process, taskName, false, params);
            EscalationHistory escalationHistory = escalationHistoryService.create(Long.valueOf(sID_Process),
                    Long.valueOf(mTaskParam.get("sTaskId").toString()),
                    Long.valueOf(escalationProcessId), EscalationHistoryService.STATUS_CREATED);
            LOG.info(" >> save to escalationHistory.. ok! escalationHistory=" + escalationHistory);
        } catch (Exception e) {
            LOG.error("ex!", e);
        }
    }

    private String startEscalationProcess(final Map<String, Object> mTaskParam, final String sID_Process,
            final String processName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("processID", sID_Process);
        variables.put("processName", processName);
        variables.put("nID_Protected", "" + AlgorithmLuna.getProtectedNumber(Long.valueOf(sID_Process)));
        variables.put("bankIdfirstName", mTaskParam.get("bankIdfirstName"));
        variables.put("bankIdmiddleName", mTaskParam.get("bankIdmiddleName"));
        variables.put("bankIdlastName", mTaskParam.get("bankIdlastName"));
        variables.put("phone", "" + mTaskParam.get("phone"));
        variables.put("email", mTaskParam.get("email"));
        variables.put("organ", getCandidateGroups(processName, mTaskParam.get("sTaskId").toString(), null));
        variables.put("saField", new JSONObject(mTaskParam).toString());
        variables.put("data", mTaskParam.get("sDate_BP"));

        LOG.info(String.format(" >> start process [%s] with params: %s", PROCESS_ESCALATION, variables));
        ProcessInstance feedbackProcess = runtimeService.startProcessInstanceByKey(PROCESS_ESCALATION, variables);
        return feedbackProcess.getProcessInstanceId();
    }

    private String getCandidateGroups(final String processName, final String taskId,
            final Map<String, Object> taskVariables) {
        Set<String> candidateCroupsToCheck = new HashSet<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processName);
        for (FlowElement flowElement : bpmnModel.getMainProcess().getFlowElements()) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                List<String> candidateGroups = userTask.getCandidateGroups();
                if (candidateGroups != null && !candidateGroups.isEmpty()) {
                    candidateCroupsToCheck.addAll(candidateGroups);
                }
            }
        }
        String str = candidateCroupsToCheck.toString();
        if (str.contains(BEGIN_GROUPS_PATTERN)) {
            Map<String, Object> processVariables = null;
            if (taskVariables == null) {//get process variables
                HistoricTaskInstance taskDetails = historyService
                        .createHistoricTaskInstanceQuery()
                        .includeProcessVariables().taskId(taskId)
                        .singleResult();
                if (taskDetails != null && taskDetails.getProcessVariables() != null) {
                    processVariables = taskDetails.getProcessVariables();
                }
            } else { //use existing process variables
                processVariables = taskVariables;
            }
            if (processVariables != null) {
                Set<String> newCandidateGroups = new HashSet<>();
                String variable;
                Object value;
                String newCandidateGroup;
                for (String candidateGroup : candidateCroupsToCheck) {
                    if (candidateGroup.contains(BEGIN_GROUPS_PATTERN)) {
                        variable = StringUtils.substringAfter(candidateGroup, BEGIN_GROUPS_PATTERN);
                        variable = StringUtils.substringBeforeLast(variable, END_GROUPS_PATTERN);
                        value = processVariables.get(variable);
                        newCandidateGroup = (value != null)
                                ?
                                candidateGroup
                                        .replace(BEGIN_GROUPS_PATTERN + variable + END_GROUPS_PATTERN, "" + value) :
                                candidateGroup;
                        newCandidateGroups.add(newCandidateGroup);
                        LOG.info(String.format("replace candidateGroups. before: [%s] after: [%s]", candidateGroup,
                                newCandidateGroup));
                    } else {
                        newCandidateGroups.add(candidateGroup);
                    }
                }
                candidateCroupsToCheck = newCandidateGroups;
                str = newCandidateGroups.toString();
            }
        }
        LOG.info("candidateGroups=" + str);
        return candidateCroupsToCheck.size() > 0 ? str.substring(1, str.length() - 1) : "";
    }
}
