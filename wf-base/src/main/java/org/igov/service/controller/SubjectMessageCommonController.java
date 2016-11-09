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

    /**
     * Колбек для сервиса отправки СМС
     *
     * @param number
     * @param message
     * @return
     */
    @RequestMapping(value = "/sentSms", method = {RequestMethod.POST,
        RequestMethod.GET}, produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String sentSms(@RequestParam(value = "number", required = false) String number, 
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "oldApiFlag", required = false) Boolean apiFlag) throws Exception {
    
        if (apiFlag == null)
        {
            apiFlag = false;
        }
        
        String resp = smsManager.sendSms(number, message, apiFlag); 

        return resp;
    }
}
