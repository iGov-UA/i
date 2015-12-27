package org.activiti.rest.controller;

import com.google.common.base.Optional;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.base.dao.EntityDao;
import org.wf.dp.dniprorada.base.util.JsonRestUtils;
import org.wf.dp.dniprorada.dao.HistoryEvent_ServiceDao;
import org.wf.dp.dniprorada.dao.SubjectMessagesDao;
import org.wf.dp.dniprorada.model.HistoryEvent_Service;
import org.wf.dp.dniprorada.model.SubjectMessage;
import org.wf.dp.dniprorada.model.SubjectMessageType;
import org.wf.dp.dniprorada.util.GeneralConfig;
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

@Controller
@Api(tags = { "ActivitiRestSubjectMessageController" }, description = "Работа с сообщениями")
@RequestMapping(value = "/messages")
public class ActivitiRestSubjectMessageController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestSubjectMessageController.class);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Подробные описания сервисов для документирования в Swagger
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String noteCODE= "\n```\n";    
    private static final String noteCODEJSON= "\n```json\n";    
    private static final String noteController = "##### Работа с сообщениями ";

    private static final String noteSetMessage = noteController + "Сохранение сообщение #####\n\n"
        + "HTTP Context: http://server:port/wf/service/messages/setMessage\n\n\n"
        + "- sHead - Строка-заглавие сообщения\n"
        + "- sBody - Строка-тело сообщения\n"
        + "- nID_Subject ИД-номер субьекта (автора) (добавляется в запрос автоматически после аутентификации пользователя)\n"
        + "- sMail - Строка электронного адреса автора\n"
        + "- sContacts - Строка контактов автора\n"
        + "- sData - Строка дополнительных данных автора\n"
        + "- nID_SubjectMessageType - ИД-номер типа сообщения (по умолчанию == 0)\n"
        + "- sID_Order -- строка-ид заявки\n"
        + "- nID_Protected - номер заявки, защищенный по алгоритму Луна\n"
        + "- nID_Server -- ид сервера, где расположена заявка (по умолчанию 0)\n"
        + "- sID_Rate -- оценка, сейчас должно содержать число от 1 до 5\n"
        + "- nID_SubjectMessageType: nID;sName;sDescription 0;ServiceNeed;Просьба добавить услугу 1;ServiceFeedback;Отзыв о услуге\n\n\n"
        + "При заданных параметрах sID_Order или nID_Protected с/без nID_Server и sID_Rate - обновляется поле nRate в записи сущности HistoryEvent_Service, которая находится по sID_Order или nID_Protected с/без nID_Server (подробнее тут, при этом приходящее значение из параметра sID_Rate должно содержать число от 1 до 5. т.е. возможные ошибки:\n\n"
        + "- nID_Protected некорректное -- ошибка 403. CRC Error, пишется в лог (т.е. сообщение все равно сохраняется)\n"
        + "- sID_Rate некорректное (не число или не в промежутке от 1 до 5) -- ошибка 403. Incorrect sID_Rate, пишется в лог\n"
        + "- запись заявки (по nID_Protected без последней цифры) не найдена -- ошибка 403. Record not found, пишется в лог проверить запись HistoryEvent_Service можно через сервис"
        + " \\sevices\\getHistoryEvent_Service?nID_Protected=xxx (link: 17. Работа с обьектами событий по услугам)\n\n"
        + "Примеры:\n"
        + "https://test.igov.org.ua/wf/service/messages/setMessage?sHead=name&sBody=body&sMail=a@a.a\n"
        + "Ответ: Status 200 если Ok\n";

    private static final String noteSetMessageFeedback = noteController + "нет описания #####\n\n";

    private static final String noteSetMessageRate = noteController + "нет описания  #####\n\n";

    private static final String noteGetMessageTest = noteController + "нет описания   #####\n\n";

    private static final String noteGetMessages = noteController + "Получение массива сообщений #####\n\n"
        + "HTTP Context: http://server:port/wf/service/messages/getMessages\n\n\n"
        + "Примеры:\n"
        + "https://test.igov.org.ua/wf/service/messages/getMessages\n\n"
        + "nID_Subject - ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)\n"
        + "Response:\n"
        + noteCODEJSON
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
        + noteCODE;

    private static final String noteGetMessage = noteController + "Получение сообщения #####\n\n"
        + "HTTP Context: http://server:port/wf/service/messages/getMessage\n\n\n"
        + "- nID - ИД-номер сообщения\n"
        + "Примеры: https://test.igov.org.ua/wf/service/messages/getMessage?nID=76\n\n"
        + noteCODEJSON
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
        + noteCODE;

    private static final String noteSetMessageFeedback_Indirectly = noteController + "нет описания #####\n\n";

    private static final String noteGetMessageFeedbackExtended = noteController + "Получить сообщения-фидбека заявки #####\n\n"
    		+ "HTTP Context: https://test.igov.org.ua/wf/service/messages/getMessageFeedbackExtended?sID_Order=XXX-XXXXXX&sToken=[TokenValue]*\n\n"
    		+ "получает сообщение-фидбека заявки по следующим параметрам:\n\n"
    		+ "- sID_Order, строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна\n"
    		+ "- sToken, токен, который сранивается со значением sToken из объекта HistoryEvent_Service\n"
    		+ "\n"
    		+ "Если объект не найден по sID_Order, то возвращается код 404 и сообщение \"Record Not Found\"\n"
    		+ "Если sToken<>'' и sToken<>null и sToken не совпадет с HistoryEvent_Service.sToken то возвращается 403 статус и сообщение \"Security Error\"\n"
    		+ "если в найденном обекте SubjectMessage sBody='', то sDate в результате возвращается как null\n"
    		+ "Пример:\n"
    		+ "https://test.igov.org.ua/wf/service/messages/getMessageFeedbackExtended?sID_Order=0-4446&sToken=TokenValue"
    		+ noteCODEJSON
    		+ "{\n"
    		+ "    \"sDate\":\"2015-11-10 23:23:59 001\",\n"
    		+ "    \"sHead\":\"Получение справки о доходах\",\n"
    		+ "    \"sID_Order\":\"0-4446\",\n"
    		+ "}\n"
    		+ noteCODE;
    
    private static final String noteSetMessageFeedbackExtended = noteController + "Сохранить сообщения-фидбека заявки #####\n\n"
    		+ "HTTP Context: https://test.igov.org.ua/wf/service/messages/setMessageFeedbackExtended?sID_Order=XXX-XXXXXX&sToken=[TokenValue]*\n\n"
    		+ "сохраняет сообщение-фидбек заявки по следующим параметрам:\n\n"
    		+ "- sID_Order - строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна\n"
    		+ "- sToken - токен, который сранивается со значением sToken из объекта HistoryEvent_Service\n"
    		+ "- sBody - строка фидбека \n"
    		+ "\n"
    		+ "Если запись успешно добавлена/обновлена то устанавливается sToken='' и sDate устанавливается в текущую.\n"
    		+ "Если запись найдена и sBody<>'', то возвращается статус 403 и сообщение \"Already exists\"\n"
    		+ "Если запись не найдена и sBody<>'', то возвращается 404 статус и сообщение \"Record Not Found\"\n"
    		+ "Если sToken<>'' и sToken<>null и sToken не совпадет с HistoryEvent_Service.sToken то возвращается 403 статус и сообщение \"Security Error\"";
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;
    @Autowired
    private SubjectMessagesDao subjectMessagesDao;
    @Autowired
    @Qualifier("subjectMessageTypeDao")
    private EntityDao<SubjectMessageType> subjectMessageTypeDao;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private GeneralConfig generalConfig;
    
    /**
     * Сохранение сообщения
     * @param sHead Строка-заглавие сообщения
     * @param sBody Строка-тело сообщения
     * @param sMail Строка электронного адреса автора //опционально
     * @param sContacts Строка контактов автора //опционально
     * @param sData Строка дополнительных данных автора //опционально
     * @param nID_SubjectMessageType ИД-номер типа сообщения  //опционально (по умолчанию == 0)
     */
    @ApiOperation(value = "Сохранение сообщение ", notes = noteSetMessage )
    @RequestMapping(value = "/setMessage", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity setMessage(
	    @ApiParam(value = "Строка-заглавие сообщения", required = true) @RequestParam(value = "sHead") String sHead,
	    @ApiParam(value = "Строка-тело сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody,
	    @ApiParam(value = "ИД-номер субьекта (автора) (добавляется в запрос автоматически после аутентификации пользователя)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
	    @ApiParam(value = "Строка электронного адреса автора", required = false) @RequestParam(value = "sMail", required = false) String sMail,
	    @ApiParam(value = "Строка контактов автора", required = false) @RequestParam(value = "sContacts", required = false) String sContacts,
	    @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
	    @ApiParam(value = "ИД-номер типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType //,
            ) throws ActivitiRestException {

        SubjectMessage message
                = createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        return JsonRestUtils.toJsonResponse(message);
    }

    /**
     * Сохранение сообщения оценки
     * @param sID_Order Строка-ИД заявки
     * @param sID_Rate Строка-ИД Рнйтинга/оценки (число от 1 до 5)
     * @param nID_Protected Номер-ИД заявки, защищенный по алгоритму Луна, опционально(для обратной совместимости)
     * @param oResponse
     * @return
     * @throws ActivitiRestException
     */
    @ApiOperation(value = "/setMessageRate", notes = noteSetMessageRate )
    @RequestMapping(value = "/setMessageRate", method = RequestMethod.GET)//Rate
    public @ResponseBody
    String setMessageRate(
	    @ApiParam(value = "Строка-ИД заявки", required = true) @RequestParam(value = "sID_Order", required = false) String sID_Order,
	    @ApiParam(value = "Строка-ИД Рнйтинга/оценки (число от 1 до 5)", required = true) @RequestParam(value = "sID_Rate", required = false) String sID_Rate,
 	     @ApiParam(value = "Номер-ИД заявки, защищенный по алгоритму Луна, опционально(для обратной совместимости)", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            HttpServletResponse oResponse) throws ActivitiRestException {

        if(sID_Order==null){
            if(nID_Protected==null){
                LOG.error("[setMessageRate]:sID_Order=null and nID_Protected=null");
            }else{
                LOG.warn("[setMessageRate]:sID_Order=null and nID_Protected="+nID_Protected);
                sID_Order = "0-"+nID_Protected;
            }
        }
        String sToken = RandomStringUtils.randomAlphanumeric(15);
        Long nID_HistoryEvent_Service = null;
        Long nID_Subject = null;
        try {
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            nID_Subject = oHistoryEvent_Service.getnID_Subject();
            oHistoryEvent_Service.setsToken(sToken);
            historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
        } catch (Exception e) {
                LOG.error("[setMessageRate]:Error occured while saving sID_Order in subject message for feedback.", e);;
        }
        
        SubjectMessage oSubjectMessage_Rate
                = createSubjectMessage(
                        "Оцінка о відпрацованій послузі по заяві " + sID_Order,
                        "Послузі надана оцінка " + sID_Rate
                        , nID_Subject, "", "", "sID_Rate=" + sID_Rate, 0l);
        if(nID_HistoryEvent_Service!=null){
            oSubjectMessage_Rate.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
        }
        subjectMessagesDao.setMessage(oSubjectMessage_Rate);
        //oSubjectMessage_Rate = subjectMessagesDao.getMessage(oSubjectMessage_Rate.getId());
        setServiceRate(sID_Order, sID_Rate);

        // storing message for feedback
        try {
            //HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
            //Long nID_HistoryEvent_Service = oHistoryEvent_Service.getId();
            //String sToken = RandomStringUtils.randomAlphanumeric(15);
            //oHistoryEvent_Service.setsToken(sToken);
            /*
            SubjectMessage oSubjectMessage_Feedback
                            = createSubjectMessage("Відгук о відпрацованій послузі по заяві " + sID_Order
                                    , ""
                                    , nID_Subject, "", "", "", 2l);
            oSubjectMessage_Feedback.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
            subjectMessagesDao.setMessage(oSubjectMessage_Feedback);
            
            //historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
            LOG.info("[setMessageRate]:Creating subject message for feedback. Linked it with HistoryEvent_Service:" + nID_HistoryEvent_Service);
            */
            String sURL_Redirect = generalConfig.sHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
            LOG.info("[setMessageRate]:Redirecting to URL:" + sURL_Redirect);
            oResponse.sendRedirect(sURL_Redirect);
        } catch (Exception e) {
            LOG.error("[setMessageRate]:Error occured while saving subject message for feedback.", e);;
        }
        return "Ok!";
    }

    /**
     * получение массива сообщений
     */
    @ApiOperation(value = "Получение массива сообщений ", notes = noteGetMessages )
    @RequestMapping(value = "/getMessages", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public @ResponseBody
    ResponseEntity getMessages() {

        List<SubjectMessage> messages = subjectMessagesDao.getMessages();
        return JsonRestUtils.toJsonResponse(messages);
    }

    /**
     * получение сообщения
     * @param nID ID сообщения
     */
    @ApiOperation(value = "Получение сообщения", notes = noteGetMessage )
    @RequestMapping(value = "/getMessage", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public @ResponseBody
    ResponseEntity getMessage(
	    @ApiParam(value = "", required = true) @RequestParam(value = "nID") Long nID) {

        SubjectMessage message = subjectMessagesDao.getMessage(nID);
        return JsonRestUtils.toJsonResponse(message);
    }

    @ApiOperation(value = "/setMessageFeedback_Indirectly", notes = noteSetMessageFeedback_Indirectly )
    @RequestMapping(value = "/setMessageFeedback_Indirectly", method = RequestMethod.GET)
    public @ResponseBody
    String setMessageFeedback_Indirectly(
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID_Protected", required = true) Long nID_Protected,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "nID_Proccess_Feedback", required = true) String nID_Proccess_Feedback,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "sBody_Indirectly", required = true) String sBody_Indirectly,
	    @ApiParam(value = "нет описания", required = true) @RequestParam(value = "sID_Rate_Indirectly", required = true) String sID_Rate_Indirectly,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server) throws ActivitiRestException {

        Optional<HistoryEvent_Service> eventServiceOptional = historyEventServiceDao.findBy("nID_Proccess_Feedback", Long.valueOf(nID_Proccess_Feedback));
        if (eventServiceOptional.isPresent()) {
            HistoryEvent_Service historyEventService = eventServiceOptional.get();
            if (historyEventService != null) {
                historyEventService.setsID_Rate_Indirectly(sID_Rate_Indirectly);
                historyEventServiceDao.saveOrUpdate(historyEventService);
                LOG.info("Successfully updated historyEvent_Service with the rate " + sID_Rate_Indirectly);
            }
        } else {
            LOG.error("Didn't find event service");
            return "Ok";
        }
        LOG.error("Finished execution");
        return "Ok";
    }

    private SubjectMessage createSubjectMessage(String sHead, String sBody, Long nID_subject, String sMail,
            String sContacts, String sData, Long nID_subjectMessageType) {
        SubjectMessage message = new SubjectMessage();
        message.setHead(sHead);
        message.setBody(sBody == null ? "" : sBody);
        message.setId_subject((nID_subject == null) ? 0 : nID_subject);
        message.setMail((sMail == null) ? "" : sMail);
        message.setContacts((sContacts == null) ? "" : sContacts);
        message.setData((sData == null) ? "" : sData);
        message.setDate(new DateTime());
        if (nID_subjectMessageType != null) {
            SubjectMessageType subjectMessageType = subjectMessageTypeDao.findByIdExpected(nID_subjectMessageType);
            message.setSubjectMessageType(subjectMessageType);
        }
        return message;
    }

    //private void checkRate(String sID_Order, Long nID_Protected, Integer nID_Server, String sID_Rate)
    private void setServiceRate(String sID_Order, String sID_Rate)
            throws ActivitiRestException {

        //if (nID_Protected == null && sID_Order == null && nID_Server == null && sID_Rate == null) {
        if (sID_Order == null || sID_Rate == null) {
            LOG.warn("[setServiceRate]:Parametr(s) is absant! {sID_Order}, {sID_Rate}", sID_Order, sID_Rate);
            throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It isn't number.");
            //return;
        }
        if (sID_Order.contains("-")) {
            LOG.warn("[setServiceRate]:Incorrect parametr! {sID_Order}", sID_Order);
            throw new ActivitiRestException(404, "Incorrect parametr! {sID_Order="+sID_Order+"}");
        }
        
        if (sID_Rate != null && !sID_Rate.trim().equals("")) {
            Integer nRate;
            try {
                nRate = Integer.valueOf(sID_Rate);
            } catch (NumberFormatException ex) {
                LOG.warn("[setServiceRate]:incorrect param sID_Rate (not a number): " + sID_Rate);
                throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It isn't number.");
            }
            if (nRate < 1 || nRate > 5) {
                LOG.warn("[setServiceRate]:incorrect param sID_Rate (not in range[1..5]): " + sID_Rate);
                throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It is too short or too long number");
            }
            try {
                HistoryEvent_Service oHistoryEvent_Service;
                LOG.info("[setServiceRate]:sID_Order: " + sID_Order + ", nRate: " + nRate);
                oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                oHistoryEvent_Service.setnRate(nRate);
                LOG.info(String.format("[setServiceRate]:set rate=%s to the task=%s, nID_Protected=%s", nRate,
                oHistoryEvent_Service.getnID_Task(), oHistoryEvent_Service.getnID_Protected()));
                historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
                if (oHistoryEvent_Service.getnID_Proccess_Feedback() != null) {//issue 1006
                    String snID_Process = "" + oHistoryEvent_Service.getnID_Proccess_Feedback();
                    LOG.info(String.format("[setServiceRate]:set rate=%s to the nID_Proccess_Feedback=%s", nRate, snID_Process));
                    List<Task> aTask = taskService.createTaskQuery().processInstanceId(snID_Process).list();
                    if (aTask.size() > 0) {//when process is not complete
                        runtimeService.setVariable(snID_Process, "nID_Rate", nRate);
                        LOG.info("[setServiceRate]:Found " + aTask.size() + " tasks by nID_Proccess_Feedback...");
                        for (Task oTask : aTask) {
                            LOG.info("[setServiceRate]:oTask;getName=" + oTask.getName() + "|getDescription=" + oTask.getDescription() + "|getId=" + oTask.getId());
                            taskService.setVariable(oTask.getId(), "nID_Rate", nRate);
                        }
                    }
                }
                LOG.info(String.format("[setServiceRate]:set rate=%s to the task=%s, nID_Protected=%s Success!",
                        nRate, oHistoryEvent_Service.getnID_Task(), oHistoryEvent_Service.getnID_Protected()));
            } catch (CRCInvalidException e) {
                LOG.error("[setServiceRate]:"+e.getMessage(), e);
            } catch (Exception e) {
                LOG.error("[setServiceRate]:ex!", e);
            }
        }
    }
    
    @ApiOperation(value = "Получить сообщение-фидбек заявки", notes = noteGetMessageFeedbackExtended )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Security Error (если не совпадает токен)"),
	    @ApiResponse(code = 404, message = "Record not found") } )
    @RequestMapping(value = "/getMessageFeedbackExtended", method = RequestMethod.GET)//Feedback
    /**
     * Получение сообщение-фидбека заявки по следующим параметрам:
     * @param sID_Order  строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна
     * @param sToken токен, который сранивается со значением sToken из объекта HistoryEvent_Service
     * @return json со значениями sDate, sHead, sID_Order
     * @throws ActivitiRestException 
     * 	404 ошибка и сообщение "Record Not Found" - если запись не найдена
     * 	403 ошибка и сообщение "Security Error" - если не совпадает токен
     */
    // (формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна)
    public @ResponseBody
    Map<String, Object> getMessageFeedbackExtended(
	    @ApiParam(value = "Строка-ИД заявки услуги", required = true) @RequestParam(value = "sID_Order") String sID_Order,
	    @ApiParam(value = "Строка-токен (защита от постороннего доступа)", required = true) @RequestParam(value = "sToken") String sToken,
	    @ApiParam(value = "Номер-ИД типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", defaultValue = "2") Long nID_SubjectMessageType
            ) throws ActivitiRestException {

		Map<String, Object> mReturn = new HashMap<String, Object>();

		try {
                    if ("".equals(sToken.trim())){
                        LOG.warn("[getMessageFeedbackExtended]:Wrong sToken: " + sToken);
                        throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
                    }
                    HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                    if (oHistoryEvent_Service != null){
	    		//if (oHistoryEvent_Service.getsToken() != null && oHistoryEvent_Service.getsToken().equals(sToken)){
		    		List<SubjectMessage> aSubjectMessage = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", oHistoryEvent_Service.getId());
                                SubjectMessage oSubjectMessage_Found = null;
		    		if (aSubjectMessage != null && aSubjectMessage.size() > 0){
		    			for (SubjectMessage oSubjectMessage : aSubjectMessage){
		    				if (Objects.equals(oSubjectMessage.getSubjectMessageType().getId(), nID_SubjectMessageType)){//2
                                                        oSubjectMessage_Found=oSubjectMessage;
		    				} else {
		    					LOG.info("Skipping subject message from processing as its ID is: " + oSubjectMessage.getSubjectMessageType().getId());
		    				}
		    			}
		    		} else {
		    			LOG.info("No SubjectMessage objects found with nID_HistoryEvent_Service:" + oHistoryEvent_Service.getId());
		    		}
                                mReturn.put("sID_Order", sID_Order);
                                if(oSubjectMessage_Found!=null){
                                    mReturn.put("sHead", oSubjectMessage_Found.getHead());
                                    if (oSubjectMessage_Found.getBody() != null && !"".equals(oSubjectMessage_Found.getBody().trim())){
                                            SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"); 
                                            mReturn.put("sDate", oSimpleDateFormat.format(oSubjectMessage_Found.getDate().toDate()));
                                    } else {
                                            mReturn.put("sDate", null);
                                    }
                                }else{
                                    mReturn.put("sHead", "Відгук о відпрацованій послузі по заяві " + sID_Order);
                                    mReturn.put("sDate", null);
                                }
                                return mReturn;
                                
	    		/*} else {
	    			LOG.info("Skipping history event service " + oHistoryEvent_Service.getId() + " from processing as it contains wrong token: " + 
	    						oHistoryEvent_Service.getsToken() + ":" + oHistoryEvent_Service.getsID_Order());
	    			throw new ActivitiRestException(
	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
	                        "Security Error",
	                        HttpStatus.FORBIDDEN);
	    		}*/
                    }else{
                        LOG.warn("[getMessageFeedbackExtended]:Skipping history event service, wrong sID_Order: " + sID_Order);
                        throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
                    }
		} catch (CRCInvalidException e) {
			LOG.error("[getMessageFeedbackExtended]:Error occurred while getting message feedback:" + e.getMessage());
		}
        
		throw new ActivitiRestException(
                ActivitiExceptionController.BUSINESS_ERROR_CODE,
                "Record Not Found",
                HttpStatus.NOT_FOUND);
    }
    
    @ApiOperation(value = "Сохранить сообщение-фидбек заявки", notes = noteSetMessageFeedbackExtended )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Already exist (если sBody в SubjectMessage не пустое ) / Security Error (если не совпадает токен)"),
	    @ApiResponse(code = 404, message = "Record not found") } )
    @RequestMapping(value = "/setMessageFeedbackExtended", method = RequestMethod.POST)//Feedback
    /**
     * Сохранение сообщение-фидбека заявки
     * @param sID_Order  строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна
     * @param sToken токен, который сранивается со значением sToken из объекта HistoryEvent_Service
     * @param sBody строка текста фидбэка
     * 
     * @throws ActivitiRestException 
     * 	404 ошибка и сообщение "Record Not Found" - если запись не найдена
     * 	403 ошибка и сообщение "Security Error" - если не совпадает токен
     *  403 ошибка и сообщение "Already exist" - если sBody в SubjectMessage не пустое 
     */
    public @ResponseBody
    String setMessageFeedbackExtended(
	    @ApiParam(value = "Строка-ИД заявки услуги", required = true) @RequestParam(value = "sID_Order") String sID_Order,
	    @ApiParam(value = "Строка-токен (защита от постороннего доступа)", required = true) @RequestParam(value = "sToken") String sToken,
	    @ApiParam(value = "Номер-ИД типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", defaultValue = "2") Long nID_SubjectMessageType,
	    @ApiParam(value = "строка текста фидбэка", required = true) @RequestParam(value = "sBody") String sBody) throws ActivitiRestException {

		try {
                    if ("".equals(sToken.trim())){
                        LOG.warn("[setMessageFeedbackExtended]:Wrong sToken: " + sToken);
                        throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
                    }
                    if (2l!=nID_SubjectMessageType){
                        LOG.warn("[setMessageFeedbackExtended]:Wrong nID_SubjectMessageType: " + nID_SubjectMessageType);
                        throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
                    }
                    HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);
                    if (oHistoryEvent_Service != null){
	    		if (oHistoryEvent_Service.getsToken() != null && oHistoryEvent_Service.getsToken().equals(sToken)){
		    		/*List<SubjectMessage> aSubjectMessage = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", oHistoryEvent_Service.getId());
		    		if (aSubjectMessage != null && aSubjectMessage.size() > 0){
		    			for (SubjectMessage oSubjectMessage : aSubjectMessage){
		    				if (oSubjectMessage.getBody() != null && !oSubjectMessage.getBody().trim().isEmpty()){
		    					LOG.warn("[setMessageFeedbackExtended]:Body in Subject message does already exist");
		    					throw new ActivitiRestException(
		    	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
		    	                        "Already exists",
		    	                        HttpStatus.FORBIDDEN);
		    				} else {
		    					Optional<SubjectMessageType> subjectMessageType = subjectMessageTypeDao.findById(nID_SubjectMessageType);
		    					
		    					oSubjectMessage.setDate(new DateTime());
		    					oSubjectMessage.setBody(sBody);
		    					if (subjectMessageType.isPresent()){
		    						oSubjectMessage.setSubjectMessageType(subjectMessageType.get());
		    						LOG.info("[setMessageFeedbackExtended]:Set SubjectMessageType with ID = "+nID_SubjectMessageType);
		    					}
		    					subjectMessagesDao.saveOrUpdate(oSubjectMessage);
		    					oHistoryEvent_Service.setsToken("");
		    					historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
		    				}
		    			}
		    		} else {*/
                                        SubjectMessage oSubjectMessage_Feedback
                                                        = createSubjectMessage("Відгук о відпрацованій послузі по заяві " + sID_Order
                                                                , ""
                                                                , oHistoryEvent_Service.getnID_Subject(), "", "", "", nID_SubjectMessageType);//2l
                                        oSubjectMessage_Feedback.setnID_HistoryEvent_Service(oHistoryEvent_Service.getId());//nID_HistoryEvent_Service
                                        subjectMessagesDao.setMessage(oSubjectMessage_Feedback);
		    			LOG.info("No SubjectMessage records found, create new!");
                                        oHistoryEvent_Service.setsToken("");
                                        historyEventServiceDao.saveOrUpdate(oHistoryEvent_Service);
    					/*throw new ActivitiRestException(
    	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
    	                        "Record Not Found",
    	                        HttpStatus.NOT_FOUND);*/
		    		//}
	    		} else {
	    			LOG.warn("[setMessageFeedbackExtended]:Skipping history event service from processing as it contains wrong token: " + oHistoryEvent_Service.getsToken());
	    			throw new ActivitiRestException(
	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
	                        "Security Error",
	                        HttpStatus.FORBIDDEN);
	    		}
                    }else{
                        LOG.warn("[setMessageFeedbackExtended]:Skipping history event service, wrong sID_Order: " + sID_Order);
                        throw new ActivitiRestException(
                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
                        "Security Error",
                        HttpStatus.FORBIDDEN);
                    }
		} catch (CRCInvalidException e) {
			LOG.error("[setMessageFeedbackExtended]:Error occurred while setting message feedback:" + e.getMessage());
		}
        
		return "Ok";
    }

}
