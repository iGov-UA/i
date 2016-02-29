package org.igov.service.controller;

import static org.igov.service.business.action.task.core.AbstractModelTask.getByteArrayMultipartFileFromStorageInmemory;
import static org.igov.service.business.subject.SubjectMessageService.sMessageHead;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.igov.io.GeneralConfig;
import org.igov.io.db.kv.statical.IBytesDataStorage;
import org.igov.io.db.kv.temp.IBytesDataInmemoryStorage;
import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.subject.message.SubjectMessage;
import org.igov.model.subject.message.SubjectMessagesDao;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.business.action.task.bp.BpService;
import org.igov.service.business.subject.SubjectMessageService;
import org.igov.service.exception.CRCInvalidException;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.FileServiceIOException;
import org.igov.util.MethodsCallRunnerUtil;
import org.igov.util.JSON.JsonRestUtils;
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

import com.google.common.base.Optional;
@Controller
@Api(tags = {"SubjectMessageController"}, description = "Сообщения субьектов")
@RequestMapping(value = "/subject/message")
public class SubjectMessageController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectMessageController.class);

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
    MethodsCallRunnerUtil methodCallRunner;
    /**
     * получение сообщения
     *
     * @param nID ID сообщения
     */
    @ApiOperation(value = "Получение сообщения", notes = "##### SubjectMessageController - Сообщения субьектов. Получение сообщения #####\n\n"
            + "HTTP Context: http://server:port/wf/service/subject/message/getMessage\n\n\n"
            + "Примеры: https://test.igov.org.ua/wf/service/subject/message/getMessage?nID=76\n\n"
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

        SubjectMessage message = subjectMessagesDao.getMessage(nID);
        return JsonRestUtils.toJsonResponse(message);
    }  
    
    /**
     * Сохранение сообщения
     *
     * @param sHead                  Строка-заглавие сообщения
     * @param sBody                  Строка-тело сообщения
     * @param sMail                  Строка электронного адреса автора //опционально
     * @param sContacts              Строка контактов автора //опционально
     * @param sData                  Строка дополнительных данных автора //опционально
     * @param nID_SubjectMessageType ИД-номер типа сообщения //опционально (по
     *                               умолчанию == 0)
     */
    @ApiOperation(value = "Сохранение сообщение ", notes = "##### SubjectMessageController - Сообщения субьектов. Сохранение сообщения #####\n\n"
            + "HTTP Context: http://server:port/wf/service/subject/message/setMessage\n\n\n"
            + "- nID_SubjectMessageType: nID;sName;sDescription 0;ServiceNeed;Просьба добавить услугу 1;ServiceFeedback;Отзыв о услуге\n\n\n"
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
     
        SubjectMessage message
                = oSubjectMessageService.createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData,
                nID_SubjectMessageType);
       
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
       
        return JsonRestUtils.toJsonResponse(message);
    }

    /**
     * Сохранение сообщения оценки
     *
     * @param sID_Order     Строка-ИД заявки (временно опциональный)
     * @param sID_Rate      Строка-ИД Рнйтинга/оценки (число от 1 до 5)
//     * @param nID_Protected Номер-ИД заявки, защищенный по алгоритму Луна,
//     *                      опционально(для обратной совместимости)
     * @throws CommonServiceException
     */
    @ApiOperation(value = "/setMessageRate", notes = "##### SubjectMessageController - Сообщения субьектов. Установка сообщения-оценки #####\n\n")
    @RequestMapping(value = "/setMessageRate", method = RequestMethod.GET)//Rate
    public
    @ResponseBody
    String setMessageRate(
            @ApiParam(value = "Строка-ИД заявки (временно опциональный)", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Строка-ИД рейтинга/оценки (число от 1 до 5)", required = true) @RequestParam(value = "sID_Rate", required = true) String sID_Rate,
            HttpServletResponse oResponse) throws CommonServiceException {

        if (!sID_Order.contains("-")) {
            LOG.warn("Incorrect parameter! (sID_Order={})", sID_Order);
            throw new CommonServiceException(404, "Incorrect parameter! {sID_Order=" + sID_Order + "}");
        }

        if ("".equals(sID_Rate.trim())) {
            LOG.warn("Parameter(s) is absent! (sID_Order={}, sID_Rate={})", sID_Order, sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
        }
        Integer nRate;
        try {
            nRate = Integer.valueOf(sID_Rate);
        } catch (NumberFormatException ex) {
            LOG.warn("Error: {},incorrect param sID_Rate (not a number): {}", ex.getMessage(), sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It isn't number.");
        }
        if (nRate < 1 || nRate > 5) {
            LOG.warn("incorrect param sID_Rate (not in range[1..5]): {}", sID_Rate);
            throw new CommonServiceException(404, "Incorrect value of sID_Rate! It is too short or too long number");
        }

        String sReturn = "Ok!";

        Long nID_HistoryEvent_Service;
        Long nID_Subject;
        HistoryEvent_Service oHistoryEvent_Service;

        try {
            //LOG.info("sID_Order: " + sID_Order + ", nRate: " + nRate);
            oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            if (oHistoryEvent_Service == null) {
                throw new CommonServiceException(404, "(sID_Order: " + sID_Order + ", nRate: " + nRate + "): Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - not found!");
            }
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            nID_Subject = oHistoryEvent_Service.getnID_Subject();

            String sToken = null;
            Integer nRateWas = oHistoryEvent_Service.getnRate();
            if (nRateWas != null && nRateWas > 0) {
                //throw new CommonServiceException(404, "(sID_Order: " + sID_Order + "): Record of HistoryEvent_Service, with sID_Order="+sID_Order+" - alredy has nRateWas="+nRateWas);
                sReturn = "Record of HistoryEvent_Service, with sID_Order=" + sID_Order + " - already has nRateWas=" + nRateWas;
                LOG.warn("{} (nID_HistoryEvent_Service={}, nID_Subject={})", sReturn, nID_HistoryEvent_Service, nID_Subject);
            } else {

                oHistoryEvent_Service.setnRate(nRate);
                //LOG.info(String.format("set nRate=%s in sID_Order=%s", nRate, sID_Order));
                sToken = RandomStringUtils.randomAlphanumeric(15);
                //HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                oHistoryEvent_Service.setsToken(sToken);

                LOG.info("save HistoryEvent_Service... (nID_HistoryEvent_Service={}, nID_Subject={}, sID_Order={}, nRate={})", nID_HistoryEvent_Service, nID_Subject, sID_Order, nRate);
                historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);

                Long nID_SubjectMessageType = 1l;
                SubjectMessage oSubjectMessage_Rate = oSubjectMessageService.createSubjectMessage(
                        sMessageHead(nID_SubjectMessageType, sID_Order),
                        "Оцінка " + sID_Rate + " (по шкалі від 2 до 5)", nID_Subject, "", "", "sID_Rate=" + sID_Rate,
                        nID_SubjectMessageType);
                if (nID_HistoryEvent_Service != null) {
                    oSubjectMessage_Rate.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
                }
                subjectMessagesDao.setMessage(oSubjectMessage_Rate);
            }

            //сохранения сообщения с рейтингом, а на ррегиональном сервере, т.к. именно там хранится экземпляр БП.
            if (oHistoryEvent_Service.getnID_Proccess_Feedback() != null) {//issue 1006
                String snID_Process = "" + oHistoryEvent_Service.getnID_Proccess_Feedback();
                Integer nID_Server = oHistoryEvent_Service.getnID_Server();
                LOG.info("set rate={} to the nID_Proccess_Feedback={}", nRate, snID_Process);
                List<String> aTaskIds = bpService.getProcessTasks(nID_Server, snID_Process);
                LOG.info("Found '{}' tasks by nID_Proccess_Feedback...", aTaskIds.size());
                if (!aTaskIds.isEmpty()) {//when process is not complete
                    bpService.setVariableToProcessInstance(nID_Server, snID_Process, "nID_Rate", nRate);
                    LOG.info("process is not complete -- change rate in it!");
                    for (String sTaskId : aTaskIds) {
                        bpService.setVariableToActivitiTask(nID_Server, sTaskId, "nID_Rate", nRate);
                    }
                }
            }
            String sURL_Redirect = generalConfig.sHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
            LOG.info("Redirecting to URL:{}", sURL_Redirect);
            oResponse.sendRedirect(sURL_Redirect);

        } catch (CommonServiceException oActivitiRestException) {
            LOG.error("FAIL: {}", oActivitiRestException.getMessage());
            throw oActivitiRestException;
        } catch (Exception e) {
            LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(404, "[setMessageRate](sID_Order: " + sID_Order + ", nRate: " + nRate + "): Unknown exception: " + e.getMessage());
        }

        //subjectMessagesDao.setMessage(oSubjectMessage_Rate);
        /*String sToken = RandomStringUtils.randomAlphanumeric(15);
         try {
         HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
         nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
         nID_Subject = oHistoryEvent_Service.getnID_Subject();
         oHistoryEvent_Service.setsToken(sToken);
         historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
         } catch (Exception e) {
         LOG.error("Error occured while saving sID_Order in subject message for feedback.", e);;
         }*/
        /*Long nID_SubjectMessageType = 0l;
         SubjectMessage oSubjectMessage_Rate
         = createSubjectMessage(
         sMessageHead(nID_SubjectMessageType, sID_Order),
         "Оцінка " + sID_Rate + " (по шкалі від 2 до 5)"
         , nID_Subject, "", "", "sID_Rate=" + sID_Rate, nID_SubjectMessageType);
         if(nID_HistoryEvent_Service!=null){
         oSubjectMessage_Rate.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
         }
         subjectMessagesDao.setMessage(oSubjectMessage_Rate);*/
        //setServiceRate(sID_Order, sID_Rate);
        // storing message for feedback
        /*try {
         String sURL_Redirect = generalConfig.sHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
         LOG.info("Redirecting to URL:" + sURL_Redirect);
         oResponse.sendRedirect(sURL_Redirect);
         } catch (Exception e) {
         LOG.error("Error occured while saving subject message for feedback.", e);;
         }*/
        return sReturn;//"Ok!";
    }

    
    
    
    @ApiOperation(value = "/setMessageFeedback_Indirectly", notes = "##### SubjectMessageController - Сообщения субьектов. нет описания #####\n\n")
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
    
    
    /**
     * получение массива сообщений
     */
    @ApiOperation(value = "Получение массива сообщений ", notes = "##### SubjectMessageController - Сообщения субьектов. Получение массива сообщений #####\n\n"
            + "HTTP Context: http://server:port/wf/service/subject/message/getMessages\n\n\n"
            + "Примеры:\n"
            + "https://test.igov.org.ua/wf/service/subject/message/getMessages\n\n"
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
     * @param sID_Order Строка-ИД заявки
     * @param nID_Subject
     * @return array of messages by sID_Order
     */
    @ApiOperation(value = "Получение массива сообщений по услуге", notes = "##### SubjectMessageController - Сообщения субьектов. Получение массива сообщений по услуге #####\n\n")
    @RequestMapping(value = "/getServiceMessages", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public
    @ResponseBody
    ResponseEntity getServiceMessages(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Строка-Token", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            @ApiParam(value = "Номер-ИД субьекта (владельца заявки)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject
    ) throws CommonServiceException {
        Long nID_HistoryEvent_Service;
        //Long nID_Subject = null;
        //SubjectMessage oSubjectMessage = null;
        List<SubjectMessage> aSubjectMessage;
        try {
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            
            if(bAuth){
                actionEventService.checkAuth(oHistoryEvent_Service, nID_Subject, sToken);
            }
            
            /*if(sToken!=null){
                if(sToken.equals(oHistoryEvent_Service.getsToken())){
                    nID_Subject = oHistoryEvent_Service.getnID_Subject();
                }
            }
            if(nID_Subject!=null && !Objects.equals(nID_Subject, oHistoryEvent_Service.getnID_Subject())){
                if(sToken!=null){
                    LOG.warn("nID_Subject is not owner of Order of messages and wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(),sToken);
                    throw new Exception("nID_Subject is not Equal and wrong sToken!");
                }else{
                    LOG.warn("nID_Subject is not owner of Order of messages! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={})", nID_Subject, oHistoryEvent_Service.getnID_Subject());
                    throw new Exception("nID_Subject is not Equal!");
                }
            }*/
            
            /*//nID_Subject = oHistoryEvent_Service.getnID_Subject();
            if(nID_Subject!=null && !Objects.equals(nID_Subject, oHistoryEvent_Service.getnID_Subject())){
                LOG.warn("nID_Subject is not owner of Order of messages! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={})", nID_Subject, oHistoryEvent_Service.getnID_Subject());
                throw new Exception("nID_Subject is not Equal!");
            }*/
            
//            historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);

            /*String sHead = "";
             if (nID_SubjectMessageType == 4l){
             sHead = "Введений коментар клієнта по заяві " + sID_Order;
             }*/

            /*oSubjectMessage
             = createSubjectMessage(sHead,sBody, nID_Subject, "", "", "", nID_SubjectMessageType);
             oSubjectMessage.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
             subjectMessagesDao.setMessage(oSubjectMessage);*/
            aSubjectMessage = subjectMessagesDao.getMessages(nID_HistoryEvent_Service);

        } catch (Exception e) {
            LOG.error("FAIL: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(500, "[setServiceMessage]{sID_Order=" + sID_Order + "}:" + e.getMessage());
        }
        return JsonRestUtils.toJsonResponse(aSubjectMessage);
    }

    /**
     * Сохранение сообщения по услуге
     *
     * @param sID_Order              Строка-ИД заявки
     * @param sBody                  Строка-тело сообщения
     * @param nID_SubjectMessageType ИД-номер типа сообщения //опционально (по
     *                               умолчанию == 0)
     */
    @SuppressWarnings("unchecked")
	@ApiOperation(value = "Сохранение сообщения по услуге", notes = "")
    @RequestMapping(value = "/setServiceMessage", method = { RequestMethod.POST, RequestMethod.GET })
    public
    @ResponseBody
    ResponseEntity setServiceMessage(
            @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "Номер-ИД субьекта (хозяина заявки сообщения)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "Строка-Token", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "Строка-тело сообщения", required = true) @RequestParam(value = "sBody", required = true) String sBody,
            @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
            @ApiParam(value = "Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            @ApiParam(value = "Ключ записи redis", required = false) @RequestParam(value = "sID_File", required = false) String sID_File,
            @ApiParam(value = "Название файла", required = false) @RequestParam(value = "sFileName", required = false) String sFileName,
            @ApiParam(value = "ИД-номер типа сообщения", required = true) @RequestParam(value = "nID_SubjectMessageType", required = true) Long nID_SubjectMessageType
            //,//, defaultValue = "4"
    ) throws CommonServiceException {

        Long nID_HistoryEvent_Service;
        SubjectMessage oSubjectMessage;
        try {
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            //nID_Subject = oHistoryEvent_Service.getnID_Subject();
            
            if(bAuth){
                actionEventService.checkAuth(oHistoryEvent_Service, nID_Subject, sToken);
            }
            
            /*if(sToken!=null){
                if(sToken.equals(oHistoryEvent_Service.getsToken())){
                    nID_Subject = oHistoryEvent_Service.getnID_Subject();
                }
            }
            if(nID_Subject!=null && !Objects.equals(nID_Subject, oHistoryEvent_Service.getnID_Subject())){
                if(sToken!=null){
                    LOG.warn("nID_Subject is not owner of Order of messages and wrong sToken! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={},sToken={})", nID_Subject, oHistoryEvent_Service.getnID_Subject(),sToken);
                    throw new Exception("nID_Subject is not Equal and wrong sToken!");
                }else{
                    LOG.warn("nID_Subject is not owner of Order of messages! (nID_Subject={},oHistoryEvent_Service.getnID_Subject()={})", nID_Subject, oHistoryEvent_Service.getnID_Subject());
                    throw new Exception("nID_Subject is not Equal!");
                }
//            }*/
            
            if (StringUtils.isNotBlank(sID_File)){
            	LOG.info("sID_File param is not null {}. File name is {}", sID_File, sFileName);
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
                                FileServiceIOException.Error.REDIS_ERROR, "oByteArrayMultipartFile==null! sID_File="+sID_File);
                    }
                } catch (RecordInmemoryException e) {
                    LOG.warn("Error: {}", e.getMessage(), e);
                    throw new FileServiceIOException(
                            FileServiceIOException.Error.REDIS_ERROR, e.getMessage());
                } catch (ClassNotFoundException | IOException e) {
                    LOG.error("Error: {}", e.getMessage(), e);
                    throw new ActivitiException(e.getMessage(), e);
                }
                String sKey = accessDataDao.setAccessData(aByte_FileContent);   //accessDataService
                LOG.info("Saved to Mongo! (sKey={},aByte_FileContent.length={})", sKey,aByte_FileContent.length);
                JSONArray oaFile = new JSONArray();
                JSONObject o = new JSONObject();
                o.put("sFileName", sFileName);//sID_File
                o.put("sKey", sKey);
                oaFile.put(o);
                sData = new JSONObject().put("aFile", oaFile).toString();                                
                LOG.info("sData={}", sData);
            }
            
            historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
            oSubjectMessage = oSubjectMessageService.createSubjectMessage(sMessageHead(nID_SubjectMessageType,
                    sID_Order), sBody, nID_Subject, "", "", sData, nID_SubjectMessageType);
            oSubjectMessage.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
            subjectMessagesDao.setMessage(oSubjectMessage);            

        } catch (Exception e) {
            LOG.error("FAIL: {} (sID_Order={})", e.getMessage(), sID_Order);
            LOG.trace("FAIL:", e);
            throw new CommonServiceException(500, "{sID_Order=" + sID_Order + "}:" + e.getMessage());
        }
        return JsonRestUtils.toJsonResponse(oSubjectMessage);
    }    

    

    @ApiOperation(value = "Получить сообщение-фидбек заявки", notes = "##### SubjectMessageController - Сообщения субьектов. Получить сообщения-фидбека заявки #####\n\n"
            + "HTTP Context: https://test.igov.org.ua/wf/service/subject/message/getMessageFeedbackExtended?sID_Order=XXX-XXXXXX&sToken=[TokenValue]*\n\n"
            + "получает сообщение-фидбека:\n\n\n"
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
    /**
     * Получение сообщение-фидбека заявки по следующим параметрам:
     *
     * @param sID_Order строка-ид события по услуге, формат XXX-XXXXXX, где
     * первая часть -- ид сервера, где расположена задача, вторая часть --
     * nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна
     * @param sToken токен, который сранивается со значением sToken из объекта
     * HistoryEvent_Service
     * @return json со значениями sDate, sHead, sID_Order
     * @throws CommonServiceException 404 ошибка и сообщение "Record Not Found" -
     * если запись не найдена 403 ошибка и сообщение "Security Error" - если не
     * совпадает токен
     */
    // (формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна)
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

                /*} else {
                 LOG.info("Skipping history event service " + oHistoryEvent_Service.getId() + " from processing as it contains wrong token: " + 
                 oHistoryEvent_Service.getsToken() + ":" + oHistoryEvent_Service.getsID_Order());
                 throw new CommonServiceException(
                 ExceptionCommonController.BUSINESS_ERROR_CODE,
                 "Security Error",
                 HttpStatus.FORBIDDEN);
                 }*/
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

    @ApiOperation(value = "Сохранить сообщение-фидбек заявки", notes = "##### SubjectMessageController - Сообщения субьектов. Сохранить сообщения-фидбека заявки #####\n\n"
            + "HTTP Context: https://test.igov.org.ua/wf/service/subject/message/setMessageFeedbackExtended?sID_Order=XXX-XXXXXX&sToken=[TokenValue]*\n\n"
            + "сохраняет сообщение-фидбек\n\n\n"
            + "Если запись успешно добавлена/обновлена то устанавливается sToken='' и sDate устанавливается в текущую.\n"
            + "Если запись найдена и sBody<>'', то возвращается статус 403 и сообщение \"Already exists\"\n"
            + "Если запись не найдена и sBody<>'', то возвращается 404 статус и сообщение \"Record Not Found\"\n"
            + "Если sToken<>'' и sToken<>null и sToken не совпадет с HistoryEvent_Service.sToken то возвращается 403 статус и сообщение \"Security Error\"")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Already exist (если sBody в SubjectMessage не пустое ) / Security Error (если не совпадает токен)"),
            @ApiResponse(code = 404, message = "Record not found")})
    @RequestMapping(value = "/setMessageFeedbackExtended", method = RequestMethod.POST)//Feedback
    /**
     * Сохранение сообщение-фидбека заявки
     *
     * @param sID_Order строка-ид события по услуге, формат XXX-XXXXXX, где
     * первая часть -- ид сервера, где расположена задача, вторая часть --
     * nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна
     * @param sToken токен, который сранивается со значением sToken из объекта
     * HistoryEvent_Service
     * @param sBody строка текста фидбэка
     *
     * @throws CommonServiceException 404 ошибка и сообщение "Record Not Found" -
     * если запись не найдена 403 ошибка и сообщение "Security Error" - если не
     * совпадает токен 403 ошибка и сообщение "Already exist" - если sBody в
     * SubjectMessage не пустое
     */
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
                    /*List<SubjectMessage> aSubjectMessage = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", oHistoryEvent_Service.getId());
                     if (aSubjectMessage != null && !aSubjectMessage.isEmpty()){
                     for (SubjectMessage oSubjectMessage : aSubjectMessage){
                     if (oSubjectMessage.getBody() != null && !oSubjectMessage.getBody().trim().isEmpty()){
                     LOG.warn("Body in Subject message does already exist");
                     throw new CommonServiceException(
                     ExceptionCommonController.BUSINESS_ERROR_CODE,
                     "Already exists",
                     HttpStatus.FORBIDDEN);
                     } else {
                     Optional<SubjectMessageType> subjectMessageType = subjectMessageTypeDao.findById(nID_SubjectMessageType);
		    					
                     oSubjectMessage.setDate(new DateTime());
                     oSubjectMessage.setBody(sBody);
                     if (subjectMessageType.isPresent()){
                     oSubjectMessage.setSubjectMessageType(subjectMessageType.get());
                     LOG.info("Set SubjectMessageType with ID = "+nID_SubjectMessageType);
                     }
                     subjectMessagesDao.saveOrUpdate(oSubjectMessage);
                     oHistoryEvent_Service.setsToken("");
                     historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
                     }
                     }
                     } else {*/
                    SubjectMessage oSubjectMessage_Feedback = oSubjectMessageService.createSubjectMessage(
                            sMessageHead(nID_SubjectMessageType, sID_Order), "", oHistoryEvent_Service.getnID_Subject(),
                            "", "", "", nID_SubjectMessageType);//2l
                    oSubjectMessage_Feedback.setnID_HistoryEvent_Service(oHistoryEvent_Service.getId());//nID_HistoryEvent_Service
                    subjectMessagesDao.setMessage(oSubjectMessage_Feedback);
                    LOG.info("No SubjectMessage records found, create new!");
                    oHistoryEvent_Service.setsToken("");
                    historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
                    /*throw new CommonServiceException(
                     ExceptionCommonController.BUSINESS_ERROR_CODE,
                     "Record Not Found",
                     HttpStatus.NOT_FOUND);*/
                    //}
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

    /** Центральный сервис получения контента файла
     * 
     *
     * @param sID_Order  Строка-ИД заявки     * 
     * @param nID_Message номер-ИД сообщения 
     * @throws CommonServiceException 
     */
    @ApiOperation(value = " Центральный сервис получения контента файла", notes = ""
            + "HTTP Context: https://test.igov.org.ua/wf/service/subject/message/getMessageFile?sID_Order=XXX-XXXXXX&nID_Message=[nID_Message]*\n\n"
            + "получает массив байт контента файла:\n\n\n"
            + "Если не найдена запись с сообщением nID_Message, то возвращать ошибку с текстом сообщения \"Record not found\"\n\n"
            + "Если не найден файл в монге, то возвращать ошибку с текстом сообщения \"Content not found\"\n\n"
            + "Логика связанная с параметром sID_Order \n\n"
            + " -задан, то сверять наличие привязанности записи сообщения SubjectMessage с записью HistoryEvent_Service \n\n"
            + " (по полю \"nID_HistoryEvent_Service\" в SubjectMessage) и значению поля sID_Order в связанной записи HistoryEvent_Service; \n\n"
            + " если значения sID_Order не совпадают - выдавать ошибку \"Alien order\" \n\n"
            + " если значения sID_Order не связаны - выдавать ошибку \"Order not found\" \n\n"
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
	    		if (StringUtils.isNotBlank(sID_Order)){
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
	    		if (StringUtils.isNotBlank(message.getData())){
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

}
