/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.analytic.model.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.ibatis.annotations.One;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.igov.analytic.model.access.AccessGroup;
import org.igov.analytic.model.access.AccessUser;
import org.igov.analytic.model.attribute.Attribute;
import org.igov.analytic.model.source.SourceDB;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.*;

/**
 * @author olga
 */
@javax.persistence.Entity
public class Process extends AbstractEntity {

    @JsonProperty(value = "sID_")
    @Column
    private String sID_;

    @JsonProperty(value = "oSourceDB")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_SourceDB")
    private SourceDB oSourceDB;

    @JsonProperty(value = "oDateStart")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oDateStart;

    @JsonProperty(value = "oDateFinish")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column
    private DateTime oDateFinish;

    @JsonProperty(value = "sID_Data")
    @Column
    private String sID_Data;

    @JsonProperty(value = "aAttribute")
    @OneToMany(mappedBy = "oProcess", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Attribute> aAttribute = new ArrayList<>();

    @JsonProperty(value = "aProcessTask")
    @OneToMany(mappedBy = "oProcess", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ProcessTask> aProcessTask = new ArrayList<>();

    @JsonProperty(value = "aAccessGroup")
    @ManyToMany(targetEntity = AccessGroup.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "AccessGroup_ProcessTask",
            joinColumns = @JoinColumn(name = "nID_Process"),
            inverseJoinColumns = @JoinColumn(name = "nID_AccessGroup"))
    private Set<AccessGroup> aAccessGroup = new HashSet<>();

    @JsonProperty(value = "aAccessUser")
    @ManyToMany(targetEntity = AccessUser.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "AccessUser_ProcessTask",
            joinColumns = @JoinColumn(name = "nID_Process"),
            inverseJoinColumns = @JoinColumn(name = "nID_AccessUser"))
    private Set<AccessUser> aAccessUser = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "oProcess", cascade = CascadeType.ALL)
    private CustomProcess customProcess;

    public String getsID_() {
        return sID_;
    }

    public void setsID_(String sID_) {
        this.sID_ = sID_;
    }

    public SourceDB getoSourceDB() {
        return oSourceDB;
    }

    public void setoSourceDB(SourceDB oSourceDB) {
        this.oSourceDB = oSourceDB;
    }

    public DateTime getoDateStart() {
        return oDateStart;
    }

    public void setoDateStart(DateTime oDateStart) {
        this.oDateStart = oDateStart;
    }

    public DateTime getoDateFinish() {
        return oDateFinish;
    }

    public void setoDateFinish(DateTime oDateFinish) {
        this.oDateFinish = oDateFinish;
    }

    public String getsID_Data() {
        return sID_Data;
    }

    public void setsID_Data(String sID_Data) {
        this.sID_Data = sID_Data;
    }

    public List<Attribute> getaAttribute() {
        return aAttribute;
    }

    public void setaAttribute(List<Attribute> aAttribute) {
        this.aAttribute = aAttribute;
    }

    public List<ProcessTask> getaProcessTask() {
        return aProcessTask;
    }

    public void setaProcessTask(List<ProcessTask> aProcessTask) {
        this.aProcessTask = aProcessTask;
    }

    public Set<AccessGroup> getaAccessGroup() {
        return aAccessGroup;
    }

    public void setaAccessGroup(Set<AccessGroup> aAccessGroup) {
        this.aAccessGroup = aAccessGroup;
    }

    public Set<AccessUser> getaAccessUser() {
        return aAccessUser;
    }

    public void setaAccessUser(Set<AccessUser> aAccessUser) {
        this.aAccessUser = aAccessUser;
    }

    public CustomProcess getCustomProcess() {
        return customProcess;
    }

    public void setCustomProcess(CustomProcess customProcess) {
        this.customProcess = customProcess;
    }

    @Override
    public String toString() {
        return "Process{" +
                "sID_='" + sID_ + '\'' +
                ", oSourceDB=" + oSourceDB +
                ", oDateStart=" + oDateStart +
                ", oDateFinish=" + oDateFinish +
                ", sID_Data='" + sID_Data + '\'' +
                ", aAttribute=" + Arrays.toString(aAttribute.toArray()) +
                ", aProcessTask=" + Arrays.toString(aProcessTask.toArray()) +
                ", aAccessGroup=" + aAccessGroup +
                ", aAccessUser=" + aAccessUser +
                '}';
    }
}
