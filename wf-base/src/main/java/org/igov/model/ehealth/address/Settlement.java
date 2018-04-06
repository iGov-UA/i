package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * @author Alex
 */
@javax.persistence.Entity
public class Settlement {

    @JsonProperty("type")
    private String sType;

    @JsonProperty("region_id")
    private String sRegion_id;

    @JsonProperty("region")
    private String sRegion;

    @JsonProperty("parent_settlement_id")
    private String sParent_settlement_id;

    @JsonProperty("parent_settlement")
    private String sParent_settlement;

    @JsonProperty("name")
    private String sName;
    
    @JsonProperty("mountain_group")
    private String sMountain_group;
    
    @JsonProperty("koatuu")
    private String sKoatuu;
    
    @JsonProperty("id")
    private String sID;
    
    @JsonProperty("district_id")
    private String sDistrict_id;

    @JsonProperty("district")
    private String District;

    public Settlement() {
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
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

    public String getsParent_settlement_id() {
        return sParent_settlement_id;
    }

    public void setsParent_settlement_id(String sParent_settlement_id) {
        this.sParent_settlement_id = sParent_settlement_id;
    }

    public String getsParent_settlement() {
        return sParent_settlement;
    }

    public void setsParent_settlement(String sParent_settlement) {
        this.sParent_settlement = sParent_settlement;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsMountain_group() {
        return sMountain_group;
    }

    public void setsMountain_group(String sMountain_group) {
        this.sMountain_group = sMountain_group;
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

    public String getsDistrict_id() {
        return sDistrict_id;
    }

    public void setsDistrict_id(String sDistrict_id) {
        this.sDistrict_id = sDistrict_id;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String District) {
        this.District = District;
    }
    
}
