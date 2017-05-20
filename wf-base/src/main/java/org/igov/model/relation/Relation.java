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
public class Relation extends AbstractEntity{
    
    @JsonProperty(value = "sID")
    @Column(name = "sID", length = 255, nullable = false)
    private String sID;
    
    @JsonProperty(value = "sName")
    @Column(name = "sName", length = 255, nullable = false)
    private String sName;
    
    /*@JsonProperty(value = "nID_RelationClass")
    @Column(name = "nID_RelationClass", nullable = false)
    private Long nID_RelationClass;*/
    
    @JsonProperty(value = "oRelationClass")
    @ManyToOne(targetEntity = RelationClass.class)
    @JoinColumn(name="nID_RelationClass", nullable = false, updatable = false)
    private RelationClass oRelationClass;

    public String getsID() {
        return sID;
    }

    public String getsName() {
        return sName;
    }

    /*public Long getnID_RelationClass() {
        return nID_RelationClass;
    }

     public void setnID_RelationClass(Long nID_RelationClass) {
        this.nID_RelationClass = nID_RelationClass;
    }*/
     
    public void setsID(String sID) {
        this.sID = sID;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public RelationClass getoRelationClass() {
        return oRelationClass;
    }

    public void setoRelationClass(RelationClass oRelationClass) {
        this.oRelationClass = oRelationClass;
    }
    
}
