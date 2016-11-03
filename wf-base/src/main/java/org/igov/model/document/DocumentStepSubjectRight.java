package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;
import org.igov.model.core.AbstractEntity;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

@Entity
public class DocumentStepSubjectRight extends AbstractEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_DocumentStep")
    private DocumentStep documentStep;

    @JsonProperty(value = "sKey_GroupPostfix")
    private String sKey_GroupPostfix;

    @JsonProperty(value = "sName")
    private String sName;

    @JsonProperty(value = "bWrite")
    private Boolean bWrite;

    @JsonProperty(value = "sLogin")
    private String sLogin;

    @JsonProperty(value = "sDate")
    @Type(type = DATETIME_TYPE)
    private DateTime sDate;

    @OneToMany(mappedBy = "documentStepSubjectRight", cascade = CascadeType.ALL)
    private List<DocumentStepSubjectRightField> documentStepSubjectRightFields;

    public List<DocumentStepSubjectRightField> getDocumentStepSubjectRightFields() {
        return documentStepSubjectRightFields;
    }

    public void setDocumentStepSubjectRightFields(List<DocumentStepSubjectRightField> documentStepSubjectRightFields) {
        this.documentStepSubjectRightFields = documentStepSubjectRightFields;
    }

    public DocumentStep getDocumentStep() {
        return documentStep;
    }

    public void setDocumentStep(DocumentStep documentStep) {
        this.documentStep = documentStep;
    }

    public String getsKey_GroupPostfix() {
        return sKey_GroupPostfix;
    }

    public void setsKey_GroupPostfix(String sKey_GroupPostfix) {
        this.sKey_GroupPostfix = sKey_GroupPostfix;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public Boolean getbWrite() {
        return bWrite;
    }

    public void setbWrite(Boolean bWrite) {
        this.bWrite = bWrite;
    }

    public String getsLogin() {
        return sLogin;
    }

    public void setsLogin(String sLogin) {
        this.sLogin = sLogin;
    }

    public DateTime getsDate() {
        return sDate;
    }

    public void setsDate(DateTime sDate) {
        this.sDate = sDate;
    }

    @Override
    public String toString() {
        return "DocumentStepSubjectRight{" +
                "id=" + getId() + ", " +
                "documentStep=" + documentStep +
                ", sKey_GroupPostfix='" + sKey_GroupPostfix + '\'' +
                ", sName='" + sName + '\'' +
                ", bWrite=" + bWrite +
                ", sLogin='" + sLogin + '\'' +
                ", sDate=" + sDate +
                '}';
    }
}
