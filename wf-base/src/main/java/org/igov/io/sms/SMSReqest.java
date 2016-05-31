package org.igov.io.sms;

import java.util.ArrayList;
import java.util.List;

public class SMSReqest {
    private String merchant_id;
    private String merchant_password;
    private List<SMS> sms = new ArrayList<>();

    public SMSReqest(String merchant_id, String merchant_password) {
	this.merchant_id = merchant_id;
	this.merchant_password = merchant_password;
    }

    public String getMerchant_id() {
	return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
	this.merchant_id = merchant_id;
    }

    public String getMerchant_password() {
	return merchant_password;
    }

    public void setMerchant_password(String merchant_password) {
	this.merchant_password = merchant_password;
    }

    public List<SMS> getSms() {
	return sms;
    }

    public void setSms(List<SMS> sms) {
	this.sms = sms;
    }

    public void addSMS(SMS sms) {
	this.sms.add(sms);
    }

    @Override
    public String toString() {
	return "{" + merchant_id + ":" + merchant_id + ", merchant_password:" + merchant_password + ",sms:" + sms + "}";
    }

}
