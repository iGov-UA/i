package org.igov.model.action.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.Entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.igov.model.document.Document;
import javax.persistence.Transient;
import java.text.SimpleDateFormat;
import java.util.Date;

@javax.persistence.Entity
public class HistoryEvent extends Entity {

    @JsonProperty(value = "nID_Subject")
    @Column(name = "nID_Subject", nullable = true)
    private Long subjectKey;

    @JsonProperty(value = "nID_HistoryEventType")
    @Column(name = "nID_HistoryEventType", nullable = true)
    private Long historyEventTypeKey;

    @JsonProperty(value = "sEventName")
    @Column(name = "sEventName_Custom", nullable = true)
    private String eventNameCustom;

    @JsonProperty(value = "sMessage")
    @Column(name = "sMessage", nullable = false)
    private String sMessage;

    @JsonProperty(value = "sDate")
    @Transient
    private String sDate;

    @JsonIgnore
    @Column(name = "sDate", nullable = false)
    private Date date;
    
    @JsonProperty(value = "oHistoryEvent_Service")
    @JoinColumn(name = "nID_HistoryEvent_Service", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private HistoryEvent_Service oHistoryEvent_Service;

    @JsonIgnore
    @Column(name = "nID_HistoryEvent_Service", nullable = true)
    private Long nID_HistoryEvent_Service;

    @JsonProperty(value = "oDocument")
    @JoinColumn(name = "nID_Document", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Document oDocument;

    @JsonIgnore
    @Column(name = "nID_Document", nullable = true)
    private Long nID_Document;

    public void setnID_HistoryEvent_Service(Long nID_HistoryEvent_Service) {
        this.nID_HistoryEvent_Service = nID_HistoryEvent_Service;
    }

    public HistoryEvent_Service getoHistoryEvent_Service() {
        return oHistoryEvent_Service;
    }

    public Document getoDocument() {
        return oDocument;
    }

    public void setnID_Document(Long nID_Document) {
        this.nID_Document = nID_Document;
    }

    public Long getSubjectKey() {
        return (this.subjectKey == null) ? 0 : subjectKey;
    }

    public void setSubjectKey(Long subjectKey) {
        this.subjectKey = subjectKey;
    }

    public Long getHistoryEventTypeKey() {
        return (this.historyEventTypeKey == null) ? 0 : historyEventTypeKey;
    }

    public void setHistoryEventTypeKey(Long historyEventTypeKey) {
        this.historyEventTypeKey = historyEventTypeKey;
    }

    public String getEventNameCustom() {
        return (this.eventNameCustom == null) ? "" : eventNameCustom;
    }

    public void setEventNameCustom(String eventNameCustom) {
        this.eventNameCustom = eventNameCustom;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }

    @Transient
    public String getsDate() {
        return new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS").format(date);
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
