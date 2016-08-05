/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.access;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author olga
 */
@javax.persistence.Entity
public class AccessGroup extends AbstractEntity {

    @JsonProperty(value = "sID")
    @Column
    private String sID;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = ProcessTask.class)
    @JoinTable(name = "AccessGroup_ProcessTask",
            joinColumns = @JoinColumn(name = "nID_AccessGroup"),
            inverseJoinColumns = @JoinColumn(name = "nID_ProcessTask"))
    private List<ProcessTask> aProcessTask = new ArrayList<>();

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public List<ProcessTask> getaProcessTask() {
        return aProcessTask;
    }

    public void setaProcessTask(List<ProcessTask> aProcessTask) {
        this.aProcessTask = aProcessTask;
    }

}
