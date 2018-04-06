package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Alex
 */
public class Region {
    
    @JsonProperty("name")
    private String sName;
    
    @JsonProperty("koatuu")
    private String sKoatuu;
    
    @JsonProperty("id")
    private String sID;

    public Region() {
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsKoatuu() {
        return sKoatuu;
    }

    public void setsKoatuu(String sKoatuu) {
        this.sKoatuu = sKoatuu;
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }
    
    
}
