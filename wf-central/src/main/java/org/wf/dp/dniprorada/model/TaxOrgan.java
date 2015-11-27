package org.wf.dp.dniprorada.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.wf.dp.dniprorada.base.model.Entity;

import javax.persistence.Column;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
@javax.persistence.Entity
public class TaxOrgan extends Entity {

    /**
     * sID_UA - ИД-номер Код, в Украинском классификкаторе (уникальный-ключ, String < 30 символов)
     */
    @JsonProperty(value = "sID_UA")
    @Column(length = 30, unique = true)
    private String sID_UA;

    /**
     * sName_UA - название на украинском (уникальный, String < 100 символов)
     */
    @JsonProperty(value = "sName_UA")
    @Column(length = 100, unique = true)
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

    @Override
    public String toString() {
        return "TaxOrgan{nID=" + getId() + ", "
                + "sID_UA='" + getsID_UA() + "\'" + ", "
                + "sName_UA='" + getsName_UA() + "\'" + "}";

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        TaxOrgan taxOrgan = (TaxOrgan) obj;

        if (getsID_UA() != null ? !getsID_UA().equals(taxOrgan.getsID_UA()) : taxOrgan.getsID_UA() != null)
            return false;
        if (getsName_UA() != null ? !getsName_UA().equals(taxOrgan.getsName_UA()) : taxOrgan.getsName_UA() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getsID_UA() != null ? getsID_UA().hashCode() : 0;
        result = 31 * result + (getsName_UA() != null ? getsName_UA().hashCode() : 0);
        return result;
    }
}
