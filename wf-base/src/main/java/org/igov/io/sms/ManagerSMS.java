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
            //.append("<to ext_id=\"").append("%s").append("\">").append("%s").append("</to>")
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
    public String sendSms(String phone, String message, String sID_Order, boolean bTest) throws Exception {//, boolean bOldAPI
        
        String resp = "[none]";

        try {
            Pattern regexpLifeCell = Pattern.compile("38093(.*)|38063(.*)|38073");
            //Pattern regexKyivStar = Pattern.compile("38067(.*)|38096(.*)|38097(.*)|38098(.*)");
            LOG.info("phone: " + phone + " sID_Order: " + sID_Order + " bTest: " + bTest);
            phone = phone.replace("+", "").trim();

            if(!bTest){
                Boolean bOldAPI = !bTest;
                if (bOldAPI) {
                    resp = SendSenderSms(sID_Order, phone, message);
                    LOG.info("phone: " + phone + " sID_Order: " + sID_Order + " bTest: " + bTest + " resp: " + resp);
                } else if (regexpLifeCell.matcher(phone).matches()) {
                    resp = SendLifeCellSms(phone, message);
                } /*else if(regexKyivStar.matcher(phone).matches())
                    {
                        SendKyivStarSms(phone, message);
                }*/ else {
                    resp = SendSenderSms(sID_Order, phone, message);
                }
            }
            LOG.info("phone: " + phone + " sID_Order: " + sID_Order + " bTest: " + bTest + " resp: " + resp + " ok!!!");
        }
        catch (Exception ex){
            LOG.error("Error sending SMS: " + ex.toString());
        }
            
        return resp;
    }
    
    private String SendLifeCellSms(String phone, String message) throws Exception {
        String bodyResult = String.format(LIFEBODY, "+" + phone, message);
        String response = oHttpRequester.postInside(generalConfig.getLifeURL(), null, bodyResult, "text/xml; charset=utf-8",
                generalConfig.getLifeLogin(), generalConfig.getLifePassword());
        LOG.info(response);
        return response;
    }

    /*private String SendKyivStarSms(String phone, String message) throws Exception
    {
        String bodyResult = String.format(KYIVSTARBODY, "+" + phone, message);
        return oHttpRequester.postInside(generalConfig.getKyivStarURL(), null, bodyResult, "text/xml; charset=utf-8",
            generalConfig.getKyivStarLogin(), generalConfig.getKyivStarPassword());
    }*/
    private String SendSenderSms(String sID_Order, String phone, String message) {
        String response = managerSMS.sendSMS(sID_Order, "+" + phone, message);
        LOG.info(response);
        return response;
    }
}
