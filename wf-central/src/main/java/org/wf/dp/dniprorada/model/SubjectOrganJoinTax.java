package org.wf.dp.dniprorada.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.wf.dp.dniprorada.base.model.Entity;

import javax.persistence.Column;

/**
 * @author NickVeremeichyk
 * @since 2015-11-24.
 */
@javax.persistence.Entity
public class SubjectOrganJoinTax extends Entity {

    /**
     * sID_UA - ИД-номер Код, в Украинском классификкаторе (уникальный-ключ, String < 30 символов)
     */
    @JsonProperty(value = "sID_UA")
    @Column(name = "sID_UA", length = 30, unique = true)
    private String sIdUA;

    /**
     * sName_UA - название на украинском (уникальный, String < 190 символов)
     */
    @JsonProperty(value = "sName_UA")
    @Column(name = "sName_UA", length = 190, unique = false)
    private String sNameUA;

    public String getsIdUA() {
        return sIdUA;
    }

    public void setsIdUA(String sIdUA) {
        this.sIdUA = sIdUA;
    }

    public String getsNameUA() {
        return sNameUA;
    }

    public void setsNameUA(String sNameUA) {
        this.sNameUA = sNameUA;
    }

    @Override
    public String toString() {
        return "SubjectOrganJoinTax{nID=" + getId() + ", "
                + "sID_UA='" + getsIdUA() + "\'" + ", "
                + "sName_UA='" + getsNameUA() + "\'" + "}";

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        SubjectOrganJoinTax subjectOrganJoinTax = (SubjectOrganJoinTax) obj;

        if (getsIdUA() != null ? !getsIdUA().equals(subjectOrganJoinTax.getsIdUA()) : subjectOrganJoinTax.getsIdUA() != null)
            return false;
        if (getsNameUA() != null ? !getsNameUA().equals(subjectOrganJoinTax.getsNameUA()) : subjectOrganJoinTax.getsNameUA() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getsIdUA() != null ? getsIdUA().hashCode() : 0;
        result = 31 * result + (getsNameUA() != null ? getsNameUA().hashCode() : 0);
        return result;
    }
}
