package org.igov.model.escalation;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import org.igov.model.core.Entity;

/**
 * @author NickVeremeichyk
 * @since 2015-12-05.
 */
@javax.persistence.Entity
public class EscalationStatus extends Entity {
    /**
     * sID - строка-ИД (уникальный)
     */
    @Column(name="sID")
    @JsonProperty(value="sID")
    private String nId;

    /**
     * sNote - строка-описание
     */
    @Column(name = "sNote")
    @JsonProperty(value="sNote")
    private String sNote;

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public String getsNote() {
        return sNote;
    }

    public void setsNote(String sNote) {
        this.sNote = sNote;
    }
}
