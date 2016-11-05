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
    
    
    public String sentSms(String phone, String message, boolean oldApiFlag) throws Exception
    {
        //Pattern regexpLifeCell = Pattern.compile("^(38093|38063)");
        Pattern regexpLifeCell = Pattern.compile("^(38092)");
        
        String resp = "[none]";
        
        if (oldApiFlag == false){
            //if (regexpLifeCell.matcher(phone).matches()){
            if (message.startsWith("38092")){  
                resp = SentLifeCellSms(phone, message);
            }
        }
        else{
            resp = SentSenderSms(phone, message);
        }
        
        return resp;
    }
    
    private String SentLifeCellSms(String phone, String message) throws Exception
    {
        String bodyResult = String.format(LIFEBODY, "+" + phone, message);
        return oHttpRequester.postInside(generalConfig.getLifeURL(), null, bodyResult, "text/xml; charset=utf-8",
            generalConfig.getLifeLogin(), generalConfig.getLifePassword());
    }
    
    private String SentSenderSms(String phone, String message)
    {
        return managerSMS.sendSMS("+" + phone, message);
    }
    
}
