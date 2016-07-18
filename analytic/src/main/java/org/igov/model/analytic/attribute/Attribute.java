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
import org.igov.model.analytic.process.Process;
import org.igov.model.analytic.process.ProcessTask;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class Attribute extends AbstractEntity{
    
    @JsonProperty(value = "oProcess")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeType")
    private AttributeType oAttributeType;
    
    @JsonProperty(value = "oProcess")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AttributeTypeCustom")
    private AttributeTypeCustom oAttributeTypeCustom;
    
    @JsonProperty(value = "oProcess")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_Process")
    private Process oProcess;
    
    @JsonProperty(value = "oProcess")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_ProcessTask")
    private ProcessTask oProcessTask;

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
    			   
}
