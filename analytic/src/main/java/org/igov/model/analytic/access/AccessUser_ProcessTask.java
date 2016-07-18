/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.analytic.access;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.igov.model.analytic.process.ProcessTask;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class AccessUser_ProcessTask extends AbstractEntity{
    
    @JsonProperty(value = "oAccessUser")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_AccessUser")
    private AccessUser oAccessUser;
    
    @JsonProperty(value = "oProcessTask")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_ProcessTask")
    private ProcessTask oProcessTask;

    public AccessUser getoAccessUser() {
        return oAccessUser;
    }

    public void setoAccessUser(AccessUser oAccessUser) {
        this.oAccessUser = oAccessUser;
    }

    public ProcessTask getoProcessTask() {
        return oProcessTask;
    }

    public void setoProcessTask(ProcessTask oProcessTask) {
        this.oProcessTask = oProcessTask;
    }
    
}
