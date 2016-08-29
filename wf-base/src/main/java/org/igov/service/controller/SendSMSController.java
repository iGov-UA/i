package org.igov.service.controller;

import java.io.IOException;

import org.igov.io.sms.ManagerSMS_New;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/sms")
/**
 * 
 * @author kr110666kai
 * 
 *         Сервис временный, создан для тестирования отсылки СМС
 *
 */
public class SendSMSController {
    private static final Logger LOG = LoggerFactory.getLogger(SendSMSController.class);

    @Autowired
    ManagerSMS_New managerSMS;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public @ResponseBody String sendSMS(@RequestParam(value = "phone", required = false) String phone,
	    @RequestParam(value = "text", required = false) String text) {
	
	String retObj = "";
	try {
	    retObj = managerSMS.sendSMS(phone, text);
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	}

	return retObj;
    }

    @RequestMapping(value = "/callbackSMS", method = RequestMethod.GET)
    public void callbackSMS( @RequestBody String soData_JSON ) {
	LOG.debug("callback JSON={}", soData_JSON);
	managerSMS.saveCallbackSMS(soData_JSON);
    }
}
