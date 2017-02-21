package org.igov.model.action.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.igov.model.core.AbstractEntity;
import org.igov.util.JSON.JsonDateTimeDeserializer;
import org.igov.util.JSON.JsonDateTimeSerializer;

import javax.persistence.Column;
import javax.persistence.Transient;

@javax.persistence.Entity
public class HistoryEvent_Service extends AbstractEntity {

    @JsonProperty(value = "sID")
    @Column
    private String sID;

    //    @JsonProperty(value="nID_Protected")
    //    @Column
    private transient Long nID_Protected;

    @JsonProperty(value = "nID_Task")
    @Column
    private Long nID_Process;

    @JsonProperty(value = "nID_Subject")
    @Column
    private Long nID_Subject;

    @JsonProperty(value = "sUserTaskName")
    @Column
    private String sUserTaskName;

    @JsonProperty(value = "sDate")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "sDate")
    private DateTime sDate;

    @JsonProperty(value = "nID_Service")
    @Column(name = "nID_Service")
    private Long nID_Service;

    @JsonProperty(value = "nID_Region")
    @Column(name = "nID_Region")
    private Long nID_Region;

    @JsonProperty(value = "sID_UA")
    @Column(name = "sID_UA")
    private String sID_UA;

    @JsonProperty(value = "nRate")
    @Column(name = "nRate")
    private Integer nRate;

    @JsonProperty(value = "soData")
    @Column(name = "soData")
    private String soData;

    @JsonProperty(value = "sToken")
    @Column(name = "sToken")
    private String sToken;

    @JsonProperty(value = "sHead")
    @Column(name = "sHead")
    private String sHead;

    @JsonProperty(value = "sBody")
    @Column(name = "sBody")
    private String sBody;

    @JsonProperty(value = "nTimeMinutes")
    @Column(name = "nTimeMinutes")
    private Integer nTimeMinutes;

    @JsonProperty(value = "sID_Order")
    @Column(name = "sID_Order")//, nullable = false, unique = true)
    private String sID_Order;

    @JsonProperty(value = "nID_Server")
    @Column(name = "nID_Server", nullable = false)
    private Integer nID_Server;

    @JsonProperty(value = "nID_Proccess_Feedback")
    @Column(name = "nID_Proccess_Feedback")
    private Long nID_Proccess_Feedback;

    @JsonProperty(value = "nID_Proccess_Escalation")
    @Column(name = "nID_Proccess_Escalation")
    private Long nID_Proccess_Escalation;

    @JsonProperty(value = "sID_Rate_Indirectly")
    @Column(name = "sID_Rate_Indirectly")
    private String sID_Rate_Indirectly;

    @JsonProperty(value = "nID_StatusType")
    @Column(name = "nID_StatusType")
    private Long nID_StatusType = 13L;

    @JsonProperty(value = "nID_ServiceData")
    @Column(name = "nID_ServiceData")
    private Long nID_ServiceData;

    @JsonProperty(value = "sID_StatusType")
    @Transient
    private String sID_StatusType;

    @JsonProperty(value = "sName_UA_StatusType")
    @Transient
    private String sName_UA_StatusType;

    @JsonProperty(value = "sDateCreate")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "sDateCreate", length = 25)
    private DateTime sDateCreate;

    @JsonProperty(value = "sDateClose")
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
    @Type(type = DATETIME_TYPE)
    @Column(name = "sDateClose", length = 25)
    private DateTime sDateClose;

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public Long getnID_Protected() {
        return nID_Protected;
    }

    public void setnID_Protected(Long nID_Protected) {
        this.nID_Protected = nID_Protected;
    }

    public Long getnID_Process() {
        return nID_Process;
    }

    public void setnID_Process(Long nID_Process) {
        this.nID_Process = nID_Process;
    }

    public Long getnID_Subject() {
        return nID_Subject;
    }

    public void setnID_Subject(Long nID_Subject) {
        this.nID_Subject = nID_Subject;
    }

    public String getsUserTaskName() {
        return sUserTaskName;
    }

    public void setsUserTaskName(String sUserTaskName) {
        this.sUserTaskName = sUserTaskName;
    }

    public DateTime getsDate() {
        return sDate;
    }

    public void setsDate(DateTime sDate) {
        this.sDate = sDate;
    }

    public Long getnID_Service() {
        return nID_Service;
    }

    public void setnID_Service(Long nID_Service) {
        this.nID_Service = nID_Service;
    }

    public Long getnID_Region() {
        return nID_Region;
    }

    public void setnID_Region(Long nID_Region) {
        this.nID_Region = nID_Region;
    }

    public String getsID_UA() {
        return sID_UA;
    }

    public void setsID_UA(String sID_UA) {
        this.sID_UA = sID_UA;
    }

    public Integer getnRate() {
        return nRate;
    }

    public void setnRate(Integer nRate) {
        this.nRate = nRate;
    }

    public String getSoData() {
        return soData;
    }

    public void setSoData(String soData) {
        this.soData = soData == null || "".equals(soData) ? "[]" : soData;
    }

    public String getsToken() {
        return sToken;
    }

    public void setsToken(String sToken) {
        this.sToken = sToken;
    }

    public String getsHead() {
        return sHead;
    }

    public void setsHead(String sHead) {
        this.sHead = sHead;
    }

    public String getsBody() {
        return sBody;
    }

    public void setsBody(String sBody) {
        this.sBody = sBody;
    }

    public Integer getnTimeMinutes() {
        return nTimeMinutes;
    }

    public void setnTimeMinutes(Integer nTimeMinutes) {
        this.nTimeMinutes = nTimeMinutes;
    }

    public String getsID_Order() {
        return sID_Order;
    }

    public void setsID_Order(String sID_Order) {
        this.sID_Order = sID_Order;
    }

    public Integer getnID_Server() {
        return nID_Server;
    }

    public void setnID_Server(Integer nID_Server) {
        this.nID_Server = nID_Server != null ? nID_Server : 0;
    }

    public Long getnID_Proccess_Feedback() {
        return nID_Proccess_Feedback;
    }

    public void setnID_Proccess_Feedback(Long nID_Proccess_Feedback) {
        this.nID_Proccess_Feedback = nID_Proccess_Feedback;
    }

    public Long getnID_Proccess_Escalation() {
        return nID_Proccess_Escalation;
    }

    public void setnID_Proccess_Escalation(Long nID_Proccess_Escalation) {
        this.nID_Proccess_Escalation = nID_Proccess_Escalation;
    }

    public String getsID_Rate_Indirectly() {
        return sID_Rate_Indirectly;
    }

    public void setsID_Rate_Indirectly(String sID_Rate_Indirectly) {
        this.sID_Rate_Indirectly = sID_Rate_Indirectly;
    }

    public Long getnID_StatusType() {
        return nID_StatusType;
    }

    public void setnID_StatusType(Long nID_StatusType) {
        this.nID_StatusType = HistoryEvent_Service_StatusType.getInstance(nID_StatusType).getnID();
    }

    public String getsID_StatusType() {
        return HistoryEvent_Service_StatusType.getInstance(nID_StatusType).getsID();
    }

    public String getsName_UA_StatusType() {
        return HistoryEvent_Service_StatusType.getInstance(nID_StatusType).getsName_UA();
    }

    public Long getnID_ServiceData() {
        return nID_ServiceData;
    }

    public void setnID_ServiceData(Long nID_ServiceData) {
        this.nID_ServiceData = nID_ServiceData;
    }

    public DateTime getsDateCreate() {
        return sDateCreate;
    }

    public void setsDateCreate(DateTime sDateCreate) {
        this.sDateCreate = sDateCreate;
    }

    public DateTime getsDateClose() {
        return sDateClose;
    }

    public void setsDateClose(DateTime sDateClose) {
        this.sDateClose = sDateClose;
    }

    @Override
    public String toString() {
        return "HistoryEvent_Service [sID=" + sID + ", nID_Task="  + nID_Process
                + ", nID_Subject=" + nID_Subject + ", sUserTaskName="
                + sUserTaskName + ", sDate=" + sDate + ", nID_Service="
                + nID_Service + ", nID_Region=" + nID_Region + ", sID_UA="
                + sID_UA + ", nRate=" + nRate + ", soData=" + soData
                + ", sToken=" + sToken + ", sHead=" + sHead + ", sBody="
                + sBody + ", nTimeMinutes=" + nTimeMinutes + ", sID_Order="
                + sID_Order + ", nID_Server=" + nID_Server
                + ", nID_Proccess_Feedback=" + nID_Proccess_Feedback
                + ", nID_Proccess_Escalation=" + nID_Proccess_Escalation
                + ", sID_Rate_Indirectly=" + sID_Rate_Indirectly
                + ", nID_StatusType=" + nID_StatusType + ", nID_ServiceData="
                + nID_ServiceData + ", sID_StatusType=" + sID_StatusType
                + ", sName_UA_StatusType=" + sName_UA_StatusType
                + ", sDateCreate=" + sDateCreate + ", sDateClose=" + sDateClose + "]";
    }

}
