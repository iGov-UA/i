/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io.mail.unisender;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.igov.io.GeneralConfig;
import org.igov.io.sms.OtpCreate;
import org.igov.io.sms.OtpPass;
import org.igov.io.sms.OtpPassword;
import org.igov.io.sms.OtpText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
@Component
public class ManagerOTP {
    
    private final static Logger LOG = LoggerFactory.getLogger(ManagerOTP.class);
    //private final String sURL = "https://igov.org.ua/index#";
    private final String sURL = "https://sms-inner.siteheart.com/api/otp_create_api.cgi";
    @Autowired
    GeneralConfig generalConfig;
    
    public ManagerOTP(){}
    
    public <T> String sendPasswordOTP(String sPhone, String sText, boolean bTextOnly) throws Exception {
        LOG.info("(sPhone={},sText={})",sPhone,sText);
        Properties oProperties = new Properties();
        File oFile = new File(System.getProperty("catalina.base") + "/conf/merch.properties");
        FileInputStream oFileInputStream = new FileInputStream(oFile);
        oProperties.load(oFileInputStream);
        oFileInputStream.close();

        OtpPassword oOtpPassword = new OtpPassword();
        oOtpPassword.setMerchant_id(oProperties.getProperty("merchant_id"));
        oOtpPassword.setMerchant_password(oProperties.getProperty("merchant_password"));

        OtpCreate oOtpCreate = new OtpCreate();
        oOtpCreate.setCategory("qwerty");
        oOtpCreate.setFrom("10060");
        oOtpCreate.setPhone(sPhone);
		/*SmsTemplate oSmsTemplate = new SmsTemplate();
		oSmsTemplate.setText("text:"+"Parol: ");
		oSmsTemplate.setPassword("password:"+"2");
		SmsTemplate oSmsTemplate2 = new SmsTemplate();
		oSmsTemplate2.setText("text:"+"-");
		oSmsTemplate2.setPassword("password:"+"2");
		SmsTemplate oSmsTemplate3 = new SmsTemplate();
		oSmsTemplate3.setText("text:"+"-");
		oSmsTemplate3.setPassword("password:"+"2");
		SmsTemplate oSmsTemplate4 = new SmsTemplate();
		oSmsTemplate4.setText("text:"+"-");
		oSmsTemplate4.setPassword("password:"+"2");*/
        List<T> a = new ArrayList<T>();
        if(bTextOnly){
            a.add((T) new OtpText(sText));
        }else{
            a.add((T) new OtpText("Parol:"));
            a.add((T) new OtpPass(sText));
        }
		/*a.add((T)new OtpPass("2"));
		a.add((T)new OtpText("-"));
		a.add((T)new OtpPass("2"));
		a.add((T)new OtpText("-"));
		a.add((T)new OtpPass("2"));
		a.add((T)new OtpText("-"));
		a.add((T)new OtpPass("2"));*/
        oOtpCreate.setSms_template(a);
        List<OtpCreate> aOtpCreate = new ArrayList<>();
        aOtpCreate.add(oOtpCreate);
        oOtpPassword.setOtp_create(aOtpCreate);
        Gson oGson = new Gson();
        String jsonObj = oGson.toJson(oOtpPassword);
        URL oURL = new URL(sURL);
        HttpURLConnection oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
        oHttpURLConnection.setRequestMethod("POST");
        oHttpURLConnection.setRequestProperty("content-type", "application/json;charset=UTF-8");
        oHttpURLConnection.setDoOutput(true);
        DataOutputStream oDataOutputStream = new DataOutputStream(oHttpURLConnection.getOutputStream());
        oDataOutputStream.writeBytes(jsonObj);
        oDataOutputStream.flush();
        oDataOutputStream.close();

        BufferedReader oBufferedReader = new BufferedReader(new InputStreamReader(oHttpURLConnection.getInputStream()));
        StringBuilder os = new StringBuilder();
        String s;
        while ((s = oBufferedReader.readLine()) != null) {
            os.append(s);
        }
        oBufferedReader.close();
        return os.toString();
    }    
    
}
