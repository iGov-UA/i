package org.igov.model.relation;

import org.igov.model.core.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;

/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class Relation_ObjectItem extends AbstractEntity{
    
    @JsonProperty(value = "nID_Relation")
    @Column(name = "nID_Relation", nullable = false)
    private Long nID_Relation;
    
    @JsonProperty(value = "nID_ObjectItem_Parent")
    @Column(name = "nID_ObjectItem_Parent", nullable = true)
    private Long nID_ObjectItem_Parent;
    
    @JsonProperty(value = "nID_ObjectItem_Child")
    @Column(name = "nID_ObjectItem_Child", nullable = false)
    private Long nID_ObjectItem_Child;

    public Long getnID_Relation() {
        return nID_Relation;
    }

    public Long getnID_ObjectItem_Parent() {
        return nID_ObjectItem_Parent;
    }

    public Long getnID_ObjectItem_Child() {
        return nID_ObjectItem_Child;
    }

    public void setnID_Relation(Long nID_Relation) {
        this.nID_Relation = nID_Relation;
    }

    public void setnID_ObjectItem_Parent(Long nID_ObjectItem_Parent) {
        this.nID_ObjectItem_Parent = nID_ObjectItem_Parent;
    }

    public void setnID_ObjectItem_Child(Long nID_ObjectItem_Child) {
        this.nID_ObjectItem_Child = nID_ObjectItem_Child;
    }
    
}
