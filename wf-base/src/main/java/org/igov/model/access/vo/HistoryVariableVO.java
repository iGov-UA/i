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
    private String sValue;

    public String getsId() {
        return sId;
    }

    public String getsName() {
        return sName;
    }

    public String getsType() {
        return sType;
    }

    public String getsValue() {
        return sValue;
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

    public void setsValue(String sValue) {
        this.sValue = sValue;
    }
    
}
