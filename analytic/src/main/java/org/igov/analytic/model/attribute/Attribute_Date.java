/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author olga
 */
@javax.persistence.Entity
public class Attribute_Date extends AbstractEntity {

    @JsonProperty(value = "oValue")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oValue;

    @JsonIgnore
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "nID_Attribute")
    Attribute oAttribute;

    public DateTime getoValue() {
        return oValue;
    }

    public void setoValue(DateTime oValue) {
        this.oValue = oValue;
    }

    public Attribute getoAttribute() {
        return oAttribute;
    }

    public void setoAttribute(Attribute oAttribute) {
        this.oAttribute = oAttribute;
    }

}
