package org.igov.model.subject;

import javax.persistence.Column;

import org.igov.model.core.AbstractEntity;
import org.igov.service.business.subject.SubjectRightBPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@javax.persistence.Entity
public class SubjectRightBP extends AbstractEntity {
	private static final Logger LOG = LoggerFactory.getLogger(SubjectRightBPService.class);

    @JsonProperty(value = "sID_BP")
    @Column(name = "sID_BP", nullable = false)
    private String sID_BP;

    @JsonProperty(value = "sID_Place_UA")
    @Column(name = "sID_Place_UA", nullable = true)
    private String sID_Place_UA;

    @JsonProperty(value = "sID_Group_Referent")
    @Column(name = "sID_Group_Referent", nullable = false)
    private String sID_Group_Referent;

    @JsonProperty(value = "asID_Group_Export")
    @Column(name = "asID_Group_Export", nullable = true)
    private String asID_Group_Export;

    @JsonProperty(value = "sFormulaFilter_Export")
    @Column(name = "sFormulaFilter_Export", nullable = true)
    private String sFormulaFilter_Export;

    @JsonProperty(value = "sNote")
    @Column(name = "sNote", nullable = true)
    private String sNote;

    public SubjectRightBP() {

    }

    public String getsID_BP() {
        return sID_BP;
    }

    public void setsID_BP(String sID_BP) {
        this.sID_BP = sID_BP;
    }

    public String getsID_Place_UA() {
        return sID_Place_UA;
    }

    public void setsID_Place_UA(String sID_Place_UA) {
        this.sID_Place_UA = sID_Place_UA;
    }

    public String getsID_Group_Referent() {
        return sID_Group_Referent;
    }

    public void setsID_Group_Referent(String sID_Group) {
        this.sID_Group_Referent = sID_Group;
    }

    public String getAsID_Group_Export() {
        return asID_Group_Export;
    }

    public String getsFormulaFilter_Export() {
        return sFormulaFilter_Export;
    }

    public String getsNote() {
        return sNote;
    }

    public void setAsID_Group_Export(String asID_Group_Export) {
        this.asID_Group_Export = asID_Group_Export;
    }

    public void setsFormulaFilter_Export(String sFormulaFilter_Export) {
        this.sFormulaFilter_Export = sFormulaFilter_Export;
    }

    public void setsNote(String sNote) {
        this.sNote = sNote;
    }

    @Override
    public String toString() {
     try {
      return new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, true)
        .writerWithDefaultPrettyPrinter().writeValueAsString(this);
     } catch (JsonProcessingException e) {
      LOG.info(String.format("error [%s]", e.getMessage()));
     }
     return null;
    }
    
    

}
