package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 *
 * @author Alex
 */
public class Street implements Serializable {
    
    @JsonProperty("type")
    private String type;
        
    @JsonProperty("settlement_id")
    private String settlement_id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("id")
    private String id;

    public Street() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSettlement_id() {
        return settlement_id;
    }

    public void setSettlement_id(String settlement_id) {
        this.settlement_id = settlement_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
