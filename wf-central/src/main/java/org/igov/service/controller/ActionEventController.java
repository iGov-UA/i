package org.igov.service.controller;

import io.swagger.annotations.*;

import org.igov.model.subject.ServerDao;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpEntityInsedeCover;
import org.igov.model.action.event.*;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.subject.Server;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import liquibase.util.csv.CSVWriter;


@Controller
@Api(tags = {"ActionEventController"}, description = "События по действиям и статистика")
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
    
    /**
     * получает объект события по услуге, по одной из следующий комбинаций
     * параметров: - только sID_Order, строка-ид события по услуге, формат
     * XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача,
     * вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма (по
     * алгоритму Луна) также для sID_Order проверяется соответсвие формату
     * (должен содержать "-"), если черточки нету -- то перед строкой
     * добавляется "0-" - только nID_Protected -- "старая" нумерация, ид сервера
     * в таком случае равно 0 - nID_Server + nID_Protected
     *
     * @param sID_Order -- сстрока-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order (опционально, если есть другие параметры)
     * @return the object (if nID_Protected is correct and record exists)
     * otherwise return 403. CRC Error (wrong nID_Protected) or 403. "Record not
     * found"
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Получить объект события по услуге", notes = "##### ActionEventController - События по действиям и статистика. Получить объект события по услуге #####\n\n"
            + "HTTP Context: https://server:port/wf/service/action/event/getHistoryEvent_Service?nID_Protected=ххх*\n\n\n"
            + "получает объект события по услуге, по одной из следующий комбинаций параметров:\n\n"
            + "- только sID_Order, строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна (описано ниже)\n"
            + "- только nID_Protected -- \"старая\" нумерация, ид сервера в таком случае равно 0\n"
            + "- nID_Server + nID_Protected (если сервера нету, то он 0)\n"
            + "- nID_Server + nID_Process (если сервера нету, то он 0)\n\n\n"
            + "для sID_Order проверяется соответсвие формату (должен содержать \"-\"), если черточки нету -- то перед строкой добавляется \"0-\"\n\n"
            + "для nID_Protected проверяется его корректность , где последняя цифра - это последний разряд контрольной суммы (по алгоритму Луна) для всего числа без нее.\n\n"
            + "- если не совпадает -- возвращается ошибка \"CRC Error\" (код состояния HTTP 403)\n"
            + "- если совпадает -- ищется запись по nID_Process = nID_Protected без последней цифры (берется последняя по дате добавления)\n"
            + "- Если не найдена запись, то возвращает объект ошибки со значением \"Record not found\" (код состояния HTTP 403)\n"
            + "- иначе возвращает обьект\n\n\n"
            + "Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/getHistoryEvent_Service?nID_Protected=11")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "CRC Error или Record not found")})
    @RequestMapping(value = "/getHistoryEvent_Service", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service getHistoryEvent_Service(
            @ApiParam(value = "строка-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order
            //, @ApiParam(value = "номер-ид субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject
            //, @ApiParam(value = "токен", required = false) @RequestParam(value = "sToken", required = false) String sToken
            ) throws CommonServiceException {

        return oActionEventService.getHistoryEventService(sID_Order);
    }

    /**
     * add the object of HistoryEvent_Service to db with record to My journal
     *
     * @param sID_Order -- сстрока-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order (опционально, если есть другие параметры)
     * @param nID_Subject- ИД-номер (long)
     * @param sUserTaskName - строка-статус
     * @param sProcessInstanceName- название услуги (для Журнала событий)
     * @param nID_Service -- ид услуги (long, опционально)
     * @param nID_Region -- ид области (long, опционально)
     * @param sID_UA -- ид страны (строка, опционально)
     * @param soData- строка-объект с данными (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param sToken - строка-токена (опционально, для поддержки дополнения
     * заявки со стороны гражданина)
     * @param sHead - строка заглавия сообщения (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param sBody - строка тела сообщения (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param nID_Proccess_Feedback - ид запущенного процесса для обработки
     * фидбеков (issue 962)
     * @param nID_Proccess_Escalation - поле на перспективу для следующего тз по
     * эскалации
     * @param nID_StatusType          - числовой код, который соответсвует статусу (long, опционально)
     * @return created object or Exception "Cannot create event_service with the
     * same nID_Process and nID_Server!"
     */
    @ApiOperation(value = "добавляет объект события по услуге", notes = "##### ActionEventController - События по действиям и статистика. Добавитьт объект события по услуге #####\n\n"
            + "HTTP Context: https://server:port/wf/service/action/event/addHistoryEvent_Service?nID_Task=xxx&sStatus=xxx&nID_Subject=xxx*\n\n\n"
            + "при добавлении сначала проверяется, не было ли уже такой записи для данного nID_Process и nID_Server. если было -- ошибка Cannot create event_service with the same nID_Process and nID_Server!\n\n"
            + "потом генерируется поле nID_Protected по принципу: nID_Protected = nID_Process (ид задачи) + \"контрольная цифра\"\n\n"
            + "контрольная цифра -- это последний разряд суммы цифр числа по алгоритму Луна это поле используется для проверки корректности запрашиваемого ид записи (в методах get и update)\n\n"
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
            //@ApiParam(value = "название услуги (для Журнала событий)", required = true) @RequestParam(value = "sProcessInstanceName") String sProcessInstanceName,
            @ApiParam(value = "ид услуги", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
            @ApiParam(value = "ид области", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @ApiParam(value = "ид страны", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-объект с данными (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "поле на перспективу для следующего тз по эскалации", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType            
    ) {
        return oActionEventService.addActionStatus_Central(
                sID_Order,
                nID_Subject,
                sUserTaskName,
                //sProcessInstanceName,
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

    /**
     * обновляет объект события по услуге, с записью в Мой журнал
     *
     * @param sID_Order -- строка-ид события по услуге, в формате XXX-XXXXXX =
     * nID_Server-nID_Protected(опционально, если задан sID_Order или
     * nID_Process с/без nID_Server)
     * @param sUserTaskName - строка-статус
     * @param soData - строка-объект с данными (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param sToken - строка-токена (опционально, для поддержки дополнения
     * заявки со стороны гражданина)
     * @param sHead - строка заглавия сообщения (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param sBody - строка тела сообщения (опционально, для поддержки
     * дополнения заявки со стороны гражданина)
     * @param nTimeMinutes - время обработки задачи (в минутах, опционально)
     * @param nID_Proccess_Feedback - ид запущенного процесса для обработки
     * фидбеков (issue 962)
     * @param nID_Proccess_Escalation - поле на перспективу для следующего тз по
     * эскалации
     * @param nID_StatusType          - числовой код, который соответсвует статусу (long, опционально)
     * @return 200ok or "Record not found"
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Обновить объект события по услуге", notes = "##### ActionEventController - События по действиям и статистика. Обновить объект события по услуге #####\n\n"
            + "HTTP Context: https://server:port/wf/service/action/event/updateHistoryEvent_Service?nID=xxx&sStatus=xxx*\n\n\n"
            + "Пример:\n"
            + "http://test.igov.org.ua/wf/service/action/event/updateHistoryEvent_Service?nID_Process=1&sUserTaskName=finish \n"
            + "Также при апдейте охраняется информация о действии в Моем Журнале 1) запись \"Ваша заявка №[nID_Process] змiнила свiй статус на [sUserTaskName]\" 2) если есть параметр soData, то еще создается запись в виде:\n\n"
            + "- \"По заявці №[nID_Process] задане прохання уточнення: [sBody]\" (если sToken не пустой) -- согласно сервису в запроса на уточнение\n"
            + "- \"По заявці №[nID_Process] дана відповідь громадянином: [sBody]\" (если sToken пустой) -- согласно сервису ответа на запрос по уточнению\n\n"
            + "плюс перечисление полей из soData в формате таблицы Поле / Тип / Текущее значение")
    @RequestMapping(value = "/updateHistoryEvent_Service", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service updateHistoryEvent_Service(
            @ApiParam(value = "строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "строка-статус", required = false) @RequestParam(value = "sUserTaskName", required = false) String sUserTaskName,
            @ApiParam(value = "строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            //@ApiParam(value = "строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "время обработки задачи (в минутах, опционально)", required = false) @RequestParam(value = "nTimeMinutes", required = false) String nTimeMinutes,
            @ApiParam(value = "ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "поле на перспективу для следующего тз по эскалации", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = true) @RequestParam(value = "nID_StatusType", required = true) Long nID_StatusType
    ) throws CommonServiceException {
        return oActionEventService.updateActionStatus_Central(
            sID_Order,
            sUserTaskName,
            soData,
            sToken,
            //String sHead,
            sBody,
            nTimeMinutes,
            nID_Proccess_Feedback,
            nID_Proccess_Escalation,
             nID_StatusType
        );
    }

    /**
     * @param nID_Subject - номер-ИД субьекта
     * @param sID_UA - строка-ИД места Услуги
     * @param nID_Service - номер-ИД услугии
     * @return the object found or to throw error
     */
    
     //@Deprecated
     @ApiOperation(value = "Проверка наличия task определенного Бизнес процесса (БП), указанного гражданина", notes = "##### ActionEventController - События по действиям и статистика. Провить наличие task определенного Бизнес процесса (БП), указанного гражданина #####\n\n"
     + "HTTP Context: http://test.igov.org.ua/wf/service/action/task/event/getLastTaskHistory?nID_Subject=nID_Subject&nID_Service=nID_Service&sID_UA=sID_UA\n\n\n"
     + "возвращает сущность HistoryEvent_Service или ошибку Record not found.\n\n\n"
     + "Примеры:\n"
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
    /**
     * @param nID_Subject - номер-ИД субьекта
     * @param sID_UA - строка-ИД места Услуги
     * @param nID_Service - номер-ИД услугии
     * @param nLimit - Число-лимит заявок, по умолчанию без лимита
     * @param bExcludeClosed - Булевый, true исключает закрытые из подсчета
     * @return the object found or to throw error
     */
    @ApiOperation(value = "Определения числа заявок по определенной услуге в рамках места и в отношении определенного субьекта", notes = "ActionEventController - События по действиям и статистика. Нет описания")
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

        Map<String, Long> m = new HashMap();
        Long nOpened = (long) 0;
        List<HistoryEvent_Service> aHistoryEvent_Service = historyEventServiceDao.getOrdersHistory(nID_Subject, nID_Service, sID_UA, nLimit);
        for (HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service) {
            nOpened++;
            if (bExcludeClosed || oHistoryEvent_Service.getsUserTaskName().startsWith("Заявка закрита")) {
                nOpened--;
            }
        }
        m.put("nOpened", nOpened);
        //throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Record not found");
        return JSONValue.toJSONString(m);

    }

    //TODO: Сделать оограничение по строкам
    @RequestMapping(value = "/getHistoryEventsService", method = RequestMethod.GET)
    public
    @ResponseBody
    List<HistoryEvent_Service> getHistoryEventsService() {
        return historyEventServiceDao.findAll();
    }
    
    
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
     */
    @ApiOperation(value = "Сохранение события ", notes = "##### ActionEventController - События по действиям и статистика. Сохранение события #####\n\n"
            + "HTTP Context: http://server:port/wf/service/action/event/setHistoryEvent")
    @RequestMapping(value = "/setHistoryEvent", method = RequestMethod.POST)
    public @ResponseBody
    Long setHistoryEvent(
            @ApiParam(value = "ИД-строка субъекта, который загрузил документ", required = false) @RequestParam(value = "nID_Subject", required = false) long nID_Subject,
            @ApiParam(value = "ИД-номер типа документа", required = false) @RequestParam(value = "nID_HistoryEventType", required = false) Long nID_HistoryEventType,
            @ApiParam(value = "кастомное описание документа", required = false) @RequestParam(value = "sEventName", required = false) String sEventName_Custom,
            @ApiParam(value = "сохраняемое содержимое", required = true) @RequestParam(value = "sMessage") String sMessage)
            throws IOException {

        return historyEventDao.setHistoryEvent(nID_Subject,
                nID_HistoryEventType, sEventName_Custom, sMessage);

    }

    /**
     * получение документа по ид документа
     *
     * @param id ИД-номер документа
     */
    @ApiOperation(value = "Получение документа по ид документа", notes = "##### ActionEventController - События по действиям и статистика. Получение документа по ид документа #####\n\n"
            + "HTTP Context: http://server:port/wf/service/action/event/getHistoryEvent\n\n\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/action/event/getHistoryEvent?nID=1")
    @RequestMapping(value = "/getHistoryEvent", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent getHistoryEvent(
            @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id) {
        return historyEventDao.getHistoryEvent(id);
    }

    /**
     * загрузка событий
     *
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос
     * автоматически после аутентификации пользователя)????????
     */
    @ApiOperation(value = "Загрузка событий", notes = "##### ActionEventController - События по действиям и статистика. Загрузка событий #####\n\n"
            + "HTTP Context: http://server:port/wf/service/action/event/getHistoryEvents\n\n\n"
            + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)")
    @RequestMapping(value = "/getHistoryEvents", method = RequestMethod.GET)
    public @ResponseBody
    List<HistoryEvent> getHistoryEvents(
            @ApiParam(value = "nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) {
        return historyEventDao.getHistoryEvents(nID_Subject);
    }

    ////-------------Statistics--------
    /**
     * @param nID_Service ID сервиса.
     */
    @ApiOperation(value = "Получение количества записей HistoryEvent_Service для сервиса по регионам", notes = "##### ActionEventController - События по действиям и статистика. Получение количества записей HistoryEvent_Service для сервиса по регионам #####\n\n"
            + "HTTP Context: https://test.igov.org.ua/wf/service/action/event/getStatisticServiceCounts?nID_Service=nID_Service\n\n\n"
            + "Примеры:\n"
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
            @ApiParam(value = "nID_Service ID сервиса", required = true) @RequestParam(value = "nID_Service") Long nID_Service) {

        List<Map<String, Object>> listOfHistoryEventsWithMeaningfulNames = oActionEventService.getListOfHistoryEvents(nID_Service);
        return JSONValue.toJSONString(listOfHistoryEventsWithMeaningfulNames);
    }
    
    @RequestMapping(value = "/getServiceHistoryReport", method = RequestMethod.GET)
    public void getServiceHistoryReport(
            @ApiParam(value = "Дата начала выборки данных в формате yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(value = "sDateAt") String sDateAt,
            @ApiParam(value = "Дата окончания выборки данных в формате yyyy-MM-dd HH:mm:ss", required = true) @RequestParam(value = "sDateTo") String sDateTo,
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
    	
    	CSVWriter csvWriter;
		try {
			csvWriter = new CSVWriter(httpResponse.getWriter());
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
	            	} else {
	            		LOG.info("Skipping subject message with SubjectMessageType {}", subjectMessage.getSubjectMessageType().getId());
	            	}
	            }
	            
		    	for (HistoryEvent_Service historyEventService : historyEvents){
		    		List<String> line = new LinkedList<String>();
		    		// sID_Order
		    		line.add(historyEventService.getsID_Order());
		    		// nID_Server
		    		line.add(historyEventService.getnID_Server().toString());
		    		// nID_Service
		    		line.add(historyEventService.getnID_Service().toString());
		    		// sID_Place
		    		line.add(historyEventService.getsID_UA());
		    		// nID_Subject
		    		line.add(historyEventService.getnID_Subject().toString());
		    		// nRate
		    		line.add(historyEventService.getnRate().toString());
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
		    		line.add(historyEventService.getnTimeMinutes().toString());
		    		
		    		Integer nID_Server = historyEventService.getnID_Server();
		            nID_Server = nID_Server == null ? 0 : nID_Server;
	
			    	Optional<Server> oOptionalServer = serverDao.findById(new Long(nID_Server));
		            if (!oOptionalServer.isPresent()) {
		                throw new RecordNotFoundException("Server with nID_Server " + nID_Server + " wasn't found.");
		            }
		            Server oServer = oOptionalServer.get();
		            String sHost = oServer.getsURL();
		            
		            String sURL = sHost + "/service/action/task/getStartFormData?nID_Task=" + historyEventService.getnID_Task();
		            ResponseEntity<String> osResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);
		            
		            JSONObject json = (JSONObject) new JSONParser().parse(osResponseEntityReturn.getBody());
		            // sPhone
		            line.add(json.get("phone").toString());
		            
		            csvWriter.writeNext(line.toArray(new String[line.size()]));
		    	}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error occurred while creating CSV file {}", e.getMessage());
		} 
    }

}
