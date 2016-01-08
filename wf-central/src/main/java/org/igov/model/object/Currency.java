package org.igov.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;

/**
 * @author grigoriy-romanenko
 */
@javax.persistence.Entity
public class Currency extends Entity {

    @JsonProperty(value = "sID_UA")
    @Column(length = 10, unique = true)
    private String sID_UA;

    @JsonProperty(value = "sName_UA")
    @Column(length = 200, unique = true)
    private String sName_UA;

    @JsonProperty(value = "sName_EN")
    @Column(length = 200, unique = true)
    private String sName_EN;

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

    public String getsName_EN() {
        return sName_EN;
    }

    public void setsName_EN(String sName_EN) {
        this.sName_EN = sName_EN;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Currency currency = (Currency) object;
        if (sID_UA != null ? !sID_UA.equals(currency.sID_UA) : currency.sID_UA != null) {
            return false;
        }
        if (sName_UA != null ? !sName_UA.equals(currency.sName_UA) : currency.sName_UA != null) {
            return false;
        }
        if (sName_EN != null ? !sName_EN.equals(currency.sName_EN) : currency.sName_EN != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = sID_UA != null ? sID_UA.hashCode() : 0;
        hash = 31 * hash + (sName_UA != null ? sName_UA.hashCode() : 0);
        hash = 31 * hash + (sName_EN != null ? sName_EN.hashCode() : 0);
        return hash;
    }
}
