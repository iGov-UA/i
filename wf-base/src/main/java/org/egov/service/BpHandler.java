package org.egov.service;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;

import java.util.*;

@Service
public class BpHandler {

    private static final Logger LOG = Logger.getLogger(BpHandler.class);

    private static final String PROCESS_FEEDBACK = "system_feedback";
    private static final String PROCESS_ESCALATION = "system_escalation";

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryEventService historyEventService;

    public String startFeedbackProcess(String task_ID, String sID_Process, String processName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nID_Proccess_Feedback", sID_Process);
        variables.put("processName", processName);
        //get process variables
        HistoricTaskInstance details = historyService
                .createHistoricTaskInstanceQuery()
                .includeProcessVariables().taskId(task_ID)
                .singleResult();
        if (details != null && details.getProcessVariables() != null) {
            Map<String, Object> processVariables = details.getProcessVariables();
            variables.put("nID_Protected", "" + AlgorithmLuna.getProtectedNumber(Long.valueOf(sID_Process)));
            variables.put("bankIdfirstName", processVariables.get("bankIdfirstName"));
            variables.put("bankIdmiddleName", processVariables.get("bankIdmiddleName"));
            variables.put("bankIdlastName", processVariables.get("bankIdlastName"));
            variables.put("phone", "" + processVariables.get("phone"));
            variables.put("email", processVariables.get("email"));
            variables.put("organ", getCandidateGroups(processName));
        }
        LOG.info(String.format(" >> start process [%s] with params: %s", PROCESS_FEEDBACK, variables));
        ProcessInstance feedbackProcess = runtimeService.startProcessInstanceByKey(PROCESS_FEEDBACK, variables);
        return feedbackProcess.getProcessInstanceId();
    }

    public void checkBpAndStartEscalationProcess(Map<String, Object> mTaskParam) {
        String sID_Process = (String) mTaskParam.get("sProcessInstanceId");
        String processName = (String) mTaskParam.get("sID_BP");
        String taskName = (String) mTaskParam.get("sTaskName");
        //todo:
        //1) check if bp has already started
        // 2) if no -- start and save id to HistoryEvwnt_Service
        // if yes -- pass
        LOG.info("TEST: start escalation service");

        String escalationProcessId = startEscalationProcess(mTaskParam, sID_Process, processName);

        Map<String, String> params = new HashMap<>();
        params.put("nID_Proccess_Escalation", escalationProcessId);
        LOG.info("   >>> put nID_Proccess_Escalation=" + escalationProcessId);
        //        try {
        //            historyEventService.updateHistoryEvent(sID_Process, taskName, false, params);
        //        } catch (Exception e) {
        //            LOG.error("ex!", e);
        //        }
    }

    private String startEscalationProcess(Map<String, Object> mTaskParam, String sID_Process, String processName) {
        Map<String, Object> variables = new HashMap<>();//sID_BP
        variables.put("processID", sID_Process);
        variables.put("processName", processName);
        variables.put("nID_Protected", "" + AlgorithmLuna.getProtectedNumber(Long.valueOf(sID_Process)));
        variables.put("bankIdfirstName", mTaskParam.get("bankIdfirstName"));
        variables.put("bankIdmiddleName", mTaskParam.get("bankIdmiddleName"));
        variables.put("bankIdlastName", mTaskParam.get("bankIdlastName"));
        variables.put("phone", "" + mTaskParam.get("phone"));
        variables.put("email", mTaskParam.get("email"));
        variables.put("organ", getCandidateGroups(processName));

        LOG.info(String.format(" >> start process [%s] with params: %s", PROCESS_ESCALATION, variables));
        //        ProcessInstance feedbackProcess = runtimeService.startProcessInstanceByKey(PROCESS_FEEDBACK, variables);
        //        return feedbackProcess.getProcessInstanceId();
        return "test-id";

    }

    private String getCandidateGroups(String processName) {
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
        LOG.info("candidateGroups=" + str);
        return candidateCroupsToCheck.size() > 0 ? str.substring(1, str.length() - 1) : "";
    }
}
