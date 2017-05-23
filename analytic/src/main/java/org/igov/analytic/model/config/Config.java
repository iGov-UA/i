/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.NamedEntity;

/**
 * @author olga
 */
@javax.persistence.Entity
public class Config extends NamedEntity {

    @JsonProperty(value = "sValue")
    String sValue;

    public String getsValue() {
        return sValue;
    }

    public void setsValue(String sValue) {
        this.sValue = sValue;
    }


    @Override
    public String toString() {
        return "Config{" +
                "sValue='" + sValue + '\'' +
                '}';
    }
}
