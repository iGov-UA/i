/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.subject;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.igov.model.core.NamedEntity;

/**
 *
 * @author HS
 */
@javax.persistence.Entity
public class SubjectHumanRole extends NamedEntity {

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = SubjectHuman.class)
    @JoinTable(name = "SubjectHumanRole_SubjectHuman",
            joinColumns = @JoinColumn(name = "nID_SubjectHumanRole"),
            inverseJoinColumns = @JoinColumn(name = "nID_SubjectHuman"))
    @JsonManagedReference
    private List<SubjectHuman> aSubjectHuman = new ArrayList<>();

    public List<SubjectHuman> getaSubjectHuman() {
        return aSubjectHuman;
    }

    public void setaSubjectHuman(List<SubjectHuman> aSubjectHuman) {
        this.aSubjectHuman = aSubjectHuman;
    }

}
