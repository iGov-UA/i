package org.igov.model.access.vo;

/**
 *
 * @author Kovylin
 */
public class HistoryVariableVO {
    
    public HistoryVariableVO(){
        
    }
    
    private String sId;
    private String sName;
    private String sType;
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
