package org.activiti.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.controller.adapter.TaskAssigneeAdapter;
import org.activiti.rest.controller.entity.TaskAssigneeI;
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
@Api(tags = { "ActivitiRestTaskController" }, description = "Activiti")
@RequestMapping(value = "/rest/tasks")
@Transactional
public class ActivitiRestTaskController {
    public static final String CANCEL_INFO_FIELD = "sCancelInfo";
    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestTaskController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteController = "#####  Электронная эскалация. ";    

    private static final String noteGetTasksByAssignee = noteController + "Загрузка задач из Activiti #####\n\n"
		+ "HTTP Context: https://server:port/wf/service/rest/tasks/{assignee}\n\n"
		+ "- assignee - Владелец\n"
		+ "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
		+ "Request:\n"
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/kermit\n\n"
		+ "Response:\n"
		+ noteCODE
		+ "    [\n"
		+ "      {\n"
		+ "            \"delegationState\": \"RESOLVED\",\n"
		+ "            \"id\": \"38\",\n"
		+ "            \"name\": \"Первый процесс пользователя kermit\",\n"
		+ "            \"description\": \"Описание процесса\",\n"
		+ "            \"priority\": 51,\n"
		+ "            \"owner\": \"kermit-owner\",\n"
		+ "            \"assignee\": \"kermit-assignee\",\n"
		+ "            \"processInstanceId\": \"12\",\n"
		+ "            \"executionId\": \"1\",\n"
		+ "            \"createTime\": \"2015-04-13 00:51:34.527\",\n"
		+ "            \"taskDefinitionKey\": \"task-definition\",\n"
		+ "            \"dueDate\": \"2015-04-13 00:51:36.527\",\n"
		+ "            \"category\": \"my-category\",\n"
		+ "            \"parentTaskId\": \"2\",\n"
		+ "            \"tenantId\": \"diver\",\n"
		+ "            \"formKey\": \"form-key-12\",\n"
		+ "            \"suspended\": true,\n"
		+ "            \"processDefinitionId\": \"21\"\n"
		+ "      }\n"
		+ "    ]\n"
		+ noteCODE;

    private static final String noteGetTasksByOrder = noteController + "Получение списка ID пользовательских тасок по номеру заявки #####\n\n"
		+ "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=nID_Protected\n\n"
		+ " -- возвращает спискок ID пользовательских тасок по номеру заявки\n\n"
		+ "- nID_Protected - Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.\n\n"
		+ "Примеры:\n"
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=123452\n\n"
		+ "Responce status 403.\n\n"
		+ noteCODE
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"CRC Error\"}\n\n"
		+ noteCODE
		+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByOrder?nID_Protected=123451\n\n"
		+ "1) Если процесса с ID 12345 и тасками нет в базе то:\n\n"
		+ "Responce status 403.\n\n"
		+ noteCODE
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"Record not found\"}\n\n"
		+ noteCODE
		+ "2) Если процесс с ID 12345 есть в базе с таской ID которой 555, то:\n\n"
		+ "Responce status 200.\n"
		+ noteCODE
		+ "[ 555 ]\n"
		+ noteCODE;

    private static final String noteGetTasksByText = noteController + "Поиск заявок по тексту (в значениях полей без учета регистра) #####\n\n"
        	+ "HTTP Context: https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=sFind&sLogin=sLogin&bAssigned=true\n\n"
        	+ " -- возвращает список ID тасок у которых в полях встречается указанный текст\n\n"
        	+ "- sFind - текст для поиска в полях заявки.\n"
        	+ "- sLogin - необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin\n"
        	+ "- bAssigned - необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin\n\n"
        	+ "Примеры:\n"
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк\n"
        	+ noteCODE
        	+ "[\"4637994\",\"4715238\",\"4585497\",\"4585243\",\"4730773\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit\n"
        	+ noteCODE
        	+ "[\"4637994\",\"4715238\",\"4585243\",\"4730773\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=false\n"
        	+ noteCODE
        	+ "[\"4637994\",\"4637746\"]\n"
        	+ noteCODE
        	+ "https://test.region.igov.org.ua/wf/service/rest/tasks/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=true\n"
        	+ noteCODE
        	+ "[\"4715238\",\"4585243\",\"4730773\"]\n"
        	+ noteCODE;

    private static final String noteCancelTask = noteController + "нет описания #####\n\n";

    private static final String noteGetStartFormData = noteController + "Получение полей стартовой формы по ID таски #####\n\n"
    		+ "HTTP Context: http://test.region.igov.org.ua/wf/service/rest/tasks/getStartFormData?nID_Task=[nID_Task] -- возвращает JSON содержащий поля стартовой формы процесса.\n\n"
    		+ "- nID_Task - номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.\n\n"
    		+ "Примеры:\n"
    		+ "http://test.region.igov.org.ua/wf/service/rest/tasks/getStartFormData?nID_Task=5170256\n"
    		+ "Ответ, если запись существует (HTTP status Code: 200 OK):\n\n"
    		+ noteCODE
    		+ "{\n"
    		+ "  waterback=\"--------------------\",\n"
    		+ "  phone=\"380979362996\",\n"
    		+ "  date_from=\"01/01/2014\",\n"
    		+ "  bankIdbirthDay=\"27.05.1985\",\n"
    		+ "  notice2=\"Я та особи, які зареєстровані (фактично проживають) у житловому приміщенні/будинку, даємо згоду на обробку персональних даних про сім’ю, доходи, майно, що необхідні для призначення житлової субсидії, та оприлюднення відомостей щодо її призначення.\",\n"
      		+ "house=\"--------------------\",\n"
    		+ "  garbage=\"--------------------\",\n"
    		+ "  waterback_notice=\"\",\n"
    		+ "  garbage_number=\"\",\n"
    		+ "  floors=\"10\",\n"
    		+ "  name_services=\"--------------------\",\n"
    		+ "  date_to=\"30/12/2014\",\n"
    		+ "  date3=\"\",\n"
    		+ "  date2=\"\",\n"
    		+ "  electricity=\"--------------------\",\n"
    		+ "  garbage_name=\"\",\n"
    		+ "  date1=\"\",\n"
    		+ "  place_type=\"2\",\n"
    		+ "  bankIdfirstName=\"ДМИТРО\",\n"
    		+ "  declaration=\"--------------------\",\n"
    		+ "  waterback_name=\"\",\n"
    		+ "  electricity_notice=\"\",\n"
    		+ "  bankIdinn=\"3119325858\",\n"
    		+ "  house_name=\"\",\n"
    		+ "  gas=\"--------------------\",\n"
    		+ "  house_number=\"\",\n"
    		+ "  subsidy=\"1\",\n"
    		+ "  email=\"dmitrij.zabrudskij@privatbank.ua\",\n"
    		+ "  warming=\"--------------------\",\n"
    		+ "  hotwater_notice=\"\",\n"
    		+ "  org0=\"Назва організації\",\n"
    		+ "  org1=\"\",\n"
    		+ "  electricity_number=\"123456\",\n"
    		+ "  org2=\"\",\n"
    		+ "  org3=\"\",\n"
    		+ "  warming_name=\"\",\n"
    		+ "  place_of_living=\"Дніпропетровська, Дніпропетровськ, пр. Героїв, 17, кв 120\",\n"
    		+ "  fio2=\"\",\n"
    		+ "  fio3=\"\",\n"
    		+ "  total_place=\"68\",\n"
    		+ "  garbage_notice=\"\",\n"
    		+ "  fio1=\"\",\n"
    		+ "  chapter1=\"--------------------\",\n"
    		+ "  bankIdmiddleName=\"ОЛЕКСАНДРОВИЧ\",\n"
    		+ "  gas_name=\"\",\n"
    		+ "  bankIdPassport=\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\n"
    		+ "  warming_place=\"45\",\n"
    		+ "  passport3=\"\",\n"
    		+ "  gas_number=\"\",\n"
    		+ "  passport2=\"\",\n"
    		+ "  electricity_name=\"коммуна\",\n"
    		+ "  area=\"samar\",\n"
    		+ "  house_notice=\"\",\n"
    		+ "  bankIdlastName=\"ДУБІЛЕТ\",\n"
    		+ "  card1=\"\",\n"
    		+ "  card3=\"\",\n"
    		+ "  coolwater_number=\"\",\n"
    		+ "  card2=\"\",\n"
    		+ "  warming_notice=\"\",\n"
    		+ "  hotwater_name=\"\",\n"
    		+ "  income0=\"attr9\",\n"
    		+ "  coolwater=\"--------------------\",\n"
    		+ "  gas_notice=\"\",\n"
    		+ "  overload=\"hxhxfhfxhfghg\",\n"
    		+ "  warming_number=\"\",\n"
    		+ "  income3=\"attr0\",\n"
    		+ "  income1=\"attr0\",\n"
    		+ "  income2=\"attr0\",\n"
    		+ "  passport1=\"\",\n"
    		+ "  coolwater_notice=\"\",\n"
    		+ "  sBody_1=\"null\",\n"
    		+ "  hotwater=\"--------------------\",\n"
    		+ "  coolwater_name=\"\",\n"
    		+ "  waterback_number=\"\",\n"
    		+ "  man1=\"\",\n"
    		+ "  hotwater_number=\"\",\n"
    		+ "  sBody_2=\"null\",\n"
    		+ "  comment=\"null\",\n"
    		+ "  decision=\"null\",\n"
    		+ "  selection=\"attr1\"\n"
    		+ "}\n"
    		+ noteCODE
    		+ "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n\n"
    		+ noteCODE
    		+ "{\n"
    		+ "  \"code\": \"BUSINESS_ERR\",\n"
    		+ "  \"message\": \"Record not found\"\n"
    		+ "}\n"
    		+ noteCODE;
        
    /////////////////////////////////////////////////////////////////////////////////////////////////////

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

    /**
     * Загрузка задач из Activiti:
     * @param assignee Владелец
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Загрузка задач из Activiti", notes =  noteGetTasksByAssignee )
    @RequestMapping(value = "/{assignee}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TaskAssigneeI> getTasksByAssignee( @ApiParam(value = "ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true)  @PathVariable("assignee") String assignee) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        List<TaskAssigneeI> facadeTasks = new ArrayList<>();
        TaskAssigneeAdapter adapter = new TaskAssigneeAdapter();
        for (Task task : tasks) {
            facadeTasks.add(adapter.apply(task));
        }
        return facadeTasks;
    }

    /**
     * @param nID_Protected Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.
     */
    @ApiOperation(value = "Получение списка ID пользовательских тасок по номеру заявки", notes =  noteGetTasksByOrder )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "CRC Error или Record not found") })
    @RequestMapping(value = "/getTasksByOrder", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getTasksByOrder( @ApiParam(value = " Номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.", required = true)  @RequestParam(value = "nID_Protected") Long nID_Protected)
            throws ActivitiRestException, CRCInvalidException, RecordNotFoundException {

        String processInstanceID = getOriginalProcessInstanceId(nID_Protected);
        return getTaskIdsByProcessInstanceId(processInstanceID);

    }

    /**
     * @param sFind текст для поиска в полях заявки.
     * @param sLogin необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin
     * @param bAssigned необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin
     */
    @ApiOperation(value = "Поиск заявок по тексту (в значениях полей без учета регистра)", notes =  noteGetTasksByText )
    @RequestMapping(value = "/getTasksByText", method = RequestMethod.GET)
    public
    @ResponseBody
    Set<String> getTasksByText( @ApiParam(value = "текст для поиска в полях заявки", required = true)  @RequestParam(value = "sFind") String sFind,
	    @ApiParam(value = "необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin", required = false )  @RequestParam(value = "sLogin", required = false) String sLogin,
	    @ApiParam(value = "необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin", required = false )  @RequestParam(value = "bAssigned", required = false) String bAssigned) throws ActivitiRestException {
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

    @ApiOperation(value = "cancelTask", notes =  noteCancelTask )
    @RequestMapping(value = "/cancelTask", method = RequestMethod.POST)
    public
    @ResponseBody
        //void cancelTask(@RequestParam(value = "nID_Protected") Long nID_Protected,
    ResponseEntity<String> cancelTask( @ApiParam(value = "нет описания", required = true )  @RequestParam(value = "nID_Protected") Long nID_Protected,
	    @ApiParam(value = "нет описания", required = false )  @RequestParam(value = "sInfo", required = false) String sInfo)
            throws ActivitiRestException, TaskAlreadyUnboundException {

        //String sMessage = "Ваша заявка відмінена. Ви можете подату нову на Порталі державних послуг iGov.org.ua.<\n<br>"
        //        + "З повагою, команду порталу  iGov.org.ua";
        String sMessage = "Vasha zayavka skasovana uspishno.";

        try {
            cancelTasksInternal(nID_Protected, sInfo);
            return new ResponseEntity<String>(sMessage, HttpStatus.OK);
        } catch (CRCInvalidException | RecordNotFoundException e) {
            ActivitiRestException newErr = new ActivitiRestException(
                    "BUSINESS_ERR", e.getMessage(), e);
            newErr.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn(e.getMessage(), e);
            //sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";
            sMessage = "Pomylka skasuvannya!";
            //                        return sMessage;
            //throw newErr;
            return new ResponseEntity<String>(sMessage, HttpStatus.FORBIDDEN);
        }

    }

    /**
     * @param nID_Task номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.
     */
    @ApiOperation(value = "Получение полей стартовой формы по ID таски", notes =  noteGetStartFormData )
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/getStartFormData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getFormDat( @ApiParam(value = " номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.", required = true )  @RequestParam(value = "nID_Task") String nID_Task)
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
        return Long.toString(AlgorithmLuna.getValidatedOriginalNumber(nID_Protected));
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
