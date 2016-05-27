package org.igov.io.sms;

public enum SMSFrom {
    FROM_IGOV("iGov");

    private final String sFrom;

    private SMSFrom(String sFrom) {
	this.sFrom = sFrom;
    }

    public String getSMSFrom(){
	return this.sFrom;
    }
}
