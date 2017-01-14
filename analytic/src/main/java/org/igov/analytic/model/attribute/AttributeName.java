/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import javax.persistence.Column;

import org.igov.model.core.NamedEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class AttributeName extends NamedEntity{ 
    
    @JsonProperty(value = "sID")
    @Column
    private String sID;
        
    @JsonProperty(value = "nOrder")
    @Column
    private Long nOrder;
    
    public Long getnOrder(){
        return nOrder;
    }

    public void setnOrder(Long nOrder){
        this.nOrder = nOrder;
    }
    
    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }
}
