/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class Attribute_Boolean extends AbstractEntity{
    
    @JsonProperty(value = "bValue")
    //@Type(type="yes_no")
    //@Column(length=1)
    Boolean bValue;
    
    @JsonProperty(value = "oAttribute")
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "oAttribute_Boolean")
    Attribute oAttribute;
    
    public Boolean getbValue() {
        return bValue;
    }

    public void setbValue(Boolean bValue) {
        this.bValue = bValue;
    }

    public Attribute getoAttribute() {
        return oAttribute;
    }

    public void setoAttribute(Attribute oAttribute) {
        this.oAttribute = oAttribute;
    }
   
}
