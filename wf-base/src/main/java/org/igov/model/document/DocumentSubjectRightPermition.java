package org.igov.model.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
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
    
    @JsonProperty(value = "sKeyGroup_Postfix")
    private String sKeyGroup_Postfix;

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
