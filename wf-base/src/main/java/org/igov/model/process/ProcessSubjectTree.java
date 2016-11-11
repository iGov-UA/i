package org.igov.model.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.igov.model.core.AbstractEntity;

@Entity
public class ProcessSubjectTree extends AbstractEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_ProcessSubject_Child")
    private ProcessSubject processSubjectChild;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "nID_ProcessSubject_Parent")
    private ProcessSubject processSubjectParent;

    public ProcessSubject getProcessSubjectChild() {
        return processSubjectChild;
    }

    public void setProcessSubjectChild(ProcessSubject processSubjectChild) {
        this.processSubjectChild = processSubjectChild;
    }

    public ProcessSubject getProcessSubjectParent() {
        return processSubjectParent;
    }

    public void setProcessSubjectParent(ProcessSubject processSubjectParent) {
        this.processSubjectParent = processSubjectParent;
    }

    @Override
    public String toString() {
        return "ProcessSubjectTree{" + 
                "id = " + getId() +
                ", processSubjectChild=" + processSubjectChild + 
                ", processSubjectParent=" + processSubjectParent + 
                '}';
    }
    
    

}
