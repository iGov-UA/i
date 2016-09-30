//package org.igov.model.subject.message;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import org.igov.model.core.AbstractEntity;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "SubjectMessageFeedbackComment")
//public class SubjectMessageFeedbackComment extends AbstractEntity {
//
//    @JsonProperty(value = "sFio")
//    @Column(name = "sFio")
//    private String sFio;
//
//    @JsonProperty(value = "bSelf")
//    @Column(name = "bSelf")
//    private Boolean bSelf;
//
//    @JsonProperty("sDate")
//    @Column(name = "sDate")
//    private String sDate;
//
//    @JsonProperty(value = "sText")
//    @Column(name = "sText")
//    private String sText;
//
//    @ManyToOne
//    private SubjectMessageFeedback commentedFeedback;
//
//    public String getsFio() {
//        return sFio;
//    }
//
//    public void setsFio(String sFio) {
//        this.sFio = sFio;
//    }
//
//    public Boolean getbSelf() {
//        return bSelf;
//    }
//
//    public void setbSelf(Boolean bSelf) {
//        this.bSelf = bSelf;
//    }
//
//    public String getsDate() {
//        return sDate;
//    }
//
//    public void setsDate(String sDate) {
//        this.sDate = sDate;
//    }
//
//    public String getsText() {
//        return sText;
//    }
//
//    public void setsText(String sText) {
//        this.sText = sText;
//    }
//}