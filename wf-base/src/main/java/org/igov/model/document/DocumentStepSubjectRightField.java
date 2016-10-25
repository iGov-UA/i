package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class DocumentStepSubjectRightField extends AbstractEntity {

    @ManyToOne(targetEntity = DocumentStepSubjectRight.class)
    @JsonProperty(value = "nID_DocumentStepSubjectRight")
    private DocumentStepSubjectRight documentStepSubjectRight;

    @JsonProperty(value = "sMask_FieldID")
    private String sMask_FieldID;

    @JsonProperty(value = "bWrite")
    private Boolean bWrite;

    public DocumentStepSubjectRight getDocumentStepSubjectRight() {
        return documentStepSubjectRight;
    }

    public void setDocumentStepSubjectRight(DocumentStepSubjectRight documentStepSubjectRight) {
        this.documentStepSubjectRight = documentStepSubjectRight;
    }

    public String getsMask_FieldID() {
        return sMask_FieldID;
    }

    public void setsMask_FieldID(String sMask_FieldID) {
        this.sMask_FieldID = sMask_FieldID;
    }

    public Boolean getbWrite() {
        return bWrite;
    }

    public void setbWrite(Boolean bWrite) {
        this.bWrite = bWrite;
    }

    @Override
    public String toString() {
        return "DocumentStepSubjectRightField{" +
                "id=" + getId() + ", " +
                "documentStepSubjectRight=" + documentStepSubjectRight +
                ", sMask_FieldID='" + sMask_FieldID + '\'' +
                ", bWrite=" + bWrite +
                '}';
    }
}
