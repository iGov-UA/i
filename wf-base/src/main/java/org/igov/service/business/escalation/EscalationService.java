package org.igov.service.business.escalation;

import org.igov.model.escalation.EscalationRuleFunctionDao;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.igov.service.exception.CommonServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.igov.service.business.action.task.core.ExploreBPMN;
import org.igov.util.ToolLuna;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.igov.io.GeneralConfig;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.model.escalation.EscalationRule;
import org.igov.model.escalation.EscalationRuleDao;
import org.igov.model.escalation.EscalationRuleFunction;

@Service
public class EscalationService {
    private static final Logger LOG = LoggerFactory.getLogger(EscalationService.class);

    @Autowired
    GeneralConfig oGeneralConfig;
    
    private static final String SEARCH_DELAYED_TASKS_URL = "/wf/service/action/task/getStartFormData?nID_Task=";// /task-activiti/
    //private static final String REGIONAL_SERVER_PATH = "https://region.org.gov.ua";

    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private EscalationRuleDao escalationRuleDao;
    @Autowired
    private EscalationHelper escalationHelper;
    @Autowired
    private EscalationRuleFunctionDao escalationRuleFunctionDao;

    private int nFailsTotal=0;
    private int nFails=0;
    
    public void runEscalationAll() throws CommonServiceException {
        nFailsTotal = 0;
        try {
            List<EscalationRule> aEscalationRule = escalationRuleDao.findAll();
            
            for (EscalationRule oEscalationRule : aEscalationRule) {
                runEscalationRule(oEscalationRule, oGeneralConfig.sHost());//REGIONAL_SERVER_PATH
            }
            if(nFailsTotal>0){
                //LOG.warn("FAIL: (nFailsTotal={})", nFailsTotal);
                throw new Exception("Has fails! (nFailsTotal="+nFailsTotal+")");
            }
        } catch (Exception oException) {
            //LOG.error("FAIL: ", oException);
            LOG.error("FAIL: {} (nFailsTotal={})", oException.getMessage(), nFailsTotal);
            throw new CommonServiceException("ex in controller!", oException);
        }

    }

    public void runEscalationRule(Long nID_escalationRule, String regionalServerPath) throws Exception {
        runEscalationRule(escalationRuleDao.findById(nID_escalationRule).orNull(), regionalServerPath);
        if(nFailsTotal>0){
            //LOG.warn("FAIL: (nFailsTotal={})", nFailsTotal);
            throw new Exception("Has fails! (nFailsTotal="+nFailsTotal+")");
        }
    }

    private void runEscalationRule(EscalationRule oEscalationRule, String regionalServerPath) {
        String sID_BP = null;
        String sID_State_BP = null;
        EscalationRuleFunction oEscalationRuleFunction = oEscalationRule.getoEscalationRuleFunction();
        Object onID_Task = null;
        nFails=0;
        try {
            sID_BP = oEscalationRule.getsID_BP();
            sID_State_BP = oEscalationRule.getsID_UserTask();
            TaskQuery oTaskQuery = taskService.createTaskQuery()
                    .processDefinitionKey(sID_BP);//.taskCreatedAfter(dateAt).taskCreatedBefore(dateTo)
            if (sID_State_BP != null && !"*".equals(sID_State_BP)) {
                oTaskQuery = oTaskQuery.taskDefinitionKey(sID_State_BP);
            }
            Integer nRowStart = 0;
            Integer nRowsMax = 1000;
            List<Task> aTask = oTaskQuery.listPage(nRowStart, nRowsMax);

            LOG.info("Iterrating tasks... (sID_BP={}, sID_State_BP={}, aTask.size()={})", sID_BP, sID_State_BP, aTask.size());
            //LOG.info("Found {} tasks for specified business process and state", aTask.size());
            for (Task oTask : aTask) {
                onID_Task = null;
                Map<String, Object> mTaskParam = null;
                try {
                    mTaskParam = getTaskData(oTask);
                    onID_Task = mTaskParam.get("nID_task_activiti");
                    mTaskParam.put("processLink", regionalServerPath + SEARCH_DELAYED_TASKS_URL + onID_Task);
                    mTaskParam.put("nID_EscalationRule", oEscalationRule.getId());
    //                LOG.info("checkTaskOnEscalation (mTaskParam={})", mTaskParam);
                    //send emails (or processing by other bean-handlers)
                    escalationHelper.checkTaskOnEscalation(mTaskParam
                            , oEscalationRule.getsCondition()
                            , oEscalationRule.getSoData()
                            , oEscalationRule.getsPatternFile()
                            , oEscalationRuleFunction.getsBeanHandler()
                    );
                } catch (Exception e) {
                    nFails++;
                    nFailsTotal++;
                    //LOG.error("Can't run handler escalation for task: {} (nFails={}, oTask.getId()={}, getsBeanHandler()={}, sID_BP={}, sID_State_BP={})", e.getMessage(), nFails, oTask.getId(), oEscalationRuleFunction.getsBeanHandler(), sID_BP, sID_State_BP);
                    LOG.error("Can't run handler escalation for task: {} (nFails={}, oTask.getId()={}, getsBeanHandler()={}, mTaskParam={})", e.getMessage(), nFails, oTask.getId(), oEscalationRuleFunction.getsBeanHandler(),mTaskParam);
                    LOG.trace("FAIL:", e);
                }
            }
        } catch (Exception e) {
            nFailsTotal++;
            LOG.error("Can't run escalation: {} (nFails={}, onID_Task={}, getsBeanHandler()={}, sID_BP={}, sID_State_BP={})", e.getMessage(), nFails, onID_Task, oEscalationRuleFunction.getsBeanHandler(), sID_BP, sID_State_BP);
            LOG.trace("FAIL:", e);
            //throw e;
        }
    }

    private Map<String, Object> getTaskData(final Task oTask) throws Exception {//Long nID_task_activiti
        final String taskId = oTask.getId();
        long nID_task_activiti = Long.valueOf(taskId);
        LOG.debug("(nID_task_activiti={})", nID_task_activiti);
        LOG.debug("(oTask.getCreateTime().toString()={})", oTask.getCreateTime());
        LOG.debug("(oTask.getDueDate().toString()={})", oTask.getDueDate());

        Map<String, Object> m = new HashMap<>();
        m.put("sTaskId", taskId);

        long nDiffMS = 0;
        if (oTask.getDueDate() != null) {
            nDiffMS = oTask.getDueDate().getTime() - oTask.getCreateTime().getTime();
        } else {
            nDiffMS = DateTime.now().toDate().getTime() - oTask.getCreateTime().getTime();
        }
        LOG.debug("(nDiffMS={})", nDiffMS);

        long nElapsedHours = nDiffMS / 1000 / 60 / 60;
        LOG.debug("(nElapsedHours={})", nElapsedHours);
        m.put("nElapsedHours", nElapsedHours);

        long nElapsedDays = nElapsedHours / 24;
        LOG.debug("(nElapsedDays={})", nElapsedDays);
        m.put("nElapsedDays", nElapsedDays);
        m.put("nDays", nElapsedDays);

        TaskFormData oTaskFormData = formService.getTaskFormData(taskId);
        for (FormProperty oFormProperty : oTaskFormData.getFormProperties()) {
        	String sType = oFormProperty.getType().getName();
        	String sValue = null;
            LOG.debug(String.format("Matching property %s:%s:%s with fieldNames", oFormProperty.getId(),
                    oFormProperty.getName(), sType));
            if ("long".equalsIgnoreCase(oFormProperty.getType().getName()) &&
                    StringUtils.isNumeric(oFormProperty.getValue())) {
                m.put(oFormProperty.getId(), Long.valueOf(oFormProperty.getValue()));
            } else {
            	if ("enum".equalsIgnoreCase(sType)) {
					sValue = ActionTaskService.parseEnumProperty(oFormProperty);
				} else {
					sValue = oFormProperty.getValue();
				}
            	if (sValue != null){
            		m.put(oFormProperty.getId(), sValue);
            	}
            }
        }

        m.put("sID_BP_full", oTask.getProcessDefinitionId());
        m.put("sID_BP", StringUtils.substringBefore(oTask.getProcessDefinitionId(), ":"));
        m.put("nID_task_activiti", ToolLuna.getProtectedNumber(Long.valueOf(oTask.getProcessInstanceId())));
        m.put("sTaskName", oTask.getName());
        m.put("sTaskDescription", oTask.getDescription());
        m.put("sProcessInstanceId", oTask.getProcessInstanceId());
        m.put("sLoginAssigned", oTask.getAssignee());
        
        List<User> aUser = ExploreBPMN
                .getUsersInfoBelongToProcess(repositoryService, identityService, oTask.getProcessDefinitionId(),
                        oTask.getTaskDefinitionKey());
        StringBuffer osaUser = new StringBuffer();
        int nCount = aUser.size();
        int n = 0;
        for (User oUser : aUser) {
            n++;
            osaUser.append(oUser.getLastName());
            osaUser.append(" ");
            osaUser.append(oUser.getFirstName());
            osaUser.append(" (");
            osaUser.append(oUser.getId());
            osaUser.append(")");
            if (n < nCount) {
                osaUser.append(", ");
            }
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(oTask.getProcessDefinitionId()).singleResult();

        m.put("sServiceType", processDefinition != null ? processDefinition.getName() : "");
        m.put("sTaskName", String.format("%s", oTask.getName()));
        m.put("sTaskNumber", ToolLuna.getProtectedNumber(Long.valueOf(oTask.getProcessInstanceId())));
        m.put("sElapsedInfo", String.format("%d", nElapsedDays));
        m.put("sResponsiblePersons", String.format("%s", osaUser.toString()));

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(oTask.getProcessInstanceId()).singleResult();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        m.put("sDate_BP", formatter.format(processInstance.getStartTime().getTime()));
        m.putAll(processInstance.getProcessVariables());


        return m;
    }

    /**
     * добавление/обновление записи правила эскалации
     * если nID - null, то это создание записи
     * если nID задан, и он есть -- запись обновляется
     *
     * @param nID                        - ИД-номер (уникальный-автоитерируемый)
     * @param sID_BP                     - ИД-строка бизнес-процесса
     * @param sID_UserTask               - ИД-строка юзертаски бизнеспроцесса (если указана * -- то выбираются все задачи из бизнес-процесса)
     * @param sCondition                 - строка-условие (на языке javascript )
     * @param soData                     - строка-обьект, с данными (JSON-обьект)
     * @param sPatternFile               - строка файла-шаблона (примеры тут)
     * @param nID_EscalationRuleFunction - ИД-номер функции эскалации
     * @return созданная/обновленная запись.
     */
    public EscalationRule setEscalationRule(
            Long nID,
            String sID_BP,
            String sID_UserTask,
            String sCondition,
            String soData,
            String sPatternFile,
            Long nID_EscalationRuleFunction) {
        EscalationRuleFunction ruleFunction = null;
        if (nID_EscalationRuleFunction != null) {
            ruleFunction = escalationRuleFunctionDao.findById(nID_EscalationRuleFunction).orNull();
        }
        return escalationRuleDao.saveOrUpdate(nID, sID_BP, sID_UserTask,
                sCondition, soData, sPatternFile, ruleFunction);
    }
}
