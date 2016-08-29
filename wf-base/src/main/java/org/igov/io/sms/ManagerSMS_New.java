package org.igov.io.sms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManagerSMS_New {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerSMS.class);

    private static int countSMS = 0;

    private String sURL_Send = null;
    private String sMerchantId = null;
    private String sMerchantPassword = null;
    private String sCallbackUrl_SMS = null;

    private String static_sMessageId = "IGOV_SMS-";

    // Признак готовности сервиса отсылать сообщения
    private boolean isReadySendSMS = false;

    @Autowired
    GeneralConfig generalConfig;

    /*
     * Проверяем заданы ли все параметры для отсылки СМС. Если нет то сервис не
     * готов отсылать сообщения.
     */
    @PostConstruct
    private void init() {
	sURL_Send = generalConfig.getURL_Send_SMSNew().trim();
	sMerchantId = generalConfig.getMerchantId_SMS().trim();
	sMerchantPassword = generalConfig.getMerchantPassword_SMS().trim();
	sCallbackUrl_SMS = generalConfig.getSelfHost() + "/wf/service/sms/callbackSMS";

	LOG.debug(
		"general.SMS_New.sURL_Send={}, general.SMS_New.sMerchantId={}, general.SMS_New.sMerchantPassword=*****, general.SMS_New.sCallbackUrl={}",
		sURL_Send, sMerchantId, sCallbackUrl_SMS);

	if (sURL_Send.startsWith("${") || sMerchantId.startsWith("${") || sMerchantPassword.startsWith("${")
		|| sCallbackUrl_SMS.startsWith("${")) {
	    LOG.warn("Сервис не готов к отсылке сообщений. Не заданы необходимые параметры");
	    return;
	}
	static_sMessageId = static_sMessageId + System.currentTimeMillis() + "-";

	LOG.info("Сервис готов к отсылке сообщений.");
	isReadySendSMS = true;
    }

    public String sendSMS(String sPhone, String sText) throws IllegalArgumentException {
	return sendSMS(null, sPhone, sText);
    }

    public String sendSMS(String sMessageId, String sPhone, String sText) throws IllegalArgumentException {
	if (!isReadySendSMS) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return "";
	}

	if (sMessageId == null) {
	    countSMS++;
	    sMessageId = static_sMessageId + Integer.toString(countSMS);
	}

	SMS_New sms;
	try {
	    sms = new SMS_New(sMessageId, sCallbackUrl_SMS, sPhone, sMerchantId, sMerchantPassword, sText);
	} catch (IllegalArgumentException e) {
	    LOG.error("Ошибка создания SMS. sPhone={}, sText={}", sPhone, sText, e);
	    return "";
	}

	String stringSmsReqest = sms.toJSONString();

	LOG.info("sURL={}", sURL_Send);
	LOG.debug("Запрос:\n{}", stringSmsReqest);

	HttpURLConnection oHttpURLConnection = null;
	String ret = "";
	try {
	    URL oURL = new URL(sURL_Send);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("POST");
	    oHttpURLConnection.setRequestProperty("content-type", "application/json");
	    oHttpURLConnection.setDoOutput(true);

	    try (DataOutputStream oDataOutputStream = new DataOutputStream(oHttpURLConnection.getOutputStream())) {
		oDataOutputStream.writeBytes(stringSmsReqest);
		oDataOutputStream.flush();
		oDataOutputStream.close();

		try (BufferedReader oBufferedReader = new BufferedReader(
			new InputStreamReader(oHttpURLConnection.getInputStream()))) {
		    StringBuilder os = new StringBuilder();
		    String s;
		    while ((s = oBufferedReader.readLine()) != null) {
			os.append(s);
		    }
		    ret = os.toString();
		} catch (java.io.FileNotFoundException e) {
		    ret = String.format("Error send SMS. Service: %s return http code: %s", sURL_Send,
			    oHttpURLConnection.getResponseCode());
		    LOG.error("Ошибка при отправке SMS. http code:{}\nЗапрос:\n{}",
			    oHttpURLConnection.getResponseCode(), stringSmsReqest);
		}
	    }
	} catch (MalformedURLException e) {
	    LOG.error("Ошибка при отправке SMS. Запрос:\n{} Ошибка:", stringSmsReqest, e);
	} catch (IOException e) {
	    LOG.error("Ошибка при отправке SMS. Запрос:\n{} Ошибка:", stringSmsReqest, e);
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

	LOG.info("Ответ:\n{}", ret);

	return ret;
    }

    public void saveCallbackSMS(String soJSON) {
	try {
	    SMSCallback sc = new SMSCallback(soJSON);
	    LOG.info("%s", sc.toJSONString());
	} catch (IllegalArgumentException e) {
	    LOG.error("Ошибка callback SMS", e);
	}
    }
}
