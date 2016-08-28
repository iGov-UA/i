package org.igov.io.sms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMS_New {
    private final static Logger LOG = LoggerFactory.getLogger(SMS.class);

    private static final String PHONE_REGEX = "^\\+[0-9]{10,12}$";

    private String messageId = null;
    private String callbackUrl = null;
    private String schemaId = "1";
    private String addrPhone = null;
    private String typeSend = "online";
    private String typeCheck = "info";
    private String merchantId = null;
    private String merchantType = "sms_service";
    private String merchantPassword = null;
    private List<SMS_Text> messageTemplate = new ArrayList<>();
    private String smsAlfaname = SMSFrom.FROM_IGOV.getSMSFrom();;
    private int senderTTL = 60;
    private int privat24TTL = 60;

    public SMS_New(String messageId, String callbackUrl, String addrPhone, String merchantId, String merchantPassword,
	    String sText) throws IllegalArgumentException {
	LOG.debug("messageId={}, callbackUrl={}, addrPhone={}, merchantId={}, merchantPassword={}, sText={}", messageId,
		callbackUrl, addrPhone, merchantId, merchantPassword, sText);

	if (messageId == null || callbackUrl == null || addrPhone == null || merchantId == null
		|| merchantPassword == null || sText == null) {
	    throw new IllegalArgumentException("parameters is null");
	}
	addrPhone = addrPhone.trim();
	if (!addrPhone.matches(PHONE_REGEX)) {
	    LOG.debug("Некорректный номер телефона: {}", addrPhone);
	    throw new IllegalArgumentException("Некорректный номер телефона: " + addrPhone);
	}

	this.messageId = messageId;
	this.callbackUrl = callbackUrl;
	this.addrPhone = addrPhone;
	this.merchantId = merchantId;
	this.merchantPassword = merchantPassword;
	this.messageTemplate.add(new SMS_Text(sText));
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer(200);
	sb.append("messageId: ");
	sb.append(messageId);
	sb.append(", callbackUrl ");
	sb.append(callbackUrl);
	sb.append(", schemaId ");
	sb.append(schemaId);
	sb.append(", addrPhone ");
	sb.append(addrPhone);
	sb.append(", typeSend ");
	sb.append(typeSend);
	sb.append(", typeCheck ");
	sb.append(typeCheck);
	sb.append(", merchantId ");
	sb.append(merchantId);
	sb.append(", merchantType ");
	sb.append(merchantType);
	sb.append(", merchantPassword ");
	sb.append(merchantPassword);
	sb.append(", messageTemplate ");
	sb.append(messageTemplate);
	sb.append(", smsAlfaname ");
	sb.append(smsAlfaname);
	sb.append(", senderTTL ");
	sb.append(senderTTL);
	sb.append(", privat24TTL ");
	sb.append(privat24TTL);

	return sb.toString();
    }

}
