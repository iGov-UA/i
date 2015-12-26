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
     * сохранение сообщения
     * @param sHead Строка-заглавие сообщения
     * @param sBody Строка-тело сообщения
     * @param sMail Строка электронного адреса автора //опционально
     * @param sContacts Строка контактов автора //опционально
     * @param sData Строка дополнительных данных автора //опционально
     * @param nID_SubjectMessageType ИД-номер типа сообщения  //опционально (по умолчанию == 0)
     * @param sID_Order строка-ид заявки (опционально)
     * @param nID_Protected номер заявки, опционально, защищенный по алгоритму Луна
     * @param nID_Server ид сервера, где расположена заявка (опционально, по умолчанию 0)
     * @param sID_Rate оценка, опционально. сейчас должно содержать число от 1 до 5
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
	    @ApiParam(value = "ИД-номер типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
	    @ApiParam(value = "строка-ид заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
	    @ApiParam(value = "номер заявки, опционально, защищенный по алгоритму Луна", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
	    @ApiParam(value = "ид сервера, где расположена заявка (по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
	    @ApiParam(value = "оценка. сейчас должно содержать число от 1 до 5", required = false) @RequestParam(value = "sID_Rate", required = false) String sID_Rate) throws ActivitiRestException {

        SubjectMessage message
                = createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        checkRate(sID_Order, nID_Protected, nID_Server, sID_Rate);
        return JsonRestUtils.toJsonResponse(message);
    }

    @ApiOperation(value = "/setMessageFeedback", notes = noteSetMessageFeedback )
    @RequestMapping(value = "/setMessageFeedback", method = RequestMethod.POST)//Feedback
    public @ResponseBody
    String setMessageFeedback(
	    @ApiParam(value = "Строка-заглавие сообщения", required = true) @RequestParam(value = "sHead") String sHead,
	    @ApiParam(value = "Строка-тело сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "warnSignal", required = false) String sWarnSignal,
	    @ApiParam(value = "ИД-номер субьекта (автора) (добавляется в запрос автоматически после аутентификации пользователя)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
	    @ApiParam(value = "Строка электронного адреса автора", required = false) @RequestParam(value = "sMail", required = false) String sMail,
	    @ApiParam(value = "Строка контактов автора", required = false) @RequestParam(value = "sContacts", required = false) String sContacts,
	    @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
	    @ApiParam(value = "ИД-номер типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
	    @ApiParam(value = "строка-ид заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
	    @ApiParam(value = "номер заявки, опционально, защищенный по алгоритму Луна", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
	    @ApiParam(value = "ид сервера, где расположена заявка (по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
	    @ApiParam(value = "оценка. сейчас должно содержать число от 1 до 5", required = false) @RequestParam(value = "sID_Rate", required = false) String sID_Rate) throws ActivitiRestException {

        SubjectMessage message
                = createSubjectMessage(
                        sHead + (sID_Rate != null ? " (sID_Rate=" + sID_Rate + ")" : "") + ("on".equals(sWarnSignal)
                                ? " (anonymous)"
                                : ""), sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        checkRate(sID_Order, nID_Protected, nID_Server, sID_Rate);
        //return "Спасибо! Вы успешно отправили отзыв!";
        return "Ok!";
    }

    @ApiOperation(value = "/setMessageRate", notes = noteSetMessageRate )
    @RequestMapping(value = "/setMessageRate", method = RequestMethod.GET)//Rate
    public @ResponseBody
    String setMessageRate(
	    @ApiParam(value = "Строка-заглавие сообщения", required = true) @RequestParam(value = "sHead") String sHead,
	    @ApiParam(value = "Строка-тело сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody,
	    @ApiParam(value = "нет описания", required = false) @RequestParam(value = "warnSignal", required = false) String sWarnSignal,
	    @ApiParam(value = "ИД-номер субьекта (автора) (добавляется в запрос автоматически после аутентификации пользователя)", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
	    @ApiParam(value = "Строка электронного адреса автора", required = false) @RequestParam(value = "sMail", required = false) String sMail,
	    @ApiParam(value = "Строка контактов автора", required = false) @RequestParam(value = "sContacts", required = false) String sContacts,
	    @ApiParam(value = "Строка дополнительных данных автора", required = false) @RequestParam(value = "sData", required = false) String sData,
	    @ApiParam(value = "ИД-номер типа сообщения", required = false) @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
	    @ApiParam(value = "строка-ид заявки", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
	    @ApiParam(value = "номер заявки, опционально, защищенный по алгоритму Луна", required = false) @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
	    @ApiParam(value = "ид сервера, где расположена заявка (по умолчанию 0)", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
	    @ApiParam(value = "setMessageFeedback", required = false) @RequestParam(value = "sID_Rate", required = false) String sID_Rate,
            HttpServletResponse response) throws ActivitiRestException {

        SubjectMessage message
                = createSubjectMessage(
                        sHead + (sID_Rate != null ? " (sID_Rate=" + sID_Rate + ")" : "") + ("on".equals(sWarnSignal)
                                ? " (anonymous)"
                                : ""), sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        checkRate(sID_Order, nID_Protected, nID_Server, sID_Rate);

     // storing message for feedback
        HistoryEvent_Service historyEventService;
		try {
			historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
	        String sToken = RandomStringUtils.randomAlphanumeric(15);
	        historyEventService.setsToken(sToken);
	        SubjectMessage feedbackMessage
	        		= createSubjectMessage(
	        					"Оставить отзыв о отработанной заявке №" + nID_Protected, "", nID_Subject, 
	        						sMail, sContacts, sData, 1l);
	        feedbackMessage.setnID_HistoryEvent_Service(historyEventService.getId());
	        subjectMessagesDao.setMessage(feedbackMessage);
	        historyEventServiceDao.saveOrUpdate(historyEventService);
	        LOG.info("Creating subject message for feedback. Linked it with HistoryEvent_Service:" + historyEventService.getId());
	        
	        String urlToRedirect = generalConfig.sHostCentral() + "/feedback?sID_Order=" + sID_Order + "&sSecret=" + sToken;
	        LOG.info("Redirecting to URL:" + urlToRedirect);
	        response.sendRedirect(urlToRedirect);
		} catch (Exception e) {
			LOG.error("Error occured while saving subject message for feedback.", e);;
		}
        return "Ok!";
    }

    @ApiOperation(value = "/getMessageTest", notes = noteGetMessageTest )
    @RequestMapping(value = "/getMessageTest", method = RequestMethod.GET)
    public @ResponseBody
    String getMessageTest() {
        return "Test Проверка";
    }

    /**
     * получение массива сообщений
     //     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
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

    private void checkRate(String sID_Order, Long nID_Protected, Integer nID_Server, String sID_Rate)
            throws ActivitiRestException {

        if (nID_Protected == null && sID_Order == null && nID_Server == null && sID_Rate == null) {
            return;
        }
        if (sID_Rate != null && !sID_Rate.trim().equals("")) {
            Integer nRate;
            try {
                nRate = Integer.valueOf(sID_Rate);
            } catch (NumberFormatException ex) {
                LOG.warn("incorrect param sID_Rate (not a number): " + sID_Rate);
                throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It isn't number.");
            }
            if (nRate < 1 || nRate > 5) {
                LOG.warn("incorrect param sID_Rate (not in range[1..5]): " + sID_Rate);
                throw new ActivitiRestException(404, "Incorrect value of sID_Rate! It is too short or too long number");
            }
            try {
                HistoryEvent_Service historyEventService;
                if (sID_Order != null) {
                    String sID_Server = (sID_Order.contains("-")
                            ? ""
                            : (nID_Server != null ? ("" + nID_Server + "-") : "0-"));
                    sID_Order = sID_Server + sID_Order;
                    LOG.info("!!!sID_Order: " + sID_Order);
                    historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
                } else if (nID_Protected != null) {
                    LOG.info("!!!nID_Protected: " + nID_Protected + " nID_Server: " + nID_Server);
                    historyEventService = historyEventServiceDao.getOrgerByProtectedID(nID_Protected, nID_Server);
                    LOG.info("!!!historyEventService: " + (historyEventService != null ?
                            historyEventService.getId() :
                            null));
                } else {
                    LOG.warn("incorrect input data!! must be: [sID_Order] OR [nID_Protected + nID_Server (optional)]");
                    throw new ActivitiRestException(404, "Incorrect input data! must be: [sID_Order] OR [nID_Protected + nID_Server (optional)]");
                }
                LOG.info("!!!nRate: " + nRate);
                historyEventService.setnRate(nRate);
                LOG.info(String.format("set rate=%s to the task=%s, nID_Protected=%s", nRate,
                        historyEventService.getnID_Task(), historyEventService.getnID_Protected()));
                historyEventServiceDao.saveOrUpdate(historyEventService);
                if (historyEventService.getnID_Proccess_Feedback() != null) {//issue 1006
                    String processInstanceID = "" + historyEventService.getnID_Proccess_Feedback();
                    LOG.info(String.format("set rate=%s to the nID_Proccess_Feedback=%s", nRate, processInstanceID));
                    List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceID).list();
                    if (tasks.size() > 0) {//when process is not complete
                        runtimeService.setVariable(processInstanceID, "nID_Rate", nRate);
                        LOG.info("Found " + tasks.size() + " tasks by nID_Proccess_Feedback...");
                        for (Task task : tasks) {
                            LOG.info("task;" + task.getName() + "|" + task.getDescription() + "|" + task.getId());
                            taskService.setVariable(task.getId(), "nID_Rate", nRate);
                        }
                    }
                }
                LOG.info(String.format("set rate=%s to the task=%s, nID_Protected=%s Success!",
                        nRate, historyEventService.getnID_Task(), historyEventService.getnID_Protected()));
            } catch (CRCInvalidException e) {
                LOG.error(e.getMessage(), e);
            } catch (Exception e) {
                LOG.error("ex!", e);
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
    public @ResponseBody
    Map<String, Object> getMessageFeedbackExtended(
	    @ApiParam(value = "строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна", required = true) @RequestParam(value = "sID_Order") String sID_Order,
	    @ApiParam(value = "токен, который сранивается со значением sToken из объекта HistoryEvent_Service", required = true) @RequestParam(value = "sToken") String sToken) throws ActivitiRestException {

		Map<String, Object> res = new HashMap<String, Object>();

		try {
			HistoryEvent_Service historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
	    	if (historyEventService != null){
	    		if (historyEventService.getsToken() != null && historyEventService.getsToken().equals(sToken)){
		    		List<SubjectMessage> subjectMessages = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", historyEventService.getId());
		    		if (subjectMessages != null && subjectMessages.size() > 0){
		    			for (SubjectMessage subjectMessage : subjectMessages){
		    				if (subjectMessage.getSubjectMessageType().getId() == 1){
		    					res.put("sHead", subjectMessage.getHead());
		    					res.put("sID_Order", sID_Order);
		    					if (subjectMessage.getBody() != null && !"".equals(subjectMessage.getBody().trim())){
		    						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS"); 
		    						res.put("sDate", sdf.format(subjectMessage.getDate().toDate()));
		    					} else {
		    						res.put("sDate", null);
		    					}
		    					return res;
		    				} else {
		    					LOG.info("Skipping subject message from processing as its ID is: " + subjectMessage.getSubjectMessageType().getId());
		    				}
		    			}
		    		} else {
		    			LOG.info("No SubjectMessage objects found with nID_HistoryEvent_Service:" + historyEventService.getId());
		    		}
	    		} else {
	    			LOG.info("Skipping history event service " + historyEventService.getId() + " from processing as it contains wrong token: " + 
	    						historyEventService.getsToken() + ":" + historyEventService.getsID_Order());
	    			throw new ActivitiRestException(
	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
	                        "Security Error",
	                        HttpStatus.FORBIDDEN);
	    		}
	    	}
		} catch (CRCInvalidException e) {
			LOG.info("Error occurred while getting message feedback:" + e.getMessage());
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
	    @ApiParam(value = "строка-ид события по услуге, формат XXX-XXXXXX, где первая часть -- ид сервера, где расположена задача, вторая часть -- nID_Protected, т.е. ид задачи + контрольная сумма по алгоритму Луна", required = true) @RequestParam(value = "sID_Order") String sID_Order,
	    @ApiParam(value = "токен, который сранивается со значением sToken из объекта HistoryEvent_Service", required = true) @RequestParam(value = "sToken") String sToken,
	    @ApiParam(value = "строка текста фидбэка", required = true) @RequestParam(value = "sBody") String sBody) throws ActivitiRestException {

		try {
			HistoryEvent_Service historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
	    	if (historyEventService != null){
	    		if (historyEventService.getsToken() != null && historyEventService.getsToken().equals(sToken)){
		    		List<SubjectMessage> subjectMessages = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", historyEventService.getId());
		    		if (subjectMessages != null && subjectMessages.size() > 0){
		    			for (SubjectMessage subjectMessage : subjectMessages){
		    				if (subjectMessage.getBody() != null && !subjectMessage.getBody().isEmpty()){
		    					LOG.info("Body in Subject message does already exist");
		    					throw new ActivitiRestException(
		    	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
		    	                        "Already exists",
		    	                        HttpStatus.FORBIDDEN);
		    				} else {
		    					historyEventService.setsToken("");
		    					historyEventServiceDao.saveOrUpdate(historyEventService);
		    					
		    					Optional<SubjectMessageType> subjectMessageType = subjectMessageTypeDao.findById(Long.valueOf(1));
		    					
		    					subjectMessage.setDate(new DateTime());
		    					subjectMessage.setBody(sBody);
		    					if (subjectMessageType.isPresent()){
		    						subjectMessage.setSubjectMessageType(subjectMessageType.get());
		    						LOG.info("Set SubjectMessageType with ID = 1");
		    					}
		    					subjectMessagesDao.saveOrUpdate(subjectMessage);
		    				}
		    			}
		    		} else {
		    			LOG.info("No SubjectMessage records found");
    					throw new ActivitiRestException(
    	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
    	                        "Record Not Found",
    	                        HttpStatus.NOT_FOUND);
		    		}
	    		} else {
	    			LOG.info("Skipping history event service from processing as it contains wrong token: " + historyEventService.getsToken());
	    			throw new ActivitiRestException(
	                        ActivitiExceptionController.BUSINESS_ERROR_CODE,
	                        "Security Error",
	                        HttpStatus.FORBIDDEN);
	    		}
	    	}
		} catch (CRCInvalidException e) {
			LOG.info("Error occurred while setting message feedback:" + e.getMessage());
		}
        
		return "Ok";
    }

}
