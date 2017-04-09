package org.igov.service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.*;
import liquibase.util.csv.CSVWriter;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.FormPropertyImpl;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.igov.io.GeneralConfig;
import org.igov.io.mail.Mail;
import org.igov.io.mail.NotificationPatterns;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEventType;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.action.task.core.ProcessDefinitionCover;
import org.igov.model.action.task.core.entity.*;
import org.igov.model.action.task.core.entity.Process;
import org.igov.model.flow.FlowSlotTicket;
import org.igov.model.flow.FlowSlotTicketDao;
import org.igov.service.business.action.task.core.ActionTaskService;
import org.igov.service.business.action.task.listener.doc.CreateDocument_UkrDoc;
import org.igov.service.business.action.task.systemtask.DeleteProccess;
import org.igov.service.business.action.task.systemtask.doc.handler.UkrDocEventHandler;
import org.igov.service.business.dfs.DfsService;
import org.igov.service.business.dfs.DfsService_New;
import org.igov.service.business.document.DocumentStepService;
import org.igov.service.business.subject.message.MessageService;
import org.igov.service.exception.*;
import org.igov.util.JSON.JsonDateTimeSerializer;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.Tool;
import org.igov.util.ToolCellSum;
import org.igov.util.db.queryloader.QueryLoader;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.activiti.engine.task.NativeTaskQuery;

import org.igov.model.subject.SubjectAccountDao;
import org.igov.service.business.action.event.ActionEventHistoryService;

import static org.igov.service.business.action.task.core.ActionTaskService.DATE_TIME_FORMAT;
import static org.igov.util.Tool.sO;
//import com.google.common.base.Optional;

/**
 * @author BW
 */
@Controller
@Api(tags = {"ActionTaskCommonController — Действия общие задач"})
@RequestMapping(value = "/action/task")
public class ActionTaskCommonController {//extends ExecutionBaseResource

    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskCommonController.class);

    @Autowired
    private HttpRequester httpRequester;
    @Autowired
    private ActionEventHistoryService actionEventHistoryService;
    @Autowired
    public GeneralConfig generalConfig;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FormService formService;
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private NotificationPatterns oNotificationPatterns;

    @Autowired
    private ProcessHistoryDao processHistoryDao;

    @Autowired
    private QueryLoader queryLoader;

    @Autowired
    private DeleteProccess deleteProccess;

    @Autowired
    private DfsService dfsService;

    @Autowired
    private DfsService_New dfsService_new;

    @Autowired
    private ActionTaskService oActionTaskService;

    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;

    @Autowired
    private ActionTaskLinkDao actionTaskLinkDao;

    @Autowired
    private MessageService oMessageService;

    @Autowired
    private SubjectAccountDao subjectAccountDao;

    @Autowired
    private Mail oMail;

    @Autowired
    private DocumentStepService oDocumentStepService;

    /**
     * Загрузка задач из Activiti:
     *
     * @param assignee ИД авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя) // * @param nID_Subject
     * ID авторизированого субъекта (добавляется в запрос автоматически после
     * аутентификации пользователя)
     */
    @ApiOperation(value = "Загрузка задач из Activiti", notes = "#####  Request:\n"
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
            + "\n```\n")
    @RequestMapping(value = "/login/{assignee}", method = RequestMethod.GET)
    public @ResponseBody
    List<TaskAssigneeI> getTasksByAssignee(@ApiParam(value = "ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @PathVariable("assignee") String assignee) {

        return oActionTaskService.getTasksByAssignee(assignee);
    }

    /**
     *
     * @param group ИД авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)
     */
    @RequestMapping(value = "/groups/{group}", method = RequestMethod.GET)
    public @ResponseBody
    List<TaskAssigneeI> getTasksByAssigneeGroup(@ApiParam(value = "ИД авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @PathVariable("group") String group) {

        return oActionTaskService.getTasksByAssigneeGroup(group);
    }

    /**
     * Получение списка ID пользовательских тасок по номеру заявки
     *
     * @param nID_Order число номер заявки, в котором, все цифры кроме последней
     * - ID процесса в activiti. А последняя цифра - его контрольная сумма
     * зашифрованная по алгоритму Луна.
     */
    @ApiOperation(value = "Получение списка ID пользовательских тасок по номеру заявки", notes = "##### Примеры:\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "CRC Error или Record not found"),
        @ApiResponse(code = 200, message = "Успех запроса. Если процесс с соответствующим ИД и таской найдены в базе")})
    @RequestMapping(value = "/getTasksByOrder", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getTasksByOrder(
            @ApiParam(value = " число номер заявки, в котором, все цифры кроме последней - ID процесса в activiti. А последняя цифра - его контрольная сумма зашифрованная по алгоритму Луна.", required = true) @RequestParam(value = "nID_Order") Long nID_Order
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
     * @param sLogin строка необязательный параметр. При указании выбираются
     * только таски, которые могут быть заассайнены или заассайнены на
     * пользователя sLogin
     * @param bAssigned булево значение необязательный параметр. Указывает, что
     * нужно искать по незаассайненным таскам (bAssigned=false) и по
     * заассайненным таскам(bAssigned=true) на пользователя sLogin
     * @param bSortByStartDate булево значение необязательный параметр. Если
     * true - будет выполнена сортировка по дате
     */
    @ApiOperation(value = "Поиск заявок по тексту (в значениях полей без учета регистра)", notes = "##### Примеры:\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "возвращает список ID тасок у которых в полях встречается указанный текст")})
    @RequestMapping(value = "/getTasksByText", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getTasksByText(@ApiParam(value = "строка текст для поиска в полях заявки", required = true) @RequestParam(value = "sFind") String sFind,
            @ApiParam(value = "строка необязательный параметр. При указании выбираются только таски, которые могут быть заассайнены или заассайнены на пользователя sLogin", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "булево значение необязательный параметр. Указывает, что нужно искать по незаассайненным таскам (bAssigned=false) и по заассайненным таскам(bAssigned=true) на пользователя sLogin", required = false) @RequestParam(value = "bAssigned", required = false) String bAssigned,
            @ApiParam(value = "булево значение необязательный параметр. Если true - будет выполнена сортировка по дате", required = false) @RequestParam(value = "bSortByStartDate", required = false, defaultValue = "false") Boolean bSortByStartDate) throws CommonServiceException {
        List<String> res = new ArrayList<>();

        Set<Task> taskSet;
        if (bSortByStartDate) {
            taskSet = new TreeSet<>((o1, o2) -> o1.getCreateTime().compareTo(o2.getCreateTime()));
        } else {
            taskSet = new HashSet<>();
        }

        String searchTeam = sFind.toLowerCase();
        TaskQuery taskQuery = oActionTaskService.buildTaskQuery(sLogin, bAssigned);
        List<Task> activeTasks = taskQuery.active().list();
        for (Task currTask : activeTasks) {
            TaskFormData data = formService.getTaskFormData(currTask.getId());
            if (data != null) {
                for (FormProperty property : data.getFormProperties()) {

                    String sValue = "";
                    String sType = property.getType() != null ? property.getType().getName() : "";
                    if ("enum".equalsIgnoreCase(sType)) {
                        sValue = oActionTaskService.parseEnumProperty(property);
                    } else {
                        sValue = property.getValue();
                    }
                    LOG.info("(taskId={}, propertyName={}, sValue={})", currTask.getId(), property.getName(), sValue);
                    if (sValue != null) {
                        if (sValue.toLowerCase().contains(searchTeam)) {
                            taskSet.add(currTask);
                        }
                    }
                }
            } else {
                LOG.info("TaskFormData for task {} is null. Skipping from processing.", currTask.getId());
            }
        }
        for (Task currTask : taskSet) {
            res.add(currTask.getId());
        }

        return res;
    }

    /**
     * Отмена задачи (в т.ч. электронной очереди)
     *
     * @param nID_Order номер-ИД процесса (с контрольной суммой)
     * @param sInfo Строка с информацией (причиной отмены)
     *
     */
    @ApiOperation(value = "Отмена задачи (в т.ч. электронной очереди)")
    @RequestMapping(value = "/cancelTask", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    ResponseEntity<String> cancelTask(
            @ApiParam(value = "номер-ИД процесса (с контрольной суммой)", required = true) @RequestParam(value = "nID_Order", required = true) Long nID_Order,
            @ApiParam(value = "Строка с информацией (причиной отмены)", required = false) @RequestParam(value = "sInfo", required = false) String sInfo,
            @ApiParam(value = "Простой вариант отмены (без электронной очереди)", required = false) @RequestParam(value = "bSimple", required = false) Boolean bSimple
    ) throws CommonServiceException, TaskAlreadyUnboundException, Exception {

        String sMessage = null;
        LOG.info("input sInfo = ", sInfo);
        sMessage = "Вибачте, виникла помилка при виконанні операції. Спробуйте ще раз, будь ласка";
        try {
            if (bSimple) {
                //@Autowired
                //private ActionTaskService oActionTaskService;
                String sLogin = "volont_escalation";
                String sReason = "Closed by user (/cancelTask)";
                String snID_Order = nID_Order + "";
                LOG.info("snID_Order={}", snID_Order);
                String snID_Process = snID_Order.substring(0, snID_Order.length() - 1);
                LOG.info("snID_Process={}", snID_Process);
                oActionTaskService.deleteProcessSimple(snID_Process, sLogin, sReason);
            } else {
                oActionTaskService.cancelTasksInternal(nID_Order, sInfo);
            }

            sMessage = "Ваша заявка відмінена. Ви можете подати нову на Порталі державних послуг iGov.org.ua.\n"
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
        } catch (TaskAlreadyUnboundException e) {
            CommonServiceException oCommonServiceException = new CommonServiceException("BUSINESS_ERR", e.getMessage(), e);
            oCommonServiceException.setHttpStatus(HttpStatus.FORBIDDEN);
            LOG.warn("Error: {}", e.getMessage(), e);
            return new ResponseEntity<>(sMessage, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            sMessage = "Ваша заявка відмінена. Ви можете подати нову на Порталі державних послуг iGov.org.ua.\n"
                    + "З повагою, команда порталу  iGov.org.ua";
            LOG.info("Error: {}", ex);
            return new ResponseEntity<>(sMessage, HttpStatus.OK);
        }
    }

    /**
     * @param nID_Task номер-ИД таски, для которой нужно найти процесс и вернуть
     * поля его стартовой формы.
     */
    @ApiOperation(value = "Получение полей стартовой формы по ID таски", notes = "##### Примеры:\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Record not found")})
    @RequestMapping(value = "/getStartFormData", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getFormDat(@ApiParam(value = " номер-ИД таски, для которой нужно найти процесс и вернуть поля его стартовой формы.", required = true) @RequestParam(value = "nID_Task") String nID_Task)
            throws CommonServiceException, JsonProcessingException, RecordNotFoundException {

        return JSONValue.toJSONString(oActionTaskService.getStartFormData(Long.parseLong(nID_Task)));
    }

    /**
     * Удаление назначенного пользователя с задачи по ИД.
     *
     * @param nID_UserTask номер-ИД задачи, для которой нужно удалить
     * назначенного пользователя.
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
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Таска занята или же нет"),
        @ApiResponse(code = 403, message = "Запись о таске не найдена")})
    @RequestMapping(value = "/resetUserTaskAssign", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<String> resetUserTaskAssign(
            @ApiParam(value = "номер-ИД юзертаски", required = true) @RequestParam(value = "nID_UserTask", required = true) String nID_UserTask)
            throws CommonServiceException, RecordNotFoundException {
        return oActionTaskService.unclaimUserTask(nID_UserTask);
    }

    /**
     * Cервис получения данных по Таске
     *
     * @param nID_Task номер-ИД таски (обязательный)
     * @param nID_Process номер-ИД процесса (опциональный, но обязательный если
     * не задан nID_Task и sID_Order)
     * @param sID_Order номер-ИД заявки (опциональный, но обязательный если не
     * задан nID_Task и nID_Process)
     * @param sLogin (опциональный) логин, по которому проверяется вхождение
     * пользователя в одну из групп, на которые распространяется данная задача
     * @param bIncludeGroups (опциональный) если задано значение true - в
     * отдельном элементе aGroup возвращается массив отождествленных групп, на
     * которые распространяется данная задача
     * @param bIncludeStartForm (опциональный) если задано значение true - в
     * отдельном элементе aFieldStartForm возвращается массив полей стартовой
     * формы
     * @param bIncludeAttachments (опциональный) если задано значение true - в
     * отдельном элементе aAttachment возвращается массив элементов-объектов
     * Attachment (без самого контента)
     * @param bIncludeMessages (опциональный) если задано значение true - в
     * отдельном элементе aMessage возвращается массив сообщений по задаче
     *
     * @return сериализованный объект Map{String : Object}
     * <br>{
     * <br> <kbd>"sStatusName"</kbd> : название юзертаски
     * <br> <kbd>"sID_Status"</kbd> : ИД юзертаски
     * <br> <kbd>sDateTimeCreate</kbd> : : дата и время создания юзертаски
     * <br> <b>"oProcess"</b> : {
     * <br><kbd>"sName"</kbd> - название услуги (БП);
     * <br> <kbd>"sBP"</kbd> - id-бизнес-процесса (БП);
     * <br> <kbd>"nID"</kbd> - номер-ИД процесса;
     * <br> <kbd>"sDateCreate"</kbd> - дата создания процесса
     * <br>},
     * <br> <b>"aField"</b> : [
     * <br> ... - массив элементов-объектов <kbd>FormProperty</kbd> (или
     * <kbd>HistoricFormProperty для архивных тасок</kbd>)
     * <br>],
     * <br> <b>"oData"</b> : {
     * <br> ... - объекты FormProperty типа queueData
     * <br>}
     * <br> ... другие опциональные объекты: aGroup, aFieldStartForm,
     * aAttachment и aMessage
     * <br>}
     */
    @ApiOperation(value = "Получение данных по таске", notes = "#####  ActionCommonTaskController: Сервис получения данных по таске #####\n\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getTaskData?nID_Task=nID_Task&sID_Order=sID_Order\n\n\n"
            + "Response:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"sStatusName\": название юзертаски\n"
            + "  \"sID_Status\": ИД юзертаски\n"
            + "  \"sDateTimeCreate\": дата и время создания юзертаски\n"
            + "  \"oProcess\":{\n"
            + "    \"sName\":\"название услуги (БП)\"\n"
            + "    \"sBP\":\"id-бизнес-процесса (БП)\"\n"
            + "    \"nID\":\"номер-ИД процесса\"\n"
            + "    \"sDateCreate\":\"дата создания процесса\"\n"
            + "  },\n"
            + "  \"aField\":[...] - массив объектов полей Таски с их атрибутами\n"
            + "  \"oData\":{...} - oбъекты электронной очереди Таски либо значение NULL, если элементов электронной очереди в таске нет\n"
            + " ... другие опциональные объекты: aGroup, aFieldStartForm, aAttachment и aMessage\n"
            + "}\n"
            + "\n```\n"
            + "\n"
            + "Элементы массива aField обычно имеют следующую структуру:\n"
            + " - для активных тасок:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"id\": идентификатор, используемый для передачи данных в форму таски\n"
            + "  \"name\": отображаемое в форме описание поля\n"
            + "  \"type\": объект типа параметра\n"
            + "  \"value\": значение параметра\n"
            + "  \"required\": свойство указывает, что поле параметра обязательно для ввода значения\n"
            + "  \"writable\": свойство указывает, что от пользователя ожидаются введенные данные в поле при отправке формы\n"
            + "  \"readable\": свойство указывает на возможность отображения параметра и его обработки методами сервисов\n"
            + "}\n"
            + "\n```\n"
            + " - для архивных тасок:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"id\": идентификатор параметра\n"
            + "  \"value\": представленное значение\n"
            + "}\n"
            + "\n```\n"
            + "\n"
    )
    @RequestMapping(value = "/getTaskData", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTaskData(
            @ApiParam(value = "номер-ИД таски (обязательный)", required = false) @RequestParam(value = "nID_Task", required = false) Long nID_Task,
            @ApiParam(value = "номер-ИД процесса (опциональный, но обязательный если не задан nID_Task и sID_Order)", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "номер-ИД заявки (опциональный, но обязательный если не задан nID_Task и nID_Process)", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "(опциональный) логин, по которому проверяется вхождение пользователя в одну из групп, на которые распространяется данная задача", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "(опциональный) если задано значение true - в отдельном элементе aGroup возвращается массив отождествленных групп, на которые распространяется данная задача", required = false) @RequestParam(value = "bIncludeGroups", required = false) Boolean bIncludeGroups,
            @ApiParam(value = "(опциональный) если задано значение true - в отдельном элементе aFieldStartForm возвращается массив полей стартовой формы", required = false) @RequestParam(value = "bIncludeStartForm", required = false) Boolean bIncludeStartForm,
            @ApiParam(value = "(опциональный) если задано значение true - в отдельном элементе aAttachment возвращается массив элементов-объектов Attachment (без самого контента)", required = false) @RequestParam(value = "bIncludeAttachments", required = false) Boolean bIncludeAttachments,
            @ApiParam(value = "(опциональный) если задано значение true - в отдельном элементе aMessage возвращается массив сообщений по задаче", required = false) @RequestParam(value = "bIncludeMessages", required = false) Boolean bIncludeMessages,
            @ApiParam(value = "(опциональный) если задано значение false - в элементе aProcessVariables не возвращается массив переменных процесса", required = false) @RequestParam(value = "bIncludeProcessVariables", required = false, defaultValue = "false") Boolean bIncludeProcessVariables)
            throws CRCInvalidException, CommonServiceException, RecordNotFoundException {

        if (nID_Task == null) {
            nID_Task = oActionTaskService.getTaskIDbyProcess(nID_Process, sID_Order, Boolean.FALSE);
        }
        if (sLogin != null) {
            if (oActionTaskService.checkAvailabilityTaskGroupsForUser(sLogin, nID_Task)) {
                LOG.info("User {} have access to the Task {}", sLogin, nID_Task);
            } else {
                String taskGroupIDs = oActionTaskService.getGroupIDsByTaskID(nID_Task).toString();
                throw new AccessServiceException(AccessServiceException.Error.LOGIN_ERROR, "Access deny " + taskGroupIDs);
            }
        }
        if (bIncludeGroups == null) {
            bIncludeGroups = Boolean.FALSE;
        }
        if (bIncludeStartForm == null) {
            bIncludeStartForm = Boolean.FALSE;
        }
        if (bIncludeAttachments == null) {
            bIncludeAttachments = Boolean.FALSE;
        }
        if (bIncludeMessages == null) {
            bIncludeMessages = Boolean.FALSE;
        }
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("oProcess", oActionTaskService.getProcessInfo(nID_Process, nID_Task, sID_Order));
        } catch (NullPointerException e) {
            String message = String.format("Incorrect Task ID [id = %s]. Record not found.", nID_Task);
            LOG.info(message);
            throw new RecordNotFoundException(message);
        }

        List<FormProperty> aField = null;
        try {
//            aField = oActionTaskService.getFormPropertiesByTaskID(nID_Task);
//            response.put("aField", aField);
            response.put("aField", oActionTaskService.getFormPropertiesMapByTaskID(nID_Task));

        } catch (ActivitiObjectNotFoundException e) {
            LOG.info(String.format("Must search Task [id = '%s'] in history!!!", nID_Task));
            response.put("aField", oActionTaskService.getHistoricFormPropertiesByTaskID(nID_Task));
        }

        response.put("oData", oActionTaskService.getQueueData(aField));

        if (bIncludeGroups.equals(Boolean.TRUE)) {
            response.put("aGroup", oActionTaskService.getGroupIDsByTaskID(nID_Task));
        }
        if (bIncludeStartForm.equals(Boolean.TRUE)) {
            response.put("aFieldStartForm", oActionTaskService.getStartFormData(nID_Task));
        }

        if (bIncludeAttachments.equals(Boolean.TRUE)) {
            LOG.info("Attach is triggered!");
            response.put("aAttachment", oActionTaskService.getAttachmentsByTaskID(nID_Task));
        } else {
            LOG.info("Attach is not triggered!");
        }
        if (bIncludeMessages.equals(Boolean.TRUE)) {
            if (nID_Process == null) {
                nID_Process = Long.parseLong(
                        oActionTaskService.getProcessInstanceIDByTaskID(nID_Task.toString())
                );
            }
            try {
                response.put("aMessage", oMessageService.gerOrderMessagesByProcessInstanceID(nID_Process));
            } catch (Exception oException) {
                LOG.error("Can't get: {}", oException.getMessage());
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Can't get: " + oException.getMessage(), oException, HttpStatus.FORBIDDEN);
            }
        }
        if (bIncludeProcessVariables.equals(Boolean.TRUE) && nID_Process != null) {
            Map<String, Object> mProcessVariable = null;
            try {
                mProcessVariable = runtimeService.getVariables(Long.toString(nID_Process));
            } catch (ActivitiObjectNotFoundException oException) {
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processDefinitionId(Long.toString(nID_Process)).singleResult();
                if (historicProcessInstance == null) {
                    LOG.error("Can't get: {}", oException.getMessage());
                } else {
                    LOG.info("Getting variables from HistoricVariableInstance");
                    mProcessVariable = historicProcessInstance.getProcessVariables();
                }

            }
            response.put("mProcessVariable", mProcessVariable);
        }

        response.put("sStatusName", oActionTaskService.getTaskName(nID_Task));
        response.put("sID_Status", oActionTaskService.getsIDUserTaskByTaskId(nID_Task));
        response.put("nID_Task", nID_Task);
        response.putAll(oActionTaskService.getTaskData(nID_Task));

        String sDateTimeCreate = JsonDateTimeSerializer.DATETIME_FORMATTER.print(
                oActionTaskService.getTaskDateTimeCreate(nID_Task).getTime()
        );
        response.put("sDateTimeCreate", sDateTimeCreate);

        return JsonRestUtils.toJsonResponse(response);
    }

    /**
     * Запуск процесса Activiti:
     *
     * @param key Ключ процесса // * @param nID_Subject ID авторизированого
     * субъекта (добавляется в запрос автоматически после аутентификации
     * пользователя)
     * @param organ
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
    public @ResponseBody
    ProcessI startProcessByKey(
            @ApiParam(value = "Ключ процесса", required = true) @PathVariable("key") String key,
            @ApiParam(value = "Орган", required = false) @RequestParam(value = "organ", required = false) String organ) {

        ProcessInstance pi;
        if (organ != null) {
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("organ", organ);
            pi = runtimeService.startProcessInstanceByKey(key, variables);
        } else {
            pi = runtimeService.startProcessInstanceByKey(key);
        }
        if (pi == null || pi.getId() == null) {
            throw new IllegalArgumentException(String.format(
                    "process did not started by key:{%s}", key));
        }
        return new Process(pi.getProcessInstanceId());
    }

    @RequestMapping(value = "/setVariable", method = RequestMethod.GET)
    @ResponseBody
    public String setVariableToProcessInstance(
            @RequestParam(value = "processInstanceId", required = true) String snID_Process,
            @RequestParam(value = "key", required = true) String sKey,
            @RequestParam(value = "value", required = true) String sValue
    ) {
        try {
            runtimeService.setVariable(snID_Process, sKey, sValue);
        } catch (Exception oException) {
            LOG.error("ERROR:{} (snID_Process={},sKey={},sValue={})", oException.getMessage(), snID_Process, sKey, sValue);
        }
        return "";
    }

    /**
     * Method takes current value of process variable and if its value doesn't
     * contain sInsertValue method appends it. Also if it contains sRemoveValue
     * it will be removed. Can be used only with Strings
     *
     * @param snID_Process - id of Activiti Process
     * @param sKey - name of Variable on Activiti Process
     * @param sInsertValues - values which should be added.
     * @param sRemoveValues - values which should be deleted.
     * @return
     */
    @RequestMapping(value = "/mergeVariable", method = RequestMethod.GET)
    @ResponseBody
    public String mergeVariableValueOnProcessInstance(
            @RequestParam(value = "processInstanceId", required = true) String snID_Process,
            @RequestParam(value = "key", required = true) String sKey,
            @RequestParam(value = "removeValues", required = false) String[] sRemoveValues,
            @RequestParam(value = "insertValues", required = false) String[] sInsertValues
    ) {
        try {
            Object currentValueObject = runtimeService.getVariable(snID_Process, sKey);
            String currentValue = currentValueObject == null ? "" : currentValueObject.toString();
            LOG.info("removeValues={} insertValues={}", sRemoveValues, sInsertValues);
            if (sInsertValues != null) {
                for (String sInsertValue : sInsertValues) {
                    if (!currentValue.contains(sInsertValue)) {
                        currentValue = (currentValue.trim() + " " + sInsertValue).trim();
                    }
                }
            }
            if (sRemoveValues != null) {
                for (String sRemoveValue : sRemoveValues) {
                    if (currentValue.contains(sRemoveValue)) {
                        currentValue = currentValue.replace(sRemoveValue, "");
                    }
                }
            }
            runtimeService.setVariable(snID_Process, sKey, currentValue.trim());
            LOG.info("currentValue={}", currentValue);
        } catch (Exception oException) {
            LOG.error("ERROR:{} (snID_Process={},sKey={},sInsertValue={}, sRemoveValue={})",
                    oException.getMessage(), snID_Process, sKey, sInsertValues, sRemoveValues);
        }
        return "";
    }

    /**
     * This method duplicates functionality of setVariableToProcessInstance but
     * uses POST method which provides bigger size of query params.
     *
     * @param allRequestParamsStr
     * @return
     */
    @RequestMapping(value = "/setVariable", method = RequestMethod.POST, consumes = "text/plain")
    @ResponseBody
    public String setVariableToProcessInstanceUsingPost(@RequestBody String allRequestParamsStr
    //                @RequestParam(value = "processInstanceId", required = true) String snID_Process,
    //            @RequestParam(value = "key", required = true) String sKey,
    //            @RequestParam(value = "value", required = true) String sValue
    ) {
        String processInstanceId = null;
        String key = null;
        String value = null;
        try {
            LOG.info("allRequestParams:{}", allRequestParamsStr);
            String[] paramsKeyValues = allRequestParamsStr.split("&");
            HashMap<String, String> params = new HashMap<>();
            for (String item : paramsKeyValues) {
                String[] result = item.split("=");
                String k = result[0];
                String v = result.length > 1 ? item.split("=")[1] : "";
                params.put(k, v);
            }
            processInstanceId = params.get("processInstanceId");
            key = params.get("key");
            key = URLDecoder.decode(key, "UTF-8");
            value = params.get("value");
            value = URLDecoder.decode(value, "UTF-8");

            runtimeService.setVariable(processInstanceId, key, value);
        } catch (Exception oException) {
            LOG.error("ERROR:{} (snID_Process={},sKey={},sValue={})", oException.getMessage(), processInstanceId, key, value);
        }
        return "";
    }

    /**
     * Загрузка каталога сервисов из Activiti:
     *
     * // * @param nID_Subject ID авторизированого субъекта (добавляется в
     * запрос автоматически после аутентификации пользователя)
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
    public @ResponseBody
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
    public @ResponseBody
    void deleteProcess(@RequestParam(value = "nID_Order") Long nID_Order,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason)
            throws Exception {
        //Вызывать сервис централа и апдейтить в истории статус на 8 (закрыт)
        oActionTaskService.deleteProcess(nID_Order, sLogin, sReason);
    }

    @ApiOperation(value = "DeleteProcess", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/delete-process-simple", method = RequestMethod.DELETE)
    public @ResponseBody
    void deleteProcessSimple(@RequestParam(value = "nID_Process") String snID_Process,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason)
            throws Exception {

        oActionTaskService.deleteProcessSimple(snID_Process, sLogin, sReason);
    }

    @ApiOperation(value = "DeleteProcess", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/delete-process-list", method = RequestMethod.DELETE)
    public @ResponseBody
    void deleteProcesList(@RequestParam(value = "sanID_Process") String sanID_Process,
            @RequestParam(value = "sLogin", required = false) String sLogin,
            @RequestParam(value = "sReason", required = false) String sReason)
            throws Exception {
        String[] asnID_Process = sanID_Process.split(",");
        for (String snID_Process : asnID_Process) {
            oActionTaskService.deleteProcessSimple(snID_Process, sLogin, sReason);
        }
    }

    /**
     * Получение статистики по бизнес процессу за указанный период
     *
     * @param sID_BP_Name - ИД бизнес процесса
     * @param dateAt - дата начала периода выборки
     * @param dateTo - дата окончания периода выборки
     * @param nRowStart - позиция начальной строки для возврата (0 по умолчанию)
     * @param nRowsMax - количество записей для возврата (1000 по умолчанию)
     * @param bDetail - если да, то выгружать все поля тасок, иначе -- только
     * основные (по умолчанию да)
     * @param saFields - вычисляемые поля (название поля -- формула, issue 907)
     * @param saFieldSummary - сведение полей, которое производится над выборкой
     * (issue 916)
     * @param httpResponse - респонс, в который пишется ответ -- csv-файл
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
    @RequestMapping(value = "/download_bp_timing", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
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
        LOG.info("111sdfFileName: " + sdfFileName);
        String fileName = "!" + sID_BP_Name + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".xlsx";
        LOG.debug("File name for statistics : {%s}", fileName);
        boolean isByFieldsSummary = saFieldSummary != null
                && !saFieldSummary.isEmpty();
        //httpResponse.setContentType("text/csv;charset=UTF-8");
        httpResponse.setContentType("application/vnd.ms-excel; charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setHeader("Content-disposition", "attachment; filename=" + fileName);

        List<HistoricTaskInstance> foundResults = historyService
                .createHistoricTaskInstanceQuery().taskCompletedAfter(dateAt)
                .taskCompletedBefore(dateTo).processDefinitionKey(sID_BP_Name)
                .listPage(nRowStart, nRowsMax);

        List<String> headers = new ArrayList<>();
        String[] headersMainField = {"nID_Process", "sLoginAssignee",
            "sDateTimeStart", "nDurationMS", "nDurationHour", "sName", "sAssignee"};
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
     * @param sID_BP business process name
     * @param sID_State_BP task state id
     * @param saFields field of the tasks to download. Separated by comma
     * @param nASCI_Spliter splitter of the fields
     * @param sID_Codepage encoding for the file
     * @param sDateCreateFormat format for sDateCreate
     * @param dateAt start date for the filter
     * @param dateTo end date for the filter
     * @param nRowStart start row for paging
     * @param nRowsMax maximal amount of row for paging
     * @param bIncludeHistory to include historic task instances. default value
     * is true
     * @param saFieldsCalc list of calculated fields
     * @param saFieldSummary parap to specify aggregated fields
     * @param httpResponse http responce wrapper
     * @throws IOException in case of connection aborted with client
     * <p/>
     * example: https://test.region.igov.org.ua/wf/service/action/task/
     * downloadTasksData ?sID_BP=kiev_mreo_1&sDateAt=2015-06-28&sDateTo
     * =2015-08-01&nASCI_Spliter =59&sID_Codepage=UTF8&saFields=nID_Task
     * ;bankIdPassport;bankIdlastName
     * ;bankIdfirstName;bankIdmiddleName;1;sDateCreate
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
            @ApiParam(value = "название бизнес-процесса", required = true) @RequestParam(value = "sID_BP", required = true) String sID_BP,
            @ApiParam(value = "состояние задачи, по умолчанию исключается из фильтра Берется из поля taskDefinitionKey задачи", required = false) @RequestParam(value = "sID_State_BP", required = false) String sID_State_BP,
            @ApiParam(value = "имена полей для выборкы разделенных через ';', чтобы добавить все поля можно использовать - '*' или не передевать параметр в запросе. "
                    + "Поле также может содержать названия колонок. Например, saFields=Passport\\=${passport};{email}", required = false) @RequestParam(value = "saFields", required = false, defaultValue = "*") String saFields,
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
            @ApiParam(value = "Email для отправки выбранных данных", required = false) @RequestParam(value = "sMailTo", required = false) String sMailTo,
            @ApiParam(value = "логин для вытаскивания фильтра", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "признак для получения фильтра", required = false) @RequestParam(value = "asField_Filter", required = false) String asField_Filter,
            @ApiParam(value = "начальная дата закрытия таски", required = false) @RequestParam(value = "sTaskEndDateAt", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date sTaskEndDateAt,
            @ApiParam(value = "конечная дата закрытия таски", required = false) @RequestParam(value = "sTaskEndDateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date sTaskEndDateTo,
            HttpServletResponse httpResponse) throws IOException, CommonServiceException, EmailException {
        LOG.info("!!!!!!!!!!!!!!begin");
        LOG.info("1dateAt= " + dateAt);
        LOG.info("2dateTo= " + dateTo);
        LOG.info("3sTaskEndDateAt= " + sTaskEndDateAt);
        LOG.info("4sTaskEndDateTo= " + sTaskEndDateTo);
//      'sID_State_BP': '',//'usertask1'
//      'saFieldsCalc': '', // поля для калькуляций
//      'saFieldSummary': '' // поля для агрегатов

        if ("".equalsIgnoreCase(sID_State_BP) || "null".equalsIgnoreCase(sID_State_BP)) {
            sID_State_BP = null;
        }
        if ("".equalsIgnoreCase(saFieldsCalc) || "null".equalsIgnoreCase(saFieldsCalc)) {
            saFieldsCalc = null;
        }
        if ("".equalsIgnoreCase(saFieldSummary) || "null".equalsIgnoreCase(saFieldSummary)) {
            saFieldSummary = null;
        }

        // 1. validation
        if (StringUtils.isBlank(sID_BP)) {
            LOG.error("Wrong name of business task - {}", sID_BP);
            throw new ActivitiObjectNotFoundException(
                    "Statistics for the business task '" + sID_BP
                    + "' not found. Wrong BP name.", Task.class);
        }
        Date dBeginDate = oActionTaskService.getBeginDate(dateAt);
        LOG.info("dBeginDate: " + dBeginDate);
        LOG.info("dateAt: " + dateAt);
        Date dEndDate = oActionTaskService.getEndDate(dateTo);
        LOG.info("dEndDate: " + dEndDate);
        LOG.info("dateAt: " + dateAt);
        String separator = oActionTaskService.getSeparator(sID_BP, nASCI_Spliter);
        //LOG.info("4444separator " + separator);
        //LOG.info("7777nASCI_Spliter " + nASCI_Spliter);
        //LOG.info("6666separator " + separator.chars());
        Charset charset = oActionTaskService.getCharset(sID_Codepage);
        //LOG.info("5555charset " + charset);
        // 2. query
        TaskQuery query = taskService.createTaskQuery()
                .processDefinitionKey(sID_BP);
        HistoricTaskInstanceQuery historicQuery = historyService
                .createHistoricTaskInstanceQuery()
                .processDefinitionKey(sID_BP);
        if (sTaskEndDateAt != null) {
            LOG.info("Selecting tasks which were completed after {}", sTaskEndDateAt);
            historicQuery.taskCompletedAfter(sTaskEndDateAt);
        }
        if (sTaskEndDateTo != null) {
            LOG.info("Selecting tasks which were completed after {}", sTaskEndDateTo);
            historicQuery.taskCompletedBefore(sTaskEndDateTo);
        }
        if (dateAt != null) {
            query = query.taskCreatedAfter(dBeginDate);
            historicQuery = historicQuery.taskCreatedAfter(dBeginDate);
        }
        if (dateTo != null) {
            query = query.taskCreatedBefore(dEndDate);
            historicQuery = historicQuery.taskCreatedBefore(dEndDate);
        }
        historicQuery.includeProcessVariables();
        if (sID_State_BP != null) {
            historicQuery.taskDefinitionKey(sID_State_BP).includeTaskLocalVariables();
        }
        List<HistoricTaskInstance> foundHistoricResults = historicQuery
                .listPage(nRowStart, nRowsMax);

        if ("*".equals(saFields)){
        	saFields = null;
        	LOG.info("Resetting saFields to null in order to get all the fields values");
        }
        String header = oActionTaskService.formHeader(saFields, foundHistoricResults, saFieldsCalc);
        String[] headers = header.split(";");

        
        saFields = oActionTaskService.processSaFields(saFields, foundHistoricResults);

        LOG.info("!!!!!!!!!!!!!!!!!!!saFields!!!!!!!!!!!!!!!!!" + saFields);
        if (sID_State_BP != null) {
            query = query.taskDefinitionKey(sID_State_BP).includeTaskLocalVariables();
        }
        List<Task> foundResults = new LinkedList<Task>();
        if (sTaskEndDateAt == null && sTaskEndDateTo == null) {
            // we need to call runtime query only when non completed tasks are selected.
            // if only completed tasks are selected - results of historic query will be used
            foundResults = query.listPage(nRowStart, nRowsMax);
        }

        // 3. response
        SimpleDateFormat sdfFileName = new SimpleDateFormat(
                "yyyy-MM-ddHH-mm-ss", Locale.ENGLISH);
        LOG.info("222sdfFileName: " + sdfFileName);
        String sTaskDataFileName = fileName != null ? fileName : "data_BP-"
                + sID_BP + "_"
                + sdfFileName.format(Calendar.getInstance().getTime()) + ".txt";

        SimpleDateFormat sDateCreateDF = new SimpleDateFormat(
                sDateCreateFormat, Locale.ENGLISH);

        LOG.debug("File name to return statistics : {}", sTaskDataFileName);

        CSVWriter printWriter = null;
        PipedInputStream pi = new PipedInputStream();
        LOG.info("!!!!!!!!!!!!!sMailTo: " + sMailTo);
        if (sMailTo != null) {
            PipedOutputStream po = new PipedOutputStream(pi);
            PrintWriter pw = new PrintWriter(po);
            printWriter = new CSVWriter(pw, separator.charAt(0),
                    CSVWriter.NO_QUOTE_CHARACTER);
        } else {
            httpResponse.setContentType("text/csv;charset=" + charset.name());
            httpResponse.setHeader("Content-disposition", "attachment; filename="
                    + sTaskDataFileName);
            printWriter = new CSVWriter(httpResponse.getWriter(), separator.charAt(0),
                    CSVWriter.NO_QUOTE_CHARACTER);
        }

        List<Map<String, Object>> csvLines = new LinkedList<>();

        if (bHeader && header != null && saFieldSummary == null) {
            printWriter.writeNext(headers);

            LOG.info("headers" + headers);
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
                    tasksIdToExclude, saFieldsCalc, headers, sID_State_BP);
        }
        LOG.info("!!!!!!!!!!!!!!saFieldsSummary" + saFieldSummary);
        if (saFieldSummary != null) {

            LOG.info(">>>saFieldsSummary={}", saFieldSummary);
            try {
                List<List<String>> stringResults = new ToolCellSum()
                        .getFieldsSummary(csvLines, saFieldSummary);
                for (int i = 0; i < stringResults.size(); i++) {
                    if (i == 0 && !bHeader) {
                        continue;
                    }
                    List<String> line = stringResults.get(i);
                    printWriter.writeNext(line.toArray(new String[line.size()]));
                    LOG.info("!!!!!!!!!!!!!!line" + line);
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
                LOG.info("!!!!oActionTaskService.createStringArray_line" + line);
                printWriter.writeNext(line);
            }
        }

        printWriter.close();

        if (sMailTo != null) {
            LOG.info("Sending email with tasks data to email {}", sMailTo);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
            String sSubject = String.format("Выборка за: (%s)-(%s) для БП: %s ", sdf.format(dBeginDate), sdf.format(dEndDate), sID_BP);
            String sFileExt = "text/csv";
            DataSource oDataSource = new ByteArrayDataSource(pi, sFileExt);
            oMail._To(sMailTo);
            oMail._Head(sSubject);
            oMail._Body(sSubject);
            oMail._Attach(oDataSource, sTaskDataFileName, "");
            try {
                oMail.send();
            } catch (EmailException ex) {
                LOG.error("Error occured while sending tasks data to email!!!", ex);
                throw ex;
            } finally {
                pi.close();
            }

            /*if (!oMail.sendWithUniSender()) {
                LOG.error("Error occured while sending tasks data to email!!!");
                pi.close();
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Error occured while sending tasks data to email!!!");
            } else {
                pi.close();
            }*/
            httpResponse.setContentType("text/csv;charset=windows-1251");
            httpResponse.getWriter().print("OK");
        }

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
    @Deprecated //новый: getBPs 
    public @ResponseBody
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
     * // * @param sID_Order - строка-ид заявки
     *
     * @param nID_Process
     * @param sMail -- строка электронного адреса гражданина // * @param
     * nID_Server - ид сервера
     * @param sHead -- строка заголовка письма //опциональный (если не задан, то
     * "Необходимо уточнить данные")
     * @param sSubjectInfo -- строка-информация о субъекте //опциональный
     * @param nID_Subject -- ID гражданина //опциональный
     * @param sJsonBody -- JSON-объект с параметрами: saField -- строка-массива
     * полей
     * (например:"[{'id':'sFamily','type':'string','value':'Белявский'},{'id':'nAge','type':'long'}]");
     * soParams - строка-обьекта параметров; sBody -- строка тела письма
     * //опциональный (если не задан, то пустота)
     *
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
    @RequestMapping(value = "/setTaskQuestions", method = RequestMethod.POST)
    public @ResponseBody
    void setTaskQuestions(
            @ApiParam(value = "номер-ИД процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process,
            //@ApiParam(value = "строка-массива полей", required = true) @RequestParam(value = "saField") String saField,
            //@ApiParam(value = "строка-обьекта параметров", required = true) @RequestParam(value = "soParams") String soParams,
            @ApiParam(value = "строка электронного адреса гражданина", required = true) @RequestParam(value = "sMail") String sMail,
            @ApiParam(value = "строка заголовка письма", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            //@ApiParam(value = "строка тела сообщения-коммента (общего)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "строка информация о субьекте", required = false) @RequestParam(value = "sSubjectInfo", required = false) String sSubjectInfo,
            @ApiParam(value = "номер - ИД субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "JSON-щбъект с параметрами: saField - строка-массива полей (required = true); soParams - строка-обьекта параметров (required = true); sBody - строка тела сообщения-коммента (общего) (required = false)", required = true) @RequestBody String sJsonBody
    ) throws CommonServiceException, CRCInvalidException {
        sHead = ((sHead == null || "".equals(sHead.trim()))
                ? "Просимо ознайомитись із коментарем держслужбовця, по Вашій заявці на iGov" : sHead);
        String saField = "";
        String soParams = null;
        String sBody = null;
        Map<String, String> mJsonBody;
        try {
            mJsonBody = JsonRestUtils.readObject(sJsonBody, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parse JSON sJsonBody in request setTaskQuestions: " + e.getMessage());
        }
        if (mJsonBody != null) {
            if (mJsonBody.containsKey("saField")) {
                saField = (String) mJsonBody.get("saField");
            }
            if (mJsonBody.containsKey("soParams")) {
                soParams = (String) mJsonBody.get("soParams");
            }
            if (mJsonBody.containsKey("sBody")) {
                sBody = (String) mJsonBody.get("sBody");
            }
        }

        String sToken = Tool.getGeneratedToken();
        try {
            String processId = String.valueOf(nID_Process);
            String variableName = "saTaskStatus";
            String waitsAnswerTag = "WaitAnswer";
            String gotAnswerTag = "GotAnswer";
            Object taskStatus = runtimeService.getVariable(processId, variableName);
            String tags = taskStatus == null ? "" : String.valueOf(taskStatus);
            LOG.info("set_tags: {}, processId={}, waitsAnswerTag={}", tags, processId, waitsAnswerTag);
            if (!tags.contains(waitsAnswerTag)) {
                tags = (tags.trim() + " " + waitsAnswerTag).trim();
            }
            if (tags.contains(gotAnswerTag)) {
                tags = tags.replace(gotAnswerTag, "").trim();
            }
            runtimeService.setVariable(processId, variableName, tags);

            String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
            String sReturn = oActionTaskService.updateHistoryEvent_Service(
                    HistoryEvent_Service_StatusType.OPENED_REMARK_EMPLOYEE_QUESTION,
                    sID_Order,
                    saField,
                    "Необхідно уточнити дані" + (sBody == null ? "" : ", за коментарем: " + sO(sBody)), sToken, null, sSubjectInfo, nID_Subject);
            LOG.info("(sReturn={})", sReturn);
            //oActionTaskService.setInfo_ToActiviti("" + nID_Process, saField, sBody);
            //createSetTaskQuestionsMessage(sID_Order, sO(sBody), saField);//issue 1042
            oNotificationPatterns.sendTaskEmployeeQuestionEmail(sHead, sO(sBody), sMail, sToken, nID_Process, saField, soParams);
        } catch (Exception e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Can't make task question: " + e.getMessage(), e,
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
    public @ResponseBody
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
            List<Task> aTask = taskService.createTaskQuery().processInstanceId(nID_Process + "").list();

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
                    if (sID == null) {
                        sID = (String) oField.get("id");
                    }
                    String sValueOld = (String) oField.get("sValue");
                    String sValueNew = (String) oField.get("sValueNew");
                    mField.put(sID, sValueNew);
                    LOG.info("Set variable sID={} with sValueNew={} (sValueOld={})", sID, sValueNew, sValueOld);
                }
                //LOG.info("Updating form data for the task {}|{}", oTask.getId(), mField);
                LOG.info("oTask: (getName()={},getDescription()={},getId()={},mField={})", oTask.getName(), oTask.getDescription(), oTask.getId(), mField);
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
    public @ResponseBody
    Map<String, Object> sendProccessToGRES(@ApiParam(value = "номер-ИД задачи", required = true) @RequestParam(value = "nID_Task") Long nID_Task)
            throws CommonServiceException {

        return oActionTaskService.sendProccessToGRESInternal(nID_Task);

    }

    @ApiOperation(value = "GetTaskFormData", notes = "#####  ActionCommonTaskController: описания нет #####\n\n")
    @RequestMapping(value = "/getTaskFormData", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getTaskFormData(@ApiParam(value = "номер-ИД задачи", required = true) @RequestParam(value = "nID_Task") Long nID_Task) throws CommonServiceException {
        return oActionTaskService.getTaskFormDataInternal(nID_Task);
    }

    /**
     * issue 808. сервис ЗАПРОСА полей, требующих уточнения, c отсылкой
     * уведомления гражданину
     *
     * // * @param sID_Order - строка-ид заявки
     *
     * @param nID_Process - номер-ИД процесса
     * @return массив сообщений (строка JSON)
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Получение сообщений по заявке", notes = "")
    @RequestMapping(value = "/getOrderMessages_Local", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String getOrderMessages_Local( //ResponseEntity
            @ApiParam(value = "номер-ИД процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process
    ) throws CommonServiceException {
        try {
            /* issue #1131
             String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
             Map<String, String> params = new HashMap<>();
             params.put("sID_Order", sID_Order);
             String soResponse = "";
             String sURL = generalConfig.getSelfHostCentral() + "/wf/service/subject/message/getServiceMessages";
             soResponse = httpRequester.getInside(sURL, params);
             LOG.info("(soResponse={})", soResponse);
             */
            //public static ResponseEntity<String> toJsonResponse(Object res) {
            //return toJsonResponse(HttpStatus.OK, soResponse);
            //return soResponse; // issue #1131
            /*String historyEventServiceJson = oActionTaskService.updateHistoryEvent_Service(
             sID_Order,
             saField,
             sHead, sBody, sToken, "Запит на уточнення даних");
             LOG.info("....ok! successfully update historyEvent_service! event= {}", historyEventServiceJson);*/
            //oActionTaskService.setInfo_ToActiviti("" + nID_Process, saField, sBody);
            //createSetTaskQuestionsMessage(sID_Order, sBody, saField);//issue 1042
            return oMessageService.gerOrderMessagesByProcessInstanceID(nID_Process); // issue #1131
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
            + " заассайнены на пользователей из полученных групп:\n\n" + "Содержит следующие параметры:\n"
            + "- sLogin - id пользователя. обязательный параметр, указывающий пользователя\n"
            + "- bIncludeAlienAssignedTasks - необязательный параметр (по умолчанию false). Если значение false - то возвращать только свои и только не ассайнутые, к которым доступ.\n"
            + "- sOrderBy - метод сортировки задач. Необязательный параметр. По умолчанию 'id'. Допустимые значения 'id', 'taskCreateTime', 'ticketCreateDate'\n"
            + "- nSize - Количество задач в результате. Необязательный параметр. По умолчанию 10.\n"
            + "- nStart - Порядковый номер первой задачи для возвращения. Необязательный параметр. По умолчанию 0\n"
            + "- sFilterStatus - Необязательный параметр (по умолчанию обрабатывается как OpenedUnassigned). статус фильтрации задач, у которого возможные значения: OpenedUnassigned (только не-ассайнутые), "
            + " OpenedAssigned(только ассайнутые), Opened(только открытые (не в истории)), Closed(только закрытые (история))\n"
            + "- bFilterHasTicket - Необязательный параметр (по умолчанию false). Если true - возвращать только те задачи, у которых есть связанный тикет\n"
            + "Примеры:\n\n" + "https://test.region.igov.org.ua/wf/service/action/task/getTasks?sLogin=kermit\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getTasks?sLogin=kermit&nSize=15&nStart=10\n\n")
    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getTasks(
            @ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin") String sLogin,
            @ApiParam(value = "nID_Process", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "bIncludeAlienAssignedTasks", required = true) @RequestParam(value = "bIncludeAlienAssignedTasks", defaultValue = "false", required = false) boolean bIncludeAlienAssignedTasks,
            @ApiParam(value = "sOrderBy", required = false) @RequestParam(value = "sOrderBy", defaultValue = "id", required = false) String sOrderBy,
            @ApiParam(value = "nSize", required = false) @RequestParam(value = "nSize", defaultValue = "10", required = false) Integer nSize,
            @ApiParam(value = "nStart", required = false) @RequestParam(value = "nStart", defaultValue = "0", required = false) Integer nStart,
            @ApiParam(value = "sFilterStatus", required = false) @RequestParam(value = "sFilterStatus", defaultValue = "OpenedUnassigned", required = false) String sFilterStatus,
            @ApiParam(value = "bFilterHasTicket", required = false) @RequestParam(value = "bFilterHasTicket", defaultValue = "false", required = false) boolean bFilterHasTicket,
            @ApiParam(value = "soaFilterField", required = false) @RequestParam(value = "soaFilterField", required = false) String soaFilterField,
            @ApiParam(value = "bIncludeVariablesProcess", required = false) @RequestParam(value = "bIncludeVariablesProcess", required = false, defaultValue = "false") Boolean bIncludeVariablesProcess)
            throws CommonServiceException {

        Map<String, Object> res = new HashMap<>();

        try {
            List<Group> groups = identityService.createGroupQuery().groupMember(sLogin).list();

            if (groups != null && !groups.isEmpty()) {
                List<String> groupsIds = new LinkedList<>();
                for (Group group : groups) {
                    groupsIds.add(group.getId());
                }
                LOG.info("Got list of groups for current user {} : {}", sLogin, groupsIds);

                Map<String, FlowSlotTicket> mapOfTickets = new HashMap<>();
                long totalNumber = 0;

                Object taskQuery = oActionTaskService.createQuery(sLogin, bIncludeAlienAssignedTasks, sOrderBy,
                        sFilterStatus, groupsIds, soaFilterField);
                // LOG.info("taskQuery: ", taskQuery );

                totalNumber = (taskQuery instanceof TaskInfoQuery) ? ((TaskInfoQuery) taskQuery).count()
                        : oActionTaskService.getCountOfTasksForGroups(groupsIds);
                LOG.info("total count before processing is: {}", totalNumber);

                long totalCountServices = 0;

                if (!"Documents".equalsIgnoreCase(sFilterStatus)) {
                    List<TaskInfo> aTaskInfo = (taskQuery instanceof TaskInfoQuery) ? ((TaskInfoQuery) taskQuery).list()
                            : (List) ((NativeTaskQuery) taskQuery).list();
                    if (aTaskInfo != null) {
                        LOG.info("all tasks size is {}", aTaskInfo.size());
                        for (TaskInfo oTaskInfo : aTaskInfo) {
                            if (!oTaskInfo.getProcessDefinitionId().startsWith("_doc")) {
                                totalCountServices++;
                            }
                        }
                    } else {
                        LOG.info("all tasks is null");
                    }
                }

                LOG.info("totalCountServices is {}", totalCountServices);
                int nStartBunch = nStart;
                List<TaskInfo> tasks = new LinkedList<>();
                long sizeOfTasksToSelect = nSize;
                if (bFilterHasTicket) {
                    sizeOfTasksToSelect = totalNumber;
                    nStartBunch = 0;
                }
                while ((tasks.size() < sizeOfTasksToSelect) && (nStartBunch < totalNumber)) {
                    LOG.info("Populating response with results. nStartFrom:{} nSize:{}", nStartBunch, nSize);
                    List<TaskInfo> currTasks = oActionTaskService.getTasksWithTicketsFromQuery(taskQuery, nStartBunch,
                            nSize, bFilterHasTicket, mapOfTickets);
                    tasks.addAll(currTasks);

                    nStartBunch += nSize;

                    if (!bFilterHasTicket) {
                        break;
                    }
                }

                int tasksSize = tasks.size();
                if (bFilterHasTicket) {
                    totalNumber = tasksSize;
                    if (tasksSize > nStart && tasksSize > (nStart + nSize)) {
                        tasks = tasks.subList(nStart, nStart + nSize);
                    } else if (tasksSize > nStart) {
                        tasks = tasks.subList(nStart, tasksSize);
                    } else {
                        LOG.info("Number of tasks with FlowSlotTicket is less than starting point to fetch:{}",
                                tasksSize);
                        tasks.clear();
                    }
                }

                List<Map<String, Object>> data = new LinkedList<>();
                if ("ticketCreateDate".equalsIgnoreCase(sOrderBy)) {
                    oActionTaskService.populateResultSortedByTicketDate(bFilterHasTicket, tasks, mapOfTickets, data);
                } else {
                    oActionTaskService.populateResultSortedByTasksOrder(bFilterHasTicket, tasks, mapOfTickets, data);
                }

                List<Map<String, Object>> checkDocumentIncludesData = new LinkedList<>();

                //long documentListSize = 0;
                if (!"Documents".equals(sFilterStatus)) {

                    for (Map<String, Object> dataElem : data) {
                        if (!((String) dataElem.get("processDefinitionId")).startsWith("_doc")) {

                            if (bIncludeVariablesProcess) {
                                dataElem.put("globalVariables", runtimeService.getVariables((String) dataElem.get("processInstanceId")));
                            }
                            checkDocumentIncludesData.add(dataElem);
                            //totalNumber = totalNumber - 1;
                            //documentListSize++;
                        }
                    }
                } else {

                    for (Map<String, Object> dataElem : data) {
                        if (bIncludeVariablesProcess) {
                            dataElem.put("globalVariables", runtimeService.getVariables((String) dataElem.get("processInstanceId")));
                        }
                        checkDocumentIncludesData.add(dataElem);
                    }
                }

                LOG.info("checkDocumentIncludesData size: {}", checkDocumentIncludesData.size());

                res.put("data", checkDocumentIncludesData);
                res.put("size", nSize);
                res.put("start", nStart);
                res.put("order", "asc");
                res.put("sort", "id");

                if ("Documents".equalsIgnoreCase(sFilterStatus)) {
                    res.put("total", totalNumber);
                } else {
                    res.put("total", totalCountServices);
                }
            }
        } catch (Exception e) {
            LOG.error("Error occured while getting list of tasks", e);
        }
        return res;
    }

    @Deprecated
    private List<Map<String, Object>> filterTasks(List<Map<String, Object>> tasks,
            String soaFilterField) {
        JSONArray jsonArray = new JSONArray(soaFilterField);

        Map<String, String> mapOfFieldsToSort = new HashMap<String, String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject elem = (JSONObject) jsonArray.get(i);
            if (elem.has("sID") && elem.has("sValue")) {
                mapOfFieldsToSort.put(elem.getString("sID"), elem.getString("sValue"));
            } else {
                LOG.info("{} json element doesn't have either sID or sValue fields", i);
            }
        }
        LOG.info("Converted filter fields to the map {}", mapOfFieldsToSort);

        List<Map<String, Object>> res = new LinkedList<Map<String, Object>>();
        for (Map<String, Object> taskData : tasks) {
            boolean toSkip = false;
            for (Map.Entry<String, String> currentFilter : mapOfFieldsToSort.entrySet()) {
                LOG.info("Matching variables {} with the filter {}", taskData, currentFilter);
                if (!taskData.containsKey(currentFilter.getKey())
                        || !matchValues(taskData.get(currentFilter.getKey()), currentFilter.getValue())) {
                    LOG.info("Skipping task {}. It doesn't match filter {}", taskData, currentFilter);
                    toSkip = true;
                    break;
                }
            }
            if (toSkip) {
                continue;
            }
            LOG.info("Adding task {} as it matches pattern", taskData);
            res.add(taskData);
        }
        return res;
    }

    private boolean matchValues(Object value, String pattern) {
        LOG.info("Matching value {} with the pattern {}", value, pattern);
        if (pattern.contains("*")) {
            pattern = pattern.replace("*", "");
            return value.toString().startsWith(pattern);
        }
        return value.toString().matches(pattern);
    }

    @ApiOperation(value = "getCountTask", notes = "#####  ActionCommonTaskController: Получение количествоа задач по нескольким наборам критериев-фильтров для указанного логина #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/getCountTask?sLogin=[sLogin]?&amFilter=[amFilter]\n\n\n"
            + "Параметры:\n"
            + "- sLogin - имя пользователя для которого подсчитывать количества тасок\n"
            + "- amFilter - массив наборов фильтров, по которым выдавать количества задач. \n"
            + "Допустимые параметры внутри наборов фильтров amFilter: \n"
            + "sFilterStatus - статус фильтрации задач. Значение по умолчанию - OpenedUnassigned (только не-ассайнутые), OpenedAssigned(только ассайнутые), Opened(только открытые (не в истории)), Closed(только закрытые (история))) \n"
            + "bFilterHasTicket - по умолчанию false. Если true - возвращать только те задачи, у которых есть связанный тикет\n"
            + "bIncludeAlienAssignedTasks - по умолчанию false. Если значение false - то возвращать только свои и только не ассайнутые, к которым доступ.\n"
            + "Примеры:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getCountTask?sLogin=kermit&amFilter=[{\"bFilterHasTicket\":true},{\"sFilterStatus\":\"OpenedUnassigned\"},{\"sFilterStatus\":\"OpenedAssigned\",\"bFilterHasTicket\":true}]\n"
            + "Ответы: \n"
            + "\n```json\n"
            + "[ \n"
            + "{\"nCount\":5},\n"
            + "{\"nCount\":688},\n"
            + "{\"nCount\":0}\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getCountTask", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, Object>> getCountTask(@ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin") String sLogin,
            @ApiParam(value = "amFilter", required = true) @RequestParam(value = "amFilter") String amFilter) throws CommonServiceException {
        List<Map<String, Object>> res = new LinkedList<Map<String, Object>>();

        List<String> groupsIds = new LinkedList<String>();
        List<Group> groups = identityService.createGroupQuery().groupMember(sLogin).list();

        if (groups != null && !groups.isEmpty()) {
            for (Group group : groups) {
                groupsIds.add(group.getId());
            }
            LOG.info("Got list of groups for current user {} : {}", sLogin, groupsIds);
        }

        JSONArray jsonArray = new JSONArray(amFilter);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject elem = (JSONObject) jsonArray.get(i);

            String sFilterStatus = null;
            boolean bFilterHasTicket = false;
            boolean bIncludeAlienAssignedTasks = false;

            LOG.info("Current element {}", elem.toString());
            if (elem.has("sFilterStatus")) {
                sFilterStatus = (String) elem.get("sFilterStatus");
            } else {
                sFilterStatus = "OpenedUnassigned";
            }
            if (elem.has("bFilterHasTicket")) {
                bFilterHasTicket = (Boolean) elem.get("bFilterHasTicket");
                LOG.info("set bFilterHasTicket to {}", bFilterHasTicket);
            }
            if (elem.has("bIncludeAlienAssignedTasks")) {
                bIncludeAlienAssignedTasks = (Boolean) elem.get("bIncludeAlienAssignedTasks");
            }

            LOG.info("Selecting tasks sLogin:{} sFilterStatus:{} bIncludeAlienAssignedTasks:{}", sLogin, sFilterStatus, bIncludeAlienAssignedTasks);
            List<TaskInfo> taskQuery = oActionTaskService.returnTasksFromCache(sLogin, sFilterStatus, bIncludeAlienAssignedTasks, groupsIds, null);//

            long totalNumber = taskQuery.size();
            LOG.info("Retreived {} tasks", taskQuery.size());

            if (bFilterHasTicket) {
                LOG.info("Removing tasks which don't have tickets");
                List<TaskInfo> tasks = oActionTaskService.matchTasksWithTicketsFromQuery(sLogin, bFilterHasTicket, sFilterStatus, taskQuery);
                totalNumber = tasks.size();
            }

            Map<String, Object> currRes = new HashMap<String, Object>();
            currRes.put("nCount", totalNumber);
            res.add(currRes);
        }

        return res;
    }

    /**
     * Региональный сервис получения контента файла
     *
     *
     * @param nID_Message - номер-ИД сообщения
     * @param nID_Process - номер-ИД процесса
     * @throws CommonServiceException
     */
    @ApiOperation(value = " Центральный сервис получения контента файла", notes = "")
    @RequestMapping(value = "/getMessageFile_Local", method = {RequestMethod.GET})
    public void getMessageFile(
            @ApiParam(value = "номер-ИД сообщения", required = true) @RequestParam(value = "nID_Message", required = true) String nID_Message,
            @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            HttpServletResponse httpResponse) throws CommonServiceException {
        try {
            Map<String, String> params = new HashMap<>();
            if (nID_Process != null) {
                String sID_Order = generalConfig.getOrderId_ByProcess(nID_Process);
                params.put("sID_Order", sID_Order);
            }
            params.put("nID_Message", nID_Message);
            String sURL = generalConfig.getSelfHostCentral() + "/wf/service/subject/message/getMessageFile";
            byte[] soResponse = httpRequester.getInsideBytes(sURL, params);

            LOG.info("Size of file {}", soResponse.length);
            httpResponse.setContentType("application/octet-stream");
            httpResponse.setHeader("Content-disposition", "attachment");
            httpResponse.getOutputStream().write(soResponse);
            httpResponse.flushBuffer();

        } catch (Exception oException) {
            LOG.error("Can't get: {}", oException.getMessage());
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Can't get: " + oException.getMessage(), oException, HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Получения обьекта-записи линка", notes = "#####  ActionCommonTaskController: Сервис получения обьекта-записи линка #####\n\n"
            + "Request:\n\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/getLink?sKey=sKey&nID_Subject_Holder=nID_Subject_Holder\n\n\n"
            + "Response:\n"
            + "\n```json\n"
            + "  {\"sKey\":\"Cтрока-ключ\","
            + "       \"nID\":ИД обьекта,"
            + "       \"nID_Process\":ИД бизнес процесса,"
            + "       \"nID_Subject_Holder\":ИД субьекта-хранителя"
            + "    }\n"
            + "\n```\n")
    @RequestMapping(value = "/getLink", method = {RequestMethod.GET})
    @ResponseBody
    public ActionTaskLink getActionTaskLink(@ApiParam(value = "ИД бизнес процесса", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "Cтрока-ключ", required = true) @RequestParam(value = "sKey", required = true) String sKey,
            @ApiParam(value = "ИД субьекта-хранителя", required = true) @RequestParam(value = "nID_Subject_Holder", required = true) Long nID_Subject_Holder) throws Exception {

        return actionTaskLinkDao.getByCriteria(nID_Process, sKey, nID_Subject_Holder);
    }

    @ApiOperation(value = "Создание нового обьекта-записи линка", notes = "#####  ActionCommonTaskController: Сервис создания обьекта-записи линка #####\n\n"
            + "Request:\n\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/setLink?nID_Process=nID_Process&sKey=sKey&nID_Subject_Holder=nID_Subject_Holder\n\n\n"
            + "Response:\n"
            + "\n```json\n"
            + "  {\"sKey\":\"Cтрока-ключ\","
            + "       \"nID\":ИД обьекта,"
            + "       \"nID_Process\":ИД бизнес процесса,"
            + "       \"nID_Subject_Holder\":ИД субьекта-хранителя"
            + "    }\n"
            + "\n```\n")
    @RequestMapping(value = "/setLink", method = {RequestMethod.GET})
    @ResponseBody
    public ActionTaskLink setActionTaskLink(@ApiParam(value = "ИД бизнес процесса", required = true) @RequestParam(value = "nID_Process", required = true) Long nID_Process,
            @ApiParam(value = "Cтрока-ключ", required = true) @RequestParam(value = "sKey", required = true) String sKey,
            @ApiParam(value = "ИД субьекта-хранителя", required = true) @RequestParam(value = "nID_Subject_Holder", required = true) Long nID_Subject_Holder) throws Exception {

        return actionTaskLinkDao.setActionTaskLink(nID_Process, sKey, nID_Subject_Holder);
    }

    @ApiOperation(value = "Обработка изменения статуса документа в УкрДок", notes = "#####  ActionCommonTaskController: Обработка изменения статуса документа в УкрДок #####\n\n"
            + "Request:\n\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/callback/ukrdoc\n\n\n"
            + "Метод выполняет следующие действия:\n"
            + " - получает текущий статус документа из тега data -> docStateEvent -> state -> current \n"
            + " - получает номер документа из тега data -> docStateEvent -> tables -> CardsDocument -> CarIdDocument \n"
            + " - ищет процесс, с которым связан данный документ используя сущность ActionTaskLink. Если сущности такой еще нет - то производится поиск "
            + " по переменной процесса sID_Document, которая устанавливается при отправке документа в УкрДок, и осуществляется сохранение сущности ActionTaskLink\n"
            + " - после нахождения текущей активной задачи статус из УкрДока записывается в переменную sID_Document_UkrDoc задачи\n"
            + " - завершает текущую задачу. для следующей активной задачи ответственным назначает пользователя завершенной задачи. Если пользователя не было - то задача остается без активного пользователя\n"
            + "В теле запроса передается объект с информацией об изменении:\n"
            + "Образец документа:\n"
            + "\n```json\n"
            + "\"create_time\": \"2016.02.05 12:51:21\",\n"
            + "\"change_time\": \"2016.02.05 12:51:21\",\n"
            + "\"node_prev_id\": \"56b0b33edfb2840b2a5644c2\",\n"
            + "\"status\": \"processed\",\n"
            + "\"user_id\": 5850,\n"
            + "\"data\": {\n"
            + "    \"docStateEvent\": {\n"
            + "            \"state\": {\n"
            + "                    \"current\": \"Не завизирован\",\n"
            + "                    \"previous\": \"Создан\"\n"
            + "            },\n"
            + "            \"pk\": {\n"
            + "                    \"id\": 6569546,\n"
            + "                    \"year\": 2016\n"
            + "            },\n"
            + "            \"tables\": {\n"
            + "                    \"Executors\": {\n"
            + "                            \"body\": [\n"
            + "                                    [\n"
            + "                                            \"DD100262LVI\",\n"
            + "                                            null,\n"
            + "                                            null,\n"
            + "                                            \"2\",\n"
            + "                                            1,\n"
            + "                                            0,\n"
            + "                                            null,\n"
            + "                                            null,\n"
            + "                                            null,\n"
            + "                                            null,\n"
            + "                                            \"0\",\n"
            + "                                            null\n"
            + "                                    ]\n"
            + "                            ],\n"
            + "                            \"head\": {\n"
            + "                                    \"IdAddedMethod\": 11,\n"
            + "                                    \"Lightning\": 10,\n"
            + "                                    \"ExecutData\": 9,\n"
            + "                                    \"ControlDate\": 8,\n"
            + "                                    \"AttentionExecutData\": 7,\n"
            + "                                    \"InitData\": 6,\n"
            + "                                    \"KindOrder\": 5,\n"
            + "                                    \"VisOrder\": 4,\n"
            + "                                    \"KindExecutor\": 3,\n"
            + "                                    \"peo_peo_ldap_login\": 2,\n"
            + "                                    \"io_peo_ldap_login\": 1,\n"
            + "                                    \"peo_ldap_login\": 0\n"
            + "                            }\n"
            + "                    },\n"
            + "                    \"PunctAttending\": {\n"
            + "                            \"DataPunctAttending\": \"2016-02-05 12:51:20\",\n"
            + "                            \"NamePunctAttending\": \"Создание\",\n"
            + "                            \"peo_ldap_login\": \"DD100262LVI\"\n"
            + "                    },\n"
            + "                    \"CardsDocument\": {\n"
            + "                         \"Invizible\": null,\n"
            + "                         \"LastCoordDate\": null,\n"
            + "                         \"DocCreateData\": null,\n"
            + "                         \"CoordBonDays\": null,\n"
            + "                         \"Flavor\": null,\n"
            + "                         \"IdPetm\": null,\n"
            + "                         \"Lightning\": \"0\",\n"
            + "                         \"Locale\": \"RU\",\n"
            + "                         \"DocLastModifData\": \"2016-02-05 12:51:20\",\n"
            + "                         \"CoordRtrnDate\": \"2016-02-05 12:51:20\",\n"
            + "                         \"Prioritet\": \"Обычный\",\n"
            + "                         \"ClassId\": \"a\",\n"
            + "                         \"IdKindDoc\": 19,\n"
            + "                         \"IdKindIncomming\": 0,\n"
            + "                         \"IdGroup\": 1,\n"
            + "                         \"DocumentName\": \"Акт перерахунку сумки\",\n"
            + "                         \"NumberDocument\": \"0\",\n"
            + "                         \"CarIdDocument\": 6569546,\n"
            + "                         \"IdActivity\": 4842,\n"
            + "                         \"IdXMLT\": 8223,\n"
            + "                         \"peo_peo_ldap_login\": \"DD100262LVI\",\n"
            + "                         \"peo_ldap_login\": \"DD100262LVI\",\n"
            + "                         \"IdAttending\": 6569544\n"
            + "                 }\n"
            + "         },\n"
            + "         \"uniq\": 5777275,\n"
            + "         \"dlm\": \"2016-02-05 12:51:20\"\n"
            + "    },\n"
            + "    \"__conveyor_copy_task_result__\": \"ok\"\n"
            + "  }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/callback/ukrdoc", method = {RequestMethod.POST})
    public @ResponseBody
    ResponseEntity processUkrDocCallBack(@RequestBody String event) throws AccessServiceException {

        UkrDocEventHandler eventHandler = new UkrDocEventHandler();
        eventHandler.processEvent(event);

        LOG.info("Parsed document ID:{} and status:{} from event", eventHandler.getDocumentId(), eventHandler.getStatus());

        String documentId = eventHandler.getDocumentId();
        String documentIdFromPkSection = eventHandler.getPkDocumentId();
        String year = eventHandler.getYear();
        String status = eventHandler.getStatus();
        String nID_DocumentTemplate = eventHandler.getnID_DocumentTemplate();
        Boolean bHasFile = eventHandler.isbFile();

        String sKey = documentId + ":" + year;
        String sKeyFromPkSection = documentIdFromPkSection + ":" + year;

        //subject
        long ukrDocSubjectId = 1l;
        ActionTaskLink actionTaskLink = actionTaskLinkDao.getByCriteria(null, sKey, ukrDocSubjectId);
        ProcessInstance processInstance = null;
        if (actionTaskLink == null) {
            List<ProcessInstance> processes = runtimeService.createProcessInstanceQuery()
                    .variableValueEquals(CreateDocument_UkrDoc.UKRDOC_ID_DOCUMENT_VARIABLE_NAME, sKey).active().list();
            LOG.info("Found {} processes with urk doc variable name {}", processes.size(), sKey);

            if (processes.size() > 0) {
                processInstance = processes.get(0);
                actionTaskLink = actionTaskLinkDao.getByCriteria(Long.valueOf(processInstance.getId()), sKey, ukrDocSubjectId);
                if (actionTaskLink == null) {
                    LOG.info("ActionTaskLink is not found. Creating a new one");
                    actionTaskLinkDao.setActionTaskLink(Long.valueOf(processInstance.getId()), sKey, ukrDocSubjectId);
                }
            }
        } else {
            LOG.info("Found ActionTaskLink. Process Id is {}", actionTaskLink.getnIdProcess());
            processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(actionTaskLink.getnIdProcess().toString()).singleResult();
        }

        if (processInstance != null) {
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
            if (tasks != null) {
                LOG.info("Found {} tasks for the process instance {}", tasks.size(), processInstance.getId());
                String assignee = null;
                for (Task task : tasks) {

                    String sLogin = "user_UkrDoc"; // тех-логин УкрДок-а
                    Long nID_Task = Long.parseLong(task.getId());
                    if (oActionTaskService.checkAvailabilityTaskGroupsForUser(sLogin, nID_Task)) {
                        LOG.info("User {} have access to the Task {}", sLogin, nID_Task);

//                  if("usertask2".equalsIgnoreCase(task.getTaskDefinitionKey().trim())){ //костыль, убрать после валидации группы
                        assignee = task.getAssignee();
                        LOG.info("Processing task {} with assignee {}", task.getId(), task.getAssignee());
                        taskService.setVariable(task.getId(), "sStatusName_UkrDoc", status);
                        runtimeService.setVariable(task.getProcessInstanceId(), "sStatusName_UkrDoc", status);
                        runtimeService.setVariable(task.getProcessInstanceId(), "sID_Document_UkrDoc", sKeyFromPkSection);
                        taskService.setVariable(task.getId(), "nID_DocumentTemplate_UkrDoc", nID_DocumentTemplate);
                        runtimeService.setVariable(task.getProcessInstanceId(), "nID_DocumentTemplate_UkrDoc", nID_DocumentTemplate);
                        taskService.setVariable(task.getId(), "bFile_UkrDoc", bHasFile);
                        runtimeService.setVariable(task.getProcessInstanceId(), "bFile_UkrDoc", bHasFile);
                        LOG.info("Set variable sStatusName_UkrDoc {} and sID_Document_UkrDoc {} and nID_DocumentTemplate {} and bHasFile {} for process instance with ID {}",
                                status, sKeyFromPkSection, nID_DocumentTemplate, bHasFile, task.getProcessInstanceId());
                        taskService.complete(task.getId());
                        LOG.info("Completed task {}", task.getId());
//                  }
                    } else {
                        throw new AccessServiceException(AccessServiceException.Error.LOGIN_ERROR,
                                String.format("user '%s' not included in group 'group_UkrDoc' ot this usertask", sLogin));
                    }
                }

                LOG.info("Looking for a new task to set form properties and claim it to the user {}", assignee);
                tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
                LOG.info("Get {} active tasks for the process", tasks);
                for (Task task : tasks) {
                    TaskFormData formData = formService.getTaskFormData(task.getId());
                    for (FormProperty formProperty : formData.getFormProperties()) {
                        if (formProperty.getId().equals("sID_Document_UkrDoc")) {
                            LOG.info("Found form property with the id sID_Document_UkrDoc. Setting value {}", sKeyFromPkSection);
                            if (formProperty instanceof FormPropertyImpl) {
                                ((FormPropertyImpl) formProperty).setValue(sKeyFromPkSection);
                            }
                        } else if (formProperty.getId().equals("sStatusName_UkrDoc")) {
                            LOG.info("Found form property with the id sStatusName_UkrDoc. Setting value {}", status);
                            if (formProperty instanceof FormPropertyImpl) {
                                ((FormPropertyImpl) formProperty).setValue(status);
                            }
                        }
                    }
                    if (assignee != null) {
                        taskService.claim(task.getId(), assignee);
                        LOG.info("Claimed task {} for the user {}", task.getId(), assignee);
                    } else {
                        LOG.info("Task was not assigned");
                    }
                }

            } else {
                LOG.info("Active tasks have not found for the process {}", processInstance.getId());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sReturnSuccess", "OK");

        return JsonRestUtils.toJsonResponse(response);
    }

    @ApiOperation(value = "Сервис получения значения переменной процесса", notes = "#####  ActionCommonTaskController: Сервис получения значения переменной процесса #####\n\n"
            + "Request:\n\n"
            + "https://test.region.igov.org.ua/wf/service/action/task/getProcessVariableValue?nID_Process=[nID_Process]&sVariableName=[sVariableName]\n\n\n"
            + "nID_Process - ID процесса, в котором искать переменную\n"
            + "sVariableName - имя переменной, значение которой необходимо вернуть\n"
            + "Пример: https://test.region.igov.org.ua/wf/service/action/task/getProcessVariableValue?nProcessID=8965001&sVariableName=phone\n"
            + "Response:\n"
            + "\n```json\n"
            + "  {\"phone\":\"+380 50 960 0041\"}"
            + "\n```\n")
    @RequestMapping(value = "/getProcessVariableValue", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getProcessVariableValue(@ApiParam(value = "ID процесса", required = true) @RequestParam(value = "nID_Process") String nID_Process,
            @ApiParam(value = "Название переменнной процесса значение которой необходимо найти", required = true) @RequestParam(value = "sVariableName") String sVariableName) throws RecordNotFoundException {

        return JSONValue.toJSONString(oActionTaskService.getProcessVariableValue(nID_Process, sVariableName));
    }

    /**
     *
     * @param sLogin - Строка логин пользователя, меняющего пароль //@param
     * sPasswordOld - Строка старый пароль //@param sPasswordNew - Строка новый
     * пароль
     * @return
     * @throws CommonServiceException
     * @throws RuntimeException
     */
    @ApiOperation(value = "Сервис смены пароля пользователя в Activity", notes = "#### Примеры \n"
            + "Request: \n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/changePassword\n"
            + "sLoginOwner=kermit\n"
            + "sPasswordOld=kermit\n"
            + "sPasswordNew=kermit1\n"
            + "Response: Ok, 200 \n"
            + "\n ```json\n"
            + "{\n"
            + "\"userId\":\"kermit\",\n"
            + "\"userEmail\":\"kermit@activiti.org\",\n"
            + "\"userFirstName\":\"Kermit\",\n"
            + "\"userLastName\":\"The Frog\"\n"
            + "}\n"
            + "\n```\n"
            + "\n"
            + "Wrong sPasswordOld\n"
            + "Request:\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/changePassword\n"
            + "sLoginOwner=kermit\n"
            + "sPasswordOld=kermit45\n"
            + "sPasswordNew=kermit1\n"
            + "Response: Forbidden 403\n"
            + "\n ```json\n"
            + "{\n"
            + "\"code\":\"BUSINESS_ERR\",\n"
            + "\"message\":\"Password kermit45 is wrong\"\n"
            + "}\n"
            + "\n```\n"
            + "\n"
            + "Wrong sLogin\n"
            + "Request:\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/task/changePassword\n"
            + "sLoginOwner=kermit45\n"
            + "sPasswordOld=kermit\n"
            + "sPasswordNew=kermit1\n"
            + "Response: Forbidden 403\n"
            + "\n ```json\n"
            + "{\n"
            + "\"code\":\"BUSINESS_ERR\",\n"
            + "\"message\":\"Error! user has not been found\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/changePassword", method = {RequestMethod.POST})
    public @ResponseBody
    String changePassword(
            @ApiParam(value = "Строка логин пользователя, меняющего пароль", required = true) @RequestParam(value = "sLoginOwner", required = true) String sLogin,
            @ApiParam(value = "JSON-cnрока с двумя параметрами: sPasswordOld - Строка старый пароль; sPasswordNew - Строка новый пароль", required = true) @RequestBody(required = true) String sPasswords
    ) throws Exception {

        String sPasswordOld = null;
        String sPasswordNew = null;

        if (sPasswords != null) {
            Map<String, Object> mBody;
            try {
                mBody = (Map<String, Object>) JSONValue.parse(sPasswords);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error parse JSON body: " + e.getMessage());
            }
            if (mBody != null) {
                if (mBody.containsKey("sPasswordOld")) {
                    sPasswordOld = (String) mBody.get("sPasswordOld");
                } else {
                    throw new Exception("The sPasswordOld in RequestBody is not defined");
                }
                if (mBody.containsKey("sPasswordNew")) {
                    sPasswordNew = (String) mBody.get("sPasswordNew");
                } else {
                    throw new Exception("The sPasswordNew in RequestBody is not defined");
                }
            }
        }

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService = processEngine.getIdentityService();
        User user = null;

        LOG.info("User will be found by sLoginOwner {}", sLogin);
        user = identityService.createUserQuery().userId(sLogin).singleResult();

        if (user == null) {
            LOG.warn("Error! user has not been found");
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Error! user has not been found",
                    HttpStatus.FORBIDDEN
            );
        }

        if (!user.getPassword().equals(sPasswordOld)) {
            LOG.warn("The sPasswordOld parameter is not equal the user's password: {}");
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Password " + sPasswordOld + " is wrong",
                    HttpStatus.FORBIDDEN
            );

        }
        user.setPassword(sPasswordNew);

        try {
            identityService.saveUser(user);
        } catch (RuntimeException e) {
            LOG.warn("User with such name already exists in base: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            user = identityService.createUserQuery().userId(user.getId()).singleResult();
        } catch (Exception e) {
            LOG.warn("Error! user has not been found: message{}", e.getMessage());
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
            );
        }
        String userData = "{ \"userId\":\"" + user.getId() + "\", "
                + "\"userEmail\" : \"" + user.getEmail() + "\", "
                + "\"userFirstName\":\"" + user.getFirstName() + "\", "
                + "\"userLastName\":\"" + user.getLastName() + "\"}";

        return userData;
    }

    /**
     * установить/задеплоить новый БП
     *
     * @param sFileName
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/setBP", method = RequestMethod.POST)
    @ResponseBody
    public String setBP(@ApiParam(value = "Cтрока-название файла", required = true) @RequestParam(value = "sFileName", required = true) String sFileName,
            @ApiParam(value = "Новий БП") @RequestParam("file") MultipartFile file,
            HttpServletRequest req) throws CommonServiceException {
        try {
            InputStream inputStream = file.getInputStream();
            repositoryService.createDeployment().addInputStream(sFileName, inputStream).deploy();
            LOG.debug("BPMN file has been deployed to repository service");
            return "SUCCESS";
        } catch (Exception e) {
            String message = "The uploaded file is wrong, that is, either no file has been chosen in the multipart form or the chosen file has no content or it is broken.";
            LOG.debug(message);
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    message,
                    HttpStatus.FORBIDDEN
            );
        }

    }

    /**
     * получить загруженный БП
     *
     * @param sID
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/getBP", method = RequestMethod.GET)
    @ResponseBody
    public void getBP(@ApiParam(value = "строка-ID БП", required = true) @RequestParam(value = "sID", required = true) String sID,
            HttpServletResponse response)
            throws IOException {

        InputStream is = repositoryService.getProcessModel(sID);
        response.setContentType("application/xml; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + "testName.bpmn" + "\"");

        FileCopyUtils.copy(is, response.getOutputStream());
        is.close();
        response.getOutputStream().close();
    }

    /**
     * получить список БП с версиями (массив объектов)
     *
     * @param sID_BP	строка-ИД БП
     * @param sFieldType	строка-типа поля
     * @param sID_Field	строка-ИД поля
     * @return	список БП
     */
    @RequestMapping(value = "/getListBP", method = RequestMethod.GET)
    @ResponseBody
    public List<ProcDefinitionI> getListBP(
            @ApiParam(value = "строка-ИД БП //опциональный фильтр, иначе все", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "строка-типа поля //опциональный фильтр, иначе все", required = false) @RequestParam(value = "sFieldType", required = false) String sFieldType,
            @ApiParam(value = "строка-ИД поля //опциональный фильтр, иначе все", required = false) @RequestParam(value = "sID_Field", required = false) String sID_Field
    ) {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().processDefinitionId(sID_BP).list();

        List<ProcDefinitionI> procDefinitions = new ArrayList<>();
        ProcessDefinitionCover adapter = new ProcessDefinitionCover();

        if (sFieldType != null || sID_Field != null) {
            for (ProcessDefinition pd : processDefinitions) {

                String id = pd.getId();
                FormData formData = null;
                //formService.getStartFormData(id) produces NullPointerException
                try {
                    formData = formService.getStartFormData(id);
                    List<FormProperty> formProperties = formData.getFormProperties();
                    for (FormProperty fp : formProperties) {
                        String idFp = fp.getId();
                        String type = fp.getType().getName();
                        if (sFieldType != null && sID_Field != null) {
                            if (sFieldType.equalsIgnoreCase(type) && sID_Field.equalsIgnoreCase(idFp)) {
                                procDefinitions.add(adapter.apply(pd));
                                break;
                            }
                        } else if (sFieldType == null && sID_Field != null) {
                            if (sID_Field.equalsIgnoreCase(idFp)) {
                                procDefinitions.add(adapter.apply(pd));
                                break;
                            }
                        } else if (sFieldType.equalsIgnoreCase(type)) {
                            procDefinitions.add(adapter.apply(pd));
                            break;
                        }
                    }
                } catch (Exception e) {
                    LOG.debug(e.getMessage());
                }
            }
        } else {
            for (ProcessDefinition processDefinition : processDefinitions) {
                procDefinitions.add(adapter.apply(processDefinition));
            }
        }
        return procDefinitions;
    }

    /**
     * удалить список БП с версиями (массив объектов)
     *
     * @param sID_BP строка-ИД БП
     * @param sFieldType строка-типа поля
     * @param sID_Field	строка-ИД поля
     * @param sVersion	строка-версия БП
     * @return	количество удаленных БП
     */
    @RequestMapping(value = "/removeListBP", method = RequestMethod.GET)
    @ResponseBody
    public String removeListBP(@ApiParam(value = "строка-ИД БП //опциональный фильтр, иначе все", required = false)
            @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "строка-типа поля //опциональный фильтр, иначе все", required = false)
            @RequestParam(value = "sFieldType", required = false) String sFieldType,
            @ApiParam(value = "строка-ИД поля //опциональный фильтр, иначе все", required = false)
            @RequestParam(value = "sID_Field", required = false) String sID_Field,
            @ApiParam(value = "строка-версия БП //опциональный фильтр, иначе все", required = false)
            @RequestParam(value = "sVersion", required = false) String sVersion) {
        List<ProcessDefinition> processDefinitions = repositoryService
                .createProcessDefinitionQuery().processDefinitionId(sID_BP).list();

        for (ProcessDefinition pd : processDefinitions) {
            removeBP(pd, sFieldType, sID_Field, sVersion);
        }
        List<ProcessDefinition> afterRemove = repositoryService
                .createProcessDefinitionQuery().processDefinitionId(sID_BP).list();
        int diff = processDefinitions.size() - afterRemove.size();

        return diff + " BP have been removed";
    }

    /**
     * Удаляет процес если он попадает по параметрам
     *
     * @param pd
     * @param sFieldType
     * @param sID_Field
     * @param sVersion
     */
    private void removeBP(ProcessDefinition pd, String sFieldType, String sID_Field, String sVersion) {
        if (sFieldType == null && sID_Field == null) {
            if (sVersion == null) {
                repositoryService.deleteDeployment(pd.getDeploymentId());
            } else if (pd.getVersion() == Integer.parseInt(sVersion)) {
                repositoryService.deleteDeployment(pd.getDeploymentId());
            }
        }
        try {
            FormData formData = formService.getStartFormData(pd.getId());
            List<FormProperty> formProperties = formData.getFormProperties();
            for (FormProperty fp : formProperties) {
                String idFp = fp.getId();
                String type = fp.getType().getName();
                if (sVersion == null) {
                    if (sFieldType != null && sID_Field != null) {
                        if (sFieldType.equalsIgnoreCase(type) && sID_Field.equalsIgnoreCase(idFp)) {
                            repositoryService.deleteDeployment(pd.getDeploymentId());
                            break;
                        }
                    } else if (sFieldType == null && sID_Field != null) {
                        if (sID_Field.equalsIgnoreCase(idFp)) {
                            repositoryService.deleteDeployment(pd.getDeploymentId());
                            break;
                        }
                    } else if (sFieldType.equalsIgnoreCase(type)) {
                        repositoryService.deleteDeployment(pd.getDeploymentId());
                        break;
                    }
                } else if (pd.getVersion() == Integer.parseInt(sVersion)) {
                    if (sFieldType != null && sID_Field != null) {
                        if (sFieldType.equalsIgnoreCase(type) && sID_Field.equalsIgnoreCase(idFp)) {
                            repositoryService.deleteDeployment(pd.getDeploymentId());
                            break;
                        }
                    } else if (sFieldType == null && sID_Field != null) {
                        if (sID_Field.equalsIgnoreCase(idFp)) {
                            repositoryService.deleteDeployment(pd.getDeploymentId());
                            break;
                        }
                    } else if (sFieldType != null && sID_Field == null) {
                        if (sFieldType.equalsIgnoreCase(idFp)) {
                            repositoryService.deleteDeployment(pd.getDeploymentId());
                            break;
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
    }

    @ApiOperation(value = "/removeOldProcess", notes = "##### Удаление закрытых процессов из таблиц активити#####\n\n")
    @RequestMapping(value = "/removeOldProcess", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Integer> removeOldProcess(@ApiParam(value = "ид процесса", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "ид бизнес-процесса", required = false) @RequestParam(value = "sID_Process_Def", required = true) String sID_Process_Def,
            @ApiParam(value = "дата закрытия процесса с ", required = true, defaultValue = "2010-01-01") @RequestParam(value = "sDateFinishAt", required = true, defaultValue = "2010-01-01") String sDateFinishAt,
            @ApiParam(value = "дата закрытия процесса по ", required = true, defaultValue = "2050-01-01") @RequestParam(value = "sDateFinishTo", required = true, defaultValue = "2050-01-01") String sDateFinishTo,
            HttpServletResponse httpResponse) throws org.igov.io.db.kv.statical.exceptions.RecordNotFoundException, CommonServiceException {
        //получение через дао из таблички с файлами файлов
        Map<String, Integer> result = new LinkedHashMap<>();
        LOG.info("/removeProcess!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :)");
        try {
            if (nID_Process == null && sID_Process_Def == null) {
                throw new CommonServiceException("404", "You should at list add param nID_Process or nID_Process_Def");
            } else {
                for (Map.Entry<String, String> removeOldProcessQuery : queryLoader.getRemoveOldProcessQueries().entrySet()) {
                    String removeOldProcessQueryValue;
                    if (removeOldProcessQuery.getKey().startsWith("update") || removeOldProcessQuery.getKey().startsWith("delete_act_hi_procinst")) {
                        removeOldProcessQueryValue = removeOldProcessQuery.getValue().replaceFirst("%s", sID_Process_Def)
                                .replaceFirst("%dateAt", sDateFinishAt).replaceFirst("%dateTo", sDateFinishTo);
                        //removeOldProcessQueryValue = String.format(removeOldProcessQuery.getValue(), sID_Process_Def);
                        //removeOldProcessQueryValue = String.format(removeOldProcessQuery.getValue(), sID_Process_Def, sDateFinishAt, sDateFinishTo);
                    } else {
                        removeOldProcessQueryValue = removeOldProcessQuery.getValue();
                    }
                    result.put(removeOldProcessQueryValue, -1);
                    LOG.info(removeOldProcessQueryValue + " ...");
                    int countRowUpdated = processHistoryDao.removeOldProcess(removeOldProcessQueryValue, sID_Process_Def, sDateFinishAt, sDateFinishTo);
                    result.put(removeOldProcessQueryValue, countRowUpdated);
                    LOG.info(removeOldProcessQueryValue + ": " + countRowUpdated + " success!");
                }
            }
        } catch (Exception ex) {
            LOG.error("!!!error: ", ex);
            result.put(ex.getMessage(), 1);
        }
        return result;
    }

    @ApiOperation(value = "/closeProcess", notes = "##### Закрытие всех инстансов бизнес-процесса#####\n\n")
    @RequestMapping(value = "/closeProcess", method = RequestMethod.GET)
    public @ResponseBody
    void closeProcess(@ApiParam(value = "ид бизнес-процесса", required = true) @RequestParam(value = "sID_Process_Def", required = true) String sID_Process_Def,
            @ApiParam(value = "лимит количества заявок для удаления", required = false) @RequestParam(value = "nLimitCountRowDeleted", required = false) Integer nLimitCountRowDeleted) {
        if (nLimitCountRowDeleted != null) {
            deleteProccess.setLimitCountRowDeleted(nLimitCountRowDeleted);
        }
        deleteProccess.closeProcess(sID_Process_Def);
    }

    @ApiOperation(value = "/getAnswer_DFS", notes = "##### Получение ответов по процессам ДФС#####\n\n")
    @RequestMapping(value = "/getAnswer_DFS", method = RequestMethod.GET)
    public @ResponseBody
    String getAnswer_DFS(@ApiParam(value = "ИНН", required = true) @RequestParam(value = "INN", required = true) String INN,
            @ApiParam(value = "Порядковый номер документа в году", required = true) @RequestParam(value = "snCountYear", required = true) String snCountYear,
            @ApiParam(value = "ИНН", required = true) @RequestParam(value = "sID_Process", required = true) String sID_Process) throws Exception {
        Task task = taskService.createTaskQuery().processInstanceId(sID_Process.trim()).active().singleResult();
        LOG.info("task.getId: " + (task != null ? task.getId() : "no active task for sID_Process = " + sID_Process));
        String asID_Attach_Dfs = "";
        if (task != null) {
            asID_Attach_Dfs = dfsService.getAnswer(task.getId(), sID_Process, INN, snCountYear, "");
            if (asID_Attach_Dfs != null && asID_Attach_Dfs.length() > 0) {
                taskService.complete(task.getId());
            }
        }
        return asID_Attach_Dfs;
    }

    @ApiOperation(value = "/getAnswer_DFS_New", notes = "##### Получение ответов по процессам ДФС#####\n\n")
    @RequestMapping(value = "/getAnswer_DFS_New", method = RequestMethod.GET)
    public @ResponseBody
    String getAnswer_DFS_New(@ApiParam(value = "ИНН", required = true) @RequestParam(value = "INN", required = true) String INN,
            @ApiParam(value = "Порядковый номер документа в году", required = true) @RequestParam(value = "snCountYear", required = true) String snCountYear,
            @ApiParam(value = "ИНН", required = true) @RequestParam(value = "sID_Process", required = true) String sID_Process) throws Exception {
        Task task = taskService.createTaskQuery().processInstanceId(sID_Process.trim()).active().singleResult();
        LOG.info("task.getId: " + (task != null ? task.getId() : "no active task for sID_Process = " + sID_Process));
        String asID_Attach_Dfs = "";
        if (task != null) {
            asID_Attach_Dfs = dfsService_new.getAnswer(task.getId(), sID_Process, INN);
            if (asID_Attach_Dfs != null && asID_Attach_Dfs.length() > 0) {
                taskService.complete(task.getId());
            }
        }
        return asID_Attach_Dfs;
    }

    @ApiOperation(value = "/getDocumentStepRights", notes = "##### Получение списка прав у логина по документу#####\n\n")
    @RequestMapping(value = "/getDocumentStepRights", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getDocumentStepRights(@ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin", required = true) String sLogin, //String
            @ApiParam(value = "nID_Process", required = true) @RequestParam(value = "nID_Process", required = true) String nID_Process) throws Exception {

        long startTime = System.nanoTime();
        Map<String, Object> res = oDocumentStepService.getDocumentStepRights(sLogin, nID_Process + "");
        long stopTime = System.nanoTime();
        LOG.info("getDocumentStepRights total time execution is: " + String.format("%,12d", (stopTime - startTime)));
        return res;
    }

    @ApiOperation(value = "/getDocumentStepLogins", notes = "##### Получение списка прав у логина по документу#####\n\n")
    @RequestMapping(value = "/getDocumentStepLogins", method = RequestMethod.GET)
    public @ResponseBody
    //Map<String,Object> getDocumentStepLogins(@ApiParam(value = "nID_Process", required = true) @RequestParam(value = "nID_Process", required = true) String nID_Process) throws Exception {//String
    List<Map<String, Object>> getDocumentStepLogins(@ApiParam(value = "nID_Process", required = true)
            @RequestParam(value = "nID_Process", required = true) String nID_Process) throws Exception {//String
        return oDocumentStepService.getDocumentStepLogins(nID_Process + "");
    }

    //save curretn values to Form
    @ApiOperation(value = "saveForm", notes = "saveForm")
    @RequestMapping(value = "/saveForm", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public HttpServletRequest saveForm(
            @ApiParam(value = "проперти формы", required = false) @RequestBody String sParams, HttpServletRequest req)
            throws ParseException, CommonServiceException, IOException {
        StringBuilder osRequestBody = new StringBuilder();
        BufferedReader oReader = req.getReader();
        String line;
        if (oReader != null) {
            while ((line = oReader.readLine()) != null) {
                osRequestBody.append(line);
            }
        }
        try {
            LOG.info("osRequestBody " + osRequestBody.toString());
            org.json.simple.JSONObject jsonObj = (org.json.simple.JSONObject) new JSONParser().parse(osRequestBody.toString());
            LOG.info("Succ. parsing of input data passed");
            String nID_Task = null;
            if (jsonObj.containsKey("taskId")) {
                nID_Task = jsonObj.get("taskId").toString();
            } else {
                LOG.error("Variable \"taskId\" not found");
            }
            LOG.info("taskId = " + nID_Task);
            Map<String, String> values = new HashMap<>();
            org.json.simple.JSONArray dates = null;
            if (jsonObj.containsKey("properties")) {
                dates = (org.json.simple.JSONArray) jsonObj.get("properties");
            } else {
                LOG.error("Variable \"properties\" not found");
            }
            LOG.info("properties = " + dates.toJSONString());

            org.json.simple.JSONObject result;
            Iterator<org.json.simple.JSONObject> datesIterator = dates.iterator();
            while (datesIterator.hasNext()) {
                result = datesIterator.next();
                values.put(result.get("id").toString(), (String) result.get("value"));
            }
            formService.saveFormData(nID_Task, values);
            LOG.info("Process of update data finiched");
            return req;
        } catch (Exception e) {
            String message = "The process of update variables fail.";
            LOG.debug(message);
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    message,
                    HttpStatus.FORBIDDEN);
        }

    }

    /**
     * Returns business processes which belong to a specified user
     *
     * @param sLogin - login of user in user activity
     * @param bDocOnly
     * @return
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
    @RequestMapping(value = "/getBPs", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    List<Map<String, String>> getBusinessProcesses(
            @ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin", required = true) String sLogin,
            @ApiParam(value = "Выводить только список БП документов", required = false) @RequestParam(value = "bDocOnly", required = false, defaultValue = "true") Boolean bDocOnly
    )
            throws IOException {

        //String jsonRes = JSONValue.toJSONString(oActionTaskService.getBusinessProcessesForUser(sLogin));
        //LOG.info("Result: {}", jsonRes);
        return oActionTaskService.getBusinessProcessesOfLogin(sLogin, bDocOnly);
    }

    @ApiOperation(value = "Получение списка полей бизнес процессов, к которым у пользователя есть доступ", notes = "#####  ActionCommonTaskController: Получение списка полей бизнес процессов к которым у пользователя есть доступ #####\n\n"
            + "HTTP Context: https://test.region.igov.org.ua/wf/service/action/task/getFields?sLogin=userId\n\n"
            + "Метод возвращает json со списком полей бизнес процессов, к которым у пользователя есть доступ, в формате:\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"sID\": \"ID field value\", \"sName\": \"[name of the field]\", \"sID_Type\": \"[type of the field]\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"sID\": \"ID field value\", \"sName\": \"[name of the field]\", \"sID_Type\": \"[type of the field]\"\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getFields", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @Transactional
    public @ResponseBody
    List<Map<String, String>> getBusinessProcessesFields(
            @ApiParam(value = "Логин пользователя", required = true) @RequestParam(value = "sLogin", required = true) String sLogin,
            @ApiParam(value = "Выводить только список БП документов", required = false) @RequestParam(value = "bDocOnly", required = false, defaultValue = "true") Boolean bDocOnly
    )
            throws IOException {

        //String jsonRes = JSONValue.toJSONString(oActionTaskService.getBusinessProcessesForUser(sLogin));
        //LOG.info("Result: {}", jsonRes);
        return oActionTaskService.getBusinessProcessesFieldsOfLogin(sLogin, bDocOnly);
    }

    @ApiOperation(value = "/setDocument", notes = "##### Создание документа (позже будет заменен на универсальній сервис /setProcess)#####\n\n")
    @RequestMapping(value = "/setDocument", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> setDocument(@ApiParam(value = "sLogin", required = true) @RequestParam(value = "sLogin", required = true) String sLogin, //String
            @ApiParam(value = "sID_BP", required = true) @RequestParam(value = "sID_BP", required = true) String sID_BP
    ) throws Exception {

        LOG.info("SetDocument in ActionTaskCommonController started...");
        LOG.info("sLogin in setDocument is {}", sLogin);
        /*if (sID_BP.startsWith("_doc_")||ConstantsInterceptor.DNEPR_MVK_291_COMMON_BP.contains(sID_BP)) {
                Integer count = ActionProcessCountUtils.callSetActionProcessCount(httpRequester, generalConfig, sID_BP, null);
                LOG.info("SetDocument process count: " + count.intValue());
        }*/
        //return oDocumentStepService.getDocumentStepRights(sLogin, nID_Process+"");
        Map<String, Object> mParam = new HashMap<>();
        mParam.put("sLoginAuthor", sLogin);
        //mParam.put("sLogin_LastSubmited", sLogin);
        ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey(sID_BP, mParam);
        Map<String, Object> mReturn = new HashMap<>();

        /*HistoryEvent_Service oHistoryEvent_Service = actionEventHistoryService.addActionStatus_Central(
                generalConfig.getOrderId_ByProcess(Long.parseLong(oProcessInstanceChild.getProcessInstanceId())), 
                subjectAccountDao.findSubjectAccounts(null, sLogin, null, null).get(0).getnID_Subject(), 
                HistoryEventType.getById(11L).getsName(), 
                null,
                null,
                null,
                null, 
                null,
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                null, 
                11L);

        LOG.info("HistoryEvent_Service elem in setDocument: {}", oHistoryEvent_Service.toString());*/
        mReturn.put("snID_Process", oProcessInstanceChild.getProcessInstanceId());
        LOG.info("mReturn={}", mReturn);

        return mReturn;
    }

    @ApiOperation(value = "/getProcessTemplate", notes = "##### Получение шаблона процесса#####\n\n")
    @RequestMapping(value = "/getProcessTemplate", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody
    Map<String, Object> setProcess(
            @ApiParam(value = "sLogin", required = false) @RequestParam(value = "sLogin", required = false, defaultValue = "kermit") String sLogin, //String
            @ApiParam(value = "sID_BP", required = true) @RequestParam(value = "sID_BP", required = true) String sID_BP
    ) throws Exception {

        //Map<String, Object> mParam = new HashMap<>();
        //mParam.put("sLoginAuthor", sLogin);
        //ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey(sID_BP, mParam);
        Map<String, Object> mReturn = new HashMap<>();

        LOG.info("Trying to get start form with ID " + sID_BP);
        List<ProcessDefinition> resProcessDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionKey(sID_BP).active().latestVersion().list();

        LOG.info("Loaded process definition ID from repository service:" + resProcessDefinitions);

        if (resProcessDefinitions != null && resProcessDefinitions.size() > 0) {
            LOG.info("Processing start form of process defiition:" + resProcessDefinitions.get(0).getKey() + ":" + resProcessDefinitions.get(0).getId());
            StartFormData formData = formService.getStartFormData(resProcessDefinitions.get(0).getId());

            LOG.info("Received form " + formData);
            Map<String, Object> formDataDTO = new HashMap<String, Object>();
            formDataDTO.put("formKey", formData.getFormKey());
            formDataDTO.put("deploymentId", formData.getDeploymentId());
            formDataDTO.put("formProperties", processFormProperties(formData.getFormProperties()));
            formDataDTO.put("processDefinitionId", formData.getProcessDefinition().getId());

            Map[] res = new Map[1];
            res[0] = formDataDTO;
            mReturn.put("data", res);
            mReturn.put("total", 1);
            mReturn.put("start", 0);
            mReturn.put("sort", "name");
            mReturn.put("order", "asc");
            mReturn.put("size", 1);

            LOG.info("mReturn={}", mReturn);
        } else {
            mReturn.put("data", new String[0]);
            mReturn.put("total", 0);
            mReturn.put("start", 0);
            mReturn.put("sort", "name");
            mReturn.put("order", "asc");
            mReturn.put("size", 0);
        }

        return mReturn;
    }

    protected List<Map<String, Object>> processFormProperties(List<FormProperty> formProperties) {
        List<Map<String, Object>> res = new LinkedList<Map<String, Object>>();
        for (FormProperty property : formProperties) {
            Map<String, Object> currProperty = new HashMap<String, Object>();
            currProperty.put("id", property.getId());
            currProperty.put("name", property.getName());
            currProperty.put("type", property.getType().getName());
            currProperty.put("value", property.getValue());
            currProperty.put("required", property.isRequired());
            currProperty.put("readable", property.isReadable());
            currProperty.put("writable", property.isWritable());
            if ("enum".equals(property.getType().getName())) {
                Object oValues = property.getType().getInformation("values");
                List<Map> enumValuesPossible = new LinkedList<Map>();
                if (oValues instanceof Map) {
                    Map<String, String> mValue = (Map) oValues;
                    for (Map.Entry<String, String> mapEntry : mValue.entrySet()) {
                        Map<String, Object> currEnumValue = new HashMap<String, Object>();
                        currEnumValue.put("id", mapEntry.getKey());
                        currEnumValue.put("name", mapEntry.getValue());
                        enumValuesPossible.add(currEnumValue);
                    }
                }
                currProperty.put("enumValues", enumValuesPossible);
            }
            res.add(currProperty);
        }
        return res;
    }

    @ApiOperation(value = "/startProcess", notes = "##### Старт процесса#####\n\n")
    @RequestMapping(value = "/startProcess", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody
    Map<String, Object> startProcess(@ApiParam(value = "sLogin", required = false) @RequestParam(value = "sLogin", required = false, defaultValue = "kermit") String sLogin, //String
            @ApiParam(value = "sID_BP", required = true) @RequestParam(value = "sID_BP", required = true) String sID_BP,
            @ApiParam(value = "JSON-щбъект с заполненными полями заполненной стартформы", required = true) @RequestBody String sJsonBody
    ) throws Exception {

        Map<String, Object> mParam = new HashMap<>();
        Map<String, Object> mJsonBody;
        try {
            mJsonBody = JsonRestUtils.readObject(sJsonBody, Map.class);
            if (mJsonBody != null) {
                if (mJsonBody.containsKey("properties")) {
                    LOG.info("Parsing properties: " + mJsonBody.get("properties"));

                    for (Map<String, Object> param : (List<Map<String, Object>>) mJsonBody.get("properties")) {
                        LOG.info("Parsing param: " + param);
                        mParam.put((String) param.get("id"), param.get("value"));
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parse JSON sJsonBody in request: " + e.getMessage());
        }

        mParam.put("sLoginAuthor", sLogin);
        LOG.info("Processing process with key " + StringUtils.substringBefore(sID_BP, ":"));
        ProcessInstance oProcessInstanceChild = runtimeService.startProcessInstanceByKey(StringUtils.substringBefore(sID_BP, ":"), mParam);
        Map<String, Object> mReturn = new HashMap<>();

        mReturn.put("snID_Process", oProcessInstanceChild.getProcessInstanceId());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(oProcessInstanceChild.getId()).active().list();
        if (tasks != null && tasks.size() > 0) {
            LOG.info("Found " + tasks.size() + " active tasks for the process instance " + oProcessInstanceChild.getProcessInstanceId());
            mReturn.put("nID_Task", tasks.get(0).getId());
        } else {
            LOG.warn("There are no active tasks for process instance " + oProcessInstanceChild.getId());
        }
        LOG.info("mReturn={}", mReturn);

        return mReturn;
    }

    @ApiOperation(value = "Обновление переменных задачи с ее опциональным завершением", notes = "#####  ActionCommonTaskController: Обновление переменных задачи с ее опциональным завершением #####\n\n"
            + "HTTP Context: https://alpha.test.region.igov.org.ua/wf/service/action/task/updateProcess\n\n"
            + "POST Метод. Принимает параметр bSaveOnly. Если bSaveOnly=true - Только обновление переменных задачи. Если false - заверешение задачи после обновления переменных:\n"
            + "Метод принимает json в теле запроса со списком переменных для обновления и номером задачи:\n"
            + "\n```json\n"
            + "{\n"
            + "  \"taskId\" : \"5\",\n"
            + "  \"properties\" : [\n"
            + "  {\n"
            + "  \"id\" : \"room\",\n"
            + "  \"value\" : \"normal\"\n"
            + "  }\n"
            + "  ]\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/updateProcess", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody
    Map<String, Object> updateProcess(@ApiParam(value = "bSaveOnly", required = false) @RequestParam(value = "bSaveOnly", required = false, defaultValue = "true") Boolean bSaveOnly,
            @ApiParam(value = "JSON-щбъект с заполненными полями заполненной стартформы", required = true) @RequestBody String sJsonBody
    ) throws Exception {

        Map<String, Object> mParam = new HashMap<>();
        Map<String, Object> mReturn = new HashMap<>();
        Map<String, Object> mJsonBody;
        String taskId = null;
        try {
            mJsonBody = JsonRestUtils.readObject(sJsonBody, Map.class);
            if (mJsonBody != null) {
                if (mJsonBody.containsKey("taskId")) {
                    LOG.info("Processsing task with ID: " + mJsonBody.get("taskId"));
                    taskId = (String) mJsonBody.get("taskId");
                    LOG.info("Updating task with ID " + taskId);
                } else {
                    throw new IllegalArgumentException("Object doesn't contain 'taskId' parameter");
                }

                if (mJsonBody.containsKey("properties")) {
                    LOG.info("Parsing properties: " + mJsonBody.get("properties"));

                    List<Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
                    String executionId = null;
                    if (tasks != null && tasks.size() > 0) {
                        Task firstTask = tasks.get(0);
                        executionId = firstTask.getExecutionId();

                        for (Map<String, Object> param : (List<Map<String, Object>>) mJsonBody.get("properties")) {
                            LOG.info("Updating variable : " + (String) param.get("id") + " with the value " + param.get("value"));
                            mParam.put((String) param.get("id"), param.get("value"));
                            runtimeService.setVariable(executionId, (String) param.get("id"), param.get("value"));
                        }

                        if (!bSaveOnly) {
                            LOG.info("Submitting task");
                            taskService.complete(firstTask.getId());
                        }

                        updateProcessHistoryEvent(firstTask.getProcessInstanceId(), mParam);
                    } else {
                        LOG.info("Have not found any tasks with ID " + taskId);
                    }
                    if (executionId != null) {
                        List<Task> activeTasks = taskService.createTaskQuery().executionId(executionId).active().list();
                        if (activeTasks != null && activeTasks.size() > 0) {
                            LOG.info("Found " + activeTasks.size() + " active tasks for the execution id " + executionId);
                            mReturn.put("taskId", tasks.get(0).getId());
                        } else {
                            LOG.warn("There are no active tasks for execution id " + executionId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error parse JSON sJsonBody in request: " + e.getMessage());
        }

        LOG.info("mReturn={}", mReturn);
        return mReturn;
    }

    public void updateProcessHistoryEvent(String processInstanceId, Map<String, Object> mParamDocument) throws ParseException {

        DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");

        ProcessInstance oProcessInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId).includeProcessVariables()
                .active().singleResult();

        if (oProcessInstance != null) {
            Map<String, Object> mProcessVariable = oProcessInstance.getProcessVariables();
            LOG.info("mProcessVariable: " + mProcessVariable);

            for (String sProcessVariable : mProcessVariable.keySet()) {

                try {
                    mProcessVariable.replace(sProcessVariable, df_StartProcess.format(mProcessVariable.get(sProcessVariable)));
                } catch (Exception ex) {
                }

                try {
                    mProcessVariable.replace(sProcessVariable, df_StartProcess.format(parseDate((String) mProcessVariable
                            .get(sProcessVariable))));
                } catch (Exception ex) {
                }

            }

            for (String sParamDocument : mProcessVariable.keySet()) {
                LOG.info("mProcessVariable param : " + " name: " + sParamDocument + " value: "
                        + mProcessVariable.get(sParamDocument));
            }

            Map<String, Object> mParamDocumentNew = new HashMap<>();

            for (String mKey : mParamDocument.keySet()) {

                Object oParamDocument = mParamDocument.get(mKey);
                Object oProcessVariable = mProcessVariable.get(mKey);
                if (oParamDocument != null) {
                    if (oProcessVariable != null) {
                        if (!(((String) oParamDocument).equals((String) oProcessVariable))) {
                            mParamDocumentNew.put(mKey, oParamDocument);
                        }
                    } else {
                        mParamDocumentNew.put(mKey, null);
                    }
                } else if (oProcessVariable != null) {
                    mParamDocumentNew.put(mKey, oProcessVariable);
                }
            }

            LOG.info("mParamDocumentNew: " + mParamDocumentNew);

            String sOldHistoryData = "<tr><td>";
            String sNewHistoryData = "<td>";

            if (!mParamDocumentNew.isEmpty()) {

                for (String mKey : mParamDocumentNew.keySet()) {
                    LOG.info("mProcessVariable.get(mKey): " + mProcessVariable.get(mKey));
                    LOG.info("mParamDocumentNew.get(mKey): " + mParamDocumentNew.get(mKey));

                    if (!mProcessVariable.get(mKey).equals(
                            mParamDocumentNew.get(mKey))) {
                        sOldHistoryData = sOldHistoryData + mKey + " : " + mProcessVariable.get(mKey) + "\n";
                        sNewHistoryData = sNewHistoryData + mKey + " : " + mParamDocumentNew.get(mKey) + "\n";
                    }
                }

                addEditHistoryEvent(oProcessInstance.getActivityId(), sNewHistoryData, sOldHistoryData, null, HistoryEvent_Service_StatusType.OPENED_ASSIGNED.getnID());
            }
        }
    }

    private Date parseDate(String sDate) throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        DateFormat df_StartProcess = new SimpleDateFormat("dd/MM/yyyy");
        Date oDateReturn;
        try {
            oDateReturn = df.parse(sDate);
        } catch (java.text.ParseException ex) {
            oDateReturn = df_StartProcess.parse(sDate);
        }
        return oDateReturn;
    }

    public void addEditHistoryEvent(String snID_Process_Activiti,
            String sNewHistoryData, String sOldHistoryData, String sLogin,
            Long nID_Status) {

        String sID_Order = generalConfig.getOrderId_ByProcess(Long.parseLong(snID_Process_Activiti));

        LOG.info("history data during task updating - snID_Process_Activiti: " + snID_Process_Activiti);
        LOG.info("history data during task updating - sID_Order: " + sID_Order);
        LOG.info("history data during task updating - sNewHistoryData: " + sNewHistoryData);
        LOG.info("history data during task updating - sOldHistoryData: " + sOldHistoryData);
        LOG.info("history data during task updating - sLogin: " + sLogin);

        Map<String, String> historyParam = new HashMap<>();

        historyParam.put("newData", sNewHistoryData + "</td></tr>");
        historyParam.put("oldData", sOldHistoryData + "</td>");
        historyParam.put("nID_StatusType", nID_Status.toString());
        historyParam.put("sLogin", sLogin);

        try {
            actionEventHistoryService
                    .addHistoryEvent(sID_Order, sLogin, historyParam,
                            HistoryEventType.ACTIVITY_STATUS_NEW.getnID());
        } catch (Exception ex) {
            LOG.info("Error saving history during document editing: {}", ex);
        }

    }

}
