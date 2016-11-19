package org.igov.model.process;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@javax.persistence.Entity
public class ProcessSubjectTree extends AbstractEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
