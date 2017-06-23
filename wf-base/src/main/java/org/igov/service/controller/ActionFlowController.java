package org.igov.service.controller;

import io.swagger.annotations.*;
import static java.lang.Math.toIntExact;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.igov.io.web.integration.queue.cherg.Cherg;
import org.igov.model.flow.FlowProperty;
import org.igov.model.flow.FlowSlotTicket;
import org.igov.model.subject.SubjectOrganDepartment;
import org.igov.service.business.flow.FlowService;
import org.igov.service.business.flow.slot.ClearSlotsResult;
import org.igov.service.business.flow.slot.Days;
import org.igov.service.business.flow.slot.FlowSlotVO;
import org.igov.service.business.flow.slot.SaveFlowSlotTicketResponse;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.JSON.JsonDateSerializer;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.ToolQuartz;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.igov.model.flow.FlowSlot;
import org.igov.model.flow.FlowSlotDao;
import org.igov.model.flow.Flow;
import static org.igov.run.schedule.JobBuilderFlowSlots.DAYS_IN_HALF_YEAR;
import static org.igov.run.schedule.JobBuilderFlowSlots.DAYS_IN_MONTH;
import static org.igov.run.schedule.JobBuilderFlowSlots.WORK_DAYS_NEEDED;
import org.igov.service.business.flow.slot.Day;
import org.igov.model.flow.FlowDao;

/**
 * User: goodg_000 Date: 21.06.2015 Time: 14:02
 */
@Controller
@Api(tags = {"ActionFlowController — Действия очередей (слоты потока, расписания и тикеты)"})
@RequestMapping(value = "/action/flow")
public class ActionFlowController {

    private static final Logger LOG = LoggerFactory.getLogger(ActionFlowController.class);
    private static final String SUFFIX_AUTO = "auto";

    @Autowired
    private FlowService oFlowService;

    @Autowired
    Cherg cherg;

    @Autowired
    private FlowDao flowServiceDataDao;

    @Autowired
    private FlowSlotDao flowSlotDao;

    /**
     * Получение слотов по сервису сгруппированных по дням.
     *
     *
     * @param nID_Service номер-ИД услуги (обязательный если нет sID_BP и
     * nID_ServiceData)
     * @param nID_ServiceData ИД сущности ServiceData (обязательный если нет
     * sID_BP и nID_Service)
     * @param sID_BP строка-ИД бизнес-процесса (обязательный если нет
     * nID_ServiceData и nID_Service)
     * @param nID_SubjectOrganDepartment ИД департамента субьекта-органа
     * (опциональный, по умолчанию false)
     * @param bAll булевое значение, если false то из возвращаемого объекта
     * исключаются элементы, содержащие "bHasFree":false "bFree":false
     * (опциональный, по умолчанию false)
     * @param nDays число дней от сегодняшего включительно(или sDateStart, если
     * задан), до nDays в будующее за который нужно вернуть слоты (опциональный,
     * по умолчанию 177 - пол года)
     * @param nFreeDays число дней со слотами будут включаться в результат пока
     * не наберется указанное кол-во свободных дней (опциональный, по умолчанию
     * 60)
     * @param sDateStart строка опциональный параметр, определяющие дату начала
     * в формате "yyyy-MM-dd", с которую выбрать слоты. При наличии этого
     * параметра слоты возвращаются только за указанный период(число дней
     * задается nDays).
     * @param nSlots число, опциональный параметр (по умолчанию 1), группировать
     * слоты по заданному числу штук
     */
    @ApiOperation(value = "Получение слотов по сервису сгруппированных по дням", notes = "##### Пример:\n"
            + "https://alpha.test.igov.org.ua/wf/service/action/flow/getFlowSlots?nID_ServiceData=1 \n"
            + "или\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getSheduleFlowIncludes?sID_BP=kiev_mreo_1 \n"
            + "Ответ: HTTP STATUS 200\n\n"
            + "\n```json\n"
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
            + "\n```\n"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Успех операции: \n"
                + " Калькулируемые поля в ответе:\n"
                + "- флаг \"bFree\" - является ли слот свободным? Слот считается свободным если на него нету тикетов у которых nID_Task_Activiti равен null,"
                + " а у тех у которых nID_Task_Activiti = null - время создания тикета (sDateEdit) не позднее чем текущее время минус 5 минут (предопределенная константа)\n"
                + "- флаг \"bHasFree\" равен true , если данных день содержит хотя бы один свободный слот.\n")})
    @RequestMapping(value = "/getFlowSlots", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getFlowSlots(
            @ApiParam(value = "номер-ИД услуги  (обязательный если нет sID_BP и nID_ServiceData)", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
            @ApiParam(value = "ИД сущности ServiceData (обязательный если нет sID_BP и nID_Service)", required = false) @RequestParam(value = "nID_ServiceData", required = false) Long nID_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса (обязательный если нет nID_ServiceData и nID_Service)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД департамента субьекта-органа", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "булевое значение, если false то из возвращаемого объекта исключаются элементы, содержащие \"bHasFree\":false \"bFree\":false (опциональный, по умолчанию false)", required = false) @RequestParam(value = "bAll", required = false, defaultValue = "false") boolean bAll,
            @ApiParam(value = "число дней со слотами будут включаться в результат пока не наберется указанное кол-во свободных дней (опциональный, по умолчанию 60)", required = false) @RequestParam(value = "nFreeDays", required = false, defaultValue = "60") int nFreeDays,
            @ApiParam(value = "число дней от сегодняшего включительно(или sDateStart, если задан), до nDays в будущее за который нужно вернуть слоты (опциональный, по умолчанию 177 - пол года)", required = false) @RequestParam(value = "nDays", required = false, defaultValue = "177") int nDays,
            @ApiParam(value = "число рабочих дней пропуска от даты начала выборки (если не задана - от текущего), с которой начинать отображать расписание", required = false) @RequestParam(value = "nDiffDays", required = false, defaultValue = "0") int nDiffDays,
            @ApiParam(value = "число смещения даты начала выборки с которой начинать отбирать расписание", required = false) @RequestParam(value = "nDiffDaysForStartDate", required = false, defaultValue = "0") int nDiffDaysForStartDate,
            @ApiParam(value = "строка параметр, определяющие дату начала в формате \"yyyy-MM-dd\", с которую выбрать слоты. При наличии этого параметра слоты возвращаются только за указанный период(число дней задается nDays)", required = false) @RequestParam(value = "sDateStart", required = false) String sDateStart,
            @ApiParam(value = "число, опциональный параметр (по умолчанию 1), группировать слоты по заданному числу штук", required = false) @RequestParam(value = "nSlots", defaultValue = "1", required = false) Integer nSlots
    ) throws Exception {
        //nDiffDays_visitDate1

        LOG.info("getFlowSlots started...");

        /*DateTime oDateStart = DateTime.now().withTimeAtStartOfDay();
        oDateStart = oDateStart.plusDays(nDiffDays);//2
        DateTime oDateEnd = oDateStart.plusDays(nDays);

        if (sDateStart != null) {
            oDateStart = JsonDateSerializer.DATE_FORMATTER.parseDateTime(sDateStart);
            oDateEnd = oDateStart.plusDays(nDays);
        }*/
        DateTime oDateStart = null;

        if (nDiffDaysForStartDate != 0) {
            oDateStart = DateTime.now().withTimeAtStartOfDay().plusDays(nDiffDaysForStartDate);
        } else {
            oDateStart = DateTime.now().withTimeAtStartOfDay().plusDays(1);
        }

        DateTime oDateEnd = oDateStart.plusDays(nDays);

        SimpleDateFormat df_DayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat df_Day = new SimpleDateFormat("yyyy-MM-dd");

        List<String> aMissDays = new ArrayList<>();
        boolean isEmptyResult = false;

        if (nDiffDays != 0) {

            Flow oFlow = null;
            if (nID_Service != null) {
                oFlow = oFlowService.getFlowByLink(nID_Service, nID_SubjectOrganDepartment);
            }

            if (sDateStart != null) {
                oDateStart = JsonDateSerializer.DATE_FORMATTER.parseDateTime(sDateStart);
                oDateStart = oDateStart.plusDays(nDiffDaysForStartDate);
            }

            int nDiffDaysCounter = nDiffDays;
            int stopIteration = 0;
            DateTime currEndDateTime = oDateEnd;

            while (aMissDays.size() <= nDiffDays) {

                if (stopIteration > 5) {
                    isEmptyResult = true;
                    break;
                }

                nDiffDaysCounter = nDiffDaysCounter + (int) Math.ceil(nDiffDaysCounter * 0.3d);

                if (nDiffDaysCounter >= nDays) {
                    nDiffDaysCounter = nDays;
                }

                currEndDateTime = oDateStart.plusDays(nDiffDaysCounter);

                LOG.info("getFlowSlots oDateStart : " + df_DayTime.format(oDateStart.toDate()));
                LOG.info("getFlowSlots oDateEnd : " + df_DayTime.format(currEndDateTime.toDate()));

                List<FlowSlot> aFlowSlot = new ArrayList<>();

                if (oFlow != null) {
                    aFlowSlot = flowSlotDao.findFlowSlotsByFlow(oFlow.getId(), oDateStart, currEndDateTime);
                } else if (nID_ServiceData != null) {
                    aFlowSlot = flowSlotDao.findFlowSlotsByServiceData(nID_ServiceData, nID_SubjectOrganDepartment, oDateStart, oDateEnd);
                } else if (sID_BP != null) {
                    aFlowSlot = flowSlotDao.findFlowSlotsByBP(sID_BP, nID_SubjectOrganDepartment, oDateStart, oDateEnd);
                } else {
                    throw new IllegalArgumentException("nID_Service, nID_ServiceData, sID_BP are null!");
                }

                LOG.info("aFlowSlot size is: " + aFlowSlot.size());

                DateTime oMissStartDate = null;
                boolean isDoubleDateRange = true;

                for (int i = 0; i < aFlowSlot.size(); i++) {
                    LOG.info("flowslot elem in getFlowSlots " + df_DayTime.format(aFlowSlot.get(i).getsDate().toDate()));

                    boolean addDay = true;

                    for (String sMissDay : aMissDays) {
                        if (sMissDay.equals(df_Day.format(aFlowSlot.get(i).getsDate().toDate()))) {
                            addDay = false;
                            break;
                        }
                    }

                    if (addDay) {
                        LOG.info("flowslot elem in getFlowSlots " + df_Day.format(aFlowSlot.get(i).getsDate().toDate()));
                        aMissDays.add(df_Day.format(aFlowSlot.get(i).getsDate().toDate()));
                        isDoubleDateRange = false;
                    }

                    if (aMissDays.size() > nDiffDays) {
                        oMissStartDate = aFlowSlot.get(i).getsDate();
                        break;
                    }
                }

                if (isDoubleDateRange) {
                    nDiffDaysCounter = nDiffDaysCounter * 2;
                }

                if (oMissStartDate != null) {
                    oDateStart = oMissStartDate;
                }
                stopIteration++;
                LOG.info("new oDateStart: " + df_DayTime.format(oDateStart.toDate()));
            }
        }

        Days res = new Days();

        if (nDiffDays == 0 || !isEmptyResult) {
            res = oFlowService.getFlowSlots(nID_Service, nID_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                    oDateStart, oDateEnd, bAll, nFreeDays, nSlots);
        }

        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * Получение массива объектов SubjectOrganDepartment по ID бизнес процесса
     *
     * @param sID_BP строка имя Activiti BP
     */
    @ApiOperation(value = "Получение массива объектов SubjectOrganDepartment по ID бизнес процесса", notes = "##### Примеры:\n"
            + " https://test.region.igov.org.ua/wf/service/action/flow/getFlowSlots_Department?sID_BP=dnepr_dms-89\n"
            + "Ответ:\n"
            + "\n```json\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "возвращает массив объектов SubjectOrganDepartment для указанного Activiti BP")})
    @RequestMapping(value = "/getFlowSlots_Department", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getFlowSlotDepartment(@ApiParam(value = "строка имя Activiti BP", required = true) @RequestParam(value = "sID_BP") String sID_BP
    ) throws Exception {

        SubjectOrganDepartment[] result = oFlowService.getSubjectOrganDepartments(sID_BP);

        return JsonRestUtils.toJsonResponse(result);
    }

    /**
     * Создание или обновление тикета в указанном слоте.
     *
     * @param nID_FlowSlot ИД сущности FlowSlot/ обязательный
     * @param nID_Subject ИД сущности Subject — субьект пользователь услуги,
     * который подписывается на слот/ обязательный
     * @param nID_Task_Activiti ИД таски активити процесса предоставления услуги
     * (не обязательный — вначале он null, а потом засчитывается после
     * подтверждения тикета и создания процесса)/ опциональный
     * @param nSlots Кол-во слотов, которые нужно зарезервировать подряд включая
     * текущий (по умолчанию 1)
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "Создание или обновление тикета в указанном слоте.", notes = "##### Пример:\n"
            + " http://test.igov.org.ua/wf/service/action/flow/setFlowSlot_ServiceData\n\n"
            + "- nID_FlowSlot=1\n"
            + "- nID_Subject=2\n\n"
            + "Ответ: HTTP STATUS 200\n\n"
            + "\n```json\n"
            + "{ \"nID_Ticket\": 1000 }\n"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Поля в ответе:\n "
                + "-поле \"nID_Ticket\" - ID созданной/измененной сущности FlowSlotTicket.\n")})
    @RequestMapping(value = {"/setFlowSlot_ServiceData", "/setFlowSlot"}, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity saveFlowSlotTicket(@ApiParam(value = "ИД сущности FlowSlot", required = true) @RequestParam(value = "nID_FlowSlot") Long nID_FlowSlot,
            @ApiParam(value = "ИД сущности Subject — субьект пользователь услуги, который подписывается на слот", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "ИД таски активити процесса предоставления услуги (не обязательный — вначале он null, а потом засчитывается после подтверждения тикета и создания процесса)", required = false) @RequestParam(value = "nID_Task_Activiti", required = false) Long nID_Task_Activiti,
            @ApiParam(value = "Кол-во слотов идущих подряд включая текущий (без зазоров времени между соседними), которые надо зарезервировать", required = true) @RequestParam(value = "nSlots", defaultValue = "1", required = false) Integer nSlots) throws Exception {

        FlowSlotTicket oFlowSlotTicket = oFlowService.saveFlowSlotTicket(nID_FlowSlot, nID_Subject, nID_Task_Activiti,
                nSlots);

        return JsonRestUtils.toJsonResponse(new SaveFlowSlotTicketResponse(oFlowSlotTicket.getId(), nSlots));
    }

    /**
     * Генерация слотов на заданный интервал для заданного потока.
     *
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @param sDateStart строка дата "начиная с такого-то момента времени", в
     * формате "2015-06-28 12:12:56.001" (опциональный)
     * @param sDateStop строка дата "заканчивая к такому-то моменту времени", в
     * формате "2015-07-28 12:12:56.001" (опциональный)
     */
    @ApiOperation(value = "Генерация слотов на заданный интервал для заданного потока", notes = "##### Пример:\n"
            + " http://test.igov.org.ua/wf/service/action/flow/buildFlowSlots\n"
            + "- nID_Flow_ServiceData=1\n"
            + "- sDateStart=2015-06-01 00:00:00.000\n"
            + "- sDateStop=2015-06-07 00:00:00.000\n"
            + "Ответ: HTTP STATUS 200.\n"
            + "Ниже приведена часть json ответа:\n"
            + "\n```json\n"
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
            + "]\n"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "json перечисление всех сгенерированных слотов\n"
                + "Если на указанные даты слоты уже сгенерены то они не будут генерится повторно, и в ответ включаться не будут.")})
    @RequestMapping(value = "/buildFlowSlots", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity buildFlowSlots(
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "строка дата, начиная с такого-то момента времени, в формате \"2015-06-28 12:12:56.001\"", required = false) @RequestParam(value = "sDateStart", required = false) String sDateStart,
            @ApiParam(value = "строка дата, заканчивая к такому-то моменту времени, в формате \"2015-07-28 12:12:56.001\"", required = false) @RequestParam(value = "sDateStop", required = false) String sDateStop) throws ParseException {

        DateTime startDate = oFlowService.parseJsonDateTimeSerializer(sDateStart);
        DateTime stopDate = oFlowService.parseJsonDateTimeSerializer(sDateStop);

        try {
            nID_Flow_ServiceData = oFlowService.determineFlow_ID(
                    nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment);
        } catch (RecordNotFoundException e) {
            LOG.error(e.getMessage());
            LOG.debug("FAIL:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<FlowSlotVO> res = oFlowService.buildFlowSlots(nID_Flow_ServiceData, startDate, stopDate);

        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * Удаление слотов на заданный интервал для заданного потока.
     *
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @param sDateStart строка дата "начиная с такого-то момента времени", в
     * формате "2015-06-28 12:12:56.001" (обязательный)
     * @param sDateStop строка дата "заканчивая к такому-то моменту времени", в
     * формате "2015-07-28 12:12:56.001" (обязательный)
     * @param bWithTickets булевое значение удалять ли слоты с тикетами,
     * отвязывая тикеты от слотов? (опциональный, по умолчанию false). Если
     * bWithTickets=true то эти слоты тоже удаляются и будут перечислены в
     * aDeletedSlot, иначе - не удаляются.
     */
    @ApiOperation(value = "Удаление слотов на заданный интервал для заданного потока", notes = "##### Пример:\n"
            + "\n```\n"
            + "http://test.igov.org.ua/wf/service/action/flow/clearFlowSlots?nID_Flow_ServiceData=1&sDateStart=2015-06-01 00:00:00.000&sDateStop=2015-06-07 00:00:00.000\n\n"
            + "\n```\n"
            + "Ответ: HTTP STATUS 200 \n"
            + "Ниже приведена часть json ответа:\n\n"
            + "\n```json\n"
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
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "json Обьект содержащий 2 списка:\n"
                + "- aDeletedSlot - удаленные слоты\n"
                + "- aSlotWithTickets - слоты с тикетами.\n "
                + " Если bWithTickets=true то эти слоты тоже удаляются и будут перечислены в aDeletedSlot,\n"
                + " иначе - не удаляются.\n")})
    @RequestMapping(value = "/clearFlowSlots", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity clearFlowSlots(
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "строка дата, начиная с такого-то момента времени, в формате \"2015-06-28 12:12:56.001\"", required = true) @RequestParam(value = "sDateStart") String sDateStart,
            @ApiParam(value = "строка дата, заканчивая к такому-то моменту времени, в формате \"2015-07-28 12:12:56.001\"", required = true) @RequestParam(value = "sDateStop") String sDateStop,
            @ApiParam(value = "булевое значение удалять ли слоты с тикетами, отвязывая тикеты от слотов? (опциональный, по умолчанию false). \n"
                    + " Если bWithTickets=true то эти слоты тоже удаляются и будут перечислены в aDeletedSlot, \n"
                    + " иначе - не удаляются.", required = false) @RequestParam(value = "bWithTickets", required = false, defaultValue = "false") boolean bWithTickets) {

        DateTime startDate = oFlowService.parseJsonDateTimeSerializer(sDateStart);
        DateTime stopDate = oFlowService.parseJsonDateTimeSerializer(sDateStop);

        try {
            nID_Flow_ServiceData = oFlowService.determineFlow_ID(
                    nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment);
        } catch (RecordNotFoundException e) {
            LOG.error("Error: {}", e.getMessage());
            LOG.debug("FAIL:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ClearSlotsResult res = oFlowService.clearFlowSlots(nID_Flow_ServiceData, startDate, stopDate, bWithTickets);
        return JsonRestUtils.toJsonResponse(res);
    }

    /**
     * Получение расписаний включений
     *
     * @param nID_Flow_ServiceData - ИД потока
     * @param sID_BP строка-ИД БизнесПроцесса
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @return List of schedule with bExclude=false
     */
    @ApiOperation(value = "Получение расписаний включений", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getSheduleFlowIncludes?nID_Flow_ServiceData=1\n\n"
            + "Пример результата\n\n"
            + "\n```json\n"
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
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
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
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "      \"nID\": 1,\n"
            + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "    }\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getSheduleFlowIncludes", method = RequestMethod.GET)
    public @ResponseBody
    List<FlowProperty> getSheduleFlowIncludes(
            @ApiParam(value = "ИД потока", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД БизнесПроцесса", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        return oFlowService.getFilteredFlowPropertiesForFlow(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                Boolean.FALSE);
    }

    /**
     * Получение расписаний исключений
     *
     * @param nID_Flow_ServiceData - ИД потока
     * @param sID_BP строка-ИД БизнесПроцесса
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @return List of schedule with bExclude=false
     *
     */
    @ApiOperation(value = "Получение расписаний исключений", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getSheduleFlowExcludes?nID_Flow_ServiceData=1\n\n"
            + "Пример результата\n"
            + "\n```json\n"
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
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
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
            + "      \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "      \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "      \"nID\": 1,\n"
            + "      \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "    }\n"
            + "  }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getSheduleFlowExcludes", method = RequestMethod.GET)
    public @ResponseBody
    List<FlowProperty> getSheduleFlowExcludes(
            @ApiParam(value = "ИД потока", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД БизнесПроцесса", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        return oFlowService.getFilteredFlowPropertiesForFlow(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment,
                Boolean.TRUE);
    }

    /**
     * Добавление/изменение расписания включений
     *
     * @param nID - ИД-номер, если задан - редактирование
     * @param nID_Flow_ServiceData - номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment - ИД номер-ИН департамента
     * @param sName - Строка-название (\"Вечерний прием\")
     * @param sRegionTime - Строка период времени ("14:16-16-30")
     * @param nLen - Число, определяющее длительность слота
     * @param sLenType - Строка определяющее тип длительности слота
     * @param sData - Строка с данными(выражением), описывающими формулу
     * расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})
     * @param saRegionWeekDay - Массив дней недели (\"su,mo,tu\")
     * @param sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss
     * (\"2015-07-31 19:00:00\")
     * @param sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss
     * (\"2015-07-31 23:00:00\")
     * @return ID of new FlowProperty
     */
    @ApiOperation(value = "Добавление/изменение расписания включений", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/setSheduleFlowInclude?nID_Flow_ServiceData=1&sName=Test&sRegionTime=%2210:30-11:30%22&sDateTimeAt=%222010-08-01%2010:10:30%22&sDateTimeTo=%222010-08-01%2018:10:00%22&saRegionWeekDay=%22mo,tu%22\n"
            + "Пример результата\n"
            + "\n```json\n"
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
            + "    \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "  }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/setSheduleFlowInclude", method = RequestMethod.GET)
    public @ResponseBody
    FlowProperty setSheduleFlowInclude(
            @ApiParam(value = "ИД-номер, если задан - редактирование", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "Строка-название (\"Вечерний прием\")", required = true) @RequestParam(value = "sName") String sName,
            @ApiParam(value = "Строка период времени (\"14:16-16-30\")", required = true) @RequestParam(value = "sRegionTime") String sRegionTime,
            @ApiParam(value = "Число, определяющее длительность слота", required = false) @RequestParam(value = "nLen", required = false) Integer nLen,
            @ApiParam(value = "Строка определяющее тип длительности слота", required = false) @RequestParam(value = "sLenType", required = false) String sLenType,
            @ApiParam(value = "Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "Массив дней недели (\"su,mo,tu\")", required = true) @RequestParam(value = "saRegionWeekDay") String saRegionWeekDay,
            @ApiParam(value = "Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")", required = true) @RequestParam(value = "sDateTimeAt") String sDateTimeAt,
            @ApiParam(value = "Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")", required = true) @RequestParam(value = "sDateTimeTo") String sDateTimeTo) throws Exception {

        if (sRegionTime != null && saRegionWeekDay != null && nLen != null) {
            sData = ToolQuartz.getQuartzFormulaByParameters(sRegionTime, saRegionWeekDay, nLen);
        }

        return oFlowService.setSheduleFlow(nID, nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment, sName,
                sRegionTime, nLen, sLenType, sData, saRegionWeekDay, sDateTimeAt, sDateTimeTo, Boolean.FALSE);
    }

    /**
     * Добавление/изменение расписания исключения
     *
     * @param nID - ИД-номер //опциональный ,если задан - редактирование
     * @param nID_Flow_ServiceData - номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP - строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment - ИД номер-ИН департамента
     * @param sName - Строка-название (\"Вечерний прием\")
     * @param sRegionTime - Строка период времени (\"14:16-16-30\")
     * @param nLen - Число, определяющее длительность слота
     * @param sLenType - Строка определяющее тип длительности слота
     * @param sData - Строка с данными(выражением), описывающими формулу
     * расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})
     * @param saRegionWeekDay - Массив дней недели (\"su,mo,tu\")
     * @param sDateTimeAt - Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss
     * (\"2015-07-31 19:00:00\")
     * @param sDateTimeTo - Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss
     * (\"2015-07-31 23:00:00\")
     * @return ID of new FlowProperty
     */
    @ApiOperation(value = "Добавление/изменение расписания исключения", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/setSheduleFlowExclude?nID_Flow_ServiceData=1&sName=Test&sRegionTime=%2210:30-11:30%22&sDateTimeAt=%222010-08-01%2010:10:30%22&sDateTimeTo=%222010-08-01%2018:10:00%22&saRegionWeekDay=%22mo,tu%22\n"
            + "\n```json\n"
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
            + "    \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "  }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/setSheduleFlowExclude", method = RequestMethod.GET)
    public @ResponseBody
    FlowProperty setSheduleFlowExclude(
            @ApiParam(value = "ИД-номер //опциональный ,если задан - редактирование", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment,
            @ApiParam(value = "Строка-название (\"Вечерний прием\")", required = true) @RequestParam(value = "sName") String sName,
            @ApiParam(value = "Строка период времени (\"14:16-16-30\")", required = true) @RequestParam(value = "sRegionTime") String sRegionTime,
            @ApiParam(value = "Число, определяющее длительность слота", required = false) @RequestParam(value = "nLen", required = false) Integer nLen,
            @ApiParam(value = "Строка определяющее тип длительности слота", required = false) @RequestParam(value = "sLenType", required = false) String sLenType,
            @ApiParam(value = "Строка с данными(выражением), описывающими формулу расписания (например: {\"0 0/30 9-12 ? * TUE-FRI\":\"PT30M\"})", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "Массив дней недели (\"su,mo,tu\")", required = true) @RequestParam(value = "saRegionWeekDay") String saRegionWeekDay,
            @ApiParam(value = "Строка-дата начала(на) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 19:00:00\")", required = true) @RequestParam(value = "sDateTimeAt") String sDateTimeAt,
            @ApiParam(value = "Строка-дата конца(к) в формате YYYY-MM-DD hh:mm:ss (\"2015-07-31 23:00:00\")", required = true) @RequestParam(value = "sDateTimeTo") String sDateTimeTo) throws Exception {

        return oFlowService.setSheduleFlow(nID, nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment, sName,
                sRegionTime, nLen, sLenType, sData, saRegionWeekDay, sDateTimeAt, sDateTimeTo, Boolean.TRUE);
    }

    /**
     * Удаление расписания включений
     *
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID ИД-номер
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     */
    @ApiOperation(value = "Удаление расписания включений", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/removeSheduleFlowInclude?nID_Flow_ServiceData=1&nID=20367\n\n"
            + "Пример результата\n"
            + "\n```json\n"
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
            + "    \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "  }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/removeSheduleFlowInclude", method = RequestMethod.GET)
    public @ResponseBody
    List<FlowProperty> removeSheduleFlowInclude(
            @ApiParam(value = "номер-ИД записи", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        try {
            nID_Flow_ServiceData = oFlowService
                    .determineFlow_ID(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment);
        } catch (RecordNotFoundException e) {
            LOG.error(e.getMessage());
            LOG.debug("FAIL:", e);
            throw new Exception(e.getMessage());
        }

        if (nID_Flow_ServiceData != null && nID != null) {
            LOG.info("nID_Flow_ServiceData is not null. Removing flow property with bExclude=false and (ID={})", nID);

            return oFlowService.removeSheduleFlow(nID, nID_Flow_ServiceData, Boolean.FALSE);
        } else {
            LOG.info("nID or nID_Flow_ServiceData are empty. Skipping logic of the method removeSheduleFlowExclude");
        }
        return new LinkedList<FlowProperty>();
    }

    /**
     * Удаление расписания исключений
     *
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID ИД-номер
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     */
    @ApiOperation(value = "Удаление расписания исключений", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/removeSheduleFlowExclude?nID_Flow_ServiceData=1&nID=20367\n\n"
            + "Пример результата\n\n"
            + "\n```json\n"
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
            + "    \"sPath\": \"org.igov.service.business.flow.handler.DefaultFlowSlotScheduler\",\n"
            + "    \"sBeanName\": \"defaultFlowSlotScheduler\",\n"
            + "    \"nID\": 1,\n"
            + "    \"sName\": \"DefaultFlowSlotScheduler\"\n"
            + "  }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/removeSheduleFlowExclude", method = RequestMethod.GET)
    public @ResponseBody
    List<FlowProperty> removeSheduleFlowExclude(
            @ApiParam(value = "ИД-номер", required = true) @RequestParam(value = "nID") Long nID,
            @ApiParam(value = "номер-ИД потока (обязательный если нет sID_BP)", required = false) @RequestParam(value = "nID_Flow_ServiceData", required = false) Long nID_Flow_ServiceData,
            @ApiParam(value = "строка-ИД бизнес-процесса потока (обязательный если нет nID_Flow_ServiceData)", required = false) @RequestParam(value = "sID_BP", required = false) String sID_BP,
            @ApiParam(value = "ИД номер-ИН департамента", required = false) @RequestParam(value = "nID_SubjectOrganDepartment", required = false) Long nID_SubjectOrganDepartment
    ) throws Exception {

        try {
            nID_Flow_ServiceData = oFlowService
                    .determineFlow_ID(nID_Flow_ServiceData, sID_BP, nID_SubjectOrganDepartment);
        } catch (RecordNotFoundException e) {
            LOG.error(e.getMessage());
            LOG.debug("FAIL:", e);
            throw new Exception(e.getMessage());
        }

        if (nID_Flow_ServiceData != null && nID != null) {
            LOG.info("nID_Flow_ServiceData is not null. Removing flow property with bExclude=true and (ID={})", nID);

            return oFlowService.removeSheduleFlow(nID, nID_Flow_ServiceData, Boolean.TRUE);
        } else {
            LOG.info("nID or nID_Flow_ServiceData are empty. Skipping logic of the method removeSheduleFlowExclude");
        }
        return new LinkedList<FlowProperty>();
    }

    /**
     * Получение активных тикетов
     *
     * @param sLogin строка имя пользоватеял для которого необходимо вернуть
     * тикеты
     * @param bEmployeeUnassigned булевое значение опциональный параметр (false
     * по умолчанию). Если true - возвращать тикеты не заассайненые на
     * пользователей
     * @param sDate строка опциональный параметр в формате yyyy-MM-dd. Дата за
     * которую выбирать тикеты. При выборке проверяется startDate тикета (без
     * учета времени. только дата). Если день такой же как и у указанное даты -
     * такой тикет добавляется в результат.
     */
    @ApiOperation(value = "Получение активных тикетов", notes = "##### Примеры:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getFlowSlotTickets?sLogin=kermit\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getFlowSlotTickets?sLogin=kermit&bEmployeeUnassigned=true\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/getFlowSlotTickets?sLogin=kermit&bEmployeeUnassigned=true&sDate=2015-07-20\n"
            + "\n```json\n"
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
            + "\n```\n")
    @RequestMapping(value = "/getFlowSlotTickets", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getFlowSlotTickets(
            @ApiParam(value = "строка имя пользователя для которого необходимо вернуть тикеты", required = true) @RequestParam(value = "sLogin") String sLogin,
            @ApiParam(value = "булевое значение опциональный параметр (false по умолчанию). Если true - возвращать тикеты не заассайненые на пользователей", required = false) @RequestParam(value = "bEmployeeUnassigned", required = false, defaultValue = "false") Boolean bEmployeeUnassigned,
            @ApiParam(value = "строка опциональный параметр в формате yyyy-MM-dd. Дата за которую выбирать тикеты. При выборке проверяется startDate тикета (без учета времени. только дата). Если день такой же как и у указанное даты - такой тикет добавляется в результат.", required = false) @RequestParam(value = "sDate", required = false) String sDate
    ) throws Exception {
        
        List<Map<String, String>> res = oFlowService.getFlowSlotTickets(sLogin, bEmployeeUnassigned, sDate);

        String jsonRes = JSONValue.toJSONString(res);
        LOG.info("Result:{}", jsonRes);
        return jsonRes;
    }

    @ApiOperation(value = "Получение списка тайм слотов", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/DMS/getSlots\n\n"
            + "nID_Service_Private - 428"
            + "nDays - 2"
            + "Пример результата\n\n"
            + "\n```json\n"
            + "{\n"
            + "  \"2016-08-15\": [\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"09:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"09:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"09:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"09:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"10:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"10:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"10:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"10:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"11:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"11:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"11:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"11:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"12:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"12:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"12:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"12:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"14:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"14:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"14:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"14:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"15:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"15:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"15:35:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"15:50:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"16:05:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"16:20:00\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"date\": \"2016-08-15\",\n"
            + "      \"t_length\": 15,\n"
            + "      \"time\": \"16:35:00\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"2016-08-14\": []\n"
            + "}"
            + "\n```\n")
    @RequestMapping(value = "/DMS/getSlots", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getSlots(
            @ApiParam(value = "уникальный строковой-ИД сервиса", required = true) @RequestParam(value = "nID_Service_Private") Integer nID_Service_Private,
            @ApiParam(value = "опциональный параметр, укзывающий количество дней для которыйх нужно найти слоты", required = false, defaultValue = "7") @RequestParam(value = "nDays", required = false, defaultValue = "7") int nDays
    ) throws Exception {
        JSONObject oJSONObjectReturn = new JSONObject();
        DateTimeFormatter oDateTimeFormatter = DateTimeFormat.forPattern("y-MM-dd");
        DateTimeFormatter oDateTimeFormatterReady = DateTimeFormat.forPattern("YYYY-MM-dd");

        JSONArray oaSlot = null;

        JSONArray oaJSONArray = cherg.getSlotFreeDaysArray(nID_Service_Private);
        for (Object o : oaJSONArray) {
            //JSONObject oJSONObject = (JSONObject) o;
            String sDate = o.toString();
            DateTime oDateReady = oDateTimeFormatterReady.parseDateTime(sDate);
            oaSlot = cherg.getFreeTime(oDateReady, nID_Service_Private);
            oJSONObjectReturn.put(oDateTimeFormatter.print(oDateReady), oaSlot);
        }

        /*DateTime oDate = DateTime.now();
		if (nDays > 7) {
			nDays = 7;
		}
		for (int i = 0; i < nDays; i++) {
			oaSlot = cherg.getFreeTime(oDate, nID_Service_Private);
			oJSONObjectReturn.put(oDateTimeFormatter.print(oDate), oaSlot);
			oDate = oDate.plusDays(1);
		}*/
        return oJSONObjectReturn.toString();
    }

    @ApiOperation(value = "Получение календаря рабочих дней оргиназиции по заданной услуге на ближайшие 15 дней, включая текущий", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/action/flow/DMS/getSlotFreeDays?nID_Service_Private=428\n\n")
    @RequestMapping(value = "/DMS/getSlotFreeDays", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getSlotFreeDays(@ApiParam(value = "ID сервиса", required = true)
            @RequestParam(value = "nID_Service_Private") Integer nID_Service_Private
    ) throws Exception {
        return cherg.getSlotFreeDays(nID_Service_Private);
    }

    @ApiOperation(value = "Резервирование тайм слота.", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/DMS/setSlotHold\n\n"
            + "nID_Service_Private - 428"
            + "sDateTime - 2016-08-17 14:05:00"
            + "sSubjectFamily - Sidorov"
            + "sSubjectName - Vladimir"
            + "sSubjectSurname - Petrovich"
            + "sSubjectPassport - 0101"
            + "sSubjectPhone - +380666800000"
            + "Пример результата\n\n"
            + "\n```json\n"
            + "{\n"
            + "  \"reserved_to\": \"2016-08-14 22:28:15\",\n"
            + "  \"reserve_id\": \"18573\",\n"
            + "  \"interval\": \"15\"\n"
            + "}"
            + "\n```\n")
    @RequestMapping(value = "/DMS/setSlotHold", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String setSlotHold(
            @ApiParam(value = "ИД услуги", required = true) @RequestParam(value = "nID_Service_Private") String sID_Service_Private,
            @ApiParam(value = "Дата и время в формате \"YYYY-MM-DD hh:ii:ss\"", required = true) @RequestParam(value = "sDateTime") String sDateTime,
            @ApiParam(value = "Фамилия клиента", required = true) @RequestParam(value = "sSubjectFamily") String sSubjectFamily,
            @ApiParam(value = "Имя клиента", required = true) @RequestParam(value = "sSubjectName") String sSubjectName,
            @ApiParam(value = "Отчество клиента", required = true) @RequestParam(value = "sSubjectSurname") String sSubjectSurname,
            @ApiParam(value = "Последние 4 цифры паспорта", required = true) @RequestParam(value = "sSubjectPassport") String sSubjectPassport,
            @ApiParam(value = "Номер телефона клиента", required = true) @RequestParam(value = "sSubjectPhone") String sSubjectPhone
    ) throws Exception {
        JSONObject result;

        //this date parsing and formatting fixes problem where we need to zero pad day of month or hour
        //basically it changes "2016-10-6 9:30:0" into "2016-10-06 09:30:00"
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("y-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(sDateTime, DateTimeFormat.forPattern("y-MM-dd HH:mm:ss"));
        sDateTime = dateTimeFormatter.print(dateTime);

        result = cherg.setReserve(sID_Service_Private,
                sDateTime,
                sSubjectPhone,
                sSubjectPassport,
                sSubjectFamily,
                sSubjectName,
                sSubjectSurname);

        return result.toString();
    }

    @ApiOperation(value = "Подтверждение резервации тайм слота.", notes = "##### Пример:\n"
            + "https://test.region.igov.org.ua/wf/service/action/flow/DMS/setSlot\n\n"
            + "nID_SlotHold - 18573"
            + "Пример результата\n\n"
            + "\n```json\n"
            + "{\n"
            + "  \"patronymic\": \"Petrovich\",\n"
            + "  \"date_time\": \"2016-08-17 14:05:00\",\n"
            + "  \"ticket_code\": \"418303677\",\n"
            + "  \"service_id\": \"428\",\n"
            + "  \"name\": \"Vladimir\",\n"
            + "  \"ticket_number\": \"501\",\n"
            + "  \"lastname\": \"Sidorov\"\n"
            + "}"
            + "\n```\n")
    @RequestMapping(value = "/DMS/setSlot", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String setSlot(
            @ApiParam(value = "ИД слота резервации", required = true) @RequestParam(value = "nID_SlotHold") String nID_SlotHold
    ) throws Exception {
        JSONObject result;

        result = cherg.confirmReserve(nID_SlotHold);

        return result.toString();
    }

    /**
     * Генерация слотов на заданный интервал для заданного потока.
     *
     * @param nID_Flow_ServiceData номер-ИД потока (обязательный если нет
     * sID_BP)
     * @param sID_BP строка-ИД бизнес-процесса потока (обязательный если нет
     * nID_Flow_ServiceData)
     * @param nID_SubjectOrganDepartment ИД номер-ИН департамента
     * @param sDateStart строка дата "начиная с такого-то момента времени", в
     * формате "2015-06-28 12:12:56.001" (опциональный)
     * @param sDateStop строка дата "заканчивая к такому-то моменту времени", в
     * формате "2015-07-28 12:12:56.001" (опциональный)
     * @param nCountAutoGenerate кол-во дней (опциональный)
     */
    @ApiOperation(value = "Генерация слотов на заданный интервал для заданного потока", notes = "##### Пример:\n"
            + " alpha.test.region.igov.org.ua/wf/service/action/flow/buildFlowSlot\n"
            + "- nID_Flow_ServiceData=1\n"
            + "- sDateStart=2015-06-01 00:00:00.000\n"
            + "- sDateStop=2015-06-07 00:00:00.000\n"
            + "Ответ: HTTP STATUS 200.\n"
            + "Ниже приведена часть json ответа:\n"
            + "\n```json\n"
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
            + "]\n"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "json перечисление всех сгенерированных слотов\n"
                + "Если на указанные даты слоты уже сгенерены то они не будут генерится повторно, и в ответ включаться не будут.")})
    @RequestMapping(value = "/buildFlowSlot", method = RequestMethod.GET)
    public @ResponseBody
    String buildFlowSlot() {
        LOG.info("buildFlowSlot start");
		try {
			oFlowService.buildFlowSlots();
		} catch (Exception e) {
			LOG.error("ERROR buildFlowSlot!!! " + e.getMessage());
		}
        LOG.info("buildFlowSlot stop!!!");
        return "ok!";
    }

}
