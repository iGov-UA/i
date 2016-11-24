package org.igov.model.process;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.igov.model.core.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonProperty;

@javax.persistence.Entity
public class ProcessSubjectTree extends AbstractEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 @JsonProperty(value = "processSubjectChild")
	 @ManyToOne(targetEntity = ProcessSubject.class)
	 @JoinColumn(name="nID_ProcessSubject_Child", nullable = false, updatable = false)
    private ProcessSubject processSubjectChild;

	 @JsonProperty(value = "processSubjectParent")
	 @ManyToOne(targetEntity = ProcessSubject.class)
	 @JoinColumn(name="nID_ProcessSubject_Parent", nullable = false, updatable = false)
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
