package org.igov.model.action.item;

import org.igov.model.core.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import static org.igov.io.fs.FileSystemData.getSmartPathFileContent_ActionItem;

/**
 * Tag of Service
 * User: goodg_000
 * Date: 19.06.2016
 * Time: 20:27
 */
@javax.persistence.Entity
public class ServiceTag extends AbstractEntity {

    @Column
    private String sID;

    @Column
    private String sName_UA;

    @Column
    private String sName_RU;
    
    @Column
    private Long nOrder;
            
    @Column
    private String sLinkImage;
    
    @Column
    private String sNote;

    @ManyToOne(targetEntity = ServiceTagType.class)
    @JoinColumn(name="nID_ServiceTagType", nullable = false, updatable = false)
    private ServiceTagType serviceTagType;

    public String getsID() {
        return sID;
    }
    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getsName_UA() {
        return sName_UA;
    }
    public void setsName_UA(String sName_UA) {
        this.sName_UA = sName_UA;
    }

    public String getsName_RU() {
        return sName_RU;
    }
    public void setsName_RU(String sName_RU) {
        this.sName_RU = sName_RU;
    }

    public ServiceTagType getServiceTagType() {
        return serviceTagType;
    }
    public void setServiceTagType(ServiceTagType serviceTagType) {
        this.serviceTagType = serviceTagType;
    }

    public Long getnOrder() {
        return nOrder;
    }

    public void setnOrder(Long nOrder) {
        this.nOrder = nOrder;
    }

    public String getsLinkImage() {
        return sLinkImage;
    }

    public void setsLinkImage(String sLinkImage) {
        this.sLinkImage = sLinkImage;
    }

    public String getsNote() {
        //return sNote;
        return getSmartPathFileContent_ActionItem(sNote, "Tag", getId());
    }
    
    public void setsNote(String sNote) {
        this.sNote = sNote;
    }
    
}
