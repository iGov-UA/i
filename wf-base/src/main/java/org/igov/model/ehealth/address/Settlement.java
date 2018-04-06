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

    @JsonProperty("region")
    private String region;

    @JsonProperty("parent_settlement")
    private String parent_settlement;

    @JsonProperty("name")
    private String name;

    @JsonProperty("koatuu")
    private String koatuu;

    @JsonProperty("id")
    private String id;

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
