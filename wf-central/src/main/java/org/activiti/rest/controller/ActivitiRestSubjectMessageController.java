package org.activiti.rest.controller;

import com.google.common.base.Optional;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
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
import org.wf.dp.dniprorada.util.luna.CRCInvalidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/messages")
public class ActivitiRestSubjectMessageController {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiRestSubjectMessageController.class);

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
    @RequestMapping(value = "/setMessage", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity setMessage(
            @RequestParam(value = "sHead") String sHead,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @RequestParam(value = "sMail", required = false) String sMail,
            @RequestParam(value = "sContacts", required = false) String sContacts,
            @RequestParam(value = "sData", required = false) String sData,
            @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @RequestParam(value = "sID_Rate", required = false) String sID_Rate) throws ActivitiRestException {

        SubjectMessage message
                = createSubjectMessage(sHead, sBody, nID_Subject, sMail, sContacts, sData, nID_SubjectMessageType);
        subjectMessagesDao.setMessage(message);
        message = subjectMessagesDao.getMessage(message.getId());
        checkRate(sID_Order, nID_Protected, nID_Server, sID_Rate);
        return JsonRestUtils.toJsonResponse(message);
    }

    @RequestMapping(value = "/setMessageFeedback", method = RequestMethod.POST)//Feedback
    public @ResponseBody
    String setMessageFeedback(
            @RequestParam(value = "sHead") String sHead,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "warnSignal", required = false) String sWarnSignal,
            @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @RequestParam(value = "sMail", required = false) String sMail,
            @RequestParam(value = "sContacts", required = false) String sContacts,
            @RequestParam(value = "sData", required = false) String sData,
            @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @RequestParam(value = "sID_Rate", required = false) String sID_Rate) throws ActivitiRestException {

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

    @RequestMapping(value = "/setMessageRate", method = RequestMethod.GET)//Rate
    public @ResponseBody
    String setMessageRate(
            @RequestParam(value = "sHead") String sHead,
            @RequestParam(value = "sBody", required = false) String sBody,
            @RequestParam(value = "warnSignal", required = false) String sWarnSignal,
            @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @RequestParam(value = "sMail", required = false) String sMail,
            @RequestParam(value = "sContacts", required = false) String sContacts,
            @RequestParam(value = "sData", required = false) String sData,
            @RequestParam(value = "nID_SubjectMessageType", required = false) Long nID_SubjectMessageType,
            @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @RequestParam(value = "nID_Protected", required = false) Long nID_Protected,
            @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @RequestParam(value = "sID_Rate", required = false) String sID_Rate) throws ActivitiRestException {

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

    @RequestMapping(value = "/getMessageTest", method = RequestMethod.GET)
    public @ResponseBody
    String getMessageTest() {
        return "Test Проверка";
    }

    /**
     * получение массива сообщений
     //     * @param nID_Subject ID авторизированого субъекта (добавляется в запрос автоматически после аутентификации пользователя)
     */
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
    @RequestMapping(value = "/getMessage", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE, headers = {"Accept=application/json"})
    public @ResponseBody
    ResponseEntity getMessage(
            @RequestParam(value = "nID") Long nID) {

        SubjectMessage message = subjectMessagesDao.getMessage(nID);
        return JsonRestUtils.toJsonResponse(message);
    }

    @RequestMapping(value = "/setMessageFeedback_Indirectly", method = RequestMethod.GET)
    public @ResponseBody
    String setMessageFeedback_Indirectly(
            @RequestParam(value = "nID_Protected", required = true) Long nID_Protected,
            @RequestParam(value = "nID_Proccess_Feedback", required = true) String nID_Proccess_Feedback,
            @RequestParam(value = "sBody_Indirectly", required = true) String sBody_Indirectly,
            @RequestParam(value = "sID_Rate_Indirectly", required = true) String sID_Rate_Indirectly,
            @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server) throws ActivitiRestException {

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
    
    @RequestMapping(value = "/getMessageFeedbackExtended", method = RequestMethod.GET)//Feedback
    public @ResponseBody
    Map<String, Object> getMessageFeedbackExtended(
            @RequestParam(value = "sID_Order") String sID_Order,
            @RequestParam(value = "sToken") String sToken) throws ActivitiRestException {

		Map<String, Object> res = new HashMap<String, Object>();

		try {
			HistoryEvent_Service historyEventService = historyEventServiceDao.getOrgerByID(sID_Order);
	    	if (historyEventService != null){
	    		if (historyEventService.getsToken() != null && historyEventService.getsToken().equals(sToken)){
		    		List<SubjectMessage> subjectMessages = subjectMessagesDao.findAllBy("nID_HistoryEvent_Service", historyEventService.getId());
		    		if (subjectMessages != null){
		    			for (SubjectMessage subjectMessage : subjectMessages){
		    				if (subjectMessage.getSubjectMessageType().getId() == 1){
		    					res.put("sHead", subjectMessage.getHead());
		    					res.put("sID_Order", sID_Order);
		    					if (subjectMessage.getBody() != null){
		    						res.put("sDate", subjectMessage.getDate());
		    					} else {
		    						res.put("sDate", null);
		    					}
		    					return res;
		    				} else {
		    					LOG.info("Skipping subject message from processing as its ID is: " + subjectMessage.getSubjectMessageType().getId());
		    				}
		    			}
		    		}
	    		} else {
	    			LOG.info("Skipping history event service " + historyEventService.getId() + " from processing as it contains wrong token: " + historyEventService.getsToken());
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
    
    @RequestMapping(value = "/setMessageFeedbackExtended", method = RequestMethod.POST)//Feedback
    public @ResponseBody
    String setMessageFeedbackExtended(
            @RequestParam(value = "sID_Order") String sID_Order,
            @RequestParam(value = "sToken") String sToken,
            @RequestParam(value = "sBody") String sBody) throws ActivitiRestException {

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
		    	                        "Already exist",
		    	                        HttpStatus.FORBIDDEN);
		    				} else {
		    					subjectMessage.setBody(sBody);
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
