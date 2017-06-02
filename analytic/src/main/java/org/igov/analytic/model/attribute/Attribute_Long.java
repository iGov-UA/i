package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created by dpekach on 11.05.17.
 */

@Entity
public class Attribute_Long extends AbstractEntity {

    @JsonProperty
    private Long nValue;

    @JsonIgnore
    @OneToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "nID_Attribute")
    private Attribute oAttribute;

    public Long getnValue() {
        return nValue;
    }

    public void setnValue(Long nValue) {
        this.nValue = nValue;
    }

    public Attribute getoAttribute() {
        return oAttribute;
    }

    public void setoAttribute(Attribute attribute) {
        this.oAttribute = attribute;
    }
}
