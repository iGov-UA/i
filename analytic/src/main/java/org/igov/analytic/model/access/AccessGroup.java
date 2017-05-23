/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.access;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import org.igov.analytic.model.process.Process;
import org.igov.analytic.model.process.ProcessTask;
import org.igov.model.core.AbstractEntity;

/**
 * @author olga
 */
@Entity
public class AccessGroup extends AbstractEntity{

    @JsonProperty(value = "sID")
    @Column
    private String sID;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = ProcessTask.class, mappedBy = "aAccessGroup")
    private Set<ProcessTask> aProcessTask = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Process.class, mappedBy = "aAccessGroup")
    private Set<Process> aProcess = new HashSet<>();

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public Set<ProcessTask> getaProcessTask() {
        return aProcessTask;
    }

    public void setaProcessTask(Set<ProcessTask> aProcessTask) {
        this.aProcessTask = aProcessTask;
    }

    public Set<Process> getaProcess() {
        return aProcess;
    }

    public void setaProcess(Set<Process> aProcess) {
        this.aProcess = aProcess;
    }

    public void addaProcess(Process process) {
        aProcess.add(process);
    }

    public void addaProcessTask(ProcessTask processTask) {
        aProcessTask.add(processTask);
    }
}
