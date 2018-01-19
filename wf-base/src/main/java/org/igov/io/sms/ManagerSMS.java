package org.igov.io.sms;

import org.igov.io.GeneralConfig;
import org.igov.io.web.HttpRequester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

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

    private static final String KYIVSTARBODY = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            .append("<message paid=\"2000\" mid=\"").append("%s").append("\" bearer=\"SMS\">")
            .append("<sn>").append("%s").append("</sn>")
            .append("<sin>").append("%s").append("</sin>")
            .append("<body content-type=\"text/plain\">").append("%s")
            .append("</body>")
            .append("</message>").toString();
        
    public String sendSms(String phone, String message, String sID_Order) {
        
        String sResponse = "[none]";

        try {
            Pattern regexpLifeCell = Pattern.compile("38093(.*)|38063(.*)|38073(.*)");
            Pattern regexKyivStar = Pattern.compile("38039(.*)|38067(.*)|38068(.*)|38096(.*)|38097(.*)|38098(.*)");
            LOG.info("phone: {}, sID_Order: {}", phone, sID_Order);
            phone = phone.replace("+", "").trim();

            if (regexpLifeCell.matcher(phone).matches()) {
                LOG.info("send sms via 'LifeCell'");
                sResponse = SendLifeCellSms(phone, message);
            } else if (regexKyivStar.matcher(phone).matches()){
                LOG.info("send sms via 'KyivStar'");
                sResponse = SendKyivStarSms(sID_Order, phone, message);
            } else {
                LOG.info("send sms via sender");
                sResponse = SendSenderSms(sID_Order, phone, message);
            }
    
            LOG.info("response: {}", sResponse);
        } catch (Exception ex){
            LOG.error("sending sms error", ex);
        }
            
        return sResponse;
    }
    
    private String SendLifeCellSms(String phone, String message) throws Exception {
        String bodyResult = String.format(LIFEBODY, "+" + phone, message);
        LOG.info("SendLifeCellSms_Body = {}", bodyResult);

        String response = oHttpRequester.postInside(generalConfig.getLifeURL(), null, bodyResult, "text/xml; charset=utf-8",
                generalConfig.getLifeLogin(), generalConfig.getLifePassword());
        LOG.info("SendLifeCellSms_Response = {}", response);
        return response;
    }

    private String SendKyivStarSms(String sID_Order, String phone, String message) throws Exception
    {
        LOG.info("SendKyivStarSms started...");
        String bodyResult = String.format(KYIVSTARBODY, sID_Order, "iGov", phone, message);
        LOG.info("generalConfig.getKievStarURL() {}", generalConfig.getKievStarURL());
        LOG.info("generalConfig.getKievStarLogin() {}", generalConfig.getKievStarLogin());
        LOG.info("generalConfig.getKievStarPassword() {}", generalConfig.getKievStarPassword());
        return oHttpRequester.postInside(generalConfig.getKievStarURL(), null, bodyResult, "text/xml; charset=utf-8",
            generalConfig.getKievStarLogin(), generalConfig.getKievStarPassword());
    }
    
    private String SendSenderSms(String sID_Order, String phone, String message) {
        String response = managerSMS.sendSMS(sID_Order, "+" + phone, message);
        LOG.info(response);
        return response;
    }
}
