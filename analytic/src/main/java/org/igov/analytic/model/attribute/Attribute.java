/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.model.core.NamedEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class Attribute extends NamedEntity {

    @JsonProperty(value = "sID_")
    @Column
    private String sID_;

    @JsonProperty(value = "oAttributeType")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeType")
    private AttributeType oAttributeType;
    
    
    @JsonProperty(value = "oAttributeName")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeName")
    private AttributeName oAttributeName;

    @JsonProperty(value = "oAttributeTypeCustom")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeTypeCustom")
    private AttributeTypeCustom oAttributeTypeCustom;

	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "nID_Process")
    private Process oProcess;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "nID_ProcessTask")
    private ProcessTask oProcessTask;

    @JsonProperty(value = "oAttribute_Boolean")
    @OneToOne(mappedBy="oAttribute")
    Attribute_Boolean oAttribute_Boolean;

    @JsonProperty(value = "oAttribute_Date")
    @OneToOne(mappedBy="oAttribute")
    Attribute_Date oAttribute_Date;

    @JsonProperty(value = "oAttribute_Float")
    @OneToOne(mappedBy="oAttribute")
    Attribute_Float oAttribute_Float;

    @JsonProperty(value = "oAttribute_Integer")
    @OneToOne(mappedBy="oAttribute")
    Attribute_Integer oAttribute_Integer;

    @JsonProperty(value = "oAttribute_StringShort")
    @OneToOne(mappedBy="oAttribute")
    Attribute_StringShort oAttribute_StringShort;

    @JsonProperty(value = "oAttribute_StringLong")
    @OneToOne(mappedBy="oAttribute")
    Attribute_StringLong oAttribute_StringLong;

    @JsonProperty(value = "oAttribute_File")
    @OneToOne(mappedBy="oAttribute")
    Attribute_File oAttribute_File;

    public String getsID_() {
        return sID_;
    }

    public void setsID_(String sID_) {
        this.sID_ = sID_;
    }

    public AttributeType getoAttributeType() {
        return oAttributeType;
    }

    public void setoAttributeType(AttributeType oAttributeType) {
        this.oAttributeType = oAttributeType;
    }

    public AttributeTypeCustom getoAttributeTypeCustom() {
        return oAttributeTypeCustom;
    }

    public void setoAttributeTypeCustom(AttributeTypeCustom oAttributeTypeCustom) {
        this.oAttributeTypeCustom = oAttributeTypeCustom;
    }

    public Process getoProcess() {
        return oProcess;
    }

    public void setoProcess(Process oProcess) {
        this.oProcess = oProcess;
    }

    public ProcessTask getoProcessTask() {
        return oProcessTask;
    }

    public void setoProcessTask(ProcessTask oProcessTask) {
        this.oProcessTask = oProcessTask;
    }

    public Attribute_Boolean getoAttribute_Boolean() {
        return oAttribute_Boolean;
    }

    public void setoAttribute_Boolean(Attribute_Boolean oAttribute_Boolean) {
        this.oAttribute_Boolean = oAttribute_Boolean;
    }

    public Attribute_Date getoAttribute_Date() {
        return oAttribute_Date;
    }

    public void setoAttribute_Date(Attribute_Date oAttribute_Date) {
        this.oAttribute_Date = oAttribute_Date;
    }

    public Attribute_Float getoAttribute_Float() {
        return oAttribute_Float;
    }

    public void setoAttribute_Float(Attribute_Float oAttribute_Float) {
        this.oAttribute_Float = oAttribute_Float;
    }

    public Attribute_Integer getoAttribute_Integer() {
        return oAttribute_Integer;
    }

    public void setoAttribute_Integer(Attribute_Integer oAttribute_Integer) {
        this.oAttribute_Integer = oAttribute_Integer;
    }

    public Attribute_StringShort getoAttribute_StringShort() {
        return oAttribute_StringShort;
    }

    public void setoAttribute_StringShort(Attribute_StringShort oAttribute_StringShort) {
        this.oAttribute_StringShort = oAttribute_StringShort;
    }

    public Attribute_StringLong getoAttribute_StringLong() {
        return oAttribute_StringLong;
    }

    public void setoAttribute_StringLong(Attribute_StringLong oAttribute_StringLong) {
        this.oAttribute_StringLong = oAttribute_StringLong;
    }

    public Attribute_File getoAttribute_File() {
        return oAttribute_File;
    }

    public void setoAttribute_File(Attribute_File oAttribute_File) {
        this.oAttribute_File = oAttribute_File;
    }
    
    public AttributeName getoAttributeName() {
  		return oAttributeName;
  	}

  	public void setoAttributeName(AttributeName oAttributeName) {
  		this.oAttributeName = oAttributeName;
  	}


}
