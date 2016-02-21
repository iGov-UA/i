package org.igov.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * @author grigoriy-romanenko
 */
@javax.persistence.Entity
public class ObjectEarthTarget extends Entity {

    @JsonProperty(value = "sID_UA")
    @Column(length = 10, unique = true, nullable = false)
    private String sID_UA;

    @JsonProperty(value = "sName_UA")
    @Column(length = 500, unique = true, nullable = false)
    private String sName_UA;

    public String getsID_UA() {
        return sID_UA;
    }

    public void setsID_UA(String sID_UA) {
        this.sID_UA = sID_UA;
    }

    public String getsName_UA() {
        return sName_UA;
    }

    public void setsName_UA(String sName_UA) {
        this.sName_UA = sName_UA;
    }
}
