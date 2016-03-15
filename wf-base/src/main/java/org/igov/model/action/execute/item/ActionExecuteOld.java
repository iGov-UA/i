package org.igov.model.action.execute.item;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.igov.model.core.Entity;
import org.igov.util.JSON.JsonDateDeserializer;
import org.igov.util.JSON.JsonDateSerializer;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@javax.persistence.Entity
public class ActionExecuteOld extends Entity {

    @JsonProperty(value = "nID_ActionExecuteStatus")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nID_ActionExecuteStatus", nullable = false)
    private ActionExecuteStatus actionExecuteStatus;

    @JsonProperty(value = "oDateMake")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "oDateMake", nullable = false)
    private DateTime oDateMake;

    @JsonProperty(value = "oDateEdit")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "oDateEdit", nullable = true)
    private DateTime oDateEdit;

    @JsonProperty(value = "nTry")
    @Column(name = "nTry", nullable = true)
    private Integer nTry;

    @JsonProperty(value = "sObject")
    @Column(name = "sObject", nullable = true)
    private String sObject;

    @JsonProperty(value = "sMethod")
    @Column(name = "sMethod", nullable = true)
    private String sMethod;

    @Type(type = "org.hibernate.type.BinaryType")
    @JsonProperty(value = "soRequest")
    @Column(name = "soRequest", nullable = true)
    private byte[] soRequest;

    @JsonProperty(value = "smParam")
    @Column(name = "smParam", nullable = true)
    private String smParam;

    @JsonProperty(value = "sReturn")
    @Column(name = "sReturn", nullable = true)
    private String sReturn;

    public ActionExecuteStatus getActionExecuteStatus() {
        return actionExecuteStatus;
    }

    public void setActionExecuteStatus(ActionExecuteStatus actionExecuteStatus) {
        this.actionExecuteStatus = actionExecuteStatus;
    }

    public DateTime getoDateMake() {
        return oDateMake;
    }

    public void setoDateMake(DateTime oDateMake) {
        this.oDateMake = oDateMake;
    }

    public DateTime getoDateEdit() {
        return oDateEdit;
    }

    public void setoDateEdit(DateTime oDateEdit) {
        this.oDateEdit = oDateEdit;
    }

    public Integer getnTry() {
        return nTry;
    }

    public void setnTry(Integer nTry) {
        this.nTry = nTry;
    }

    public String getsMethod() {
        return sMethod;
    }

    public void setsMethod(String sMethod) {
        this.sMethod = sMethod;
    }

    public byte[] getSoRequest() {
        return soRequest;
    }

    public void setSoRequest(byte[] soRequest) {
        this.soRequest = soRequest;
    }

    public String getSmParam() {
        return smParam;
    }

    public void setSmParam(String smParam) {
        this.smParam = smParam;
    }

    public String getsReturn() {
        return sReturn;
    }

    public void setsReturn(String sReturn) {
        this.sReturn = sReturn;
    }

    public String getsObject() {
        return sObject;
    }

    public void setsObject(String sObject) {
        this.sObject = sObject;
    }
}
