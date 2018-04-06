package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Alex
 */
@javax.persistence.Entity
public class District {
    
    @JsonProperty("region_id")
    private String sRegion_id;
    
    @JsonProperty("region")
    private String sRegion;
    
    @JsonProperty("name")
    private String sName;
    
    @JsonProperty("koatuu")
    private String sKoatuu;
    
    @JsonProperty("id")
    private String sID;

    public District() {
    }

    public String getsRegion_id() {
        return sRegion_id;
    }

    public void setsRegion_id(String sRegion_id) {
        this.sRegion_id = sRegion_id;
    }

    public String getsRegion() {
        return sRegion;
    }

    public void setsRegion(String sRegion) {
        this.sRegion = sRegion;
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
