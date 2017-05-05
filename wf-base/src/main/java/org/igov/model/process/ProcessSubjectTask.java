package org.igov.model.process;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class ProcessSubjectTask extends AbstractEntity{
    
    @JsonProperty(value = "sHead")
    @Column
    private String sHead;
    
    @JsonProperty(value = "sBody")
    @Column
    private String sBody;
    
    @JsonProperty(value = "snID_Process_Activiti_Root")
    @Column
    private String snID_Process_Activiti_Root;
    
    @OneToMany(mappedBy = "oProcessSubjectTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE) 
    private List<ProcessSubject> aProcessSubject;

    public String getsHead() {
        return sHead;
    }

    public String getsBody() {
        return sBody;
    }

    public String getSnID_Process_Activiti_Root() {
        return snID_Process_Activiti_Root;
    }

    public List<ProcessSubject> getaProcessSubject() {
        return aProcessSubject;
    }

    public void setsHead(String sHead) {
        this.sHead = sHead;
    }

    public void setsBody(String sBody) {
        this.sBody = sBody;
    }

    public void setSnID_Process_Activiti_Root(String snID_Process_Activiti_Root) {
        this.snID_Process_Activiti_Root = snID_Process_Activiti_Root;
    }

    public void setaProcessSubject(List<ProcessSubject> aProcessSubject) {
        this.aProcessSubject = aProcessSubject;
    }

    @Override
    public String toString() {
        return "ProcessSubjectTask{"
                + "id=" + getId()
                + "sHead=" + sHead + ", sBody=" + sBody
                + ", snID_Process_Activiti_Root=" + snID_Process_Activiti_Root
                + ", aProcessSubject=" + aProcessSubject + '}';
    }
   
}
