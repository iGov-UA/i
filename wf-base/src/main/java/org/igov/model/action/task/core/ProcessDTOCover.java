package org.igov.model.action.task.core;

/**
 * @author Khalikov
 *
 *класс для сериализации JSON-объекта
 */
public class ProcessDTOCover {

    private String sName;
    private String sBP;
    private Long nID;
    private String sDateCreate;
    private String sDateClose;

    public ProcessDTOCover(String sName, String sBP, Long nID, String sDateCreate, String sDateClose) {
        this.sName = sName;
        this.sBP = sBP;
        this.nID = nID;
        this.sDateCreate = sDateCreate;
        this.sDateClose = sDateClose;
    }

    public String getsName() {
        return sName;
    }

    public String getsBP() {
        return sBP;
    }

    public Long getnID() {
        return nID;
    }

    public String getsDateCreate() {
        return sDateCreate;
    }
    
    public String getsDateClose() {
        return sDateClose;
    }
    
}
