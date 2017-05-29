package org.igov.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.igov.model.action.event.HistoryEvent_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.igov.service.business.action.event.ActionEventHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kovilin
 */
@Controller
@Api(tags = {"ActionHistoryController -- История по документам"})
@RequestMapping(value = "/history/document/event")
public class ActionHistoryController {

    @Autowired
    private ActionEventHistoryService oActionEventHistoryService;

    private static final Logger LOG = LoggerFactory.getLogger(ActionHistoryController.class);

    @RequestMapping(value = "/addHistoryEvent", method = RequestMethod.GET)
    public @ResponseBody
    HistoryEvent_Service addHistoryEvent(
            @ApiParam(value = "строка-ид заявки, в формате XXX-XXXXXX = nID_Server-nID_Order", required = false) @RequestParam(value = "sID_Order", required = false) String sID_Order,
            @ApiParam(value = "ИД-номер", required = false) @RequestParam(value = "nID_Subject", required = false) Long nID_Subject,
            @ApiParam(value = "строка-статус", required = false) @RequestParam(value = "sUserTaskName", required = false) String sUserTaskName,
            @ApiParam(value = "ид услуги", required = false) @RequestParam(value = "nID_Service", required = false) Long nID_Service,
            @ApiParam(value = "ид под-услуги", required = false) @RequestParam(value = "nID_ServiceData", required = false) Long nID_ServiceData,
            @ApiParam(value = "ид области", required = false) @RequestParam(value = "nID_Region", required = false) Long nID_Region,
            @ApiParam(value = "ид страны", required = false) @RequestParam(value = "sID_UA", required = false) String sID_UA,
            @ApiParam(value = "строка-объект с данными (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "soData", required = false) String soData,
            @ApiParam(value = "строка-токена (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sToken", required = false) String sToken,
            @ApiParam(value = "строка заглавия сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sHead", required = false) String sHead,
            @ApiParam(value = "строка тела сообщения (для поддержки дополнения заявки со стороны гражданина)", required = false) @RequestParam(value = "sBody", required = false) String sBody,
            @ApiParam(value = "номер-ид запущенного процесса для обработки фидбеков (issue 962)", required = false) @RequestParam(value = "nID_Proccess_Feedback", required = false) Long nID_Proccess_Feedback,
            @ApiParam(value = "номер-ид бп эсклации (поле на перспективу для следующего тз по эскалации)", required = false) @RequestParam(value = "nID_Proccess_Escalation", required = false) Long nID_Proccess_Escalation,
            @ApiParam(value = "числовой код, который соответсвует статусу", required = false) @RequestParam(value = "nID_StatusType", required = false) Long nID_StatusType,
            @ApiParam(value = "числовой код, который соответсвует типу события", required = false) @RequestParam(value = "nID_HistoryEventType", required = false) Long nID_HistoryEventType,
            @ApiParam(value = "новые данные после редактирвоание", required = false) @RequestParam(value = "newData", required = false) String newData,
            @ApiParam(value = "старые данные до редактирвоания", required = false) @RequestParam(value = "oldData", required = false) String oldData,
            @ApiParam(value = "ид бп созданного документа (для вызова субпроцесса)", required = false) @RequestParam(value = "new_BP_ID", required = false) String new_BP_ID,
            @ApiParam(value = "логин или ФИО", required = false) @RequestParam(value = "sLogin", required = false) String sLogin,
            @ApiParam(value = "логин", required = false) @RequestParam(value = "sLoginNew", required = false) String sLoginNew,
            @ApiParam(value = "ФИО", required = false) @RequestParam(value = "sName", required = false) String sName
        ){
        LOG.info("addHistoryEvent was started with params: sID_Order: {} nID_Subject : {} sUserTaskName: {} "
                + "nID_Service: {} nID_ServiceData: {} nID_ServiceData: {} nID_Region: {} sID_UA: {} soData: {} "
                + "sToken: {} sHead: {} sBody: nID_Proccess_Feedback: {} nID_Proccess_Escalation: {} nID_StatusType: {}",
                sID_Order, nID_Subject, sUserTaskName, nID_Service, nID_ServiceData, nID_ServiceData, nID_Region,
                sID_UA, soData, sToken, sHead, sBody, nID_Proccess_Feedback, nID_Proccess_Escalation, nID_StatusType);

        return oActionEventHistoryService.addActionStatus(
                sID_Order,
                nID_Subject,
                sUserTaskName,
                nID_Service,
                nID_ServiceData,
                nID_Region,
                sID_UA,
                soData,
                sToken,
                sHead,
                sBody,
                nID_Proccess_Feedback,
                nID_Proccess_Escalation,
                nID_StatusType,
                nID_HistoryEventType,
                newData,
                oldData,
                sLogin,
                new_BP_ID,
                sLoginNew,
                sName,
                true,
                true,
                false
        );
    }
}
