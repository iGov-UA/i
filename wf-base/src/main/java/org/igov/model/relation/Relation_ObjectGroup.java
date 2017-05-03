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
    
    @JsonProperty(value = "oRelation")
    @ManyToOne(targetEntity = Relation.class)
    @JoinColumn(name="nID_Relation", nullable = false, updatable = false)
    private Relation oRelation;
    
    @JsonProperty(value = "oObjectGroup_Child")
    @ManyToOne(targetEntity = ObjectGroup.class)
    @JoinColumn(name="nID_ObjectGroup_Child", nullable = true, updatable = false)
    private ObjectGroup oObjectGroup_Child;
    
    @JsonProperty(value = "oObjectGroup_Parent")
    @ManyToOne(targetEntity = ObjectGroup.class)
    @JoinColumn(name="nID_ObjectGroup_Parent", nullable = true, updatable = false)
    private ObjectGroup oObjectGroup_Parent;
    
    
    /*@JsonProperty(value = "nID_Relation")
    @Column(name = "nID_Relation", nullable = false)
    private Long nID_Relation;
    
    @JsonProperty(value = "nID_ObjectGroup_Parent")
    @Column(name = "nID_ObjectGroup_Parent", nullable = true)
    private Long nID_ObjectGroup_Parent;
    
    @JsonProperty(value = "nID_ObjectGroup_Child")
    @Column(name = "nID_ObjectGroup_Child", nullable = false)
    private Long nID_ObjectGroup_Child;*/

    public ObjectGroup getoObjectGroup_Child() {
        return oObjectGroup_Child;
    }

    public Relation getoRelation() {
        return oRelation;
    }

    public ObjectGroup getoObjectGroup_Parent() {
        return oObjectGroup_Parent;
    }

    public void setoObjectGroup_Child(ObjectGroup oObjectGroup_Child) {
        this.oObjectGroup_Child = oObjectGroup_Child;
    }

    public void setoRelation(Relation oRelation) {
        this.oRelation = oRelation;
    }

    public void setoObjectGroup_Parent(ObjectGroup oObjectGroup_Parent) {
        this.oObjectGroup_Parent = oObjectGroup_Parent;
    }

    public void setoObjectGroup(ObjectGroup oObjectGroup) {
        this.oObjectGroup_Child = oObjectGroup;
    }

    public ObjectGroup getoObjectGroup() {
        return oObjectGroup_Child;
    }
    
    /*public Long getnID_Relation() {
        return nID_Relation;
    }

    public Long getnID_ObjectGroup_Parent() {
        return nID_ObjectGroup_Parent;
    }

    public Long getnID_ObjectGroup_Child() {
        return nID_ObjectGroup_Child;
    }

    public void setnID_Relation(Long nID_Relation) {
        this.nID_Relation = nID_Relation;
    }

    public void setnID_ObjectGroup_Parent(Long nID_ObjectGroup_Parent) {
        this.nID_ObjectGroup_Parent = nID_ObjectGroup_Parent;
    }

    public void setnID_ObjectGroup_Child(Long nID_ObjectGroup_Child) {
        this.nID_ObjectGroup_Child = nID_ObjectGroup_Child;
    }*/

}
