package org.igov.model.relation;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class Relation_ObjectGroup extends AbstractEntity{
    
    @JsonProperty(value = "oObjectGroup_Child")
    @ManyToOne(targetEntity = ObjectGroup.class)
    @JoinColumn(name="nID_ObjectGroup_Child", nullable = false, updatable = false)
    private ObjectGroup oObjectGroup;
    
    @JsonProperty(value = "nID_Relation")
    @Column(name = "nID_Relation", nullable = false)
    private Long nID_Relation;
    
    @JsonProperty(value = "nID_ObjectGroup_Parent")
    @Column(name = "nID_ObjectGroup_Parent", nullable = true)
    private Long nID_ObjectGroup_Parent;
    
    /*@JsonProperty(value = "nID_ObjectGroup_Child")
    @Column(name = "nID_ObjectGroup_Child", nullable = false)
    private Long nID_ObjectGroup_Child;*/

    public void setoObjectGroup(ObjectGroup oObjectGroup) {
        this.oObjectGroup = oObjectGroup;
    }

    public ObjectGroup getoObjectGroup() {
        return oObjectGroup;
    }
    
    public Long getnID_Relation() {
        return nID_Relation;
    }

    public Long getnID_ObjectGroup_Parent() {
        return nID_ObjectGroup_Parent;
    }

    /*public Long getnID_ObjectGroup_Child() {
        return nID_ObjectGroup_Child;
    }*/

    public void setnID_Relation(Long nID_Relation) {
        this.nID_Relation = nID_Relation;
    }

    public void setnID_ObjectGroup_Parent(Long nID_ObjectGroup_Parent) {
        this.nID_ObjectGroup_Parent = nID_ObjectGroup_Parent;
    }

    /*public void setnID_ObjectGroup_Child(Long nID_ObjectGroup_Child) {
        this.nID_ObjectGroup_Child = nID_ObjectGroup_Child;
    }*/

}
