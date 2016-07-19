/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.analytic.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.igov.model.analytic.process.Process;
import org.igov.model.analytic.process.ProcessTask;
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
    Attribute oAttribute_Boolean;
    
    @JsonProperty(value = "oAttribute_Date")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute oAttribute_Date;
    
    @JsonProperty(value = "oAttribute_Float")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute oAttribute_Float;
    
    @JsonProperty(value = "oAttribute_Integer")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute oAttribute_Integer;
    
    @JsonProperty(value = "oAttribute_StingShort")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute oAttribute_StingShort;
    
    @JsonProperty(value = "oAttribute_StringLong")
    @OneToOne(fetch = FetchType.EAGER)
    Attribute oAttribute_StringLong;

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

    public Attribute getoAttribute_Boolean() {
        return oAttribute_Boolean;
    }

    public void setoAttribute_Boolean(Attribute oAttribute_Boolean) {
        this.oAttribute_Boolean = oAttribute_Boolean;
    }

    public Attribute getoAttribute_Date() {
        return oAttribute_Date;
    }

    public void setoAttribute_Date(Attribute oAttribute_Date) {
        this.oAttribute_Date = oAttribute_Date;
    }

    public Attribute getoAttribute_Float() {
        return oAttribute_Float;
    }

    public void setoAttribute_Float(Attribute oAttribute_Float) {
        this.oAttribute_Float = oAttribute_Float;
    }

    public Attribute getoAttribute_Integer() {
        return oAttribute_Integer;
    }

    public void setoAttribute_Integer(Attribute oAttribute_Integer) {
        this.oAttribute_Integer = oAttribute_Integer;
    }

    public Attribute getoAttribute_StingShort() {
        return oAttribute_StingShort;
    }

    public void setoAttribute_StingShort(Attribute oAttribute_StingShort) {
        this.oAttribute_StingShort = oAttribute_StingShort;
    }

    public Attribute getoAttribute_StringLong() {
        return oAttribute_StringLong;
    }

    public void setoAttribute_StringLong(Attribute oAttribute_StringLong) {
        this.oAttribute_StringLong = oAttribute_StringLong;
    }
    			   
}
