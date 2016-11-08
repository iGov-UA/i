package org.igov.service.controller;

import org.igov.io.sms.ManagerSMS_New;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.springframework.web.bind.annotation.RequestParam;
import org.igov.io.sms.ManagerSMS;

@Controller
@Api(tags = {"SubjectMessageCommonController -- Сообщения субьектов"})
@RequestMapping(value = "/subject/message")
public class SubjectMessageCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectMessageCommonController.class);
    
    @Autowired
    private ManagerSMS_New managerSMS;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private HttpRequester oHttpRequester;
    
    @Autowired
    private ManagerSMS smsManager;

    /**
     * Колбек для сервиса отправки СМС
     *
     * @param soData_JSON
     * @return 
     */
    @RequestMapping(value = "/getCallbackSMS_PB", method = {RequestMethod.POST,
        RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String callbackSMS(@RequestBody String soData_JSON) {
        LOG.debug("callback JSON={}", soData_JSON);
        String ret = managerSMS.saveCallbackSMS(soData_JSON);
        LOG.info("save callback JSON={}", ret);

        return "";
    }

    @ApiOperation(value = "/sendSms", notes = "##### Контроллер отправки смс с проверкой номера по оператору абонента\n")
    @RequestMapping(value = "/sendSms", method = {RequestMethod.POST,
        RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String sendSms(@ApiParam(value = "Номер телефона в формате 380XXXXXXXXX", required = true)@RequestParam(value = "phone") String phone, 
            @ApiParam(value = "Текст сообщения", required = true) @RequestParam(value = "message") String message,
            @ApiParam(value = "Если значение флага true - отправка смс идет через старое api, независимо от оператора", required = false) @RequestParam(value = "oldApiFlag") Boolean apiFlag){
    
        if (apiFlag == null)
        {
            apiFlag = false;
        }
        
        try{
            String resp = smsManager.sendSms(phone, message, apiFlag);
            return resp;
        }
        catch (Exception ex)
        {
            return ex.toString();
        }
    }
}
