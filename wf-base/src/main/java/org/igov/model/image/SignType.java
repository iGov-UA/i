package org.igov.model.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author Kovylin
 */
@javax.persistence.Entity
public class SignType extends AbstractEntity{
    
    @JsonProperty(value = "sID")
    @Column
    private String sID;
    
    @JsonProperty(value = "sName")
    @Column
    private String sName;
    
    @JsonProperty(value = "sNote")
    @Column
    private String sNote;
    
    @JsonProperty(value = "sClass")
    @Column
    private String sClass;

    public String getsID() {
        return sID;
    }

    public String getsName() {
        return sName;
    }

    public String getsNote() {
        return sNote;
    }

    public String getsClass() {
        return sClass;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setsNote(String sNote) {
        this.sNote = sNote;
    }

    public void setsClass(String sClass) {
        this.sClass = sClass;
    }
    
}
