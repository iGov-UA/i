package org.igov.model.relation;

import org.igov.model.core.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Kovilin
 */
@javax.persistence.Entity
public class Relation_ObjectItem extends AbstractEntity{
    
    /*
    @JsonProperty(value = "nID_Relation")
    @Column(name = "nID_Relation", nullable = false)
    private Long nID_Relation;
    
    @JsonProperty(value = "nID_ObjectItem_Child")
    @Column(name = "nID_ObjectItem_Child", nullable = false)
    private Long nID_ObjectItem_Child;
    
    @JsonProperty(value = "nID_ObjectItem_Parent")
    @Column(name = "nID_ObjectItem_Parent", nullable = true)
    private Long nID_ObjectItem_Parent;
    */
    
    @JsonProperty(value = "oRelation")
    @ManyToOne(targetEntity = Relation.class)
    @JoinColumn(name="nID_Relation", nullable = false, updatable = false)
    private Relation oRelation;
    
    @JsonProperty(value = "oObjectItem_Child")
    @ManyToOne(targetEntity = ObjectItem.class)
    @JoinColumn(name="nID_ObjectItem_Child", nullable = false, updatable = false)
    private ObjectItem oObjectItem_Child;
    
    @JsonProperty(value = "oObjectItem_Parent")
    @ManyToOne(targetEntity = ObjectItem.class)
    @JoinColumn(name="nID_ObjectItem_Parent", nullable = true, updatable = false)
    private ObjectItem oObjectItem_Parent;

    public void setoRelation(Relation oRelation) {
        this.oRelation = oRelation;
    }

    public void setoObjectItem_Child(ObjectItem oObjectItem_Child) {
        this.oObjectItem_Child = oObjectItem_Child;
    }

    public void setoObjectItem_Parent(ObjectItem oObjectItem_Parent) {
        this.oObjectItem_Parent = oObjectItem_Parent;
    }
    
    public Relation getoRelation() {
        return oRelation;
    }

    public ObjectItem getoObjectItem_Child() {
        return oObjectItem_Child;
    }

    public ObjectItem getoObjectItem_Parent() {
        return oObjectItem_Parent;
    }
    
    /*public Long getnID_Relation() {
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
    }*/

}
