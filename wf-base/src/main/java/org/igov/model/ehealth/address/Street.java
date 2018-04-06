package org.igov.model.ehealth.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author Alex
 */
@javax.persistence.Entity
public class Street {
    
    @JsonProperty("type")
    private String sType;
        
    @JsonProperty("settlement_id")
    private String sSettlement_id;
    
    @JsonProperty("name")
    private String sName;
    
    @JsonProperty("id")
    private String sID;
    
    @JsonProperty("aliases")
    private List<String> asAliases;

    public Street() {
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getsSettlement_id() {
        return sSettlement_id;
    }

    public void setsSettlement_id(String sSettlement_id) {
        this.sSettlement_id = sSettlement_id;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public List<String> getAsAliases() {
        return asAliases;
    }

    public void setAsAliases(List<String> asAliases) {
        this.asAliases = asAliases;
    }
    
    
}
