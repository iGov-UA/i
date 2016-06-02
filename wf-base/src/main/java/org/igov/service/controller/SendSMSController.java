package org.igov.service.controller;

import java.io.IOException;

import org.igov.io.sms.ManagerSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @Autowired
    ManagerSMS managerSMS;

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public @ResponseBody String sendSMS(@RequestParam(value = "phone", required = false) String phone,
	    @RequestParam(value = "text", required = false) String text) {
	
	String retObj = "";
	try {
	    retObj = managerSMS.sendSMS(phone, text);
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return retObj;
    }

}
