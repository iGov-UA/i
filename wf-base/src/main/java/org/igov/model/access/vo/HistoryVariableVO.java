package org.igov.model.access.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Kovylin
 */
public class HistoryVariableVO {
    
    public HistoryVariableVO(){
        
    }
    
    @JsonProperty(value = "sId")
    private String sId;
    
    @JsonProperty(value = "sName")
    private String sName;
    
    @JsonProperty(value = "sType")
    private String sType;
    
    @JsonProperty(value = "oValue")
    private Object oValue;

    public String getsId() {
        return sId;
    }

    public String getsName() {
        return sName;
    }

    public String getsType() {
        return sType;
    }

    public Object getoValue() {
        return oValue;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public void setoValue(Object sValue) {
        this.oValue = sValue;
    }
    
}
