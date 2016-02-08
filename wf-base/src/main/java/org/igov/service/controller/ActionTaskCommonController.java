package org.igov.service.controller;

import static org.igov.service.business.action.task.core.ActionTaskService.DATE_TIME_FORMAT;
import static org.igov.util.Tool.sO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import liquibase.util.csv.CSVWriter;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.NotificationPatterns;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.action.task.core.ProcessDTOCover;
import org.igov.model.action.task.core.ProcessDefinitionCover;
import org.igov.model.action.task.core.entity.*;
import org.igov.model.action.task.core.entity.Process;
import org.igov.model.action.task.core.entity.ActionTaskLinkDao;
import org.igov.model.flow.FlowSlotTicket;
import org.igov.model.flow.FlowSlotTicketDao;
import org.igov.service.business.action.event.HistoryEventService;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.service.exception.TaskAlreadyUnboundException;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.igov.util.Tool;
import org.igov.util.ToolCellSum;
import org.igov.util.JSON.JsonRestUtils;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

//import com.google.common.base.Optional;

/**
 * @author BW
 */

@Controller
@Api(tags = { "ActionTaskCommonController — Действия общие задач" })
@RequestMapping(value = "/action/task")
public class ActionTaskCommonController {//extends ExecutionBaseResource

    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskCommonController.class);
    

	static final Comparator<FlowSlotTicket> FLOW_SLOT_TICKET_ORDER_CREATE_COMPARATOR = new Comparator<FlowSlotTicket>() {
		public int compare(FlowSlotTicket e1, FlowSlotTicket e2) {
			return e2.getsDateStart().compareTo(e1.getsDateStart());
		}
	};
    
    @Autowired
    private HttpRequester httpRequester;
    
    @Autowired
    public GeneralConfig generalConfig;
    @Autowired
    private TaskService taskService;
    //@Autowired
    //private ExceptionCommonController exceptionController;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;
    @Autowired
    private RepositoryService repositoryService;
    //@Autowired
    //private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    @Autowired
    private HistoryEventService historyEventService;
    @Autowired
    private IdentityService identityService;
    //@Autowired
    //private ExceptionCommonController exceptionController;
    @Autowired
    private NotificationPatterns oNotificationPatterns;

    /*@ExceptionHandler({CRCInvalidException.class, EntityNotFoundException.class, RecordNotFoundException.class, TaskAlreadyUnboundException.class})
    @ResponseBody
    public ResponseEntity<String> handleAccessException(Exception e) throws CommonServiceException {
        return exceptionController.catchActivitiRestException(new CommonServiceException(
                ExceptionCommonController.BUSINESS_ERROR_CODE,
                e.getMessage(), e,
                HttpStatus.FORBIDDEN));
    }*/
    @Autowired
    private ActionTaskService oActionTaskService;
    
    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;

    @Autowired
    private ActionTaskLinkDao actionTaskLinkDao;

    /**
     * Загрузка задач из Activiti:
     * @param assignee ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
    //     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Загрузка задач из Activiti", notes =  "#####  Request:\n"
		+ "https://test.region.igov.org.ua/wf/service/action/task/kermit\n\n"
		+ "Response:\n"
		+ "\n```json\n"
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
		+ "\n```\n" )
    @RequestMapping(value = "/login/{assignee}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TaskAssigneeI> getTasksByAssignee( @ApiParam(value = "ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true)  @PathVariable("assignee") String assignee) {

        return oActionTaskService.getTasksByAssignee(assignee);
    }
    
     /**
     *
     * @param group ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/groups/{group}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<TaskAssigneeI> getTasksByAssigneeGroup( @ApiParam(value = "ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true)  @PathVariable("group") String group) {

        return oActionTaskService.getTasksByAssigneeGroup(group);
    }

    /**
     * Получение списка ID пользовательских тасок по номеру заявки
     * 
     * @param nID_Order число номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.
     */
    @ApiOperation(value = "Получение списка ID пользовательских тасок по номеру заявки", notes =  "##### Примеры:\n"
		+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByOrder?nID_Order=123452\n\n"
		+ "Response status 403.\n\n"
		+ "\n```json\n"
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"CRC Error\"}\n\n"
		+ "\n```\n"
		+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByOrder?nID_Order=123451\n\n"
		+ "1) Если процесса с ID 12345 и тасками нет в базе то:\n\n"
		+ "Response status 403.\n\n"
		+ "\n```json\n"
		+ "{\"code\":\"BUSINESS_ERR\",\"message\":\"Record not found\"}\n\n"
		+ "\n```\n"
		+ "2) Если процесс с ID 12345 есть в базе с таской ID которой 555, то:\n\n"
		+ "Response status 200.\n"
		+ "\n```json\n"
		+ "[ 555 ]\n"
		+ "\n```\n" )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "CRC Error или Record not found"),
                            @ApiResponse(code = 200, message = "Успех запроса. Если процесс с соответствующим ИД и таской найдены в базе")})
    @RequestMapping(value = "/getTasksByOrder", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getTasksByOrder(
            @ApiParam(value = " число номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.", required = true)  @RequestParam(value = "nID_Order") Long nID_Order
            //@ApiParam(value = " Номер процесса activiti.", required = true)  @RequestParam(value = "nID_Process") String snID_Process
    )
            throws CommonServiceException, CRCInvalidException, RecordNotFoundException {

        String snID_Process = oActionTaskService.getOriginalProcessInstanceId(nID_Order);
        return oActionTaskService.getTaskIdsByProcessInstanceId(snID_Process);

    }

    /**
     * Поиск заявок по тексту (в значениях полей без учета регистра)
     * 
     * @param sFind строка текст для поиска в полях заявки.
     * @param sLogin строка необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin
     * @param bAssigned булево значение необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin
     */
    @ApiOperation(value = "Поиск заявок по тексту (в значениях полей без учета регистра)", notes =  "##### Примеры:\n"
    	+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByText?sFind=будинк\n"
    	+ "\n```json\n"
    	+ "[\"4637994\",\"4715238\",\"4585497\",\"4585243\",\"4730773\",\"4637746\"]\n"
    	+ "\n```\n"
    	+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByText?sFind=будинк&sLogin=kermit\n"
    	+ "\n```json\n"
    	+ "[\"4637994\",\"4715238\",\"4585243\",\"4730773\",\"4637746\"]\n"
    	+ "\n```\n"
    	+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=false\n"
    	+ "\n```json\n"
    	+ "[\"4637994\",\"4637746\"]\n"
    	+ "\n```\n"
    	+ "https://test.region.igov.org.ua/wf/service/action/task/getTasksByText?sFind=будинк&sLogin=kermit&bAssigned=true\n"
    	+ "\n```json\n"
    	+ "[\"4715238\",\"4585243\",\"4730773\"]\n"
    	+ "\n```\n" )
    @ApiResponses(value = { @ApiResponse(code = 200, message = "возвращает список ID тасок у которых в полях встречается указанный текст")})
    @RequestMapping(value = "/getTasksByText", method = RequestMethod.GET)
    public
    @ResponseBody
    Set<String> getTasksByText( @ApiParam(value = "строка текст для поиска в полях заявки", required = true)  @RequestParam(value = "sFind") String sFind,
	    @ApiParam(value = "строка необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin", required = false )  @RequestParam(value = "sLogin", required = false) String sLogin,
	    @ApiParam(value = "булево значение необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin", required = false )  @RequestParam(value = "bAssigned", required = false) String bAssigned) throws CommonServiceException {
        Set<String> res = new HashSet<>();

        String searchTeam = sFind.toLowerCase();
        TaskQuery taskQuery = oActionTaskService.buildTaskQuery(sLogin, bAssigned);
        List<Task> activeTasks = taskQuery.active().list();
        for (Task currTask : activeTasks) {
            TaskFormData data = formService.getTaskFormData(currTask.getId());
            if (data != null) {
                for (FormProperty property : data.getFormProperties()) {

                    String sValue = "";
                    String sType = property.getType().getName();
                    if ("enum".equalsIgnoreCase(sType)) {
                        sValue = oActionTaskService.parseEnumProperty(property);
                    } else {
                        sValue = property.getValue();
                    }
                    LOG.info("(taskId={}, propertyName={}, sValue={})", currTask.getId(), property.getName(), sValue);
                    if (sValue != null) {
                        if (sValue.toLowerCase().contains(searchTeam)) {
                            res.add(currTask.getId());
                        }
                    }
                }
            } else {
                LOG.info("TaskFormData for task {} is null. Skipping from processing.", currTask.getId());
            }
        }

        return res;
    }
    
    /**
     * Отмена задачи (в т.ч. электронной очереди)
     * 
     * @param nID_Order  номер-ИД процесса (с контрольной суммой)
     * @param sInfo      Строка с информацией (причиной отмены)
     * 
     */
    
    @ApiOperation(value = "Отмена задачи (в т.ч. электронной очереди)")
    @RequestMapping(value = "/cancelTask", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/plain;charset=UTF-8")
    public
    @ResponseBody
    ResponseEntity<String> cancelTask(
            @ApiParam(value = "номер-ИД процесса (с контрольной суммой)", required = true )  @RequestParam(value = "nID_Order", required = true) Long nID_Order,
	    @ApiParam(value = "Строка с информацией (причиной отмены)", required = false )  @RequestParam(value = "sInfo", required = false) String sInfo
        )throws CommonServiceException, TaskAlreadyUnboundException {

        String sMessage = null;

        sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";
        try {
            oActionTaskService.cancelTasksInternal(nID_Order, sInfo);
            sMessage = "Ваша заявка відмінена. Ви можете подати нову на Порталі державних послуг iGov.org.ua.\n<br>"
                + "З повагою, команда порталу  iGov.org.ua";
            return new ResponseEntity<>(sMessage, HttpStatus.OK);
        } catch (CRCInvalidException e) {
            sMessage = "Вибачте, виникла помилка: Помилковий номер заявки!";
            CommonServiceException oCommonServiceException = new CommonServiceException("BUSINESS_ERR", e.getMessage(), e);
            oCommonServiceException.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn("Error: {}", e.getMessage());
            return new ResponseEntity<>(sMessage, HttpStatus.FORBIDDEN);
        } catch (RecordNotFoundException e) {
            sMessage = "Вибачте, виникла помилка: Заявка не знайдена!";
            CommonServiceException oCommonServiceException = new CommonServiceException("BUSINESS_ERR", e.getMessage(), e);
            oCommonServiceException.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn("Error: {}", e.getMessage());
            return new ResponseEntity<>(sMessage, HttpStatus.FORBIDDEN);
        } catch (CommonServiceException | TaskAlreadyUnboundException e) {
            CommonServiceException oCommonServiceException = new CommonServiceException("BUSINESS_ERR", e.getMessage(), e);
            oCommonServiceException.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn("Error: {}", e.getMessage(), e);
            return new ResponseEntity<>(sMessage, HttpStatus.FORBIDDEN);
        }

    }

    /**
     * @param nID_Task номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.
     */
    @ApiOperation(value = "Получение полей стартовой формы по ID таски", notes =  "##### Примеры:\n"
		+ "http://test.region.igov.org.ua/wf/service/action/task/getStartFormData?nID_Task=5170256\n"
		+ "Ответ, если запись существует (HTTP status Code: 200 OK):\n"
		+ "\n```json\n"
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
		+ "\n```\n"
		+ "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n\n"
		+ "\n```json\n"
		+ "{\n"
		+ "  \"code\": \"BUSINESS_ERR\",\n"
		+ "  \"message\": \"Record not found\"\n"
		+ "}\n"
		+ "\n```\n" )
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/getStartFormData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getFormDat( @ApiParam(value = " номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.", required = true )  @RequestParam(value = "nID_Task") String nID_Task)
            throws CommonServiceException, JsonProcessingException, RecordNotFoundException {

        return JSONValue.toJSONString(oActionTaskService.getStartFormData(Long.parseLong(nID_Task)));
    }

    /**
     * Удаление назначенного пользователя с задачи по ИД.
     * 
     * @param nID_UserTask номер-ИД задачи, для которой нужно удалить назначенного пользователя.
     */
    @ApiOperation(value = "Удаление назначенного пользователя с задачи по ИД.", notes = "#####  Request:\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/resetUserTaskAssign\n\n"
            + "- nID_UserTask=24\n"
            + "Response if task assigned: HTTP STATUS 200\n\n"
            + "\n```json\n"
            + "{}\n"
            + "\n```\n"
            + "Response if task is not assigned: HTTP STATUS 200\n\n"
            + "\n```json\n"
            + "{\"Not assigned UserTask\"}\n\n"
            + "\n```\n"
            + "Response if task not found: HTTP STATUS 403 Forbidden\n\n"
            + "\n```json\n"
            + "{\n"
            + "\"code\": \"BUSINESS_ERR\"\n"
            + "\"message\": \"Record not found\"\n"
            + "}"
            + "\n```\n")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Таска занята или же нет"), @ApiResponse(code = 403, message = "Запись о таске не найдена")})
    @RequestMapping(value = "/resetUserTaskAssign", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<String> resetUserTaskAssign(
            @ApiParam(value = "номер-ИД юзертаски", required = true) @RequestParam(value = "nID_UserTask", required = true) String nID_UserTask)
            throws CommonServiceException, RecordNotFoundException {
        return oActionTaskService.unclaimUserTask(nID_UserTask);
    }

    /**
     * Cервис получения данных по Таске
     *
     * @param nID_Task  номер-ИД таски (обязательный)
     * @param nID_Process  номер-ИД процесса (опциональный, но обязательный если не задан nID_Task и sID_Order)
     * @param sID_Order номер-ИД заявки (опциональный, но обязательный если не задан nID_Task и nID_Process)
     * @return сериализованный объект <br> <b>oProcess</b> {<br><kbd>sName</kbd> - название услуги (БП);<br> <kbd>sBP</kbd> - id-бизнес-процесса (БП);<br> <kbd>nID</kbd> - номер-ИД процесса;<br> <kbd>sDateCreate</kbd> - дата создания процесса<br>}
     */
    @ApiOperation(value = "Получение данных по таске", notes = "#####  ActionCommonTaskController: Сервис получения данных по таске #####\n\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getTaskData?nID_Task=nID_Task&sID_Order=sID_Order\n\n\n"
            + "Response:\n"
            + "\n```json\n"
            + "  {\n"
            + "    \"sName\":\"название услуги (БП)\"\n"
            + "    \"sBP\":\"id-бизнес-процесса (БП)\"\n"
            + "    \"nID\":\"номер-ИД процесса\"\n"
            + "    \"sDateCreate\":\"дата создания процесса\"\n"
            + "  }\n"
            + "\n```\n")
    @RequestMapping(value = "/getTaskData", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getTaskData(
            @ApiParam(value = "номер-ИД таски (обязательный)", required = true)
            @RequestParam(value = "nID_Task", required = true) Long nID_Task,
            @ApiParam(value = "номер-ИД процесса (опциональный, но обязательный если не задан nID_Task и sID_Order)", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "номер-ИД заявки (опциональный, но обязательный если не задан nID_Task и nID_Process)", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "", required = false) @RequestParam(value = "bIncludeGroups", required = false) Boolean bIncludeGroups)
            throws CRCInvalidException, CommonServiceException, RecordNotFoundException {

        if (nID_Task == null) {
            nID_Task = oActionTaskService.getTaskIDbyProcess(nID_Process, sID_Order, Boolean.FALSE);
        }
        if(sLogin != null){

        }
        Map<String, Object> response = new HashMap<>();

        response.put("oProcess", oActionTaskService.getProcessInfoByTaskID(nID_Task));
        response.put("aField", oActionTaskService.getTaskAllFields(nID_Task));
        if(bIncludeGroups.equals(Boolean.TRUE)){
            response.put("aGroups", oActionTaskService.getCandidateGroupByTaskID(nID_Task));
        }

        response.put("sStatusName", oActionTaskService.getTaskName(nID_Task));
        response.put("sID_Status", nID_Task);

        String sDateTimeCreate = JsonDateTimeSerializer.DATETIME_FORMATTER.print(
                oActionTaskService.getTaskDateTimeCreate(nID_Task).getTime()
        );
        response.put("sDateTimeCreate", sDateTimeCreate);

        return JsonRestUtils.toJsonResponse(response);
    }

    /*private static class TaskAlreadyUnboundException extends Exception {
        private TaskAlreadyUnboundException(String message) {
            super(message);
        }
    }*/

    
    
//@RequestMapping("/web")
//public class StartWebController {
    /*private final Logger LOG = LoggerFactory
            .getLogger(StartWebController.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private FormService formService;

    @RequestMapping(value = "/activiti/index", method = RequestMethod.GET)
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView("index");
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().latestVersion()
                .list();
        modelAndView.addObject("processList", processDefinitions);
        return modelAndView;
    }

    @RequestMapping(value = "/activiti/startForm/{id}", method = RequestMethod.GET)
    public ModelAndView startForm(@PathVariable("id") String id) {

        StartFormData sfd = formService.getStartFormData(id);

        List<FormProperty> fpList = sfd.getFormProperties();
        ModelAndView modelAndView = new ModelAndView("startForm");
        modelAndView.addObject("fpList", fpList);
        modelAndView.addObject("id", id);
        return modelAndView;
    }

    @RequestMapping(value = "/activiti/startProcess/{id}", method = RequestMethod.POST)
    public ModelAndView startProcess(@PathVariable("id") String id, @RequestParam Map<String, String> params) {
        ProcessInstance pi = formService.submitStartFormData(id, params);

        ModelAndView modelAndView = new ModelAndView("startedProcess");
        modelAndView.addObject("pi", pi.getProcessInstanceId());
        modelAndView.addObject("bk", pi.getBusinessKey());
        return modelAndView;
    }*/
/*
        private String getOriginalProcessInstanceId(Long nID_Protected) throws CRCInvalidException {
                return Long.toString(ToolLuna.getValidatedOriginalNumber(nID_Protected));
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
                        LOG.error(
                                String.format("Tasks for Process Instance [id = '%s'] not found", processInstanceID));
                        throw new RecordNotFoundException();
        }
                return tasks;
    }
*/
    /**
     * Запуск процесса Activiti:
     *
     * @param key         Ключ процесса
    //     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/start-process/{key}", method = RequestMethod.GET)
    @ApiOperation(value = "Запуск процесса Activiti", notes = "#####  ActionCommonTaskController: Запуск процесса Activiti #####\n\n"
            + "HTTP Context: https://server:port/wf/service/action/task/start-process/{key}\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/start-process/citizensRequest\n\n"
            + "Response\n"
            + "\n```json\n"
            + "  {\n"
            + "    \"id\":\"31\"\n"
            + "  }\n"
            + "\n```\n")
    @Transactional
    public
    @ResponseBody
    ProcessI startProcessByKey(
            @ApiParam(value = "Ключ процесса", required = true) @PathVariable("key") String key) {
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);
        if (pi == null || pi.getId() == null) {
            throw new IllegalArgumentException(String.format(
                    "process did not started by key:{%s}", key));
        }
        return new Process(pi.getProcessInstanceId());
    }

//    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
//    @ResponseBody
//    public List<String> getProcessTasks(@RequestParam String processInstanceId)
//            throws CRCInvalidException, CommonServiceException, RecordNotFoundException {
//        return getTasksByOrder(ToolLuna.getProtectedNumber(Long.valueOf(processInstanceId)));
//    }

    @RequestMapping(value = "/setVariable", method = RequestMethod.GET)
    public void setVariableToProcessInstance(
            @RequestParam String processInstanceId,
            @RequestParam String key,
            @RequestParam Object value) {
        runtimeService.setVariable(processInstanceId, key, value);
    }

    /**
     * Загрузка каталога сервисов из Activiti:
     *
     //     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
    @ApiOperation(value = "Загрузка каталога сервисов из Activiti", notes = "#####  ActionCommonTaskController: Загрузка каталога сервисов из Activiti #####\n\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/process-definitions\n\n"
            + "Response:\n\n"
            + "\n```json\n"
            + "  [\n"
            + "    {\n"
            + "      \"id\": \"CivilCardAccountlRequest:1:9\",\n"
            + "      \"category\": \"http://www.activiti.org/test\",\n"
            + "      \"name\": \"Видача картки обліку об’єкта торговельного призначення\",\n"
            + "      \"key\": \"CivilCardAccountlRequest\",\n"
            + "      \"description\": \"Описание процесса\",\n"
            + "      \"version\": 1,\n"
            + "      \"resourceName\": \"dnepr-2.bpmn\",\n"
            + "      \"deploymentId\": \"1\",\n"
            + "      \"diagramResourceName\": \"dnepr-2.CivilCardAccountlRequest.png\",\n"
            + "      \"tenantId\": \"diver\",\n"
            + "      \"suspended\": true\n"
            + "    }\n"
            + "  ]\n"
            + "\n```\n")
    @RequestMapping(value = "/process-definitions", method = RequestMethod.GET)
    @Transactional
    public
    @ResponseBody
    List<ProcDefinitionI> getProcessDefinitions() {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().latestVersion().list();
        List<ProcDefinitionI> procDefinitions = new ArrayList<>();
        ProcessDefinitionCover adapter = new ProcessDefinitionCover();
        for (ProcessDefinition processDefinition : processDefinitions) {
            procDefinitions.add(adapter.apply(processDefinition));
        }
        return procDefinitions;
    }

    @ApiOperation(value = "DeleteProcess", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/delete-process", method = RequestMethod.DELETE)
    public
    @ResponseBody
    void deleteProcess(@RequestParam(value = "nID_Order") Long nID_Order,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason    )
            throws Exception {

        oActionTaskService.deleteProcess(nID_Order, sLogin, sReason);
    }

    /**
     * Получение статистики по бизнес процессу за указанный период
     *
     * @param sID_BP_Name    - ИД бизнес процесса
     * @param dateAt         - дата начала периода выборки
     * @param dateTo         - дата окончания периода выборки
     * @param nRowStart      - позиция начальной строки для возврата (0 по умолчанию)
     * @param nRowsMax       - количество записей для возврата (1000 по умолчанию)
     * @param bDetail        - если да, то выгружать все поля тасок, иначе -- только
     *                       основные (по умолчанию да)
     * @param saFields       - вычисляемые поля (название поля -- формула, issue 907)
     * @param saFieldSummary - сведение полей, которое производится над выборкой
     *                       (issue 916)
     * @param httpResponse   - респонс, в который пишется ответ -- csv-файл
     * @throws java.io.IOException
     */
    @ApiOperation(value = "Получение статистики по задачам в рамках бизнес процесса", notes = "#####  ActionCommonTaskController: Получение статистики по задачам в рамках бизнес процесса #####\n\n"
	    + "HTTP Context: https://server:port/wf/service/action/task/download_bp_timing?sID_BP_Name=XXX&sDateAt=XXX8&sDateTo=XXX\n\n\n"
            + "Метод возвращает .csv файл со информацией о завершенных задачах в указанном бизнес процессе за период. Если указан параметр saFieldSummary -- "
            + "то также будет выполнено \"сведение\" полей (описано ниже). Если не указан, то формат выходного файла:\n\n"
            + "- nID_Process - ид задачи\n"
            + "- sLoginAssignee - кто выполнял задачу\n"
            + "- sDateTimeStart - Дата и время начала\n"
            + "- nDurationMS - Длительность выполнения задачи в миллисекундах\n"
            + "- nDurationHour - Длительность выполнения задачи в часах\n"
            + "- sName - Название задачи\n\n"
            + "Поля из FormProperty (если bDetail=true)\n"
            + "настраиваемые поля из saFields\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/action/task/download_bp_timing?sID_BP_Name=lviv_mvk-1&sDateAt=2015-06-28&sDateTo=2015-07-01\n\n"
            + "Пример выходного файла\n"
            + "\n```\n"
            + "\"Assignee\",\"Start Time\",\"Duration in millis\",\"Duration in hours\",\"Name of Task\"\n"
            + "\"kermit\",\"2015-06-21:09-20-40\",\"711231882\",\"197\",\"Підготовка відповіді на запит: пошук документа\"\n"
            + "\n```\n"
            + "Сведение полей\n"
            + "параметр saFieldSummary может содержать примерно такое значение: \"sRegion;nSum=sum(nMinutes);nVisites=count()\"\n"
            + "тот элемент, который задан первым в параметре saFieldSummary - является \"ключевым полем\" "
            + "следующие элементы состоят из названия для колонки, агрегирующей функции и названия агрегируемого поля. Например: \"nSum=sum(nMinutes)\"\n\n"
            + "где:\n\n"
            + "- nSum - название поля, куда будет попадать результат\n"
            + "- sum - оператор сведения\n"
            + "- nMinutes - расчетное поле переменная, которая хранит в себе значение уже существующего или посчитанного поля формируемой таблицы\n\n"
            + "Перечень поддерживаемых \"операторов сведения\":\n\n"
            + "- count() - число строк/элементов (не содержит аргументов)\n"
            + "- sum(field) - сумма чисел (содержит аргумент - название обрабатываемого поля)\n"
            + "- avg(field) - среднее число (содержит аргумент - название обрабатываемого поля)\n\n"
            + "Операторы можно указывать в произвольном регистре, т.е. SUM, sum и SuM \"распознаются\" как оператор суммы sum. \n"
            + "Для среднего числа также предусмотрено альтернативное название \"average\".\n"
            + "Если в скобках не указано поле, то берется ключевое.\n\n"
            + "Значение \"ключевого поля\" переносится в новую таблицу без изменений в виде единой строки,и все остальные сводные поля подсчитываются исключительно в контексте\n"
            + "значения этого ключевого поля, и проставляютя соседними полями в рамках этой единой строки.\n\n"
            + "Особенности подсчета:\n\n"
            + "- если нету исходных данных или нету такого ключевого поля, то ничего не считается (в исходном файле просто будут заголовки)\n"
            + "- если расчетного поля нету, то поле не считается (т.е. сумма и количество для ключевого не меняется)\n"
            + "тип поля Сумма и Среднее -- дробное число, Количество -- целое. Исходя из этого при подсчете суммы значение конвертируется в число, если конвертация неудачна, то "
            + "сумма не меняется. (т.е. если расчетное поле чисто текстовое, то сумма и среднее будет 0.0)\n\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/action/task/download_bp_timing?sID_BP_Name=_test_queue_cancel&sDateAt=2015-04-01&sDateTo=2015-10-31&saFieldSummary=email;nSum=sum(nDurationHour);nVisites=count();nAvg=avg(nDurationHour)\n\n"
            + "Ответ:\n"
            + "\n```\n"
            + "\"email\",\"nSum\",\"nVisites\",\"nAvg\"\n"
            + "\"email1\",\"362.0\",\"5\",\"72.4\"\n"
            + "\"email2\",\"0.0\",\"1\",\"0.0\"\n\n"
            + "\n```\n"
            + "Настраиваемые поля\n"
            + "Параметр saFields может содержать набор полей с выражениями, разделенными символом ; \n"
            + "Вычисленное выражение, расчитанное на основании значений текущей задачи, подставляется в выходной файл \n\n"
            + "Пример выражения \n"
            + "saFields=\"nCount=(sID_UserTask=='usertask1'?1:0);nTest=(sAssignedLogin=='kermit'?1:0)\" \n"
            + "где:\n\n"
            + "- nCount, nTest - названия колонок в выходном файле\n"
            + "- sID_UserTask, sAssignedLogin - ID таски в бизнес процессе и пользователь, на которого заассайнена таска, соответственно\n\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/action/task/download_bp_timing?sID_BP_Name=_test_queue_cancel&sDateAt=2015-04-01&sDateTo=2015-10-31&saFields=\"nCount=(sID_UserTask=='usertask1'?1:0);nTest=(sAssignedLogin=='kermit'?1:0)\"\n\n"
            + "Результат:\n"
            + "\n```\n"
            + "\"nID_Process\",\"sLoginAssignee\",\"sDateTimeStart\",\"nDurationMS\",\"nDurationHour\",\"sName\",\"bankIdPassport\",\"bankIdfirstName\",\"bankIdlastName\",\"bankIdmiddleName\",\"biometrical\",\"date_of_visit\",\"date_of_visit1\",\"email\",\"finish\",\"have_passport\",\"initiator\",\"phone\",\"urgent\",\"visitDate\",\"nCount\",\"nTest\"\n"
            + "\"5207501\",\"kermit\",\"2015-09-25:12-18-28\",\"1433990\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-10-09 09:00:00.00\",\"dd.MM.yyyy HH:MI\",\"nazarenkod1990@gmail.com\",\"attr1_ok\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27764,\"\"sDate\"\":\"\"2015-10-09 09:00:00.00\"\"}\",\"0.0\",\"1.0\"\n"
            + "\"5215001\",\"kermit\",\"2015-09-25:13-03-29\",\"75259\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-10-14 11:15:00.00\",\"dd.MM.yyyy HH:MI\",\"nazarenkod1990@gmail.com\",\"attr1_ok\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27767,\"\"sDate\"\":\"\"2015-10-14 11:15:00.00\"\"}\",\"0.0\",\"1.0\"\n"
            + "\"5215055\",\"dn200986zda\",\"2015-09-25:13-05-22\",\"1565056\",\"0\",\"обробка дмс\",\"АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002\",\"ДМИТРО\",\"ДУБІЛЕТ\",\"ОЛЕКСАНДРОВИЧ\",\"attr1_no\",\"2015-09-28 08:15:00.00\",\"dd.MM.yyyy HH:MI\",\"dmitrij.zabrudskij@privatbank.ua\",\"attr2_missed\",\"attr1_yes\",\"\",\"38\",\"attr1_no\",\"{\"\"nID_FlowSlotTicket\"\":27768,\"\"sDate\"\":\"\"2015-09-28 08:15:00.00\"\"}\",\"0.0\",\"0.0\"\n"
            + "\n```\n")
    @Deprecated
    @RequestMapping(value = "/download_bp_timing", method = RequestMethod.GET)
    @Transactional
    public void getTimingForBusinessProcessNew(
            @ApiParam(value = "ИД бизнес процесса", required = true) @RequestParam(value = "sID_BP_Name") String sID_BP_Name,
            @ApiParam(value = "дата начала периода выборки", required = false) @RequestParam(value = "sDateAt") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @ApiParam(value = "дата окончания периода выборки", required = false) @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @ApiParam(value = "позиция начальной строки для возврата (0 по умолчанию)", required = false) @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @ApiParam(value = "количество записей для возврата (1000 по умолчанию)", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @ApiParam(value = "если да, то выгружать все поля тасок, иначе -- только основные (по умолчанию да)", required = false) @RequestParam(value = "bDetail", required = false, defaultValue = "true") Boolean bDetail,
            @ApiParam(value = "сведение полей, которое производится над выборкой", required = false) @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            @ApiParam(value = "вычисляемые поля (название поля -- формула)", required = false) @RequestParam(value = "saFields", required = false) String saFields,
            HttpServletResponse httpResponse) throws IOException {

        if (sID_BP_Name == null || sID_BP_Name.isEmpty()) {
            LOG.error(String.format(
                    "Statistics for the business process '{%s}' not found.",
                    sID_BP_Name));
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business process '" + sID_BP_Name
                            + "' not found.", Process.class);
        }
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                "yyyy-MM-ddHH-mm-ss", Locale.ENGLISH);
        String fileName = sID_BP_Name + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".csv";
        LOG.debug("File name for statistics : {%s}", fileName);
        boolean isByFieldsSummary = saFieldSummary != null
                && !saFieldSummary.isEmpty();
        httpResponse.setContentType("text/csv;charset=UTF-8");
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + fileName);

        List<HistoricTaskInstance> foundResults = historyService
                .createHistoricTaskInstanceQuery().taskCompletedAfter(dateAt)
                .taskCompletedBefore(dateTo).processDefinitionKey(sID_BP_Name)
                .listPage(nRowStart, nRowsMax);

        List<String> headers = new ArrayList<>();
        String[] headersMainField = { "nID_Process", "sLoginAssignee",
                "sDateTimeStart", "nDurationMS", "nDurationHour", "sName" };
        headers.addAll(Arrays.asList(headersMainField));
        LOG.debug("(headers={})", headers);
        Set<String> headersExtra = oActionTaskService.findExtraHeaders(bDetail, foundResults,
                headers);
        if (saFields != null) {
            saFields = StringUtils.substringAfter(saFields, "\"");
            saFields = StringUtils.substringBeforeLast(saFields, "\"");
            String[] params = saFields.split(";");
            for (String header : params) {
                String cutHeader = StringUtils.substringBefore(header, "=");
                LOG.info("Adding header to the csv file from saFields: {}", cutHeader);
                headers.add(cutHeader);
            }
        }
        LOG.info("headers:{}", headers);

        CSVWriter csvWriter = new CSVWriter(httpResponse.getWriter());
        if (!isByFieldsSummary) {
            csvWriter.writeNext(headers.toArray(new String[headers.size()]));
        }
        List<Map<String, Object>> csvLines = new LinkedList<>();
        if (CollectionUtils.isNotEmpty(foundResults)) {
            LOG.debug(String
                    .format("Found {%s} completed tasks for business process {%s} for date period {%s} - {%s}",
                            foundResults.size(), sID_BP_Name,
                            DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
            for (HistoricTaskInstance currTask : foundResults) {
                Map<String, Object> csvLine = oActionTaskService.createCsvLine(bDetail
                        || isByFieldsSummary, headersExtra, currTask, saFields);
                String[] line = oActionTaskService.createStringArray(csvLine, headers);
                LOG.info("line: {}", csvLine);
                if (!isByFieldsSummary) {
                    csvWriter.writeNext(line);
                }
                csvLines.add(csvLine);
            }
        } else {
            LOG.debug(String
                    .format("No completed tasks found for business process {%s} for date period {%s} - {%s}",
                            sID_BP_Name, DATE_TIME_FORMAT.format(dateAt),
                            DATE_TIME_FORMAT.format(dateTo)));
        }
        if (isByFieldsSummary) { // issue 916
            LOG.info(">>>saFieldsSummary={}", saFieldSummary);
            try {
                List<List<String>> stringResults = new ToolCellSum()
                        .getFieldsSummary(csvLines, saFieldSummary);
                for (List<String> line : stringResults) {
                    csvWriter.writeNext(line.toArray(new String[line.size()]));
                }
            } catch (Exception e) {
                List<String> errorList = new LinkedList<>();
                errorList.add(e.getMessage());
                errorList.add(e.getCause() != null ? e.getCause().getMessage()
                        : "");
                csvWriter.writeNext(errorList.toArray(new String[errorList
                        .size()]));
                LOG.error("Error: {}", e.getMessage());
                LOG.trace("FAIL:", e);
            }
            LOG.info(">>>>csv for saFieldSummary is complete.");
        }
        csvWriter.close();
    }

    /**
     * Download information about the tasks in csv format
     *
     * @param sID_BP            business process name
     * @param sID_State_BP      task state id
     * @param saFields          field of the tasks to download. Separated by comma
     * @param nASCI_Spliter     splitter of the fields
     * @param sID_Codepage      encoding for the file
     * @param sDateCreateFormat format for sDateCreate
     * @param dateAt            start date for the filter
     * @param dateTo            end date for the filter
     * @param nRowStart         start row for paging
     * @param nRowsMax          maximal amount of row for paging
     * @param bIncludeHistory   to include historic task instances. default value
     *                          is true
     * @param saFieldsCalc      list of calculated fields
     * @param saFieldSummary    parap to specify aggregated fields
     * @param httpResponse      http responce wrapper
     * @throws IOException in case of connection aborted with client
     *                     <p/>
     *                     example: https://test.region.igov.org.ua/wf/service/action/task/
     *                     downloadTasksData ?sID_BP=kiev_mreo_1&sDateAt=2015-06-28&sDateTo
     *                     =2015-08-01&nASCI_Spliter =59&sID_Codepage=UTF8&saFields=nID_Task
     *                     ;bankIdPassport;bankIdlastName
     *                     ;bankIdfirstName;bankIdmiddleName;1;sDateCreate
     */
    @ApiOperation(value = "Загрузка данных по задачам", notes = "#####  ActionCommonTaskController: Загрузка данных по задачам #####\n\n"
            + "HTTP Context: https://server:port/wf/service/action/task/downloadTasksData\n\n\n"
            + "Загрузка полей по задачам в виде файла.\n\n"
            + "Поля по умолчанию, которые всегда включены в выборку:\n"
            + "- nID_Task - \"id таски\"\n"
            + "- sDateCreate - \"дата создания таски\" (в формате sDateCreateFormat)\n\n"
            + "Особенности обработки полей:\n"
            + "- Если тип поля enum, то брать не его ИД пункта в энуме а именно значение Если тип поля enum, и в значении присутствует знак \";\", то брать только то ту часть текста, которая находится справа от этого знака\n\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/action/task/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-08-01&saFields=${nID_Task};${sDateCreate};${area};;;0;${bankIdlastName} ${bankIdfirstName} ${bankIdmiddleName};4;${aim};${date_start};${date_stop};${place_living};${bankIdPassport};1;${phone};${email}&sID_Codepage=win1251&nASCI_Spliter=18&sDateCreateFormat=dd.mm.yyyy hh:MM:ss&sFileName=dohody.dat\n\n"
            + "Пример ответа:\n"
            + "\n```\n"
            + "1410042;16.32.2015 10:07:17;АНД (пров. Універсальний, 12);;;0;БІЛЯВЦЕВ ВОЛОДИМИР ВОЛОДИМИРОВИЧ;4;мета;16/07/2015;17/07/2015;мокешрмшгкеу;АЕ432204 БАБУШКИНСКИМ РО ДГУ УМВД 26.09.1996;1;380102030405;mendeleev.ua@gmail.com\n"
            + "995161;07.07.2015 05:07:27;;;;0;ДУБІЛЕТ ДМИТРО ОЛЕКСАНДРОВИЧ;4;для роботи;01/07/2015;07/07/2015;Дніпропетровська, Дніпропетровськ, вул. Донецьке шосе, 15/110;АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЙНИ В ДНИПРОПЕТРОВСЬКИЙ ОБЛАСТИ 18.03.2002;1;;ukr_rybak@rambler.ru\n"
            + "\n```\n"
            + "Формат поля saFieldsCalc - смотри сервис https://github.com/e-government-ua/i/blob/test/docs/specification.md#16-Получение-статистики-по-задачам-в-рамках-бизнес-процесса и параметр saFields\n"
            + "Пример запроса: https://test.region.igov.org.ua/wf/service/action/task/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&bHeader=true&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-10-01&saFieldsCalc=%22nCount=(sID_UserTask==%27usertask1%27?1:0);nTest=(sAssignedLogin==%27kermit%27?1:0)%22\n\n"
            + "Пример ответа (фрагмент):\n"
            + "\n```\n"
            + ";380970044803;ДМИТРО;;ОЛЕКСАНДРОВИЧ;;dd.MM.yyyy;Днепропетровск;;;3119325858;АМ765369 ЖОВТНЕВИМ РВ ДМУ УМВС УКРАЇНИ В ДНІПРОПЕТРОВСЬКІЙ ОБЛАСТІ 18.03.2002;0463;dd.MM.yyyy;;тест;;ДУБІЛЕТ;vidokgulich@gmail.com;1.0;1.0\n"
            + "\n```\n"
            + "Формат поля saFieldSummary - смотри сервис https://github.com/e-government-ua/i/blob/test/docs/specification.md#16-Получение-статистики-по-задачам-в-рамках-бизнес-процесса и параметр saFieldSummary\n"
            + "Пример запроса: https://test.region.igov.org.ua/wf/service/action/task/downloadTasksData?&sID_BP=dnepr_spravka_o_doxodax&bHeader=true&sID_State_BP=usertask1&sDateAt=2015-06-01&sDateTo=2015-10-01&saFieldSummary=email;nVisites=count()\n\n"
            + "Пример ответа:\n"
            + "\n```\n"
            + "vidokgulich@gmail.com;2\n"
            + "kermit;1\n"
            + "rostislav.siryk@gmail.com;4\n"
            + "rostislav.siryk+igov.org.ua@gmail.com;3\n"
            + "\n```\n")
    @RequestMapping(value = "/downloadTasksData", method = RequestMethod.GET)
    @Transactional
    public void downloadTasksData(
            @ApiParam(value = "название бизнесс процесса", required = true) @RequestParam(value = "sID_BP", required = true) String sID_BP,
            @ApiParam(value = "состояние задачи, по умолчанию исключается из фильтра Берется из поля taskDefinitionKey задачи", required = false) @RequestParam(value = "sID_State_BP", required = false) String sID_State_BP,
            @ApiParam(value = "имена полей для выборкы разделенных через ';', чтобы добавить все поля можно использовать - '*' или не передевать параметр в запросе. Поле также может содержать названия колонок. Например, saFields=Passport\\=${passport};{email}", required = false) @RequestParam(value = "saFields", required = false, defaultValue = "*") String saFields,
            @ApiParam(value = "ASCII код для разделителя", required = false) @RequestParam(value = "nASCI_Spliter", required = false) String nASCI_Spliter,
            @ApiParam(value = "имя исходящего файла, по умолчанию - data_BP-bpName_.txt\"", required = false) @RequestParam(value = "sFileName", required = false) String fileName,
            @ApiParam(value = "кодировка исходящего файла, по умолчанию - win1251", required = false) @RequestParam(value = "sID_Codepage", required = false, defaultValue = "win1251") String sID_Codepage,
            @ApiParam(value = "форматирование даты создания таски, по умолчанию - yyyy-MM-dd HH:mm:ss", required = false) @RequestParam(value = "sDateCreateFormat", required = false, defaultValue = "yyyy-MM-dd HH:mm:ss") String sDateCreateFormat,
            @ApiParam(value = "начальная дата создания таски, по умолчанию - вчера", required = false) @RequestParam(value = "sDateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateAt,
            @ApiParam(value = "конечная дата создания таски, по умолчанию - сегодня", required = false) @RequestParam(value = "sDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            @ApiParam(value = "начало выборки для пейджирования, по умолчанию - 0", required = false) @RequestParam(value = "nRowStart", required = false, defaultValue = "0") Integer nRowStart,
            @ApiParam(value = "размер выборки для пейджирования, по умолчанию - 1000", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "1000") Integer nRowsMax,
            @ApiParam(value = "включить информацию по хисторик задачам, по умолчанию - true", required = false) @RequestParam(value = "bIncludeHistory", required = false, defaultValue = "true") Boolean bIncludeHistory,
            @ApiParam(value = "добавить заголовок с названиями полей в выходной файл, по умолчанию - false", required = false) @RequestParam(value = "bHeader", required = false, defaultValue = "false") Boolean bHeader,
            @ApiParam(value = "настраиваемые поля (название поля -- формула, issue 907", required = false) @RequestParam(value = "saFieldsCalc", required = false) String saFieldsCalc,
            @ApiParam(value = "сведение полей, которое производится над выборкой (issue 916)", required = false) @RequestParam(value = "saFieldSummary", required = false) String saFieldSummary,
            HttpServletResponse httpResponse) throws IOException {

//      'sID_State_BP': '',//'usertask1'
//      'saFieldsCalc': '', // поля для калькуляций
//      'saFieldSummary': '' // поля для агрегатов
        if("".equalsIgnoreCase(sID_State_BP) || "null".equalsIgnoreCase(sID_State_BP)){
            sID_State_BP=null;
        }
        if("".equalsIgnoreCase(saFieldsCalc) || "null".equalsIgnoreCase(saFieldsCalc)){
            saFieldsCalc=null;
        }
        if("".equalsIgnoreCase(saFieldSummary) || "null".equalsIgnoreCase(saFieldSummary)){
            saFieldSummary=null;
        }

        // 1. validation
        if (StringUtils.isBlank(sID_BP)) {
            LOG.error("Wrong name of business task - {}", sID_BP);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task '" + sID_BP
                            + "' not found. Wrong BP name.", Task.class);
        }

        Date dBeginDate = oActionTaskService.getBeginDate(dateAt);
        Date dEndDate = oActionTaskService.getEndDate(dateTo);
        String separator = oActionTaskService.getSeparator(sID_BP, nASCI_Spliter);
        Charset charset = oActionTaskService.getCharset(sID_Codepage);

        // 2. query
        TaskQuery query = taskService.createTaskQuery()
                .processDefinitionKey(sID_BP).taskCreatedAfter(dBeginDate)
                .taskCreatedBefore(dEndDate);
        HistoricTaskInstanceQuery historicQuery = historyService
                .createHistoricTaskInstanceQuery()
                .processDefinitionKey(sID_BP).taskCreatedAfter(dBeginDate)
                .taskCreatedBefore(dEndDate).includeProcessVariables();
        if (sID_State_BP != null) {
            historicQuery.taskDefinitionKey(sID_State_BP);
        }
        List<HistoricTaskInstance> foundHistoricResults = historicQuery
                .listPage(nRowStart, nRowsMax);

        String header = oActionTaskService.formHeader(saFields, foundHistoricResults, saFieldsCalc);
        String[] headers = header.split(";");

        saFields = oActionTaskService.processSaFields(saFields, foundHistoricResults);

        if (sID_State_BP != null) {
            query = query.taskDefinitionKey(sID_State_BP);
        }
        List<Task> foundResults = query.listPage(nRowStart, nRowsMax);

        // 3. response
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                "yyyy-MM-ddHH-mm-ss", Locale.ENGLISH);
        String sTaskDataFileName = fileName != null ? fileName : "data_BP-"
                + sID_BP + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".txt";

        SimpleDateFormat sDateCreateDF = new SimpleDateFormat(
                sDateCreateFormat, Locale.ENGLISH);

        LOG.debug("File name to return statistics : {}", sTaskDataFileName);

        httpResponse.setContentType("text/csv;charset=" + charset.name());
        httpResponse.setHeader("Content-disposition", "attachment; filename="
                + sTaskDataFileName);

        CSVWriter printWriter = new CSVWriter(httpResponse.getWriter(), separator.charAt(0),
                CSVWriter.NO_QUOTE_CHARACTER);

        List<Map<String, Object>> csvLines = new LinkedList<>();

        if (bHeader && header != null && saFieldSummary == null) {
            printWriter.writeNext(headers);
        }

        oActionTaskService.fillTheCSVMap(sID_BP, dBeginDate, dEndDate, foundResults, sDateCreateDF,
                csvLines, saFields, saFieldsCalc, headers);
        if (Boolean.TRUE.equals(bIncludeHistory)) {
            Set<String> tasksIdToExclude = new HashSet<>();
            for (Task task : foundResults) {
                tasksIdToExclude.add(task.getId());
            }
            oActionTaskService.fillTheCSVMapHistoricTasks(sID_BP, dBeginDate, dEndDate,
                    foundHistoricResults, sDateCreateDF, csvLines, saFields,
                    tasksIdToExclude, saFieldsCalc, headers);
        }

        if (saFieldSummary != null) {
            LOG.info(">>>saFieldsSummary={}", saFieldSummary);
            try {
                List<List<String>> stringResults = new ToolCellSum()
                        .getFieldsSummary(csvLines, saFieldSummary);
                for (int i = 0; i < stringResults.size(); i++) {
                    if (i == 0 && !bHeader)
                        continue;
                    List<String> line = stringResults.get(i);
                    printWriter.writeNext(line.toArray(new String[line.size()]));
                }
            } catch (Exception e) {
                List<String> errorList = new LinkedList<>();
                errorList.add(e.getMessage());
                errorList.add(e.getCause() != null ? e.getCause().getMessage()
                        : "");
                printWriter.writeNext(errorList.toArray(new String[errorList
                        .size()]));
                LOG.error("Error: {}", e.getMessage());
                LOG.trace("FAIL:", e);
            }
            LOG.info(">>>>csv for saFieldSummary is complete.");
        } else {
            for (Map<String, Object> currLine : csvLines) {
                String[] line = oActionTaskService.createStringArray(currLine, Arrays.asList(headers));
                printWriter.writeNext(line);
            }
        }

        printWriter.close();
    }

    /**
     * Returns business processes which belong to a specified user
     *
     * @param sLogin - login of user in user activity
     */
    @ApiOperation(value = "Получение списка бизнес процессов к которым у пользователя есть доступ", notes = "#####  ActionCommonTaskController: Получение списка бизнес процессов к которым у пользователя есть доступ #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/getLoginBPs?sLogin=userId\n\n"
            + "Метод возвращает json со списком бизнес процессов, к которым у пользователя есть доступ, в формате:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sID\": \"[process definition key]\"\"sName\": \"[process definition name]\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"[process definition key]\"\"sName\": \"[process definition name]\"\n"
            + "  }\n"
            + "]\n"
            + "\n```\n"
            + "Принадлежность пользователя к процессу проверяется по вхождению в группы, которые могут запускать usertask-и внутри процесса, или по вхождению в группу, которая может стартовать процесс\n\n"
            + "Пример:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getLoginBPs?sLogin=kermit\n"
            + "Пример результата\n"
            + "\n```json\n"
            + "[\n"
            + "{\n"
            + "    \"sID\": \"dnepr_spravka_o_doxodax\",\n"
            + "    \"sName\": \"Дніпропетровськ - Отримання довідки про доходи фіз. осіб\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"dnepr_subsidies2\",\n"
            + "    \"sName\": \"Отримання субсидії на оплату житлово-комунальних послуг2\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"khmelnitskij_mvk_2\",\n"
            + "    \"sName\": \"Хмельницький - Надання інформації, що підтверджує відсутність (наявність) земельної ділянки\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"khmelnitskij_zemlya\",\n"
            + "    \"sName\": \"Заява про наявність земельної ділянки\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"kiev_spravka_o_doxodax\",\n"
            + "    \"sName\": \"Київ - Отримання довідки про доходи фіз. осіб\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"kuznetsovsk_mvk_5\",\n"
            + "    \"sName\": \"Кузнецовськ МВК - Узгодження графіка роботи підприємства торгівлі\\/обслуговування\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"post_spravka_o_doxodax_pens\",\n"
            + "    \"sName\": \"Отримання довідки про доходи (пенсійний фонд)\"\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getLoginBPs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public
    @ResponseBody
    String getBusinessProcessesForUser(
            @ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin") String sLogin)
            throws IOException {

        String jsonRes = JSONValue.toJSONString(oActionTaskService.getBusinessProcessesForUser(sLogin));
        LOG.info("Result: {}", jsonRes);
        return jsonRes;
    }

    /**
     * issue 808. сервис ЗАПРОСА полей, требующих уточнения, c отсылкой
     * уведомления гражданину
     *
//     * @param sID_Order - строка-ид заявки
     * @param nID_Process
     * @param saField       -- строка-массива полей (например:
     *                      "[{'id':'sFamily','type':'string','value':'Белявский'},{'id':'nAge','type':'long'}]"
     *                      )
//     * @param nID_Process - ид заявки
     * @param sMail         -- строка электронного адреса гражданина
//     * @param nID_Server - ид сервера
     * @param sHead         -- строка заголовка письма //опциональный (если не задан, то
     *                      "Необходимо уточнить данные")
     * @param sBody         -- строка тела письма //опциональный (если не задан, то
     *                      пустота)
     * @throws CommonServiceException
     * @throws CRCInvalidException
     */
    @ApiOperation(value = "Вызов сервиса уточнения полей формы", notes = "#####  ActionCommonTaskController: Вызов сервиса уточнения полей формы #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/setTaskQuestions?nID_Protected=nID_Protected&saField=saField&sMail=sMail\n\n\n"
	    + "сервис запроса полей, требующих уточнения у гражданина, с отсылкой уведомления параметры:\n\n\n"
            + "при вызове сервиса:\n\n"
            + "- обновляется запись HistoryEvent_Service полем значениями из soData (из saField), sToken (сгенерированый случайно 20-ти символьный код), sHead, sBody (т.е. на этоп этапе могут быть ошибки, связанные с нахождением и апдейтом обьекта события по услуге)\n"
            + "- отсылается письмо гражданину на указанный емейл (sMail):\n"
            + "  с заголовком sHead,\n"
            + "  телом sBody\n"
            + "  перечисление полей из saField в формате таблицы: Поле / Тип / Текущее значение\n"
            + "  гиперссылкой в конце типа: https://[hostCentral]/order?nID_Protected=[nID_Protected]&sToken=[sToken]\n"
            + "- находитcя на региональном портале таска, которой устанавливается в глобальную переменные sQuestion содержимое sBody и saFieldQuestion - содержимое saField\n"
            + "- сохраняется информация о действии в Моем Журнале в виде\n"
            + "  По заявці №____ задане прохання уточнення: [sBody]\n"
            + "  плюс перечисление полей из saField в формате таблицы Поле / Тип / Текущее значение\n"
            + "- Пример: https://test.region.igov.org.ua/wf/service/action/task/setTaskQuestions?nID_Protected=52302969&saField=[{'id':'bankIdfirstName','type':'string','value':'3119325858'}]&sMail=test@email\n\n"
            + "Ответы: Пустой ответ в случае успешного обновления (и приход на указанный емейл письма описанного выше формата)\n\n"
            + "Возможные ошибки:\n\n"
            + "- не найдена заявка (Record not found) или ид заявки неверное (CRC Error)\n"
            + "- связанные с отсылкой письма, например, невалидный емейл (Error happened when sending email)\n"
            + "- из-за некорректных входящих данных, например неверный формат saField (пример ошибки: Expected a ',' or ']' at 72 [character 73 line 1])")
    @RequestMapping(value = "/setTaskQuestions", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskQuestions(
            @ApiParam(value = "номер-ИД процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process,
            @ApiParam(value = "строка-массива полей", required = true) @RequestParam(value = "saField") String saField,
            @ApiParam(value = "строка-массива параметров", required = true) @RequestParam(value = "saField") String soParams,
            @ApiParam(value = "строка электронного адреса гражданина", required = true) @RequestParam(value = "sMail") String sMail,
            @ApiParam(value = "строка заголовка письма", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения-коммента (общего)", required = false) @RequestParam(value = "sBody", required = false) String sBody)
            throws CommonServiceException, CRCInvalidException {

        String sToken = Tool.getGeneratedToken();
        try {
            String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
            String sReturn = oActionTaskService.updateHistoryEvent_Service(
                    HistoryEvent_Service_StatusType.OPENED_REMARK_EMPLOYEE_QUESTION,
                    sID_Order,
                    saField,
                    "Необхідно уточнити дані" + (sBody == null ? "" : ", за коментарем: " + sO(sBody)), sToken, null);
            LOG.info("(sReturn={})", sReturn);
            //oActionTaskService.setInfo_ToActiviti("" + nID_Process, saField, sBody);
            //createSetTaskQuestionsMessage(sID_Order, sO(sBody), saField);//issue 1042
            oNotificationPatterns.sendTaskEmployeeQuestionEmail(sHead, sO(sBody), sMail, sToken, nID_Process, saField, soParams);
        } catch (Exception e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Can't make task question: " + e.getMessage() , e,
                    HttpStatus.FORBIDDEN);
        }
    }

    /*private void createSetTaskQuestionsMessage(String sID_Order, String sBody, String saData) throws Exception {
        Map<String, String> params = new HashMap<>();
        if (sBody != null && !sBody.isEmpty()) {
            params.put("sBody", sBody);
        }
        params.put("sData", saData);
        params.put("nID_SubjectMessageType", "" + 5L);
        params.put("sID_Order", sID_Order);
        LOG.info("try to save service message with params {}", params);
        String jsonResponse = historyEventService.addServiceMessage(params);
        LOG.info("(jsonResponse={})", jsonResponse);
    }*/

    @ApiOperation(value = "Вызов сервиса ответа по полям требующим уточнения", notes = "#####  ActionCommonTaskController: Вызов сервиса ответа по полям требующим уточнения #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/setTaskAnswer?nID_Protected=nID_Protected&saField=saField&sToken=sToken&sBody=sBody\n\n\n"
            + "- обновляет поля формы указанного процесса значениями, переданными в параметре saField Важно:позволяет обновлять только те поля, для которых в форме бизнес процесса не стоит атрибут writable=\"false\"\n\n"
            + "Во время выполнения метод выполняет такие действия:\n\n"
            + "- Находит в сущности HistoryEvent_Service нужную запись (по nID_Protected) и сверяет токен. Eсли токен в сущности указан но не совпадает с переданным, возвращается ошибка \"Token wrong\". Если он в сущности не указан (null) - возвращается ошибка \"Token absent\".\n"
            + "- Находит на региональном портале таску и устанавливает в глобальную переменную sAnswer найденной таски содержимое sBody.\n"
            + "- Устанавливает в каждое из полей из saField новые значения\n"
            + "- Обновляет в сущности HistoryEvent_Service поле soData значением из saField и поле sToken значением null.\n"
            + "- Сохраняет информацию о действии в Мой Журнал (Текст: На заявку №____ дан ответ гражданином: [sBody])\n\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/setTaskAnswer?nID_Protected=54352839&saField=[{%27id%27:%27bankIdinn%27,%27type%27:%27string%27,%27value%27:%271234567890%27}]&sToken=93ODp4uPBb5To4Nn3kY1\n\n"
            + "Ответы: Пустой ответ в случае успешного обновления\n\n"
            + "Токен отсутствует\n\n"
            + "\n```json\n"
            + "{\"code\":\"BUSINESS_ERR\",\"message\":\"Token is absent\"}\n\n"
            + "\n```\n"
            + "Токен не совпадает со значением в HistoryEvent_Service\n"
            + "\n```json\n"
            + "{\"code\":\"BUSINESS_ERR\",\"message\":\"Token is absent\"}\n\n"
            + "\n```\n"
            + "Попытка обновить поле с атрибутом writable=\"false\"\n"
            + "\n```json\n"
            + "{\"code\":\"BUSINESS_ERR\",\"message\":\"form property 'bankIdinn' is not writable\"}\n"
            + "\n```\n")
    @RequestMapping(value = "/setTaskAnswer", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskAnswer_Region(
            @ApiParam(value = "номер-ИД процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process,
            @ApiParam(value = "saField - строка-массива полей", required = true) @RequestParam(value = "saField") String saField
            //, @ApiParam(value = "строка тела сообщения (опциональный параметр)", required = false) @RequestParam(value = "sBody", required = false) String sBody
        ) throws CommonServiceException {

        try {
            //String processInstanceID = "" + nID_Order;
            //String snID_Process = nID_Process+"";
            JSONObject oFields = new JSONObject("{ soData:" + saField + "}");
            JSONArray aField = oFields.getJSONArray("soData");
            List<Task> aTask = taskService.createTaskQuery().processInstanceId(nID_Process+"").list();

//            runtimeService.setVariable(processInstanceID, "sAnswer", sBody);
//            LOG.info("Added variable sAnswer to the process {}", snID_Process);

            //LOG.info("Found {} tasks by nID_Protected... ", aTask.size());
            for (Task oTask : aTask) {
                //LOG.info("oTask: (getName()={},getDescription()={},getId()={})", oTask.getName(), oTask.getDescription(), oTask.getId());
                TaskFormData oTaskFormData = formService.getTaskFormData(oTask.getId());
                Map<String, String> mField = new HashMap<>();
                for (FormProperty oFormProperty : oTaskFormData.getFormProperties()) {
                    if (oFormProperty.isWritable()) {
                        mField.put(oFormProperty.getId(), oFormProperty.getValue());
                    }
                }
                /*
                sID: item.id,
                sName: item.name,
                sType: item.type,
                sValue: item.value,
                sValueNew: item.value,
                sNotify: $scope.clarifyFields[item.id].text
                */
                for (int i = 0; i < aField.length(); i++) {
                    JSONObject oField = aField.getJSONObject(i);
                    String sID = (String) oField.get("sID");
                    if(sID==null){
                        sID = (String) oField.get("id");
                    }
                    String sValueOld = (String) oField.get("sValue");
                    String sValueNew = (String) oField.get("sValueNew");
                    mField.put(sID, sValueNew);
                    LOG.info("Set variable sID={} with sValueNew={} (sValueOld={})", sID, sValueNew, sValueOld);
                }
                //LOG.info("Updating form data for the task {}|{}", oTask.getId(), mField);
                LOG.info("oTask: (getName()={},getDescription()={},getId()={},mField={})", oTask.getName(), oTask.getDescription(), oTask.getId(),mField);
                formService.saveFormData(oTask.getId(), mField);
            }
            //LOG.info("....ok!");
        } catch (Exception e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e, HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "SendProccessToGRES", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/sendProccessToGRES", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> sendProccessToGRES(@ApiParam(value = "номер-ИД задачи", required = true) @RequestParam(value = "nID_Task") Long nID_Task)
            throws CommonServiceException {

        return oActionTaskService.sendProccessToGRESInternal(nID_Task);
        
    }

    @ApiOperation(value = "GetTaskFormData", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/getTaskFormData", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, String> getTaskFormData(@ApiParam(value = "номер-ИД задачи", required = true) @RequestParam(value = "nID_Task") Long nID_Task) throws CommonServiceException {

        return oActionTaskService.getTaskFormDataInternal(nID_Task);
    }

    /**
     * issue 808. сервис ЗАПРОСА полей, требующих уточнения, c отсылкой
     * уведомления гражданину
     *
//     * @param sID_Order - строка-ид заявки
     * @param nID_Process - номер-ИД процесса
     * @return массив сообщений (строка JSON)
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Получение сообщений по заявке", notes = "")
    @RequestMapping(value = "/getOrderMessages_Local", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public
    @ResponseBody
    String getOrderMessages_Local( //ResponseEntity
            @ApiParam(value = "номер-ИД процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process
        ) throws CommonServiceException {
        try {
            String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
            Map<String, String> params = new HashMap<>();
            params.put("sID_Order", sID_Order);
            String soResponse = "";
            String sURL = generalConfig.sHostCentral() + "/wf/service/subject/message/getServiceMessages";
            soResponse = httpRequester.getInside(sURL, params);
            LOG.info("(soResponse={})", soResponse);
            //public static ResponseEntity<String> toJsonResponse(Object res) {
            //return toJsonResponse(HttpStatus.OK, soResponse);
            return soResponse;
            /*String historyEventServiceJson = oActionTaskService.updateHistoryEvent_Service(
                    sID_Order,
                    saField,
                    sHead, sBody, sToken, "Запит на уточнення даних");
            LOG.info("....ok! successfully update historyEvent_service! event= {}", historyEventServiceJson);*/
            //oActionTaskService.setInfo_ToActiviti("" + nID_Process, saField, sBody);
            //createSetTaskQuestionsMessage(sID_Order, sBody, saField);//issue 1042
        } catch (Exception oException) {
            LOG.error("Can't get: {}", oException.getMessage());
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Can't get: " + oException.getMessage(), oException, HttpStatus.FORBIDDEN);
        }
    }    
    
    @ApiOperation(value = "getTasks", notes = "#####  ActionCommonTaskController: Получение списка всех тасок, которые могут быть доступны указанному логину #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/getTasks?sLogin=[sLogin]\n\n\n"
            + "- Возвращает список всех тасок, которые могут быть доступны указанному логину [sLogin] и которые уже заняты другими логинами, входящими во все те-же группы, в которые входит данный логин\n\n"
            + "Во время выполнения производит поиск групп, в которые входит указанный пользователь, и затем возвращает список задач, которые могут быть "
			+ " заассайнены на пользователей из полученных групп:\n\n"
            + "Содержит следующие параметры:\n"
            + "- sLogin - id пользователя. обязательный параметр, указывающий пользователя\n"
            + "- bIncludeAlienAssignedTasks - необязательный параметр (по умолчанию false). Если значение false - то возвращать только свои и только не ассайнутые, к которым доступ.\n"
			+ "- sOrderBy - метод сортировки задач. Необязательный параметр. По умолчанию 'id'. Допустимые значения 'id', 'taskCreateTime', 'ticketCreateDate'\n"
			+ "- nSize - Количество задач в результате. Необязательный параметр. По умолчанию 10.\n"
			+ "- nStart - Порядковый номер первой задачи для возвращения. Необязательный параметр. По умолчанию 0\n"
			+ "- sFilterStatus - Необязательный параметр (по умолчанию обрабатывается как OpenedUnassigned). статус фильтрации задач, у которого возможные значения: OpenedUnassigned (только не-ассайнутые), "
			+ " OpenedAssigned(только ассайнутые), Opened(только открытые (не в истории)), Closed(только закрытые (история))\n"
			+ "- bFilterHasTicket - Необязательный параметр (по умолчанию false). Если true - возвращать только те задачи, у которых есть связанный тикет\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getTasks?sLogin=kermit\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getTasks?sLogin=kermit&nSize=15&nStart=10\n\n")
    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getTasks(@ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin") String sLogin,
    		@ApiParam(value = "bIncludeAlienAssignedTasks", required = true) @RequestParam(value = "bIncludeAlienAssignedTasks", defaultValue="false", required=false) boolean bIncludeAlienAssignedTasks,
    		@ApiParam(value = "sOrderBy", required = false) @RequestParam(value = "sOrderBy", defaultValue="id", required=false) String sOrderBy,
    		@ApiParam(value = "nSize", required = false) @RequestParam(value = "nSize", defaultValue="10", required=false) Integer nSize,
    		@ApiParam(value = "nStart", required = false) @RequestParam(value = "nStart", defaultValue="0", required=false) Integer nStart,
    		@ApiParam(value = "sFilterStatus", required = false) @RequestParam(value = "sFilterStatus", defaultValue="OpenedUnassigned", required=false) String sFilterStatus,
    		@ApiParam(value = "bFilterHasTicket", required = false) @RequestParam(value = "bFilterHasTicket", defaultValue="false", required=false) boolean bFilterHasTicket) throws CommonServiceException {

    	Map<String, Object> res = new HashMap<String, Object>();
    	
    	try {
        List<Group> groups = identityService.createGroupQuery().groupMember(sLogin).list();
        
			if (groups != null && !groups.isEmpty()) {
				List<String> groupsIds = new LinkedList<String>();
				for (Group group : groups) {
					groupsIds.add(group.getId());
				}
				LOG.info("Got list of groups for current user {} : {}", sLogin, groupsIds);

				Map<String, FlowSlotTicket> mapOfTickets = new HashMap<String, FlowSlotTicket>();
				long totalNumber = 0;
				Object taskQuery = createQuery(sLogin, bIncludeAlienAssignedTasks, sOrderBy, sFilterStatus,
						groupsIds);
				
				totalNumber = (taskQuery instanceof TaskInfoQuery) ? ((TaskInfoQuery)taskQuery).count() : getCountOfTasks(groupsIds);
				LOG.info("Total number of tasks:{}", totalNumber);
				int nStartBunch = nStart;
				List<TaskInfo> tasks = new LinkedList<TaskInfo>();
				
				// this while is intended to work until we either pass through all the tasks or select needed number of tickets
				long sizeOfTasksToSelect = nSize;
				if (bFilterHasTicket){
					// in case bFilterHasTicket tasks can be shifted. So we have select tasks from 0 and select tasks from nStart after that
					sizeOfTasksToSelect = totalNumber;
					nStartBunch = 0;
				}
				while ((tasks.size() < sizeOfTasksToSelect) && (nStartBunch < totalNumber)){
					LOG.info("Populating response with results. nStartFrom:{} nSize:{}", nStartBunch, nSize);
					List<TaskInfo> currTasks = getTasksWithTicketsFromQuery(taskQuery, nStartBunch, nSize, bFilterHasTicket, mapOfTickets);
					tasks.addAll(currTasks);
					
					nStartBunch += nSize;
					
					if (!bFilterHasTicket){
						break;
					}
				}
				
				int tasksSize = tasks.size();
				if (bFilterHasTicket){
					totalNumber = tasksSize;
					if (tasksSize > nStart && tasksSize > (nStart + nSize)){
						tasks = tasks.subList(nStart, nStart + nSize);
					} else if (tasksSize > nStart) {
						tasks = tasks.subList(nStart, tasksSize);
					} else {
						LOG.info("Number of tasks with FlowSlotTicket is less than starting point to fetch:{}", tasksSize);
						tasks.clear();
					}
				}
				
				List<Map<String, Object>> data = new LinkedList<Map<String, Object>>();
				if ("ticketCreateDate".equalsIgnoreCase(sOrderBy)) {
					populateResultSortedByTicketDate(bFilterHasTicket, tasks, mapOfTickets, data);
				} else {
					populateResultSortedByTasksOrder(bFilterHasTicket, tasks, mapOfTickets, data);
				}				

				res.put("data", data);
				res.put("size", nSize);
				res.put("start", nStart);
				res.put("order", "asc");
				res.put("sort", "id");
				res.put("total", totalNumber);
			}
		} catch (Exception e) {
			LOG.error("Error occured while getting list of tasks", e);
		}
        return res;
    }

    protected List<TaskInfo> getTasksWithTicketsFromQuery(Object taskQuery, int nStart, int nSize, boolean bFilterHasTicket, Map<String, FlowSlotTicket> mapOfTickets){
	    List<TaskInfo> tasks = (taskQuery instanceof TaskInfoQuery) ? ((TaskInfoQuery) taskQuery).listPage(nStart, nSize)
				: ((NativeTaskQuery) taskQuery).listPage(nStart, nSize);
	
		List<Long> taskIds = new LinkedList<Long>();
		for (int i = 0; i < tasks.size(); i++){
			taskIds.add(Long.valueOf(tasks.get(i).getProcessInstanceId()));
		}
		LOG.info("Preparing to select flow slot tickets. taskIds:{}", taskIds.toString());
		List<FlowSlotTicket> tickets  = new LinkedList<FlowSlotTicket>();
		if (taskIds.size() == 0){
			return tasks;
		}
		try {
			tickets = flowSlotTicketDao.findAllByInValues("nID_Task_Activiti", taskIds);
		} catch (Exception e){
			LOG.error("Error occured while getting tickets for tasks", e);
		}
		LOG.info("Found {} tickets for specified list of tasks IDs", tickets.size());
		if (tickets != null) {
			for (FlowSlotTicket ticket : tickets) {
				mapOfTickets.put(ticket.getnID_Task_Activiti().toString(), ticket);
			}
		}
		if (bFilterHasTicket) {
			LOG.info("Removing tasks which don't have flow slot tickets");
			Iterator<TaskInfo> iter = tasks.iterator();
			while (iter.hasNext()){
				TaskInfo curr = iter.next();
				if (!mapOfTickets.keySet().contains(curr.getProcessInstanceId())){
					LOG.info("Removing tasks with ID {}", curr.getId());
					iter.remove();
				}
			}
		}
		return tasks;
    }
    /** Региональный сервис получения контента файла
     * 
     *
     * @param nID_Message - номер-ИД сообщения  
     * @param nID_Process - номер-ИД процесса 
     * @throws CommonServiceException 
     */
    @ApiOperation(value = " Центральный сервис получения контента файла", notes = "")
    @RequestMapping(value = "/getMessageFile_Local", method = { RequestMethod.GET })
    public
    @ResponseBody
    byte[] getMessageFile(
            @ApiParam(value = "номер-ИД сообщения", required = true) @RequestParam(value = "nID_Message", required = true) String nID_Message ,
            @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process ) throws CommonServiceException{
    	try {
            String sID_Order = generalConfig.sID_Order_ByProcess(nID_Process);
            Map<String, String> params = new HashMap<>();
            params.put("sID_Order", sID_Order);
            params.put("nID_Message", nID_Message);
            String soResponse = "";
            String sURL = generalConfig.sHostCentral() + "/wf/service/subject/message/getMessageFile";
            soResponse = httpRequester.getInside(sURL, params);
            LOG.info("(soResponse={})", soResponse);
            return soResponse.getBytes();
        } catch (Exception oException) {
            LOG.error("Can't get: {}", oException.getMessage());
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Can't get: " + oException.getMessage(), oException, HttpStatus.FORBIDDEN);
        }
    }
    
    private long getCountOfTasks(List<String> groupsIds) {
		StringBuilder groupIdsSB = new StringBuilder();
		for (int i = 0; i < groupsIds.size(); i++){
			groupIdsSB.append("'");
			groupIdsSB.append(groupsIds.get(i));
			groupIdsSB.append("'");
			if (i < groupsIds.size() - 1){
				groupIdsSB.append(",");
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT count(task.*) FROM ACT_RU_TASK task, ACT_RU_IDENTITYLINK link WHERE task.ID_ = link.TASK_ID_ AND link.GROUP_ID_ IN(");
		sql.append(groupIdsSB.toString());
		sql.append(") ");
		
		return taskService.createNativeTaskQuery().sql(sql.toString()).count();
	}

	protected void populateResultSortedByTasksOrder(boolean bFilterHasTicket,
			List<?> tasks, Map<String, FlowSlotTicket> mapOfTickets,
			List<Map<String, Object>> data) {
		LOG.info("populateResultSortedByTasksOrder. number of tasks:{} number of tickets:{} ", tasks.size(), mapOfTickets.size());
		for (int i = 0; i < tasks.size(); i++){
			try {
				TaskInfo task = (TaskInfo)tasks.get(i);
				Map<String, Object> taskInfo = populateTaskInfo(task, mapOfTickets.get(task.getProcessInstanceId()));
				
				data.add(taskInfo);
			} catch (Exception e){
				LOG.error("error: Error while populatiing task", e);
			}
		}
	}

	protected void populateResultSortedByTicketDate(boolean bFilterHasTicket, List<?> tasks,
			Map<String, FlowSlotTicket> mapOfTickets, List<Map<String, Object>> data) {
		LOG.info("Sorting result by flow slot ticket create date. Number of tasks:{} number of tickets:{}", tasks.size(), mapOfTickets.size());
		List<FlowSlotTicket> tickets = new LinkedList<FlowSlotTicket>();
		tickets.addAll(mapOfTickets.values());
		Collections.sort(tickets, FLOW_SLOT_TICKET_ORDER_CREATE_COMPARATOR);
		LOG.info("Sorted tickets by order create date");
		Map<String, TaskInfo> tasksMap = new HashMap<String, TaskInfo>();
		for (int i = 0; i < tasks.size(); i++){
			TaskInfo task = (TaskInfo)tasks.get(i);
			tasksMap.put(((TaskInfo)tasks.get(i)).getProcessInstanceId(), task);
		}
		for (int i = 0; i < tickets.size(); i++){
			try {
				FlowSlotTicket ticket = tickets.get(i);
				TaskInfo task = tasksMap.get(ticket.getnID_Task_Activiti());
				Map<String, Object> taskInfo = populateTaskInfo(task, ticket);
				
				data.add(taskInfo);
			} catch (Exception e){
				LOG.error("error: ", e);
			}
		}
	}
    
	protected Object createQuery(String sLogin,
			boolean bIncludeAlienAssignedTasks, String sOrderBy, String sFilterStatus,
			List<String> groupsIds) {
		Object taskQuery = null; 
		if ("Closed".equalsIgnoreCase(sFilterStatus)){
			taskQuery = historyService.createHistoricTaskInstanceQuery().
					taskCandidateGroupIn(groupsIds).finished();
			if ("taskCreateTime".equalsIgnoreCase(sOrderBy)){
				 ((TaskInfoQuery)taskQuery).orderByTaskCreateTime();
			} else {
				 ((TaskInfoQuery)taskQuery).orderByTaskId();
			}
			 ((TaskInfoQuery)taskQuery).asc();
		} else {
			if (bIncludeAlienAssignedTasks){
				StringBuilder groupIdsSB = new StringBuilder();
				for (int i = 0; i < groupsIds.size(); i++){
					groupIdsSB.append("'");
					groupIdsSB.append(groupsIds.get(i));
					groupIdsSB.append("'");
					if (i < groupsIds.size() - 1){
						groupIdsSB.append(",");
					}
				}
				
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT task.* FROM ACT_RU_TASK task, ACT_RU_IDENTITYLINK link WHERE task.ID_ = link.TASK_ID_ AND link.GROUP_ID_ IN(");
				sql.append(groupIdsSB.toString());
				sql.append(") ");
				
				if ("taskCreateTime".equalsIgnoreCase(sOrderBy)){
					 sql.append(" order by task.CREATE_TIME_ asc");
				} else {
					 sql.append(" order by task.ID_ asc");
				}
				LOG.info("Query to execute {}", sql.toString());
				taskQuery = taskService.createNativeTaskQuery().sql(sql.toString());
			}  else {
				taskQuery = taskService.createTaskQuery();
				if ("OpenedUnassigned".equalsIgnoreCase(sFilterStatus)){
					((TaskQuery)taskQuery).taskCandidateUser(sLogin);
				} else if ("OpenedAssigned".equalsIgnoreCase(sFilterStatus)){
					taskQuery =  ((TaskQuery)taskQuery).taskAssignee(sLogin);
				} else if ("Opened".equalsIgnoreCase(sFilterStatus)){
					taskQuery = ((TaskQuery)taskQuery).taskCandidateOrAssigned(sLogin);
				}
				if ("taskCreateTime".equalsIgnoreCase(sOrderBy)){
					 ((TaskQuery)taskQuery).orderByTaskCreateTime();
				} else {
					 ((TaskQuery)taskQuery).orderByTaskId();
				}
				 ((TaskQuery)taskQuery).asc();
			}
		}
		return taskQuery;
	}

	protected Map<String, Object> populateTaskInfo(TaskInfo task, FlowSlotTicket flowSlotTicket) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Map<String, Object> taskInfo = new HashMap<String, Object>();
		taskInfo.put("id", task.getId());
		taskInfo.put("url", generalConfig.sHost() + "/wf/service/runtime/tasks/" + task.getId());
		taskInfo.put("owner", task.getOwner());
		taskInfo.put("assignee", task.getAssignee());
		taskInfo.put("delegationState", (task instanceof Task) ? ((Task)task).getDelegationState() : null);
		taskInfo.put("name", task.getName());
		taskInfo.put("description", task.getDescription());
		taskInfo.put("createTime", sdf.format(task.getCreateTime()));
		taskInfo.put("dueDate", task.getDueDate() != null ? sdf.format(task.getDueDate()) : null);
		taskInfo.put("priority", task.getPriority());
		taskInfo.put("suspended", (task instanceof Task) ? ((Task)task).isSuspended() : null);
		taskInfo.put("taskDefinitionKey", task.getTaskDefinitionKey());
		taskInfo.put("tenantId", task.getTenantId());
		taskInfo.put("category", task.getCategory());
		taskInfo.put("formKey", task.getFormKey());
		taskInfo.put("parentTaskId", task.getParentTaskId());
		taskInfo.put("parentTaskUrl", "");
		taskInfo.put("executionId", task.getExecutionId());
		taskInfo.put("executionUrl", generalConfig.sHost() + "/wf/service/runtime/executions/" + task.getExecutionId());
		taskInfo.put("processInstanceId", task.getProcessInstanceId());
		taskInfo.put("processInstanceUrl", generalConfig.sHost() + "/wf/service/runtime/process-instances/" + task.getProcessInstanceId());
		taskInfo.put("processDefinitionId", task.getProcessDefinitionId());
		taskInfo.put("processDefinitionUrl", generalConfig.sHost() + "/wf/service/repository/process-definitions/" + task.getProcessDefinitionId());
		taskInfo.put("variables", new LinkedList());
		if (flowSlotTicket != null){
			LOG.info("Populating flow slot ticket");
			DateTimeFormatter dtf = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd_HH-mm-ss");
			Map<String, Object> flowSlotTicketData = new HashMap<String, Object>();
			flowSlotTicketData.put("nID", flowSlotTicket.getId());
			flowSlotTicketData.put("nID_Subject", flowSlotTicket.getnID_Subject());
			flowSlotTicketData.put("sDateStart", flowSlotTicket.getsDateStart() != null ? dtf.print(flowSlotTicket.getsDateStart()): null);
			flowSlotTicketData.put("sDateFinish", flowSlotTicket.getsDateFinish() != null ? dtf.print(flowSlotTicket.getsDateFinish()): null);
			taskInfo.put("flowSlotTicket", flowSlotTicketData);
		}
		return taskInfo;
	}


    @RequestMapping(value = "/getLink", method = { RequestMethod.GET })
    @ResponseBody
    public ActionTaskLink getActionTaskLink(@RequestParam(value = "nID_Process", required = false) Long nID_Process,
                                    @RequestParam(value = "sKey", required = true) String sKey,
                                    @RequestParam(value = "nID_Subject_Holder", required = true) Long nID_Subject_Holder) throws Exception {

        return actionTaskLinkDao.getByCriteria(nID_Process, sKey, nID_Subject_Holder);
    }

    @RequestMapping(value = "/setLink", method = { RequestMethod.GET })
    @ResponseBody
    public ActionTaskLink setActionTaskLink(@RequestParam(value = "nID_Process", required = true) Long nID_Process,
                                            @RequestParam(value = "sKey", required = true) String sKey,
                                            @RequestParam(value = "nID_Subject_Holder", required = true) Long nID_Subject_Holder) throws Exception {

        return actionTaskLinkDao.setActionTaskLink(nID_Process, sKey, nID_Subject_Holder);
    }
    
}
