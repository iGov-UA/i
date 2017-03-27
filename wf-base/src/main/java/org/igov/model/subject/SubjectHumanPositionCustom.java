/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import org.igov.model.core.NamedEntity;

/**
 *
 * @author olga
 */
@Entity
public class SubjectHumanPositionCustom extends NamedEntity{
    
    @JsonProperty(value = "sNote")
    private String sNote;

    public String getsNote() {
        return sNote;
    }

    public void setsNote(String sNote) {
        this.sNote = sNote;
    }
}
