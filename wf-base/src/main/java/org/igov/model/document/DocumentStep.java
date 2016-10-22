package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.igov.model.core.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class DocumentStep extends AbstractEntity {

    @JsonProperty(value = "sKey_Step")
    private String sKey_Step;

    @JsonProperty(value = "nOrder")
    private Long nOrder;

    @JsonProperty(value = "snID_Process_Activiti")
    private String snID_Process_Activiti;

    @OneToMany(mappedBy = "documentStep")
    private List<DocumentStepSubjectRight> rights;

    public List<DocumentStepSubjectRight> getRights() {
        return rights;
    }

    public void setRights(List<DocumentStepSubjectRight> rights) {
        this.rights = rights;
    }

    public String getsKey_Step() {
        return sKey_Step;
    }

    public void setsKey_Step(String sKey_Step) {
        this.sKey_Step = sKey_Step;
    }

    public Long getnOrder() {
        return nOrder;
    }

    public void setnOrder(Long nOrder) {
        this.nOrder = nOrder;
    }

    public String getSnID_Process_Activiti() {
        return snID_Process_Activiti;
    }

    public void setSnID_Process_Activiti(String snID_Process_Activiti) {
        this.snID_Process_Activiti = snID_Process_Activiti;
    }

    @Override
    public String toString() {
        return "DocumentStep{" +
                "id=" + getId() + ", " +
                "sKey_Step='" + sKey_Step + '\'' +
                ", nOrder=" + nOrder +
                ", snID_Process_Activiti='" + snID_Process_Activiti + '\'' +
                '}';
    }
}
