package org.igov.io.sms;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.*;

/**
 *
 * @author Kovilin
 */
@Service
public class ManagerSMS {
    
    private static final Logger LOG = LoggerFactory.getLogger(ManagerSMS.class);
    
    @Autowired
    private ManagerSMS_New managerSMS;

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    private HttpRequester oHttpRequester;

    private static final String LIFEBODY = new StringBuilder("<message>")
                .append("<service id='single' source='iGov'/>")
                .append("<to>").append("%s").append("</to>")
                .append("<body content-type=\"text/plain\" encoding=\"plain\">").append("%s")
                .append("</body>")
                .append("</message>").toString();
    
    /*private static final String KYIVSTARBODY = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            .append("<message rid=\"id\" paid=\"false\" bearer=\"SMS\">")
            .append("<sin>").append("%s").append("</sin>")
            .append("<body content-type=\"text/plain\" encoding=\"plain\">").append("%s")
            .append("</body>")
            .append("</message>").toString();*/
    
           
    public String sendSms(String phone, String message, boolean oldApiFlag) throws Exception
    {
        Pattern regexpLifeCell = Pattern.compile("38093(.*)|38063(.*)|38073");
        Pattern regexKyivStar = Pattern.compile("38067(.*)|38096(.*)");

        String resp = "[none]";
        
        if (oldApiFlag == false){
            if (regexpLifeCell.matcher(phone).matches()){
                resp = SendLifeCellSms(phone, message);
            }
            /*else if(regexKyivStar.matcher(phone).matches())
            {
                SendKyivStarSms(phone, message);
            }*/
            else{
                resp = SendSenderSms(phone, message);
            }
        }
        else{
            resp = SendSenderSms(phone, message);
        }
        
        return resp;
    }
    
    private String SendLifeCellSms(String phone, String message) throws Exception
    {
        String bodyResult = String.format(LIFEBODY, "+" + phone, message);
        return oHttpRequester.postInside(generalConfig.getLifeURL(), null, bodyResult, "text/xml; charset=utf-8",
            generalConfig.getLifeLogin(), generalConfig.getLifePassword());
    }
    
    /*private String SendKyivStarSms(String phone, String message) throws Exception
    {
        String bodyResult = String.format(KYIVSTARBODY, "+" + phone, message);
        return oHttpRequester.postInside(generalConfig.getKyivStarURL(), null, bodyResult, "text/xml; charset=utf-8",
            generalConfig.getKyivStarLogin(), generalConfig.getKyivStarPassword());
    }*/
    
    private String SendSenderSms(String phone, String message)
    {
        return managerSMS.sendSMS("+" + phone, message);
    }
    
}
