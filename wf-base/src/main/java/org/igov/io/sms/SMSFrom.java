package org.igov.io.sms;

public enum SMSFrom {
    FROM_10060("10060"), 
    FROM_PRIVATBANK("PrivatBank");

    private final String sFrom;

    private SMSFrom(String sFrom) {
	this.sFrom = sFrom;
    }

    public String getSMSFrom(){
	return this.sFrom;
    }
}
