package org.igov.model.action.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kovilin
 */
public class TaskAttachVO {
    
    //@JsonProperty(value = "sID_StorageType")
    private String sID_StorageType;
    
    //@JsonProperty(value = "sKey")
    private String sKey;
    
    //@JsonProperty(value = "sVersion")
    private String sVersion;
    
    //@JsonProperty(value = "sDateTime")
    private String sDateTime;
    
    //@JsonProperty(value = "sFileNameAndExt")
    private String sFileNameAndExt;
    
    //@JsonProperty(value = "sContentType")
    private String sContentType;
    
    //@JsonProperty(value = "nBytes")
    private String nBytes;
    
    //@JsonProperty(value = "bSigned")
    private boolean bSigned;
    
    //@JsonProperty(value = "aAttribute")
    //private List<Map<String, Object>> aAttribute;

    public TaskAttachVO() {
    }

    public String getsID_StorageType() {
        return sID_StorageType;
    }

    public String getsKey() {
        return sKey;
    }

    public String getsVersion() {
        return sVersion;
    }

    public String getsDateTime() {
        return sDateTime;
    }

    public String getsFileNameAndExt() {
        return sFileNameAndExt;
    }

    public String getsContentType() {
        return sContentType;
    }

    public String getnBytes() {
        return nBytes;
    }

    public boolean isbSigned() {
        return bSigned;
    }

    /*public List<Map<String, Object>> getaAttribute() {
        return aAttribute;
    }*/

    public void setsID_StorageType(String sID_StorageType) {
        this.sID_StorageType = sID_StorageType;
    }

    public void setsKey(String sKey) {
        this.sKey = sKey;
    }

    public void setsVersion(String sVersion) {
        this.sVersion = sVersion;
    }

    public void setsDateTime(String sDateTime) {
        this.sDateTime = sDateTime;
    }

    public void setsFileNameAndExt(String sFileNameExt) {
        this.sFileNameAndExt = sFileNameExt;
    }

    public void setsContentType(String sContentType) {
        this.sContentType = sContentType;
    }

    public void setnBytes(String nBytes) {
        this.nBytes = nBytes;
    }

    public void setbSigned(boolean bSigned) {
        this.bSigned = bSigned;
    }

    /*public void setaAttribute(List<Map<String, Object>> aAttribute) {
        this.aAttribute = aAttribute;
    }*/
    
}
