package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.NamedEntity;

import javax.persistence.Column;

@javax.persistence.Entity
public class DocumentType extends NamedEntity {

    @JsonProperty("bHidden")
    @Column
    private boolean bHidden;

    public boolean isbHidden() {
        return bHidden;
    }

    public void setbHidden(boolean bHidden) {
        this.bHidden = bHidden;
    }
}
