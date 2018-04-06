package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
/**
 *
 * @author Alex
 */
public class Settlement implements Serializable {

    @JsonProperty("type")
    private String type;

    @JsonProperty("region_id")
    private String region_id;

    @JsonProperty("region")
    private String region;

    @JsonProperty("parent_settlement_id")
    private String parent_settlement_id;

    @JsonProperty("parent_settlement")
    private String parent_settlement;

    @JsonProperty("name")
    private String name;
    
    @JsonProperty("mountain_group")
    private String mountain_group;
    
    @JsonProperty("koatuu")
    private String koatuu;
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("district_id")
    private String district_id;

    @JsonProperty("district")
    private String district;

    public Settlement() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getParent_settlement_id() {
        return parent_settlement_id;
    }

    public void setParent_settlement_id(String parent_settlement_id) {
        this.parent_settlement_id = parent_settlement_id;
    }

    public String getParent_settlement() {
        return parent_settlement;
    }

    public void setParent_settlement(String parent_settlement) {
        this.parent_settlement = parent_settlement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMountain_group() {
        return mountain_group;
    }

    public void setMountain_group(String mountain_group) {
        this.mountain_group = mountain_group;
    }

    public String getKoatuu() {
        return koatuu;
    }

    public void setKoatuu(String koatuu) {
        this.koatuu = koatuu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    
    
}
