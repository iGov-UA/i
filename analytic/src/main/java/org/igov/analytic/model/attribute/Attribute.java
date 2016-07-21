/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class Attribute extends AbstractEntity{
    
    @JsonProperty(value = "oAttributeType")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeType")
    private AttributeType oAttributeType;
    
    @JsonProperty(value = "oAttributeTypeCustom")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeTypeCustom")
    private AttributeTypeCustom oAttributeTypeCustom;
    
    @JsonProperty(value = "oProcess")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Process")
    private Process oProcess;
    
    @JsonProperty(value = "oProcessTask")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_ProcessTask")
    private ProcessTask oProcessTask;
    
    @JsonProperty(value = "oAttribute_Boolean")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_Boolean oAttribute_Boolean;
    
    @JsonProperty(value = "oAttribute_Date")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_Date oAttribute_Date;
    
    @JsonProperty(value = "oAttribute_Float")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_Float oAttribute_Float;
    
    @JsonProperty(value = "oAttribute_Integer")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_Integer oAttribute_Integer;
    
    @JsonProperty(value = "oAttribute_StingShort")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_StingShort oAttribute_StingShort;
    
    @JsonProperty(value = "oAttribute_StringLong")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_StringLong oAttribute_StringLong;
    
    @JsonProperty(value = "oAttribute_File")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute_File oAttribute_File;

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

    public Attribute_StingShort getoAttribute_StingShort() {
        return oAttribute_StingShort;
    }

    public void setoAttribute_StingShort(Attribute_StingShort oAttribute_StingShort) {
        this.oAttribute_StingShort = oAttribute_StingShort;
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
    			   
}
