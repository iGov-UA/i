package org.activiti.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.cmd.CustomSqlExecution;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.controller.adapter.TaskAssigneeAdapter;
import org.activiti.rest.controller.entity.TaskAssigneeI;
import org.apache.ibatis.annotations.Select;
import org.egov.service.HistoryEventService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.wf.dp.dniprorada.base.dao.EntityNotFoundException;
import org.wf.dp.dniprorada.base.dao.FlowSlotTicketDao;
import org.wf.dp.dniprorada.base.model.AbstractModelTask;
import org.wf.dp.dniprorada.form.QueueDataFormType;
import org.wf.dp.dniprorada.util.luna.AlgorithmLuna;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import java.util.*;

import static org.activiti.rest.controller.ActivitiRestApiController.parseEnumProperty;

/**
 * @author vit@tym.im
 */
@Controller
@RequestMapping(value = "/rest/tasks")
@Transactional
public class ActivitiRestTaskController {
    public static final String CANCEL_INFO_FIELD = "sCancelInfo";
    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestTaskController.class);
    @Autowired
    private TaskService taskService;
    @Autowired
    private ActivitiExceptionController exceptionController;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;
    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;
    @Autowired
    private HistoryEventService historyEventService;

    @ExceptionHandler({CRCInvalidException.class, EntityNotFoundException.class, RecordNotFoundException.class, TaskAlreadyUnboundException.class})
    @ResponseBody
    public ResponseEntity<String> handleAccessException(Exception e) throws ActivitiRestException {
        return exceptionController.catchActivitiRestException(new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                e.getMessage(), e,
                HttpStatus.FORBIDDEN));
    }

    @RequestMapping(value = "/{assignee}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TaskAssigneeI> getTasksByAssignee(@PathVariable("assignee") String assignee) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        List<TaskAssigneeI> facadeTasks = new ArrayList<>();
        TaskAssigneeAdapter adapter = new TaskAssigneeAdapter();
        for (Task task : tasks) {
            facadeTasks.add(adapter.apply(task));
        }
        return facadeTasks;
    }

    @RequestMapping(value = "/getTasksByOrder", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getTasksByOrder(@RequestParam(value = "nID_Protected") Long nID_Protected)
            throws ActivitiRestException, CRCInvalidException, RecordNotFoundException {

        String processInstanceID = getOriginalProcessInstanceId(nID_Protected);
        return getTaskIdsByProcessInstanceId(processInstanceID);

    }

    @RequestMapping(value = "/getTasksByText", method = RequestMethod.GET)
    public
    @ResponseBody
    Set<String> getTasksByText(@RequestParam(value = "sFind") String sFind,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "bAssigned", required = false) String bAssigned) throws ActivitiRestException {
        Set<String> res = new HashSet<String>();

        String searchTeam = sFind.toLowerCase();
        TaskQuery taskQuery = buildTaskQuery(sLogin, bAssigned);
        List<Task> activeTasks = taskQuery.active().list();
        for (Task currTask : activeTasks) {
            TaskFormData data = formService.getTaskFormData(currTask.getId());
            if (data != null) {
                for (FormProperty property : data.getFormProperties()) {

                    String sValue = "";
                    String sType = property.getType().getName();
                    if ("enum".equalsIgnoreCase(sType)) {
                        sValue = parseEnumProperty(property);
                    } else {
                        sValue = property.getValue();
                    }
                    LOG.info("taskId=" + currTask.getId() + "propertyName=" + property.getName() + "sValue=" + sValue);
                    if (sValue != null) {
                        if (sValue.toLowerCase().indexOf(searchTeam) >= 0) {
                            res.add(currTask.getId());
                        }
                    }
                }
            } else {
                LOG.info("TaskFormData for task " + currTask.getId() + "is null. Skipping from processing.");
            }
        }

        return res;
    }

    @RequestMapping(value = "/removeTask", method = RequestMethod.DELETE)
    public
    @ResponseBody
    void removeTask(@RequestParam(value = "nID_Protected") Long nID_Protected,
            @RequestParam(value = "sLogin", required = false) String sLogin)
            throws Exception {

        String processInstanceID = getOriginalProcessInstanceId(nID_Protected);

        taskService.deleteTasks(getTaskIdsByProcessInstanceId(processInstanceID));
        String sID_status = "Заявка была удалена";
        if (sLogin != null) {
            sID_status += " (" + sLogin + ")";
        }
        historyEventService.updateHistoryEvent(processInstanceID, sID_status, false, null);
    }

    @RequestMapping(value = "/cancelTask", method = RequestMethod.POST)
    public
    @ResponseBody
        //void cancelTask(@RequestParam(value = "nID_Protected") Long nID_Protected,
    ResponseEntity<String> cancelTask(@RequestParam(value = "nID_Protected") Long nID_Protected,
            @RequestParam(value = "sInfo", required = false) String sInfo)
            throws ActivitiRestException, TaskAlreadyUnboundException {

        String sMessage = "Ваша заявка відмінена. Ви можете подату нову на Порталі державних послуг iGov.org.ua.<\n<br>"
                + "З повагою, команду порталу  iGov.org.ua";

        try {
            cancelTasksInternal(nID_Protected, sInfo);
            return new ResponseEntity<String>(sMessage, HttpStatus.OK);
        } catch (CRCInvalidException | RecordNotFoundException e) {
            ActivitiRestException newErr = new ActivitiRestException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn(e.getMessage(), e);
            sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";
            //                        return sMessage;
            //throw newErr;
            return new ResponseEntity<String>(sMessage, HttpStatus.FORBIDDEN);
        }

    }

    @RequestMapping(value = "/getStartFormData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getFormDat(@RequestParam(value = "nID_Task") String nID_Task)
            throws ActivitiRestException, JsonProcessingException, RecordNotFoundException {
        List<FormProperty> formProperties = null;
        StringBuilder sb = null;
        StartFormData formData = null;

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(nID_Task).singleResult();
        LOG.info("historicTaskInstance {} ", historicTaskInstance);

        List<HistoricDetail> details = null;
        String processInstanceId = null;
        if (historicTaskInstance == null) {
            throw new RecordNotFoundException();
        }
        processInstanceId = historicTaskInstance.getProcessInstanceId();
        LOG.info("processInstanceId {} ", processInstanceId);

        if(processInstanceId != null){
            details = historyService.createHistoricDetailQuery().formProperties()
                    .executionId(processInstanceId).list();
        }

        LOG.info("details {} ", details);
        if(details == null){
            throw new RecordNotFoundException();
        }

        sb = new StringBuilder("{");
        for (Iterator<HistoricDetail> iterator = details.iterator(); iterator.hasNext(); ) {
            HistoricDetail detail = iterator.next();
            HistoricFormProperty property = (HistoricFormProperty) detail;
            sb.append(property.getPropertyId());
            sb.append("=");
            sb.append("\"");
            sb.append(property.getPropertyValue());
            sb.append("\"");
            if(iterator.hasNext()){
                sb.append(",");
            }
        }
        sb.append("}");

        return sb.toString();
    }


    protected TaskQuery buildTaskQuery(String sLogin, String bAssigned) {
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (bAssigned != null) {
            if (!Boolean.valueOf(bAssigned).booleanValue()) {
                taskQuery.taskUnassigned();
                if (sLogin != null && !sLogin.isEmpty()) {
                    taskQuery.taskCandidateUser(sLogin);
                }
            } else if (sLogin != null && !sLogin.isEmpty()) {
                taskQuery.taskAssignee(sLogin);
            }
        } else {
            if (sLogin != null && !sLogin.isEmpty()) {
                taskQuery.taskCandidateOrAssigned(sLogin);
            }
        }
        return taskQuery;
    }

    void cancelTasksInternal(Long nID_Protected, String sInfo) throws ActivitiRestException,
            CRCInvalidException, RecordNotFoundException, TaskAlreadyUnboundException {

        String processInstanceId = getOriginalProcessInstanceId(nID_Protected);

        getTasksByProcessInstanceId(processInstanceId);
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(
                processInstanceId).singleResult();

        FormData formData = formService.getStartFormData(processInstance.getProcessDefinitionId());

        List<String> propertyIds = AbstractModelTask.getListField_QueueDataFormType(formData);
        List<String> queueDataList = AbstractModelTask.getVariableValues(runtimeService, processInstanceId,
                propertyIds);

        if (queueDataList.isEmpty()) {
            LOG.error(String.format("Queue data list for Process Instance [id = '%s'] not found", processInstanceId));
            throw new RecordNotFoundException("Метаданные электронной очереди не найдены");
        }

        for (String queueData : queueDataList) {
            Map<String, Object> m = QueueDataFormType.parseQueueData(queueData);
            long nID_FlowSlotTicket = QueueDataFormType.get_nID_FlowSlotTicket(m);
            if (!flowSlotTicketDao.unbindFromTask(nID_FlowSlotTicket)) {
                throw new TaskAlreadyUnboundException("Заявка уже отменена");
            }
        }

        runtimeService.setVariable(processInstanceId, CANCEL_INFO_FIELD,
                String.format("[%s] Причина отмены заявки: %s", DateTime.now(), sInfo == null ? "" : sInfo));

    }

    private String getOriginalProcessInstanceId(Long nID_Protected) throws CRCInvalidException {
        AlgorithmLuna.validateProtectedNumber(nID_Protected);

        return Long.toString(AlgorithmLuna.getOriginalNumber(nID_Protected));
    }

    private List<String> getTaskIdsByProcessInstanceId(String processInstanceID) throws RecordNotFoundException {
        List<Task> aTask = getTasksByProcessInstanceId(processInstanceID);
        List<String> res = new ArrayList<>();

        for (Task task : aTask) {
            res.add(task.getId());
        }

        return res;
    }

    private List<Task> getTasksByProcessInstanceId(String processInstanceID) throws RecordNotFoundException {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceID).list();
        if (tasks == null || tasks.isEmpty()) {
            LOG.error(String.format("Tasks for Process Instance [id = '%s'] not found", processInstanceID));
            throw new RecordNotFoundException();
        }
        return tasks;
    }

    private static class TaskAlreadyUnboundException extends Exception {
        public TaskAlreadyUnboundException(String message) {
            super(message);
        }
    }
}
