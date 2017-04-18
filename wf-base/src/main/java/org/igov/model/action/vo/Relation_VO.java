package org.igov.model.action.vo;

/**
 *
 * @author Kovilin
 */
public class Relation_VO {

    public Relation_VO() {
    }
    
    private Long nID;
    
    private String sID_Private_Source;
    
    private String sName;

    public Long getnID() {
        return nID;
    }

    public String getsID_Private_Source() {
        return sID_Private_Source;
    }

    public String getsName() {
        return sName;
    }

    public void setnID(Long nID) {
        this.nID = nID;
    }

    public void setsID_Private_Source(String sID_Private_Source) {
        this.sID_Private_Source = sID_Private_Source;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }
    
}
