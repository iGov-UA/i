package org.igov.service.controller;

import org.igov.service.exception.CommonServiceException;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.model.escalation.EscalationHistoryDao;
import org.igov.model.escalation.EscalationRuleDao;
import org.igov.model.escalation.EscalationRuleFunctionDao;
import org.igov.model.escalation.EscalationStatusDao;
import org.igov.model.escalation.EscalationHistory;
import org.igov.model.escalation.EscalationRule;
import org.igov.model.escalation.EscalationRuleFunction;
import org.igov.model.escalation.EscalationStatus;
import org.igov.service.business.escalation.EscalationService;
import org.igov.io.GeneralConfig;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@Api(tags = { "ActionEscalationController — Действия эскалаций" })
@RequestMapping(value = "/action/escalation")
public class ActionEscalationController {

    private static final Logger LOG = LoggerFactory.getLogger(ActionEscalationController.class);
    private static final String ERROR_CODE = "exception in escalation-controller!";
    
    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    private EscalationRuleFunctionDao escalationRuleFunctionDao;
    @Autowired
    private EscalationRuleDao escalationRuleDao;
    @Autowired
    private EscalationService oEscalationService;
    @Autowired
    private EscalationHistoryDao escalationHistoryDao;
    @Autowired
    private EscalationStatusDao escalationStatusDao;

    /**
     * запуск правила эскалации по его Ид
     * правило эскалации -- это запись с указанием бп и задачи, по которым следует отправлять уведомления
     * в случае "зависания", т.е. необработки задач чиновниками.
     *
     * @param nID - ид правила эскалации
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Запуск правила эскалации по его Ид ")
    @RequestMapping(value = "/runEscalationRule", method = RequestMethod.GET)
    @ResponseBody
    public void runEscalationRule( @ApiParam(value = "ид правила эскалации", required = true) @RequestParam(value = "nID") Long nID) throws CommonServiceException, Exception {
        oEscalationService.runEscalationRule(nID, generalConfig.sHost());
    }

    /**
     * запуск всех правил эскалаций
     * правило эскалации -- это запись с указанием бп и задачи, по которым следует отправлять уведомления
     * в случае "зависания", т.е. необработки задач чиновниками.
     *
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Запуск всех правил эскалаций " )
    @RequestMapping(value = "/runEscalation", method = RequestMethod.GET)
    @ResponseBody
    public void runEscalationAll() throws CommonServiceException {
        oEscalationService.runEscalationAll();
    }

    //----------EscalationRuleFunction services-----------------

    /**
     * добавление/обновление записи функции эскалации
     * если nID не задан, то это создание записи
     * если nID задан, но его нету -- будет ошибка "403. Record not found"
     * если nID задан, и он есть -- запись обновляется
     *
     * @param nID          — ИД-номер (уникальный-автоитерируемый), опционально
     * @param sName        — строка-название (Например "Отсылка уведомления на электронную почту"), обязательно
     * @param sBeanHandler — строка бина-обработчика, опционально
     * @return созданная/обновленная запись.
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Добавление/обновление записи функции эскалации")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Record not found")})
    @RequestMapping(value = "/setEscalationRuleFunction", method = RequestMethod.GET)
    @ResponseBody
    public EscalationRuleFunction setEscalationRuleFunction(
	    @ApiParam(value = "ИД-номер (уникальный-автоитерируемый):\n "
                    + "если nID не задан, то это создание записи;\n "
                    + "если nID задан, но его нету — будет ошибка;\n"
                    + "если nID задан, и он есть — запись обновляется.\n", required = false) @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "строка-название (Например \"Отсылка уведомления на электронную почту\")", required = true) @RequestParam(value = "sName") String sName,
	    @ApiParam(value = "строка бина-обработчика", required = false) @RequestParam(value = "sBeanHandler", required = false) String sBeanHandler)
            throws CommonServiceException {

        try {
            return escalationRuleFunctionDao.saveOrUpdate(nID, sName, sBeanHandler);
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }

    }

    /**
     * возврат одной записи функции эскалации по ее nID, если записи нету — "403. Record not found"
     *
     * @param nID — ИД функции эскалации
     * @return запись функции эскалации по ее nID, если записи нету — "403. Record not found"
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Возврат одной записи функции эскалации по ее nID ")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") })
    @RequestMapping(value = "/getEscalationRuleFunction", method = RequestMethod.GET)
    @ResponseBody
    public EscalationRuleFunction getEscalationRuleFunction(
	    @ApiParam(value = "ИД функции эскалации", required = true) @RequestParam(value = "nID") Long nID) throws CommonServiceException {

        EscalationRuleFunction ruleFunction = escalationRuleFunctionDao.findById(nID).orNull();
        if (ruleFunction == null) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Record not found. No such EscalationRuleFunction with nID=" + nID,
                    HttpStatus.FORBIDDEN);
        }
        return ruleFunction;
    }

    /**
     * выборка всех записей функции эскалации
     *
     * @return все записи функций эскалации
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Выборка всех записей функции эскалации" )
    @RequestMapping(value = "/getEscalationRuleFunctions", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationRuleFunction> getEscalationRuleFunctions()
            throws CommonServiceException {

        try {
            return escalationRuleFunctionDao.findAll();
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    /**
     * удаление записи функции эскалации по ее nID, если записи нету -- "403&#183; Record not found"
     *
     * @param nID — nID функции эскалации
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Удаление записи функции эскалации по ее nID" )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") })
    @RequestMapping(value = "/removeEscalationRuleFunction", method = RequestMethod.GET)
    @ResponseBody
    public void removeEscalationRuleFunction(
	    @ApiParam(value = "ИД функции эскалации", required = true) @RequestParam(value = "nID") Long nID) throws CommonServiceException {

        try {
            escalationRuleFunctionDao.delete(nID);
        } catch (EntityNotFoundException e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    e, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    //----------EscalationRule services-----------------

    /**
     * добавление/обновление записи правила эскалации
     * если nID не задан, то это создание записи
     * если nID задан, но его нету -- будет ошибка "403\. Record not found"
     * если nID задан, и он есть -- запись обновляется
     * ПРИМЕР: test.region.igov.org.ua/wf/service/action/escalation/setEscalationRule
     * ?sID_BP=zaporoshye_mvk-1a&sID_UserTask=*&sCondition=nElapsedDays==nDaysLimit
     * &soData={nDaysLimit:3,asRecipientMail:['test@email.com']}
     * &sPatternFile=escalation/escalation_template.html&nID_EscalationRuleFunction=1
     *
     * @param nID                        - ИД-номер (уникальный-автоитерируемый)
     * @param sID_BP                     - ИД-строка бизнес-процесса
     * @param sID_UserTask               - ИД-строка юзертаски бизнеспроцесса (если указана * -- то выбираются все задачи из бизнес-процесса)
     * @param sCondition                 - строка-условие (на языке javascript )
     * @param soData                     - строка-обьект, с данными (JSON-обьект)
     * @param sPatternFile               - строка файла-шаблона (примеры тут)
     * @param nID_EscalationRuleFunction - ИД-номер функции эскалации
     * @return созданная/обновленная запись.
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Добавление/обновление записи правила эскалации", notes = "##### \n"
            + "- если nID не задан, то это создание записи\n"
            + "- если nID задан, но его нету -- будет ошибка \"403. Record not found\"\n"
            + "- если nID задан, и он есть -- запись обновляется\n"
            + "ПРИМЕР:\n"
            + "\n```\n"
            + "https://test.region.igov.org.ua/wf/service/action/escalation/setEscalationRule?sID_BP=zaporoshye_mvk-1a&sID_UserTask=*&sCondition=nElapsedDays==nDaysLimit&soData={nDaysLimit:3,asRecipientMail:'test@email.com'}&sPatternFile=escalation/escalation_template.html&nID_EscalationRuleFunction=1\n\n"
            + "\n```\n"
            + "ОТВЕТ:\n"
            + "\n```json\n"
            + "  {\n"
            + "    \"sID_BP\":\"zaporoshye_mvk-1a\",\n"
            + "    \"sID_UserTask\":\"*\",\n"
            + "    \"sCondition\":\"nElapsedDays==nDaysLimit\",\n"
            + "    \"soData\":\"{nDaysLimit:3,asRecipientMail:[test@email.com]}\",\n"
            + "    \"sPatternFile\":\"escalation/escalation_template.html\",\n"
            + "    \"nID\":1008,\n"
            + "    \"nID_EscalationRuleFunction\":\n"
            + "    {\"sBeanHandler\":\"EscalationHandler_SendMailAlert\",\n"
            + "      \"nID\":1,\n"
            + "      \"sName\":\"Send Email\"\n"
            + "    }\n"
            + "  }\n"
            + "\n```\n" )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") })
    @RequestMapping(value = "/setEscalationRule", method = RequestMethod.GET)
    @ResponseBody
    public EscalationRule setEscalationRule(
	    @ApiParam(value = "ИД-номер (уникальный-автоитерируемый)", required = false)  @RequestParam(value = "nID", required = false) Long nID,
	    @ApiParam(value = "ИД-строка бизнес-процесса", required = true) @RequestParam(value = "sID_BP") String sID_BP,
	    @ApiParam(value = "ИД-строка юзертаски бизнеспроцесса (если указана * -- то выбираются все задачи из бизнес-процесса)", required = true) @RequestParam(value = "sID_UserTask") String sID_UserTask,
	    @ApiParam(value = "строка-условие (на языке javascript )", required = true) @RequestParam(value = "sCondition") String sCondition,
	    @ApiParam(value = "строка-обьект, с данными (JSON-обьект)", required = true) @RequestParam(value = "soData") String soData,
	    @ApiParam(value = "строка файла-шаблона (примеры тут)", required = true) @RequestParam(value = "sPatternFile") String sPatternFile,
	    @ApiParam(value = "ИД-номер функции эскалации", required = true) @RequestParam(value = "nID_EscalationRuleFunction") Long nID_EscalationRuleFunction)
            throws CommonServiceException {

        try {
            return oEscalationService.setEscalationRule(nID, sID_BP, sID_UserTask, sCondition, soData, sPatternFile,
                            nID_EscalationRuleFunction);
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    /**
     * возврат одной записи правила эскалации по ее nID, если записи нету -- "403. Record not found"
     *
     * @param nID - ИД правила эскалации
     * @return правило эскалации по ее nID, если записи нету -- "403. Record not found"
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Возврат одной записи правила эскалации по ее nID" )
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") })
    @RequestMapping(value = "/getEscalationRule", method = RequestMethod.GET)
    @ResponseBody
    public EscalationRule getEscalationRule(
	    @ApiParam(value = "ИД правила эскалации", required = true) @RequestParam(value = "nID") Long nID) throws CommonServiceException {

        EscalationRule rule = escalationRuleDao.findById(nID).orNull();
        if (rule == null) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    "Record not found. No such EscalationRule with nID=" + nID,
                    HttpStatus.FORBIDDEN);
        }
        return rule;
    }

    /**
     * возвращает список всех записей правил ескалации
     *
     * @return список всех записей правил ескалации
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Возвращает список всех записей правил эскалации" )
    @RequestMapping(value = "/getEscalationRules", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationRule> getEscalationRules() throws CommonServiceException {
        try {
            return escalationRuleDao.findAll();
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    /**
     * удаление записи правила эскалации по ее nID, если записи нету -- "403. Record not found"
     *
     * @param nID - ИД правила эскалации
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Удаление записи правила эскалации по ее nID")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Record not found") })
    @RequestMapping(value = "/removeEscalationRule", method = RequestMethod.GET)
    @ResponseBody
    public void removeEscalationRule(
	    @ApiParam(value = "ИД правила эскалации", required = true) @RequestParam(value = "nID") Long nID) throws CommonServiceException {

        try {
            escalationRuleDao.delete(nID);
        } catch (EntityNotFoundException e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    e, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    //------------------------------Escalation History services----------------------------------
    /**
     * Возвращает массив объектов сущности по заданним параметрам (но не больше 5000 записей)
     * Пример 1: https://test.igov.org.ua/wf/service/action/escalation/getEscalationHistory
     *
     * Пример ответа:
     * [{
     *      "sDate":"2015-09-09 21:20:25.000",
     *      "nID":1,
     *      "nID_Process":9463,
     *      "nID_Process_Root":29193,
     *      "nID_UserTask":894,
     *      "nID_EscalationStatus":91
     *  }
     *  ...
     * ]
     *
     * Пример 2: https://test.igov.org.ua/wf/service/action/escalation/getEscalationHistory?nID_Process=6276&nID_Process_Root=57119&nID_UserTask=634&sDateStart=2014-11-24%2000:03:00&sDateEnd=2014-12-26%2000:03:00&nRowsMax=100
     *
     * Пример ответа: записи, попадающие под критерии параметров в запросе
     *
     * @param nIdProcess     номер-ИД процесса //опциональный
     * @param nIdProcessRoot номер-ИД процесса (корневого) //опциональный
     * @param nIdUserTask    номер-ИД юзертаски //опциональный
     * @param sDateStart     строка дата начала выборки //опциональный, в формате YYYY-MM-DD hh:mm:ss
     * @param sDateEnd       строка дата конца выборки //опциональный, в формате YYYY-MM-DD hh:mm:ss
     * @param nRowsMax       максимальное число строк //опциональный, по умолчанию 100 (защита - не более 5000)
     * @return List<EscalationHistory>
     * @throws CommonServiceException
     */
    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Возвращает массив объектов сущности по заданним параметрам", notes = "##### "
	        + "Пример 1: https://test.igov.org.ua/wf/service/action/escalation/getEscalationHistory\n\n"
	        + "Пример ответа:\n\n"
	        + "\n```json\n"
	        + "  [{\n"
	        + "    \"sDate\":\"2015-09-09 21:20:25.000\",\n"
	        + "    \"nID\":1,\n"
	        + "    \"nID_Process\":9463,\n"
	        + "    \"nID_Process_Root\":29193,\n"
	        + "    \"nID_UserTask\":894,\n"
	        + "    \"nID_EscalationStatus\":91\n"
	        + "  }\n"
	        + "  ...\n"
	        + "  ]\n"
	        + "\n```\n"
	        + "Пример 2:\n https://test.igov.org.ua/wf/service/action/escalation/getEscalationHistory?nID_Process=6276&nID_Process_Root=57119&nID_UserTask=634&sDateStart=2014-11-24%2000:03:00&sDateEnd=2014-12-26%2000:03:00&nRowsMax=100" )
    @RequestMapping(value = "/getEscalationHistory", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationHistory> getEscalationHistory(
	    @ApiParam(value = "номер-ИД процесса", required = false) @RequestParam(value = "nID_Process", required = false) Long nIdProcess,
	    @ApiParam(value = "номер-ИД процесса (корневого)", required = false) @RequestParam(value = "nID_Process_Root", required = false) Long nIdProcessRoot,
	    @ApiParam(value = "номер-ИД юзертаски", required = false) @RequestParam(value = "nID_UserTask", required = false) Long nIdUserTask,
	    @ApiParam(value = "строка дата начала выборки", required = false) @RequestParam(value = "sDateStart", required = false) String sDateStart,
	    @ApiParam(value = "строка дата конца выборки", required = false) @RequestParam(value = "sDateEnd", required = false) String sDateEnd,
	    @ApiParam(value = "максимальное число строк, по умолчанию 100 (защита - не более 5000)", required = false) @RequestParam(value = "nRowsMax", required = false) Integer nRowsMax) throws CommonServiceException {
        try {
            DateTime startDate = null;
            DateTime endDate = null;
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            if (sDateStart != null) {
                startDate = formatter.parseDateTime(sDateStart);
            }
            if (sDateEnd != null) {
                endDate = formatter.parseDateTime(sDateEnd);
            }

            return escalationHistoryDao.getAllByCriteria(nIdProcess, nIdProcessRoot, nIdUserTask, startDate, endDate, nRowsMax);
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }
    }

    //------------------------------------Escalation Status services--------------------------------

    /**
     * Возвращает массив объектов сущности EscalationStatus
     * Пример: https://<server>/wf/service/action/escalation/getEscalationStatuses
     *
     * Пример ответа:
     * [
     *  {"sNote":"Отослано письмо","nID":1,"sID":"MailSent"},
     *  {"sNote":"БП создан","nID":2,"sID":"BP_Created"},
     *  {"sNote":"БП в процессе","nID":3,"sID":"BP_Process"},
     *  {"sNote":"БП закрыт","nID":4,"sID":"BP_Closed"}
     * ]
     *
     * @return List<EscalationStatus>
     * @throws CommonServiceException
     */
    @ApiOperation(value = "Возвращает массив объектов сущности EscalationStatus", notes = "##### "
            + "Пример: https://<server>/wf/service/action/escalation/getEscalationStatuses\n\n"
            + "Пример ответа:\n\n"
            + "\n```json\n"
            + "[\n"
            + "{\"sNote\":\"Отослано письмо\",\"nID\":1,\"sID\":\"MailSent\"},\n"
            + "{\"sNote\":\"БП создан\",\"nID\":2,\"sID\":\"BP_Created\"},\n"
            + "{\"sNote\":\"БП в процессе\",\"nID\":3,\"sID\":\"BP_Process\"},\n"
            + "{\"sNote\":\"БП закрыт\",\"nID\":4,\"sID\":\"BP_Closed\"}\n"
            + "]\n"
            + "\n```\n" )
    @RequestMapping(value = "/getEscalationStatuses", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationStatus> getEscalationStatuses() throws CommonServiceException {
        try {
            return escalationStatusDao.findAll();
        } catch (Exception e) {
            throw new CommonServiceException(ERROR_CODE, e);
        }


    }
}
