package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.*;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.log4j.Logger;
import org.igov.activiti.bp.HistoryEventService;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.igov.model.core.EntityNotFoundException;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.enums.HistoryEventMessage;
import org.igov.model.enums.HistoryEventType;
import org.igov.model.HistoryEventDao;
import org.igov.model.HistoryEvent_ServiceDao;
import org.igov.model.ServerDao;
import org.igov.io.liqpay.LiqBuyUtil;
import org.igov.model.HistoryEvent;
import org.igov.model.HistoryEvent_Service;
import org.igov.model.Region;
import org.igov.model.Server;
import org.igov.io.web.HttpRequester;
import org.igov.io.GeneralConfig;
import org.igov.model.core.CRCInvalidException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.igov.service.controller.ActivitiExceptionController;
import org.igov.service.controller.ActivitiRestException;
import org.igov.service.controller.RecordNotFoundException;

@Controller
@Api(tags = { "ActivitiRestHistoryEventController" }, description = "ActivitiRestHistoryEventController")
@RequestMapping(value = "/services")
public class ActivitiRestHistoryEventController {

    private static final Logger LOG = Logger.getLogger(ActivitiRestHistoryEventController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE = "\n```\n";
    private static final String noteCODEJSON = "\n```json\n";
    private static final String noteController = "##### ActivitiRestHistoryEventController. ";

    private static final String noteSetTaskAnswer_Central = noteController + "Нет описания #####\n\n";

    private static final String noteGetHistoryEvent_Service =
            noteController + "Получить объект события по услуге #####\n\n"
                    + "HTTP Context: https://server:port/wf/service/services/getHistoryEvent_Service?nID_Protected=ххх*\n\n"
                    + "получает объект события по услуге, по одной из следующий комбинаций параметров:\n\n"
                    + "- только sID_Order, строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна (описано ниже)\n"
                    + "- только nID_Protected -- \"старая\" нумерация, ид сервера в таком случае равно 0\n"
                    + "- nID_Server + nID_Protected (если сервера нету, то он 0)\n"
                    + "- nID_Server + nID_Process (если сервера нету, то он 0)\n\n\n"
                    + "параметры запроса:\n\n"
                    + "- sID_Order -- строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected (опционально, если есть другие параметры)\n"
                    + "- nID_Protected -- зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order)\n"
                    + "- nID_Process -- ид задачи (опционально, если задан один из предыдущих параметров)\n"
                    + "- nID_Server -- ид сервера, где расположена задача (опционально, по умолчанию 0)\n\n\n"
                    + "для sID_Order проверяется соответсвие формату (должен содержать \"-\"), если черточки нету -- то перед строкой добавляется \"0-\"\n\n"
                    + "для nID_Protected проверяется его корректность , где последняя цифра - это последний разряд контрольной суммы (по алгоритму Луна) для всего числа без нее.\n\n"
                    + "- если не совпадает -- возвращается ошибка \"CRC Error\" (код состояния HTTP 403)\n"
                    + "- если совпадает -- ищется запись по nID_Process = nID_Protected без последней цифры (берется последняя по дате добавления)\n"
                    + "- Если не найдена запись, то возвращает объект ошибки со значением \"Record not found\" (код состояния HTTP 403)\n"
                    + "- иначе возвращает обьект\n\n\n"
                    + "Пример:\n"
                    + "http://test.igov.org.ua/wf/service/services/getHistoryEvent_Service?nID_Protected=11";

    private static final String noteAddHistoryEvent_Service =
            noteController + "Добавляет объект события по услуге #####\n\n"
                    + "HTTP Context: https://server:port/wf/service/services/addHistoryEvent_Service?nID_Task=xxx&sStatus=xxx&nID_Subject=xxx*\n\n"
                    + "параметры:\n\n"
                    + "- nID_Process - ИД-номер задачи (long)\n"
                    + "- nID_Server - ид сервера, где расположена задача (опционально, по умолчанию 0)\n"
                    + "- nID_Subject - ИД-номер (long)\n"
                    + "- sID_Status - строка-статус\n"
                    + "- sProcessInstanceName - название услуги (для Журнала событий)\n"
                    + "- nID_Service -- ид услуги (long, опционально)\n"
                    + "- nID_Region -- ид области (long, опционально)\n"
                    + "- sID_UA -- ид страны (строка, опционально)\n"
                    + "- soData - строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sToken - строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sHead - строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sBody - строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)\n\n\n"
                    + "при добавлении сначала проверяется, не было ли уже такой записи для данного nID_Process и nID_Server. если было -- ошибка Cannot create event_service with the same nID_Process and nID_Server!\n\n"
                    + "потом генерируется поле nID_Protected по принципу: nID_Protected = nID_Process (ид задачи) + \"контрольная цифра\"\n\n"
                    + "контрольная цифра -- это последний разряд суммы цифр числа по алгоритму Луна это поле используется для проверки корректности запрашиваемого ид записи (в методах get и update)\n\n"
                    + "также генерируется поле sID_Order по принципу: sID_Order = nID_Server + \"-\" + nID_Protected\n\n"
                    + "пример: http://test.igov.org.ua/wf/service/services/addHistoryEvent_Service?nID_Process=2&sID_Status=new&nID_Subject=2&sProcessInstanceName=test_bp\n\n"
                    + "ответ:\n\n"
                    + noteCODEJSON
                    + "{\n"
                    + "    \"sID\":null,\n"
                    + "    \"nID_Task\":2,\n"
                    + "    \"nID_Subject\":2,\n"
                    + "    \"sStatus\":\"new\",\n"
                    + "    \"sID_Status\":\"new\",\n"
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
                    + noteCODE;

    private static final String noteUpdateHistoryEvent_Service =
            noteController + "Обновить объект события по услуге #####\n\n"
                    + "HTTP Context: https://server:port/wf/service/services/updateHistoryEvent_Service?nID=xxx&sStatus=xxx*\n\n"
                    + "Параметры:\n\n"
                    + "- sID_Order -- строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected(опционально, если задан sID_Order или nID_Process с/без nID_Server)\n"
                    + "- nID_Protected -- зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order или nID_Process с/без nID_Server)\n"
                    + "- nID_Process - ид задачи (опционально, если задан sID_Order или nID_Protected с/без nID_Server)\n"
                    + "- nID_Server -- ид сервера, где расположена задача (опционально, по умолчанию 0)\n"
                    + "- nID_Process - ИД-номер задачи (long)\n"
                    + "- sID_Status - строка-статус\n"
                    + "- soData - строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sToken - строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sHead - строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- sBody - строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)\n"
                    + "- nTimeMinutes - время обработки задачи (в минутах, опционально)\n\n\n"
                    + "Пример:\n"
                    + "http://test.igov.org.ua/wf/service/services/updateHistoryEvent_Service?nID_Process=1&sID_Status=finish \n"
                    + "Также при апдейте охраняется информация о действии в Моем Журнале 1) запись \"Ваша заявка №[nID_Process] змiнила свiй статус на [sID_Status]\" 2) если есть параметр soData, то еще создается запись в виде:\n\n"
                    + "- \"По заявці №[nID_Process] задане прохання уточнення: [sBody]\" (если sToken не пустой) -- согласно сервису в запроса на уточнение\n"
                    + "- \"По заявці №[nID_Process] дана відповідь громадянином: [sBody]\" (если sToken пустой) -- согласно сервису ответа на запрос по уточнению\n\n"
                    + "плюс перечисление полей из soData в формате таблицы Поле / Тип / Текущее значение";

    private static final String noteGetLastTaskHistory =
            noteController + "Проверка наличия task определенного Бизнес процесса (БП), указанного гражданина #####\n\n"
                    + "HTTP Context: http://test.igov.org.ua/wf/service/services/getLastTaskHistory?nID_Subject=nID_Subject&nID_Service=nID_Service&sID_UA=sID_UA\n\n"
                    + "возвращает сущность HistoryEvent_Service или ошибку Record not found.\n\n"
                    + "- nID_Subject - номер-ИД субьекта (переменная обязательна)\n"
                    + "- nID_Service - номер-ИД услуги (переменная обязательна)\n"
                    + "- sID_UA - строка-ИД места Услуги (переменная обязательна)\n\n\n"
                    + "Примеры:\n"
                    + "http://test.igov.org.ua/wf/service/services/getLastTaskHistory?nID_Subject=2&nID_Service=1&sID_UA=1200000000\n\n"
                    + "Ответ, если запись существует (HTTP status Code: 200 OK):\n"
                    + noteCODEJSON
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
                    + noteCODE
                    + "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n\n"
                    + noteCODEJSON
                    + "{\n"
                    + "  \"code\": \"BUSINESS_ERR\",\n"
                    + "  \"message\": \"Record not found\"\n"
                    + "}\n"
                    + noteCODE;

    private static final String noteSetHistoryEvent = noteController + "Сохранение события #####\n\n"
            + "HTTP Context: http://server:port/wf/service/services/setHistoryEvent - \n\n"
            + "- nID_Subject - ИД-строка субъекта, который загрузил документ (необязательное поле)\n"
            + "- nID_HistoryEventType - ИД-номер типа документа (необязательное поле)\n"
            + "- sEventName - строка - кастомное описание документа (необязательное поле)\n"
            + "- sMessage - строка - сохраняемое содержимое (обязательное поле)";

    private static final String noteGetHistoryEvent =
            noteController + "Работа с историей (Мой журнал). Получение документа по ид документа #####\n\n"
                    + "HTTP Context: http://server:port/wf/service/services/getHistoryEvent\n\n"
                    + "- nID - ИД-номер документа\n"
                    + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n\n"
                    + "Пример:\n"
                    + "https://test.igov.org.ua/wf/service/services/getHistoryEvent?nID=1";

    private static final String noteGetHistoryEvents =
            noteController + "Работа с историей (Мой журнал). Загрузка событий #####\n\n"
                    + "HTTP Context: http://server:port/wf/service/services/getHistoryEvents\n\n"
                    + "- nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)";

    private static final String noteGetStatisticServiceCounts =
            noteController + "Получение количества записей HistoryEvent_Service для сервиса по регионам #####\n\n"
                    + "HTTP Context: https://test.igov.org.ua/wf/service/services/getStatisticServiceCounts?nID_Service=[nID_Service]\n\n"
                    + "- nID_Service - ID сервиса.\n\n\n"
                    + "Примеры:\n"
                    + "https://test.igov.org.ua/wf/service/services/getStatisticServiceCounts?nID_Service=26\n\n"
                    + "Результат\n"
                    + noteCODEJSON
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
                    + noteCODE;

    private static final String noteGetStartFormByTask =
            noteController + "Получение полей стартовой формы по: ИД субьекта, ИД услуги, ИД места Услуги #####\n\n"
                    + "HTTP Context: https://test.igov.org.ua/wf-central/service/services/getStartFormByTask?nID_Subject=nID_Subject&nID_Service=nID_Service&sID_UA=sID_UA&nID_Server=nID_Server\n\n"
                    + "возвращает JSON содержащий поля стартовой формы процесса, процесс находится на основании ИД таски полученой из сущности HistoryEvent_Service. На основании HistoryEvent_Service определяется региональный сервер откуда нужно получить поля формы и собственно ИД таски.\n\n"
                    + "- nID_Subject - номер-ИД субьекта (переменная обязательна)\n"
                    + "- nID_Service - номер-ИД услуги (переменная обязательна)\n"
                    + "- sID_UA - строка-ИД места Услуги (переменная обязательна)\n"
                    + "- nID_Server - номер-ИД сервера опциональный, по умолчанию 0\n\n\n"
                    + "Примеры:\n"
                    + "https://test.igov.org.ua/wf-central/service/services/getStartFormByTask?nID_Subject=2&nID_Service=1&sID_UA=1200000000\n\n"
                    + "Ответ, если запись существует (HTTP status Code: 200 OK):\n"
                    + noteCODEJSON
                    + "{\n"
                    + "  waterback=\"--------------------\",\n"
                    + "  phone=\"380979362996\",\n"
                    + "  date_from=\"01/01/2014\",\n"
                    + "  bankIdbirthDay=\"27.05.1985\",\n"
                    + "  notice2=\"Я та особи, які зареєстровані (фактично проживають) у житловому приміщенні/будинку, даємо згоду на обробку персональних даних про сім’ю, доходи, майно, що необхідні для призначення житлової субсидії, та оприлюднення відомостей щодо її призначення.\",\n"
                    + "  house=\"--------------------\",\n"
                    + "garbage=\"--------------------\",\n"
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
                    + "}\n\n\n"
                    + noteCODE
                    + "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n"
                    + noteCODEJSON
                    + "{\n"
                    + "  \"code\": \"BUSINESS_ERR\",\n"
                    + "  \"message\": \"Record not found\"\n"
                    + "}"
                    + noteCODE;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    HttpRequester httpRequester;

    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    @Autowired
    private HistoryEventDao historyEventDao;

    @Autowired
    @Qualifier("regionDao")
    private GenericEntityDao<Region> regionDao;

    @Autowired
    private ServerDao serverDao;
    @Autowired
    private GeneralConfig generalConfig;

    @Autowired
    private HistoryEventService historyEventService;
    //@Autowired
    //private ServerDao serverDao;
    //@Autowired
    //HttpRequester httpRequester;

    /**
     * @param nID_Protected номер-ИД заявки (защищенный, опционально, если есть sID_Order или nID_Process)
     * @param sID_Order     строка-ид заявки (опционально, подробнее [тут](https://github.com/e-government-ua/i/blob/test/docs/specification.md#17_workWithHistoryEvent_Services) )
     * @param nID_Process   ид заявки (опционально)
     * @param nID_Server    ид сервера, где расположена заявка
     * @param saField       строка-массива полей (например: "[{'id':'sFamily','type':'string','value':'Белявцев'},{'id':'nAge','type':'long','value':35}]")
     * @param sToken        строка-токена. Данный параметр формируется и сохраняется в запись HistoryEvent_Service во время вызова метода setTaskQuestions
     * @param sHead         строка заголовка сообщения (опциональный параметр)
     * @param sBody         строка тела сообщения (опциональный параметр)
     */
    @ApiOperation(value = "/setTaskAnswer_Central", notes = noteSetTaskAnswer_Central)
    @RequestMapping(value = "/setTaskAnswer_Central", method = RequestMethod.GET)
    public
    @ResponseBody
    void setTaskAnswer(
            @ApiParam(value = "строка-ид заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "номер-ИД заявки (защищенный, опционально, если есть sID_Order или nID_Process)", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @ApiParam(value = "ид заявки", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "ид сервера", required = false) @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @ApiParam(value = "строка-массива полей (например: \"[{'id':'sFamily','type':'string','value':'Белявцев'},{'id':'nAge','type':'long','value':35}]\")", required = true) @RequestParam(value = "saField") String saField,
            @ApiParam(value = "строка-токена. Данный параметр формируется и сохраняется в запись HistoryEvent_Service во время вызова метода setTaskQuestions", required = true) @RequestParam(value = "sToken") String sToken,
            @ApiParam(value = "строка заголовка сообщения", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody)
            throws ActivitiRestException {

        try {
            LOG.info(
                    "try to find history event_service by sID_Order=" + sID_Order + ", nID_Protected-" + nID_Protected
                            + ", nID_Process=" + nID_Process + " and nID_Server=" + nID_Server
            );
            String historyEvent = historyEventService.getHistoryEvent(
                    sID_Order, nID_Protected, nID_Process, nID_Server);
            LOG.info("....ok! successfully get historyEvent_service! event="
                    + historyEvent);

            JSONObject fieldsJson = new JSONObject(historyEvent);
            String processInstanceID = fieldsJson.get("nID_Task").toString();
            sHead = sHead != null ? sHead : "На заявку "
                    + fieldsJson.getString("sID_Order")
                    + " дана відповідь громаданином";
            if (fieldsJson.has("sToken")) {
                String tasksToken = fieldsJson.getString("sToken");
                if (tasksToken.isEmpty() || !tasksToken.equals(sToken)) {
                    throw new ActivitiRestException(
                            ActivitiExceptionController.BUSINESS_ERROR_CODE,
                            "Token is wrong");
                }
            } else {
                throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Token is absent");
            }

            String sHost = null;
            Optional<Server> oOptionalServer = serverDao.findById(Long.valueOf(nID_Server + ""));
            if (!oOptionalServer.isPresent()) {
                throw new RecordNotFoundException();
            } else {//https://test.region.igov.org.ua/wf
                //sHost = oOptionalServer.get().getsURL_Alpha();
                sHost = oOptionalServer.get().getsURL();
            }

            String sURL = sHost + "/service/rest/setTaskAnswer";
            LOG.info("sURL=" + sURL);

            Map<String, String> mParam = new HashMap<String, String>();
            mParam.put("nID_Process", processInstanceID);//nID_Process
            mParam.put("saField", saField);
            mParam.put("sBody", sBody);
            LOG.info("mParam=" + mParam);
            String sReturn = httpRequester.get(sURL, mParam);
            LOG.info("sReturn=" + sReturn);

            LOG.info(
                    "try to find history event_service by sID_Order=" + sID_Order + ", nID_Protected-" + nID_Protected
                            + " and nID_Server=" + nID_Server
            );

            saField = "[]";
            historyEvent = updateHistoryEvent_Service_Central(sID_Order, nID_Protected,
                    nID_Process, nID_Server, saField, sHead, null, null,
                    "Відповідь на запит по уточненню даних");
            LOG.info("....ok! successfully get historyEvent_service! event="
                    + historyEvent);
        } catch (Exception e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e, HttpStatus.FORBIDDEN);
        }
    }

    public String updateHistoryEvent_Service_Central(String sID_Order,
            Long nID_Protected, Long nID_Process, Integer nID_Server,
            String saField, String sHead, String sBody, String sToken,
            String sID_Status) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("sID_Order", sID_Order);
        params.put("nID_Protected", nID_Protected != null ? "" + nID_Protected
                : null);
        String sID_Process = nID_Process != null ? "" + nID_Process : null;
        params.put("nID_Process", sID_Process);
        params.put("nID_Server", nID_Server != null ? "" + nID_Server : null);
        params.put("soData", saField);
        params.put("sHead", sHead);
        params.put("sBody", sBody);
        params.put("sToken", sToken);
        params.put("sID_Status", sID_Status);
        return historyEventService.updateHistoryEvent(sID_Process, sID_Status,
                true, params);
    }

    /**
     * получает объект события по услуге, по одной из следующий комбинаций параметров:
     * - только sID_Order, строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача,
     * вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма (по алгоритму Луна)
     * также для sID_Order проверяется соответсвие формату (должен содержать "-"), если черточки нету -- то перед строкой добавляется "0-"
     * - только nID_Protected -- "старая" нумерация, ид сервера в таком случае равно 0
     * - nID_Server + nID_Protected
     *
     * @param sID_Order     -- строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected (опционально, если есть другие параметры)
     * @param nID_Protected -- зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order)
     * @param nID_Process   -- ид задачи (опционально, если задан один из предыдущих параметров)
     * @param nID_Server    -- ид сервера, где расположена задача (опционально, по умолчанию 0)
     * @return the object (if nID_Protected is correct and record exists) otherwise return
     * 403. CRC Error (wrong nID_Protected) or 403. "Record not found"
     * @throws ActivitiRestException
     */
    @ApiOperation(value = "Получить объект события по услуге", notes = noteGetHistoryEvent_Service)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "CRC Error или Record not found") })
    @RequestMapping(value = "/getHistoryEvent_Service", method = RequestMethod.GET)
    public
    @ResponseBody
    HistoryEvent_Service getHistoryEvent_Service(
            @ApiParam(value = "строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected (опционально, если есть другие параметры)", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order)", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @ApiParam(value = "ид задачи (опционально, если задан один из предыдущих параметров)", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "ид сервера, где расположена задача (опционально, по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false) Integer nID_Server)
            throws ActivitiRestException {

        return getHistoryEventService(sID_Order, nID_Protected, nID_Process, nID_Server);
    }

    /**
     * add the object of HistoryEvent_Service to db
     * with record to My journal
     *
     * @param nID_Process-            ИД-номер задачи (long)
     * @param nID_Server              - ид сервера, где расположена задача (опционально, по умолчанию 0)
     * @param nID_Subject-            ИД-номер (long)
     * @param sID_Status              - строка-статус
     * @param sProcessInstanceName-   название услуги (для Журнала событий)
     * @param nID_Service             -- ид услуги (long, опционально)
     * @param nID_Region              -- ид области (long, опционально)
     * @param sID_UA                  -- ид страны (строка, опционально)
     * @param soData-                 строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sToken                  - строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sHead                   - строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sBody                   - строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param nID_Proccess_Feedback   - ид запущенного процесса для обработки фидбеков (issue 962)
     * @param nID_Proccess_Escalation - поле на перспективу для следующего тз по эскалации
     * @return created object or Exception "Cannot create event_service with the same nID_Process and nID_Server!"
     */
    @ApiOperation(value = "добавляет объект события по услуге", notes = noteAddHistoryEvent_Service)
    @RequestMapping(value = "/addHistoryEvent_Service", method = RequestMethod.GET)
    public
    @ResponseBody
    HistoryEvent_Service addHistoryEvent_Service(
            @ApiParam(value = "ИД-номер задачи", required = true) @RequestParam(value = "nID_Process") Long nID_Process,
            @ApiParam(value = "ид сервера, где расположена задача (по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @ApiParam(value = "ИД-номер", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "строка-статус", required = true) @RequestParam(value = "sID_Status") String sID_Status,
            @ApiParam(value = "название услуги (для Журнала событий)", required = true) @RequestParam(value = "sProcessInstanceName") String sProcessInstanceName,
            @ApiParam(value = "ид услуги", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
            @ApiParam(value = "ид области", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @ApiParam(value = "ид страны", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-объект с данными (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "поле на перспективу для следующего тз по эскалации", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation
    ) {

        HistoryEvent_Service event_service = new HistoryEvent_Service();
        event_service.setnID_Task(nID_Process);
        event_service.setsStatus(sID_Status);
        event_service.setsID_Status(sID_Status);
        event_service.setnID_Subject(nID_Subject);
        event_service.setnID_Region(nID_Region);
        event_service.setnID_Service(nID_Service);
        event_service.setsID_UA(sID_UA);
        event_service.setnRate(null);
        event_service.setSoData(soData);
        event_service.setsToken(sToken);
        event_service.setsHead(sHead);
        event_service.setsBody(sBody);
        event_service.setnID_Server(nID_Server);
        event_service.setnID_Proccess_Feedback(nID_Proccess_Feedback);
        event_service.setnID_Proccess_Escalation(nID_Proccess_Escalation);
        event_service = historyEventServiceDao.addHistoryEvent_Service(event_service);
        //get_service history event
        Map<String, String> mParamMessage = new HashMap<>();
        mParamMessage.put(HistoryEventMessage.SERVICE_NAME, sProcessInstanceName);
        mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sID_Status);
        setHistoryEvent(HistoryEventType.GET_SERVICE, nID_Subject, mParamMessage);
        //My journal. setTaskQuestions (issue 808)
        createHistoryEventForTaskQuestions(HistoryEventType.SET_TASK_QUESTIONS, soData, soData,
                event_service.getnID_Protected(), nID_Subject);
        return event_service;
    }

    /**
     * обновляет объект события по услуге, с записью в Мой журнал
     *
     * @param sID_Order               -- строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected(опционально, если задан sID_Order или nID_Process с/без nID_Server)
     * @param nID_Protected           -- зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order или nID_Process с/без nID_Server)
     * @param nID_Process             - ид задачи (опционально, если задан sID_Order или nID_Protected с/без nID_Server)
     * @param nID_Server              -- ид сервера, где расположена задача (опционально, по умолчанию 0)
     * @param sID_Status              - строка-статус
     * @param soData                  - строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sToken                  - строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sHead                   - строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param sBody                   - строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)
     * @param nTimeMinutes            - время обработки задачи (в минутах, опционально)
     * @param nID_Proccess_Feedback   - ид запущенного процесса для обработки фидбеков (issue 962)
     * @param nID_Proccess_Escalation - поле на перспективу для следующего тз по эскалации
     * @return 200ok or "Record not found"
     * @throws ActivitiRestException
     */
    @ApiOperation(value = "Обновить объект события по услуге", notes = noteUpdateHistoryEvent_Service)
    @RequestMapping(value = "/updateHistoryEvent_Service", method = RequestMethod.GET)
    public
    @ResponseBody
    HistoryEvent_Service updateHistoryEvent_Service(
            @ApiParam(value = "строка-ид события по услуге, в формате XXX-XXXXXX = nID_Server-nID_Protected(опционально, если задан sID_Order или nID_Process с/без nID_Server)", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "зашифрованое ид задачи, nID задачи + контрольная цифра по алгоритму Луна (опционально, если задан sID_Order или nID_Process с/без nID_Server)", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @ApiParam(value = "ид задачи (опционально, если задан sID_Order или nID_Protected с/без nID_Server)", required = false) @RequestParam(value = "nID_Process", required = false) Long nID_Process,
            @ApiParam(value = "ид сервера, где расположена задача (опционально, по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false) Integer nID_Server,
            @ApiParam(value = "строка-статус", required = true) @RequestParam(value = "sID_Status") String sID_Status,
            @ApiParam(value = "строка-объект с данными (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (опционально, для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "время обработки задачи (в минутах, опционально)", required = false) @RequestParam(value = "nTimeMinutes", required = false) String nTimeMinutes,
            @ApiParam(value = "ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "поле на перспективу для следующего тз по эскалации", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation
    ) throws ActivitiRestException {

        HistoryEvent_Service historyEventService = getHistoryEventService(sID_Order, nID_Protected, nID_Process,
                nID_Server);

        boolean isChanged = false;
        if (sID_Status != null && !sID_Status.equals(historyEventService.getsID_Status())) {
            historyEventService.setsID_Status(sID_Status);
            isChanged = true;
        }
        if (soData != null && !soData.equals(historyEventService.getSoData())) {
            historyEventService.setSoData(soData);
            isChanged = true;
            if (sHead == null) {
                sHead = "Необхідно уточнити дані";
            }
        }
        if (sHead != null && !sHead.equals(historyEventService.getsHead())) {
            historyEventService.setsHead(sHead);
            isChanged = true;
        }
        if (sBody != null && !sBody.equals(historyEventService.getsBody())) {
            historyEventService.setsBody(sBody);
            isChanged = true;
        }
        if (sToken == null || !sToken.equals(historyEventService.getsToken())) {
            historyEventService.setsToken(sToken);
            isChanged = true;
        }
        if (nTimeMinutes != null && !nTimeMinutes.isEmpty()) {
            Integer nMinutes;
            try {
                nMinutes = Integer.valueOf(nTimeMinutes);
            } catch (NumberFormatException ignored) {
                nMinutes = 0;
            }
            historyEventService.setnTimeMinutes(nMinutes);
            isChanged = true;
        }
        if (nID_Proccess_Feedback != null && !nID_Proccess_Feedback
                .equals(historyEventService.getnID_Proccess_Feedback())) {
            historyEventService.setnID_Proccess_Feedback(nID_Proccess_Feedback);
            isChanged = true;
        }
        if (nID_Proccess_Escalation != null && !nID_Proccess_Escalation
                .equals(historyEventService.getnID_Proccess_Escalation())) {
            historyEventService.setnID_Proccess_Escalation(nID_Proccess_Escalation);
            isChanged = true;
        }
        //for new numeration of historyEvent_services (889)
        nID_Protected = historyEventService.getnID_Protected();
        nID_Server = nID_Server != null ? nID_Server : 0;
        String sID_Server = (sID_Order != null && sID_Order.contains("-")) ? ""
                : ("" + nID_Server + "-");
        sID_Order = sID_Server + (sID_Order != null ? sID_Order : nID_Protected);
        historyEventService.setsID_Order(sID_Order);
        //        event_service.setnID_Server(nID_Server);
        //        if (isChanged) { temp -- for sID_Order. todo remove after deleting dublicates (889)
        historyEventServiceDao.updateHistoryEvent_Service(historyEventService);
        //        }

        Long nID_Subject = historyEventService.getnID_Subject();
        //My journal. change status of task
        Map<String, String> mParamMessage = new HashMap<>();
        mParamMessage.put(HistoryEventMessage.SERVICE_STATE, sID_Status);
        mParamMessage.put(HistoryEventMessage.TASK_NUMBER, sID_Order);
        setHistoryEvent(HistoryEventType.ACTIVITY_STATUS_NEW, nID_Subject, mParamMessage);
        //My journal. setTaskQuestions (issue 808, 809)
        if (soData != null) {
            createHistoryEventForTaskQuestions(
                    sToken != null ? HistoryEventType.SET_TASK_QUESTIONS : HistoryEventType.SET_TASK_ANSWERS,
                    soData, sBody, nID_Protected, nID_Subject);
        }
        return historyEventService;
    }

    private HistoryEvent_Service getHistoryEventService(
            String sID_Order, Long nID_Protected, Long nID_Process, Integer nID_Server) throws ActivitiRestException {

        HistoryEvent_Service historyEventService;
        try {
            if (sID_Order != null) {
                String sID_Server = sID_Order.contains("-") ?
                        "" :
                        (nID_Server != null ? ("" + nID_Server + "-") : "0-");
                sID_Order = sID_Server + sID_Order;
                historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
            } else if (nID_Protected != null) {
                historyEventService = historyEventServiceDao.getOrgerByProtectedID(nID_Protected, nID_Server);
            } else if (nID_Process != null) {
                historyEventService = historyEventServiceDao.getOrgerByProcessID(nID_Process, nID_Server);
            } else {
                throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "incorrect input data!! must be: [sID_Order] OR [nID_Protected + nID_Server (optional)] OR [nID_Process + nID_Server(optional)]",
                        HttpStatus.FORBIDDEN);
            }
        } catch (CRCInvalidException | EntityNotFoundException e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e,
                    HttpStatus.FORBIDDEN);
        }
        return historyEventService;
    }

    private void createHistoryEventForTaskQuestions(HistoryEventType eventType, String soData, String data,
            Long nID_Protected, Long nID_Subject) {
        Map<String, String> mParamMessage = new HashMap<>();
        if (soData != null && !"[]".equals(soData)) {
            LOG.info(">>>>create history event for SET_TASK_QUESTIONS.TASK_NUMBER=" + nID_Protected);
            mParamMessage.put(HistoryEventMessage.TASK_NUMBER, "" + nID_Protected);
            LOG.info(">>>>create history event for SET_TASK_QUESTIONS.data=" + data);
            mParamMessage.put(HistoryEventMessage.S_BODY, data == null ? "" : data);
            LOG.info(">>>>create history event for SET_TASK_QUESTIONS.TABLE_BODY=" + HistoryEventMessage
                    .createTable(soData));
            mParamMessage.put(HistoryEventMessage.TABLE_BODY, HistoryEventMessage.createTable(soData));
            LOG.info(">>>>create history event for SET_TASK_QUESTIONS.nID_Subject=" + nID_Subject);
            setHistoryEvent(eventType, nID_Subject, mParamMessage);
            LOG.info(">>>>create history event for SET_TASK_QUESTIONS... ok!");
        }
    }

    /**
     * @param nID_Subject - номер-ИД субьекта
     * @param sID_UA      - строка-ИД места Услуги
     * @param nID_Service - номер-ИД услугии
     * @return the object found or to throw error
     */
    /*
    @Deprecated
    @ApiOperation(value = "Проверка наличия task определенного Бизнес процесса (БП), указанного гражданина", notes = noteGetLastTaskHistory)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/getLastTaskHistory", method = RequestMethod.GET)
    public
    @ResponseBody
    HistoryEvent_Service getLastTaskHistory(
            @ApiParam(value = "Номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
            @ApiParam(value = "Номер-ИД услуги", required = true) @RequestParam(value = "nID_Service", required = true) Long nID_Service,
            @ApiParam(value = "Строка-ИД места (по Украинскому классификатору)", required = true) @RequestParam(value = "sID_UA", required = true) String sID_UA)
            throws ActivitiRestException {

        HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getLastTaskHistory(nID_Subject, nID_Service, sID_UA);
        if (oHistoryEvent_Service == null) {
            throw new ActivitiRestException(ActivitiExceptionController.BUSINESS_ERROR_CODE, "Record not found");
        }
        return oHistoryEvent_Service;
    }*/
    
    /**
     * @param nID_Subject - номер-ИД субьекта
     * @param sID_UA      - строка-ИД места Услуги
     * @param nID_Service - номер-ИД услугии
     * @param nLimit      - Число-лимит заявок, по умолчанию без лимита
     * @param bExcludeClosed - Булевый, true исключает закрытые из подсчета
     * @return the object found or to throw error
     */
    @ApiOperation(value = "Определения числа заявок по определенной услуге в рамках места и в отношении определенного субьекта", notes = "")
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/getCountOrders", method = RequestMethod.GET)
    public
    @ResponseBody
    String getCountOrders(
            @ApiParam(value = "Номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject", required = true) Long nID_Subject,
            @ApiParam(value = "Номер-ИД услуги", required = true) @RequestParam(value = "nID_Service", required = true) Long nID_Service,
            @ApiParam(value = "Строка-ИД места (по Украинскому классификатору)", required = true) @RequestParam(value = "sID_UA", required = true) String sID_UA,
            @ApiParam(value = "Число-лимит заявок, по умолчанию без лимита", required = false) @RequestParam(value = "nLimit", required = false, defaultValue = "0") int nLimit,
            @ApiParam(value = "Булевый, true исключает закрытые из подсчета", required = false) @RequestParam(value = "bExcludeClosed", required = false, defaultValue = "false") Boolean bExcludeClosed)
            throws ActivitiRestException {

        Map<String, Long> m = new HashMap();
        Long nOpened = (long) 0;
        List<HistoryEvent_Service> aHistoryEvent_Service = historyEventServiceDao.getOrdersHistory(nID_Subject, nID_Service, sID_UA, nLimit);
        for(HistoryEvent_Service oHistoryEvent_Service : aHistoryEvent_Service){
            nOpened++;
            if(bExcludeClosed && (
                     "Заявка виконана".equals(oHistoryEvent_Service.getsID_Status())
                     || "closed".equals(oHistoryEvent_Service.getsID_Status())
                    )){
                nOpened--;
            }
        }
        m.put("nOpened", nOpened);
        //throw new ActivitiRestException(ActivitiExceptionController.BUSINESS_ERROR_CODE, "Record not found");
        return JSONValue.toJSONString(m);
        
    }
    

    //################ HistoryEvent services ###################

    /**
     * сохранение события
     *
     * @param nID_Subject          ИД-строка субъекта, который загрузил документ (необязательное поле)???????????????????????????????????
     * @param nID_HistoryEventType ИД-номер типа документа (необязательное поле)
     * @param sEventName_Custom    строка - кастомное описание документа (необязательное поле)
     * @param sMessage             строка - сохраняемое содержимое (обязательное поле)
     */
    @ApiOperation(value = "Сохранение события ", notes = noteSetHistoryEvent)
    @RequestMapping(value = "/setHistoryEvent", method = RequestMethod.POST)
    public
    @ResponseBody
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
    @ApiOperation(value = "Работа с историей (Мой журнал)", notes = noteGetHistoryEvent)
    @RequestMapping(value = "/getHistoryEvent", method = RequestMethod.GET)
    public
    @ResponseBody
    HistoryEvent getHistoryEvent(
            @ApiParam(value = "ИД-номер документа", required = true) @RequestParam(value = "nID") Long id) {
        return historyEventDao.getHistoryEvent(id);
    }

    /**
     * загрузка событий
     *
     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)????????
     */
    @ApiOperation(value = "Работа с историей (Мой журнал). Загрузка событий", notes = noteGetHistoryEvents)
    @RequestMapping(value = "/getHistoryEvents", method = RequestMethod.GET)
    public
    @ResponseBody
    List<HistoryEvent> getHistoryEvents(
            @ApiParam(value = "nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)", required = true) @RequestParam(value = "nID_Subject") long nID_Subject) {
        return historyEventDao.getHistoryEvents(nID_Subject);
    }

    ////-------------Statistics--------

    /**
     * @param nID_Service ID сервиса.
     */
    @ApiOperation(value = "Получение количества записей HistoryEvent_Service для сервиса по регионам", notes = noteGetStatisticServiceCounts)
    @RequestMapping(value = "/getStatisticServiceCounts", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getStatisticServiceCounts(
            @ApiParam(value = "nID_Service ID сервиса", required = true) @RequestParam(value = "nID_Service") Long nID_Service) {

        List<Map<String, Object>> listOfHistoryEventsWithMeaningfulNames = getListOfHistoryEvents(nID_Service);
        return JSONValue.toJSONString(listOfHistoryEventsWithMeaningfulNames);
    }

    private List<Map<String, Object>> getListOfHistoryEvents(Long nID_Service) {

        List<Map<String, Object>> aRowReturn = new LinkedList<>();
        List<Map<String, Long>> aRow = historyEventServiceDao
                .getHistoryEvent_ServiceBynID_Service(nID_Service);

        Map<String, Object> mCellReturn;
        for (Map<String, Long> mCell : aRow) {
            mCellReturn = new HashMap<>();

            Long nCount = mCell.get("nCount") == null ? 0L : mCell.get("nCount");

            String sName = "Вся країна";
            Long nID_Region = mCell.get("sName");
            if (nID_Region > 0) {
                Region oRegion = regionDao.findByIdExpected(nID_Region);
                sName = oRegion.getName();
                nCount = addSomeServicesCount(nCount, nID_Service, oRegion);
            }
            LOG.info("[getListOfHistoryEvents]sName=" + sName);
            mCellReturn.put("sName", sName);

            Long nTimeMinutes = mCell.get("nTimeMinutes");
            Long nRate = mCell.get("nRate") == null ? 0L : mCell.get("nRate");

            if (nID_Service == 159) {//issue 750 + 777
                LOG.info("[getListOfHistoryEvents]!!!nID_Service=" + nID_Service);
                List<Map<String, Object>> am;
                Long[] arr;
                Long nSumRate = nRate * nCount;
                for (Long nID = 726L; nID < 734L; nID++) {
                    am = getListOfHistoryEvents(nID);
                    arr = getCountFromStatisticArrayMap(am);
                    nCount += arr[0];
                    nSumRate += arr[1];
                }
                LOG.info("[getListOfHistoryEvents]nCount(summ)=" + nCount);
                nRate = nSumRate / nCount;
                LOG.info("[getListOfHistoryEvents]nRAte(summ)=" + nRate);
            }
            LOG.info("[getListOfHistoryEvents]nCount=" + nCount);
            mCellReturn.put("nCount", nCount);
            mCellReturn.put("nRate", nRate);
            mCellReturn.put("nTimeMinutes", nTimeMinutes != null ? nTimeMinutes : "0");
            aRowReturn.add(mCellReturn);
        }
        return aRowReturn;
    }

    /**
     * @param nID_Subject номер-ИД субьекта (переменная обязательна)
     * @param nID_Service номер-ИД услуги  (переменная обязательна)
     * @param sID_UA      строка-ИД места Услуги  (переменная обязательна)
     * @param nID_Server  номер-ИД сервера опциональный, по умолчанию 0
     */
    @ApiOperation(value = "Получение полей стартовой формы по: ИД субьекта, ИД услуги, ИД места Услуги", notes = noteGetStartFormByTask)
    @ApiResponses(value = { @ApiResponse(code = 500, message = "Record not found") })
    @RequestMapping(value = "/getStartFormByTask", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getStartFormByTask(
            @ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "номер-ИД сервера", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @ApiParam(value = "номер-ИД услуги", required = true) @RequestParam(value = "nID_Service") Long nID_Service,
            @ApiParam(value = "строка-ИД места Услуги", required = true) @RequestParam(value = "sID_UA") String sID_UA)
            throws RecordNotFoundException {
        String URI = "/service/rest/tasks/getStartFormData?nID_Task=";

        HistoryEvent_Service historyEventService = historyEventServiceDao
                .getLastTaskHistory(nID_Subject, nID_Service,
                        sID_UA);
        if (historyEventService == null) {
            throw new RecordNotFoundException("HistoryEvent_Service wasn't found.");
        }

        Long nID_Task = historyEventService.getnID_Task();
        nID_Server = historyEventService.getnID_Server();
        nID_Server = nID_Server == null ? 0 : nID_Server;

        Optional<Server> serverOpt = serverDao.findById(new Long(nID_Server));
        if (!serverOpt.isPresent()) {
            throw new RecordNotFoundException("Server with nID_Server " + nID_Server + " wasn't found.");
        }
        Server server = serverOpt.get();
        String serverUrl = server.getsURL();
        if (server.getId().equals(0L)) {
            serverUrl = "https://test.region.igov.org.ua/wf";
        }

        serverUrl = serverUrl + URI + nID_Task;

        String sUser = generalConfig.sAuthLogin();
        String sPassword = generalConfig.sAuthPassword();
        String sAuth = LiqBuyUtil.base64_encode(sUser + ":" + sPassword);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + sAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        LOG.info("Calling URL with parametes " + serverUrl);
        ResponseEntity<String> result;

        try {
            result = template.exchange(serverUrl, HttpMethod.GET, httpEntity, String.class);
        } catch (RestClientException e) {
            LOG.warn(e);
            throw new RecordNotFoundException();
        }

        return result.getBody();
    }

    private Long addSomeServicesCount(Long nCount, Long nID_Service, Region region) {
        //currMapWithName.put("nCount", currMap.get("nCount"));
              /*https://igov.org.ua/service/661/general - 43
                https://igov.org.ua/service/655/generall - 75
				https://igov.org.ua/service/176/general - 546
				https://igov.org.ua/service/654/general - 307   */

        boolean magicID = "1200000000".equals(region.getsID_UA());
        if (nID_Service == 661) {
            if (magicID) {
                nCount += 43;
            }
        } else if (nID_Service == 665) {
            if (magicID) {
                nCount += 75;
            }
        } else if (nID_Service == 176) {
            if (magicID) {
                nCount += 546;
            }
        } else if (nID_Service == 654) {
            if (magicID) {
                nCount += 307;
            }
        } else if (nID_Service == 159) {
                /*https://igov.org.ua/service/159/general
				Днепропетровская область - 53
                Киевская область - 69
                1;Дніпропетровська;"1200000000"
                5;Київ;"8000000000"
                16;Київська;"3200000000"*/
            if (magicID) {
                nCount += 53;
            } else if ("8000000000".equals(region.getsID_UA()) || "3200000000".equals(region.getsID_UA())) {
                nCount += 69;
            }
        } else if (nID_Service == 1) {
			 /*https://igov.org.ua/service/1/general
			Днепропетровская область - 812*/
			  /*if("".equals(region.getsID_UA())){
				nCount+=53;
              }else if("".equals(region.getsID_UA())){
                nCount+=69;
              }*/
            if (magicID) {
                nCount += 812;
            }
        } else if (nID_Service == 772) {
            if (magicID) {
                nCount += 96;
            }
        } else if (nID_Service == 4) {
			  /*
			https://igov.org.ua/service/4/general -
            Днепропетровская область - услуга временно приостановлена
            по иным регионам заявок вне было.
              */
            nCount += 0;
        } else if (nID_Service == 0) {
            nCount += 0;
            //region.getsID_UA()
        }
        return nCount;
    }

    private Long[] getCountFromStatisticArrayMap(List<Map<String, Object>> am) {
        Long n = 0L;
        Long nRate = 0L;
        LOG.info("[getCountFromStatisticArrayMap] am=" + am);
        if (am.size() > 0) {
            if (am.get(0).containsKey("nCount")) {
                String s = am.get(0).get("nCount") + "";
                if (!"null".equals(s)) {
                    n = new Long(s);
                    LOG.info("[getCountFromStatisticArrayMap] n=" + n);
                }
            }
            if (am.get(0).containsKey("nRate")) {
                String s = am.get(0).get("nRate") + "";
                if (!"null".equals(s)) {
                    nRate = new Long(s);
                    LOG.info("[getCountFromStatisticArrayMap] nRate=" + n);
                }
            }
        }
        return new Long[] { n, nRate * n };
    }

    private void setHistoryEvent(HistoryEventType eventType,
            Long nID_Subject, Map<String, String> mParamMessage) {
        try {
            String eventMessage = HistoryEventMessage.createJournalMessage(
                    eventType, mParamMessage);
            historyEventDao.setHistoryEvent(nID_Subject, eventType.getnID(),
                    eventMessage, eventMessage);
        } catch (IOException e) {
            LOG.error("error during creating HistoryEvent", e);
        }
    }

}
