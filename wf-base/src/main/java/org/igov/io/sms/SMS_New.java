package org.igov.io.sms;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class SMS_New {
    private final static Logger LOG = LoggerFactory.getLogger(SMS_New.class);    
    
    private final static String PHONE_REGEX = "^\\+[0-9]{10,12}$";
    private final static Gson oGson = new Gson();

    private String messageId = null;
    private String callbackUrl = null;
    private String schemaId = null;
    private String addrPhone = null;
    private String typeSend = "online";
    private String typeCheck = "info";
    private String merchantId = null;
    private String merchantType = "sms_service";
    private String merchantPassword = null;
    private List<SMS_Text> messageTemplate = new ArrayList<>();
    private String smsAlfaname = SMSFrom.FROM_IGOV.getSMSFrom();
    private int senderTTL = 60;
    private int privat24TTL = 60;

    public SMS_New(String messageId, String callbackUrl, String schemaId, String addrPhone, String merchantId, String merchantPassword,
	    String sText) throws IllegalArgumentException {
	LOG.debug("messageId={}, callbackUrl={}, schemaId={}, addrPhone={}, merchantId={}, merchantPassword={}, sText={}", messageId,
		callbackUrl, schemaId, addrPhone, merchantId, merchantPassword, sText);

	if (messageId == null || callbackUrl == null || schemaId == null || addrPhone == null || merchantId == null
		|| merchantPassword == null || sText == null) {
	    throw new IllegalArgumentException("parameters is null");
	}
	addrPhone = addrPhone.trim();
	if (!addrPhone.matches(PHONE_REGEX)) {
	    LOG.debug("Некорректный номер телефона: {}, должен быть вида +380671234567", addrPhone);
	    throw new IllegalArgumentException("Некорректный номер телефона: " + addrPhone + ", должен быть вида +380671234567");
	}

	this.messageId = messageId;
	this.callbackUrl = callbackUrl;
	this.schemaId = schemaId;
	this.addrPhone = addrPhone;
	this.merchantId = merchantId;
	this.merchantPassword = merchantPassword;
	this.messageTemplate.add(new SMS_Text(sText));
    }

    @Override
    public String toString() {
	return toJSONString();
    }

    public String toJSONString() {
	return oGson.toJson(this);
    }

    public String getMessageId() {
        return messageId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public String getAddrPhone() {
        return addrPhone;
    }

    public String getTypeSend() {
        return typeSend;
    }

    public String getTypeCheck() {
        return typeCheck;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public List<SMS_Text> getMessageTemplate() {
        return messageTemplate;
    }

    public String getSmsAlfaname() {
        return smsAlfaname;
    }

    public int getSenderTTL() {
        return senderTTL;
    }

    public int getPrivat24TTL() {
        return privat24TTL;
    }
    
}
