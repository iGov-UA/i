package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.*;
import liquibase.util.csv.CSVWriter;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityInsedeCover;
import org.igov.model.action.event.HistoryEvent;
import org.igov.model.action.event.HistoryEventDao;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.document.DocumentDao;
import org.igov.model.subject.Server;
import org.igov.model.subject.ServerDao;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


@Controller
@Api(tags = { "ActionEventController -- События по действиям и статистика" })
@RequestMapping(value = "/action/event")
public class ActionEventController {

    private static final Logger LOG = LoggerFactory.getLogger(ActionEventController.class);
    @Autowired
    public GeneralConfig generalConfig;
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    @Autowired
    private HistoryEventDao historyEventDao;
    @Autowired
    private ActionEventService oActionEventService;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    private HttpEntityInsedeCover oHttpEntityInsedeCover;
    @Autowired
    private SubjectMessagesDao subjectMessagesDao; 
    @Autowired    
    private DocumentDao documentDao;

    @ApiOperation(value = "Получить объект события по услуге", notes = "##### Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/getHistoryEvent_Service?nID_Protected=11\n"
            + "для sID_Order проверяется соответствие формату (должен содержать \"-\"), если черточки нету -- то перед строкой добавляется \"0-\"\n"
            + "для nID_Protected проверяется его корректность , где последняя цифра - это последний разряд контрольной суммы (по алгоритму Луна) для всего числа без нее.\n"
            + "- если не совпадает -- возвращается ошибка \"CRC Error\" (код состояния HTTP 403)\n"
            + "- если совпадает -- ищется запись по nID_Process = nID_Protected без последней цифры (берется последняя по дате добавления)\n"
            + "- Если не найдена запись, то возвращает объект ошибки со значением \"Record not found\" (код состояния HTTP 403)\n"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "CRC Error или Record not found")})
    @RequestMapping(value = "/getHistoryEvent_Service", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service getHistoryEvent_Service(
            @ApiParam(value = "строка-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order, "
                    + "первая часть -- ид сервера, где расположена задача, "
                    + "вторая часть -- nID_Protected, ид задачи + контрольная сумма по алгоритму Луна", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order
            //, @ApiParam(value = "номер-ид субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject
            //, @ApiParam(value = "токен", required = false) @RequestParam(value = "sToken", required = false) String sToken
            ) throws CommonServiceException {

        return oActionEventService.getHistoryEventService(sID_Order);
    }

    @ApiOperation(value = "добавляет объект события по услуге", notes = ""
            + "при добавлении сначала проверяется, не было ли уже такой записи для данного nID_Process и nID_Server. если было -- ошибка Cannot create event_service with the same nID_Process and nID_Server!\n\n"
            + "потом генерируется поле nID_Protected по принципу: nID_Protected = nID_Process (ид задачи) + \"контрольная цифра\"\n\n"
            + "контрольная цифра -- это последний разряд суммы цифр числа по алгоритму Луна (для проверки корректности запрашиваемого ид записи) \n\n"
            + "также генерируется поле sID_Order по принципу: sID_Order = nID_Server + \"-\" + nID_Protected\n\n"
            + "пример: http://test.igov.org.ua/wf/service/action/event/addHistoryEvent_Service?nID_Process=2&sUserTaskName=new&nID_Subject=2&sProcessInstanceName=test_bp\n\n"
            + "ответ:\n\n"
            + "\n```json\n"
            + "{\n"
            + "    \"sID\":null,\n"
            + "    \"nID_Task\":2,\n"
            + "    \"nID_Subject\":2,\n"
            + "    \"sStatus\":\"new\",\n"
            + "    \"sUserTaskName\":\"new\",\n"
            + "    \"sDate\":\"2015-11-09 18:50:02.772\",\n"
            + "    \"nID_Service\":null,\n"
            + "    \"nID_Region\":null,\n"
            + "    \"sID_UA\":null,\n"
            + "    \"nRate\":0,\n"
            + "    \"soData\":\"[]\",\n"
            + "    \"sToken\":null,\n"
            + "    \"sHead\":null,\n"
            + "    \"sBody\":null,\n"
            + "    \"nTimeMinutes\":null,\n"
            + "    \"sID_Order\":\"0-22\",\n"
            + "    \"nID_Server\":0,\n"
            + "    \"nID_Protected\":22,\n"
            + "    \"nID\":40648\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/addHistoryEvent_Service", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service addHistoryEvent_Service(
            @ApiParam(value = "строка-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "ИД-номер", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "строка-статус", required = true) @RequestParam(value = "sUserTaskName") String sUserTaskName,
            @ApiParam(value = "ид услуги", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
            @ApiParam(value = "ид области", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @ApiParam(value = "ид страны", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-объект с данными (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "номер-ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "номер-ид бп эсклации (поле на перспективу для следующего тз по эскалации)", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType            
    ) {
        return oActionEventService.addActionStatus_Central(
                sID_Order,
                nID_Subject,
                sUserTaskName,
                nID_Service,
                nID_Region,
                sID_UA,
                soData,
                sToken,
                sHead,
                sBody,
                nID_Proccess_Feedback,
                nID_Proccess_Escalation,
                nID_StatusType            
        );
    }

    @ApiOperation(value = "Обновить объект события по услуге", notes = "##### Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/updateHistoryEvent_Service?nID_Process=1&sUserTaskName=finish \n"
            + "Также при апдейте охраняется информация о действии в Моем Журнале 1) запись \"Ваша заявка №[nID_Process] змiнила свiй статус на [sUserTaskName]\" "
            + "2) если есть параметр soData, то еще создается запись в виде:\n\n"
            + "- \"По заявці №[nID_Process] задане прохання уточнення: [sBody]\" (если sToken не пустой) -- согласно сервису запроса на уточнение\n"
            + "- \"По заявці №[nID_Process] дана відповідь громадянином: [sBody]\" (если sToken пустой) -- согласно сервису ответа на запрос по уточнению\n\n"
            + "плюс перечисление полей из soData в формате таблицы Поле / Тип / Текущее значение")
    @RequestMapping(value = "/updateHistoryEvent_Service", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service updateHistoryEvent_Service(
            @ApiParam(value = "строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "строка-статус", required = false) @RequestParam(value = "sUserTaskName", required = false) String sUserTaskName,
            @ApiParam(value = "строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "строка-время обработки задачи (в минутах, опционально)", required = false) @RequestParam(value = "nTimeMinutes", required = false) String nTimeMinutes,
            @ApiParam(value = "номер-ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "номер-ид бп эсклации (поле на перспективу для следующего тз по эскалации)", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType
    ) throws CommonServiceException {
        return oActionEventService.updateActionStatus_Central(
            sID_Order,
            sUserTaskName,
            soData,
            sToken,
            sBody,
            nTimeMinutes,
            nID_Proccess_Feedback,
            nID_Proccess_Escalation,
             nID_StatusType
        );
    }

    @ApiOperation(value = "Проверка наличия задачи определенного Бизнес процесса (БП), указанного гражданина", notes =
            "##### Примеры:\n"
     + "http://test.igov.org.ua/wf/service/action/task/event/getLastTaskHistory?nID_Subject=2&nID_Service=1&sID_UA=1200000000\n\n"
     + "Ответ, если запись существует (HTTP status Code: 200 OK):\n"
     + "\n```json\n"
     + "{\n"
     + "\"sID\": \"2\",\n"
     + "\"nID_Task\": 2,\n"
     + "\"nID_Subject\": 2,\n"
     + "  \"sStatus\": \"processing\",\n"
     + "  \"sID_Status\": \"заявка в обработке\",\n"
     + "  \"sDate\": null,\n"
     + "  \"nID_Service\": 1,\n"
     + "  \"nID_Region\": 1,\n"
     + "  \"sID_UA\": \"1200000000\",\n"
     + "  \"nRate\": 0,\n"
     + "  \"soData\": \"[]\",\n"
     + "  \"sToken\": \"\",\n"
     + "  \"sHead\": \"\",\n"
     + "  \"sBody\": \"\",\n"
     + "  \"nTimeMinutes\": 0,\n"
     + "  \"sID_Order\": \"0-22\",\n"
     + "  \"nID_Server\": 0,\n"
     + "  \"nID_Protected\": null,\n"
     + "  \"nID\": 8\n"
     + "}\n\n"
     + "\n```\n"
     + "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n\n"
     + "\n```json\n"
     + "{\n"
     + "  \"code\": \"BUSINESS_ERR\",\n"
     + "  \"message\": \"Record not found\"\n"
     + "}\n"
     + "\n```\n")
     @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
     @RequestMapping(value = "/getLastTaskHistory", method = RequestMethod.GET)
     public
     @ResponseBody
     HistoryEvent_Service getLastTaskHistory(
     @ApiParam(value = "Номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
     @ApiParam(value = "Номер-ИД услуги", required = true) @RequestParam(value = "nID_Service", required = true) Long nID_Service,
     @ApiParam(value = "Строка-ИД места (по Украинскому классификатору)", required = true) @RequestParam(value = "sID_UA", required = true) String sID_UA)
     throws CommonServiceException {

     HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getLastTaskHistory(nID_Subject, nID_Service, sID_UA);
     if (oHistoryEvent_Service == null) {
     throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Record not found");
     }
     return oHistoryEvent_Service;
     }

    @ApiOperation(value = "Определения числа заявок по определенной услуге в рамках места и в отношении определенного субьекта", notes = "Нет описания")
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Record not found")})
    @RequestMapping(value = "/getCountOrders", method = RequestMethod.GET)
    public @ResponseBody
    String getCountOrders(
            @ApiParam(value = "Номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
            @ApiParam(value = "Номер-ИД услуги", required = true) @RequestParam(value = "nID_Service", required = true) Long nID_Service,
            @ApiParam(value = "Строка-ИД места (по Украинскому классификатору)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "Число-лимит заявок, по умолчанию без лимита", required = false) @RequestParam(value = "nLimit", required = false, defaultValue = "0") int nLimit,
            @ApiParam(value = "Булевый, true исключает закрытые из подсчета", required = false) @RequestParam(value = "bExcludeClosed", required = false, defaultValue = "false") Boolean bExcludeClosed)
            throws CommonServiceException {

        Map<String, Long> m = new HashMap<>();
        Long nOpened = (long) 0;
        List<HistoryEvent_Service> aHistoryEvent_Service = historyEventServiceDao.getOrdersHistory(nID_Subject, nID_Service, sID_UA, nLimit);
        for (HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service) {
            nOpened++;
            if (bExcludeClosed || oHistoryEvent_Service.getsUserTaskName().startsWith("Заявка закрита")) {
                nOpened--;
            }
        }
        m.put("nOpened", nOpened);
        return JSONValue.toJSONString(m);

    }

    //TODO: Сделать оограничение по строкам
    /*@RequestMapping(value = "/getHistoryEventsService", method = RequestMethod.GET)
    public
    @ResponseBody
    List<HistoryEvent_Service> getHistoryEventsService() {
        return historyEventServiceDao.findAll();
    }*/
    
    
    //################ HistoryEvent services ###################
    /**
     * сохранение события
     *
     * @param nID_Subject ИД-строка субъекта, который загрузил документ
     * (необязательное поле)???????????????????????????????????
     * @param nID_HistoryEventType ИД-номер типа документа (необязательное поле)
     * @param sEventName_Custom строка - кастомное описание документа
     * (необязательное поле)
     * @param sMessage строка - сохраняемое содержимое (обязательное поле)
     * @param nID_HistoryEvent_Service строка - id - сервиса HistoryEven (не обязательное поле)
     * @param nID_Document строка - id - документа (не обязательное поле)
     */
    @ApiOperation(value = "Сохранение события в Мой Журнал", notes = "##### Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/setHistoryEvent")
    @RequestMapping(value = "/setHistoryEvent", method = RequestMethod.POST)
    public @ResponseBody
    Long setHistoryEvent(
            @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @ApiParam(value = "ИД-номер типа документа", required = false) @RequestParam(value = "nID_HistoryEventType", required = false) Long nID_HistoryEventType,
            @ApiParam(value = "строка-кастомное описание документа", required = false) @RequestParam(value = "sEventName", required = false) String sEventName_Custom,
            @ApiParam(value = "строка-сохраняемое содержимое", required = true) @RequestParam(value = "sMessage") String sMessage,
            @ApiParam(value = "номер-id обьекта события по услуге", required = false) @RequestParam(value = "nID_HistoryEvent_Service", required = false) Long nID_HistoryEvent_Service,
            @ApiParam(value = "номер-id - документа", required = false) @RequestParam(value = "nID_Document", required = false) Long nID_Document)
            throws IOException {
	
        return historyEventDao.setHistoryEvent(nID_Subject,
                nID_HistoryEventType, sEventName_Custom, sMessage, nID_HistoryEvent_Service, nID_Document);
    }


    @ApiOperation(value = "Получение события истории по его id", notes = "\n"
            + "Пример 1 (Нет документа или HistoryEvent_Service связанных с этим событием):\n"
            + "https://test.igov.org.ua/wf/service/action/event/getHistoryEvent?nID=11\n\n"
            + "\n```json\n"
            + "{\n"
            + "  \"sMessage\": \"Новая заявка создана_11\",\n"
            + "  \"sDate\": \"2015-07-04 12:18:56.235\",\n"
            + "  \"oHistoryEvent_Service\": null,\n"
            + "  \"oDocument\": null,\n"
            + "  \"nID\": 11,\n"
            + "  \"nID_Subject\": 10,\n"
            + "  \"nID_HistoryEventType\": 6,\n"
            + "  \"sEventName\": \"Изменение статуса заявки\"\n"
            + "}\n"
            + "\n```\n"
            + "Пример 2 (Есть Документ и HistoryEvent_Service связанные с этим событием):\n"
            + "https://test.igov.org.ua/wf/service/action/event/getHistoryEvent?nID=12\n\n"
            + "\n```json\n"
            + "{\n"
            + "  \"sMessage\": \"Новая заявка создана_11\",\n"
            + "  \"sDate\": \"2015-07-04 12:18:56.235\",\n"
            + "  \"oHistoryEvent_Service\": {\n"
            + "    \"sID\": \"2\",\n"
            + "    \"nID_Task\": 2,\n"
            + "    \"nID_Subject\": 2,\n"
            + "    \"sUserTaskName\": \"processing\",\n"
            + "    \"sDate\": null,\n"
            + "    \"nID_Service\": 1,\n"
            + "    \"nID_Region\": 1,\n"
            + "    \"sID_UA\": \"1200000000\",\n"
            + "    \"nRate\": 0,\n"
            + "    \"soData\": \"[]\",\n"
            + "    \"sToken\": \"TokenValue\",\n"
            + "    \"sHead\": \"\",\n"
            + "    \"sBody\": \"\",\n"
            + "    \"nTimeMinutes\": 567,\n"
            + "    \"sID_Order\": \"0-22\",\n"
            + "    \"nID_Server\": 0,\n"
            + "    \"nID_Proccess_Feedback\": 0,\n"
            + "    \"nID_Proccess_Escalation\": 0,\n"
            + "    \"sID_Rate_Indirectly\": \"\",\n"
            + "    \"nID_StatusType\": 13,\n"
            + "    \"sID_StatusType\": \"Unknown\",\n"
            + "    \"sName_UA_StatusType\": \"Заявка у невідомому статусі\",\n"
            + "    \"nID_Protected\": null,\n"
            + "    \"nID\": 2\n"
            + "  },\n"
            + "  \"oDocument\": {\n"
            + "    \"sContentType\": \"text/plain\",\n"
            + "    \"oSignData\": \"{}\",\n"
            + "    \"contentType\": \"text/plain\",\n"
            + "    \"nID\": 7,\n"
            + "    \"sName\": \"Справка\",\n"
            + "    \"oDocumentType\": {\n"
            + "      \"bHidden\": false,\n"
            + "      \"nID\": 0,\n"
            + "      \"sName\": \"Квитанція про сплату\"\n"
            + "    },\n"
            + "    \"sID_Content\": \"0\",\n"
            + "    \"oDocumentContentType\": {\n"
            + "      \"nID\": 2,\n"
            + "      \"sName\": \"text/plain\"\n"
            + "    },\n"
            + "    \"sFile\": \"dd1.txt\",\n"
            + "    \"sDate_Upload\": \"2015-01-01\",\n"
            + "    \"sID_Subject_Upload\": \"2872618520\",\n"
            + "    \"sSubjectName_Upload\": \"ВасяПупкин3\",\n"
            + "    \"oSubject_Upload\": {\n"
            + "      \"sID\": \"ПАО\",\n"
            + "      \"sLabel\": \"ПАО ПриватБанк\",\n"
            + "      \"sLabelShort\": \"ПриватБанк\",\n"
            + "      \"aSubjectAccountContact\": null,\n"
            + "      \"nID\": 1\n"
            + "    },\n"
            + "    \"oSubject\": {\n"
            + "      \"sID\": \"\",\n"
            + "      \"sLabel\": \"Fozzie Bear\",\n"
            + "      \"sLabelShort\": \"\",\n"
            + "      \"aSubjectAccountContact\": null,\n"
            + "      \"nID\": 25\n"
            + "    }\n"
            + "  },\n"
            + "  \"nID\": 12,\n"
            + "  \"nID_Subject\": 10,\n"
            + "  \"nID_HistoryEventType\": 6,\n"
            + "  \"sEventName\": \"Изменение статуса заявки\"\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getHistoryEvent", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent getHistoryEvent(
            @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id) {
        return historyEventDao.getHistoryEvent(id);
    }

    @ApiOperation(value = "Загрузка событий", notes =
            "##### Пример: http://test.igov.org.ua/wf/service/action/event/getHistoryEvents?nID_Subject=10\n\n"
            + "В зависимости от параметра **bGrouped** к списку может применяться фильтр.\n\n"
            + "- Если **bGrouped = false** - выбираются все сущности для данного субъекта\n"
            + "- если **bGrouped = true**, то в список попадают только уникальные сущности. Если сущности не уникальные, то из них отбирается только "
            + "одна с самым большим временем в поле sDate\n\n"             
            + "Уникальность сущности определяется путем сравнения полей **oHistoryEvent_Service, oDocument**\n\n"
            + "Алгоритм сравнения сущностей:\n\n"
            + "- если поля **oHistoryEvent_Service = null** и **oDocument=null**- сущности разные\n" 
            + "- если **oHistoryEvent_Service = null**, а **oDocument = не null** -сравнение идет только по **oDocument**\n" 
            + "- если **oHistoryEvent_Service = не null**, **а oDocument = null** - савнение идет только по **oHistoryEvent_Service**\n" 
            + "- если **oHistoryEvent_Service = не null** и **oDocument = не null** - сравнение идет и по **oHistoryEvent_Service** и по **oDocument**\n")
    @RequestMapping(value = "/getHistoryEvents", method = RequestMethod.GET)
    public @ResponseBody
    List<HistoryEvent> getHistoryEvents(
            @ApiParam(value = "номер-id авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject,
            @ApiParam(value =
                    "булевый флаг, если **true**, то возвращает только последнюю по дате (sDate) запись, из тех, "
                            + "у которых nID_HistoryEvent_Service или nID_Document - один и тот-же", required = false) @RequestParam(value = "bGrouped", required = false, defaultValue = "false") Boolean bGrouped) {
        return historyEventDao.getHistoryEvents(nID_Subject, bGrouped);
    }

    ////-------------Statistics--------
    /**
     * @param nID_Service ID сервиса.
     */
    @ApiOperation(value = "Получение количества записей HistoryEvent_Service для сервиса по регионам", notes =
            "##### Примеры:\n"
            + "https://test.igov.org.ua/wf/service/action/event/getStatisticServiceCounts?nID_Service=26\n\n"
            + "Результат\n"
            + "\n```json\n"
            + "[\n"
            + "  {\n"
            + "    \"nCount\": 5,\n"
            + "    \"nRate\": 0,\n"
            + "    \"nTimeMinutes\": \"0\",\n"
            + "    \"sName\": \"Київ\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"nCount\": 15,\n"
            + "    \"nRate\": 0,\n"
            + "    \"nTimeMinutes\": \"2\",\n"
            + "    \"sName\": \"Дніпропетровська\"\n"
            + "  }\n"
            + "]"
            + "\n```\n")
    @RequestMapping(value = "/getStatisticServiceCounts", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getStatisticServiceCounts(
            @ApiParam(value = "номер-ид сервиса(услуги)", required = true) @RequestParam(value = "nID_Service") Long nID_Service) {

        List<Map<String, Object>> listOfHistoryEventsWithMeaningfulNames = oActionEventService.getListOfHistoryEvents(nID_Service);
        return JSONValue.toJSONString(listOfHistoryEventsWithMeaningfulNames);
    }

    @ApiOperation(value = "Получение отчета о поданных заявках", notes =
            "##### Возвращает csv файл с информацией о задачах за указанный период на основании HistoryEventService записей.\n"
            + "В результате возвращаются следующий набор полей - sID_Order, nID_Server, nID_Service, sID_Place, nID_Subject, nRate, sTextFeedback, sUserTaskName, sHead, sBody, nTimeMinutes, sPhone\n"
			+ "Результат для колонки sTextFeedback возвращается из сущности SubjectMessage, у которой nID_SubjectMessageType = 2\n"
			+ "Результат для колонки sPhone возвращается из стартовой формы процесса из поля phone соответствующего регионального сервера\n"
			+ "Примеры:\n"
            + "https://test.igov.org.ua/wf/service/action/event/getServiceHistoryReport?sDateAt=2016-02-09 00:00:00&sDateTo=2016-02-11 00:00:00\n\n"
            + "Результат\n"
            + "\n```csv\n"
            + "sID_Order,nID_Server,nID_Service,sID_Place,nID_Subject,nRate,sTextFeedback,sUserTaskName,sHead,sBody,nTimeMinutes,sPhone\n"
			+ "0-88625055,0,740,6500000000,20045,,,,,Необхідно уточнити дані, за коментарем: не вірно вказані дані членів родини. Син - не відповідні ПІБ, бат - відсутні обов'язкові дані,,+380 97 225 5363\n"
            + "\n```\n")
    @RequestMapping(value = "/getServiceHistoryReport", method = RequestMethod.GET)
    public void getServiceHistoryReport(
            @ApiParam(value = "строка-Дата начала выборки данных в формате yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(value = "sDateAt") String sDateAt,
            @ApiParam(value = "строка-Дата окончания выборки данных в формате yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(value = "sDateTo") String sDateTo,
            HttpServletResponse httpResponse){
    	DateTime dateAt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(sDateAt);
    	DateTime dateTo = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(sDateTo);
    	
    	String[] headersMainField = { "sID_Order", "nID_Server",
                "nID_Service", "sID_Place", "nID_Subject", "nRate", "sTextFeedback", "sUserTaskName", "sHead", 
                "sBody", "nTimeMinutes", "sPhone" };
    	List<String> headers = new ArrayList<String>();
        headers.addAll(Arrays.asList(headersMainField));
    	
    	httpResponse.setHeader("Content-disposition", "attachment; filename="
                + "serviceHistoryReport.csv");
    	httpResponse.setHeader("Content-Type", "text/csv; charset=UTF-8");
    	
    	CSVWriter csvWriter;
		try {
            csvWriter = new CSVWriter(httpResponse.getWriter(), ';',
                    CSVWriter.NO_QUOTE_CHARACTER);
	        csvWriter.writeNext(headers.toArray(new String[headers.size()]));
	        
	    	List<HistoryEvent_Service> historyEvents = historyEventServiceDao.getHistoryEventPeriod(dateAt, dateTo);
	    	
	    	LOG.info("Found {} history events for the period from {} to {}", historyEvents.size(), sDateAt, sDateTo);
	    	
	    	if (historyEvents.size() > 0){
	            List<Long> historyEventServicesIDs = new LinkedList<Long>(); 
	            for (HistoryEvent_Service historyEventService : historyEvents){
	            	historyEventServicesIDs.add(historyEventService.getId());
	            }
	            LOG.info("Looking history event services by IDs " + historyEventServicesIDs);
	            List<SubjectMessage> subjectMessages = subjectMessagesDao.findAllByInValues("nID_HistoryEvent_Service", historyEventServicesIDs);
	            LOG.info("Found {} subject messages by nID_HistoryEvent_Service values", subjectMessages.size());
	            Map<Long, SubjectMessage> subjectMessagesMap = new HashMap<Long, SubjectMessage>();
	            for (SubjectMessage subjectMessage : subjectMessages){
	            	if (subjectMessage.getSubjectMessageType().getId() == 2) {
	            		subjectMessagesMap.put(subjectMessage.getnID_HistoryEvent_Service(), subjectMessage);
	            	} 
	            }
	            
		    	for (HistoryEvent_Service historyEventService : historyEvents){
		    		List<String> line = new LinkedList<String>();
		    		// sID_Order
		    		line.add(historyEventService.getsID_Order());
		    		// nID_Server
		    		line.add(historyEventService.getnID_Server() != null ? historyEventService.getnID_Server().toString() : "");
		    		// nID_Service
		    		line.add(historyEventService.getnID_Service() != null ? historyEventService.getnID_Service().toString() : "");
		    		// sID_Place
		    		line.add(historyEventService.getsID_UA());
		    		// nID_Subject
		    		line.add(historyEventService.getnID_Subject() != null ? historyEventService.getnID_Subject().toString() : "");
		    		// nRate
		    		line.add(historyEventService.getnRate() != null ? historyEventService.getnRate().toString() : "");
		    		String sTextFeedback = "";
		    		if (subjectMessagesMap.get(historyEventService.getId()) != null){
		    			sTextFeedback = subjectMessagesMap.get(historyEventService.getId()).getBody();
		    		} else {
		    			LOG.error("Unable to find feedabck for history event with ID {}", historyEventService.getId());
		    		}
		    		// sTextFeedback
		    		line.add(sTextFeedback);
		    		// sUserTaskName
		    		line.add(historyEventService.getsUserTaskName());
		    		// sHead
		    		line.add(historyEventService.getsHead());
		    		// sBody
		    		line.add(historyEventService.getsBody());
		    		// nTimeMinutes
		    		line.add(historyEventService.getnTimeMinutes() != null ? historyEventService.getnTimeMinutes().toString() : "");
		    		
		    		Integer nID_Server = historyEventService.getnID_Server();
		            nID_Server = nID_Server == null ? 0 : nID_Server;
	
			    	Optional<Server> oOptionalServer = serverDao.findById(new Long(nID_Server));
		            if (!oOptionalServer.isPresent()) {
		                throw new RecordNotFoundException("Server with nID_Server " + nID_Server + " wasn't found.");
		            }
		            Server oServer = oOptionalServer.get();
		            String sHost = oServer.getsURL();
		            
		            String sURL = sHost + "/service/action/task/getProcessVariableValue?nID_Process=" + historyEventService.getnID_Task() + "&sVariableName=phone";
		            ResponseEntity<String> osResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);
		            
		            JSONObject json = (JSONObject) new JSONParser().parse(osResponseEntityReturn.getBody());
		            // sPhone
		            line.add(json.get("phone") != null ? json.get("phone").toString() : "");
		            
		            csvWriter.writeNext(line.toArray(new String[line.size()]));
		    	}
	    	}
	    	csvWriter.close();
		} catch (Exception e) {
			LOG.error("Error occurred while creating CSV file {}", e.getMessage());
		} 
    }

}
