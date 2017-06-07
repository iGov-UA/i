package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.*;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.subject.Server;
import org.igov.model.subject.ServerDao;
import org.igov.model.subject.message.*;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.business.action.task.bp.BpService;
import org.igov.service.business.subject.SubjectMessageService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.FileServiceIOException;
import org.igov.service.exception.RecordNotFoundException;
import org.igov.util.JSON.JsonRestUtils;
import org.igov.util.MethodsCallRunnerUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.*;
import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;
import org.igov.io.mail.NotificationPatterns;
import org.igov.model.action.event.HistoryEventDao;
import org.igov.service.business.promin.ProminSession_Singleton;
import static org.igov.util.Tool.sO;

@Controller
@Api(tags = { "SubjectMessageController -- Сообщения субьектов" })
@RequestMapping(value = "/subject/message")
public class SubjectMessageController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectMessageController.class);
    
    @Autowired
    MethodsCallRunnerUtil methodCallRunner;
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    @Autowired
    private SubjectMessagesDao subjectMessagesDao;
    @Autowired
    private GeneralConfig generalConfig;
    @Autowired
    private BpService bpService;
    @Autowired
    private AccessDataService accessDataDao;
    @Autowired
    private IBytesDataStorage durableBytesDataStorage;
    @Autowired
    private ActionEventService actionEventService;
    @Autowired
    private SubjectMessageService oSubjectMessageService;
    @Autowired
    private IBytesDataInmemoryStorage oBytesDataInmemoryStorage;
    @Autowired
    private SubjectMessageFeedbackDao subjectMessageFeedbackDao;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    private HistoryEventDao historyEventDao;
    @Autowired
    private ProminSession_Singleton prominSession_Singleton;
    @Autowired
    HttpRequester httpRequester;

    @Autowired
    private NotificationPatterns oNotificationPatterns;

    @Autowired
    private TaskService taskService;

	@ApiOperation(value = "Получение сообщения", notes = ""
            + "Примеры: https://test.igov.org.ua/wf/service/subject/message/getMessage?nID=76\n"
            + "\n```json\n"
            + "Ответ:\n"
            + "{\n"
            + "    \"nID\":76\n"
            + "    ,\"sHead\":\"Закликаю владу перевести цю послугу в електронну форму!\"\n"
            + "    ,\"sBody\":\"Дніпропетровськ - Видача витягу з технічної документації про нормативну грошову оцінку земельної ділянки\"\n"
            + "    ,\"sDate\":\"2015-06-03 22:09:16.536\"\n"
            + "    ,\"nID_Subject\":0\n"
            + "    ,\"sMail\":\"bvv4ik@gmail.com\"\n"
            + "    ,\"sContacts\":\"\"\n"
            + "    ,\"sData\":\"\"\n"
            + "    ,\"oSubjectMessageType\": {\n"
            + "        \"sDescription\": \"Просьба добавить услугу\",\n"
            + "        \"nID\": 0,\n"
            + "        \"sName\": \"ServiceNeed\"\n"
            + "    }\n"
            + "}\n"
            + "\n```\n")
    @RequestMapping(value = "/getMessage", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getMessage(
            @ApiParam(value = "", required = true) @RequestParam(value = "nID") Long nID) {
        LOG.info("/getMessage: start");    
        SubjectMessage message = subjectMessagesDao.getMessage(nID);
                
        return JsonRestUtils.toJsonResponse(message);
    }
    
    
    
    
    @RequestMapping(value = "/getSID_Auth_PB_SMS", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    String getSID_Auth_PB_SMS(
            @ApiParam(value = "", required = false) @RequestParam(value = "nID") Long nID) {
        LOG.info("getSID_Auth_PB_SMS start...");
        String sSid = prominSession_Singleton.getSID_Auth_PB_SMS();
        LOG.info("getSID_Auth_PB_SMS finish sSid: " + sSid);
        return (sSid != null || "".equals(sSid)) ? sSid : "None";
    }
    

    @ApiOperation(value = "Сохранение сообщение ", notes = ""
            + "При заданных параметрах sID_Order или nID_Protected с/без nID_Server и sID_Rate - обновляется поле nRate в записи сущности HistoryEvent_Service, которая находится по sID_Order или nID_Protected с/без nID_Server (подробнее тут, при этом приходящее значение из параметра sID_Rate должно содержать число от 1 до 5. т.е. возможные ошибки:\n\n"
            + "- nID_Protected некорректное -- ошибка 403. CRC Error, пишется в лог (т.е. сообщение все равно сохраняется)\n"
            + "- sID_Rate некорректное (не число или не в промежутке от 1 до 5) -- ошибка 403. Incorrect sID_Rate, пишется в лог\n"
            + "- запись заявки (по nID_Protected без последней цифры) не найдена -- ошибка 403. Record not found, пишется в лог проверить запись HistoryEvent_Service можно через сервис"
            + " \\sevices\\getHistoryEvent_Service?nID_Protected=xxx (link: 17. Работа с обьектами событий по услугам)\n\n"
            + "Примеры:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/setMessage?sHead=name&sBody=body&sMail=a@a.a\n"
            + "Ответ: Status 200 если Ok\n")
    @RequestMapping(value = "/setMessage", method = {RequestMethod.POST, RequestMethod.GET})
    public
    @ResponseBody
    ResponseEntity setMessage(
            @ApiParam(value = "Строка-заглавие сообщения", required = true) @RequestParam(value = "sHead") String sHead,
            @ApiParam(value = "Строка-тело сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "ИД-номер субьекта (автора) (добавляется в запрос автоматически после аутентификации пользователя)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Строка электронного адреса автора", required = false) @RequestParam(value = "sMail", required = false) String sMail,
            @ApiParam(value = "Строка контактов автора", required = false) @RequestParam(value = "sContacts", required = false) String sContacts,
            @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "ИД-номер типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType
    ) throws CommonServiceException {
        //LOG.info("Start create message: sHead={}, sBody={}, nID_Subject={}, sMail={}, sContacts={}, sData={}, nID_SubjectMessageType={}", sHead, sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);

        SubjectMessage message
                = oSubjectMessageService.createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData,
                nID_SubjectMessageType);

        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        //LOG.info("End create message id={}, Mail={}, Contacts={}, Data={}, Date={}, Head={}, Body={}, Id_subject={}, ID_HistoryEvent_Service={}, sID_DataLink={}, nID_HistoryEvent_Service={}", message.getId(), message.getMail(), message.getContacts(), message.getData(), message.getDate(), message.getHead(), message.getBody(), message.getId_subject(), message.getnID_HistoryEvent_Service(), message.getsID_DataLink(), message.getnID_HistoryEvent_Service());

        return JsonRestUtils.toJsonResponse(message);
    }

    @ApiOperation(value = "/setMessageRate", notes = "Установка сообщения-оценки")
    @RequestMapping(value = "/setMessageRate", method = RequestMethod.GET)//Rate
    public
    @ResponseBody
    String setMessageRate(
            @ApiParam(value = "Строка-ИД заявки (временно опциональный)", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Строка-ИД рейтинга/оценки (число от 1 до 5)", required = true) @RequestParam(value = "sID_Rate", required = true) String sID_Rate,
            HttpServletResponse oResponse) throws CommonServiceException, IOException {


        if ("".equals(sID_Rate.trim())) {
            LOG.warn("Parameter(s) is absent! (sID_Order={}, sID_Rate={})", sID_Order, sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
        }
        Integer nRate = null;
        try {
            nRate = Integer.valueOf(sID_Rate);
        } catch (NumberFormatException ex) {
            LOG.warn("Error: {},incorrect param sID_Rate (not a number): {}", ex.getMessage(), sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
        }

        Map<String,Object> m = mInitOrderMessageFeedback(sID_Order, nRate);
        String sURL_Redirect = (String)m.get("sURL_Redirect");
        oResponse.sendRedirect(sURL_Redirect);
        String sReturn = "Ok!";
        return sReturn;
    }

    private Map<String,Object> mInitOrderMessageFeedback(String sID_Order, Integer nID_Rate) throws CommonServiceException{
        Map<String,Object> m = new HashMap();
        if (!sID_Order.contains("-")) {
            LOG.warn("Incorrect parameter! (sID_Order={})", sID_Order);
            throw new CommonServiceException(404, "Incorrect parameter! {sID_Order=" + sID_Order + "}");
        }
        if (nID_Rate==null || nID_Rate < 1 || nID_Rate > 5) {
            LOG.warn("incorrect param nID_Rate (not in range[1..5]): {}", nID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It is too short or too long number");
        }

        String sURL_Redirect = null;
        String sReturn = null;

        Long nID_HistoryEvent_Service;
        Long nID_Subject;
        HistoryEvent_Service oHistoryEvent_Service;

        try {
            //LOG.info("sID_Order: " + sID_Order + ", nRate: " + nRate);
            oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            if (oHistoryEvent_Service == null) {
                throw new CommonServiceException(404, "(sID_Order: " + sID_Order + ", nID_Rate: " + nID_Rate + "): Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - not found!");
            }
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            nID_Subject = oHistoryEvent_Service.getnID_Subject();

            String sToken = null;
            Integer nRateWas = oHistoryEvent_Service.getnRate();
            Boolean bExist = nRateWas != null && nRateWas > 0;
            m.put("bExist", bExist);
            if (bExist) {
                //throw new CommonServiceException(404, "(sID_Order: " + sID_Order + "): Record of HistoryEvent_Service, with sID_Order="+sID_Order+" - alredy has nRateWas="+nRateWas);
//                sReturn = "Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - already has nRateWas=" + nRateWas;
                LOG.warn("Alredy exist! {} (nID_HistoryEvent_Service={}, nID_Subject={})", sReturn, nID_HistoryEvent_Service, nID_Subject);
                sReturn = "Alredy exist!";
            } else {
                oHistoryEvent_Service.setnRate(nID_Rate);
                //LOG.info(String.format("set nRate=%s in sID_Order=%s", nRate, sID_Order));
                sToken = RandomStringUtils.randomAlphanumeric(15);
                //HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                oHistoryEvent_Service.setsToken(sToken);

                LOG.info("save HistoryEvent_Service... (nID_HistoryEvent_Service={}, nID_Subject={}, sID_Order={}, nRate={})", nID_HistoryEvent_Service, nID_Subject, sID_Order, nID_Rate);
                historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);

                Long nID_SubjectMessageType = 1l;
                SubjectMessage oSubjectMessage_Rate = oSubjectMessageService.createSubjectMessage(
                        sMessageHead(nID_SubjectMessageType, sID_Order),
                        "Оцінка " + nID_Rate + " (по шкалі від 1 до 5)", nID_Subject, "", "", "nID_Rate=" + nID_Rate,
                        nID_SubjectMessageType);
                if (nID_HistoryEvent_Service != null) {
                    oSubjectMessage_Rate.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
                }
                subjectMessagesDao.setMessage(oSubjectMessage_Rate);
                sReturn = "Ok!";
            }

            //сохранения сообщения с рейтингом, а на ррегиональном сервере, т.к. именно там хранится экземпляр БП.
            if (oHistoryEvent_Service.getnID_Proccess_Feedback() != null) {//issue 1006
                String snID_Process = "" + oHistoryEvent_Service.getnID_Proccess_Feedback();
                Integer nID_Server = oHistoryEvent_Service.getnID_Server();
                LOG.info("set rate={} to the nID_Proccess_Feedback={}", nID_Rate, snID_Process);
                List<String> aTaskIds = bpService.getProcessTasks(nID_Server, snID_Process);
                LOG.info("Found '{}' tasks by nID_Proccess_Feedback...", aTaskIds.size());
                if (!aTaskIds.isEmpty()) {//when process is not complete
                    bpService.setVariableToProcessInstance(nID_Server, snID_Process, "nID_Rate", nID_Rate);
                    LOG.info("process is not complete -- change rate in it!");
                    for (String sTaskId : aTaskIds) {
                        bpService.setVariableToActivitiTask(nID_Server, sTaskId, "nID_Rate", nID_Rate);
                    }
                }
            }
            sURL_Redirect = generalConfig.getSelfHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
            m.put("sURL_Redirect", sURL_Redirect);
            LOG.info("Redirecting to URL:{}", sURL_Redirect);
        } catch (CommonServiceException oActivitiRestException) {
            LOG.error("FAIL: {}", oActivitiRestException.getMessage());
            throw oActivitiRestException;
        } catch (Exception e) {
            LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(404, "[setMessageRate](sID_Order: " + sID_Order + ", nRate: " + nID_Rate + "): Unknown exception: " + e.getMessage());
        }
        return m;//sURL_Redirect
    }

    @ApiOperation(value = "/setMessageFeedback_Indirectly", notes = "")
    @RequestMapping(value = "/setMessageFeedback_Indirectly", method = RequestMethod.GET)
    public
    @ResponseBody
    String setMessageFeedback_Indirectly(
            @ApiParam(value = "Номер-ИД заявки, защищенный по алгоритму Луна", required = true)
            @RequestParam(value = "nID_Protected", required = true) Long nID_Protected,
            @ApiParam(value = "Номер-ИД бп фидбека по заявке", required = true)
            @RequestParam(value = "nID_Proccess_Feedback", required = true) String nID_Proccess_Feedback,
            @ApiParam(value = "Строка отзыв об услуге", required = true)
            @RequestParam(value = "sBody_Indirectly", required = true) String sBody_Indirectly,
            @ApiParam(value = "Строка-ИД Рейтинга/оценки (число от 1 до 5)", required = true)
            @RequestParam(value = "sID_Rate_Indirectly", required = true) String sID_Rate_Indirectly,
            @ApiParam(value = "ИД сервера, где размещена заявка (опционально, по умолчанию 0)", required = false)
            @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server)
            throws CommonServiceException {

        Optional<HistoryEvent_Service> eventServiceOptional = historyEventServiceDao.findBy("nID_Proccess_Feedback", Long.valueOf(nID_Proccess_Feedback));
        if (eventServiceOptional.isPresent()) {
            HistoryEvent_Service historyEventService = eventServiceOptional.get();
            if (historyEventService != null) {
                historyEventService.setsID_Rate_Indirectly(sID_Rate_Indirectly);
                historyEventServiceDao.saveOrUpdate(historyEventService);
                LOG.info("Successfully updated historyEvent_Service with the rate {}", sID_Rate_Indirectly);
                /////issue 1037
                // create rate-message
                String sID_Order = "" + (nID_Server != null ? nID_Server : 0) + "-" + nID_Protected;
                SubjectMessage oSubjectMessage_Rate = oSubjectMessageService.createSubjectMessage(
                        sMessageHead(6L, sID_Order),
                        "Оцінка " + sID_Rate_Indirectly + " (по шкалі від 2 до 5)", historyEventService.getnID_Subject(), "", "", "sID_Rate=" + sID_Rate_Indirectly, 6L);
                oSubjectMessage_Rate.setnID_HistoryEvent_Service(historyEventService.getId());
                subjectMessagesDao.setMessage(oSubjectMessage_Rate);
                LOG.info("Successfully created SubjectMessage:{}", oSubjectMessage_Rate.getHead());
                ///// create note-message
                oSubjectMessage_Rate = oSubjectMessageService.createSubjectMessage(
                        sMessageHead(7L, sID_Order), sBody_Indirectly,
                        historyEventService.getnID_Subject(), "", "", "sID_Rate=" + sID_Rate_Indirectly, 7L);
                oSubjectMessage_Rate.setnID_HistoryEvent_Service(historyEventService.getId());
                subjectMessagesDao.setMessage(oSubjectMessage_Rate);
                LOG.info("Successfully created SubjectMessage:{}", oSubjectMessage_Rate.getHead());
                /////
            }
        } else {
            LOG.error("Didn't find event service");
            return "Ok";
        }
        LOG.error("Finished execution");
        return "Ok";
    }

    @ApiOperation(value = "Получение массива сообщений ", notes = "Примеры:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/getMessages\n"
            + "nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n"
            + "Response:\n"
            + "\n```json\n"
            + "[\n"
            + "    {\n"
            + "        \"nID\":76,\"sHead\":\"Закликаю владу перевести цю послугу в електронну форму!\"\n"
            + "        ,\"sBody\":\"Дніпропетровськ - Видача витягу з технічної документації про нормативну грошову оцінку земельної ділянки\"\n"
            + "        ,\"sDate\":\"2015-06-03 22:09:16.536\"\n"
            + "        ,\"nID_Subject\":0\n"
            + "        ,\"sMail\":\"bvv4ik@gmail.com\"\n"
            + "        ,\"sContacts\":\"\"\n"
            + "        ,\"sData\":\"\"\n"
            + "        ,\"oSubjectMessageType\": {\n"
            + "            \"sDescription\": \"Просьба добавить услугу\",\n"
            + "            \"nID\": 0,\n"
            + "            \"sName\": \"ServiceNeed\"\n"
            + "        }\n"
            + "    }\n"
            + "]\n"
            + "\n```\n")
    @RequestMapping(value = "/getMessages", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getMessages() {
        List<SubjectMessage> messages = subjectMessagesDao.getMessages();
        return JsonRestUtils.toJsonResponse(messages);
    }

    /**
     * получение массива сообщений по услуге
     *
     * @param sID_Order   Строка-ИД заявки
     * @param nID_Subject
     * @return array of messages by sID_Order
     */
    @ApiOperation(value = "Получение массива сообщений по услуге", notes = "")
    @RequestMapping(value = "/getServiceMessages", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getServiceMessages(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Строка-Token", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "булевский флаг, Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            @ApiParam(value = "булевский флаг, определяющий сервер вызова", required = false) @RequestParam(value = "isRegion", required = false, defaultValue = "false") Boolean isRegion,
            @ApiParam(value = "Номер-ИД субьекта (владельца заявки)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject
    ) throws CommonServiceException {
        Long nID_HistoryEvent_Service;
        List<SubjectMessage> aSubjectMessage = new ArrayList();
        List<Object> aoSubjectMessage = new ArrayList();
        try {
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();

            if (bAuth) {
                actionEventService.checkAuth(oHistoryEvent_Service, nID_Subject, sToken);
            }
            aSubjectMessage = subjectMessagesDao.getMessages(nID_HistoryEvent_Service);
            aoSubjectMessage.addAll(aSubjectMessage);
            
            if(isRegion){
                aoSubjectMessage.addAll(historyEventDao.getHistoryEvents(null, nID_HistoryEvent_Service, false));
            }
            
        
        } catch (Exception e) {
            LOG.error("FAIL: {}", e);
            //LOG.trace("FAIL:", e);
            //throw new CommonServiceException(500, "[setServiceMessage]{sID_Order=" + sID_Order + "}:" + e.getMessage());
        }
        return JsonRestUtils.toJsonResponse(aoSubjectMessage);
    }


    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Сохранение сообщения по услуге", notes = "")
    @RequestMapping(value = "/setServiceMessage", method = {RequestMethod.POST, RequestMethod.GET})
    public
    @ResponseBody
    ResponseEntity setServiceMessage(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Номер-ИД субьекта (хозяина заявки сообщения)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Строка-Token", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "Строка-тело сообщения", required = true) @RequestParam(value = "sBody", required = true) String sBody,
            @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "строка-массива параметров", required = false) @RequestParam(value = "soParams", required = false) String soParams,
            @ApiParam(value = "булевский флаг, Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            @ApiParam(value = "Строка-ИД записи/файла (в оперативном кеше 'StorageTemp' (например Redis))", required = false) @RequestParam(value = "sID_File", required = false) String sID_File,
//            @ApiParam(value = "строка-ИД источника записи/файла (TempCentral(default), StaticRegion)", required = false) @RequestParam(value = "sID_FileSource", required = false, defaultValue = "TempCentral") String sID_FileSource,
//            @ApiParam(value = "строка-ИД автора записи (Self(default), SFS)", required = false) @RequestParam(value = "sID_FileAuthor", required = false, defaultValue = "Self") String sID_FileAuthor,
            @ApiParam(value = "строка-Название файла", required = false) @RequestParam(value = "sFileName", required = false) String sFileName,
            @ApiParam(value = "строка-типа контента файла", required = false) @RequestParam(value = "sFileContentType", required = false) String sFileContentType,
            @ApiParam(value = "ИД-номер типа сообщения", required = true) @RequestParam(value = "nID_SubjectMessageType", required = true) Long nID_SubjectMessageType,
            @ApiParam(value = "Заголовок сообщения", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "электронка, но которую отсылаем", required = false) @RequestParam(value = "sMail", required = false) String sMail,
            @ApiParam(value = "указывать дату и время отправки письма", required = false) @RequestParam(value = "bAddDate", required = false, defaultValue = "false") Boolean bAddDate,
            @ApiParam(value = "Строка-ИД(ключ) записи/файла", required = false) @RequestParam(value = "sID_DataLink", required = false) String sID_DataLink,
            @ApiParam(value = "Строка-ИД(ключ) источника записи/файла (Central(default), Region)", required = false) @RequestParam(value = "sID_DataLinkSource", required = false, defaultValue = "Region") String sID_DataLinkSource,
            @ApiParam(value = "Строка-ИД(ключ) автора записи/файла (System(default), SFS)", required = false) @RequestParam(value = "sID_DataLinkAuthor", required = false, defaultValue = "System") String sID_DataLinkAuthor
            //,//, defaultValue = "4"
    ) throws CommonServiceException {
        //12;ServiceCryptedFileRecived;Получен криптированный файл/пакет
        //sID_FileAuthor//SFS
        Long nID_HistoryEvent_Service;
        SubjectMessage oSubjectMessage;
        LOG.info("setServiceMessage started for the sID_Order {}", sID_Order);
        try {
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();

            if (bAuth) {
                actionEventService.checkAuth(oHistoryEvent_Service, nID_Subject, sToken);
            }
        
            if (isNotBlank(sID_File)) {
                LOG.info("sID_File={}, sFileName={}", sID_File, sFileName);
                    byte[] aByte_FileContent = null;
                    try {
                        byte[] aByte_FileContent_Redis = oBytesDataInmemoryStorage.getBytes(sID_File);
                        LOG.info("Size of bytes: {}", aByte_FileContent_Redis.length);
                        ByteArrayMultipartFile oByteArrayMultipartFile = null;
                        oByteArrayMultipartFile = getByteArrayMultipartFileFromStorageInmemory(aByte_FileContent_Redis);
                        if (oByteArrayMultipartFile != null) {
                            aByte_FileContent = oByteArrayMultipartFile.getBytes();
                            LOG.info("Size of multi part content: {}", aByte_FileContent_Redis.length);
                        } else {
                            LOG.error("oByteArrayMultipartFile==null! sID_File={}", sID_File);
                            throw new FileServiceIOException(
                                    FileServiceIOException.Error.REDIS_ERROR, "oByteArrayMultipartFile==null! sID_File=" + sID_File);
                        }
                    } catch (RecordInmemoryException e) {
                        LOG.warn("Error: {}", e.getMessage(), e);
                        throw new FileServiceIOException(
                                FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
                    } catch (ClassNotFoundException | IOException e) {
                        LOG.error("Error: {}", e.getMessage(), e);
                        throw new ActivitiException(e.getMessage(), e);
                    }
                    sID_DataLink = accessDataDao.setAccessData(aByte_FileContent);   //accessDataService//sKey
                    sID_DataLinkSource="Central";
                    LOG.info("Saved to Mongo! (sID_DataLink={},aByte_FileContent.length={})", sID_DataLink, aByte_FileContent.length);                    
            }

            if (isNotBlank(sID_DataLink)) {
                //LOG.info("sID_DataLink={}, sFileName={}", sID_DataLink, sFileName);
                if("Central".equals(sID_DataLinkSource)){
                }else if("Region".equals(sID_DataLinkSource)){
                }else{
                    throw new CommonServiceException(500, "{sID_Order=" + sID_Order + ", sID_DataLinkSource="+sID_DataLinkSource+"}:Invalid sID_DataLinkSource!");
                }
                JSONArray oaFile = new JSONArray();
                JSONObject o = new JSONObject();
                o.put("sFileName", sFileName);
                o.put("sFileContentType", sFileContentType);
                 o.put("sKey", sID_DataLink); //TODO: заменить на клиенте использование на одноименный параметр сущности
                //o.put("sID_DataLink", sID_DataLink);
                o.put("sID_DataLinkSource", sID_DataLinkSource); //TODO: заменить на сервере и клиенте использование на одноименный параметр сущности 
                o.put("sID_DataLinkAuthor", sID_DataLinkAuthor); //TODO: заменить на сервере и клиенте использование на одноименный параметр сущности
                //sID_FileAuthor//SFS
                oaFile.put(o);
                sData = new JSONObject().put("aFile", oaFile).toString();
                LOG.info("sData={}", sData);                
            }
            
            historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
            oSubjectMessage = oSubjectMessageService.createSubjectMessage(sMessageHead(nID_SubjectMessageType,
                    sID_Order), sBody, nID_Subject, sMail != null ? sMail : "", "", sData, nID_SubjectMessageType);
            oSubjectMessage.setsID_DataLink(sID_DataLink);
            if (bAddDate != null) {
                oSubjectMessage.setDate(new DateTime());
            }
            oSubjectMessage.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
            subjectMessagesDao.setMessage(oSubjectMessage);

            Long messageID = oSubjectMessage.getId();

            LOG.info("Successfully saved message with the ID {}", messageID);

            String sHost = null;
            int nID_Server = oHistoryEvent_Service.getnID_Server();
            Optional<Server> oOptionalServer = serverDao.findById(Long.valueOf(nID_Server + ""));
            if (!oOptionalServer.isPresent()) {
                throw new RecordNotFoundException();
            } else {
                sHost = oOptionalServer.get().getsURL();
            }

            String mergeUrl = sHost + "/service/action/task/mergeVariable";
            Long nID_Task = oHistoryEvent_Service.getnID_Process();
            Map<String, String> mergeParams = new HashMap<>();
            Map<String, List<String>> multipleParam = new HashMap<>();
            mergeParams.put("processInstanceId", String.valueOf(nID_Task));
            mergeParams.put("key", "saTaskStatus");

            LOG.info("mergeParams={}, mergeUrl=", mergeParams, mergeUrl);

            if (nID_SubjectMessageType == 8L) { //citizen's comment or question
                mergeParams.put("insertValues", "GotUpdate");
            }
            if (nID_SubjectMessageType == 9L) { //officer's comment or question
                multipleParam.put("removeValues", Arrays.asList(new String[] {"GotUpdate", "GotAnswer"}));
            }
            httpRequester.getInside(mergeUrl, mergeParams, multipleParam);
            if(nID_SubjectMessageType==9){
                 //String sToken = Tool.getGeneratedToken();
                 oNotificationPatterns.sendTaskEmployeeMessageEmail(sHead, sO(sBody), sMail, sID_Order, soParams);
            }
        } catch (Exception e) {
            LOG.error("FAIL: {} (sID_Order={})", e.getMessage(), sID_Order);
            LOG.error("FAIL:", e);
            throw new CommonServiceException(500, "{sID_Order=" + sID_Order + "}:" + e.getMessage());
        }
        return JsonRestUtils.toJsonResponse(oSubjectMessage);
    }

    @ApiOperation(value = "Сохранить отзыв по услуге от сторонней организации")
    @RequestMapping(value = "/setFeedbackExternal", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> setFeedbackExternal(
            @ApiParam(value = "Строка-токен для доступа к записи", required = false) @RequestParam(value = "sID_Token", required = false) String sID_Token,
            @ApiParam(value = "ID источника", required = true) @RequestParam(value = "sID_Source", required = true) String sID_Source,
            @ApiParam(value = "ФИО автора отзыва", required = true) @RequestParam(value = "sAuthorFIO", required = true) String sAuthorFIO,
            @ApiParam(value = "e-mail автора отзыва", required = true) @RequestParam(value = "sMail", required = true) String sMail,
            @ApiParam(value = "Заголовок отзыва", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "Тело отзыва", required = true) @RequestParam(value = "sBody", required = true) String sBody,
            @ApiParam(value = "Место", required = false) @RequestParam(value = "sPlace", required = false) String sPlace,
            @ApiParam(value = "Имя сотрудника", required = false) @RequestParam(value = "sEmployeeFIO", required = false) String sEmployeeFIO,
            @ApiParam(value = "ID оценки", required = false) @RequestParam(value = "nID_Rate", required = false) Long nID_Rate,
            //@ApiParam(value = "ID оценки (old compatibility)", required = false) @RequestParam(value = "sID_Rate", required = false) Long sID_Rate,
            @ApiParam(value = "ID заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "ID сервиса", required = false) @RequestParam(value = "nID_Service", required = false, defaultValue = "0") Long nID_Service,
            //@ApiParam(value = "комментарий для отзыва", required = false) @RequestParam(value = "sAnswer", required = false) String sAnswer,
            @ApiParam(value = "ID отзыва, который надо отредактировать", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "ID субъекта создавшего сообщение", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "булевый параметр bSelf", required = false) @RequestParam(value = "bSelf", required = false) boolean bSelf,
            HttpServletResponse oResponse
    ) throws CommonServiceException, IOException {

        LOG.info("Started! (sID_Source={}, nID_Service={}, nID={}, sID_Order={}, nID_Subject={})", sID_Source, nID_Service, nID, nID_Subject);
        LOG.info("nID_Subject: " + nID_Subject);
        String responseMessage = null;

        /*if(nID_Rate==null){
            nID_Rate=sID_Rate;
        }*/
        //String sURL_Redirect = null;
        JSONObject oJSONObject = new JSONObject();
        Boolean bExist = false;
        LOG.info("sID_Order: "+sID_Order);
        if(sID_Order!=null){
            Map<String,Object> m = mInitOrderMessageFeedback(sID_Order, Integer.valueOf(""+nID_Rate));
            bExist = (Boolean) m.get("bExist");
        }
        if(bExist){
            LOG.warn("Cant save feedback! (sID_Order={})", sID_Order);
            if(bSelf){
                responseMessage = String.format("%s/service/%d/feedback",
                        generalConfig.getSelfHost(), nID_Service);
            	oResponse.sendRedirect(responseMessage);
            }else{
                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Cant save feedback! sID_Order="+sID_Order, HttpStatus.NOT_FOUND);
            }
        }else{
            try {
                String sAnswer=null;
                LOG.info("nIdSubject that we try to set in controller: " + nID_Subject);
                SubjectMessageFeedback oSubjectMessageFeedback = oSubjectMessageService.setSubjectMessageFeedback(sID_Source,
                        sAuthorFIO, sMail, sHead, sBody, sPlace, sEmployeeFIO, nID_Rate, nID_Service, sAnswer, nID,
                        nID_Subject, sID_Order);
                LOG.info("sMail: " + sMail + " oSubjectMessageFeedback: " + oSubjectMessageFeedback + " nID_Rate: " + nID_Rate + " sAuthorFIO: " + sAuthorFIO );
                if (nID!=null && (sID_Token==null || sID_Token.equals(oSubjectMessageFeedback.getsID_Token()))) {
                    throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "sID_Token not equal or absant! sID_Token="+sID_Token+", nID="+nID, HttpStatus.NOT_FOUND);
                }

                LOG.info("successfully saved feedback for the sID_Source: {}, nID_Service: {}, nID: {}, sID_Token: {}, sID_Order: {}",
                        sID_Source, nID_Service, oSubjectMessageFeedback.getId(), oSubjectMessageFeedback.getsID_Token(), oSubjectMessageFeedback.getsID_Order());
               responseMessage = String.format("%s/service/%d/feedback?nID=%d&sID_Token=%s",
                        generalConfig.getSelfHost(), nID_Service, oSubjectMessageFeedback.getId(), oSubjectMessageFeedback.getsID_Token());

                if(bSelf){
                    oResponse.sendRedirect(responseMessage);
                }

            } catch (Exception e) {
                LOG.error("(nID={}, message={})", nID, e.getMessage());
                throw new CommonServiceException(e.getMessage(), e);
            }
        }
        oJSONObject.put("sURL", responseMessage);
        if(nID_Rate.compareTo(1L)==0 || nID_Rate.compareTo(2L)==0||nID_Rate.compareTo(3L)==0) {
            String sHost = null;
            try {
    	        HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
    	   if(oHistoryEvent_Service.getnID_Proccess_Feedback()==null) {
    	        int nID_Server = oHistoryEvent_Service.getnID_Server();
    	        nID_Server = generalConfig.getServerId(nID_Server);
    	        Optional<Server> oOptionalServer = serverDao.findById(Long.valueOf(nID_Server + ""));
    	        if (!oOptionalServer.isPresent()) {
    	            throw new RecordNotFoundException();
    	        } else {//https://test.region.igov.org.ua/wf
    	            sHost = oOptionalServer.get().getsURL();
    	        }
    	        String sURL = sHost + "/service/action/feedback/runFeedBack";
    	        Map<String, String> mParam = new HashMap<>();
    	        mParam.put("snID_Process", String.valueOf(oHistoryEvent_Service.getnID_Process()));
    	        LOG.info("mParam={}", mParam);
    	        String sReturnRegion = httpRequester.getInside(sURL, mParam);
    	        LOG.info("(sReturnRegion={})", sReturnRegion);
            }
            } catch (Exception e) {
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        e.getMessage(), e, HttpStatus.FORBIDDEN);
            }
            }
        return new ResponseEntity<>(oJSONObject.toString(), HttpStatus.CREATED);

    }


    @ApiOperation(value = "Сохранить ответ об отзыве по услуге от сторонней организации")
    @RequestMapping(value = "/setFeedbackAnswerExternal", method = RequestMethod.POST)
    public ResponseEntity<String> setFeedbackAnswerExternal(
            @ApiParam(value = "Строка-токен для доступа к записи", required = false) @RequestParam(value = "sID_Token", required = false) String sID_Token,
            @ApiParam(value = "Тело ответа", required = true) @RequestParam(value = "sBody", required = true) String sBody,
            @ApiParam(value = "ФИО автора ответа", required = true) @RequestParam(value = "sAuthorFIO", required = true) String sAuthorFIO,
            @ApiParam(value = "ID отзыва, ответ на который надо сохранить", required = true) @RequestParam(value = "nID_SubjectMessageFeedback", required = true) Long nID_SubjectMessageFeedback,
            @ApiParam(value = "Собственный контр-ответ автора фидбека", required = false) @RequestParam(value = "bSelf", required = true) Boolean bSelf,
            @ApiParam(value = "ID субъекта создавшего сообщение", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject
    ) throws CommonServiceException {

        String sAnswer=sBody;
        LOG.info("Started! (nID_SubjectMessageFeedback={}, sBody={})", nID_SubjectMessageFeedback, sBody);
        JSONObject oJSONObject = new JSONObject();
        try {
            SubjectMessageFeedback oSubjectMessageFeedback = oSubjectMessageService.getSubjectMessageFeedbackById(nID_SubjectMessageFeedback);
            if (oSubjectMessageFeedback==null) {
                LOG.warn("SubjectMessageFeedback not found! (nID_SubjectMessageFeedback={})", nID_SubjectMessageFeedback);
                throw new CommonServiceException("oSubjectMessageFeedback==null!", "(nID_SubjectMessageFeedback="+nID_SubjectMessageFeedback+"):oSubjectMessageFeedback==null!");
            }
            if (sID_Token!=null && sID_Token.equals(oSubjectMessageFeedback.getsID_Token())) {
                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "sID_Token not equal! sID_Token="+sID_Token+", nID_SubjectMessageFeedback="+nID_SubjectMessageFeedback, HttpStatus.NOT_FOUND);
            }
            SubjectMessageFeedbackAnswer oSubjectMessageFeedbackAnswer=null;
            if (isEmpty(sAnswer)) {
                LOG.warn("sBody is empty!, nID_SubjectMessageFeedback={}", nID_SubjectMessageFeedback);
                throw new CommonServiceException("sBody is empty!", "nID_SubjectMessageFeedback="+nID_SubjectMessageFeedback);
            }else{
                oSubjectMessageFeedbackAnswer = oSubjectMessageService.setSubjectMessageFeedbackAnswer(nID_SubjectMessageFeedback, sAnswer, nID_Subject, bSelf, sAuthorFIO);
                List<SubjectMessageFeedbackAnswer> aSubjectMessageFeedbackAnswer = oSubjectMessageFeedback.getoSubjectMessageFeedbackAnswers();
                aSubjectMessageFeedbackAnswer.add(oSubjectMessageFeedbackAnswer);
                oSubjectMessageFeedback.setoSubjectMessageFeedbackAnswers(aSubjectMessageFeedbackAnswer);
                subjectMessageFeedbackDao.update(oSubjectMessageFeedback);
            }
            LOG.info("successfully saved answer to feedback (nID_SubjectMessageFeedback={})", nID_SubjectMessageFeedback);
            oJSONObject.put("nID", oSubjectMessageFeedbackAnswer.getId());
            return new ResponseEntity<>(oJSONObject.toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error("/setFeedbackAnswerExternal, message: {}", e.getMessage());
            throw new CommonServiceException(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "Получить отзыв по услуге от сторонней организации по номеру отзыва и паролю или все отзывы по сервису")
    @RequestMapping(value = "/getFeedbackExternal", method = RequestMethod.GET)
    public ResponseEntity<String> getFeedbackExternal(
            @ApiParam(value = "ID отзыва", required = false) @RequestParam(value = "nID", required = false) Long nID,
            @ApiParam(value = "Строка-токен для доступа к записи", required = false) @RequestParam(value = "sID_Token", required = false) String sID_Token,

            @ApiParam(value = "фильтр, при задаче которого будет выдаваться список, в котором nID записей будут менее чем этот", required = false) @RequestParam(value = "nID__LessThen_Filter", required = false) Long nID__LessThen_Filter,
            @ApiParam(value = "фильтр, при задаче которого будет лимитироваться указанное число отдаваемых строк", required = false) @RequestParam(value = "nRowsMax", required = false, defaultValue = "20") Integer nRowsMax,

            @ApiParam(value = "ID сервиса", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service)
            throws CommonServiceException {

        LOG.info("getFeedbackExternal started for the nID: {}, sID_Token: {}", nID, sID_Token);
        if(nID!=null){
            SubjectMessageFeedback feedback = oSubjectMessageService.getSubjectMessageFeedbackById(nID);
            if (feedback != null) {
                if (sID_Token!=null && sID_Token.equals(feedback.getsID_Token())) {
                    return JsonRestUtils.toJsonResponse(HttpStatus.OK, feedback);
                } else {
                    throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "sID_Token not equal or absant! sID_Token="+sID_Token+", nId="+nID, HttpStatus.NOT_FOUND);
                }
            } else {
                throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "feedback = null nId="+nID, HttpStatus.NOT_FOUND);
            }
        }else if(nID_Service!=null){
                LOG.info("getFeedbackExternal started for the nID_Service: {} ", nID_Service);
            List<SubjectMessageFeedback> feedbackList =
                    //oSubjectMessageService.getAllSubjectMessageFeedbackBynID_Service(nID_Service); // return list of feedbacks by nID_Service
                    oSubjectMessageService.getAllSubjectMessageFeedback_Filtered(nID_Service, nID__LessThen_Filter, nRowsMax); // return list of feedbacks by nID_Service
                    //getAllSubjectMessageFeedbackBynID_Service(Long nID_service, Integer nID__LessThen_Filter, Integer nRowsMax)

            for (SubjectMessageFeedback messageFeedback : feedbackList) {
                messageFeedback.setsID_Token(null);
            }
            LOG.info("getFeedbackExternal returned list size: {} for nID_Service: {} ", feedbackList.size(), nID_Service);
            return JsonRestUtils.toJsonResponse(HttpStatus.OK, feedbackList);
        }else{
            throw new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "need parameters!", HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Получить сообщение-фидбек заявки", notes = "получает сообщение-фидбека:\n"
            + "Если объект не найден по sID_Order, то возвращается код 404 и сообщение \"Record Not Found\"\n"
            + "Если sToken<>'' и sToken<>null и sToken не совпадет с HistoryEvent_Service.sToken то возвращается 403 статус и сообщение \"Security Error\"\n"
            + "если в найденном обекте SubjectMessage sBody='', то sDate в результате возвращается как null\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/getMessageFeedbackExtended?sID_Order=0-4446&sToken=TokenValue"
            + "\n```json\n"
            + "{\n"
            + "    \"sDate\":\"2015-11-10 23:23:59 001\",\n"
            + "    \"sHead\":\"Получение справки о доходах\",\n"
            + "    \"sID_Order\":\"0-4446\",\n"
            + "}\n"
            + "\n```\n")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Security Error (если не совпадает токен)"),
            @ApiResponse(code = 404, message = "Record not found")})
    @RequestMapping(value = "/getMessageFeedbackExtended", method = RequestMethod.GET)//Feedback
    public
    @ResponseBody
    Map<String, Object> getMessageFeedbackExtended(
            @ApiParam(value = "Строка-ИД заявки услуги", required = true) @RequestParam(value = "sID_Order") String sID_Order,
            @ApiParam(value = "Строка-токен (защита от постороннего доступа)", required = true) @RequestParam(value = "sToken") String sToken,
            @ApiParam(value = "Номер-ИД типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", defaultValue = "2") Long nID_SubjectMessageType
    ) throws CommonServiceException {

        Map<String, Object> mReturn = new HashMap<>();

        try {
            if ("".equals(sToken.trim())) {
                LOG.warn("Wrong sToken: {}", sToken);
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
            }
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            if (oHistoryEvent_Service != null) {
                //if (oHistoryEvent_Service.getsToken() != null && oHistoryEvent_Service.getsToken().equals(sToken)){
                List<SubjectMessage> aSubjectMessage = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", oHistoryEvent_Service.getId());
                SubjectMessage oSubjectMessage_Found = null;
                if (aSubjectMessage != null && !aSubjectMessage.isEmpty()) {
                    for (SubjectMessage oSubjectMessage : aSubjectMessage) {
                        if (Objects.equals(oSubjectMessage.getSubjectMessageType().getId(), nID_SubjectMessageType)) {//2
                            oSubjectMessage_Found = oSubjectMessage;
                        } else {
                            LOG.info("Skipping subject message from processing as its ID is: {}", oSubjectMessage.getSubjectMessageType().getId());
                        }
                    }
                } else {
                    LOG.info("No SubjectMessage objects found with nID_HistoryEvent_Service:{}", oHistoryEvent_Service.getId());
                }
                mReturn.put("sID_Order", sID_Order);
                if (oSubjectMessage_Found != null) {
                    mReturn.put("sHead", oSubjectMessage_Found.getHead());
                    if (oSubjectMessage_Found.getBody() != null && !"".equals(oSubjectMessage_Found.getBody().trim())) {
                        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
                        mReturn.put("sDate", oSimpleDateFormat.format(oSubjectMessage_Found.getDate().toDate()));
                    } else {
                        mReturn.put("sDate", null);
                    }
                } else {
                    mReturn.put("sHead", sMessageHead(nID_SubjectMessageType, sID_Order));
                    mReturn.put("sDate", null);
                }
                return mReturn;
            } else {
                LOG.warn("Skipping history event service, wrong sID_Order: {}",  sID_Order);
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
            }
        } catch (CRCInvalidException e) {
            LOG.error("Error: {}, occurred while getting message feedback:",  e.getMessage());
            LOG.trace("FAIL:", e);
        }

        throw new CommonServiceException(
                ExceptionCommonController.BUSINESS_ERROR_CODE,
                "Record Not Found",
                HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Сохранить сообщение-фидбек заявки", notes = "сохраняет сообщение-фидбек\n"
            + "Если запись успешно добавлена/обновлена то устанавливается sToken='' и sDate устанавливается в текущую.\n"
            + "Если запись найдена и sBody<>'', то возвращается статус 403 и сообщение \"Already exists\"\n"
            + "Если запись не найдена и sBody<>'', то возвращается 404 статус и сообщение \"Record Not Found\"\n"
            + "Если sToken<>'' и sToken<>null и sToken не совпадет с HistoryEvent_Service.sToken то возвращается 403 статус и сообщение \"Security Error\"")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Already exist (если sBody в SubjectMessage не пустое ) / Security Error (если не совпадает токен)"),
            @ApiResponse(code = 404, message = "Record not found")})
    @RequestMapping(value = "/setMessageFeedbackExtended", method = RequestMethod.POST)//Feedback

    public
    @ResponseBody
    String setMessageFeedbackExtended(
            @ApiParam(value = "Строка-ИД заявки услуги", required = true) @RequestParam(value = "sID_Order") String sID_Order,
            @ApiParam(value = "Строка-токен (защита от постороннего доступа)", required = true) @RequestParam(value = "sToken") String sToken,
            @ApiParam(value = "Номер-ИД типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", defaultValue = "2") Long nID_SubjectMessageType,
            @ApiParam(value = "строка текста фидбэка", required = true) @RequestParam(value = "sBody") String sBody) throws CommonServiceException {

        try {
            if ("".equals(sToken.trim())) {
                LOG.warn("Wrong sToken: {}", sToken);
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
            }
            if (2l != nID_SubjectMessageType) {
                LOG.warn("Wrong nID_SubjectMessageType: {}", nID_SubjectMessageType);
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
            }
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            if (oHistoryEvent_Service != null) {
                if (oHistoryEvent_Service.getsToken() != null && oHistoryEvent_Service.getsToken().equals(sToken)) {
                    
                    SubjectMessage oSubjectMessage_Feedback = oSubjectMessageService.createSubjectMessage(
                            sMessageHead(nID_SubjectMessageType, sID_Order), "", oHistoryEvent_Service.getnID_Subject(),
                            "", "", "", nID_SubjectMessageType);//2l
                    oSubjectMessage_Feedback.setnID_HistoryEvent_Service(oHistoryEvent_Service.getId());//nID_HistoryEvent_Service
                    subjectMessagesDao.setMessage(oSubjectMessage_Feedback);
                    LOG.info("No SubjectMessage records found, create new!");
                    oHistoryEvent_Service.setsToken("");
                    historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
                    
                } else {
                    LOG.warn("Skipping history event service from processing as it contains wrong token: {}", oHistoryEvent_Service.getsToken());
                    throw new CommonServiceException(
                            ExceptionCommonController.BUSINESS_ERROR_CODE,
                            "Security Error",
                            HttpStatus.FORBIDDEN);
                }
            } else {
                LOG.warn("Skipping history event service, wrong sID_Order: {}", sID_Order);
                throw new CommonServiceException(
                        ExceptionCommonController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
            }
        } catch (CRCInvalidException e) {
            LOG.error("Error: {}, occurred while setting message feedback:", e.getMessage());
            LOG.trace("FAIL:", e);
        }

        return "Ok";
    }

    @ApiOperation(value = " Центральный сервис получения контента файла", notes = ""
            + "получает массив байт контента файла:\n"
            + "Если не найдена запись с сообщением nID_Message, то возвращать ошибку с текстом сообщения \"Record not found\"\n"
            + "Если не найден файл в монге, то возвращать ошибку с текстом сообщения \"Content not found\"\n"
            + "Логика связанная с параметром sID_Order \n"
            + " -задан, то сверять наличие привязанности записи сообщения SubjectMessage с записью HistoryEvent_Service \n\n"
            + " (по полю \"nID_HistoryEvent_Service\" в SubjectMessage) и значению поля sID_Order в связанной записи HistoryEvent_Service; \n"
            + " если значения sID_Order не совпадают - выдавать ошибку \"Alien order\" \n"
            + " если значения sID_Order не связаны - выдавать ошибку \"Order not found\" \n"
            + " -не задан - отдавать найденный контент, если он найден /n/n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/getMessageFile?sID_Order=0-4446&nID_Message=")
    @RequestMapping(value = "/getMessageFile", method = { RequestMethod.GET })
    public
    @ResponseBody
    byte[] getMessageFile(
            @ApiParam(value = "Строка-ИД заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "Номер-ИД сообщения", required = true) @RequestParam(value = "nID_Message", required = true) Long nID_Message) throws CommonServiceException{

    		//content of the message file
    		byte[] aByte = null;
    		try{
	    		SubjectMessage message = subjectMessagesDao.getMessage(nID_Message);
	    		if(message == null){
	        		LOG.info("Message is not found by nID_Message {}", nID_Message);
	    			CommonServiceException newErr = new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Record not found");
	                throw newErr;
	    		}
	    		LOG.info("Message is recieved by nID_Message {}", nID_Message);
	    		if (isNotBlank(sID_Order)){
	    			HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
	                if (oHistoryEvent_Service != null) {
                                LOG.info("oHistoryEvent_Service.getId()={},message.getnID_HistoryEvent_Service()={}", oHistoryEvent_Service.getId(),message.getnID_HistoryEvent_Service());
	                	if (oHistoryEvent_Service.getId() == null){
	                		LOG.info("ID_HIstoryEvent_Service of the order is empty {}", nID_Message);
	        				CommonServiceException newErr = new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Order not found");
	        				throw newErr;
	                	}else if(!Objects.equals(oHistoryEvent_Service.getId(), message.getnID_HistoryEvent_Service())){
	                		LOG.info("ID_HIstoryEvent_Service of the message is not equal to ID_HIstoryEvent_Service of the order {}", nID_Message);
	        				CommonServiceException newErr = new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Alien order");
	        				throw newErr;
	                	}
	                }
	    		}
	    		if (isNotBlank(message.getData())){
	    			LOG.info("Field sData in message is not null");
	    	        JSONArray sDataArrayJson = (new JSONObject(message.getData())).getJSONArray("aFile");
	    			String sFileName = (String) ((JSONObject) sDataArrayJson.getJSONObject(0)).get("sFileName");
	    			String sKey = (String) ((JSONObject) sDataArrayJson.getJSONObject(0)).get("sKey");

	    			LOG.info("sKey value {}",sKey);
	    			LOG.info("sFileName value {}", sFileName);

                                aByte = durableBytesDataStorage.getData(sKey);
                                LOG.info("aByte.length={}", aByte.length);
	    		}
    		}catch(Exception e){
    			if(e instanceof CommonServiceException)
    				throw (CommonServiceException)e;
    			else
    			{
    				LOG.error("FAIL: {}", e.getMessage());
    	            LOG.trace("FAIL:", e);
    	            throw new CommonServiceException(500, "Unknown exception: " + e.getMessage());
    			}
    		}
    	return aByte;
    }

    @ApiOperation(value = " Возвращает содержимое отсылаемого сообщения, если такое существует.", notes = ""
            + "Возвращает содержимое отсылаемого сообщения, если такое существует.:\n"
            + "Если не найдена запись SubjectMessage по ID или у найденного сообщения пустое поле, указывающее на контект сообщения, то метод возвращает ошибку с текстом сообщения \"Record not found\"\n"
            + "Пример:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/getSubjectMessageData?nID_SubjectMessage=111111")
    @RequestMapping(value = "/getSubjectMessageData", method = { RequestMethod.GET })
    public
    @ResponseBody
    void getSubjectMessageData(
            @ApiParam(value = "номер-ИД записи с сообщением", required = false) @RequestParam(value = "nID_SubjectMessage", required = true) String sID_SubjectMessage,
            @ApiParam(value = "Номер-ИД субьекта (хозяина заявки сообщения)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "булевский флаг, Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            HttpServletResponse httpResponse) throws CommonServiceException{

        //content of the message file
        String res = null;
        try{
            Long nID_SubjectMessage = Long.valueOf(sID_SubjectMessage);
            SubjectMessage message = subjectMessagesDao.getMessage(nID_SubjectMessage);
            Long nID_HistoryEvent_Service = message.getnID_HistoryEvent_Service();
            //LOG.info("nID_HistoryEvent_Service={}", nID_HistoryEvent_Service);

            Optional<HistoryEvent_Service> oHistoryEvent_Service = historyEventServiceDao.findById(nID_HistoryEvent_Service);

            if(bAuth && oHistoryEvent_Service.isPresent()){
                actionEventService.checkAuth(oHistoryEvent_Service.get(), nID_Subject, oHistoryEvent_Service.get().getsToken());
            }
            if(message == null || isBlank(message.getsID_DataLink())){
                //LOG.info("Message is not found by nID_Message {}", nID_SubjectMessage);
                CommonServiceException newErr = new CommonServiceException(ExceptionCommonController.BUSINESS_ERROR_CODE, "Record not found");
                throw newErr;
            }
            //LOG.info("Message is recieved by nID_Message {}", nID_SubjectMessage);

            if (isNotBlank(message.getsID_DataLink())){
                //LOG.info("Field sID_DataLink in message is not null");

                byte[] resBytes = durableBytesDataStorage.getData(message.getsID_DataLink());

                httpResponse.setHeader("Content-Type", "text/html;charset=UTF-8");
                res = new String(resBytes, Charset.forName("UTF-8"));

                httpResponse.getWriter().print(res);
                httpResponse.getWriter().close();
            }
        }catch(Exception e){
            if(e instanceof CommonServiceException)
                throw (CommonServiceException)e;
            else
            {
                LOG.error("FAIL: {}", e.getMessage());
                LOG.trace("FAIL:", e);
                throw new CommonServiceException(500, "Unknown exception: " + e.getMessage());
            }
        }
    }

}
