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

@Controller
@Api(tags = {"SubjectMessageCommonController -- Сообщения субьектов"})
@RequestMapping(value = "/subject/message")
public class SubjectMessageCommonController {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectMessageCommonController.class);
    
    /*private static final String body = new StringBuilder("<message>")
                .append("<service id='single' source='iGov'/>")
                .append("<to>").append("%s").append("</to>")
                .append("<body content-type=\"text/plain\" encoding=\"plain\">").append("%s")
                .append("</body>")
                .append("</message>").toString();*/
    @Autowired
    private ManagerSMS_New managerSMS;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private HttpRequester oHttpRequester;

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
            @RequestParam(value = "message", required = false) String message) throws Exception {

        String resp;

        byte[] utf8Message = message.getBytes("UTF-8");

        //if (number.startsWith("+38063")||number.startsWith("+38093"))
        //{
        String body = new StringBuilder("<message>")
                .append("<service id='single' source='iGov'/>")
                .append("<to>").append(number).append("</to>")
                .append("<body content-type=\"text/plain\" encoding=\"plain\">").append(message).append(new String(utf8Message, "UTF-8"))
                .append("</body>")
                .append("</message>").toString();
        
        String result = String.format(body, number, message);
        
        resp = oHttpRequester.postInside(generalConfig.getLifeURL(), null, body, "text/xml; charset=utf-8",
                generalConfig.getLifeLogin(), generalConfig.getLifePassword());
        //}

        return resp;
    }
}
