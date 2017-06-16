package org.igov.service.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityInsedeCover;
import org.igov.model.action.event.HistoryEvent;
import org.igov.model.action.event.HistoryEventDao;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.action.event.ServicesStatistics;
import org.igov.model.action.task.core.entity.ActionProcessCount;
import org.igov.model.action.task.core.entity.ActionProcessCountDao;
import org.igov.model.subject.Server;
import org.igov.model.subject.ServerDao;
import org.igov.model.subject.Subject;
import org.igov.model.subject.SubjectDao;
import org.igov.model.subject.SubjectHuman;
import org.igov.model.subject.SubjectHumanDao;
import org.igov.model.subject.message.SubjectMessageFeedback;
import org.igov.model.subject.message.SubjectMessageFeedbackDao;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import liquibase.util.csv.CSVWriter;

@Controller
@Api(tags = {"ActionEventController -- События по действиям и статистика"})
@RequestMapping(value = "/action/event")
public class ActionEventController implements ControllerConstants {

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
    private SubjectMessageFeedbackDao subjectMessageFeedbackDao;
    @Autowired
    private ActionProcessCountDao actionProcessCountDao;
    @Autowired
    private SubjectDao subjectDao;
    @Autowired
    private SubjectHumanDao subjectHumanDao;
    
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
    ) throws CommonServiceException, CRCInvalidException {

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
            @ApiParam(value = "ид под-услуги", required = false) @RequestParam(value = "nID_ServiceData", required = false) Long nID_ServiceData,
            @ApiParam(value = "ид области", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @ApiParam(value = "ид страны", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-объект с данными (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "номер-ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "номер-ид бп эсклации (поле на перспективу для следующего тз по эскалации)", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType,
            @ApiParam(value = "значение органа, в котором заказана услуга", required = false) @RequestParam(value = "sID_Public_SubjectOrganJoin", required = false) String sID_Public_SubjectOrganJoin
    ) {
        LOG.info("addHistoryEvent_Service was started with params: sID_Order: {} nID_Subject : {} sUserTaskName: {} "
                + "nID_Service: {} nID_ServiceData: {} nID_ServiceData: {} nID_Region: {} sID_UA: {} soData: {} "
                + "sToken: {} sHead: {} sBody: nID_Proccess_Feedback: {} nID_Proccess_Escalation: {} nID_StatusType: {}",
        sID_Order, nID_Subject, sUserTaskName, nID_Service, nID_ServiceData, nID_ServiceData, nID_Region, 
        sID_UA, soData, sToken, sHead, sBody, nID_Proccess_Feedback, nID_Proccess_Escalation, nID_StatusType,  sID_Public_SubjectOrganJoin);
        
        return oActionEventService.addActionStatus_Central(
                sID_Order,
                nID_Subject,
                sUserTaskName,
                nID_Service,
                nID_ServiceData,
                nID_Region,
                sID_UA,
                soData,
                sToken,
                sHead,
                sBody,
                nID_Proccess_Feedback,
                nID_Proccess_Escalation,
                nID_StatusType,
                sID_Public_SubjectOrganJoin
        );
    }

    @ApiOperation(value = "Обновить объект события по услуге", notes = "##### Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/updateHistoryEvent_Service?nID_Process=1&sUserTaskName=finish \n"
            + "Также при апдейте охраняется информация о действии в Моем Журнале 1) запись \"Ваша заявка №[nID_Process] змiнила свiй статус на [sUserTaskName]\" "
            + "2) если есть параметр soData, то еще создается запись в виде:\n\n"
            + "- \"По заявці №[nID_Process] задане прохання уточнення: [sBody]\" (если sToken не пустой) -- согласно сервису запроса на уточнение\n"
            + "- \"По заявці №[nID_Process] дана відповідь громадянином: [sBody]\" (если sToken пустой) -- согласно сервису ответа на запрос по уточнению\n\n"
            + "плюс перечисление полей из soData в формате таблицы Поле / Тип / Текущее значение")
    @RequestMapping(value = "/updateHistoryEvent_Service", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody
    HistoryEvent_Service updateHistoryEvent_Service(
            @ApiParam(value = "строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected", required = true) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "строка-статус", required = false) @RequestParam(value = "sUserTaskName", required = false) String sUserTaskName,
            @ApiParam(value = "строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "строка-время обработки задачи (в минутах, опционально)", required = false) @RequestParam(value = "nTimeMinutes", required = false) String nTimeMinutes,
            @ApiParam(value = "номер-ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "номер-ид бп эсклации (поле на перспективу для следующего тз по эскалации)", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType,
            @ApiParam(value = "строка информация о субьекте", required = false) @RequestParam(value = "sSubjectInfo", required = false) String sSubjectInfo,
            @ApiParam(value = "номер - ИД субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "JSON-строка для передачи всех параметров в POST-запросе", required = false) @RequestBody(required = false) String body
    // @ApiParam(value = "дата создания таски", required = false) @RequestParam(value = "sDateCreate", required =false) String sDateCreate,
    // @ApiParam(value = "дата закрытия таски", required = false) @RequestParam(value = "sDateClosed", required = false) String sDateClosed
    ) throws CommonServiceException {
        if(body != null){
            Map<String, Object> mBody;
            try {
                String decoded = "";
                try {
                    decoded = URLDecoder.decode(body, "UTF-8");
                } catch (UnsupportedEncodingException e){
                    decoded = body;
                }
                mBody = (Map<String, Object>) JSONValue.parse(decoded);
            } catch (Exception e){
                throw new IllegalArgumentException("Error parse JSON body: " + e.getMessage());
            }
            if(mBody != null){
                if (mBody.containsKey("sID_Order")) {
                    sID_Order = (String) mBody.get("sID_Order");
                }
                if (mBody.containsKey("sUserTaskName")) {
                    sUserTaskName = (String) mBody.get("sUserTaskName");
                }
                if (mBody.containsKey("soData")) {
                    soData = (String) mBody.get("soData");
                }
                if (mBody.containsKey("sToken")) {
                    sToken = (String) mBody.get("sToken");
                }
                if (mBody.containsKey("sBody")) {
                    sBody = (String) mBody.get("sBody");
                }
                if (mBody.containsKey("nTimeMinutes")) {
                    nTimeMinutes = (String) mBody.get("nTimeMinutes");
                }
                if (mBody.containsKey("nID_Proccess_Feedback")) {
                    nID_Proccess_Feedback = (Long) mBody.get("nID_Proccess_Feedback");
                }
                if (mBody.containsKey("nID_Proccess_Escalation")) {
                    nID_Proccess_Escalation = (Long) mBody.get("nID_Proccess_Escalation");
                }
                if (mBody.containsKey("nID_StatusType")) {
                    nID_StatusType = (Long) mBody.get("nID_StatusType");
                }
                if (mBody.containsKey("sSubjectInfo")) {
                    sSubjectInfo = (String) mBody.get("sSubjectInfo");
                }
                if (mBody.containsKey("nID_Subject")) {
                    nID_Subject = (Long) mBody.get("nID_Subject");
                }
            }
        }

        if(sID_Order == null || sID_Order.isEmpty() || sID_Order.equals("")){
            throw new CommonServiceException(HttpStatus.BAD_REQUEST.toString(), "sID_Orfer is undefined");
        }
        if(nID_StatusType == null){
            throw new CommonServiceException(HttpStatus.BAD_REQUEST.toString(), "nID_StatusType is undefined");
        }

        //LOG.info("sBody= " + sBody);
        //LOG.info("soData= " + soData);

        return oActionEventService.updateActionStatus_Central(
                sID_Order,
                sUserTaskName,
                soData,
                sToken,
                sBody,
                nTimeMinutes,
                nID_Proccess_Feedback,
                nID_Proccess_Escalation,
                nID_StatusType,
                sSubjectInfo,
                nID_Subject
        //   sDateCreate,
        //  sDateClosed
        );
    }

    @ApiOperation(value = "Проверка наличия задачи определенного Бизнес процесса (БП), указанного гражданина", notes
            = "##### Примеры:\n"
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
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Record not found")})
    @RequestMapping(value = "/getLastTaskHistory", method = RequestMethod.GET)
    public @ResponseBody
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

    @ApiOperation(value = "Определения числа заявок по определенной услуге в рамках места и в отношении определенного субьекта", notes
            = "Возвращает:\\n\\n\"\n"
            + "     + \"\\n```json\\n\"\n"
            + "     + \"{\\n\"\n"
            + "     + \"  \\\"nOpened\\\": \\\"количество открытых заявок\\\",\\n\"\n"
            + "     + \"  \\\"bIsLimitReached\\\": \\\"0 - если лимит по открытым заявкам не достигнут; 1 - лостигнут лимит\\\"\\n\"\n"
            + "     + \"}\\n\"\n"
            + "     + \"\\n```\\n\"")
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

        Map<String, Object> m = new HashMap<>();
        Long nOpened = (long) 0;
        Long nClosed = (long) 0;

        List<HistoryEvent_Service> aHistoryEvent_Service = historyEventServiceDao.getOrdersHistory(nID_Subject, nID_Service, sID_UA);

        for (HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service) {
            nOpened++;
            if (oHistoryEvent_Service.getsID_StatusType().toLowerCase().startsWith("closed")
                    || oHistoryEvent_Service.getsID_StatusType().toLowerCase().startsWith("removed")) {
                nClosed++;
            }
        }

        Long result = nOpened - nClosed;
        if (bExcludeClosed) {
            m.put("nOpened", result);
        } else {
            m.put("nOpened", nOpened);
        }

        if (nLimit == 0 || result <= nLimit) {
            m.put("bIsLimitReached", false);
        } else {
            m.put("bIsLimitReached", true);
        }
        return JSONValue.toJSONString(m);
    }

    @ApiOperation(value = "Определения числа заявок по определенной услуге в рамках места, в котором она была подана", notes
            = "Возвращает:\\n\\n\"\n"
            + "     + \"\\n```json\\n\"\n"
            + "     + \"{\\n\"\n"
            + "     + \"  \\\"countClaim\\\": \\\"число заявок по определенной услуге в рамках места\\\",\\n\"\n"
            + "     + \"}\\n\"\n"
            + "     + \"\\n```\\n\"")
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Record not found")})
    @RequestMapping(value = "/getCountClaimHistory", method = RequestMethod.GET)
    public @ResponseBody
    Long getCountClaimHistory(
            @ApiParam(value = "Строка-ИД места (по Украинскому классификатору)", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "Номер-ИД услуги", required = true) @RequestParam(value = "nID_Service", required = true) String nID_Service,
            @ApiParam(value = "Тип статуса заявки", required = true) @RequestParam(value = "nID_StatusType", required = true) String nID_StatusType)
            throws CommonServiceException {

        return historyEventServiceDao.getClaimCountHistory(sID_UA, Long.valueOf(nID_Service), Long.valueOf(nID_StatusType));
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
     * @param nID_HistoryEvent_Service строка - id - сервиса HistoryEven (не
     * обязательное поле)
     * @param nID_Document строка - id - документа (не обязательное поле)
     * @param sSubjectInfo строка-информация о субъекте (не обязательное поле)
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
            @ApiParam(value = "номер-id - документа", required = false) @RequestParam(value = "nID_Document", required = false) Long nID_Document,
            @ApiParam(value = "строка-информация о субъекте", required = false) @RequestParam(value = "sSubjectInfo", required = false) String sSubjectInfo)
            throws IOException {

        return historyEventDao.setHistoryEvent(nID_Subject,
                nID_HistoryEventType, sEventName_Custom, sMessage, nID_HistoryEvent_Service, nID_Document, sSubjectInfo);
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

    @ApiOperation(value = "Загрузка событий", notes
            = "Пример 1: http://test.igov.org.ua/wf/service/action/event/getServicesStatistics?nID_Subject=10\n\n"
            + "Пример 2: http://test.igov.org.ua/wf/service/action/event/getServicesStatistics?nID_Subject=10&nID_HistoryEvent_Service=2\n\n"
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
            @ApiParam(value = "id авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject,
            @ApiParam(value = "id HistoryEvent_Service. Если задан, то параметр добавляется к кретериям отбора", required = false) @RequestParam(value = "nID_HistoryEvent_Service", required = false) Long nID_HistoryEvent_Service,
            @ApiParam(value
                    = "булевый флаг, если **true**, то возвращает только последнюю по дате (sDate) запись, из тех, "
                    + "у которых nID_HistoryEvent_Service или nID_Document - один и тот-же", required = false) @RequestParam(value = "bGrouped", required = false, defaultValue = "false") Boolean bGrouped) {
        return historyEventDao.getHistoryEvents(nID_Subject, nID_HistoryEvent_Service, bGrouped);
    }

    ////-------------Statistics--------
    /**
     * @param nID_Service ID сервиса.
     */
    @ApiOperation(value = "Получение количества записей HistoryEvent_Service для сервиса по регионам", notes
            = "##### Примеры:\n"
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

    @ApiOperation(value = "Получение статистики по сервисам и регионам за заданный промежуток времени", notes
            = "##### Примеры:\n"
            + "https://test.igov.org.ua/wf/service/action/event/getServicesStatistic?sDate_from=2010-07-04 12:09:56&sDate_to=2019-07-04 12:09:56\n\n"
            + "Результат\n"
            + "\n```csv\n"
            + "nID_Service;ServiceName;SID_UA;placeName;nCountTotal;averageRate;averageTime\n"
            + "1;Надання довідки про притягнення до кримінальної відповідальності, відсутність (наявність) судимості або обмежень, передбачених кримінально-процесуальним законодавством України;1200000000;Дніпропетровська;4;0.0;7.516667\n"
            + "\n```\n")
    @RequestMapping(value = "/getServicesStatistic", method = RequestMethod.GET)
    public @ResponseBody
    void getServicesStatistic(
            @ApiParam(value = "дата \"С\", обязательный в формате YYYY-MM-DD hh:mm:ss", required = true) @RequestParam(value = "sDate_from") String sDate_from,
            @ApiParam(value = "дата \"По\", обязательный в формате YYYY-MM-DD hh:mm:ss", required = true) @RequestParam(value = "sDate_to") String sDate_to,
            HttpServletResponse httpResponse) {

        //parse date to check that it has appropriate form
        DateTime from = DateTime.parse(sDate_from, DateTimeFormat.forPattern("y-MM-d HH:mm:ss"));
        DateTime to = DateTime.parse(sDate_to, DateTimeFormat.forPattern("y-MM-d HH:mm:ss"));

        List<ServicesStatistics> servicesStatistics = oActionEventService.getServicesStatistics(from, to);

        String[] headingFields = {"nID_Service", "ServiceName", "SID_UA", "placeName", "nCountTotal", "nCountFeedback",
            "nCountEscalation", "averageRate", "averageTime"};
        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(headingFields));

        httpResponse.setHeader("Content-disposition", "attachment; filename=" + "ServicesStatistics.csv");
        httpResponse.setHeader("Content-Type", "text/csv; charset=UTF-8");

        CSVWriter csvWriter;
        try {
            csvWriter = new CSVWriter(httpResponse.getWriter(), ';', CSVWriter.NO_QUOTE_CHARACTER);
            csvWriter.writeNext(headers.toArray(new String[headers.size()]));

            for (ServicesStatistics item : servicesStatistics) {
                List<String> line = new LinkedList<>();
                line.add(String.valueOf(item.getnID_Service()));
                line.add(item.getServiceName());
                line.add(String.valueOf(item.getSID_UA()));
                line.add(item.getPlaceName());
                line.add(item.getnCountTotal() == null ? "0" : item.getnCountTotal().toString());
                line.add(item.getnCountFeedback() == null ? "0" : item.getnCountFeedback().toString());
                line.add(item.getnCountEscalation() == null ? "0" : item.getnCountEscalation().toString());
                line.add(item.getAverageRate() == null ? "0" : item.getAverageRate().toString());
                //divide average time (mins) to 60 to get hours
                line.add(item.getAverageTime() == null ? "0" : String.valueOf(item.getAverageTime().floatValue() / 60f));
                csvWriter.writeNext(line.toArray(new String[line.size()]));
            }
            csvWriter.close();
        } catch (Exception e) {
            LOG.error("Error occurred while creating CSV file {}", e.getMessage());
            LOG.error("stacktrace {}", ExceptionUtils.getStackTrace(e));
        }
    }

    @ApiOperation(value = SERVICE_HISTORY_REPORT, notes = SERVICE_HISTORY_REPORT_NOTES)
    @RequestMapping(value = "/getServiceHistoryReport", method = RequestMethod.GET)
    public void getServiceHistoryReport(
            @ApiParam(value = SERVICE_HISTORY_REPORT_DATE_AT, required = true)
            @RequestParam(value = "sDateAt") String sDateAt,
            @ApiParam(value = SERVICE_HISTORY_REPORT_DATE_TO, required = true)
            @RequestParam(value = "sDateTo") String sDateTo,
            @ApiParam(value = SERVICE_HISTORY_REPORT_ID_FILTER_DATE_TYPE, required = false)
            @RequestParam(value = "sID_FilterDateType", required = false, defaultValue = "Edit") String sID_FilterDateType,
            @ApiParam(value = SERVICE_HISTORY_REPORT_INCLUDE_TASK_INFO, required = false)
            @RequestParam(value = "bIncludeTaskInfo", required = false, defaultValue = "false") Boolean bIncludeTaskInfo,
            @ApiParam(value = SERVICE_HISTORY_REPORT_ID_SERVICE_EXCLUDE, required = false)
            @RequestParam(value = "sanID_Service_Exclude", required = false) String[] sanID_Service_Exclude,
            @ApiParam(value = SERVICE_HISTORY_REPORT_CODEPAGE, required = false)
            @RequestParam(value = "sCodepage", required = false, defaultValue = "windows-1251") String sCodepage,
            HttpServletResponse oHttpServletResponse) {
        LOG.info("{Enter into function}");
        DateTimeFormatter oDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        LOG.info("oDateFormat" + oDateFormat.toString());
        DateTime oDateAt = oDateFormat.parseDateTime(sDateAt);
        LOG.info("oDateAt" + oDateAt.toString());
        DateTime oDateTo = oDateFormat.parseDateTime(sDateTo);
        LOG.info("oDateTo" + oDateTo.toString());
        Charset charset = Charset.availableCharsets().getOrDefault(sCodepage, Charset.forName("windows-1251"));
        List<HistoryEvent_Service> aHistoryEvent_Service = new ArrayList<>();

        String[] headersMainField = {"sID_Order", "nID_Server",
            "nID_Service", "sID_Place", "nID_Subject", "nRate", "sTextFeedback", "sUserTaskName", "sHead",
            "sBody", "nTimeMinutes", "sPhone", "nID_ServiceData", "sDateCreate", "sDateClose"};
        List<String> asHeader = new ArrayList<>();
        asHeader.addAll(Arrays.asList(headersMainField));

        oHttpServletResponse.setHeader("Content-disposition", "attachment; filename="
                + "serviceHistoryReport.csv");
        oHttpServletResponse.setHeader("Content-Type", "text/csv; charset=" + charset.displayName());
        oHttpServletResponse.setCharacterEncoding(charset.displayName());
        CSVWriter oCSVWriter;
        try {
            oCSVWriter = new CSVWriter(oHttpServletResponse.getWriter(), ';',
                    CSVWriter.NO_QUOTE_CHARACTER);
            oCSVWriter.writeNext(asHeader.toArray(new String[asHeader.size()]));

            List<Long> anID_Service_Exclude = null;

            if (sanID_Service_Exclude != null && sanID_Service_Exclude.length > 0) {
                List<String> asID_Service_Exclude = Arrays.asList(sanID_Service_Exclude);
                anID_Service_Exclude = asID_Service_Exclude.stream().map(s -> NumberUtils.parseNumber(s, Long.class)).collect(Collectors.toList());
            }

            //List<HistoryEvent_Service> aHistoryEvent_Service = historyEventServiceDao.getHistoryEventPeriod(oDateAt, oDateTo, anID_Service_Exclude);
            //LOG.info("Found {} history events for the period from {} to {}", aHistoryEvent_Service.size(), sDateAt, sDateTo);
            if (sID_FilterDateType.equals("Edit")) {
                aHistoryEvent_Service = historyEventServiceDao.getHistoryEventPeriod(oDateAt, oDateTo, anID_Service_Exclude);
                LOG.info("Found {} history events for the period from {} to {}", aHistoryEvent_Service.size(), sDateAt, sDateTo);
            } else if (sID_FilterDateType.equals("Create")) {
                aHistoryEvent_Service = historyEventServiceDao.getHistoryEventPeriodByCreate(oDateAt, oDateTo, anID_Service_Exclude);
                LOG.info("Found {} history events by Create events for the period from {} to {}", aHistoryEvent_Service.size(), sDateAt, sDateTo);
            } else if (sID_FilterDateType.equals("Close")) {
                aHistoryEvent_Service = historyEventServiceDao.getHistoryEventPeriodByClose(oDateAt, oDateTo, anID_Service_Exclude);
                LOG.info("Found {} history events by Close events for the period from {} to {}", aHistoryEvent_Service.size(), sDateAt, sDateTo);
            } else {
                throw new IllegalArgumentException("Check the sID_FilterDateType parameter, must be Edit, Create or Close");
            }
            LOG.info("aHistoryEvent_Service.size()" + aHistoryEvent_Service.size());
            if (aHistoryEvent_Service.size() > 0) {
                List<Long> anID_HistoryEvent_Service = new LinkedList<>();
                for (HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service) {
                    anID_HistoryEvent_Service.add(oHistoryEvent_Service.getId());
                }
                LOG.info("Looking history event services by IDs " + anID_HistoryEvent_Service);
                    
                for (HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service) {
                    List<String> asCell = new LinkedList<>();
                    // sID_Order
                    asCell.add(oHistoryEvent_Service.getsID_Order());
                    // nID_Server
                    asCell.add(oHistoryEvent_Service.getnID_Server() != null ? oHistoryEvent_Service.getnID_Server().toString() : "");
                    // nID_Service
                    asCell.add(oHistoryEvent_Service.getnID_Service() != null ? oHistoryEvent_Service.getnID_Service().toString() : "");
                    // sID_Place
                    asCell.add(oHistoryEvent_Service.getsID_UA());
                    // nID_Subject
                    asCell.add(oHistoryEvent_Service.getnID_Subject() != null ? oHistoryEvent_Service.getnID_Subject().toString() : "");
                    // nRate
                    asCell.add(oHistoryEvent_Service.getnRate() != null ? oHistoryEvent_Service.getnRate().toString() : "");
                    String sTextFeedback;
                    LOG.info("SubjectMessageFeedback get by order SubjectMessageFeedback get by order " + oHistoryEvent_Service.getsID_Order() + "!");
                    SubjectMessageFeedback oSubjectMessageFeedback
                            = subjectMessageFeedbackDao.findByOrder(oHistoryEvent_Service.getsID_Order());
                    LOG.info("found oSubjectMessageFeedback: " + oSubjectMessageFeedback);
                    
                    if (oSubjectMessageFeedback != null && oSubjectMessageFeedback.getoSubjectMessage() != null
                            && oSubjectMessageFeedback.getoSubjectMessage().getBody() != null) {
                        sTextFeedback = oSubjectMessageFeedback.getoSubjectMessage().getBody();
                        } else {
                        sTextFeedback = (oSubjectMessageFeedback != null && oSubjectMessageFeedback.getoSubjectMessage().getBody() != null) ? oSubjectMessageFeedback.getoSubjectMessage().getBody() + "." : "";
                        }
                        
                    // Кусок кода для теста. После тестирования будет убрано.
                       /*  sTextFeedback = "";                  
                        if (oSubjectMessageFeedback != null) {
                            LOG.info("!!!!!oSubjectMessageFeedback.getsBody(): " + oSubjectMessageFeedback.getsBody());
                            LOG.info("!!!!!oSubjectMessageFeedback.getsHead(): " + oSubjectMessageFeedback.getsHead());
                            LOG.info("!!!!!oSubjectMessageFeedback.getsMail(): " + oSubjectMessageFeedback.getsMail());
                            if (oSubjectMessageFeedback.getoSubjectMessage() != null) {
                                if (oSubjectMessageFeedback.getoSubjectMessage().getoMail() != null) {
                                  LOG.info("!!!!!oSubjectMessageFeedback.getoSubjectMessage().getoMail().getsValue(): " + oSubjectMessageFeedback.getoSubjectMessage().getoMail().getsValue());  
                                } else {
                                LOG.info("oSubjectMessageFeedback.getoSubjectMessage().getoMail() = null");
                                }
                            
                            sTextFeedback = oSubjectMessageFeedback.getoSubjectMessage().getBody();
                            LOG.info("!!!!!oSubjectMessageFeedback.getoSubjectMessage().getBody(): " + sTextFeedback);
                            LOG.info("!!!!!oSubjectMessageFeedback.getoSubjectMessage().getHead(): " + oSubjectMessageFeedback.getoSubjectMessage().getHead());
                            LOG.info("!!!!!oSubjectMessageFeedback.getoSubjectMessage().getMail(): " + oSubjectMessageFeedback.getoSubjectMessage().getMail());
                            
                            }
                        else {
                        LOG.info("SubjectMessage() = null");
                        } 
                        } 
                        else {
                        LOG.info("SubjectMessageFeedback() = null");
                        } */
                        
                    // sTextFeedback
                    asCell.add(sTextFeedback);
                    // sUserTaskName
                    asCell.add(oHistoryEvent_Service.getsUserTaskName());
                    // sHead
                    asCell.add(oHistoryEvent_Service.getsHead());
                    // sBody
                    asCell.add(oHistoryEvent_Service.getsBody());
                    // nTimeMinutes
                    asCell.add(oHistoryEvent_Service.getnTimeMinutes() != null ? oHistoryEvent_Service.getnTimeMinutes().toString() : "");

                    Integer nID_Server = oHistoryEvent_Service.getnID_Server();
                    nID_Server = nID_Server == null ? 0 : nID_Server;

                    nID_Server = generalConfig.getServerId(nID_Server);
                    Optional<Server> oOptionalServer = serverDao.findById(new Long(nID_Server));
                    if (!oOptionalServer.isPresent()) {
                        throw new RecordNotFoundException("Server with nID_Server " + nID_Server + " wasn't found.");
                    }
                    Server oServer = oOptionalServer.get();
                    String sHost = oServer.getsURL();

                    String sPhone = "";
                    String sURL = "";

                    if (bIncludeTaskInfo) {
                        sURL = sHost + "/service/action/task/getProcessVariableValue?nID_Process=" + oHistoryEvent_Service.getnID_Process() + "&sVariableName=phone";
                        ResponseEntity<String> osResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);

                        JSONObject oJSONObject = (JSONObject) new JSONParser().parse(osResponseEntityReturn.getBody());
                        sPhone = oJSONObject.get("phone") != null ? oJSONObject.get("phone").toString() : "";
                    }

                    asCell.add(sPhone);

                    asCell.add(oHistoryEvent_Service.getnID_ServiceData() != null ? oHistoryEvent_Service.getnID_ServiceData().toString() : "");
                    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        
                    sURL = sHost + "/service/action/task/getTaskData?sID_Order=" + oHistoryEvent_Service.getsID_Order()
                            + "&bIncludeGroups=false&bIncludeStartForm=false&bIncludeAttachments=false&bIncludeMessages=false";

                    DateTime sDateCreate = oHistoryEvent_Service.getsDateCreate();
                    DateTime sDateClose = oHistoryEvent_Service.getsDateClose();

                    SimpleDateFormat uDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        
                    if ((sDateCreate == null || sDateClose == null) && (oHistoryEvent_Service.getnID_StatusType() != HistoryEvent_Service_StatusType.ABSENT.getnID())) {
                        try {

                            ResponseEntity<String> oResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);
                            JSONObject oJSONObject = (JSONObject) new JSONParser().parse(oResponseEntityReturn.getBody());
                            JSONObject opJSONObject = (JSONObject) oJSONObject.get("oProcess");

                            sDateCreate = (DateTime) opJSONObject.get("sDateCreate");
                            sDateClose = (DateTime) opJSONObject.get("sDateClose");

                            if (sDateCreate != null) {
                                oHistoryEvent_Service.setsDateCreate(sDateCreate);

                                if (sDateClose != null) {
                                    if (oHistoryEvent_Service.getnID_StatusType() != 8L) {
                                        oHistoryEvent_Service.setnID_StatusType(8L);
                                    }
                                    oHistoryEvent_Service.setsDateClose(sDateClose);
                                }

                                historyEventServiceDao.updateHistoryEvent_Service(oHistoryEvent_Service);
                            }
                        } catch (Exception ex) {
                            oHistoryEvent_Service.setnID_StatusType(HistoryEvent_Service_StatusType.ABSENT.getnID());
                            historyEventServiceDao.updateHistoryEvent_Service(oHistoryEvent_Service);
                        }
                    }

                    asCell.add(sDateCreate != null ?  uDateFormat.format(sDateCreate.toDate()) : "");
                    asCell.add(sDateClose != null ? uDateFormat.format(sDateClose.toDate()) : "");
                   
                    oCSVWriter.writeNext(asCell.toArray(new String[asCell.size()]));
                }
            }
            oCSVWriter.close();

        } catch (Exception e) {
            LOG.error("Error occurred while creating CSV file {}", e.getMessage());
        }
    }

    @ApiOperation(value = "getActionProcessCount", notes = "getActionProcessCount")
    @RequestMapping(value = "/getActionProcessCount", method = RequestMethod.GET)
    public @ResponseBody
    String getActionProcessCount(
            @ApiParam(required = true) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(required = true) @RequestParam(value = "nID_Service", required = false) Integer nID_Service,
            @ApiParam(required = false) @RequestParam(value = "nYear ", required = false) Integer nYear,
            HttpServletResponse httpResponse) {
        
        LOG.info("getActionProcessCount sID_BP: " + sID_BP);
        LOG.info("getActionProcessCount nID_Service: " + nID_Service);
        LOG.info("getActionProcessCount nYear: " + nYear);
        
        ActionProcessCount res = actionProcessCountDao.getByCriteria(sID_BP, nID_Service, nYear == null ? Calendar.getInstance().get(Calendar.YEAR) : nYear);

        Map<String, Integer> mReturn = new HashMap<String, Integer>();

        if (res != null) {
            mReturn.put("nCountYear", res.getnCountYear().intValue());
        } else {
            mReturn.put("nCountYear", 0);
        }
        
        LOG.info("getActionProcessCount mReturn:" + JSONValue.toJSONString(mReturn));
        return JSONValue.toJSONString(mReturn);
    }

    @ApiOperation(value = "setActionProcessCount", notes = "getActionProcessCount")
    @RequestMapping(value = "/setActionProcessCount", method = RequestMethod.GET)
    public @ResponseBody
    String setActionProcessCount(
            @ApiParam(required = true) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(required = true) @RequestParam(value = "nID_Service", required = false) Integer nID_Service,
            @ApiParam(required = false) @RequestParam(value = "nYear ", required = false) Integer nYear,
            HttpServletResponse httpResponse) {
        ActionProcessCount oActionProcessCountReturn = actionProcessCountDao.getByCriteria(sID_BP, nID_Service, nYear == null ? Calendar.getInstance().get(Calendar.YEAR) : nYear);

        LOG.info("Found ActionProcessCount {}", oActionProcessCountReturn);
        if (oActionProcessCountReturn == null) {
            ActionProcessCount oActionProcessCount = new ActionProcessCount();
            oActionProcessCount.setsID_BP(sID_BP);
            oActionProcessCount.setnCountYear(0);
            oActionProcessCount.setnID_Service(nID_Service);
            oActionProcessCount.setnYear(nYear == null ? Calendar.getInstance().get(Calendar.YEAR) : nYear);
            oActionProcessCountReturn = oActionProcessCount;
        } else {
            oActionProcessCountReturn.setnCountYear(oActionProcessCountReturn.getnCountYear() + 1);
        }
        oActionProcessCountReturn = actionProcessCountDao.saveOrUpdate(oActionProcessCountReturn);
        LOG.info("Saved updated info {}: {}", oActionProcessCountReturn, oActionProcessCountReturn.getId());

        List<ActionProcessCount> list = actionProcessCountDao.findAll();
        LOG.info("Total number of elements: {}", list);
        Map<String, Integer> mReturn = new HashMap<String, Integer>();

        if (oActionProcessCountReturn != null) {
            mReturn.put("nCountYear", oActionProcessCountReturn.getnCountYear());
        } else {
            mReturn.put("nCountYear", 0);
        }
        return JSONValue.toJSONString(mReturn);
    }

    @ApiOperation(value = "cleanActionProcessCount", notes = "cleanActionProcessCount")
    @RequestMapping(value = "/cleanActionProcessCount", method = RequestMethod.GET)
    public void setActionProcessCount(
            @ApiParam(required = true) @RequestParam(value = "sID_BP", required = false) String sID_BP) {
        int res = actionProcessCountDao.deleteBy("sID_BP", sID_BP);
        LOG.info("Removed {} entities", res);
    }

    @ApiOperation(value = "/sheduleAnswer_DFS", notes = "##### Получение ответов по процессам ДФС#####\n\n")
    @RequestMapping(value = "/sheduleAnswer_DFS", method = RequestMethod.GET)
    public @ResponseBody
    String sheduleAnswer_DFS() throws Exception {
        String result = "None";
        LOG.info("sheduleAnswer_DFS... ");
        List<HistoryEvent_Service> historyEvent_Services = historyEventServiceDao.getHistoryEvent_Service(null, new Long(1549), null);
        LOG.info("historyEvent_Services.size: " + historyEvent_Services.size());
        for (HistoryEvent_Service historyEvent_Service : historyEvent_Services) {
            if (historyEvent_Service.getnID_StatusType() != 8) {
                Subject subject = subjectDao.findByIdExpected(historyEvent_Service.getnID_Subject()); //
                LOG.info("subject: " + subject);
                SubjectHuman subjectHuman = subjectHumanDao.getSubjectHuman(subject);
                LOG.info("subjectHuman.getsINN: " + subjectHuman.getsINN());
                //вызываем сервис региона по закрыти процесса и сету ответа от ДФС
                Server server = serverDao.findByIdExpected(new Long(historyEvent_Service.getnID_Server()));
                String sURL = server.getsURL() + "/service/action/task/getAnswer_DFS?sID_Process=" + historyEvent_Service.getnID_Process()
                        + "&INN=" + subjectHuman.getsINN();
                LOG.info(sURL + "...");
                ResponseEntity<String> osResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);
                result = osResponseEntityReturn.getBody();
                LOG.info(sURL + " result: " + result);
                if(result != null && result.length() > 0){
                   historyEvent_Service.setnID_StatusType(HistoryEvent_Service_StatusType.CLOSED.getnID());
                   historyEventServiceDao.saveOrUpdate(historyEvent_Service);
                   LOG.info(historyEvent_Service.getnID_Process() + " closed!!!");
                }
            }
        }
        return result;
    }
    
    @ApiOperation(value = "Получение статистики по выбранному списку сервисов и региону за заданный промежуток времени", notes
            = "##### Примеры:\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/event/getServicesStatisticsOfDnepr?sDate_from=2017-06-01 12:09:56&sDate_to=2017-06-15 12:09:56\n\n"
            + "Результат\n"
            + "\n```csv\n"
            + "nID_Service;ServiceName;SID_UA;placeName;nCountTotal;averageRate;averageTime\n"
            + "1;Надання довідки про притягнення до кримінальної відповідальності, відсутність (наявність) судимості або обмежень, передбачених кримінально-процесуальним законодавством України;1200000000;Дніпропетровська;4;0.0;7.516667\n"
            + "\n```\n")
    @RequestMapping(value = "/getServicesStatisticsOfDnepr", method = RequestMethod.GET)
    public @ResponseBody
    void getServicesStatisticOfDnepr(
            @ApiParam(value = "дата \"С\", обязательный в формате YYYY-MM-DD hh:mm:ss", required = true) @RequestParam(value = "sDate_from") String sDate_from,
            @ApiParam(value = "дата \"По\", обязательный в формате YYYY-MM-DD hh:mm:ss", required = true) @RequestParam(value = "sDate_to") String sDate_to,
            HttpServletResponse httpResponse) {

    	    	
		try {
			oActionEventService.getServicesStatisticsOfDnepr(sDate_from, sDate_to, httpResponse);

		} catch (Exception e) {
			LOG.error("Error getServicesStatisticOfDnepr {}", e.getMessage());
			LOG.error("stacktrace {}", ExceptionUtils.getStackTrace(e));
		}
    }

}
