/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class Attribute_File extends AbstractEntity{
    			
    @JsonProperty(value = "sID_Data")
    @Column
    private String sID_Data;
    
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE})
    @JoinColumn(name = "nID_Attribute")
    Attribute oAttribute;
    
    @JsonProperty(value = "sFileName")
    @Column
    private String sFileName;
    
    @JsonProperty(value = "sExtName")
    @Column
    private String sExtName;
    
    @JsonProperty(value = "sContentType")
    @Column
    private String sContentType;
    
    public Attribute getoAttribute() {
        return oAttribute;
    }

    public void setoAttribute(Attribute oAttribute) {
        this.oAttribute = oAttribute;
    }

    public String getsID_Data() {
        return sID_Data;
    }

    public void setsID_Data(String sID_Data) {
        this.sID_Data = sID_Data;
    }

    public String getsFileName() {
        return sFileName;
    }

    public void setsFileName(String sFileName) {
        this.sFileName = sFileName;
    }

    public String getsExtName() {
        return sExtName;
    }

    public void setsExtName(String sExtName) {
        this.sExtName = sExtName;
    }

    public String getsContentType() {
        return sContentType;
    }

    public void setsContentType(String sContentType) {
        this.sContentType = sContentType;
    }
    
}
