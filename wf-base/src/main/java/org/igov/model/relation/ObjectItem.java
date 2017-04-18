package org.igov.model.relation;

import org.igov.model.core.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;

/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class ObjectItem extends AbstractEntity{
    
    @JsonProperty(value = "nID_Subject_Source")
    @Column(name = "nID_Subject_Source", nullable = true)
    private Long nID_Subject_Source;
    
    @JsonProperty(value = "sID_Private_Source")
    @Column(name = "sID_Private_Source", length = 5000, nullable = true)
    private String sID_Private_Source;

    public Long getnID_Subject_Source() {
        return nID_Subject_Source;
    }

    public String getsID_Private_Source() {
        return sID_Private_Source;
    }

    public void setnID_Subject_Source(Long nID_Subject_Source) {
        this.nID_Subject_Source = nID_Subject_Source;
    }

    public void setsID_Private_Source(String sID_Private_Source) {
        this.sID_Private_Source = sID_Private_Source;
    }
    
}
