package org.activiti.rest.controller;

import org.apache.log4j.Logger;
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
import org.wf.dp.dniprorada.base.dao.EscalationHistoryDao;
import org.wf.dp.dniprorada.base.dao.EscalationRuleDao;
import org.wf.dp.dniprorada.base.dao.EscalationRuleFunctionDao;
import org.wf.dp.dniprorada.base.dao.EscalationStatusDao;
import org.wf.dp.dniprorada.base.model.EscalationHistory;
import org.wf.dp.dniprorada.base.model.EscalationRule;
import org.wf.dp.dniprorada.base.model.EscalationRuleFunction;
import org.wf.dp.dniprorada.base.model.EscalationStatus;
import org.wf.dp.dniprorada.base.service.escalation.EscalationService;
import org.wf.dp.dniprorada.util.GeneralConfig;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Controller
@RequestMapping(value = "/escalation")
public class ActivitiRestEscalationController {

    private static final Logger LOG = Logger.getLogger(ActivitiRestEscalationController.class);
    private static final String ERROR_CODE = "exception in escalation-controller!";

    @Autowired
    GeneralConfig generalConfig;
    @Autowired
    private EscalationRuleFunctionDao escalationRuleFunctionDao;
    @Autowired
    private EscalationRuleDao escalationRuleDao;
    @Autowired
    private EscalationService escalationService;
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
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/runEscalationRule", method = RequestMethod.GET)
    public
    @ResponseBody
    void runEscalationRule(@RequestParam(value = "nID") Long nID) throws ActivitiRestException {
        escalationService.runEscalationRule(nID, generalConfig.sHost());
    }

    /**
     * запуск всех правил эскалаций
     * правило эскалации -- это запись с указанием бп и задачи, по которым следует отправлять уведомления
     * в случае "зависания", т.е. необработки задач чиновниками.
     *
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/runEscalation", method = RequestMethod.GET)
    public
    @ResponseBody
    void runEscalationAll() throws ActivitiRestException {
        escalationService.runEscalationAll();
    }

    //----------EscalationRuleFunction services-----------------

    /**
     * добавление/обновление записи функции эскалации
     * если nID не задан, то это создание записи
     * если nID задан, но его нету -- будет ошибка "403. Record not found"
     * если nID задан, и он есть -- запись обновляется
     *
     * @param nID          -- ИД-номер (уникальный-автоитерируемый), опционально
     * @param sName        -- строка-название (Например "Отсылка уведомления на электронную почту"), обязательно
     * @param sBeanHandler -- строка бина-обработчика, опционально
     * @return созданная/обновленная запись.
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/setEscalationRuleFunction", method = RequestMethod.GET)
    public
    @ResponseBody
    EscalationRuleFunction setEscalationRuleFunction(
            @RequestParam(value = "nID", required = false) Long nID,
            @RequestParam(value = "sName") String sName,
            @RequestParam(value = "sBeanHandler", required = false) String sBeanHandler)
            throws ActivitiRestException {

        try {
            return escalationRuleFunctionDao.saveOrUpdate(nID, sName, sBeanHandler);
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }

    }

    /**
     * возврат одной записи функции эскалации по ее nID, если записи нету -- "403. Record not found"
     *
     * @param nID -- nID функции эскалации
     * @return запись функции эскалации по ее nID, если записи нету -- "403. Record not found"
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/getEscalationRuleFunction", method = RequestMethod.GET)
    public
    @ResponseBody
    EscalationRuleFunction getEscalationRuleFunction(
            @RequestParam(value = "nID") Long nID) throws ActivitiRestException {

        EscalationRuleFunction ruleFunction = escalationRuleFunctionDao.findById(nID).orNull();
        if (ruleFunction == null) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    "Record not found. No such EscalationRuleFunction with nID=" + nID,
                    HttpStatus.FORBIDDEN);
        }
        return ruleFunction;
    }

    /**
     * выборка всех записей функции эскалации
     *
     * @return все записи функций эскалации
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/getEscalationRuleFunctions", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EscalationRuleFunction> getEscalationRuleFunctions()
            throws ActivitiRestException {

        try {
            return escalationRuleFunctionDao.findAll();
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }
    }

    /**
     * удаление записи функции эскалации по ее nID, если записи нету -- "403&#183; Record not found"
     *
     * @param nID -- nID функции эскалации
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/removeEscalationRuleFunction", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeEscalationRuleFunction(
            @RequestParam(value = "nID") Long nID) throws ActivitiRestException {

        try {
            escalationRuleFunctionDao.delete(nID);
        } catch (EntityNotFoundException e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    e, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }
    }

    //----------EscalationRule services-----------------

    /**
     * добавление/обновление записи правила эскалации
     * если nID не задан, то это создание записи
     * если nID задан, но его нету -- будет ошибка "403\. Record not found"
     * если nID задан, и он есть -- запись обновляется
     * ПРИМЕР: test.region.igov.org.ua/wf/service/escalation/setEscalationRule
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
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/setEscalationRule", method = RequestMethod.GET)
    public
    @ResponseBody
    EscalationRule setEscalationRule(
            @RequestParam(value = "nID", required = false) Long nID,
            @RequestParam(value = "sID_BP") String sID_BP,
            @RequestParam(value = "sID_UserTask") String sID_UserTask,
            @RequestParam(value = "sCondition") String sCondition,
            @RequestParam(value = "soData") String soData,
            @RequestParam(value = "sPatternFile") String sPatternFile,
            @RequestParam(value = "nID_EscalationRuleFunction") Long nID_EscalationRuleFunction)
            throws ActivitiRestException {

        try {
            EscalationRuleFunction ruleFunction = null;
            if (nID_EscalationRuleFunction != null) {
                ruleFunction = escalationRuleFunctionDao.findById(nID_EscalationRuleFunction).orNull();
            }
            return escalationRuleDao.saveOrUpdate(nID, sID_BP, sID_UserTask,
                    sCondition, soData, sPatternFile, ruleFunction);
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }

    }

    /**
     * возврат одной записи правила эскалации по ее nID, если записи нету -- "403. Record not found"
     *
     * @param nID - nID правила эскалации
     * @return правило эскалации по ее nID, если записи нету -- "403. Record not found"
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/getEscalationRule", method = RequestMethod.GET)
    public
    @ResponseBody
    EscalationRule getEscalationRule(
            @RequestParam(value = "nID") Long nID) throws ActivitiRestException {

        EscalationRule rule = escalationRuleDao.findById(nID).orNull();
        if (rule == null) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    "Record not found. No such EscalationRule with nID=" + nID,
                    HttpStatus.FORBIDDEN);
        }
        return rule;
    }

    /**
     * возвращает список всех записей правил ескалации
     *
     * @return список всех записей правил ескалации
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/getEscalationRules", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EscalationRule> getEscalationRules() throws ActivitiRestException {
        try {
            return escalationRuleDao.findAll();
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }
    }

    /**
     * удаление записи правила эскалации по ее nID, если записи нету -- "403. Record not found"
     *
     * @param nID - nID правила эскалации
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/removeEscalationRule", method = RequestMethod.GET)
    public
    @ResponseBody
    void removeEscalationRule(
            @RequestParam(value = "nID") Long nID) throws ActivitiRestException {

        try {
            escalationRuleDao.delete(nID);
        } catch (EntityNotFoundException e) {
            throw new ActivitiRestException(
                    ActivitiExceptionController.BUSINESS_ERROR_CODE,
                    e.getMessage(),
                    e, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }
    }

    //------------------------------Escalation History services----------------------------------
    /**
     * Возвращает массив объектов сущности по заданним параметрам (но не больше 5000 записей)
     * Пример 1: https://test.igov.org.ua/wf/service/escalation/getEscalationHistory
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
     * Пример 2: https://test.igov.org.ua/wf/service/escalation/getEscalationHistory?nID_Process=6276&nID_Process_Root=57119&nID_UserTask=634&sDateStart=2014-11-24%2000:03:00&sDateEnd=2014-12-26%2000:03:00&nRowsMax=100
     *
     * Пример ответа: записи, попадающие под критерии параметров в запросе
     *
     * @param nIdProcess     номер-ИД процесса //опциональный
     * @param nIdProcessRoot номер-ИД процесса (корневого) //опциональный
     * @param nIdUserTask    номер-ИД юзертаски //опциональный
     * @param sDateStart     дата начала выборки //опциональный, в формате YYYY-MM-DD hh:mm:ss
     * @param sDateEnd       дата конца выборки //опциональный, в формате YYYY-MM-DD hh:mm:ss
     * @param nRowsMax       максимальное число строк //опциональный, по умолчанию 100 (защита - не более 5000)
     * @return List<EscalationHistory>
     * @throws ActivitiRestException
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getEscalationHistory", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationHistory> getEscalationHistory(@RequestParam(value = "nID_Process", required = false) Long nIdProcess,
                                                        @RequestParam(value = "nID_Process_Root", required = false) Long nIdProcessRoot,
                                                        @RequestParam(value = "nID_UserTask", required = false) Long nIdUserTask,
                                                        @RequestParam(value = "sDateStart", required = false) String sDateStart,
                                                        @RequestParam(value = "sDateEnd", required = false) String sDateEnd,
                                                        @RequestParam(value = "nRowsMax", required = false) Integer nRowsMax) throws ActivitiRestException {
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
            throw new ActivitiRestException(ERROR_CODE, e);
        }
    }

    //------------------------------------Escalation Status services--------------------------------

    /**
     * Возвращает массив объектов сущности EscalationStatus
     * Пример: https://<server>/wf/service/escalation/getEscalationStatuses
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
     * @throws ActivitiRestException
     */
    @RequestMapping(value = "/getEscalationStatuses", method = RequestMethod.GET)
    @ResponseBody
    public List<EscalationStatus> getEscalationStatuses() throws ActivitiRestException {
        try {
            return escalationStatusDao.findAll();
        } catch (Exception e) {
            throw new ActivitiRestException(ERROR_CODE, e);
        }


    }

}
