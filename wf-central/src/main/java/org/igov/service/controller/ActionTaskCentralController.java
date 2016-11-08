package org.igov.service.controller;

import com.google.common.base.Optional;
import io.swagger.annotations.*;
import org.igov.io.web.HttpEntityInsedeCover;
import org.igov.io.web.HttpRequester;
import org.igov.model.action.event.HistoryEvent_Service;
import org.igov.model.action.event.HistoryEvent_ServiceDao;
import org.igov.model.action.event.HistoryEvent_Service_StatusType;
import org.igov.model.subject.Server;
import org.igov.model.subject.ServerDao;
import org.igov.service.business.action.ActionEventService;
import org.igov.service.exception.CommonServiceException;
import org.igov.service.exception.RecordNotFoundException;
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

import java.util.HashMap;
import java.util.Map;
import org.igov.io.GeneralConfig;

@Controller
@Api(tags = { "ActionTaskCentralController -- Действия задачи центрально" })
@RequestMapping(value = "/action/task")
public class ActionTaskCentralController {

    private static final Logger LOG = LoggerFactory.getLogger(ActionTaskCentralController.class);

    @Autowired
    public GeneralConfig generalConfig;
    
    @Autowired
    HttpRequester httpRequester;
    @Autowired
    private HttpEntityInsedeCover oHttpEntityInsedeCover;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    private ActionEventService oActionEventService;
    @Autowired
    private HistoryEvent_ServiceDao historyEventServiceDao;

    @ApiOperation(value = "/setTaskAnswer_Central", notes = "Нет описания")
    @RequestMapping(value = "/setTaskAnswer_Central", method = RequestMethod.GET)
    public @ResponseBody
    void setTaskAnswer(
            @ApiParam(value = "строка-ид заявки", required = true) @RequestParam(value = "sID_Order", required = true) String sID_Order,
            @ApiParam(value = "строка-массива полей (например: \"[{'id':'sFamily','type':'string','value':'Белявцев'},{'id':'nAge','type':'long','value':35}]\")", required = true) @RequestParam(value = "saField", required = true) String saField,
            @ApiParam(value = "строка-токена. Данный параметр формируется и сохраняется в запись HistoryEvent_Service во время вызова метода setTaskQuestions", required  = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "номер-ИД субьекта", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "строка заголовка сообщения", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "булевый флаг. Включить авторизацию", required = false) @RequestParam(value = "bAuth", required = false, defaultValue = "false") Boolean bAuth,
            @ApiParam(value = "строка тела сообщения", required = false) @RequestParam(value = "sBody", required = false) String sBody
        ) throws CommonServiceException {

        try {
            LOG.info("/setTaskAnswer_Central started");
            HistoryEvent_Service oHistoryEvent_Service = historyEventServiceDao.getOrgerByID(sID_Order);

            if(bAuth){
                oActionEventService.checkAuth(oHistoryEvent_Service, nID_Subject, sToken);
            }
             
            LOG.info("Update history! (sID_Order={})", sID_Order);
            LOG.info("Look, arguments are sBody: "+sBody+" saField: "+saField);
            sBody = sBody != null ? sBody : "На заявку "+ sID_Order + " дана відповідь громадянином";
            LOG.info("Method /setTaskAnswer_Central calling updateActionStatus_Central");
            oActionEventService.updateActionStatus_Central(
                sID_Order,
                null,//sUserTaskName
                saField,//soData
                sToken,
                sBody,
                null,//nTimeMinutes
                null,//nID_Proccess_Feedback,
                null,//nID_Proccess_Escalation,
                HistoryEvent_Service_StatusType.OPENED_REMARK_CLIENT_ANSWER.getnID(), //nID_StatusType
                    null,//sSubjectInfo
                    null //nID_Subject
                   // null, //sDateCreate
                  // null //sDateClosed
            );
            String sHost = null;
            int dash_position = sID_Order.indexOf("-");
            int nID_Server = dash_position != -1 ? Integer.parseInt(sID_Order.substring(0, dash_position)) : 0;
            nID_Server = generalConfig.getServerId(nID_Server);
            Optional<Server> oOptionalServer = serverDao.findById(Long.valueOf(nID_Server + ""));
            if (!oOptionalServer.isPresent()) {
                throw new RecordNotFoundException();
            } else {//https://test.region.igov.org.ua/wf
                sHost = oOptionalServer.get().getsURL();
            }
            String sURL = sHost + "/service/action/task/setTaskAnswer";
            String processId = String.valueOf(oHistoryEvent_Service.getnID_Task());
            LOG.info("sURL={}", sURL);
            Map<String, String> mParam = new HashMap<String, String>();
            mParam.put("nID_Process", processId);
            mParam.put("saField", saField);
            LOG.info(" mParam={} ", mParam);
            String sReturnRegion = httpRequester.getInside(sURL, mParam);
            LOG.info("(sReturnRegion={})", sReturnRegion);

            String mergeUrl = sHost + "/service/action/task/mergeVariable";
            Map<String, String> mergeParams = new HashMap<String, String>();
            mergeParams.put("processInstanceId", processId);
            mergeParams.put("key", "saTaskStatus");
            mergeParams.put("insertValues", "GotAnswer");
            mergeParams.put("removeValues", "WaitAnswer");
            LOG.info("mergeParams={}, mergeUrl={}", mergeParams, mergeUrl);
            httpRequester.getInside(mergeUrl, mergeParams);

        } catch (Exception e) {
            throw new CommonServiceException(
                    ExceptionCommonController.BUSINESS_ERROR_CODE,
                    e.getMessage(), e, HttpStatus.FORBIDDEN);
        }
    }

    /*private void createSetTaskAnswerMessage(String sID_Order, String sBody, String saField, String jsonHistoryEvent) {
        Long nID_SubjectMessageType = 4L;
        JSONObject jsonObject = new JSONObject(jsonHistoryEvent);
        Long nID_HistoryEvent_Service = jsonObject.getLong("nID");
        Long nID_Subject = jsonObject.getLong("nID_Subject");
        SubjectMessage oSubjectMessage = subjectMessageService
                .createSubjectMessage(sMessageHead(nID_SubjectMessageType,
                        sID_Order), sBody, nID_Subject, "", "", saField, nID_SubjectMessageType);
        oSubjectMessage.setnID_HistoryEvent_Service(nID_HistoryEvent_Service);
        subjectMessagesDao.setMessage(oSubjectMessage);
    }*/

    @ApiOperation(value = "Получение полей стартовой формы по: ИД субьекта, ИД услуги, ИД места Услуги", notes = ""
            + "возвращает JSON содержащий поля стартовой формы процесса, процесс находится на основании ИД таски полученой из сущности HistoryEvent_Service. "
            + "На основании HistoryEvent_Service определяется региональный сервер откуда нужно получить поля формы и собственно ИД таски.\n\n"
            + "Примеры:\n"
            + "https://test.igov.org.ua/wf-central/service/action/task/getStartFormByTask_Central?nID_Subject=2&nID_Service=1&sID_UA=1200000000\n\n"
            + "Ответ, если запись существует (HTTP status Code: 200 OK):\n"
            + "\n```json\n"
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
            + "\n```\n"
            + "Ответ, если записи не существует. (HTTP status Code: 500 Internal Server Error):\n"
            + "\n```json\n"
            + "{\n"
            + "  \"code\": \"BUSINESS_ERR\",\n"
            + "  \"message\": \"Record not found\"\n"
            + "}"
            + "\n```\n")
    @ApiResponses(value = {
        @ApiResponse(code = 500, message = "Record not found")})
    @RequestMapping(value = "/getStartFormByTask_Central", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String getStartFormByTask(
            @ApiParam(value = "номер-ИД субьекта", required = true) @RequestParam(value = "nID_Subject") Long nID_Subject,
            @ApiParam(value = "номер-ИД сервера", required = false) @RequestParam(value = "nID_Server", required = false, defaultValue = "0") Integer nID_Server,
            @ApiParam(value = "номер-ИД услуги", required = true) @RequestParam(value = "nID_Service") Long nID_Service,
            @ApiParam(value = "строка-ИД места Услуги", required = true) @RequestParam(value = "sID_UA") String sID_UA)
            throws RecordNotFoundException {

        HistoryEvent_Service historyEventService = historyEventServiceDao
                .getLastTaskHistory(nID_Subject, nID_Service,
                        sID_UA);
        if (historyEventService == null) {
            throw new RecordNotFoundException("HistoryEvent_Service wasn't found.");
        }
        
        Long nID_Task = historyEventService.getnID_Task();
        nID_Server = historyEventService.getnID_Server();
        nID_Server = nID_Server == null ? 0 : nID_Server;

        nID_Server = generalConfig.getServerId(nID_Server);
        Optional<Server> oOptionalServer = serverDao.findById(new Long(nID_Server));
        if (!oOptionalServer.isPresent()) {
            throw new RecordNotFoundException("Server with nID_Server " + nID_Server + " wasn't found.");
        }
        Server oServer = oOptionalServer.get();
        String sHost = oServer.getsURL();
        String sURL = sHost + "/service/action/task/getStartFormData?nID_Task=" + nID_Task;
        ResponseEntity<String> osResponseEntityReturn = oHttpEntityInsedeCover.oReturn_RequestGet_JSON(sURL);
        
        /*String sUser = generalConfig.getAuthLogin();
        String sPassword = generalConfig.getAuthPassword();
        String sAuth = SignUtil.base64_encode(sUser + ":" + sPassword);
        HttpHeaders oHttpHeaders = new HttpHeaders();
        oHttpHeaders.add("Authorization", "Basic " + sAuth);
        oHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> osHttpEntity = new HttpEntity<>(oHttpHeaders);

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        LOG.info("Calling URL with parametes {}",  serverUrl);
        ResponseEntity<String> result;

        try {
            osResponseEntityReturn = oRestTemplate.exchange(sURI, HttpMethod.GET, osHttpEntity, String.class);
        } catch (RestClientException e) {
            LOG.warn("Error: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new RecordNotFoundException();
        }*/

        return osResponseEntityReturn.getBody();
    }

}
