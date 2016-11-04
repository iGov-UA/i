package org.igov.model.action.task.core;

/**
 * @author Khalikov
 *
 *класс для сериализации JSON-объекта
 */
public class ProcessDTOCover {

    private final String sName;
    private final String sBP;
    private final Long nID;
    private final String sDateCreate;
    private final String sDateClose;

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
