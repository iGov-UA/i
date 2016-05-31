package org.igov.io.sms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMS {
    private final static Logger LOG = LoggerFactory.getLogger(SMS.class);

    private String phone = null;
    private String text = null;
    private String from = SMSFrom.FROM_IGOV.getSMSFrom();
    private String sms_category = null;
    private LocalDateTime date_expired = null;
    private String order_id = null;

    private static final String PHONE_REGEX = "^\\+[0-9]{10,12}$";

    public SMS(String sPhone, String sText) throws IllegalArgumentException {
	LOG.debug("sPhone={}, sText={}", sPhone, sText);

	if (sPhone == null || sText == null) {
	    throw new IllegalArgumentException("parameters is null");
	}
	sPhone = sPhone.trim();
	if (!sPhone.matches(PHONE_REGEX)) {
	    LOG.debug("Некорректный номер телефона: {}", sPhone);
	    throw new IllegalArgumentException("Некорректный номер телефона: " + sPhone);
	}

	this.phone = sPhone;
	this.text = sText.trim();

    }

    public String getsPhone() {
	return phone;
    }

    public void setdDate_expired(String dDate_expired) {
	LocalDateTime parsedDateTime;

	try {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    parsedDateTime = LocalDateTime.parse(dDate_expired, formatter);
	} catch (DateTimeParseException e) {
	    LOG.debug("Некорректный параметр date_expired: {}", dDate_expired, e);
	    parsedDateTime = null;
	}

	this.date_expired = parsedDateTime;
    }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer(100);
	sb.append("phone: ");
	sb.append(this.phone);
	sb.append(", from: ");
	sb.append(this.from);
	sb.append(", sms_category: ");
	sb.append(this.sms_category);
	sb.append(", date_expired: ");
	sb.append(this.date_expired);
	sb.append(", order_id: ");
	sb.append(this.order_id);
	sb.append(", text: ");
	sb.append(this.text);

	return sb.toString();
    }

    public String getFrom() {
	return from;
    }

    public void setFrom(SMSFrom from) {
	this.from = from.getSMSFrom();
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public String getSms_category() {
	return sms_category;
    }

    public void setSms_category(String sms_category) {
	this.sms_category = sms_category;
    }

    public LocalDateTime getDate_expired() {
	return date_expired;
    }

    public void setDate_expired(String sDate_expired) {
	LocalDateTime parsedDateTime;

	try {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    parsedDateTime = LocalDateTime.parse(sDate_expired, formatter);
	} catch (DateTimeParseException e) {
	    LOG.debug("Некорректный параметр date_expired: {}", sDate_expired, e);
	    parsedDateTime = null;
	}

	this.date_expired = parsedDateTime;
    }

    public String getOrder_id() {
	return order_id;
    }

    public void setOrder_id(String order_id) {
	this.order_id = order_id;
    }

    public void setFrom(String from) {
	this.from = from;
    }

}
