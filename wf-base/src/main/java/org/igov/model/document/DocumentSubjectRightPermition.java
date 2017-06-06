package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.igov.model.core.AbstractEntity;

/**
 *
 * @author Kovilin
 */
@Entity
public class DocumentSubjectRightPermition extends AbstractEntity{
    
    @JsonProperty(value = "nID_DocumentStepSubjectRight")
    @Column
    private Long nID_DocumentStepSubjectRight;
    
    @JsonProperty(value = "PermitionType")
    @Column
    private String PermitionType;
    
    @JsonProperty(value = "sKeyGroupeSource")
    @Column
    private String sKeyGroupeSource;
    
    @Transient
    private String sKeyGroup_Postfix;
    
    @Transient
    private String sKey_Step;

    public String getsKey_Step() {
        return sKey_Step;
    }

    public void setsKey_Step(String sKey_Step) {
        this.sKey_Step = sKey_Step;
    }

    public void setsKeyGroup_Postfix(String sKeyGroup_Postfix) {
        this.sKeyGroup_Postfix = sKeyGroup_Postfix;
    }

    public String getsKeyGroup_Postfix() {
        return sKeyGroup_Postfix;
    }
            
    public Long getnID_DocumentStepSubjectRight() {
        return nID_DocumentStepSubjectRight;
    }

    public String getPermitionType() {
        return PermitionType;
    }

    public String getsKeyGroupeSource() {
        return sKeyGroupeSource;
    }

    public void setnID_DocumentStepSubjectRight(Long nID_DocumentStepSubjectRight) {
        this.nID_DocumentStepSubjectRight = nID_DocumentStepSubjectRight;
    }

    public void setPermitionType(String PermitionType) {
        this.PermitionType = PermitionType;
    }

    public void setsKeyGroupeSource(String sKeyGroupeSource) {
        this.sKeyGroupeSource = sKeyGroupeSource;
    }
    
}
