
package org.igov.model.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import org.igov.model.core.Entity;

import javax.persistence.Column;

@javax.persistence.Entity
public class ObjectCustoms extends Entity
{
    @JsonProperty(value = "sID_UA")
    @Column(name="sID_UA", length=30, nullable=false)
    private String sID_UA;
    
    
    @JsonProperty(value = "sName_UA")
    @Column(name="sName_UA", length=5000, nullable=false)
    private String sName_UA;
    
    
    @JsonProperty(value = "sMeasure_UA")
    @Column(name="sMeasure_UA", length=20)
    private String sMeasure_UA;

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

    public String getsMeasure_UA() {
        return sMeasure_UA;
    }

    public void setsMeasure_UA(String sMeasure_UA) {
        this.sMeasure_UA = sMeasure_UA;
    }

    @Override
    public String toString() {
        return "ObjectCustoms{" + "sID_UA=" + sID_UA + ", sName_UA=" + sName_UA + ", sMeasure_UA=" + sMeasure_UA + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.sID_UA);
        hash = 79 * hash + Objects.hashCode(this.sName_UA);
        hash = 79 * hash + Objects.hashCode(this.sMeasure_UA);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectCustoms other = (ObjectCustoms) obj;
        if (!Objects.equals(this.sID_UA, other.sID_UA)) {
            return false;
        }
        if (!Objects.equals(this.sName_UA, other.sName_UA)) {
            return false;
        }
        if (!Objects.equals(this.sMeasure_UA, other.sMeasure_UA)) {
            return false;
        }
        return true;
    }
    
    
}
