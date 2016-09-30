package org.igov.service.business.msg;

public enum MsgType {
    ACCES_DENIED_ERROR("AC"), 
    EXTERNAL_ERROR("EX"),
    INF_MESSAGE("IF"),
    INTERNAL_ERROR("IN"),
    VALIDATION_ERROR("VL"),
    WARNING("WR")//,
    //DANGER("DR")
    ;
    
    private final String abbr;
    
    private MsgType(String abbr) {
        this.abbr = abbr;
    }
    
    public String getAbbr() {
        return this.abbr;
    }
    
}
