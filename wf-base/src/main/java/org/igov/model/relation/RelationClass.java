package org.igov.model.relation;

import org.igov.model.core.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;


/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class RelationClass extends AbstractEntity{
    
    @JsonProperty(value = "sClass")
    @Column(name = "sClass", length = 255, nullable = false)
    private String sClass;

    public String getsClass() {
        return sClass;
    }

    public void setsClass(String sClass) {
        this.sClass = sClass;
    }

}
