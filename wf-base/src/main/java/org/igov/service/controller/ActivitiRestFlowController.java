package org.igov.service.controller;

import org.igov.model.flow.FlowSlotTicket;
import org.igov.model.flow.FlowProperty;
import org.igov.model.subject.SubjectOrganDepartment;
import org.igov.model.flow.FlowPropertyClass;
import org.igov.model.flow.Flow_ServiceData;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.time.DateUtils;
import org.igov.util.convert.QuartzUtil;
import org.joda.time.DateTime;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.flow.FlowServiceDataDao;
import org.igov.model.flow.FlowSlotTicketDao;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.flow.FlowService;
import org.igov.util.convert.JsonDateSerializer;
import org.igov.util.convert.JsonDateTimeSerializer;
import org.igov.util.convert.JsonRestUtils;
import org.igov.model.flow.slot.ClearSlotsResult;
import org.igov.model.flow.slot.Days;
import org.igov.model.flow.slot.FlowSlotVO;
import org.igov.model.flow.slot.SaveFlowSlotTicketResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: goodg_000
 * Date: 21.06.2015
 * Time: 14:02
 */
@Controller
@Api(tags = { "ActivitiRestFlowController" }, description = "Электронные очереди (слоты потока, расписания и тикеты)")
@RequestMapping(value = "/flow")
public class ActivitiRestFlowController {

    private static final long DEFAULT_FLOW_PROPERTY_CLASS = 1l;

    private static final Logger log = LoggerFactory.getLogger(ActivitiRestFlowController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController ="##### Аутентификация пользователя. ";

    private static final String noteGetFlowSlots = noteController + "Получение слотов по сервису сгруппированных по дням #####\n\n"
        + "HTTP Context: http://server:port/wf/service/flow/getFlowSlots_ServiceData\n\n"
        + "Параметры:\n\n"
        + "- nID_Service -номер-ИД услуги (обязательный если нет sID_BP и nID_ServiceData)\n"
        + "- nID_ServiceData - ID сущности ServiceData (обязательный если нет sID_BP и nID_Service)\n"
        + "- sID_BP - строка-ИД бизнес-процесса (обязательный если нет nID_ServiceData и nID_Service)\n"
        + "- nID_SubjectOrganDepartment - ID департамента субьекта-органа (опциональный, по умолчанию false)\n"
        + "- bAll - если false то из возвращаемого объекта исключаются элементы, содержащие \"bHasFree\":false \"bFree\":false (опциональный, по умолчанию false)\n"
        + "- nDays - колличество дней от сегодняшего включительно(или sDateStart, если задан), до nDays в будующее за который нужно вернуть слоты (опциональный, по умолчанию 177 - пол года)\n"
        + "- nFreeDays - дни со слотами будут включаться в результат пока не наберется указанное кол-во свободных дней (опциональный, по умолчанию 60)\n"
        + "- sDateStart - опциональный параметр, определяющие дату начала в формате \"yyyy-MM-dd\", с которую выбрать слоты. При наличии этого параметра слоты возвращаются только за указанный период(число дней задается nDays).\n"
        + "Пример:\n"
        + "https://test.igov.org.ua/wf/service/flow/getFlowSlots_ServiceData?nID_ServiceData=1\n"
        + "или\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getSheduleFlowIncludes?sID_BP=kiev_mreo_1\n\n"
        + "Ответ: HTTP STATUS 200\n\n"
        + noteCODEJSON
        + "{\n"
        + "    \"aDay\": [\n"
        + "        {\n"
        + "            \"sDate\": \"2015-07-19\",\n"
        + "            \"bHasFree\": true,\n"
        + "            \"aSlot\": [\n"
        + "                {\n"
        + "                    \"nID\": 1,\n"
        + "                    \"sTime\": \"18:00\",\n"
        + "                    \"nMinutes\": 15,\n"
        + "                    \"bFree\": true\n"
        + "                }\n"
        + "            ]\n"
        + "        },\n"
        + "        {\n"
        + "            \"sDate\": \"2015-07-20\",\n"
        + "            \"bHasFree\": true,\n"
        + "            \"aSlot\": [\n"
        + "                {\n"
        + "                    \"nID\": 3,\n"
        + "                    \"sTime\": \"18:15\",\n"
        + "                    \"nMinutes\": 15,\n"
        + "                    \"bFree\": true\n"
        + "                }\n"
        + "            ]\n"
        + "        }\n"
        + "    ]\n"
        + "}\n\n"
        + noteCODE
        + "Калькулируемые поля в ответе:\n\n"
        + "- флаг \"bFree\" - является ли слот свободным? Слот считается свободным если на него нету тикетов у которых nID_Task_Activiti равен null, а у тех у которых nID_Task_Activiti = null - время создания тикета (sDateEdit) не позднее чем текущее время минус 5 минут (предопределенная константа)\n\n"
        + "- флаг \"bHasFree\" равен true , если данных день содержит хотя бы один свободный слот.\n";

    private static final String noteGetFlowSlotDepartment = noteController + "Получение массива объектов SubjectOrganDepartment по ID бизнес процесса #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/getFlowSlots_Department?sID_BP=[sID_BP] -- возвращает массив объектов SubjectOrganDepartment для указанного Activiti BP.\n\n"
        + "- sID_BP - имя Activiti BP\n\n"
        + "Примеры:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getFlowSlots_Department?sID_BP=dnepr_dms-89\n\n"
        + "Ответ:\n\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sName\": \"ДМС, Днепр, пр. Ильича, 3 (dnepr_dms-89,dnepr_dms-89s)\",\n"
        + "    \"nID_SubjectOrgan\": 2,\n"
        + "    \"sGroup_Activiti\": \"dnepr_dms_89_bab\",\n"
        + "    \"nID\": 13\n"
        + "  },\n"
        + "  {\n"
        + "    \"sName\": \"ДМС, Днепр, вул. Шевченко, 7 (dnepr_dms-89,dnepr_dms-89s)\",\n"
        + "    \"nID_SubjectOrgan\": 2,\n"
        + "    \"sGroup_Activiti\": \"dnepr_dms_89_zhovt\",\n"
        + "    \"nID\": 14\n"
        + "  }\n"
        + "]\n"
        + noteCODE;
    
    private static final String noteSaveFlowSlotTicket = noteController + "Создание или обновление тикета в указанном слоте #####\n\n"
        + "HTTP Context: http://server:port/wf/service/flow/setFlowSlots_ServiceData\n\n"
        + "Параметры:\n\n"
        + "- nID_FlowSlot - ID сущности FlowSlot (обязательный)\n"
        + "- nID_Subject - ID сущнсоти Subject - субьект пользователь услуги, который подписывается на слот (обязательный)\n"
        + "- nID_Task_Activiti - ID таски активити процесса предоставления услуги (не обязательный - вначале он null, а потом засчивается после подтверждения тикета, и создания процесса)\n\n"
        + "Пример: http://test.igov.org.ua/wf/service/flow/setFlowSlot_ServiceData\n\n"
        + "- nID_FlowSlot=1\n"
        + "- nID_Subject=2\n\n"
        + "Ответ: HTTP STATUS 200\n\n"
        + noteCODEJSON
        + "{ \"nID_Ticket\": 1000 }\n"
        + noteCODE
        + "Поля в ответе:\n\n"
        + "-поле \"nID_Ticket\" - ID созданной/измененной сущности FlowSlotTicket.\n";

    private static final String noteBuildFlowSlots = noteController + "Генерация слотов на заданный интервал для заданного потока #####\n\n"
        + "HTTP Context: http://server:port/wf/service/flow/buildFlowSlots\n\n"
        + "Параметры:\n\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- sDateStart - дата \"начиная с такого-то момента времени\", в формате \"2015-06-28 12:12:56.001\" (опциональный)\n"
        + "- sDateStop - дата \"заканчивая к такому-то моменту времени\", в формате \"2015-07-28 12:12:56.001\" (опциональный)\n"
        + "Пример: http://test.igov.org.ua/wf/service/flow/buildFlowSlots\n\n"
        + "- nID_Flow_ServiceData=1\n\n"
        + "- sDateStart=2015-06-01 00:00:00.000\n\n"
        + "- sDateStop=2015-06-07 00:00:00.000\n\n"
        + "Ответ: HTTP STATUS 200 + json перечисление всех сгенерированных слотов.\n\n"
        + "Ниже приведена часть json ответа:\n\n"
        + noteCODEJSON
        + "[\n"
        + "    {\n"
        + "        \"nID\": 1000,\n"
        + "        \"sTime\": \"08:00\",\n"
        + "        \"nMinutes\": 15,\n"
        + "        \"bFree\": true\n"
        + "    },\n"
        + "    {\n"
        + "        \"nID\": 1001,\n"
        + "        \"sTime\": \"08:15\",\n"
        + "        \"nMinutes\": 15,\n"
        + "        \"bFree\": true\n"
        + "    },\n"
        + "    {\n"
        + "        \"nID\": 1002,\n"
        + "        \"sTime\": \"08:30\",\n"
        + "        \"nMinutes\": 15,\n"
        + "        \"bFree\": true\n"
        + "    },\n"
        + "...\n"
        + "]\n\n"
        + "Если на указанные даты слоты уже сгенерены то они не будут генерится повторно, и в ответ включаться не будут.\n"
        + noteCODE;

    private static final String noteClearFlowSlots = noteController + "Удаление слотов на заданный интервал для заданного потока #####\n\n"
        + "HTTP Context: http://server:port/wf/service/flow/clearFlowSlots\n\n"
        + "Параметры:\n\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- sDateStart - дата \"начиная с такого-то момента времени\", в формате \"2015-06-28 12:12:56.001\" (обязательный)\n"
        + "- sDateStop - дата \"заканчивая к такому-то моменту времени\", в формате \"2015-07-28 12:12:56.001\" (обязательный)\n"
        + "- bWithTickets - удалять ли слоты с тикетами, отвязывая тикеты от слотов? (опциональный, по умолчанию false)\n\n"
        + "Пример:\n"
        + "\"http://test.igov.org.ua/wf/service/flow/clearFlowSlots?nID_Flow_ServiceData=1&sDateStart=2015-06-01 00:00:00.000&sDateStop=2015-06-07 00:00:00.000\"\n\n"
        + "Ответ: HTTP STATUS 200 + json Обьект содержащий 2 списка:\n\n"
        + "- aDeletedSlot - удаленные слоты\n"
        + "- aSlotWithTickets - слоты с тикетами. Елси bWithTickets=true то эти слоты тоже удаляются и будут перечислены в aDeletedSlot, иначе - не удаляются.\n\n"
        + "Ниже приведена часть json ответа:\n\n"
        + noteCODEJSON
        + "{\n"
        + "    \"aDeletedSlot\": [\n"
        + "        {\n"
        + "            \"nID\": 1000,\n"
        + "            \"sTime\": \"08:00\",\n"
        + "            \"nMinutes\": 15,\n"
        + "            \"bFree\": true\n"
        + "        },\n"
        + "        {\n"
        + "            \"nID\": 1001,\n"
        + "            \"sTime\": \"08:15\",\n"
        + "            \"nMinutes\": 15,\n"
        + "            \"bFree\": true\n"
        + "        },\n"
        + "        ...\n"
        + "     ],\n"
        + "     \"aSlotWithTickets\": []\n"
        + "}\n"
        + noteCODE;

    private static final String noteGetSheduleFlowIncludes = noteController + "Получение расписаний включений #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/getSheduleFlowIncludes?nID_Flow_ServiceData=flowId\n\n"
        + "- flowId - ID потока\n\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getSheduleFlowIncludes?nID_Flow_ServiceData=1\n\n"
        + "Пример результата\n\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sData\": null,\n"
        + "    \"bExclude\": false,\n"
        + "    \"sName\": \"Test\",\n"
        + "    \"sRegionTime\": \"\"10:30-11:30\"\",\n"
        + "    \"saRegionWeekDay\": \"\"mo,tu\"\",\n"
        + "    \"sDateTimeAt\": \"\"2010-08-01 10:10:30\"\",\n"
        + "    \"sDateTimeTo\": \"\"2010-08-01 18:10:00\"\",\n"
        + "    \"nID\": 20367,\n"
        + "    \"nID_FlowPropertyClass\": {\n"
        + "      \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "      \"nID\": 1,\n"
        + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "    }\n"
        + "  },\n"
        + "  {\n"
        + "    \"sData\": null,\n"
        + "    \"bExclude\": false,\n"
        + "    \"sName\": \"Test\",\n"
        + "    \"sRegionTime\": \"10:30-11:30\",\n"
        + "    \"saRegionWeekDay\": \"mo,tu\",\n"
        + "    \"sDateTimeAt\": \"10:30\",\n"
        + "    \"sDateTimeTo\": \"12:30\",\n"
        + "    \"nID\": 20364,\n"
        + "    \"nID_FlowPropertyClass\": {\n"
        + "      \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "      \"nID\": 1,\n"
        + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "    }\n"
        + "  }\n"
        + "]\n"
        + noteCODE;

    private static final String noteGetSheduleFlowExcludes = noteController + "Получение расписаний исключений #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/getSheduleFlowExcludes?nID_Flow_ServiceData=flowId*\n\n"
        + "-flowId - ID потока\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getSheduleFlowExcludes?nID_Flow_ServiceData=1\n\n"
        + "Пример результата\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sData\": null,\n"
        + "    \"bExclude\": true,\n"
        + "    \"sName\": \"Test\",\n"
        + "    \"sRegionTime\": \"10:30-11:30\",\n"
        + "    \"saRegionWeekDay\": \"mo,tu\"\n"
        + "    \"sDateTimeAt\": \"2010-08-01 10:10:30\",\n"
        + "    \"sDateTimeTo\": \"2010-08-01 18:10:00\",\n"
        + "    \"nID\": 20367,\n"
        + "    \"nID_FlowPropertyClass\": {\n"
        + "      \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "      \"nID\": 1,\n"
        + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "    }\n"
        + "  },\n"
        + "  {\n"
        + "    \"sData\": null,\n"
        + "    \"bExclude\": false,\n"
        + "    \"sName\": \"Test\",\n"
        + "    \"sRegionTime\": \"10:30-11:30\",\n"
        + "    \"saRegionWeekDay\": \"mo,tu\",\n"
        + "    \"sDateTimeAt\": \"10:30\",\n"
        + "    \"sDateTimeTo\": \"12:30\",\n"
        + "    \"nID\": 20364,\n"
        + "    \"nID_FlowPropertyClass\": {\n"
        + "      \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "      \"nID\": 1,\n"
        + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "    }\n"
        + "  }\n"
        + "]\n"
        + noteCODE;

    private static final String noteSetSheduleFlowInclude = noteController + "Добавление/изменение расписания включений #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/setSheduleFlowInclude?nID_Flow_ServiceData=nID_Flow_ServiceData&sName=sName&sRegionTime=sRegionTime&sDateTimeAt=sDateTimeAt&sDateTimeTo=sDateTimeTo&saRegionWeekDay=saRegionWeekDay\n\n"
        + "- nID - ИД-номер //опциональный ,если задан - редактирование\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- sName - Строка-название (\"Вечерний прием\")\n"
        + "- sRegionTime - Строка период времени (\"14:16-16-30\")\n"
        + "- saRegionWeekDay - Массив дней недели (\"su,mo,tu\")\n"
        + "- sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")\n"
        + "- sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")\n"
        + "- sData - Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})\n"
        + "- nLen - Число, определяющее длительность слота\n"
        + "- sLenType - Строка определяющее тип длительности слота\n\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/setSheduleFlowInclude?nID_Flow_ServiceData=1&sName=Test&sRegionTime=%2210:30-11:30%22&sDateTimeAt=%222010-08-01%2010:10:30%22&sDateTimeTo=%222010-08-01%2018:10:00%22&saRegionWeekDay=%22mo,tu%22\n\n"
        + "Пример результата\n"
        + noteCODEJSON
        + "{\n"
        + "  \"sData\": null,\n"
        + "  \"bExclude\": false,\n"
        + "  \"sName\": \"Test\",\n"
        + "  \"sRegionTime\": \"10:30-11:30\",\n"
        + "  \"saRegionWeekDay\": \"mo,tu\",\n"
        + "  \"sDateTimeAt\": \"2010-08-01 10:10:30\",\n"
        + "  \"sDateTimeTo\": \"2010-08-01 18:10:00\",\n"
        + "  \"nID\": 20367,\n"
        + "  \"nID_FlowPropertyClass\": {\n"
        + "    \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "    \"nID\": 1,\n"
        + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "  }\n"
        + "}\n"
        + noteCODE;

    private static final String noteSetSheduleFlowExclude = noteController + "Добавление/изменение расписания исключения #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/setSheduleFlowExclude?nID_Flow_ServiceData=nID_Flow_ServiceData&sName=sName&sRegionTime=sRegionTime&sDateTimeAt=sDateTimeAt&sDateTimeTo=sDateTimeTo&saRegionWeekDay=saRegionWeekDay\n\n"
        + "- nID - ИД-номер //опциональный ,если задан - редактирование\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- sName - Строка-название (\"Вечерний прием\")\n"
        + "- sRegionTime - Строка период времени (\"14:16-16-30\")\n"
        + "- saRegionWeekDay - Массив дней недели (\"su,mo,tu\")\n"
        + "- sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")\n"
        + "- sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")\n"
        + "- sData - Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})\n"
        + "- nLen - Число, определяющее длительность слота\n"
        + "- sLenType - Строка определяющее тип длительности слота\n\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/setSheduleFlowExclude?nID_Flow_ServiceData=1&sName=Test&sRegionTime=%2210:30-11:30%22&sDateTimeAt=%222010-08-01%2010:10:30%22&sDateTimeTo=%222010-08-01%2018:10:00%22&saRegionWeekDay=%22mo,tu%22\n"
        + noteCODEJSON
        + "Пример результата\n"
        + "{\n"
        + "  \"sData\": null,\n"
        + "  \"bExclude\": true,\n"
        + "  \"sName\": \"Test\",\n"
        + "  \"sRegionTime\": \"10:30-11:30\",\n"
        + "  \"saRegionWeekDay\": \"mo,tu\",\n"
        + "  \"sDateTimeAt\": \"2010-08-01 10:10:30\",\n"
        + "  \"sDateTimeTo\": \"2010-08-01 18:10:00\",\n"
        + "  \"nID\": 20367,\n"
        + "  \"nID_FlowPropertyClass\": {\n"
        + "    \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "    \"nID\": 1,\n"
        + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "  }\n"
        + "}\n"
        + noteCODE;

    private static final String noteRemoveSheduleFlowInclude = noteController + "Удаление расписания включений #####\n\n"
        + "HTTP Context: hhttps://test.region.igov.org.ua/wf/service/flow/removeSheduleFlowInclude?nID_Flow_ServiceData=nID_Flow_ServiceData&nID=nID\n\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- nID - ИД-номер\n\n"
        + "Ответ: Массив объектов сущности расписаний включений\n\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/removeSheduleFlowInclude?nID_Flow_ServiceData=1&nID=20367\n\n"
        + "Пример результата\n"
        + noteCODEJSON
        + "{\n"
        + "  \"sData\": null,\n"
        + "  \"bExclude\": false,\n"
        + "  \"sName\": \"Test\",\n"
        + "  \"sRegionTime\": \"10:30-11:30\",\n"
        + "  \"saRegionWeekDay\": \"mo,tu\",\n"
        + "  \"sDateTimeAt\": \"2010-08-01 10:10:30\",\n"
        + "  \"sDateTimeTo\": \"2010-08-01 18:10:00\",\n"
        + "  \"nID\": 20367,\n"
        + "  \"nID_FlowPropertyClass\": {\n"
        + "    \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "    \"nID\": 1,\n"
        + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "  }\n"
        + "}\n"
        + noteCODE;

    private static final String noteRemoveSheduleFlowExclude = noteController + "Удаление расписания исключений #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/removeSheduleFlowExclude?nID_Flow_ServiceData=nID_Flow_ServiceData&nID=nID\n\n"
        + "- nID_Flow_ServiceData - номер-ИД потока (обязательный если нет sID_BP)\n"
        + "- sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)\n"
        + "- nID - ИД-номер\n\n"
        + "Ответ: Массив объектов сущности расписаний исключений\n\n"
        + "Пример:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/removeSheduleFlowExclude?nID_Flow_ServiceData=1&nID=20367\n\n"
        + "Пример результата\n\n"
        + noteCODEJSON
        + "{\n"
        + "  \"sData\": null,\n"
        + "  \"bExclude\": true,\n"
        + "  \"sName\": \"Test\",\n"
        + "  \"sRegionTime\": \"10:30-11:30\",\n"
        + "  \"saRegionWeekDay\": \"mo,tu\",\n"
        + "  \"sDateTimeAt\": \"2010-08-01 10:10:30\",\n"
        + "  \"sDateTimeTo\": \"2010-08-01 18:10:00\",\n"
        + "  \"nID\": 20367,\n"
        + "  \"nID_FlowPropertyClass\": {\n"
        + "    \"sPath\": \"org.igov.model.flow.handler.DefaultFlowSlotScheduler\",\n"
        + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
        + "    \"nID\": 1,\n"
        + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
        + "  }\n"
        + "}\n"
        + noteCODE;
    
    private static final String noteGetFlowSlotTickets = noteController + "Получение активных тикетов #####\n\n"
        + "HTTP Context: https://test.region.igov.org.ua/wf/service/flow/getFlowSlotTickets?sLogin=sLogin&bEmployeeUnassigned=true|false&sDate=yyyy-MM-dd\n\n"
        + "возвращает активные тикеты, отсортированные по startDate\n\n"
        + "- sLogin - имя пользоватеял для которого необходимо вернуть тикеты\n"
        + "- bEmployeeUnassigned - опциональный параметр (false по умолчанию). Если true - возвращать тикеты не заассайненые на пользователей\n"
        + "- sDate - опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.\n\n"
        + "Примеры:\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getFlowSlotTickets?sLogin=kermit\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-07-20T15:15:00\",\n"
        + "    \"sDateEdit\": \"2015-07-06T11:03:52\",\n"
        + "    \"sTaskDate\": \"2015-07-30T10:03:43\",\n"
        + "    \"sDateFinish\": \"2015-07-20T15:30:00\",\n"
        + "    \"nID_FlowSlot\": \"6\",\n"
        + "    \"sNameBP\": \"Киев - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Надання послуги: Огляд авто\",\n"
        + "    \"nID\": \"20005\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-07-20T15:45:00\",\n"
        + "    \"sDateEdit\": \"2015-07-06T23:25:15\",\n"
        + "    \"sTaskDate\": \"2015-07-06T23:27:18\",\n"
        + "    \"sDateFinish\": \"2015-07-20T16:00:00\",\n"
        + "    \"nID_FlowSlot\": \"7\",\n"
        + "    \"sNameBP\": \"Киев - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Надання послуги: Огляд авто\",\n"
        + "    \"nID\": \"20010\"\n"
        + "  }\n"
        + "]\n"
        + noteCODE
        + "\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getFlowSlotTickets?sLogin=kermit&bEmployeeUnassigned=true\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-08-03T08:00:00\",\n"
        + "    \"sDateEdit\": \"2015-07-30T23:10:58\",\n"
        + "    \"sTaskDate\": \"2015-07-30T23:50:07\",\n"
        + "    \"sDateFinish\": \"2015-08-03T08:15:00\",\n"
        + "    \"nID_FlowSlot\": \"20086\",\n"
        + "    \"sNameBP\": \"Днепропетровск - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Друк держ.номерів\",\n"
        + "    \"nID\": \"20151\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-08-03T08:15:00\",\n"
        + "    \"sDateEdit\": \"2015-07-31T21:00:56\",\n"
        + "    \"sTaskDate\": \"2015-07-31T21:01:19\",\n"
        + "    \"sDateFinish\": \"2015-08-03T08:30:00\",\n"
        + "    \"nID_FlowSlot\": \"20023\",\n"
        + "    \"sNameBP\": \"Киев - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Перевірка наявності обтяжень\",\n"
        + "    \"nID\": \"20357\"\n"
        + "  }\n"
        + "]\n"
        + noteCODE
        + "\n"
        + "https://test.region.igov.org.ua/wf/service/flow/getFlowSlotTickets?sLogin=kermit&bEmployeeUnassigned=true&sDate=2015-07-20\n"
        + noteCODEJSON
        + "[\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-07-20T15:15:00\",\n"
        + "    \"sDateEdit\": \"2015-07-06T11:03:52\",\n"
        + "    \"sTaskDate\": \"2015-07-30T10:03:43\",\n"
        + "    \"sDateFinish\": \"2015-07-20T15:30:00\",\n"
        + "    \"nID_FlowSlot\": \"6\",\n"
        + "    \"sNameBP\": \"Киев - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Надання послуги: Огляд авто\",\n"
        + "    \"nID\": \"20005\"\n"
        + "  },\n"
        + "  {\n"
        + "    \"sDateStart\": \"2015-07-20T15:45:00\",\n"
        + "    \"sDateEdit\": \"2015-07-06T23:25:15\",\n"
        + "    \"sTaskDate\": \"2015-07-06T23:27:18\",\n"
        + "    \"sDateFinish\": \"2015-07-20T16:00:00\",\n"
        + "    \"nID_FlowSlot\": \"7\",\n"
        + "    \"sNameBP\": \"Киев - Реєстрація авто з пробігом в МРЕВ\",\n"
        + "    \"nID_Subject\": \"20045\",\n"
        + "    \"sUserTaskName\": \"Надання послуги: Огляд авто\",\n"
        + "    \"nID\": \"20010\"\n"
        + "  }\n"
        + "]\n"
        + noteCODE;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    @Qualifier("flowPropertyDao")
    private GenericEntityDao<FlowProperty> flowPropertyDao;

    @Autowired
    private FlowServiceDataDao flowServiceDataDao;

    @Autowired
    @Qualifier("flowPropertyClassDao")
    private GenericEntityDao<FlowPropertyClass> flowPropertyClassDao;

    @Autowired
    @Qualifier("subjectOrganDepartmentDao")
    private GenericEntityDao<SubjectOrganDepartment> subjectOrganDepartmentDao;

    @Autowired
    private FlowSlotTicketDao flowSlotTicketDao;

    /**
     * Получение слотов по сервису сгруппированных по дням.
     * @param nID_Service номер-ИД услуги  (обязательный если нет sID_BP и nID_ServiceData)
     * @param nID_ServiceData ID сущности ServiceData (обязательный если нет sID_BP и nID_Service)
     * @param sID_BP строка-ИД бизнес-процесса (обязательный если нет nID_ServiceData и nID_Service)
     * @param nID_SubjectOrganDepartment ID департамента субьекта-органа  (опциональный, по умолчанию false)
     * @param bAll если false то из возвращаемого объекта исключаются элементы, содержащие "bHasFree":false "bFree":false (опциональный, по умолчанию false)
     * @param nDays колличество дней от сегодняшего включительно(или sDateStart, если задан), до nDays в будующее за который нужно вернуть слоты (опциональный, по умолчанию 177 - пол года)
     * @param nFreeDays дни со слотами будут включаться в результат пока не наберется указанное кол-во свободных дней (опциональный, по умолчанию 60)
     * @param sDateStart опциональный параметр, определяющие дату начала в формате "yyyy-MM-dd", с которую выбрать слоты. При наличии этого параметра слоты возвращаются только за указанный период(число дней задается nDays).
     */
    @ApiOperation(value = "Получение слотов по сервису сгруппированных по дням", notes = noteGetFlowSlots )
    @RequestMapping(value = "/getFlowSlots_ServiceData", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getFlowSlots(
	    @ApiParam(value = "номер-ИД услуги  (обязательный если нет sID_BP и nID_ServiceData)", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
	    @ApiParam(value = "ID сущности ServiceData (обязательный если нет sID_BP и nID_Service)", required = false) @RequestParam(value = "nID_ServiceData", required = false) Long nID_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса (обязательный если нет nID_ServiceData и nID_Service)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "ID департамента субьекта-органа", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
	    @ApiParam(value = "если false то из возвращаемого объекта исключаются элементы, содержащие \"bHasFree\":false \"bFree\":false (опциональный, по умолчанию false)", required = false) @RequestParam(value = "bAll", required = false, defaultValue = "false") boolean bAll,
	    @ApiParam(value = "дни со слотами будут включаться в результат пока не наберется указанное кол-во свободных дней (опциональный, по умолчанию 60)", required = false) @RequestParam(value = "nFreeDays", required = false, defaultValue = "60") int nFreeDays,
	    @ApiParam(value = "количество дней от сегодняшего включительно(или sDateStart, если задан), до nDays в будующее за который нужно вернуть слоты (опциональный, по умолчанию 177 - пол года)", required = false) @RequestParam(value = "nDays", required = false, defaultValue = "177") int nDays,
	    @ApiParam(value = "параметр, определяющие дату начала в формате \"yyyy-MM-dd\", с которую выбрать слоты. При наличии этого параметра слоты возвращаются только за указанный период(число дней задается nDays)", required = false) @RequestParam(value = "sDateStart", required = false) String sDateStart
    ) throws Exception {

        DateTime oDateStart = DateTime.now().withTimeAtStartOfDay();
        oDateStart = oDateStart.plusDays(2);
        DateTime oDateEnd = oDateStart.plusDays(nDays);

        if (sDateStart != null) {
            oDateStart = JsonDateSerializer.DATE_FORMATTER.parseDateTime(sDateStart);
            oDateEnd = oDateStart.plusDays(nDays);
        }

        Days res = flowService.getFlowSlots(nID_Service, nID_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                oDateStart, oDateEnd, bAll, nFreeDays);

        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * @param sID_BP имя Activiti BP
     */
    @ApiOperation(value = "Получение массива объектов SubjectOrganDepartment по ID бизнес процесса", notes = noteGetFlowSlotDepartment )
    @RequestMapping(value = "/getFlowSlots_Department", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getFlowSlotDepartment( @ApiParam(value = "имя Activiti BP", required = true)  @RequestParam(value = "sID_BP") String sID_BP
    ) throws Exception {

        List<Flow_ServiceData> serviceDataList = flowServiceDataDao.findAllBy("sID_BP", sID_BP);
        SubjectOrganDepartment[] result = new SubjectOrganDepartment[serviceDataList.size()];
        for (int i = 0; i < serviceDataList.size(); i++) {
            Flow_ServiceData sd = serviceDataList.get(i);
            Long nID_SubjectOrganDepartment = sd.getnID_SubjectOrganDepartment();
            SubjectOrganDepartment subjectOrganDepartment = subjectOrganDepartmentDao
                    .findByIdExpected(nID_SubjectOrganDepartment);
            result[i] = subjectOrganDepartment;
        }

        return JsonRestUtils.toJsonResponse(result);
    }

    @ApiOperation(value = "Создание или обновление тикета в указанном слоте.", notes = noteSaveFlowSlotTicket )
    @RequestMapping(value = "/setFlowSlot_ServiceData", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity saveFlowSlotTicket(@ApiParam(value = "ID сущности FlowSlot", required = true) @RequestParam(value = "nID_FlowSlot") Long nID_FlowSlot,
	    @ApiParam(value = "ID сущнсоти Subject - субьект пользователь услуги, который подписывается на слот", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
	    @ApiParam(value = "ID таски активити процесса предоставления услуги (не обязательный - вначале он null, а потом засчивается после подтверждения тикета, и создания процесса)", required = false) @RequestParam(value = "nID_Task_Activiti", required = false) Long nID_Task_Activiti) throws Exception {

        FlowSlotTicket oFlowSlotTicket = flowService.saveFlowSlotTicket(nID_FlowSlot, nID_Subject, nID_Task_Activiti);

        return JsonRestUtils.toJsonResponse(new SaveFlowSlotTicketResponse(oFlowSlotTicket.getId()));
    }

    /**
     * Генерация слотов на заданный интервал для заданного потока.
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)
     * @param sDateStart дата "начиная с такого-то момента времени", в формате "2015-06-28 12:12:56.001" (опциональный)
     * @param sDateStop дата "заканчивая к такому-то моменту времени", в формате "2015-07-28 12:12:56.001" (опциональный)
     */
    @ApiOperation(value = "Генерация слотов на заданный интервал для заданного потока", notes = noteBuildFlowSlots )
    @RequestMapping(value = "/buildFlowSlots", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity buildFlowSlots(
	    @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
	    @ApiParam(value = "дата, начиная с такого-то момента времени, в формате \"2015-06-28 12:12:56.001\"", required = false) @RequestParam(value = "sDateStart", required = false) String sDateStart,
	    @ApiParam(value = "дата, заканчивая к такому-то моменту времени, в формате \"2015-07-28 12:12:56.001\"", required = false) @RequestParam(value = "sDateStop", required = false) String sDateStop) {

        DateTime startDate = null;
        if (sDateStart != null) {
            startDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDateStart);
        }

        DateTime stopDate = null;
        if (sDateStop != null) {
            stopDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDateStop);
        }

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                log.error(sError);
                return new ResponseEntity<>(sError, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            log.error(sError);
            return new ResponseEntity<>(sError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("sID_BP=" + sID_BP + ",nID_Flow_ServiceData=" + nID_Flow_ServiceData);

        List<FlowSlotVO> res = flowService.buildFlowSlots(nID_Flow_ServiceData, startDate, stopDate);

        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * Удаление слотов на заданный интервал для заданного потока.
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)
     * @param sDateStart дата "начиная с такого-то момента времени", в формате "2015-06-28 12:12:56.001" (обязательный)
     * @param sDateStop дата "заканчивая к такому-то моменту времени", в формате "2015-07-28 12:12:56.001" (обязательный)
     * @param bWithTickets удалять ли слоты с тикетами, отвязывая тикеты от слотов? (опциональный, по умолчанию false)
     * @param aDeletedSlot удаленные слоты
     * @param bWithTickets слоты с тикетами. Елси bWithTickets=true то эти слоты тоже удаляются и будут перечислены в aDeletedSlot, иначе - не удаляются.
     */
    @ApiOperation(value = "Удаление слотов на заданный интервал для заданного потока", notes = noteClearFlowSlots )
    @RequestMapping(value = "/clearFlowSlots", method = RequestMethod.DELETE)
    public
    @ResponseBody
    ResponseEntity clearFlowSlots(
	    @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "дата, начиная с такого-то момента времени, в формате \"2015-06-28 12:12:56.001\"", required = true) @RequestParam(value = "sDateStart") String sDateStart,
            @ApiParam(value = "дата, заканчивая к такому-то моменту времени, в формате \"2015-07-28 12:12:56.001\"", required = true) @RequestParam(value = "sDateStop") String sDateStop,
            @ApiParam(value = "слоты с тикетами. Елси bWithTickets=true то эти слоты тоже удаляются и будут перечислены в aDeletedSlot, иначе - не удаляются.", required = false) @RequestParam(value = "bWithTickets", required = false, defaultValue = "false")
            boolean bWithTickets) {
        DateTime startDate = null;
        if (sDateStart != null) {
            startDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDateStart);
        }

        DateTime stopDate = null;
        if (sDateStop != null) {
            stopDate = JsonDateTimeSerializer.DATETIME_FORMATTER.parseDateTime(sDateStop);
        }

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                log.error(sError);
                return new ResponseEntity<>(sError, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            log.error(sError);
            return new ResponseEntity<>(sError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("sID_BP=" + sID_BP + ",nID_Flow_ServiceData=" + nID_Flow_ServiceData);

        ClearSlotsResult res = flowService.clearFlowSlots(nID_Flow_ServiceData, startDate, stopDate, bWithTickets);
        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * Returns list of included schedules within flow
     *
     * @param nID_Flow_ServiceData - ID of flow
     * @return List of schedule with bExclude=false
     */
    @ApiOperation(value = "Получение расписаний включений", notes = noteGetSheduleFlowIncludes )
    @RequestMapping(value = "/getSheduleFlowIncludes", method = RequestMethod.GET)
    public
    @ResponseBody
    List<FlowProperty> getSheduleFlowIncludes(
	    @ApiParam(value = "ID потока", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment

    ) throws Exception {
        //if (nID_Flow_ServiceData != null) {
        //log.info("nID_Flow_ServiceData is not null. Getting flow property for the flow with ID: " + nID_Flow_ServiceData);
        return getFilteredFlowPropertiesForFlowServiceData(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                Boolean.FALSE);
        //}
        //return new LinkedList<FlowProperty>();
    }

    /**
     * Returns list of excluded schedules within flow
     *
     * @param nID_Flow_ServiceData - ID of flow
     * @return List of schedule with bExclude=false
     */
    @ApiOperation(value = "Получение расписаний исключений", notes = noteGetSheduleFlowExcludes )
    @RequestMapping(value = "/getSheduleFlowExcludes", method = RequestMethod.GET)
    public
    @ResponseBody
    List<FlowProperty> getSheduleFlowExcludes(
	    @ApiParam(value = "ID потока", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {
        //if (nID_Flow_ServiceData != null) {
        //log.info("nID_Flow_ServiceData is not null. Getting flow property for the flow with ID: " + nID_Flow_ServiceData);
        return getFilteredFlowPropertiesForFlowServiceData(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                Boolean.TRUE);
        //}
        //return new LinkedList<FlowProperty>();
    }

    /**
     * Adds/removes schedule include slot
     *
     * @param nID                  - ID of flow property
     * @param nID_Flow_ServiceData - ID of flow
     * @param sName                - name of the slot
     * @param sRegionTime          - time period, "14:16-16:30"
     * @param saRegionWeekDay      - array of days in a week ("su,mo,tu")
     * @return ID of new FlowProperty
     */
    @ApiOperation(value = "Добавление/изменение расписания включений", notes = noteSetSheduleFlowInclude )
    @RequestMapping(value = "/setSheduleFlowInclude", method = RequestMethod.GET)
    public
    @ResponseBody
    FlowProperty setSheduleFlowInclude(
	    @ApiParam(value = "ИД-номер, если задан - редактирование", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
	    @ApiParam(value = "Строка-название (\"Вечерний прием\")", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "Строка период времени (\"14:16-16-30\")", required = true) @RequestParam(value = "sRegionTime") String sRegionTime,

	    @ApiParam(value = "Число, определяющее длительность слота", required = false) @RequestParam(value = "nLen", required = false) Integer nLen,
	    @ApiParam(value = "Строка определяющее тип длительности слота", required = false) @RequestParam(value = "sLenType", required = false) String sLenType,
	    @ApiParam(value = "Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})", required = false) @RequestParam(value = "sData", required = false) String sData,

	    @ApiParam(value = "Массив дней недели (\"su,mo,tu\")", required = true) @RequestParam(value = "saRegionWeekDay") String saRegionWeekDay,
	    @ApiParam(value = "Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")", required = true) @RequestParam(value = "sDateTimeAt") String sDateTimeAt,
	    @ApiParam(value = "Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")", required = true) @RequestParam(value = "sDateTimeTo") String sDateTimeTo) throws Exception {
        FlowProperty flowProperty = null;
        if (sRegionTime != null && saRegionWeekDay != null && nLen != null) {
            sData = QuartzUtil.getQuartzFormulaByParameters(sRegionTime, saRegionWeekDay, nLen);
        }
        if (nID != null) {
            log.info("nID is not null. Updating existing FLowProperty with parameters");
            flowProperty = flowPropertyDao.findByIdExpected(nID);
            if (flowProperty != null) {
                flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay,
                        sDateTimeAt, sDateTimeTo, nLen, sLenType, sData, flowProperty);
                flowProperty.setbExclude(false);

                flowPropertyDao.saveOrUpdate(flowProperty);
                log.info("nID is not null. Updating existing FLowProperty with parameters");
            } else {
                log.info("Have not found FlowProperty object with ID: " + nID);
            }
        } else {

            if (nID_Flow_ServiceData == null) {
                if (sID_BP != null) {
                    nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                } else {
                    String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                    log.error(sError);
                    throw new Exception(sError);
                }
            }
            if (nID_Flow_ServiceData == null) {
                String sError = "nID_Flow_ServiceData==null";
                log.error(sError);
                throw new Exception(sError);
            }

            log.info("Creating new flow property for the flow with ID: "
                    + nID_Flow_ServiceData);
            flowProperty = new FlowProperty();

            FlowPropertyClass flowPropertyClass = flowPropertyClassDao.findByIdExpected(DEFAULT_FLOW_PROPERTY_CLASS);
            log.info("Loaded flow propetry service class: " + flowPropertyClass);
            Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
            log.info("Loaded flow service data class: " + flowServiceData);

            flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay, sDateTimeAt, sDateTimeTo, nLen,
                    sLenType, sData, flowProperty);
            flowProperty.setoFlowPropertyClass(flowPropertyClass);
            flowProperty.setbExclude(false);
            flowProperty.setoFlow_ServiceData(flowServiceData);

            flowServiceData.getFlowProperties().add(flowProperty);

            flowServiceDataDao.saveOrUpdate(flowServiceData);
            log.info("Successfully updated flow with new FlowProperty.");
        }
        return flowProperty;
    }

    /**
     * Adds/removes schedule exclude slot
     *
     * @param nID                  - ID of flow property
     * @param nID_Flow_ServiceData - ID of flow
     * @param sName                - name of the slot
     * @param sRegionTime          - time period, "14:16-16:30"
     * @param saRegionWeekDay      - array of days in a week ("su,mo,tu")
     * @return ID of new FlowProperty
     */
    @ApiOperation(value = "Добавление/изменение расписания исключения", notes = noteSetSheduleFlowExclude )
    @RequestMapping(value = "/setSheduleFlowExclude", method = RequestMethod.GET)
    public
    @ResponseBody
    FlowProperty setSheduleFlowExclude(
	    @ApiParam(value = "ИД-номер //опциональный ,если задан - редактирование", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
	    @ApiParam(value = "Строка-название (\"Вечерний прием\")", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "Строка период времени (\"14:16-16-30\")", required = true) @RequestParam(value = "sRegionTime") String sRegionTime,

	    @ApiParam(value = "Число, определяющее длительность слота", required = false) @RequestParam(value = "nLen", required = false) Integer nLen,
	    @ApiParam(value = "Строка определяющее тип длительности слота", required = false) @RequestParam(value = "sLenType", required = false) String sLenType,
	    @ApiParam(value = "Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})", required = false) @RequestParam(value = "sData", required = false) String sData,

	    @ApiParam(value = "Массив дней недели (\"su,mo,tu\")", required = true) @RequestParam(value = "saRegionWeekDay") String saRegionWeekDay,
	    @ApiParam(value = "Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")", required = true) @RequestParam(value = "sDateTimeAt") String sDateTimeAt,
	    @ApiParam(value = "Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")", required = true) @RequestParam(value = "sDateTimeTo") String sDateTimeTo) throws Exception {
        FlowProperty flowProperty = null;
        if (nID != null) {
            log.info("nID is not null. Updating existing FLowProperty with parameters");
            flowProperty = flowPropertyDao.findByIdExpected(nID);
            if (flowProperty != null) {
                flowProperty = fillFlowProperty(sName, sRegionTime,
                        saRegionWeekDay, sDateTimeAt, sDateTimeTo, nLen, sLenType, sData, flowProperty);
                flowProperty.setbExclude(true);
                flowPropertyDao.saveOrUpdate(flowProperty);
                log.info("nID is not null. Updating existing FLowProperty with parameters");
            } else {
                log.info("Have not found FlowProperty object with ID: " + nID);
            }
        } else {

            if (nID_Flow_ServiceData == null) {
                if (sID_BP != null) {
                    nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                } else {
                    String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                    log.error(sError);
                    throw new Exception(sError);
                }
            }
            if (nID_Flow_ServiceData == null) {
                String sError = "nID_Flow_ServiceData==null";
                log.error(sError);
                throw new Exception(sError);
            }

            log.info("Creating new flow property for the flow with ID: "
                    + nID_Flow_ServiceData);
            flowProperty = new FlowProperty();

            FlowPropertyClass flowPropertyClass = flowPropertyClassDao.findByIdExpected(DEFAULT_FLOW_PROPERTY_CLASS);
            log.info("Loaded flow propetry service class: " + flowPropertyClass);
            Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
            log.info("Loaded flow service data class: " + flowServiceData);

            flowProperty = fillFlowProperty(sName, sRegionTime, saRegionWeekDay, sDateTimeAt, sDateTimeTo, nLen,
                    sLenType, sData, flowProperty);
            flowProperty.setoFlowPropertyClass(flowPropertyClass);
            flowProperty.setbExclude(true);
            flowProperty.setoFlow_ServiceData(flowServiceData);

            flowServiceData.getFlowProperties().add(flowProperty);

            flowServiceDataDao.saveOrUpdate(flowServiceData);
            log.info("Successfully updated flow with new FlowProperty.");
        }
        return flowProperty;
    }

    /**
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)
     * @param nID ИД-номер
     */
    @ApiOperation(value = "Удаление расписания включений", notes = noteRemoveSheduleFlowInclude )
    @RequestMapping(value = "/removeSheduleFlowInclude", method = RequestMethod.GET)
    public
    @ResponseBody
    List<FlowProperty> removeSheduleFlowInclude(
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID") Long nID,
	    @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "ИД-номер", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                log.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            log.error(sError);
            throw new Exception(sError);
        }

        if (nID_Flow_ServiceData != null && nID != null) {
            log.info("nID_Flow_ServiceData is not null. Removing flow property with bExclude=false and ID:" + nID);

            Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);

            Iterator<FlowProperty> iterator = flowServiceData.getFlowProperties().iterator();
            while (iterator.hasNext()) {
                FlowProperty curr = iterator.next();
                log.info("Processing flow property with ID " + nID + " and bexclude=" + curr.getbExclude());

                if (curr.getId().equals(nID) && curr.getbExclude() != null && Boolean.valueOf(curr.getbExclude())
                        .equals(
                                Boolean.FALSE)) {
                    iterator.remove();
                    flowPropertyDao.delete(curr.getId());

                    log.info("Removed flow property with ID " + nID + " and bexclude=false");
                    break;
                }
            }

            log.info(
                    "Updated flow data. Removed FlowProperty schedules with bExlclude=true. Returning list without removed item:"
                            + flowServiceData.getFlowProperties().size());

            return flowServiceData.getFlowProperties();
        } else {
            log.info("nID or nID_Flow_ServiceData are empty. Skipping logic of the method removeSheduleFlowExclude");
        }
        return new LinkedList<FlowProperty>();
    }

    /**
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)
     * @param nID ИД-номер
     */
    @ApiOperation(value = "Удаление расписания исключений", notes = noteRemoveSheduleFlowExclude )
    @RequestMapping(value = "/removeSheduleFlowExclude", method = RequestMethod.GET)
    public
    @ResponseBody
    List<FlowProperty> removeSheduleFlowExclude(
	    @ApiParam(value = "ИД-номер", required = true) @RequestParam(value = "nID") Long nID,
	    @ApiParam(value = " номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
	    @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                log.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            log.error(sError);
            throw new Exception(sError);
        }

        if (nID_Flow_ServiceData != null && nID != null) {
            log.info("nID_Flow_ServiceData is not null. Removing flow property with bExclude=true and ID:" + nID);

            Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);

            Iterator<FlowProperty> iterator = flowServiceData.getFlowProperties().iterator();
            while (iterator.hasNext()) {
                FlowProperty curr = iterator.next();
                log.info("Processing flow property with ID " + nID + " and bexclude=" + curr.getbExclude());

                if (curr.getId().equals(nID) && curr.getbExclude() != null && Boolean.valueOf(curr.getbExclude())
                        .equals(
                                Boolean.TRUE)) {
                    iterator.remove();
                    flowPropertyDao.delete(curr.getId());

                    log.info("Removed flow property with ID " + nID + " and bexclude=true");
                    break;
                }
            }

            log.info(
                    "Updated flow data. Removed FlowProperty schedules with bExlclude=true. Returning list without removed item:"
                            + flowServiceData.getFlowProperties().size());

            return flowServiceData.getFlowProperties();
        } else {
            log.info("nID or nID_Flow_ServiceData are empty. Skipping logic of the method removeSheduleFlowExclude");
        }
        return new LinkedList<FlowProperty>();
    }

    /**
     * @param sLogin имя пользоватеял для которого необходимо вернуть тикеты
     * @param bEmployeeUnassigned опциональный параметр (false по умолчанию). Если true - возвращать тикеты не заассайненые на пользователей
     * @param sDate опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.
     */
    @ApiOperation(value = "Получение активных тикетов", notes = noteGetFlowSlotTickets )
    @RequestMapping(value = "/getFlowSlotTickets", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public
    @ResponseBody
    String getFlowSlotTickets(
	    @ApiParam(value = "имя пользоватеял для которого необходимо вернуть тикеты", required = true) @RequestParam(value = "sLogin") String sLogin,
	    @ApiParam(value = "опциональный параметр (false по умолчанию). Если true - возвращать тикеты не заассайненые на пользователей", required = false) @RequestParam(value = "bEmployeeUnassigned", required = false, defaultValue = "false") Boolean bEmployeeUnassigned,
	    @ApiParam(value = "опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.", required = false) @RequestParam(value = "sDate", required = false) String sDate
    ) throws Exception {

        List<Map<String, String>> res = new LinkedList<Map<String, String>>();

        List<Task> tasks = getTasksForChecking(sLogin, bEmployeeUnassigned);

        Map<Long, Task> taskActivityIDsMap = new HashMap<Long, Task>();
        for (Task task : tasks) {
            if (task.getProcessInstanceId() != null) {
                taskActivityIDsMap.put(Long.valueOf(task.getProcessInstanceId()), task);
            } else {
                log.info("Task with ID:" + task.getId() + " has null process instance id value");
            }
        }

        log.info("Will check tasks which belong to process definition IDs:" + taskActivityIDsMap.keySet());

        List<FlowSlotTicket> allFlowSlowTickets = flowSlotTicketDao.findAll();
        log.info("Found " + (allFlowSlowTickets != null ? allFlowSlowTickets.size() : 0) + " flow slot tickets.");
        if (allFlowSlowTickets != null) {

            Collections.sort(allFlowSlowTickets, new Comparator<FlowSlotTicket>() {
                @Override
                public int compare(FlowSlotTicket ticket1, FlowSlotTicket ticket2) {
                    return ticket1.getsDateStart().compareTo(ticket2.getsDateStart());
                }
            });

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date dateOfTasks = null;
            if (sDate != null) {
                log.info("Checking for flow spot tickets for the date: " + sDate);
                dateOfTasks = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
            }
            for (FlowSlotTicket currFlowSlotTicket : allFlowSlowTickets) {
                if (taskActivityIDsMap.keySet().contains(currFlowSlotTicket.getnID_Task_Activiti())) {
                    Task tasksByActivitiID = taskActivityIDsMap.get(currFlowSlotTicket.getnID_Task_Activiti());

                    if (dateOfTasks != null) {
                        log.info("Comparing two dates:" + currFlowSlotTicket.getsDateStart().toDate() + " and "
                                + dateOfTasks);
                    }
                    if (dateOfTasks == null || (DateUtils
                            .isSameDay(currFlowSlotTicket.getsDateStart().toDate(), dateOfTasks))) {
                        addFlowSlowTicketToResult(res, dateFormat, currFlowSlotTicket, tasksByActivitiID);
                    } else {
                        log.info("Skipping flowSlot " + currFlowSlotTicket.getId() + " for the task:"
                                + currFlowSlotTicket.getnID_Task_Activiti() +
                                " as they have not valid  start-end date" + currFlowSlotTicket.getsDateStart()
                                .toString() + ":" +
                                currFlowSlotTicket.getsDateFinish());
                    }
                }
            }
        }

        String jsonRes = JSONValue.toJSONString(res);
        log.info("Result" + jsonRes);
        return jsonRes;
    }

    private void addFlowSlowTicketToResult(List<Map<String, String>> res,
            SimpleDateFormat dateFormat, FlowSlotTicket currFlowSlowTicket,
            Task tasksByActivitiID) {
        Map<String, String> currRes = new HashMap<String, String>();

        StringBuilder sb = new StringBuilder();
        sb.append("Adding flow slot ticket: ");
        sb.append(currFlowSlowTicket.getId());
        sb.append(":");
        sb.append(currFlowSlowTicket.getnID_Subject());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateStart());
        sb.append(":");
        sb.append(currFlowSlowTicket.getsDateFinish());
        log.info(sb.toString());

        currRes.put("nID", currFlowSlowTicket.getId().toString());
        currRes.put("nID_FlowSlot", currFlowSlowTicket.getoFlowSlot() != null ?
                currFlowSlowTicket.getoFlowSlot().getId().toString() : "");
        currRes.put("nID_Subject", currFlowSlowTicket.getnID_Subject().toString());
        Date startDate = new Date(currFlowSlowTicket.getsDateStart().getMillis());
        currRes.put("sDateStart", dateFormat.format(startDate));
        Date finishDate = new Date(currFlowSlowTicket.getsDateFinish().getMillis());
        currRes.put("sDateFinish", dateFormat.format(finishDate));
        Date editDate = new Date(currFlowSlowTicket.getsDateEdit().getMillis());
        currRes.put("sDateEdit", dateFormat.format(editDate));

        currRes.put("nID_Task_Activiti", tasksByActivitiID.getId());

        currRes.put("name", tasksByActivitiID.getName());
        currRes.put("id", tasksByActivitiID.getId());
        currRes.put("assignee", tasksByActivitiID.getAssignee());
        currRes.put("nID_Instance", tasksByActivitiID.getProcessInstanceId());

        currRes.put("sUserTaskName", tasksByActivitiID.getName());
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(tasksByActivitiID.getProcessDefinitionId()).singleResult();
        currRes.put("sNameBP", processDefinition != null ? processDefinition.getName() : "");
        currRes.put("sTaskDate", dateFormat.format(tasksByActivitiID.getCreateTime()));
        res.add(currRes);
    }

    private List<Task> getTasksForChecking(String sLogin,
            Boolean bEmployeeUnassigned) {
        List<Task> tasks;
        if (bEmployeeUnassigned) {
            //tasks = taskService.createTaskQuery().taskUnassigned().active().list();
            tasks = taskService.createTaskQuery().taskCandidateUser(sLogin).taskUnassigned().active().list();
            log.info("Looking for unassigned tasks. Found " + (tasks != null ? tasks.size() : 0) + " tasks");
        } else {
            tasks = taskService.createTaskQuery().taskAssignee(sLogin).active().list();
            log.info("Looking for tasks assigned to user:" + sLogin + ". Found " + (tasks != null ? tasks.size() : 0)
                    + " tasks");
        }
        return tasks;
    }

    protected List<FlowProperty> getFilteredFlowPropertiesForFlowServiceData(Long nID_Flow_ServiceData,
            String sID_BP,
            Long nID_SubjectOrganDepartment,
            Boolean bExclude) throws Exception {

        if (nID_Flow_ServiceData == null) {
            if (sID_BP != null) {
                nID_Flow_ServiceData = flowServiceDataDao.findFlowId(sID_BP, nID_SubjectOrganDepartment);
                log.info("[getFilteredFlowPropertiesForFlowServiceData](sID_BP=" + sID_BP + ",nID_Flow_ServiceData="
                        + nID_Flow_ServiceData + ")");
            } else {
                String sError = "nID_Flow_ServiceData==null and sID_BP==null";
                log.error(sError);
                throw new Exception(sError);
            }
        }
        if (nID_Flow_ServiceData == null) {
            String sError = "nID_Flow_ServiceData==null";
            log.error(sError);
            throw new Exception(sError);
        }

        log.info("[getFilteredFlowPropertiesForFlowServiceData](nID_Flow_ServiceData=" + nID_Flow_ServiceData + ")");
        Flow_ServiceData flowServiceData = flowServiceDataDao.findByIdExpected(nID_Flow_ServiceData);
        List<FlowProperty> res = new LinkedList<FlowProperty>();
        if (flowServiceData != null) {
            if (flowServiceData.getFlowProperties() != null && !flowServiceData.getFlowProperties().isEmpty()) {
                log.info("nID_Flow_ServiceData contains " + flowServiceData.getFlowProperties().size()
                        + " elements. Getting only wuth bExclude =" + bExclude.toString());
                for (FlowProperty flowProperty : flowServiceData.getFlowProperties()) {
                    log.info("flowProperty " + flowProperty.getId() + ":" + flowProperty.getsName() + ":" + flowProperty
                            .getbExclude());
                    if (flowProperty.getbExclude() != null && flowProperty.getbExclude().equals(bExclude)) {
                        res.add(flowProperty);
                    }
                }
                log.info("Found " + res.size() + " flow properties with bExclude=" + bExclude + " . Results:" + res
                        .toString());
            } else {
                log.info("Flow service data contains empty list of FlowProperty");
            }
        } else {
            log.info("Have not found nID_Flow_ServiceData object with ID: " + nID_Flow_ServiceData);
        }
        return res;
    }

    protected FlowProperty fillFlowProperty(String sName, String sRegionTime,
            String saRegionWeekDay, String sDateTimeAt, String sDateTimeTo,
            Integer nLen,
            String sLenType,
            String sData,
            FlowProperty flowProperty) {
        flowProperty.setbExclude(false);
        flowProperty.setsName(sName);
        flowProperty.setsRegionTime(sRegionTime);
        flowProperty.setSaRegionWeekDay(saRegionWeekDay);
        if (nLen != null) {
            flowProperty.setLen(nLen);
        }
        if (sLenType != null) {
            flowProperty.setLenType(sLenType);
        }
        if (sData != null) {
            flowProperty.setsData(sData);
        }
        flowProperty.setsDateTimeAt(sDateTimeAt);
        flowProperty.setsDateTimeTo(sDateTimeTo);
        return flowProperty;
    }

}
