package org.igov.io.sms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.igov.io.GeneralConfig;
import org.igov.service.business.action.task.systemtask.doc.util.UkrDocUtil;
import org.igov.util.JSON.JsonRestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManagerSMS_New {

    private final static Logger LOG = LoggerFactory.getLogger(ManagerSMS_New.class);

    private static AtomicInteger countSMS = new AtomicInteger(0);

    private String sURL_Send = null;
    private String sMerchantId = null;
    private String sMerchantPassword = null;
    private String sCallbackUrl_SMS = null;
    private String sChemaId = null;

    private String static_sMessageId = "IGOVSMS_";

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
	sURL_Send = generalConfig.getURL_Send_SMSNew().trim() + "/api/v1/send";
	sMerchantId = generalConfig.getMerchantId_SMS().trim();
	sMerchantPassword = generalConfig.getMerchantPassword_SMS().trim();
	sCallbackUrl_SMS = generalConfig.getSelfHost().trim() + "/wf/service/subject/message/getCallbackSMS_PB";
	sChemaId = generalConfig.getChemaId().trim();

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	// Временно заменен на локальный адрес, для тестирования
	sCallbackUrl_SMS = "http://10.4.1.84:8080/wf-central/service/subject/message/getCallbackSMS_PB";
//	sCallbackUrl_SMS = "http://alpha.test.igov.org.ua.80.e.it.loc/wf-central/service/subject/message/getCallbackSMS_PB";
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LOG.debug("sURL_Send={}, sMerchantId={}, sCallbackUrl_SMS={}, sChemaId",
		sURL_Send, sMerchantId, sCallbackUrl_SMS, sChemaId);

	if (sURL_Send.startsWith("${") || sMerchantId.startsWith("${") || sMerchantPassword.startsWith("${")
		|| sCallbackUrl_SMS.startsWith("${") || sChemaId.startsWith("${")) {
	    LOG.warn("Сервис не готов к отсылке сообщений. Не заданы необходимые параметры");
	    return;
	}
	static_sMessageId = static_sMessageId + System.currentTimeMillis() + "_";

	LOG.info("Сервис готов к отсылке сообщений.");
	isReadySendSMS = true;
    }

    public String sendSMS(String sPhone, String sText) throws IllegalArgumentException {
	return sendSMS(null, sPhone, sText);
    }

    public String sendSMS(String sID_Order, String sPhone, String sText) throws IllegalArgumentException {
	if (!isReadySendSMS) {
	    LOG.warn("Сервис не готов к отсылке сообщений.");
	    return "";
	}
	LOG.debug("sID_Order:{}, sPhone:{}, sText:{}", sID_Order, sPhone, sText);

	String sMessageId = static_sMessageId + Integer.toString(countSMS.incrementAndGet());
	if (sID_Order != null) {
	    sMessageId = sMessageId + "_" + sID_Order;
	}

	SMS_New sms;
	try {
	    sms = new SMS_New(sMessageId, sCallbackUrl_SMS, sChemaId, sPhone, sMerchantId, sMerchantPassword, sText);
	} catch (IllegalArgumentException e) {
	    LOG.error("Error create SMS. sPhone={}, sText={}", sPhone, sText, e);
	    return String.format("Error create SMS. phone=%s, text=%s", sPhone, sText);
	}

	String sessionId;
	try {
	    sessionId = UkrDocUtil.getSessionId(generalConfig.getLogin_Auth_UkrDoc_SED(),
		    generalConfig.getPassword_Auth_UkrDoc_SED(),
		    generalConfig.getURL_GenerateSID_Auth_UkrDoc_SED() + "?lang=UA");
	} catch (Exception e) {
	    LOG.error("Error get Session ID", e);
	    return String.format("Error get Session ID. %s", e.getMessage());
	}

	String stringSmsReqest = sms.toJSONString();

	LOG.info("sURL={}", sURL_Send);
	LOG.debug("Session ID:{}, RequestBody:\n{}", sessionId, stringSmsReqest);

	String ret = "";
	HttpURLConnection oHttpURLConnection = null;
	try {
	    URL oURL = new URL(sURL_Send);
	    oHttpURLConnection = (HttpURLConnection) oURL.openConnection();
	    oHttpURLConnection.setRequestMethod("POST");
	    oHttpURLConnection.setRequestProperty("Content-Type", "application/json");
	    oHttpURLConnection.setRequestProperty("sid", sessionId);
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

		    // Этап обработки ответа сервиса
		    Map<String, Object> moData = null;
		    try {
			moData = JsonRestUtils.readObject(ret, Map.class);

			String msStatus = "";
			String msCode = "";
			String msMessage = "";
			String messageId = "";
			if (moData != null) {
			    if (moData.containsKey("msStatus")) {
				msStatus = (String) moData.get("msStatus");
			    }

			    // Если ответ с ошибкой
			    if (msStatus.equals("not_delivered") || msStatus.equals("warning")
				    || msStatus.equals("error")) {
				if (moData.containsKey("msCode")) {
				    msCode = (String) moData.get("msCode");
				}
				if (moData.containsKey("msMessage")) {
				    msMessage = (String) moData.get("msMessage");
				}
				if (moData.containsKey("messageId")) {
				    messageId = (String) moData.get("messageId");
				}
				LOG.error(
					"Error send SMS. RequestBody:\n{}\nResponse msStatus:{}, msCode:{}, msMessage:{}, messageId:{}",
					stringSmsReqest, msStatus, msCode, msMessage, messageId);
			    }
			}
		    } catch (Exception e) {
			LOG.error("Error parse response JSON: {}", ret);
		    }

		} catch (java.io.FileNotFoundException e) {
		    ret = String.format("Error send SMS. Service: %s return http code: %s", sURL_Send,
			    oHttpURLConnection.getResponseCode());
		    LOG.error("Error send SMS. RequestBody:\n{}\nhttp code:{}\n", stringSmsReqest,
			    oHttpURLConnection.getResponseCode(), e);
		}
	    }

	} catch (MalformedURLException e) {
	    LOG.error("Error send SMS. RequestBody:\n{} Error:", stringSmsReqest, e);
	    ret = e.getMessage();
	} catch (IOException e) {
	    LOG.error("Error send SMS. RequestBody:\n{} Error:", stringSmsReqest, e);
	    ret = e.getMessage();
	} finally {
	    if (oHttpURLConnection != null) {
		oHttpURLConnection.disconnect();
	    }
	}

	LOG.info("ResponseBody:\n{}", ret);

	return ret;
    }

    public String saveCallbackSMS(String soJSON) {
	SMSCallback sc = null;
	try {
	    sc = new SMSCallback(soJSON);
	    LOG.info("soJSON: {}", sc.toJSONString());
	} catch (IllegalArgumentException e) {
	    LOG.error("Error parse JSON response callback SMS", e.getMessage());
	}
	return sc == null? "{}" : sc.toJSONString();
    }

}
